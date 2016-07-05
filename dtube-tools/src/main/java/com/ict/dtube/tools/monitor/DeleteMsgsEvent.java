package com.ict.dtube.tools.monitor;

import com.ict.dtube.common.protocol.topic.OffsetMovedEvent;


public class DeleteMsgsEvent {
    private OffsetMovedEvent offsetMovedEvent;
    private long eventTimestamp;


    public OffsetMovedEvent getOffsetMovedEvent() {
        return offsetMovedEvent;
    }


    public void setOffsetMovedEvent(OffsetMovedEvent offsetMovedEvent) {
        this.offsetMovedEvent = offsetMovedEvent;
    }


    public long getEventTimestamp() {
        return eventTimestamp;
    }


    public void setEventTimestamp(long eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }
}
