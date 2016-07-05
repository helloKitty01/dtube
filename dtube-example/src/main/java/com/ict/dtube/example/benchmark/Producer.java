package com.ict.dtube.example.benchmark;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import com.ict.dtube.client.exception.MQBrokerException;
import com.ict.dtube.client.exception.MQClientException;
import com.ict.dtube.client.producer.DtubeProducer;
import com.ict.dtube.common.message.Message;
import com.ict.dtube.remoting.exception.RemotingException;


/**
 * @author sky
 * 性能测试，多线程同步发送消息
 */
public class Producer {
    public static void main(String[] args) throws MQClientException {
        final int threadCount = args.length >= 1 ? Integer.parseInt(args[0]) : 32;
        final int messageSize = args.length >= 2 ? Integer.parseInt(args[1]) : 256;
        final String topic = args.length >= 3 ? args[2] : "BenchmarkTest";
        final String namesrv=args.length >=4 ? args[3] : null;

        System.out.printf("threadCount %d messageSize %d topic %s\n", threadCount, messageSize,topic);

        final Message msg = buildMessage(messageSize,topic);

        final ExecutorService sendThreadPool = Executors.newFixedThreadPool(threadCount);

        final StatsBenchmarkProducer statsBenchmark = new StatsBenchmarkProducer();

        final Timer timer = new Timer("BenchmarkTimerThread", true);

        final LinkedList<Long[]> snapshotList = new LinkedList<Long[]>();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                snapshotList.addLast(statsBenchmark.createSnapshot());
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

                    final long sendTps =
                            (long) (((end[3] - begin[3]) / (double) (end[0] - begin[0])) * 1000L);
                    final double averageRT = ((end[5] - begin[5]) / (double) (end[3] - begin[3]));

                    System.out.printf(
                        "Send TPS: %d Max RT: %d Average RT: %7.3f Send Failed: %d Response Failed: %d Send(Total): %d\n"//
                        , sendTps//
                        , statsBenchmark.getSendMessageMaxRT().get()//
                        , averageRT//
                        , end[2]//
                        , end[4]//
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

        final DtubeProducer producer = new DtubeProducer("benchmark_producer");
        producer.setInstanceName(Long.toString(System.currentTimeMillis()));

        producer.setCompressMsgBodyOverHowmuch(Integer.MAX_VALUE);
        
        if(namesrv!=null){
        	producer.setNamesrvAddr(namesrv);
        }

        producer.start();

        for (int i = 0; i < threadCount; i++) {
            sendThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            final long beginTimestamp = System.currentTimeMillis();
                            producer.send(msg);
                            statsBenchmark.getSendRequestSuccessCount().incrementAndGet();
                            statsBenchmark.getReceiveResponseSuccessCount().incrementAndGet();
                            final long currentRT = System.currentTimeMillis() - beginTimestamp;
                            statsBenchmark.getSendMessageSuccessTimeTotal().addAndGet(currentRT);
                            long prevMaxRT = statsBenchmark.getSendMessageMaxRT().get();
                            while (currentRT > prevMaxRT) {
                                boolean updated =
                                        statsBenchmark.getSendMessageMaxRT().compareAndSet(prevMaxRT,
                                            currentRT);
                                if (updated)
                                    break;

                                prevMaxRT = statsBenchmark.getSendMessageMaxRT().get();
                            }
                        }
                        catch (RemotingException e) {
                            statsBenchmark.getSendRequestFailedCount().incrementAndGet();
                            e.printStackTrace();
                        }
                        catch (InterruptedException e) {
                            statsBenchmark.getSendRequestFailedCount().incrementAndGet();
                            e.printStackTrace();
                        }
                        catch (MQClientException e) {
                            statsBenchmark.getSendRequestFailedCount().incrementAndGet();
                            e.printStackTrace();
                        }
                        catch (MQBrokerException e) {
                            statsBenchmark.getReceiveResponseFailedCount().incrementAndGet();
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }


    private static Message buildMessage(final int messageSize,String topic) {
        Message msg = new Message();
        msg.setTopic(topic);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < messageSize; i += 10) {
            sb.append("hello baby");
        }

        msg.setBody(sb.toString().getBytes());

        return msg;
    }
}


class StatsBenchmarkProducer {
    // 1
    private final AtomicLong sendRequestSuccessCount = new AtomicLong(0L);
    // 2
    private final AtomicLong sendRequestFailedCount = new AtomicLong(0L);
    // 3
    private final AtomicLong receiveResponseSuccessCount = new AtomicLong(0L);
    // 4
    private final AtomicLong receiveResponseFailedCount = new AtomicLong(0L);
    // 5
    private final AtomicLong sendMessageSuccessTimeTotal = new AtomicLong(0L);
    // 6
    private final AtomicLong sendMessageMaxRT = new AtomicLong(0L);


    public Long[] createSnapshot() {
        Long[] snap = new Long[] {//
                System.currentTimeMillis(),//
                        this.sendRequestSuccessCount.get(),//
                        this.sendRequestFailedCount.get(),//
                        this.receiveResponseSuccessCount.get(),//
                        this.receiveResponseFailedCount.get(),//
                        this.sendMessageSuccessTimeTotal.get(), //
                };

        return snap;
    }


    public AtomicLong getSendRequestSuccessCount() {
        return sendRequestSuccessCount;
    }


    public AtomicLong getSendRequestFailedCount() {
        return sendRequestFailedCount;
    }


    public AtomicLong getReceiveResponseSuccessCount() {
        return receiveResponseSuccessCount;
    }


    public AtomicLong getReceiveResponseFailedCount() {
        return receiveResponseFailedCount;
    }


    public AtomicLong getSendMessageSuccessTimeTotal() {
        return sendMessageSuccessTimeTotal;
    }


    public AtomicLong getSendMessageMaxRT() {
        return sendMessageMaxRT;
    }
}
