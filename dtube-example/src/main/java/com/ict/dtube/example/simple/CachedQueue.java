package com.ict.dtube.example.simple;

import java.util.TreeMap;

import com.ict.dtube.common.message.MessageExt;


public class CachedQueue {
    private final TreeMap<Long, MessageExt> msgCachedTable = new TreeMap<Long, MessageExt>();


    public TreeMap<Long, MessageExt> getMsgCachedTable() {
        return msgCachedTable;
    }
}
