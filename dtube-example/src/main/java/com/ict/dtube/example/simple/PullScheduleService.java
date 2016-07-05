package com.ict.dtube.example.simple;

import com.ict.dtube.client.consumer.DefaultMQPullConsumer;
import com.ict.dtube.client.consumer.MQPullConsumerScheduleService;
import com.ict.dtube.client.consumer.PullResult;
import com.ict.dtube.client.consumer.PullTaskCallback;
import com.ict.dtube.client.consumer.PullTaskContext;
import com.ict.dtube.client.exception.MQClientException;
import com.ict.dtube.common.message.MessageQueue;
import com.ict.dtube.common.protocol.heartbeat.MessageModel;


public class PullScheduleService {

    public static void main(String[] args) throws MQClientException {
        final MQPullConsumerScheduleService scheduleService = new MQPullConsumerScheduleService("GroupName1");

        scheduleService.setMessageModel(MessageModel.CLUSTERING);
        scheduleService.registerPullTaskCallback("TopicTest1", new PullTaskCallback() {

            @Override
            public void doPullTask(MessageQueue mq, PullTaskContext context) {
                DefaultMQPullConsumer consumer = scheduleService.getDefaultMQPullConsumer();
                try {
                    long offset = consumer.fetchConsumeOffset(mq, false);
                    if (offset < 0)
                        offset = 0;

                    PullResult pullResult = consumer.pull(mq, "*", offset, 32);
                    System.out.println(offset + "\t" + mq + "\t" + pullResult);
                    switch (pullResult.getPullStatus()) {
                    case FOUND:
                        break;
                    case NO_MATCHED_MSG:
                        break;
                    case NO_NEW_MSG:
                    case OFFSET_ILLEGAL:
                        break;
                    default:
                        break;
                    }

                    consumer.updateConsumeOffset(mq, pullResult.getNextBeginOffset());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        scheduleService.start();
    }
}
