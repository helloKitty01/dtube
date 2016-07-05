package com.ict.dtube.client;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

import com.ict.dtube.client.consumer.DtubePushConsumer;
import com.ict.dtube.client.consumer.listener.ConsumeConcurrentlyContext;
import com.ict.dtube.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.ict.dtube.client.consumer.listener.MessageListenerConcurrently;
import com.ict.dtube.client.exception.MQClientException;
import com.ict.dtube.client.producer.DtubeProducer;
import com.ict.dtube.client.producer.SendResult;
import com.ict.dtube.common.consumer.ConsumeFromWhere;
import com.ict.dtube.common.message.Message;
import com.ict.dtube.common.message.MessageExt;


public class TestMultiConsumerProducer {
    private static final String TOPIC_TEST = "TopicTest-fundmng";


    @Test
    public void testProducerConsumer() throws MQClientException, InterruptedException {
        System.setProperty("dtube.namesrv.domain", "jmenv.tbsite.alipay.net");

        DtubePushConsumer consumer = new DtubePushConsumer("S_fundmng_demo_producer");
        DtubeProducer producer = new DtubeProducer("P_fundmng_demo_producer");

        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.subscribe(TOPIC_TEST, null);

        final AtomicLong lastReceivedMills = new AtomicLong(System.currentTimeMillis());

        final AtomicLong consumeTimes = new AtomicLong(0);

        consumer.registerMessageListener(new MessageListenerConcurrently() {
            public ConsumeConcurrentlyStatus consumeMessage(final List<MessageExt> msgs,
                    final ConsumeConcurrentlyContext context) {
                System.out.println("接收了" + consumeTimes.incrementAndGet() + "条消息!");

                lastReceivedMills.set(System.currentTimeMillis());

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        consumer.start();
        producer.start();

        for (int i = 0; i < 100; i++) {
            try {
                Message msg = new Message(TOPIC_TEST, ("Hello Dtube " + i).getBytes());
                SendResult sendResult = producer.send(msg);
                System.out.println(sendResult);
            }
            catch (Exception e) {
                TimeUnit.SECONDS.sleep(1);
            }
        }

        // wait no messages
        while ((System.currentTimeMillis() - lastReceivedMills.get()) < 5000) {
            TimeUnit.MILLISECONDS.sleep(200);
        }

        consumer.shutdown();
        producer.shutdown();
    }
}
