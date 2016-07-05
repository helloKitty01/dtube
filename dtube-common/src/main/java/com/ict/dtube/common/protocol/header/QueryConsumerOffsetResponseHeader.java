/**
 * $Id: QueryConsumerOffsetResponseHeader.java 1835 2013-05-16 02:00:50Z shijia.wxr $
 */
package com.ict.dtube.common.protocol.header;

import com.ict.dtube.remoting.CommandCustomHeader;
import com.ict.dtube.remoting.annotation.CFNotNull;
import com.ict.dtube.remoting.exception.RemotingCommandException;


/**
 * @author shijia.wxr<vintage.wang@gmail.com>
 */
public class QueryConsumerOffsetResponseHeader implements CommandCustomHeader {
    @CFNotNull
    private Long offset;


    @Override
    public void checkFields() throws RemotingCommandException {
        // TODO Auto-generated method stub

    }


    public Long getOffset() {
        return offset;
    }


    public void setOffset(Long offset) {
        this.offset = offset;
    }
}
