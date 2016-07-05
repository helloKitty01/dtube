/**
 * $Id: SendMessageResponseHeader.java 1835 2013-05-16 02:00:50Z shijia.wxr $
 */
package com.ict.dtube.common.protocol.header;

import com.ict.dtube.remoting.CommandCustomHeader;
import com.ict.dtube.remoting.annotation.CFNotNull;
import com.ict.dtube.remoting.exception.RemotingCommandException;


/**
 * @author shijia.wxr<vintage.wang@gmail.com>
 */
public class SendMessageResponseHeader implements CommandCustomHeader {
    @CFNotNull
    private String msgId;
    @CFNotNull
    private Integer queueId;
    @CFNotNull
    private Long queueOffset;


    @Override
    public void checkFields() throws RemotingCommandException {
    }


    public String getMsgId() {
        return msgId;
    }


    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }


    public Integer getQueueId() {
        return queueId;
    }


    public void setQueueId(Integer queueId) {
        this.queueId = queueId;
    }


    public Long getQueueOffset() {
        return queueOffset;
    }


    public void setQueueOffset(Long queueOffset) {
        this.queueOffset = queueOffset;
    }
}
