

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



public class Volume extends Query {
    private final static Log log = LogFactory.getLog(Volume.class);

    public void start() {
        /*
        String inq = "insert into MyWin "+
                      "  select securityID, size, price "+
                      "  from TradeEvent.win:time(1 min)   ";
         EPStatement istmt = zepService.getEPAdministrator().createEPL(inq);
         istmt.start();
               */

        //XXX  this errors with unknow source after about 30 seconds
        String volume = " select sum(size) as volume, price  " +
            " from TradeEvent.win:time(30 sec)  group by price output all every 10 seconds";


        /*
        String volume = " select avg(price) as price  " +
            " from TradeEvent.win:time(30 sec)  group by securityID ";
        */

        setStatement(volume);
        consumeData();
    }

}
