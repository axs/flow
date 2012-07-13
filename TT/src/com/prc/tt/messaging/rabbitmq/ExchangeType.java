




package com.prc.tt.messaging.rabbitmq;


import java.util.Map;
import java.util.HashMap;
import java.util.Collections;



public enum ExchangeType {
     FANOUT("fanout")
    ,DIRECT("direct")
    ,TOPIC("topic") ;


    private static final Map<String, ExchangeType> mValueMap;
    private final String otype;

    ExchangeType(String ot) {
        this.otype=ot;
    }


    public String getValue() {
        return otype;
    }

    static ExchangeType getInstanceForValue(String inValue) {
        ExchangeType ot = mValueMap.get(inValue);
        return ot;
    }


    static {
        Map<String, ExchangeType> table = new HashMap<String,ExchangeType>();
        for( ExchangeType ot: values() ) {
            table.put(ot.getValue(), ot);
        }
        mValueMap = Collections.unmodifiableMap(table);
    }



}


