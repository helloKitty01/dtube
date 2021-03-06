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
package com.ict.dtube.example.filter;

import java.util.List;

import com.ict.dtube.client.consumer.DtubePushConsumer;
import com.ict.dtube.client.consumer.listener.ConsumeConcurrentlyContext;
import com.ict.dtube.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.ict.dtube.client.consumer.listener.MessageListenerConcurrently;
import com.ict.dtube.client.exception.MQClientException;
import com.ict.dtube.common.message.MessageExt;


public class Consumer {

    public static void main(String[] args) throws InterruptedException, MQClientException {
        DtubePushConsumer consumer = new DtubePushConsumer("ConsumerGroupNamecc4");
        consumer.setNamesrvAddr("10.235.170.7:9877");
        /**
         * 使用Java代码，在服务器做消息过滤
         */
        consumer.subscribe("TopicFilter7", MessageFilterImpl.class.getCanonicalName());

        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                    ConsumeConcurrentlyContext context) {
                System.out.println(Thread.currentThread().getName() + " Receive New Messages: " + msgs);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        /**
         * Consumer对象在使用之前必须要调用start初始化，初始化一次即可<br>
         */
        consumer.start();

        System.out.println("Consumer Started.");
    }
}
