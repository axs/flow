/*
$Id$
*/

package com.prc.tt.cep.query;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.espertech.esper.client.*;
import com.espertech.esper.event.*;
import com.espertech.esperio.*;

import com.prc.tt.messaging.Quote;



public class KeyReversal extends Query {
    private final static Log log = LogFactory.getLog(KeyReversal.class);

    public void start() {
        //String vol =" select securityID, stddev(p.bid) as sdbid from PriceEvent.win:time(3 minutes) as p group by securityID output last every 5 seconds";
        String inq = "insert into Bar "+
                     "  select securityID, first(bid) as open, last(bid) as close, max(bid) as high, min(bid) as low "+
                     "  from PriceEvent.win:time(2 min)  group by securityID "+
                     "  output last every 2 min ";
        EPStatement istmt = zepService.getEPAdministrator().createEPL(inq);
        istmt.start();



        String keyrev = " select securityID,first(open) as fopen, first(high) as fhigh,first(low) as flow, first(close) as fclose   " +
                      " ,last(open) as lopen, last(high) as lhigh,last(low) as llow, last(close) as lclose   " +
                      " from Bar.std:groupwin(securityID).win:length(2)  " +
                      " group by securityID ";



        setStatement(keyrev);
        consumeData();
    }

}
