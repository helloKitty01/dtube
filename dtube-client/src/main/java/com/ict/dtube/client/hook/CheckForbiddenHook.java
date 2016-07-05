package com.ict.dtube.client.hook;

import com.ict.dtube.client.exception.MQClientException;


/**
 * 读写权限控制 Hook
 * 
 * @author: manhong.yqd<jodie.yqd@gmail.com>
 * @since: 14-4-9
 */
public interface CheckForbiddenHook {
    public String hookName();


    public void checkForbidden(final CheckForbiddenContext context) throws MQClientException;
}
