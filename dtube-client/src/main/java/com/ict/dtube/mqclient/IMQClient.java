package com.ict.dtube.mqclient;

import java.util.List;

import com.ict.dtube.client.consumer.GetResult;

/**
 * GetConsumer的公共接口类
 * @author sky
 */
public interface IMQClient {
	
	/**
	 * 初始化GetConsumer
	 * @throws MQInitException
	 */
	void start() throws MQInitException;

	/**
	 * 订阅topic
	 * @param topic
	 */
	void subscribe(String topic);
	
	/**
	 * 从topic中Get消息 ,默认非阻塞式，返回数据小于等于1.
	 * @param topic 
	 * @return GetResult
	 */
	GetResult get(String topic);

	/**
	 * 从topic中Get消息 ,默认非阻塞式，返回数据小于等于maxNum.
	 * @param topic 消息所属topic
	 * @param maxNum 消息个数最大值
	 * @return GetResult
	 */
	GetResult get(String topic,int maxNum);

	/**
	 * 从topicList中获取消息
	 * @param   topicList 消息所属的topic列表
	 * @param   maxNum 消息个数最大值
	 * @return  返回GetResult
	 */
	GetResult get(List topicList,int maxNum);
	/**
	 * 以阻塞的方式从topic中获取消息，在没有消息时或者消息个数没达到maxNum时会阻塞。
	 * @param   topic 消息所属的topic
	 * @param   maxNum 消息个数
	 * @return  返回GetResult
	 */
	GetResult get(String topic,int maxNum,GetMethod ifSync);

	/**
	 * 关闭GetConsumer
	 * @return 返回是否关闭 true 已经关闭 ；false 不能关闭
	 */
	boolean close();		
}
