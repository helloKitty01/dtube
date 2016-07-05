package com.ict.dtube.common.protocol.header.namesrv;

import com.ict.dtube.remoting.CommandCustomHeader;
import com.ict.dtube.remoting.annotation.CFNotNull;
import com.ict.dtube.remoting.exception.RemotingCommandException;


public class PutKVConfigRequestHeader implements CommandCustomHeader {
    @CFNotNull
    private String namespace;
    @CFNotNull
    private String key;
    @CFNotNull
    private String value;


    @Override
    public void checkFields() throws RemotingCommandException {
    }


    public String getNamespace() {
        return namespace;
    }


    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }


    public String getKey() {
        return key;
    }


    public void setKey(String key) {
        this.key = key;
    }


    public String getValue() {
        return value;
    }


    public void setValue(String value) {
        this.value = value;
    }
}
