package com.ict.dtube.example.filter;

import com.ict.dtube.common.filter.MessageFilter;
import com.ict.dtube.common.message.MessageExt;


public class MessageFilterImpl implements MessageFilter {

    @Override
    public boolean match(MessageExt msg) {
        String property = msg.getUserProperty("SequenceId");
        if (property != null) {
            int id = Integer.parseInt(property);
            if ((id % 3) == 0 && (id > 10)) {
                return true;
            }
        }

        return false;
    }
}
