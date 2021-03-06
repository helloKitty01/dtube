/**
 * Copyright (C) 2010-2013 Alibaba Group Holding Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ict.dtube.client.consumer;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;

import com.ict.dtube.client.exception.MQClientException;
import com.ict.dtube.client.log.ClientLogger;
import com.ict.dtube.common.message.MessageQueue;
import com.ict.dtube.common.protocol.heartbeat.MessageModel;


/**
 * PullConsumer的调度服务，降低Pull方式的编程复杂度
 * 
 * @author shijia.wxr<vintage.wang@gmail.com>
 * @since 2014-2-26
 */
public class MQPullConsumerScheduleService {
    private final Logger log = ClientLogger.getLog();
    private DefaultMQPullConsumer defaultMQPullConsumer;
    private int pullThreadNums = 20;
    private ConcurrentHashMap<String /* topic */, PullTaskCallback> callbackTable =
            new ConcurrentHashMap<String, PullTaskCallback>();

    /**
     * 具体实现，使用者不用关心
     */
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private final MessageQueueListener messageQueueListener = new MessageQueueListenerImpl();

    // 正在拉取的任务
    private final ConcurrentHashMap<MessageQueue, PullTaskImpl> taskTable =
            new ConcurrentHashMap<MessageQueue, PullTaskImpl>();

    class MessageQueueListenerImpl implements MessageQueueListener {
        @Override
        public void messageQueueChanged(String topic, Set<MessageQueue> mqAll, Set<MessageQueue> mqDivided) {
            MessageModel messageModel =
                    MQPullConsumerScheduleService.this.defaultMQPullConsumer.getMessageModel();
            switch (messageModel) {
            case BROADCASTING:
                MQPullConsumerScheduleService.this.putTask(topic, mqAll);
                break;
            case CLUSTERING:
                MQPullConsumerScheduleService.this.putTask(topic, mqDivided);
                break;
            default:
                break;
            }
        }
    }

    class PullTaskImpl implements Runnable {
        private final MessageQueue messageQueue;
        private volatile boolean cancelled = false;


        public PullTaskImpl(final MessageQueue messageQueue) {
            this.messageQueue = messageQueue;
        }


        @Override
        public void run() {
            String topic = this.messageQueue.getTopic();
            if (!this.isCancelled()) {
                PullTaskCallback pullTaskCallback =
                        MQPullConsumerScheduleService.this.callbackTable.get(topic);
                if (pullTaskCallback != null) {
                    final PullTaskContext context = new PullTaskContext();
                    try {
                        pullTaskCallback.doPullTask(this.messageQueue, context);
                    }
                    catch (Throwable e) {
                        context.setPullNextDelayTimeMillis(1000);
                        log.error("doPullTask Exception", e);
                    }

                    if (!this.isCancelled()) {
                        MQPullConsumerScheduleService.this.scheduledThreadPoolExecutor.schedule(this,
                            context.getPullNextDelayTimeMillis(), TimeUnit.MILLISECONDS);
                    }
                    else {
                        log.warn("The Pull Task is cancelled after doPullTask, {}", messageQueue);
                    }
                }
                else {
                    log.warn("Pull Task Callback not exist , {}", topic);
                }
            }
            else {
                log.warn("The Pull Task is cancelled, {}", messageQueue);
            }
        }


        public boolean isCancelled() {
            return cancelled;
        }


        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }


        public MessageQueue getMessageQueue() {
            return messageQueue;
        }
    }


    public MQPullConsumerScheduleService(final String consumerGroup) {
        this.defaultMQPullConsumer = new DefaultMQPullConsumer(consumerGroup);
        this.defaultMQPullConsumer.setMessageModel(MessageModel.CLUSTERING);
    }


    public void putTask(String topic, Set<MessageQueue> mqNewSet) {
        // 删除多余的队列
        Iterator<Entry<MessageQueue, PullTaskImpl>> it = this.taskTable.entrySet().iterator();
        while (it.hasNext()) {
            Entry<MessageQueue, PullTaskImpl> next = it.next();
            if (next.getKey().getTopic().equals(topic)) {
                if (!mqNewSet.contains(next.getKey())) {
                    next.getValue().setCancelled(true);
                    it.remove();
                }
            }
        }

        // 增加新的队列
        for (MessageQueue mq : mqNewSet) {
            if (!this.taskTable.containsKey(mq)) {
                PullTaskImpl command = new PullTaskImpl(mq);
                this.taskTable.put(mq, command);
                this.scheduledThreadPoolExecutor.schedule(command, 0, TimeUnit.MILLISECONDS);
            }
        }
    }


    /**
     * 启动服务
     * 
     * @throws MQClientException
     */
    public void start() throws MQClientException {
        final String group = this.defaultMQPullConsumer.getConsumerGroup();
        this.scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(//
            this.pullThreadNums,//
            new ThreadFactory() {
                private AtomicLong threadIndex = new AtomicLong(0);


                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "PullMsgThread-" //
                            + group//
                            + "-" + this.threadIndex.incrementAndGet());
                }
            });

        this.defaultMQPullConsumer.setMessageQueueListener(this.messageQueueListener);

        this.defaultMQPullConsumer.start();

        log.info("MQPullConsumerScheduleService start OK, {} {}",
            this.defaultMQPullConsumer.getConsumerGroup(), this.callbackTable);
    }


    public void registerPullTaskCallback(final String topic, final PullTaskCallback callback) {
        this.callbackTable.put(topic, callback);
        this.defaultMQPullConsumer.registerMessageQueueListener(topic, null);
    }


    /**
     * 关闭服务
     */
    public void shutdown() {
        if (this.scheduledThreadPoolExecutor != null) {
            this.scheduledThreadPoolExecutor.shutdown();
        }

        if (this.defaultMQPullConsumer != null) {
            this.defaultMQPullConsumer.shutdown();
        }
    }


    public ConcurrentHashMap<String, PullTaskCallback> getCallbackTable() {
        return callbackTable;
    }


    public void setCallbackTable(ConcurrentHashMap<String, PullTaskCallback> callbackTable) {
        this.callbackTable = callbackTable;
    }


    public int getPullThreadNums() {
        return pullThreadNums;
    }


    public void setPullThreadNums(int pullThreadNums) {
        this.pullThreadNums = pullThreadNums;
    }


    public DefaultMQPullConsumer getDefaultMQPullConsumer() {
        return defaultMQPullConsumer;
    }


    public void setDefaultMQPullConsumer(DefaultMQPullConsumer defaultMQPullConsumer) {
        this.defaultMQPullConsumer = defaultMQPullConsumer;
    }


    public MessageModel getMessageModel() {
        return this.defaultMQPullConsumer.getMessageModel();
    }


    public void setMessageModel(MessageModel messageModel) {
        this.defaultMQPullConsumer.setMessageModel(messageModel);
    }
}
