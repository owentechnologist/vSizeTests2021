package com.redislabs.sa.ot.vst;

import com.redislabs.sa.ot.util.JedisConnectionFactory;
import com.redislabs.sa.ot.util.PropertyFileFetcher;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Main {

    public static void main(String args[]){
        loadData(100000,1000);
    }

    static void loadData(int howMany,int batchSize){
        long startTime = 0l;
        long endTime = 0l;
        System.out.println("now: writing in batches of "+batchSize);
        startTime = System.currentTimeMillis();
        batchLoadRecords(howMany,batchSize);
        endTime = System.currentTimeMillis();
        System.out.println("Loading "+howMany+" hashes in batch sizes of "+batchSize+" took "+(endTime-startTime)/1000+" seconds");
    }

    static void batchLoadRecords(int numHashes,int batchSize) {
        int batches = numHashes / batchSize;
        int counter = 0;
        Jedis batchJedis = null;
        try {
            batchJedis = JedisConnectionFactory.getInstance().getJedisPool().getResource();
            System.out.println("\t");
            for (int x = 0; x < batches; x++) {
                counter++;
                Transaction trans = batchJedis.multi();
                for (int y = 0; y < batchSize; y++) {
                    addRecord(trans);
                }
                trans.exec();
                if (counter % 10 == 0) {
                    System.out.print(". ");
                }
                if (counter % 100 == 0) {
                    System.out.println();
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            batchJedis.close();
        }
    }

    static void addRecord(Transaction tx){
        String hashKey = createKey();
        Map<String,String> fields = getFields(hashKey);
        tx.hset(hashKey, fields);
        tx.expire(hashKey,(86400*2));
    }

    //Day:Device-ID:App-ID
    //Day, indicates day of month, e.g., today is 2021/3/10, the value of day 10
    //Device-ID, device unique ID, e.g., c88ba7b8-95cf-4c90-8b14-583b3e60db29
    //App-ID, app unique ID, e.g., 56cb247c4c275f425b000054
    //example of key 10:c88ba7b8-95cf-4c90-8b14-583b3e60db29:56cb247c4c275f425b000054
    static String createKey(){
        int day = (DummyData.getRandomNumber(30)+130)%30;
        day = day+1; //no zero days
        String deviceID=DummyData.getRandomUserIDSeed(3)+"-"
                +DummyData.getRandomAlphaChar()+"12a-"
                +DummyData.getRandomAlphaChar()+"8Wq-"
                +DummyData.getRandomAlphaChar()+"u6b-"
                +DummyData.getRandomUserIDSeed(6);
        String appID = DummyData.getRandomUserIDSeed(12)
                +DummyData.getRandomAlphaChar()+"1a"
                +DummyData.getRandomAlphaChar()+"q"
                +DummyData.getRandomAlphaChar()+"ub"
                +DummyData.getRandomUserIDSeed(3);
        String key = day+":"+deviceID+":"+appID;
        return key;
    }

    // #a = sum of the other fields
    //10:c88ba7b8-95cf-4c90-8b14-583b3e60db29:56cb247c4c275f425b000054
    //               |- #a: 12
    //               |- ONET3D_ANDROID_REWARD_LA_BIDDING-4910186: 10
    //               |- ONET3D_ANDROID_REWARD_LA_BIDDING-4910187: 1
    //               |- ONET3D_ANDROID_REWARD_LA_BIDDING-4910188: 1
    static Map<String,String> getFields(String parentKey){
        HashMap<String,String> map = new HashMap<>();
        long numFields = (System.currentTimeMillis()%20)+8;
        if(System.nanoTime()%40==0){
            numFields = numFields*(3+System.nanoTime()%3);
            if(numFields>124) {
                System.out.println("\n" + parentKey + " has " + numFields + " fields");
            }
        }
        String phoneOS = DummyData.getPhoneOS();
        for(int x=0;x < numFields;x++){
            String field = DummyData.getNetworkProvider()+"_"+
                    phoneOS+"_"+
                    "REWARD_"+DummyData.getCityCode()+"_"+
                    "BIDDING-"+DummyData.getRandomUserIDSeed(2);
            map.put(field,""+DummyData.getRandomNumber(10));
        }
        int total = 0;
        for(String v :(map.values()) ){
            int t = Integer.parseInt(v);
            total+=t;
        }
        map.put("#a",""+total);
        return map;
    }

}
