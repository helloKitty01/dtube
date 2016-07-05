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
package com.ict.dtube.broker.processor;

import io.netty.channel.ChannelHandlerContext;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ict.dtube.broker.BrokerController;
import com.ict.dtube.broker.client.ClientChannelInfo;
import com.ict.dtube.broker.client.ConsumerGroupInfo;
import com.ict.dtube.common.MixAll;
import com.ict.dtube.common.constant.LoggerName;
import com.ict.dtube.common.constant.PermName;
import com.ict.dtube.common.protocol.RequestCode;
import com.ict.dtube.common.protocol.ResponseCode;
import com.ict.dtube.common.protocol.header.GetConsumerListByGroupRequestHeader;
import com.ict.dtube.common.protocol.header.GetConsumerListByGroupResponseBody;
import com.ict.dtube.common.protocol.header.GetConsumerListByGroupResponseHeader;
import com.ict.dtube.common.protocol.header.UnregisterClientRequestHeader;
import com.ict.dtube.common.protocol.header.UnregisterClientResponseHeader;
import com.ict.dtube.common.protocol.heartbeat.ConsumerData;
import com.ict.dtube.common.protocol.heartbeat.HeartbeatData;
import com.ict.dtube.common.protocol.heartbeat.ProducerData;
import com.ict.dtube.common.subscription.SubscriptionGroupConfig;
import com.ict.dtube.remoting.common.RemotingHelper;
import com.ict.dtube.remoting.exception.RemotingCommandException;
import com.ict.dtube.remoting.netty.NettyRequestProcessor;
import com.ict.dtube.remoting.protocol.RemotingCommand;


/**
 * Client注册与注销管理
 * 
 * @author shijia.wxr<vintage.wang@gmail.com>
 * @since 2013-7-26
 */
public class ClientManageProcessor implements NettyRequestProcessor {
    private static final Logger log = LoggerFactory.getLogger(LoggerName.BrokerLoggerName);

    private final BrokerController brokerController;


    public ClientManageProcessor(final BrokerController brokerController) {
        this.brokerController = brokerController;
    }


    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request)
            throws RemotingCommandException {
        switch (request.getCode()) {
        case RequestCode.HEART_BEAT:
            return this.heartBeat(ctx, request);
        case RequestCode.UNREGISTER_CLIENT:
            return this.unregisterClient(ctx, request);
        case RequestCode.GET_CONSUMER_LIST_BY_GROUP:
            return this.getConsumerListByGroup(ctx, request);
        default:
            break;
        }
        return null;
    }


    public RemotingCommand getConsumerListByGroup(ChannelHandlerContext ctx, RemotingCommand request)
            throws RemotingCommandException {
        final RemotingCommand response =
                RemotingCommand.createResponseCommand(GetConsumerListByGroupResponseHeader.class);
        final GetConsumerListByGroupRequestHeader requestHeader =
                (GetConsumerListByGroupRequestHeader) request
                    .decodeCommandCustomHeader(GetConsumerListByGroupRequestHeader.class);

        ConsumerGroupInfo consumerGroupInfo =
                this.brokerController.getConsumerManager().getConsumerGroupInfo(
                    requestHeader.getConsumerGroup());
        if (consumerGroupInfo != null) {
            List<String> clientIds = consumerGroupInfo.getAllClientId();
            if (!clientIds.isEmpty()) {
                GetConsumerListByGroupResponseBody body = new GetConsumerListByGroupResponseBody();
                body.setConsumerIdList(clientIds);
                response.setBody(body.encode());
                response.setCode(ResponseCode.SUCCESS);
                response.setRemark(null);
                return response;
            }
            else {
                log.warn("getAllClientId failed, {} {}", requestHeader.getConsumerGroup(),
                    RemotingHelper.parseChannelRemoteAddr(ctx.channel()));
            }
        }
        else {
            log.warn("getConsumerGroupInfo failed, {} {}", requestHeader.getConsumerGroup(),
                RemotingHelper.parseChannelRemoteAddr(ctx.channel()));
        }

        response.setCode(ResponseCode.SYSTEM_ERROR);
        response.setRemark("no consumer for this group, " + requestHeader.getConsumerGroup());
        return response;
    }


    public RemotingCommand unregisterClient(ChannelHandlerContext ctx, RemotingCommand request)
            throws RemotingCommandException {
        final RemotingCommand response =
                RemotingCommand.createResponseCommand(UnregisterClientResponseHeader.class);
        final UnregisterClientRequestHeader requestHeader =
                (UnregisterClientRequestHeader) request
                    .decodeCommandCustomHeader(UnregisterClientRequestHeader.class);

        ClientChannelInfo clientChannelInfo = new ClientChannelInfo(//
            ctx.channel(),//
            requestHeader.getClientID(),//
            request.getLanguage(),//
            request.getVersion()//
                );

        // 注销Producer
        {
            final String group = requestHeader.getProducerGroup();
            if (group != null) {
                this.brokerController.getProducerManager().unregisterProducer(group, clientChannelInfo);
            }
        }

        // 注销Consumer
        {
            final String group = requestHeader.getConsumerGroup();
            if (group != null) {
                this.brokerController.getConsumerManager().unregisterConsumer(group,
                    clientChannelInfo);
            }
        }

        response.setCode(ResponseCode.SUCCESS);
        response.setRemark(null);
        return response;
    }


    public RemotingCommand heartBeat(ChannelHandlerContext ctx, RemotingCommand request) {
        RemotingCommand response = RemotingCommand.createResponseCommand(null);

        HeartbeatData heartbeatData = HeartbeatData.decode(request.getBody(), HeartbeatData.class);

        ClientChannelInfo clientChannelInfo = new ClientChannelInfo(//
            ctx.channel(),//
            heartbeatData.getClientID(),//
            request.getLanguage(),//
            request.getVersion()//
                );

        // 注册Consumer
        for (ConsumerData data : heartbeatData.getConsumerDataSet()) {
            SubscriptionGroupConfig subscriptionGroupConfig =
                    this.brokerController.getSubscriptionGroupManager().findSubscriptionGroupConfig(
                        data.getGroupName());
            if (null != subscriptionGroupConfig) {
                String newTopic = MixAll.getRetryTopic(data.getGroupName());
                this.brokerController.getTopicConfigManager().createTopicInSendMessageBackMethod(//
                    newTopic,//
                    subscriptionGroupConfig.getRetryQueueNums(), //
                    PermName.PERM_WRITE | PermName.PERM_READ);
            }

            boolean changed = this.brokerController.getConsumerManager().registerConsumer(//
                data.getGroupName(),//
                clientChannelInfo,//
                data.getConsumeType(),//
                data.getMessageModel(),//
                data.getConsumeFromWhere(),//
                data.getSubscriptionDataSet()//
                );

            if (changed) {
                log.info("registerConsumer info changed {} {}",//
                    data.toString(),//
                    RemotingHelper.parseChannelRemoteAddr(ctx.channel())//
                );
            }
        }

        // 注册Producer
        for (ProducerData data : heartbeatData.getProducerDataSet()) {
            this.brokerController.getProducerManager().registerProducer(data.getGroupName(),
                clientChannelInfo);
        }

        response.setCode(ResponseCode.SUCCESS);
        response.setRemark(null);
        return response;
    }
}
