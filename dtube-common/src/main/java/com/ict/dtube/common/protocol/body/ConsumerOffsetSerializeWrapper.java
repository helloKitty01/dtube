package com.ict.dtube.common.protocol.body;

import java.util.concurrent.ConcurrentHashMap;

import com.ict.dtube.remoting.protocol.RemotingSerializable;


/**
 * Consumer消费进度，序列化包装
 * 
 * @author manhong.yqd<manhong.yqd@ict.com>
 * @since 2013-8-19
 */
public class ConsumerOffsetSerializeWrapper extends RemotingSerializable {
    private ConcurrentHashMap<String/* topic@group */, ConcurrentHashMap<Integer, Long>> offsetTable =
            new ConcurrentHashMap<String, ConcurrentHashMap<Integer, Long>>(512);


    public ConcurrentHashMap<String, ConcurrentHashMap<Integer, Long>> getOffsetTable() {
        return offsetTable;
    }


    public void setOffsetTable(ConcurrentHashMap<String, ConcurrentHashMap<Integer, Long>> offsetTable) {
        this.offsetTable = offsetTable;
    }
}
