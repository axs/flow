
package com.prc.tt.algo.oms;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;


import com.prc.tt.utils.AePlayWave;
import com.prc.tt.enums.ESide;
import com.prc.tt.Instrument;


public class Position {
    private static final Log log = LogFactory.getLog(Position.class);

    private static final ExecutorService exec =Executors.newSingleThreadExecutor();

    private final Instrument instrument;
    private double WkQty = 0;
    private double Qty = 0;

    public Position(Instrument i) {
        this.instrument = i;
    }


    public synchronized double getWkQty() {
        return WkQty;
    }


    public synchronized void setWkQty(double q) {
        WkQty =q ;
    }


    public synchronized double getQty() {
        return Qty;
    }


    public synchronized void setQty(double q) {
        Qty = q;
    }


    public synchronized void addFillQty(double qty,ESide side) {
        double oldqty = Qty;

        Qty += side == ESide.BUY ? qty: -1*qty;
        WkQty -=  qty;

        exec.execute( new AePlayWave(oldqty, Qty) );
    }

}
