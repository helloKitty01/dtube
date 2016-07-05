package com.ict.dtube.common.protocol.header;

import com.ict.dtube.remoting.CommandCustomHeader;
import com.ict.dtube.remoting.annotation.CFNotNull;
import com.ict.dtube.remoting.exception.RemotingCommandException;


/**
 * 删除订阅组请求参数
 * 
 * @author manhong.yqd<manhong.yqd@ict.com>
 * @since 2013-8-22
 */
public class DeleteSubscriptionGroupRequestHeader implements CommandCustomHeader {
    @CFNotNull
    private String groupName;


    @Override
    public void checkFields() throws RemotingCommandException {
    }


    public String getGroupName() {
        return groupName;
    }


    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
