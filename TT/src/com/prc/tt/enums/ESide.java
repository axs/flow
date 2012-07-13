


/*
  $Id$
*/

package com.prc.tt.enums;


import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import quickfix.field.Side;


public enum ESide {
    BUY(quickfix.field.Side.BUY)
    ,SELL(quickfix.field.Side.SELL);

    private static final Map<Character, ESide> mValueMap;
    private final Character side;

    ESide(Character s) {
        this.side=s;
    }


    public Character getValue() {
        return side;
    }


    public ESide getFlip(){
           return getValue() == quickfix.field.Side.BUY ? ESide.SELL : ESide.BUY;
       }


    static ESide getInstanceForValue(Character inValue) {
        ESide ot = mValueMap.get(inValue);
        return ot;
    }


    static {
        Map<Character, ESide> table = new HashMap<Character,ESide>();
        for( ESide ot: values() ) {
            table.put(ot.getValue(), ot);
        }
        mValueMap = Collections.unmodifiableMap(table);
    }

}
