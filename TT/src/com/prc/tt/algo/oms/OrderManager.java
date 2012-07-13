
/*
  $Id$
*/

package com.prc.tt.algo.oms;


import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import quickfix.field.OrdStatus;
import quickfix.field.Side;
import quickfix.field.MsgType;
import quickfix.field.ExecType;

import com.prc.tt.enums.ESide;
import com.prc.tt.messaging.Order;
import com.prc.tt.messaging.Execution;
import com.prc.tt.messaging.MessageListener;
import com.prc.tt.messaging.MessageType;
import com.prc.tt.Instrument;
import com.prc.tt.Configuration;


public class OrderManager implements MessageListener<Execution> {

    private static final Log log = LogFactory.getLog(OrderManager.class);
    private static final Configuration cfg = Configuration.getInstance();
    private static final OrderManager INSTANCE  = new OrderManager();

    //clordid->newordersingle
    private ConcurrentHashMap<String,Order> orders;
    private ConcurrentHashMap<Instrument,Position> instrument2position;

    private OrderManager() {
        this.orders= new ConcurrentHashMap<String,Order>();
        this.instrument2position= new ConcurrentHashMap<Instrument,Position>();

        ArrayList<Instrument>  instruments = cfg.getInstruments();
        for ( Instrument i : instruments ) {
            log.info("inititializing intrument2position lookup "+ i.getSecurityID().getValue() );
            instrument2position.put(i,new Position(i));
        }
    }


    public static OrderManager getInstance() {
        return INSTANCE;
    }


    /*
    public Set<Map.Entry<String, Order>> getOrderEntrySet() {
        return orders.entrySet();
    }
    */

    public Order getOrder(String clorderId) {
        if ( orders.containsKey(clorderId) ) {
            return orders.get(clorderId);
        }
        return null;
    }

    public boolean isWorking(String clorderId) {
        return orders.containsKey(clorderId);
    }


    public void add(Order order) {
        try {
            log.info("adding order " + order.getClOrdID() );
            orders.put(order.getClOrdID() ,order);
        }
        catch ( Exception err ) {
            log.info(err.getStackTrace());
        }
    }


    public void remove(String orderid) {
        try {
            if ( orders.containsKey( orderid ) ) {
                log.info("removing order " + orderid );
                orders.remove( orderid );
            }
        }
        catch ( Exception err ) {
            log.info("ERROR trying to remove orderid => " +orderid);
            log.info(err.getStackTrace());
        }
    }


    public final ArrayList<Order> getBidOrders( String securityid ) {
        ArrayList<Order> bids= new ArrayList<Order>();
        for ( String oid : orders.keySet() ) {
            if ( orders.get(oid).getSecurityID().equals( securityid ) ) {
                Order o = orders.get(oid);
                if ( o.getSide() == quickfix.field.Side.BUY &&
                     o.getMsgType() != MsgType.ORDER_CANCEL_REQUEST &&
                     o.getOrdStatus() == OrdStatus.NEW ){
                    bids.add(o);
                }
            }
        }
        return bids;
    }


    public final ArrayList<Order> getOfferOrders( String securityid ) {
        ArrayList<Order> offers= new ArrayList<Order>();
        for ( String oid : orders.keySet() ) {
            if ( orders.get(oid).getSecurityID().equals( securityid ) ) {
                Order o = orders.get(oid);
                if ( o.getSide() == quickfix.field.Side.SELL &&
                     o.getMsgType() != MsgType.ORDER_CANCEL_REQUEST &&
                     o.getOrdStatus() == OrdStatus.NEW  ) {
                    offers.add(o);
                }
            }
        }
        return offers;
    }


    public final ArrayList<String> getBids( String securityid ) {
        ArrayList<String> bids= new ArrayList<String>();
        for ( String oid : orders.keySet() ) {
            if ( orders.get(oid).getSecurityID().equals( securityid ) ) {
                if ( orders.get(oid).getSide() == quickfix.field.Side.BUY &&
                     orders.get(oid).getMsgType() != MsgType.ORDER_CANCEL_REQUEST &&
                     orders.get(oid).getOrdStatus() == OrdStatus.NEW  ){
                    bids.add(oid);
                }
            }
        }
        return bids;
    }


    public final ArrayList<String> getOffers( String securityid ) {
        ArrayList<String> offers= new ArrayList<String>();
        for ( String oid : orders.keySet() ) {
            if ( orders.get(oid).getSecurityID().equals( securityid ) ) {
                if ( orders.get(oid).getSide() == quickfix.field.Side.SELL &&
                     orders.get(oid).getMsgType() != MsgType.ORDER_CANCEL_REQUEST &&
                     orders.get(oid).getOrdStatus() == OrdStatus.NEW   ){
                    offers.add(oid);
                }
            }
        }
        return offers;
    }


    public final Set<String> getClOrdIDs() {
        return orders.keySet();
    }
    public final ArrayList<Order> getOrders() {
        return new ArrayList<Order>(orders.values());
    }



    public void onMessage( MessageType t, Execution message ) {
        log.info("onMessage Called " );
        handleExecution(message);

        /*
        if( orders.containsKey(message.getClOrdID() ) ) {
            Order o = orders.get( message.getClOrdID()  );
            log.info(o.m_totalQuantity + " ~ " + message.m_shares );
            o.m_totalQuantity -= message.m_shares;


            Contract c = getContract( message.m_orderId );
            if( contract2position.containsKey(c.m_conId) ){
                Position position = getPosition( c.m_conId );

                position.addFillQty(
                                    message.m_shares
                                   ,message.m_side.equals("BOT") ? Side.BUY :Side.SELL
                                   );
                contract2position.put(c.m_conId, position);
            }

            if( o.m_totalQuantity == 0 ) {
                removeOrder( message.m_orderId );
            }
        }
        */
    }

    public Position getPosition(Instrument i){
        return instrument2position.get(i);
    }

    public void handleExecution( Execution execution ) {
        char ostatus = execution.getOrdStatus();
        log.info("execution for " + execution.getClOrdID() );
        if ( ostatus == OrdStatus.CANCELED ) {
            log.info("CXLED ");
            remove( execution.getClOrdID() );
            remove( execution.getOrigClOrdID() );
        }
        else if ( ostatus == OrdStatus.FILLED ) {
            //adjust position
            log.info("FILLED " + execution.getClOrdID() + " " + execution.getSecurityID() );
            Instrument i = cfg.getInstrument( execution.getSecurityID() );
            try{
                /*
                XXX   we could get a FILL back from an ordercancel reject msg
                      we wont know the qty from that message or the securityID
                      so position will be off
                      if FILLED && OrderCancelReject
                        getOrder().qty() side() securityID()
                        adjust position
                */
                Position p = instrument2position.get(i);
                p.addFillQty(execution.getLastQty()
                             ,execution.getSide() == ESide.BUY.getValue() ? ESide.BUY : ESide.SELL);
            }
            catch(Exception errr){
                errr.printStackTrace();
            }
            remove( execution.getClOrdID() );
        }
        else if ( ostatus == OrdStatus.PARTIALLY_FILLED ) {
            Instrument i = cfg.getInstrument( execution.getSecurityID() );
            Position p = instrument2position.get(i);
            p.addFillQty(execution.getLastQty()
                         ,execution.getSide() == ESide.BUY.getValue() ? ESide.BUY : ESide.SELL);
            log.info("PARTIALLY_FILLED "); //PARTIALLY_FILLED position
        }
        else if ( ostatus == OrdStatus.REPLACED ){
            remove( execution.getOrigClOrdID() );
            log.info("REPLACED ");
        }
        else if ( ostatus == OrdStatus.REJECTED ){
            remove( execution.getClOrdID() );
            log.info("REJECTED ");
        }
        else if ( ostatus == OrdStatus.PENDING_CANCEL && execution.getExecType() == ExecType.FILL ){
            log.info("PENDING_CANCEL FILL "+ execution.getClOrdID() + " " + execution.getSecurityID());
            Instrument i = cfg.getInstrument( execution.getSecurityID() );
            Position p = instrument2position.get(i);
            p.addFillQty(execution.getLastQty()
                         ,execution.getSide() == ESide.BUY.getValue() ? ESide.BUY : ESide.SELL);
            remove( execution.getClOrdID() );
        }
    }

}
