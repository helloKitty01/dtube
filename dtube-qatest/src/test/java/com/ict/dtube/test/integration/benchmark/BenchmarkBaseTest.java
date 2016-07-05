package com.ict.dtube.test.integration.benchmark;

import org.apache.commons.lang.StringUtils;

import com.ict.dtube.common.MixAll;
import com.ict.dtube.common.message.Message;
import com.ict.dtube.test.integration.BaseTest;


/**
 * @author manhong.yqd<jodie.yqd@gmail.com>
 * @since 2013-8-26
 */
public class BenchmarkBaseTest extends BaseTest {
    protected String consumerGroup = "qatest_Benchmark_consumer";
    protected String producerGroup = "qatest_Benchmark_producer";
    protected String topic = "qatest_BenchmarkTest";
    static {
        if (StringUtils.isNotBlank(NAME_SERVER_LIST)) {
            System.setProperty(MixAll.NAMESRV_ADDR_PROPERTY, NAME_SERVER_LIST);
        }
        else {
            try {
                initNameServerAndBroker();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    protected static Message buildMessage(final int messageSize) {
        Message msg = new Message();
        msg.setTopic("BenchmarkTest");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < messageSize; i += 10) {
            sb.append("hello baby");
        }

        msg.setBody(sb.toString().getBytes());

        return msg;
    }
}
