package com.ict.dtube.common.namesrv;

/**
 * @author shijia.wxr<vintage.wang@gmail.com>
 * @since 2013-7-8
 */
public class RegisterBrokerResult {
    private String haServerAddr;
    private String masterAddr;


    public String getHaServerAddr() {
        return haServerAddr;
    }


    public void setHaServerAddr(String haServerAddr) {
        this.haServerAddr = haServerAddr;
    }


    public String getMasterAddr() {
        return masterAddr;
    }


    public void setMasterAddr(String masterAddr) {
        this.masterAddr = masterAddr;
    }
}
