package com.ict.dtube.common.protocol.header.namesrv;

import com.ict.dtube.remoting.CommandCustomHeader;
import com.ict.dtube.remoting.annotation.CFNotNull;
import com.ict.dtube.remoting.exception.RemotingCommandException;


/**
 * @author shijia.wxr<vintage.wang@gmail.com>
 * @since 2013-7-1
 */
public class GetKVListByNamespaceRequestHeader implements CommandCustomHeader {
    @CFNotNull
    private String namespace;


    @Override
    public void checkFields() throws RemotingCommandException {
    }


    public String getNamespace() {
        return namespace;
    }


    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
