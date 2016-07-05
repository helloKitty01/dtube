package com.ict.dtube.mqclient.support;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ict.dtube.common.protocol.heartbeat.MessageModel;

public class MQConfig{
	
	//######全局相关设置
	//重发消息间隔
	private int resendPeroid=5;
	//批量重发消息数量
	private int resendBatch=1000;
	//发送失败消息持久化间隔
	private int commitPeroid=5;
        //是否启用监控
    private boolean useMonitor=false;
	
	//#######rocketmq相关设置
	//consumerGroup设置
	private String groupName="DefaultConsumerGroup";  //required
	//instance 设置
	private String rocketInstanceName=System.getProperty("rocketmq.client.name", "DEFAULT");
	//消费的类型 
	private MessageModel rocketConsumerMessageModel=MessageModel.CLUSTERING;
        //topic集合,value对应的需要消费的队列索引
        private Map<String,Set<Integer>> rocketTopicMap=new HashMap<String,Set<Integer>>();
        //每次请求从mq中获取的最大消息数
        private int rocketMaxSize=32;
        //队列数的最小的单位该参数指定一个broker中topic下queue的数量与rocketFactor配合实现MessageQueueSelector算法
        private int rocketSeed=8;
        //按照实际队列数是rocketSeed的几倍来定义这个系数 如实际队列数是16 该因子为 2 必须大于0
        private int rocketFactor=2;
        //消息体上线。超过之后自动压缩
        private int rocketMaxMsgBody=512;
        //异步发送的线程数
        private int rocketSendThreadNum=Runtime.getRuntime().availableProcessors()*2;
        //消息缓存大小
        private int rocketBufferSize=512;
        //异步发送开关
        private boolean rocketAsyncFlag=false;
        //最大积压数量 小于0 忽略该设置
        private int rocketMaxOverstock=-1;
        
	public MQConfig(){
		
	}
	
	public MQConfig(int resendPeroid,int resendBatch,int commitPeroid){
		this.resendPeroid=resendPeroid;
		this.resendBatch=resendBatch;
		this.commitPeroid=commitPeroid;
	}
	
	public int getResendPeroid() {
		return resendPeroid;
	}
	public void setResendPeroid(int resendPeroid) {
		this.resendPeroid = resendPeroid;
	}
	public int getResendBatch() {
		return resendBatch;
	}
	public void setResendBatch(int resendBatch) {
		this.resendBatch = resendBatch;
	}
	public int getCommitPeroid() {
		return commitPeroid;
	}
	public void setCommitPeroid(int commitPeroid) {
		this.commitPeroid = commitPeroid;
	}

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

	public String getRocketInstanceName() {
		return rocketInstanceName;
	}

	public void setRocketInstanceName(String rocketInstanceName) {
		this.rocketInstanceName = rocketInstanceName;
	}

	public MessageModel getRocketConsumerMessageModel() {
		return rocketConsumerMessageModel;
	}

	public void setRocketConsumerMessageModel(
			MessageModel rocketConsumerMessageModel) {
		this.rocketConsumerMessageModel = rocketConsumerMessageModel;
	}
        
        public void registRocketTopicSet(String topic,Set<Integer> ids){
            if(ids==null){
                rocketTopicMap.put(topic,new HashSet<Integer>());
            }else{
                rocketTopicMap.put(topic,new HashSet<Integer>(ids));
            }
        }
        
        public Map<String,Set<Integer>> getRocketTopicMap(){
            return rocketTopicMap;
        }

        public int getRocketMaxSize() {
            return rocketMaxSize;
        }

        public void setRocketMaxSize(int rocketMaxSize) {
            this.rocketMaxSize = rocketMaxSize;
        }
        
        
        /**
         * 获取所有的topic
         * @return
         */
        public Set<String> getRocketTopic(){
            return rocketTopicMap.keySet();
        }

        public int getRocketFactor() {
            return rocketFactor;
        }

        public void setRocketFactor(int rocketFactor) {
            if(rocketFactor>0){
                this.rocketFactor = rocketFactor;
            }
        }

        public int getRocketMaxMsgBody() {
            return rocketMaxMsgBody;
        }

        public void setRocketMaxMsgBody(int rocketMaxMsgBody) {
            this.rocketMaxMsgBody = rocketMaxMsgBody;
        }

        public int getRocketSendThreadNum() {
            return rocketSendThreadNum;
        }

        public void setRocketSendThreadNum(int rocketSendThreadNum) {
            this.rocketSendThreadNum = rocketSendThreadNum;
        }

        public boolean isRocketAsyncFlag() {
            return rocketAsyncFlag;
        }

        public void setRocketAsyncFlag(boolean rocketAsyncFlag) {
            this.rocketAsyncFlag = rocketAsyncFlag;
        }

        public int getRocketBufferSize() {
            return rocketBufferSize;
        }

        public void setRocketBufferSize(int rocketBufferSize) {
            this.rocketBufferSize = rocketBufferSize;
        }

        public int getRocketMaxOverstock() {
            return rocketMaxOverstock;
        }

        public void setRocketMaxOverstock(int rocketMaxOverstock) {
            this.rocketMaxOverstock = rocketMaxOverstock;
        }

        public boolean isUseMonitor() {
            return useMonitor;
        }

        public void setUseMonitor(boolean useMonitor) {
            this.useMonitor = useMonitor;
        }

        public int getRocketSeed() {
            return rocketSeed;
        }

        public void setRocketSeed(int rocketSeed) {
            this.rocketSeed = rocketSeed;
        }
        
}