/*
  $Id$
*/


/*
this keeps track of the orders that each client sends to the main system
so we can send back ex3ecution to the correct client
*/
package com.prc.tt.messaging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ConcurrentHashMap;


public class OrderRoutes{
    private static final Log log = LogFactory.getLog(OrderRoutes.class);

    private static final OrderRoutes INSTANCE  = new OrderRoutes();
    private ConcurrentHashMap<String, String> order2client;


    public static OrderRoutes getInstance() {
        return INSTANCE;
    }

    private OrderRoutes(){
        order2client = new ConcurrentHashMap<String, String>();
    }

    public void add(Order order){
        log.info("adding order to om from " + order.getSourceID() );
         order2client.put(order.getClOrdID(),order.getSourceID());
    }


    //get the client who sent it
    public String getSourceID(String clordid){
        if (order2client.containsKey(clordid)) {
            return order2client.get(clordid);
        }
        return null;
    }


    //get the client who sent it
    public String remove(String clordid){
        log.info("removing order  " + clordid );
        return order2client.remove(clordid);
    }

}
