/**
 * $Id: NamesrvConfig.java 1839 2013-05-16 02:12:02Z shijia.wxr $
 */
package com.ict.dtube.common.namesrv;

import java.io.File;

import com.ict.dtube.common.MixAll;


/**
 * Name server 的配置类
 * 
 * @author shijia.wxr<vintage.wang@gmail.com>
 * @author lansheng.zj@ict.com
 */
public class NamesrvConfig {
    private String dtubeHome = System.getProperty(MixAll.DTUBE_HOME_PROPERTY,
        System.getenv(MixAll.DTUBE_HOME_ENV));
    // 通用的KV配置持久化地址
    private String kvConfigPath = System.getProperty("user.home") + File.separator + "namesrv"
            + File.separator + "kvConfig.json";


    public String getDtubeHome() {
        return dtubeHome;
    }


    public void setDtubeHome(String dtubeHome) {
        this.dtubeHome = dtubeHome;
    }


    public String getKvConfigPath() {
        return kvConfigPath;
    }


    public void setKvConfigPath(String kvConfigPath) {
        this.kvConfigPath = kvConfigPath;
    }
}
