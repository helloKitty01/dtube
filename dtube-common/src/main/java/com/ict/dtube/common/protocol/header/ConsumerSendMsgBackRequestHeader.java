package com.ict.dtube.common.protocol.header;

import com.ict.dtube.remoting.CommandCustomHeader;
import com.ict.dtube.remoting.annotation.CFNotNull;
import com.ict.dtube.remoting.exception.RemotingCommandException;


/**
 * @author shijia.wxr<vintage.wang@gmail.com>
 * @since 2013-6-18
 */
public class ConsumerSendMsgBackRequestHeader implements CommandCustomHeader {
    @CFNotNull
    private Long offset;
    @CFNotNull
    private String group;
    @CFNotNull
    private Integer delayLevel;


    @Override
    public void checkFields() throws RemotingCommandException {

    }


    public Long getOffset() {
        return offset;
    }


    public void setOffset(Long offset) {
        this.offset = offset;
    }


    public String getGroup() {
        return group;
    }


    public void setGroup(String group) {
        this.group = group;
    }


    public Integer getDelayLevel() {
        return delayLevel;
    }


    public void setDelayLevel(Integer delayLevel) {
        this.delayLevel = delayLevel;
    }


    @Override
    public String toString() {
        return "ConsumerSendMsgBackRequestHeader [offset=" + offset + ", group=" + group + ", delayLevel="
                + delayLevel + "]";
    }
}
