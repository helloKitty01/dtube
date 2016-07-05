package com.ict.dtube.broker.transaction;

public class TransactionRecord {
    // Commit Log Offset
    private long offset;
    private String producerGroup;


    public long getOffset() {
        return offset;
    }


    public void setOffset(long offset) {
        this.offset = offset;
    }


    public String getProducerGroup() {
        return producerGroup;
    }


    public void setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
    }
}