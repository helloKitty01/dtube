package com.ict.dtube.common.protocol.header.filtersrv;

import com.ict.dtube.remoting.CommandCustomHeader;
import com.ict.dtube.remoting.annotation.CFNotNull;
import com.ict.dtube.remoting.exception.RemotingCommandException;


public class RegisterMessageFilterClassRequestHeader implements CommandCustomHeader {
    @CFNotNull
    private String consumerGroup;
    @CFNotNull
    private String topic;
    @CFNotNull
    private String className;
    @CFNotNull
    private Integer classCRC;


    @Override
    public void checkFields() throws RemotingCommandException {
    }


    public String getConsumerGroup() {
        return consumerGroup;
    }


    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }


    public String getTopic() {
        return topic;
    }


    public void setTopic(String topic) {
        this.topic = topic;
    }


    public String getClassName() {
        return className;
    }


    public void setClassName(String className) {
        this.className = className;
    }


    public Integer getClassCRC() {
        return classCRC;
    }


    public void setClassCRC(Integer classCRC) {
        this.classCRC = classCRC;
    }
}
