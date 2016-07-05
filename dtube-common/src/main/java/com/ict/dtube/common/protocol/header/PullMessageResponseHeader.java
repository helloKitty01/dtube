/**
 * $Id: PullMessageResponseHeader.java 1835 2013-05-16 02:00:50Z shijia.wxr $
 */
package com.ict.dtube.common.protocol.header;

import com.ict.dtube.remoting.CommandCustomHeader;
import com.ict.dtube.remoting.annotation.CFNotNull;
import com.ict.dtube.remoting.exception.RemotingCommandException;


/**
 * @author shijia.wxr<vintage.wang@gmail.com>
 */
public class PullMessageResponseHeader implements CommandCustomHeader {
    @CFNotNull
    private Long suggestWhichBrokerId;
    @CFNotNull
    private Long nextBeginOffset;
    @CFNotNull
    private Long minOffset;
    @CFNotNull
    private Long maxOffset;


    @Override
    public void checkFields() throws RemotingCommandException {
    }


    public Long getNextBeginOffset() {
        return nextBeginOffset;
    }


    public void setNextBeginOffset(Long nextBeginOffset) {
        this.nextBeginOffset = nextBeginOffset;
    }


    public Long getMinOffset() {
        return minOffset;
    }


    public void setMinOffset(Long minOffset) {
        this.minOffset = minOffset;
    }


    public Long getMaxOffset() {
        return maxOffset;
    }


    public void setMaxOffset(Long maxOffset) {
        this.maxOffset = maxOffset;
    }


    public Long getSuggestWhichBrokerId() {
        return suggestWhichBrokerId;
    }


    public void setSuggestWhichBrokerId(Long suggestWhichBrokerId) {
        this.suggestWhichBrokerId = suggestWhichBrokerId;
    }
}
