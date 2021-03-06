package com.ict.dtube.example.filter;

import com.ict.dtube.common.filter.MessageFilter;
import com.ict.dtube.common.message.MessageExt;


public class MessageFilterImpl implements MessageFilter {

    @Override
    public boolean match(MessageExt msg) {
        String property = msg.getProperty("SequenceId");
        if (property != null) {
            int id = Integer.parseInt(property);
            if (((id % 10) == 0) && //
                    (id > 100)) {
                return true;
            }
        }

        return false;
    }
}
