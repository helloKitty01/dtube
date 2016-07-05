/**
 * $Id: EndTransactionRequestHeader.java 1835 2013-05-16 02:00:50Z shijia.wxr $
 */
package com.ict.dtube.common.protocol.header;

import com.ict.dtube.common.sysflag.MessageSysFlag;
import com.ict.dtube.remoting.CommandCustomHeader;
import com.ict.dtube.remoting.annotation.CFNotNull;
import com.ict.dtube.remoting.annotation.CFNullable;
import com.ict.dtube.remoting.exception.RemotingCommandException;


/**
 * @author shijia.wxr<vintage.wang@gmail.com>
 */
public class EndTransactionRequestHeader implements CommandCustomHeader {
    @CFNotNull
    private String producerGroup;
    @CFNotNull
    private Long tranStateTableOffset;
    @CFNotNull
    private Long commitLogOffset;
    @CFNotNull
    private Integer commitOrRollback; // TransactionCommitType
    // TransactionRollbackType
    // TransactionNotType

    @CFNullable
    private Boolean fromTransactionCheck = false;

    @CFNotNull
    private String msgId;


    @Override
    public void checkFields() throws RemotingCommandException {
        if (MessageSysFlag.TransactionNotType == this.commitOrRollback) {
            return;
        }

        if (MessageSysFlag.TransactionCommitType == this.commitOrRollback) {
            return;
        }

        if (MessageSysFlag.TransactionRollbackType == this.commitOrRollback) {
            return;
        }

        throw new RemotingCommandException("commitOrRollback field wrong");
    }


    public String getProducerGroup() {
        return producerGroup;
    }


    public void setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
    }


    public Long getTranStateTableOffset() {
        return tranStateTableOffset;
    }


    public void setTranStateTableOffset(Long tranStateTableOffset) {
        this.tranStateTableOffset = tranStateTableOffset;
    }


    public Long getCommitLogOffset() {
        return commitLogOffset;
    }


    public void setCommitLogOffset(Long commitLogOffset) {
        this.commitLogOffset = commitLogOffset;
    }


    public Integer getCommitOrRollback() {
        return commitOrRollback;
    }


    public void setCommitOrRollback(Integer commitOrRollback) {
        this.commitOrRollback = commitOrRollback;
    }


    public Boolean getFromTransactionCheck() {
        return fromTransactionCheck;
    }


    public void setFromTransactionCheck(Boolean fromTransactionCheck) {
        this.fromTransactionCheck = fromTransactionCheck;
    }


    public String getMsgId() {
        return msgId;
    }


    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }


    @Override
    public String toString() {
        return "EndTransactionRequestHeader [producerGroup=" + producerGroup + ", tranStateTableOffset="
                + tranStateTableOffset + ", commitLogOffset=" + commitLogOffset + ", commitOrRollback="
                + commitOrRollback + ", fromTransactionCheck=" + fromTransactionCheck + ", msgId=" + msgId
                + "]";
    }
}
