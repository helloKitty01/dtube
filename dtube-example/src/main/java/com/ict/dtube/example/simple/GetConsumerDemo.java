package com.ict.dtube.example.simple;


import java.util.List;

import com.ict.dtube.client.consumer.DtubeGetConsumer;
import com.ict.dtube.client.consumer.GetResult;
import com.ict.dtube.common.message.MessageExt;
import com.ict.dtube.mqclient.GetMethod;


public class GetConsumerDemo {

	public static void main(String[] args) {
		DtubeGetConsumer Consumer=new DtubeGetConsumer("Default_consumer_group");
		Consumer.subscribe("topic1");
		Consumer.start();
		while(true){
			GetResult result=Consumer.get("topic1", 57,GetMethod.SYNC);
			switch (result.getGetStatus()) {
			case FOUND:
				List<MessageExt> messageLists=result.getMessageList();
				for (MessageExt messageExt : messageLists) {
//					System.out.println(messageExt);
				}
				break;
			case NO_NEW_MSG:
				
				break;
			case TOPIC_NOT_FOUND:
				
				break;
			case ARGS_ILLEGAL:
				
				break;

			default:
				break;
			}
		}
/*
 * getConsumer.get("TopicTest1")接口测试
*/		
/*		while(true){
			GetResult result=getConsumer.get("TopicTest1");
			if(result.getMessageList()!=null){
				System.out.println(result);
				System.out.println(result.getMessageList().size());
			}
		}*/		
		
/*
 * getConsumer.get(topicList, 500)接口测试
 *
*/
/*  		while(true){
			List<String> topicList=new ArrayList<String>();
			topicList.add("TopicTest1");
			topicList.add("TopicTest5");
			GetResult result=getConsumer.get(topicList, 500);
			if(result.getMessageList()!=null){
				i=i+result.getMessageList().size();
				System.out.println(i);
				System.out.println(result.getMessageList().size());
			}else{
			}
		}*/

/*
 * getConsumer.getWithBlock("TopicTest1", 500)接口测试
 * 
*/
/*		
		GetResult result=getConsumer.getWithBlock("TopicTest_bb", 1);
		System.out.println(result.getMessageList().get(0));*/
/* 		while(true){
			
			if(result.getMessageList()!=null){
				System.out.println(result.getMessageList().size());
			}
		}*/
/*
 * getConsumer.get("TopicTest1", 500)接口测试
 *
*/
/*  while(true){
			GetResult result=getConsumer.get("TopicTest1", 500);
			if(result.getMessageList()!=null){
				i=i+result.getMessageList().size();
				System.out.println(i);
			}
		}*/
	}

}
