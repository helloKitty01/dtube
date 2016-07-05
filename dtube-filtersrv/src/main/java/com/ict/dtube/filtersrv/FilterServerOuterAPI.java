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
package com.ict.dtube.filtersrv;

import com.ict.dtube.client.exception.MQBrokerException;
import com.ict.dtube.common.protocol.RequestCode;
import com.ict.dtube.common.protocol.ResponseCode;
import com.ict.dtube.common.protocol.header.filtersrv.RegisterFilterServerRequestHeader;
import com.ict.dtube.common.protocol.header.filtersrv.RegisterFilterServerResponseHeader;
import com.ict.dtube.remoting.RemotingClient;
import com.ict.dtube.remoting.exception.RemotingCommandException;
import com.ict.dtube.remoting.exception.RemotingConnectException;
import com.ict.dtube.remoting.exception.RemotingSendRequestException;
import com.ict.dtube.remoting.exception.RemotingTimeoutException;
import com.ict.dtube.remoting.netty.NettyClientConfig;
import com.ict.dtube.remoting.netty.NettyRemotingClient;
import com.ict.dtube.remoting.protocol.RemotingCommand;


/**
 * Broker对外调用的API封装
 * 
 * @author shijia.wxr<vintage.wang@gmail.com>
 * @since 2014-4-10
 */
public class FilterServerOuterAPI {
    private final RemotingClient remotingClient;


    public FilterServerOuterAPI() {
        this.remotingClient = new NettyRemotingClient(new NettyClientConfig());
    }


    public void start() {
        this.remotingClient.start();
    }


    public void shutdown() {
        this.remotingClient.shutdown();
    }


    public RegisterFilterServerResponseHeader registerFilterServerToBroker(//
            final String brokerAddr,// 1
            final String filterServerAddr// 2
    ) throws RemotingCommandException, RemotingConnectException, RemotingSendRequestException,
            RemotingTimeoutException, InterruptedException, MQBrokerException {
        RegisterFilterServerRequestHeader requestHeader = new RegisterFilterServerRequestHeader();
        requestHeader.setFilterServerAddr(filterServerAddr);
        RemotingCommand request =
                RemotingCommand.createRequestCommand(RequestCode.REGISTER_FILTER_SERVER, requestHeader);

        RemotingCommand response = this.remotingClient.invokeSync(brokerAddr, request, 3000);
        assert response != null;
        switch (response.getCode()) {
        case ResponseCode.SUCCESS: {
            RegisterFilterServerResponseHeader responseHeader =
                    (RegisterFilterServerResponseHeader) response
                        .decodeCommandCustomHeader(RegisterFilterServerResponseHeader.class);

            return responseHeader;
        }
        default:
            break;
        }

        throw new MQBrokerException(response.getCode(), response.getRemark());
    }
}
