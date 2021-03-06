package com.ict.dtube.tools.msgcheck;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.ict.dtube.store.ConsumeQueue;
import com.ict.dtube.store.MapedFile;
import com.ict.dtube.store.MapedFileQueue;
import com.ict.dtube.store.SelectMapedBufferResult;
import com.ict.dtube.store.config.StorePathConfigHelper;


/**
 * @auther lansheng.zj
 */
public class Store {

    // 每个消息对应的MAGIC CODE daa320a7
    public final static int MessageMagicCode = 0xAABBCCDD ^ 1880681586 + 8;
    // 文件末尾空洞对应的MAGIC CODE cbd43194
    private final static int BlankMagicCode = 0xBBCCDDEE ^ 1880681586 + 8;
    // 存储消息的队列
    private MapedFileQueue mapedFileQueue;
    // ConsumeQueue集合
    private ConcurrentHashMap<String/* topic */, ConcurrentHashMap<Integer/* queueId */, ConsumeQueue>> consumeQueueTable;

    private String cStorePath;
    private int cSize;
    private String lStorePath;
    private int lSize;


    public Store(String cStorePath, int cSize, String lStorePath, int lSize) {
        this.cStorePath = cStorePath;
        this.cSize = cSize;
        this.lStorePath = lStorePath;
        this.lSize = lSize;
        mapedFileQueue = new MapedFileQueue(cStorePath, cSize, null);
        consumeQueueTable =
                new ConcurrentHashMap<String/* topic */, ConcurrentHashMap<Integer/* queueId */, ConsumeQueue>>();
    }


    public boolean load() {
        boolean result = this.mapedFileQueue.load();
        System.out.println("load commit log " + (result ? "OK" : "Failed"));
        if (result) {
            result = loadConsumeQueue();
        }
        System.out.println("load logics log " + (result ? "OK" : "Failed"));
        return result;
    }


    private boolean loadConsumeQueue() {
        File dirLogic = new File(StorePathConfigHelper.getStorePathConsumeQueue(lStorePath));
        File[] fileTopicList = dirLogic.listFiles();
        if (fileTopicList != null) {
            // TOPIC 遍历
            for (File fileTopic : fileTopicList) {
                String topic = fileTopic.getName();
                // TOPIC 下队列遍历
                File[] fileQueueIdList = fileTopic.listFiles();
                if (fileQueueIdList != null) {
                    for (File fileQueueId : fileQueueIdList) {
                        int queueId = Integer.parseInt(fileQueueId.getName());
                        ConsumeQueue logic = new ConsumeQueue(//
                            topic,//
                            queueId,//
                            StorePathConfigHelper.getStorePathConsumeQueue(lStorePath),//
                            lSize,//
                            null);
                        this.putConsumeQueue(topic, queueId, logic);
                        if (!logic.load()) {
                            return false;
                        }
                    }
                }
            }
        }
        System.out.println("load logics queue all over, OK");
        return true;
    }


    private void putConsumeQueue(final String topic, final int queueId, final ConsumeQueue consumeQueue) {
        ConcurrentHashMap<Integer/* queueId */, ConsumeQueue> map = this.consumeQueueTable.get(topic);
        if (null == map) {
            map = new ConcurrentHashMap<Integer/* queueId */, ConsumeQueue>();
            map.put(queueId, consumeQueue);
            this.consumeQueueTable.put(topic, map);
        }
        else {
            map.put(queueId, consumeQueue);
        }
    }


    public ConsumeQueue findConsumeQueue(String topic, int queueId) {
        ConcurrentHashMap<Integer, ConsumeQueue> map = consumeQueueTable.get(topic);
        if (null == map) {
            ConcurrentHashMap<Integer, ConsumeQueue> newMap =
                    new ConcurrentHashMap<Integer, ConsumeQueue>(128);
            ConcurrentHashMap<Integer, ConsumeQueue> oldMap = consumeQueueTable.putIfAbsent(topic, newMap);
            if (oldMap != null) {
                map = oldMap;
            }
            else {
                map = newMap;
            }
        }
        ConsumeQueue logic = map.get(queueId);
        if (null == logic) {
            ConsumeQueue newLogic = new ConsumeQueue(//
                topic,//
                queueId,//
                StorePathConfigHelper.getStorePathConsumeQueue(lStorePath),//
                lSize,//
                null);
            ConsumeQueue oldLogic = map.putIfAbsent(queueId, newLogic);
            if (oldLogic != null) {
                logic = oldLogic;
            }
            else {
                logic = newLogic;
            }
        }
        return logic;
    }


    public void traval(boolean openAll) {
        boolean success = true;
        byte[] bytesContent = new byte[1024];
        List<MapedFile> mapedFiles = this.mapedFileQueue.getMapedFiles();
        ALL: for (MapedFile mapedFile : mapedFiles) {
            long startOffset = mapedFile.getFileFromOffset();
            int position = 0;
            int msgCount = 0;
            int errorCount = 0;

            System.out.println("start travel " + mapedFile.getFileName());
            long startTime = System.currentTimeMillis();
            ByteBuffer byteBuffer = mapedFile.sliceByteBuffer();
            while (byteBuffer.hasRemaining()) {
                // 1 TOTALSIZE
                int totalSize = byteBuffer.getInt();
                // 2 MAGICCODE
                int magicCode = byteBuffer.getInt();
                if (BlankMagicCode == magicCode) {
                    position = byteBuffer.limit();
                    break;
                }
                // 3 BODYCRC
                int bodyCRC = byteBuffer.getInt();

                // 4 QUEUEID
                int queueId = byteBuffer.getInt();

                // 5 FLAG
                int flag = byteBuffer.getInt();
                flag = flag + 0;

                // 6 QUEUEOFFSET
                long queueOffset = byteBuffer.getLong();

                // 7 PHYSICALOFFSET
                long physicOffset = byteBuffer.getLong();

                // 8 SYSFLAG
                int sysFlag = byteBuffer.getInt();

                // 9 BORNTIMESTAMP
                long bornTimeStamp = byteBuffer.getLong();
                bornTimeStamp = bornTimeStamp + 0;

                // 10 BORNHOST（IP+PORT）
                byteBuffer.position(byteBuffer.position() + 8);

                // 11 STORETIMESTAMP
                long storeTimestamp = byteBuffer.getLong();

                // 12 STOREHOST（IP+PORT）
                byteBuffer.position(byteBuffer.position() + 8);

                // 13 RECONSUMETIMES
                int reconsumeTimes = byteBuffer.getInt();

                // 14 Prepared Transaction Offset
                long preparedTransactionOffset = byteBuffer.getLong();

                // 15 BODY
                int bodyLen = byteBuffer.getInt();
                if (bodyLen > 0) {
                    byteBuffer.position(byteBuffer.position() + bodyLen);
                }

                // 16 TOPIC
                byte topicLen = byteBuffer.get();
                byteBuffer.get(bytesContent, 0, topicLen);
                String topic = new String(bytesContent, 0, topicLen);

                Date storeTime = new Date(storeTimestamp);

                // 计算出来当前消息的偏移量
                long currentPhyOffset = startOffset + position;
                if (physicOffset != currentPhyOffset) {
                    System.out.println(storeTime
                            + " [fetal error] physicOffset != currentPhyOffset. position=" + position
                            + ", msgCount=" + msgCount + ", physicOffset=" + physicOffset
                            + ", currentPhyOffset=" + currentPhyOffset);
                    errorCount++;
                    if (!openAll) {
                        success = false;
                        break ALL;
                    }
                }

                ConsumeQueue consumeQueue = findConsumeQueue(topic, queueId);
                SelectMapedBufferResult smb = consumeQueue.getIndexBuffer(queueOffset);
                try {
                    long offsetPy = smb.getByteBuffer().getLong();
                    int sizePy = smb.getByteBuffer().getInt();
                    if (physicOffset != offsetPy) {
                        System.out.println(storeTime + " [fetal error] physicOffset != offsetPy. position="
                                + position + ", msgCount=" + msgCount + ", physicOffset=" + physicOffset
                                + ", offsetPy=" + offsetPy);
                        errorCount++;
                        if (!openAll) {
                            success = false;
                            break ALL;
                        }
                    }
                    if (totalSize != sizePy) {
                        System.out.println(storeTime + " [fetal error] totalSize != sizePy. position="
                                + position + ", msgCount=" + msgCount + ", totalSize=" + totalSize
                                + ", sizePy=" + sizePy);
                        errorCount++;
                        if (!openAll) {
                            success = false;
                            break ALL;
                        }
                    }
                }
                finally {
                    smb.release();
                }

                msgCount++;
                position += totalSize;
                byteBuffer.position(position);
            }

            System.out.println("end travel " + mapedFile.getFileName() + ", total msg=" + msgCount
                    + ", error count=" + errorCount + ", cost:" + (System.currentTimeMillis() - startTime));
        }

        System.out.println("travel " + (success ? "ok" : "fail"));
    }
}
