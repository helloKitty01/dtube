package com.ict.dtube.common.protocol.header;

import com.ict.dtube.remoting.CommandCustomHeader;
import com.ict.dtube.remoting.exception.RemotingCommandException;


/**
 * @author shijia.wxr<vintage.wang@gmail.com>
 */
public class GetConsumerListByGroupResponseHeader implements CommandCustomHeader {

    @Override
    public void checkFields() throws RemotingCommandException {
    }
}
