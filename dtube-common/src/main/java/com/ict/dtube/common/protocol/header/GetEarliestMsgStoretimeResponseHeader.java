/**
 * $Id: GetEarliestMsgStoretimeResponseHeader.java 1835 2013-05-16 02:00:50Z shijia.wxr $
 */
package com.ict.dtube.common.protocol.header;

import com.ict.dtube.remoting.CommandCustomHeader;
import com.ict.dtube.remoting.annotation.CFNotNull;
import com.ict.dtube.remoting.exception.RemotingCommandException;


/**
 * @author shijia.wxr<vintage.wang@gmail.com>
 */
public class GetEarliestMsgStoretimeResponseHeader implements CommandCustomHeader {
    @CFNotNull
    private Long timestamp;


    @Override
    public void checkFields() throws RemotingCommandException {
    }


    public Long getTimestamp() {
        return timestamp;
    }


    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
