

package com.prc.tt.utils.fix;

public class CurrentFIXDataDictionary {
    public static FIXDataDictionary getCurrentFIXDataDictionary() {
        if(sCurrent == null ){
            CurrentFIXDataDictionary.initCurrentFIXDataDictionary();
        }
        return sCurrent;
    }

    public static void setCurrentFIXDataDictionary(FIXDataDictionary inFDD) {
        sCurrent = inFDD;
    }

    public static void initCurrentFIXDataDictionary() {
        try{
        sCurrent = FIXDataDictionary.initializeDataDictionary("C:/work/TT/conf/fix/TTFIX42.xml");
                }
        catch( Exception errr){
            errr.printStackTrace();
        }
    }


    private volatile static FIXDataDictionary sCurrent;
    private static final String LOGGER_NAME = CurrentFIXDataDictionary.class.getName();
}

