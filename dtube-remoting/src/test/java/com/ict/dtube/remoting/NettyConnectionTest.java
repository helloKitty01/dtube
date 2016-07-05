package com.ict.dtube.remoting;

import org.junit.Test;

import com.ict.dtube.remoting.exception.RemotingConnectException;
import com.ict.dtube.remoting.exception.RemotingSendRequestException;
import com.ict.dtube.remoting.exception.RemotingTimeoutException;
import com.ict.dtube.remoting.netty.NettyClientConfig;
import com.ict.dtube.remoting.netty.NettyRemotingClient;
import com.ict.dtube.remoting.protocol.RemotingCommand;


/**
 * 连接超时测试
 * 
 * @author shijia.wxr<vintage.wang@gmail.com>
 * @since 2013-7-6
 */
public class NettyConnectionTest {
    public static RemotingClient createRemotingClient() {
        NettyClientConfig config = new NettyClientConfig();
        config.setClientChannelMaxIdleTimeSeconds(15);
        RemotingClient client = new NettyRemotingClient(config);
        client.start();
        return client;
    }


    @Test
    public void test_connect_timeout() throws InterruptedException, RemotingConnectException,
            RemotingSendRequestException, RemotingTimeoutException {
        RemotingClient client = createRemotingClient();

        for (int i = 0; i < 100; i++) {
            try {
                RemotingCommand request = RemotingCommand.createRequestCommand(0, null);
                RemotingCommand response = client.invokeSync("127.0.0.1:8888", request, 1000 * 3);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        client.shutdown();
        System.out.println("-----------------------------------------------------------------");
    }
}
