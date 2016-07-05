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
public class GetProducerConnectionListRequestHeader implements CommandCustomHeader {
    @CFNotNull
    private String producerGroup;


    @Override
    public void checkFields() throws RemotingCommandException {
        // To change body of implemented methods use File | Settings | File
        // Templates.
    }


    public String getProducerGroup() {
        return producerGroup;
    }


    public void setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
    }
}
