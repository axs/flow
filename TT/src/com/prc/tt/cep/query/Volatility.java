/*

$Id$
*/



package com.prc.tt.cep.query;


import com.espertech.esper.client.*;
import com.espertech.esper.event.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.espertech.esperio.*;



public class Volatility extends Query  {
    private final static Log log = LogFactory.getLog(Volatility.class);
    double threshold = 1.0;
    private String winSize="30 min";


    public void start(){
        log.info("start called");

        String vol =" select securityID, stddev(p.bid) as sdbid from PriceEvent.win:time(3 minutes) as p group by securityID output last every 5 seconds";
        /*
        String vol =" select first(p.mid)  as prevPrice,stddev(p.mid) as sd, "   +
                    " stddev(tick.mid) as bigsd, p.mid as price,p.symbol as symbol " +
                    " , max(p.mid) as maxprice, min(p.mid) as minprice, p.ID as uid " +
                    " from PriceEvent.std:groupwin(symbol).win:time(60 sec) as p  "  +
                    "     ,PriceEvent.std:groupwin(symbol).win:time(5 min) as tick "  +
                    " where p.ID = tick.ID  "  +
                    " group by p.symbol "  +
                    " having stddev(p.mid) > stddev(tick.mid) * 1.1     ";
        */


        setStatement(vol);
        consumeData();
    }


    public void setWinSize(String winSize){
        this.winSize = winSize;
    }

    public void setThreshold( double t){
         this.threshold = t;
     }



}
