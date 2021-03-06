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
package com.ict.dtube.client.producer.selector;

import java.util.List;
import java.util.Set;

import com.ict.dtube.client.producer.MessageQueueSelector;
import com.ict.dtube.common.message.Message;
import com.ict.dtube.common.message.MessageQueue;


/**
 * 根据机房来选择发往哪个队列，支付宝逻辑机房使用
 * 
 * @author shijia.wxr<vintage.wang@gmail.com>
 * @since 2013-7-25
 */
public class SelectMessageQueueByMachineRoom implements MessageQueueSelector {
    private Set<String> consumeridcs;


    @Override
    public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
        // TODO Auto-generated method stub
        return null;
    }


    public Set<String> getConsumeridcs() {
        return consumeridcs;
    }


    public void setConsumeridcs(Set<String> consumeridcs) {
        this.consumeridcs = consumeridcs;
    }
}
