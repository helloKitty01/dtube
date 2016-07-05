package com.ict.dtube.common.protocol.body;

import java.util.concurrent.ConcurrentHashMap;

import com.ict.dtube.common.DataVersion;
import com.ict.dtube.common.subscription.SubscriptionGroupConfig;
import com.ict.dtube.remoting.protocol.RemotingSerializable;


/**
 * 订阅组配置，序列化包装
 * 
 * @author manhong.yqd<manhong.yqd@ict.com>
 * @since 2013-8-19
 */
public class SubscriptionGroupWrapper extends RemotingSerializable {
    private ConcurrentHashMap<String, SubscriptionGroupConfig> subscriptionGroupTable =
            new ConcurrentHashMap<String, SubscriptionGroupConfig>(1024);
    private DataVersion dataVersion = new DataVersion();


    public ConcurrentHashMap<String, SubscriptionGroupConfig> getSubscriptionGroupTable() {
        return subscriptionGroupTable;
    }


    public void setSubscriptionGroupTable(
            ConcurrentHashMap<String, SubscriptionGroupConfig> subscriptionGroupTable) {
        this.subscriptionGroupTable = subscriptionGroupTable;
    }


    public DataVersion getDataVersion() {
        return dataVersion;
    }


    public void setDataVersion(DataVersion dataVersion) {
        this.dataVersion = dataVersion;
    }
}
