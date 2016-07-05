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
package com.ict.dtube.broker.offset;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ict.dtube.broker.BrokerController;
import com.ict.dtube.broker.BrokerPathConfigHelper;
import com.ict.dtube.common.ConfigManager;
import com.ict.dtube.common.constant.LoggerName;
import com.ict.dtube.remoting.protocol.RemotingSerializable;


/**
 * Consumer消费进度管理
 * 
 * @author shijia.wxr<vintage.wang@gmail.com>
 * @since 2013-8-11
 */
public class ConsumerOffsetManager extends ConfigManager {
    private static final Logger log = LoggerFactory.getLogger(LoggerName.BrokerLoggerName);
    private static final String TOPIC_GROUP_SEPARATOR = "@";

    private ConcurrentHashMap<String/* topic@group */, ConcurrentHashMap<Integer, Long>> offsetTable =
            new ConcurrentHashMap<String, ConcurrentHashMap<Integer, Long>>(512);

    private transient BrokerController brokerController;


    public ConsumerOffsetManager() {
    }


    public ConsumerOffsetManager(BrokerController brokerController) {
        this.brokerController = brokerController;
    }


    /**
     * 扫描数据被删除了的topic，offset记录也对应删除
     */
    public void scanUnsubscribedTopic() {
        Iterator<Entry<String, ConcurrentHashMap<Integer, Long>>> it = this.offsetTable.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, ConcurrentHashMap<Integer, Long>> next = it.next();
            String topicAtGroup = next.getKey();
            String[] arrays = topicAtGroup.split(TOPIC_GROUP_SEPARATOR);
            if (arrays != null && arrays.length == 2) {
                String topic = arrays[0];
                String group = arrays[1];
                // 当前订阅关系里面没有group-topic订阅关系（消费端当前是停机的状态）并且offset落后很多,则删除消费进度
                if (null == brokerController.getConsumerManager().findSubscriptionData(group, topic)
                        && this.offsetBehindMuchThanData(topic, next.getValue())) {
                    it.remove();
                    log.warn("remove topic offset, {}", topicAtGroup);
                }
            }
        }
    }


    private boolean offsetBehindMuchThanData(final String topic, ConcurrentHashMap<Integer, Long> table) {
        Iterator<Entry<Integer, Long>> it = table.entrySet().iterator();
        boolean result = !table.isEmpty();

        while (it.hasNext() && result) {
            Entry<Integer, Long> next = it.next();
            long minOffsetInStore =
                    this.brokerController.getMessageStore().getMinOffsetInQuque(topic, next.getKey());
            long offsetInPersist = next.getValue();
            if (offsetInPersist > minOffsetInStore) {
                result = false;
            }
        }

        return result;
    }


    public Set<String> whichTopicByConsumer(final String group) {
        Set<String> topics = new HashSet<String>();

        Iterator<Entry<String, ConcurrentHashMap<Integer, Long>>> it = this.offsetTable.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, ConcurrentHashMap<Integer, Long>> next = it.next();
            String topicAtGroup = next.getKey();
            String[] arrays = topicAtGroup.split(TOPIC_GROUP_SEPARATOR);
            if (arrays != null && arrays.length == 2) {
                if (group.equals(arrays[1])) {
                    topics.add(arrays[0]);
                }
            }
        }

        return topics;
    }


    public void commitOffset(final String group, final String topic, final int queueId, final long offset) {
        // topic@group
        String key = topic + TOPIC_GROUP_SEPARATOR + group;
        this.commitOffset(key, queueId, offset);
    }


    public long queryOffset(final String group, final String topic, final int queueId) {
        // topic@group
        String key = topic + TOPIC_GROUP_SEPARATOR + group;
        ConcurrentHashMap<Integer, Long> map = this.offsetTable.get(key);
        if (null != map) {
            Long offset = map.get(queueId);
            if (offset != null)
                return offset;
        }

        return -1;
    }


    private void commitOffset(final String key, final int queueId, final long offset) {
        ConcurrentHashMap<Integer, Long> map = this.offsetTable.get(key);
        if (null == map) {
            map = new ConcurrentHashMap<Integer, Long>(32);
            map.put(queueId, offset);
            this.offsetTable.put(key, map);
        }
        else {
            map.put(queueId, offset);
        }
    }


    public String encode() {
        return this.encode(false);
    }


    public String encode(final boolean prettyFormat) {
        return RemotingSerializable.toJson(this, prettyFormat);
    }


    @Override
    public void decode(String jsonString) {
        if (jsonString != null) {
            ConsumerOffsetManager obj =
                    RemotingSerializable.fromJson(jsonString, ConsumerOffsetManager.class);
            if (obj != null) {
                this.offsetTable = obj.offsetTable;
            }
        }
    }


    @Override
    public String configFilePath() {
        return BrokerPathConfigHelper.getConsumerOffsetPath(this.brokerController.getMessageStoreConfig()
            .getStorePathRootDir());
    }


    public ConcurrentHashMap<String, ConcurrentHashMap<Integer, Long>> getOffsetTable() {
        return offsetTable;
    }


    public void setOffsetTable(ConcurrentHashMap<String, ConcurrentHashMap<Integer, Long>> offsetTable) {
        this.offsetTable = offsetTable;
    }
}
