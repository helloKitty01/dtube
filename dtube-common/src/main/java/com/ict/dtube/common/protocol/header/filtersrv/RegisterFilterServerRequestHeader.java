package com.ict.dtube.common.protocol.header.filtersrv;

import com.ict.dtube.remoting.CommandCustomHeader;
import com.ict.dtube.remoting.annotation.CFNotNull;
import com.ict.dtube.remoting.exception.RemotingCommandException;


public class RegisterFilterServerRequestHeader implements CommandCustomHeader {
    @CFNotNull
    private String filterServerAddr;


    @Override
    public void checkFields() throws RemotingCommandException {
    }


    public String getFilterServerAddr() {
        return filterServerAddr;
    }


    public void setFilterServerAddr(String filterServerAddr) {
        this.filterServerAddr = filterServerAddr;
    }
}
