package com.ict.dtube.common.protocol.header;

import com.ict.dtube.remoting.CommandCustomHeader;
import com.ict.dtube.remoting.annotation.CFNotNull;
import com.ict.dtube.remoting.exception.RemotingCommandException;


/**
 * TODO
 * 
 * @author shijia.wxr<vintage.wang@gmail.com>
 * @since 13-8-5
 */
public class GetConsumerConnectionListRequestHeader implements CommandCustomHeader {
    @CFNotNull
    private String consumerGroup;


    @Override
    public void checkFields() throws RemotingCommandException {
        // To change body of implemented methods use File | Settings | File
        // Templates.
    }


    public String getConsumerGroup() {
        return consumerGroup;
    }


    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }
}
