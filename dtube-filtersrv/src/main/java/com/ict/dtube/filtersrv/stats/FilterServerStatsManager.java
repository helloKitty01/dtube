package com.ict.dtube.filtersrv.stats;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ict.dtube.common.ThreadFactoryImpl;
import com.ict.dtube.common.constant.LoggerName;
import com.ict.dtube.common.stats.StatsItemSet;


public class FilterServerStatsManager {
    private static final Logger log = LoggerFactory.getLogger(LoggerName.FiltersrvLoggerName);
    private final ScheduledExecutorService scheduledExecutorService = Executors
        .newSingleThreadScheduledExecutor(new ThreadFactoryImpl("FSStatsThread"));

    // ConsumerGroup Get Nums
    private final StatsItemSet groupGetNums = new StatsItemSet("GROUP_GET_NUMS",
        this.scheduledExecutorService, log);

    // ConsumerGroup Get Size
    private final StatsItemSet groupGetSize = new StatsItemSet("GROUP_GET_SIZE",
        this.scheduledExecutorService, log);


    public FilterServerStatsManager() {
    }


    public void start() {
    }


    public void shutdown() {
        this.scheduledExecutorService.shutdown();
    }


    public void incGroupGetNums(final String group, final String topic, final int incValue) {
        this.groupGetNums.addValue(topic + "@" + group, incValue, 1);
    }


    public void incGroupGetSize(final String group, final String topic, final int incValue) {
        this.groupGetSize.addValue(topic + "@" + group, incValue, 1);
    }
}
