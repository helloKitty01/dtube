/**
 * $Id: RegisterOrderTopicRequestHeader.java 1835 2013-05-16 02:00:50Z shijia.wxr $
 */
package com.ict.dtube.common.protocol.header.namesrv;

import com.ict.dtube.remoting.CommandCustomHeader;
import com.ict.dtube.remoting.annotation.CFNotNull;
import com.ict.dtube.remoting.exception.RemotingCommandException;


/**
 * @author shijia.wxr<vintage.wang@gmail.com>
 */
public class RegisterOrderTopicRequestHeader implements CommandCustomHeader {
    @CFNotNull
    private String topic;
    @CFNotNull
    private String orderTopicString;


    @Override
    public void checkFields() throws RemotingCommandException {
        // TODO Auto-generated method stub
    }


    public String getTopic() {
        return topic;
    }


    public void setTopic(String topic) {
        this.topic = topic;
    }


    public String getOrderTopicString() {
        return orderTopicString;
    }


    public void setOrderTopicString(String orderTopicString) {
        this.orderTopicString = orderTopicString;
    }
}
