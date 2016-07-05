package com.ict.dtube.research.filter;

import com.ict.dtube.common.message.MessageExt;


public class MessageFilterImpl implements MessageFilter {

    @Override
    public MessageExt doFilter(MessageExt msg) {
        System.out.println("MessageFilterImpl doFilter");
        return null;
    }

}
