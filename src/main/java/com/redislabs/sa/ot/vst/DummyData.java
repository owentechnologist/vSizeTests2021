package com.redislabs.sa.ot.vst;

public class DummyData {

    public static int getRandomNumber(int scale){
        double randNum =  (Math.random()*scale);
        int aNum = (int) randNum; // 0 and scale
        return aNum;
    }

    public static char getRandomAlphaChar(){
        double randCharA =  (Math.random()*26);
        int numCharA = (int) randCharA; // 0 and 25
        numCharA += 65;
        char charA = (char)numCharA;
        return charA;
    }

    public static String getPhoneOS(){
        String phoneOS = "ANDROiD";
        if(System.nanoTime()%3==0){
            phoneOS="IPHONE";
        }
        return phoneOS;
    }

    public static String getCityCode() {
        String[] cities = new String[]{
                "LA", "NY", "CH", "DT", "AU", "DE"
        };
        long tidx = System.nanoTime()%(cities.length-1);
        int idx = ((int) tidx);
        return cities[idx];
    }

    public static String getNetworkProvider(){
        String[] providers = new String[]{
                "Xfinity","Verizon"
                ,"AT&T", "Spectrum"
                ,"Cox", "RCN"
                ,"CenturyLink","Frontier"
                ,"HughesNet","Mediacom"
                ,"Viasat", "Optimum"
        };
        long tidx = System.nanoTime()%(providers.length-1);
        int idx = ((int) tidx);
        return providers[idx];
    }

    public static String getRandomUserIDSeed(int scale){
        char c1 = getRandomAlphaChar();
        char c2 = getRandomAlphaChar();
        char c3 = getRandomAlphaChar();
        char c4 = getRandomAlphaChar();
        char c5 = getRandomAlphaChar();
        int aNum = getRandomNumber(scale);
        return ""+c1+c2+c3+c4+c5+aNum;
    }

    public static String getCommaSeparatedStringOfServiceIDs(int numberOfIds){
        String serviceIds = "";
        for(int x=0;x<(numberOfIds-1);x++){
            serviceIds+=getRandomServiceID(x+"")+",";
        }
        serviceIds+=getRandomServiceID(12+""); // last one doesn't need the comma
        return serviceIds;
    }

    public static String getRandomServiceID(String idVal){
        return ""+getRandomAlphaChar()+idVal;
    }

}
