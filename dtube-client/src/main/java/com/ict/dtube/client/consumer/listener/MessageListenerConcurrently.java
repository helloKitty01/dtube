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
package com.ict.dtube.client.consumer.listener;

import java.util.List;

import com.ict.dtube.common.message.MessageExt;


/**
 * 同一队列的消息并行消费
 * 
 * @author shijia.wxr<vintage.wang@gmail.com>
 * @since 2013-7-24
 */
public interface MessageListenerConcurrently extends MessageListener {
    /**
     * 方法抛出异常等同于返回 ConsumeConcurrentlyStatus.RECONSUME_LATER<br>
     * P.S: 建议应用不要抛出异常
     * 
     * @param msgs
     *            msgs.size() >= 1<br>
     *            DefaultMQPushConsumer.consumeMessageBatchMaxSize=1，默认消息数为1
     * @param context
     * @return
     */
    public ConsumeConcurrentlyStatus consumeMessage(final List<MessageExt> msgs,
            final ConsumeConcurrentlyContext context);
}
