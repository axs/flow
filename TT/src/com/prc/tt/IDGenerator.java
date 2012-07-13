



package com.prc.tt;



import java.util.concurrent.atomic.AtomicInteger;
import quickfix.SessionID;


public class IDGenerator{

    private static AtomicInteger genid = new AtomicInteger(101);
    private static Configuration cfg = com.prc.tt.Configuration.getInstance();

    public static int getAndIncrement() {
        return genid.getAndIncrement();
    }

    public static int getGenID() {
        return genid.get();
    }

    public static void setGenID(int val){
        genid.set(val);
    }


    public static String getID(SessionID aSessionID){
        String x= aSessionID + "-" + cfg.getSourceID() + "-" + System.currentTimeMillis() + "-" + getAndIncrement();
        return x;
    }

    public static String getID(){
        String x=  cfg.getSourceID() + "-" + System.currentTimeMillis() + "-" + getAndIncrement();
        return x;
    }

}
