/**
 * Copyright (C) 2010-2013 Alibaba Group Holding Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ict.dtube.client.consumer;

import java.util.List;

import com.ict.dtube.common.message.MessageExt;


/**
 * Get方法返回结果类
 * @author sky
 */
public class GetResult {
    private final PullStatus getStatus;
    private List<MessageExt> messageList;
    private int bufferSize;
    private int buffercapacity;


    public GetResult(PullStatus pullStatus,
            List<MessageExt> msgFoundList) {
        this.getStatus = pullStatus;
        this.messageList = msgFoundList;
    }


    public List<MessageExt> getMessageList() {
		return messageList;
	}


	public void setMessageList(List<MessageExt> messageList) {
		this.messageList = messageList;
	}


	public PullStatus getGetStatus() {
		return getStatus;
	}


	@Override
    public String toString() {
        return "GetResult [getStatus=" + getStatus + ",messageList="
                + (messageList == null ? 0 : messageList.size()) + "]";
    }
}
