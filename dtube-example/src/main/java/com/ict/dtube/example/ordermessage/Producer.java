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
package com.ict.dtube.example.ordermessage;

import java.util.List;

import com.ict.dtube.client.exception.MQBrokerException;
import com.ict.dtube.client.exception.MQClientException;
import com.ict.dtube.client.producer.DtubeProducer;
import com.ict.dtube.client.producer.MQProducer;
import com.ict.dtube.client.producer.MessageQueueSelector;
import com.ict.dtube.client.producer.SendResult;
import com.ict.dtube.common.message.Message;
import com.ict.dtube.common.message.MessageQueue;
import com.ict.dtube.remoting.exception.RemotingException;


/**
 * Producer，发送顺序消息
 */
public class Producer {
    public static void main(String[] args) {
        try {
            MQProducer producer = new DtubeProducer("please_rename_unique_group_name");

            producer.start();

            String[] tags = new String[] { "TagA", "TagB", "TagC", "TagD", "TagE" };

            for (int i = 0; i < 100; i++) {
                // 订单ID相同的消息要有序
                int orderId = i % 10;
                Message msg =
                        new Message("TopicTestjjj", tags[i % tags.length], "KEY" + i,
                            ("Hello Dtube " + i).getBytes());

                SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
                    @Override
                    public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                        Integer id = (Integer) arg;
                        int index = id % mqs.size();
                        return mqs.get(index);
                    }
                }, orderId);

                System.out.println(sendResult);
            }

            producer.shutdown();
        }
        catch (MQClientException e) {
            e.printStackTrace();
        }
        catch (RemotingException e) {
            e.printStackTrace();
        }
        catch (MQBrokerException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
