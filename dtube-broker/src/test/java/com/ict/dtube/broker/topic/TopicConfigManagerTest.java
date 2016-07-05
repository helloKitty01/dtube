/**
 * $Id: TopicConfigManagerTest.java 1831 2013-05-16 01:39:51Z shijia.wxr $
 */
package com.ict.dtube.broker.topic;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.ict.dtube.broker.BrokerController;
import com.ict.dtube.common.BrokerConfig;
import com.ict.dtube.common.MixAll;
import com.ict.dtube.common.TopicConfig;
import com.ict.dtube.remoting.netty.NettyClientConfig;
import com.ict.dtube.remoting.netty.NettyServerConfig;
import com.ict.dtube.store.config.MessageStoreConfig;


/**
 * @author shijia.wxr<vintage.wang@gmail.com>
 */
public class TopicConfigManagerTest {
    @Test
    public void test_flushTopicConfig() throws Exception {
        BrokerController brokerController = new BrokerController(//
            new BrokerConfig(), //
            new NettyServerConfig(), //
            new NettyClientConfig(), //
            new MessageStoreConfig());
        boolean initResult = brokerController.initialize();
        System.out.println("initialize " + initResult);
        brokerController.start();

        TopicConfigManager topicConfigManager = new TopicConfigManager(brokerController);

        TopicConfig topicConfig =
                topicConfigManager.createTopicInSendMessageMethod("TestTopic_SEND", MixAll.DEFAULT_TOPIC,
                    null, 4);
        assertTrue(topicConfig != null);

        System.out.println(topicConfig);

        for (int i = 0; i < 10; i++) {
            String topic = "UNITTEST-" + i;
            topicConfig =
                    topicConfigManager.createTopicInSendMessageMethod(topic, MixAll.DEFAULT_TOPIC, null, 4);
            assertTrue(topicConfig != null);
        }

        topicConfigManager.persist();

        brokerController.shutdown();
    }
}
