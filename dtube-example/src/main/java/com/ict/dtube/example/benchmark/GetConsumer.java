package com.ict.dtube.example.benchmark;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import com.ict.dtube.client.consumer.DtubeGetConsumer;
import com.ict.dtube.client.consumer.GetResult;
import com.ict.dtube.common.message.MessageExt;
import com.ict.dtube.mqclient.GetMethod;


/**
 * @author sky
 * 性能测试，get消息
 */
public class GetConsumer {
	public static List<String> topicList;
	
	public static void main(String[] args) {
        final int flag = args.length >= 1 ? Integer.parseInt(args[0]) : 1;/*1表示get（topic） 2表示get（topic，num） 3表示get（topic，num，sync），4表示get（topicList，num）*/
        final int maxNum = args.length >= 2 ? Integer.parseInt(args[1]) : 32;/*maxNum,默认为32*/
        final String topic = args.length >= 3 ? args[2] : "BenchmarkTest";
        final String namesrv=args.length >=4 ? args[3] : null;
        
        System.out.printf("flag %d maxNum %d topic %s\n", flag, maxNum,topic);
		final GetStatsBenchmarkConsumer statsBenchmarkConsumer = new GetStatsBenchmarkConsumer();
		final Timer timer = new Timer("BenchmarkTimerThread", true);
		final LinkedList<Long[]> snapshotList = new LinkedList<Long[]>();
	    timer.scheduleAtFixedRate(new TimerTask() {
	        @Override
	        public void run() {
	            snapshotList.addLast(statsBenchmarkConsumer.createSnapshot());
	            if (snapshotList.size() > 10) {
	                snapshotList.removeFirst();
	            }
	        }
	    }, 1000, 1000);
        timer.scheduleAtFixedRate(new TimerTask() {
            private void printStats() {
                if (snapshotList.size() >= 10) {
                    Long[] begin = snapshotList.getFirst();
                    Long[] end = snapshotList.getLast();

                    final long consumeTps =
                            (long) (((end[1] - begin[1]) / (double) (end[0] - begin[0])) * 1000L);
                    final double averageB2CRT = ((end[2] - begin[2]) / (double) (end[1] - begin[1]));
                    final double averageS2CRT = ((end[3] - begin[3]) / (double) (end[1] - begin[1]));

                    System.out.printf(
                        "Consume TPS: %d Average(B2C) RT: %7.3f Average(S2C) RT: %7.3f MAX(B2C) RT: %d MAX(S2C) RT: %d Receive(Total): %d\n"//
                        , consumeTps//
                        , averageB2CRT//
                        , averageS2CRT//
                        , end[4]//
                        , end[5]//
                        , end[1]
                        );
                }
            }


            @Override
            public void run() {
                try {
                    this.printStats();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 10000, 10000);

		
		DtubeGetConsumer consumer=new DtubeGetConsumer("benchmark_getconsumer");
		if(namesrv!=null){
			consumer.setNamesrvAddr(namesrv);
		}
		consumer.subscribe(topic);
		topicList=new ArrayList<String>();
		topicList.add(topic);
		consumer.start();
		GetResult getResult=null;
		while(true){
			if(flag==4){
				getResult=consumer.get(topicList, maxNum);
				onSucess(getResult, statsBenchmarkConsumer);
			}else{
				switch (flag) {
				case 1:
					getResult=consumer.get(topic);
					onSucess(getResult, statsBenchmarkConsumer);
					break;
				case 2:
					getResult=consumer.get(topic,maxNum);
					onSucess(getResult, statsBenchmarkConsumer);
					break;
				case 3:
					getResult=consumer.get(topic,maxNum,GetMethod.SYNC);
					onSucess(getResult, statsBenchmarkConsumer);
					break;
				default:
					getResult=consumer.get(topic);
					onSucess(getResult, statsBenchmarkConsumer);
					break;
				}
			
			}
		}
	}
    public static void compareAndSetMax(final AtomicLong target, final long value) {
        long prev = target.get();
        while (value > prev) {
            boolean updated = target.compareAndSet(prev, value);
            if (updated)
                break;

            prev = target.get();
        }
    }
    private static void onSucess(GetResult getResult,GetStatsBenchmarkConsumer statsBenchmarkConsumer) {
		switch (getResult.getGetStatus()) {
		case FOUND:
			List<MessageExt> messageList=getResult.getMessageList();
			long now = System.currentTimeMillis();
			for (int i = 0; i < messageList.size(); i++) {
				statsBenchmarkConsumer.getReceiveMessageTotalCount().incrementAndGet();
                long born2ConsumerRT = now - messageList.get(i).getBornTimestamp();
                statsBenchmarkConsumer.getBorn2ConsumerTotalRT().addAndGet(born2ConsumerRT);
                long store2ConsumerRT = now - messageList.get(i).getStoreTimestamp();
                statsBenchmarkConsumer.getStore2ConsumerTotalRT().addAndGet(store2ConsumerRT);
                compareAndSetMax(statsBenchmarkConsumer.getBorn2ConsumerMaxRT(), born2ConsumerRT);
                compareAndSetMax(statsBenchmarkConsumer.getStore2ConsumerMaxRT(), store2ConsumerRT);
			}
			
			break;
		default:
			break;
		}
    }

}
class GetStatsBenchmarkConsumer {
    // 1
    private final AtomicLong receiveMessageTotalCount = new AtomicLong(0L);
    // 2
    private final AtomicLong born2ConsumerTotalRT = new AtomicLong(0L);
    // 3
    private final AtomicLong store2ConsumerTotalRT = new AtomicLong(0L);
    // 4
    private final AtomicLong born2ConsumerMaxRT = new AtomicLong(0L);
    // 5
    private final AtomicLong store2ConsumerMaxRT = new AtomicLong(0L);


    public Long[] createSnapshot() {
        Long[] snap = new Long[] {//
                System.currentTimeMillis(),//
                        this.receiveMessageTotalCount.get(),//
                        this.born2ConsumerTotalRT.get(),//
                        this.store2ConsumerTotalRT.get(),//
                        this.born2ConsumerMaxRT.get(),//
                        this.store2ConsumerMaxRT.get(), //
                };

        return snap;
    }


    public AtomicLong getReceiveMessageTotalCount() {
        return receiveMessageTotalCount;
    }


    public AtomicLong getBorn2ConsumerTotalRT() {
        return born2ConsumerTotalRT;
    }


    public AtomicLong getStore2ConsumerTotalRT() {
        return store2ConsumerTotalRT;
    }


    public AtomicLong getBorn2ConsumerMaxRT() {
        return born2ConsumerMaxRT;
    }


    public AtomicLong getStore2ConsumerMaxRT() {
        return store2ConsumerMaxRT;
    }
}
