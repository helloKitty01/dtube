package com.ict.dtube.common.protocol.body;

import java.util.Map;

import com.ict.dtube.common.message.MessageQueue;
import com.ict.dtube.remoting.protocol.RemotingSerializable;


/**
 * 重置 offset 处理结果。
 * 
 * @author: manhong.yqd<jodie.yqd@gmail.com>
 * @since: 13-12-30
 */
public class ResetOffsetBody extends RemotingSerializable {
    private Map<MessageQueue, Long> offsetTable;


    public Map<MessageQueue, Long> getOffsetTable() {
        return offsetTable;
    }


    public void setOffsetTable(Map<MessageQueue, Long> offsetTable) {
        this.offsetTable = offsetTable;
    }
}
