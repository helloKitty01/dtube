package com.ict.dtube.research.filter;

import com.ict.dtube.common.message.MessageExt;


public interface MessageFilter {
    public MessageExt doFilter(final MessageExt msg);

}
