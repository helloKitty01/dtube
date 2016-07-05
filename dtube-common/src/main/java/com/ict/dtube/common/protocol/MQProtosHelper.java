package com.ict.dtube.common.protocol;

import com.ict.dtube.common.protocol.header.namesrv.RegisterBrokerRequestHeader;
import com.ict.dtube.remoting.common.RemotingHelper;
import com.ict.dtube.remoting.exception.RemotingConnectException;
import com.ict.dtube.remoting.exception.RemotingSendRequestException;
import com.ict.dtube.remoting.exception.RemotingTimeoutException;
import com.ict.dtube.remoting.protocol.RemotingCommand;


/**
 * 协议辅助类
 * 
 * @author shijia.wxr<vintage.wang@gmail.com>
 */
public class MQProtosHelper {
    /**
     * 将Broker地址注册到Name Server
     */
    public static boolean registerBrokerToNameServer(final String nsaddr, final String brokerAddr,
            final long timeoutMillis) {
        RegisterBrokerRequestHeader requestHeader = new RegisterBrokerRequestHeader();
        requestHeader.setBrokerAddr(brokerAddr);

        RemotingCommand request =
                RemotingCommand.createRequestCommand(RequestCode.REGISTER_BROKER, requestHeader);

        try {
            RemotingCommand response = RemotingHelper.invokeSync(nsaddr, request, timeoutMillis);
            if (response != null) {
                return ResponseCode.SUCCESS == response.getCode();
            }
        }
        catch (RemotingConnectException e) {
            e.printStackTrace();
        }
        catch (RemotingSendRequestException e) {
            e.printStackTrace();
        }
        catch (RemotingTimeoutException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }
}
