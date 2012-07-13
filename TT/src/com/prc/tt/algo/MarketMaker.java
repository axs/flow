
/*
$Id$
*/


package com.prc.tt.algo;


import quickfix.field.Side;
import quickfix.field.OrdType;
import quickfix.field.OrdStatus;
import quickfix.field.MsgType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Set;

import com.prc.tt.Configuration;
import com.prc.tt.Instrument;
import com.prc.tt.messaging.OrderBuilder;
import com.prc.tt.messaging.Order;
import com.prc.tt.messaging.Quote;
import com.prc.tt.messaging.MessageListener;
import com.prc.tt.messaging.Execution;
import com.prc.tt.messaging.MessageType;
import com.prc.tt.algo.oms.Position;
import com.prc.tt.utils.Comparors;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.io.IOException;


public class MarketMaker extends Algorithm implements MessageListener<Execution> {

    private final static Log log = LogFactory.getLog(MarketMaker.class);

    private ScheduledExecutorService exec;

    private int threshold;
    private int mktdist=5;
    private int maxpos=6;
    private int coverdist=2;
    private static int size=3;
    private static int compsize=1;
    private static int compdist=1;
    private double ticksize;

    public void setSize(int sz) {
        this.size=sz;
    }
    public void setThreshold(int thresh ) {
        this.threshold = thresh;
    }

    public void setMktdist( int mktd ) {
        this.mktdist = mktd;
    }

    public void setCoverdist( int coverd ) {
        this.coverdist = coverd;
    }

    public void setMaxpos( int maxpos ) {
        this.maxpos = maxpos;
    }



    public void start(  String sourceid,String conffile) {
        cfg.subcribeReader(conffile);
        cfg.setSourceID(sourceid);

        startFeeds();
        executionConsumer.addListener(this,MessageType.EXECUTION);
        // executionConsumer.addListener(om,MessageType.EXECUTION);


        try {
            Thread.sleep(5000);
        }
        catch( Exception err ) {
        }


        makemarkets();
    }

    public String getSecurityID(){
        ArrayList<Instrument> intru = cfg.getInstruments();
        Instrument myinstrument = intru.get(0);
        return  myinstrument.getSecurityID().getValue();
    }


    public void makemarkets(){
        currentstate = State.QUOTING;
        ArrayList<Instrument> intru = cfg.getInstruments();
        Instrument myinstrument = intru.get(0);
        Quote quote=null;

        while( quote == null ){
            try {
                quote = marketwindow.getQuote( myinstrument.getSecurityID().getValue() );
            }
            catch( Exception err ) {
                err.printStackTrace();
                log.info("no datat");
            }

            if( quote == null ) {
                log.info("no quote sleeping");
                try {
                    Thread.sleep(3000L);
                }
                catch( Exception eer ) {
                    log.info( eer.getStackTrace() );
                }
            }

        }

        placeOrder(Side.BUY,this.size,quote.getBid() - this.mktdist *myinstrument.getExchTickSize().getValue() ,myinstrument);
        placeOrder(Side.SELL,this.size,quote.getOffer() + this.mktdist *myinstrument.getExchTickSize().getValue() ,myinstrument);

        placeOrder(Side.BUY,this.size,quote.getBid() - this.mktdist*2 *myinstrument.getExchTickSize().getValue() ,myinstrument);
        placeOrder(Side.SELL,this.size,quote.getOffer() + this.mktdist*2*myinstrument.getExchTickSize().getValue() ,myinstrument);
    }


    private void placeOrder( char side,double qty ,double price,Instrument instru ) {
        //place bid
        Order ord = new OrderBuilder()
                    .withSide(side)
                    .withOrdType(OrdType.LIMIT)
                    .withPrice( price )
                    .withOrderQty(qty)
                    .withMsgType(MsgType.ORDER_SINGLE)
                    .withInstrument(instru)
                    .withSourceID(cfg.getSourceID())
                    .create();

        sendOrder(ord);
    }



    public void onMessage( MessageType t, Execution message ) {
        if( message.getMsgType().equals(MsgType.ORDER_CANCEL_REJECT) ) {
            om.handleExecution(message);
        }
        else {
            log.info("onMessage Execution Called " + message.getClOrdID() + " " + message.getMsgType() );
            String secid = message.getSecurityID();
            Quote quote = marketwindow.getQuote( secid );
            Instrument myin =cfg.getInstrument( secid );
            String sourceid = cfg.getSourceID();

            om.handleExecution(message);
            Position p = om.getPosition(myin);
            double currentpos= p.getQty();
            log.info("Position=>" + currentpos );


            //make sure we dont keep adding positions
            if( currentstate == State.QUOTING && Math.abs(currentpos) > this.maxpos ){
                log.info("Max position breached=> " + currentpos);
                flatposition(secid);
            }


            ArrayList<Order> bids = om.getBidOrders(secid);
            ArrayList<Order> offers = om.getOfferOrders(secid);

            log.info("#bids=>" + bids.size() );
            log.info("#offers=>" + offers.size() );
            log.info("ordstatus =>" + message.getOrdStatus() );


            //we in flat state so dont put out orders
            if(currentstate == State.FLAT) {
                return;
            }



            //place cover if we got filled
            if( message.getOrdStatus() == OrdStatus.FILLED || message.getOrdStatus() == OrdStatus.PARTIALLY_FILLED ) {
                if( message.getSide() == Side.BUY ) {
                    log.info("we bought");
                    double ordersize = currentpos < 0 ? size : message.getLastQty();
                    placeOrder(Side.SELL, ordersize,message.getPrice() + this.coverdist * myin.getExchTickSize().getValue()  , myin);

                    if( bids.size() == 0 ) {
                        log.info("no bid placing order");
                        placeOrder(Side.BUY,size,quote.getBid() - this.mktdist * myin.getExchTickSize().getValue()  , myin);
                    }
                }
                else if( message.getSide() == Side.SELL ) {
                    log.info("we sold");
                    double ordersize = currentpos > 0 ? size : message.getLastQty();
                    placeOrder(Side.BUY,ordersize,message.getPrice() - this.coverdist * myin.getExchTickSize().getValue() , myin);

                    if( offers.size() == 0 ) {
                        log.info("no offers placing order");
                        placeOrder(Side.SELL,size,quote.getOffer() +this.mktdist * myin.getExchTickSize().getValue() ,myin);
                    }
                }
            }


            ArrayList<Order> xbids = om.getBidOrders(secid);
            ArrayList<Order> xoffers = om.getOfferOrders(secid);
            if( xbids.size() > 3 ) {
                log.info("remove off market bids");
                Collections.sort(xbids,Comparors.LOW_PRICE_ORDERS);
                Order oldorder = xbids.get(0);
                Order cxlord = OrderBuilder.createCancel( oldorder.getClOrdID(),sourceid,myin);
                sendCxlOrder(cxlord);
            }
            if( xoffers.size() > 3 ) {
                Collections.sort(xoffers,Comparors.HIGH_PRICE_ORDERS);
                log.info("remove off market offers");
                Order oldorder = xoffers.get(0);
                Order cxlord = OrderBuilder.createCancel( oldorder.getClOrdID(),sourceid,myin);
                sendCxlOrder(cxlord);
            }


        }
    }

}




