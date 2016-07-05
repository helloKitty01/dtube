package com.ict.dtube.common.protocol.body;

import java.util.concurrent.ConcurrentHashMap;

import com.ict.dtube.common.DataVersion;
import com.ict.dtube.common.TopicConfig;
import com.ict.dtube.remoting.protocol.RemotingSerializable;


public class TopicConfigSerializeWrapper extends RemotingSerializable {
    private ConcurrentHashMap<String, TopicConfig> topicConfigTable =
            new ConcurrentHashMap<String, TopicConfig>();
    private DataVersion dataVersion = new DataVersion();


    public ConcurrentHashMap<String, TopicConfig> getTopicConfigTable() {
        return topicConfigTable;
    }


    public void setTopicConfigTable(ConcurrentHashMap<String, TopicConfig> topicConfigTable) {
        this.topicConfigTable = topicConfigTable;
    }


    public DataVersion getDataVersion() {
        return dataVersion;
    }


    public void setDataVersion(DataVersion dataVersion) {
        this.dataVersion = dataVersion;
    }
}
