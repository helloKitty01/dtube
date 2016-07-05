/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ict.dtube.client.consumer;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ict.dtube.client.consumer.DefaultMQPullConsumer;
import com.ict.dtube.client.consumer.GetResult;
import com.ict.dtube.client.consumer.MessageQueueListener;
import com.ict.dtube.client.consumer.PullResult;
import com.ict.dtube.client.consumer.PullStatus;
import com.ict.dtube.client.exception.MQClientException;
import com.ict.dtube.common.message.Message;
import com.ict.dtube.common.message.MessageExt;
import com.ict.dtube.common.message.MessageQueue;
import com.ict.dtube.mqclient.GetMethod;
import com.ict.dtube.mqclient.IMQClient;
import com.ict.dtube.mqclient.MQInitException;
import com.ict.dtube.mqclient.support.MQConfig;
import com.ict.dtube.remoting.exception.RemotingTimeoutException;

/**
 *GetConsumer的接口实现类
 *@author sky
 */
public class DtubeGetConsumer implements IMQClient{

    
    private Logger logger = LoggerFactory.getLogger(DtubeGetConsumer.class);

    private MQConfig _conf;
    
    private final static String MQ_KEY_PROP="mqKey";
    
    //key topic value queue  消息缓冲区
    private final Map<String,BlockingQueue<MessageExt>> messageBuffer;
    
    private final DefaultMQPullConsumer consumer;
    
    //key: topic value:{key brokerName-queueId value messagequeue} topic与mq的映射信息
    private Map<String,Map<String,MessageQueue>> messageQueuesMap;
    
    private ConcurrentHashMap<MessageQueue,PullMessageThread> messageThreadMap;
    
    //是否关闭
    private volatile boolean stopFlag=false;
    
    private Timer timer=new Timer("mq_client_monitor",true);

	@Override
	public void start() throws MQInitException {
        //设置关闭状态为否
        stopFlag=false;

        //注册如果topic中MessageQueue发生改变的回调函数
        for(String topic:_conf.getRocketTopic()){
            consumer.registerMessageQueueListener(topic,new CustomerMessageQueueListener());
        }
        try {
            consumer.start();
            initMessageQueuesMap();
        } catch (MQClientException ex) {
           logger.debug("GetConsumer init error!", ex);
           throw new MQInitException(ex);
        }
        
        if(_conf.isUseMonitor()){
            timer.scheduleAtFixedRate(new TimerTask(){
                @Override
                public void run() {
                   for(Map.Entry<String,BlockingQueue<MessageExt>> entry:messageBuffer.entrySet()){
                       System.out.println("topic:"+entry.getKey()+"size:"+entry.getValue().size());
                   }
                }
                
            }, 1000, 1000);
        }    		
	}
    
    public DtubeGetConsumer(String groupName){
        _conf=new MQConfig();
        consumer=new DefaultMQPullConsumer(groupName);
        consumer.setInstanceName(_conf.getRocketInstanceName());
        consumer.setClientCallbackExecutorThreads(Runtime.getRuntime().availableProcessors()*2);
//        consumer.setRegisterTopics(topics);
        consumer.setMessageModel(_conf.getRocketConsumerMessageModel());
        messageBuffer=new HashMap<String,BlockingQueue<MessageExt>>();
        messageQueuesMap=new ConcurrentHashMap<String,Map<String,MessageQueue>>();
        messageThreadMap=new ConcurrentHashMap<MessageQueue,PullMessageThread>();
    }
    public void setInstanceName(String name) {
    	consumer.setInstanceName(name);
    }
    
    @Override
    public void subscribe(String topic){
    	_conf.registRocketTopicSet(topic,null);
    }
    public void setNamesrvAddr(String hosts){
    	consumer.setNamesrvAddr(hosts);
    }
    public String getNamesrvAddr(){
    	return consumer.getNamesrvAddr();
    }
    public Set<String> getRegisterTopics(){
    	return _conf.getRocketTopic();
    }
    public int getBufferSize(){
    	return _conf.getRocketBufferSize();
    }
    public void setBufferSize(int bufferSize){
    	_conf.setRocketBufferSize(bufferSize);
    }
    public boolean isUseMonitor() {
        return _conf.isUseMonitor();
    }

    public void setUseMonitor(boolean useMonitor) {
        _conf.setUseMonitor(useMonitor);
    }
    
    public void setTimeOut(long timeout){
    	consumer.setConsumerPullTimeoutMillis(timeout);
    }
    
    public long getTimeOut() {
    	return consumer.getConsumerPullTimeoutMillis();
    }
    
    /**
     * 初始化MessageQueuesMap
     * @throws MQClientException 
     */
    private void initMessageQueuesMap() throws MQClientException{
        Set<String> topics=_conf.getRocketTopic();
        for(String topic:topics){
            messageBuffer.put(topic, new ArrayBlockingQueue<MessageExt>(_conf.getRocketBufferSize()));
            Set<MessageQueue> mqs=consumer.fetchMessageQueuesInBalance(topic);
            reloadMqMap(topic,mqs);
        }
    }
    
    /**
     * 初始化拉取数据的线程
     */
    private void initPullTopic(){
       for(final String topic:_conf.getRocketTopic()){
           //遍历topic
            Map<String,MessageQueue> mqMap=messageQueuesMap.get(topic);
            for(Map.Entry<String,MessageQueue> e:mqMap.entrySet()){
                //遍历每个topic下的每个队列
                String mqKey=e.getKey();
                MessageQueue mq=e.getValue();
                if(messageThreadMap.get(mq)==null){
                    //每个队列建立一个线程来负责拉取
                    PullMessageThread t=new PullMessageThread(mq,"thread-"+topic+mqKey);
                    messageThreadMap.put(mq, t);
                    t.start();
                }
            }
       }
    }

    @Override
    public boolean close() {
        try{
            //设置为关闭
            stopFlag=true;
            consumer.shutdown();
            messageBuffer.clear();
            messageQueuesMap.clear();
            for(PullMessageThread t:messageThreadMap.values()){
                t.close();
            }
            messageThreadMap.clear();
            return true;
        }catch(Exception e){
            logger.error("PullConsumerAdapter close error");
        }
        return false;
    }
    public void shutdown() {
        try{
            //设置为关闭
            stopFlag=true;
            consumer.shutdown();
            messageBuffer.clear();
            messageQueuesMap.clear();
            for(PullMessageThread t:messageThreadMap.values()){
                t.close();
            }
            messageThreadMap.clear();
        }catch(Exception e){
            logger.error("PullConsumerAdapter close error");
        }
    }
    
    /**
     * 更新队列的offset
     * @param messExt 
     * @param topic
     */
    private boolean updateOffset(MessageExt messExt,String topic){
        //消息的flag字段再读取之后保存了对应queue在map中的key
        String mqKey=messExt.getUserProperty(MQ_KEY_PROP);
        MessageQueue mq=messageQueuesMap.get(topic).get(mqKey);
        if(mq!=null){
            try {
                consumer.updateConsumeOffset(mq, messExt.getQueueOffset()+1);
            } catch (MQClientException ex) {
                logger.error("PullConsumerAdapter updateOffset error!", ex);
            }
            return true;
        }
        return false;
    }
    /**
     * 更新队列的offset
     * @param messExt 
     * @param topic
     */
    private boolean updateOffset(List<MessageExt> messExts,String topic){
        //消息的flag字段再读取之后保存了对应queue在map中的key
    	for (MessageExt messageExt : messExts) {
			if(!updateOffset(messageExt, topic)){
				return false;
			}
		}
    	return true;
    }
    
    /**
     * 重新载入TOPIC下的，队列信息
     */
    private void reloadMqMap(final String topic,Set<MessageQueue> mqs){
        messageQueuesMap.remove(topic);
        Map<String,MessageQueue> mqMap=new ConcurrentHashMap<String,MessageQueue>();
        for(final MessageQueue mq:mqs){
               String mqKey=mq.getBrokerName()+"-"+mq.getQueueId(); //生成mqKey
               mqMap.put(mqKey, mq);
               PullMessageThread t=new PullMessageThread(mq,"thread-"+topic+mqKey);
               messageThreadMap.put(mq,t);
               t.start();
        }
        messageQueuesMap.put(topic,mqMap);
    }      
    public class CustomerMessageQueueListener implements MessageQueueListener {

            @Override
            public synchronized void messageQueueChanged(String topic, Set<MessageQueue > mqAll,Set<MessageQueue> mqDivided) {
            	logger.debug("reallocate message queues of topic " + topic + ": " + mqDivided.toString() + "\nall queues of this topic: " + mqAll.toString());
            	if(messageBuffer.containsKey(topic)){
                    //删除不存在的队列
                    Iterator<Map.Entry<MessageQueue, PullMessageThread>> it = messageThreadMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<MessageQueue, PullMessageThread> e = it.next();
                        MessageQueue mq = e.getKey();
                        if (topic.equals(mq.getTopic())&&!mqDivided.contains(mq)) {
                            //不属于分配的队列则删除
                            messageQueuesMap.get(topic).remove(mq.getBrokerName() + "-" + mq.getQueueId());
                            PullMessageThread t = e.getValue();
                            t.close();
                            it.remove();
                        }
                    }
                    //新增的队列添加进去
                    Iterator<MessageQueue> it2 = mqDivided.iterator();
                    while (it2.hasNext()) {
                        MessageQueue mq = it2.next();
                        if (!messageThreadMap.containsKey(mq)) {
                            //需要添加
                            messageQueuesMap.get(topic).put(mq.getBrokerName() + "-" + mq.getQueueId(), mq);
                            PullMessageThread pt = new PullMessageThread(mq, "thread-" + topic + mq.getBrokerName() + "-" + mq.getQueueId());
                            messageThreadMap.put(mq, pt);
                            pt.start();
                        }
                    }
                }
            }
    }
    public class PullMessageThread extends Thread {

        private MessageQueue mq;
        
        private volatile boolean runFlag=true;
        
        public PullMessageThread(MessageQueue mq,String name) {
            super(name);
            this.mq=mq;
        }
        
        public void close(){
            this.runFlag=false;
        }

        @Override
        public void run() {
            try {
                long offset = consumer.fetchConsumeOffset(mq, false);
                offset = offset < 0 ? 0 : offset;
                //判断是否超过最大的积压数，在初始化的时候设置offset
                if (_conf.getRocketMaxOverstock() > 0) {
                    //获取队列中最大的偏移量
                    long mqMaxOffset = consumer.maxOffset(mq);
                    if ((mqMaxOffset - offset) > _conf.getRocketMaxOverstock()) {
                        //队列挤压数量已经超过最大的上线
                        offset = mqMaxOffset;
                    }
                }
                while (!stopFlag&&runFlag) {   //如果没有关闭则进行循环
                    try {
                        PullResult result = consumer.pullBlockIfNotFound(mq, null, offset, _conf.getRocketMaxSize());
                        switch (result.getPullStatus()) {
                            case FOUND:
                                //拉取到数据
                                List<MessageExt> messagel = result.getMsgFoundList();
                                boolean addFlag = false;
                                while (!addFlag&&runFlag) {
                                    for (int i = 0; i < messagel.size();) {
                                        MessageExt msg = messagel.get(i);
                                        msg.putUserProperty(MQ_KEY_PROP, mq.getBrokerName()+"-"+mq.getQueueId());
//                                                        msg.setFlag(queueKeyId); //设置了在内存中保存的queue对应的key的id，等待消费之后再通过这个flag字段来更新对应队列的offset
                                        //                                                    if(messageBuffer.get(topic).size()<_conf.getRocketBufferSize()){
                                        try {
                                            messageBuffer.get(mq.getTopic()).put(msg);  //放入缓存区
                                            offset++; //移动偏移量
                                            i++;
                                            addFlag = true;
                                        } catch (Exception e) {
                                            logger.warn("cosume process error:",e);
                                        }
                                        //                                                    }
                                    }
                                }
                                break;
                            case NO_NEW_MSG:
                            case NO_MATCHED_MSG:
                            case OFFSET_ILLEGAL:
                                consumer.updateConsumeOffset(mq, result.getNextBeginOffset());
                                offset = result.getNextBeginOffset();
                                break;
                        }
                    } catch (RemotingTimeoutException e) {
                        logger.warn("thread:" + Thread.currentThread().getName() + " pull timeout!", e);
                    } catch (Exception e) {
                        logger.warn("thread:" + Thread.currentThread().getName() + " error!", e);
                    }
                }
            } catch (Exception ex) {
                logger.error("thread:" + Thread.currentThread().getName() + " pull error!", ex);
            }
        }
    }
	@Override
	public  GetResult get(String topic, int maxNum) {
		if(maxNum<1||topic==null){
			return new GetResult(PullStatus.ARGS_ILLEGAL, null);
		}
        if(messageBuffer.containsKey(topic)){
    		List<MessageExt> results=new ArrayList<MessageExt>();
            BlockingQueue<MessageExt> queue=messageBuffer.get(topic);
            int num=queue.drainTo(results, maxNum);
            if(num<1){
            	return new GetResult(PullStatus.NO_NEW_MSG, null);
            }
            if(updateOffset(results,topic)){
            	return new GetResult(PullStatus.FOUND, results);
            }
        }else{
        	return new GetResult(PullStatus.TOPIC_NOT_FOUND, null);
        }
        return new GetResult(PullStatus.UNKOWN_EXCEPTION, null);
    
	}
	@Override
	public  GetResult get(List topicList,int maxNum) {
		if(maxNum<1||topicList==null||topicList.size()<1){
			return new GetResult(PullStatus.ARGS_ILLEGAL, null);
		}
		int size=topicList.size();
		return get((String)topicList.get((int)(Math.random()*size)), maxNum);
	}
	@Override
	public GetResult get(String topic, int maxNum,GetMethod ifSync) {
		switch (ifSync) {
		case ASYNC:
			return get(topic, maxNum);
		case SYNC:
			if(maxNum<1||topic==null){
				return new GetResult(PullStatus.ARGS_ILLEGAL, null);
			}
	        if(messageBuffer.containsKey(topic)){
	        	List<MessageExt> results=new ArrayList<MessageExt>();
	            BlockingQueue<MessageExt> queue=messageBuffer.get(topic);
	            int num=queue.drainTo(results,maxNum);
	            /*
	             * 对num进行判断，如果num为0，表示当前缓冲区没有数据，需要阻塞等待。否则将返回缓冲区内数据（<=maxNum）
	             */
	            if(num==0){
	            	try {
						results.add(queue.take());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	            updateOffset(results,topic);
	            return new GetResult(PullStatus.FOUND, results);
	        }else{
	        	return new GetResult(PullStatus.TOPIC_NOT_FOUND, null);       	
	        }
		}
		return new GetResult(PullStatus.UNKOWN_EXCEPTION, null); 
	}
    @Override
    public GetResult get(String topic) {return get(topic,1); }

}


