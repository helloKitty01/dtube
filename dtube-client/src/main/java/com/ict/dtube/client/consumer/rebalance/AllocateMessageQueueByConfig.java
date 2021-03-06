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
package com.ict.dtube.client.consumer.rebalance;

import java.util.List;

import com.ict.dtube.client.consumer.AllocateMessageQueueStrategy;
import com.ict.dtube.common.message.MessageQueue;


/**
 * 按照配置来分配队列，建议应用使用Spring来初始化
 * 
 * @author shijia.wxr<vintage.wang@gmail.com>
 * @since 2013-7-24
 */
public class AllocateMessageQueueByConfig implements AllocateMessageQueueStrategy {
    private List<MessageQueue> messageQueueList;


    @Override
    public List<MessageQueue> allocate(String consumerGroup, String currentCID, List<MessageQueue> mqAll,
            List<String> cidAll) {
        return this.messageQueueList;
    }


    public List<MessageQueue> getMessageQueueList() {
        return messageQueueList;
    }


    public void setMessageQueueList(List<MessageQueue> messageQueueList) {
        this.messageQueueList = messageQueueList;
    }
}
