
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.io.IOException;


public class QuickQuote extends Algorithm implements MessageListener<Execution> {

    private final static Log log = LogFactory.getLog(QuickQuote.class);


    private ScheduledExecutorService sexec;
    private ExecutorService exec;

    private int threshold;
    private int maxpos=6;
    private int coverdist=4;
    private static int size=1;
    private int quoteoffset = 2;
    private long quotedelay = 1000L;
    private long coverdelay = 3500L;

    private double ticksize;
    private char side=Side.BUY;



    public void setCoverDelay(long d) {
        this.coverdelay=d;
    }

    public void setQuoteDelay(long d) {
        this.quotedelay=d;
    }

    public void setQuoteOffset(int d) {
        this.quoteoffset=d;
    }

    public void setSize(int sz) {
        this.size=sz;
    }
    public void setThreshold(int thresh ) {
        this.threshold = thresh;
    }


    public void setCoverdist( int coverd ) {
        this.coverdist = coverd;
    }

    public void setMaxpos( int maxpos ) {
        this.maxpos = maxpos;
    }


    public void setSide(char side) {
        this.side=side;
    }

    public void start(  String sourceid,String conffile) {
        exec =Executors.newSingleThreadExecutor();
        cfg.subcribeReader(conffile);
        cfg.setSourceID(sourceid);

        startFeeds();
        executionConsumer.addListener(this,MessageType.EXECUTION);

        try {
            Thread.sleep(5000);
        }
        catch( Exception err ) {
        }


        //placeBid();

        sexec = Executors.newScheduledThreadPool( 2  );
        ArrayList<Instrument> intru = cfg.getInstruments();
        Instrument myinstrument = intru.get(0);
        sexec.schedule(new OpenOrder( myinstrument,Side.BUY ), 1000L, TimeUnit.MILLISECONDS);
        sexec.schedule(new OpenOrder( myinstrument,Side.SELL ), 1000L, TimeUnit.MILLISECONDS);
    }

    public void startBidThread() {
        ArrayList<Instrument> intru = cfg.getInstruments();
        Instrument myinstrument = intru.get(0);
        sexec.schedule(new OpenOrder( myinstrument,Side.BUY ), 500L, TimeUnit.MILLISECONDS);
    }

    public void startOfferThread() {
        ArrayList<Instrument> intru = cfg.getInstruments();
        Instrument myinstrument = intru.get(0);
        sexec.schedule(new OpenOrder( myinstrument,Side.SELL ), 500L, TimeUnit.MILLISECONDS);
    }


    public String getSecurityID() {
        ArrayList<Instrument> intru = cfg.getInstruments();
        Instrument myinstrument = intru.get(0);
        return  myinstrument.getSecurityID().getValue();
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
            if( currentstate == State.QUOTING && Math.abs(currentpos) > this.maxpos ) {
                log.info("Max position breached=> " + currentpos);
                flatposition(secid);
            }


            ArrayList<Order> bids = om.getBidOrders(secid);
            ArrayList<Order> offers = om.getOfferOrders(secid);

            log.info("#bids=>" + bids.size() );
            log.info("#offers=>" + offers.size() );
            log.info("ordstatus =>" + message.getOrdStatus() );


            //we in flat state so dont put out orders
            if( currentstate == State.FLAT || currentpos==0 ) {
                return;
            }


            //place cover if we got filled
            if( message.getOrdStatus() == OrdStatus.FILLED || message.getOrdStatus() == OrdStatus.PARTIALLY_FILLED ) {
                if( message.getSide() == Side.BUY ) {
                    log.info("we bought");
                    double ordersize = currentpos < 0 ? size : message.getLastQty();
                    cxlAll();
                    exec.submit( new Cover(myin,Side.SELL) );
                }
                else if( message.getSide() == Side.SELL ) {
                    log.info("we sold");
                    double ordersize = currentpos > 0 ? size : message.getLastQty();
                    cxlAll();
                    exec.submit( new Cover(myin,Side.BUY) );
                }
            }
        }
    }



    class OpenOrder implements Runnable {
        Quote quote;
        final Instrument instrument;
        final char side;
        final Position position;
        final double tick;

        public OpenOrder(final Instrument i, char side ) {
            this.instrument = i;
            this.side = side;
            this.quote = marketwindow.getQuote( i.getSecurityID().getValue() );
            position = om.getPosition(i);
            tick = i.getExchTickSize().getValue();
        }

        public void run() {
            double price = side == Side.BUY ? quote.getBid() - quoteoffset : quote.getOffer() +quoteoffset;
            placeOrder(side, size, price , this.instrument);
            currentstate = State.QUOTING;

            while( position.getQty() == 0 && currentstate == State.QUOTING ) {
                this.quote = marketwindow.getQuote( this.instrument.getSecurityID().getValue() );

                try {
                    Thread.sleep(quotedelay);
                }
                catch( Exception exc ) {
                    exc.printStackTrace();
                }


                if( (this.side==Side.BUY  && price  != this.quote.getBid() - tick* quoteoffset )
                    || (this.side==Side.SELL && price != this.quote.getOffer() + tick* quoteoffset )
                  ) {
                    if( this.side==Side.BUY ) {
                        if( price >= this.quote.getBid() - tick*quoteoffset ) {
                            shiftBidsPrice(this.instrument.getSecurityID().getValue(),false);
                            price -= tick;
                        }
                        else {
                            shiftBidsPrice(this.instrument.getSecurityID().getValue(),true);
                            price += tick;
                        }
                    }
                    else {
                        if( price <= this.quote.getOffer() + tick*quoteoffset ) {
                            shiftOffersPrice(this.instrument.getSecurityID().getValue(),true);
                            price += tick;
                        }
                        else {
                            shiftOffersPrice(this.instrument.getSecurityID().getValue(),false);
                            price -= tick;
                        }
                    }
                }
            }

        }

    }



    class Cover implements Runnable {
        int trys=0;
        int increment=1;
        Quote quote;
        final Instrument instrument;
        final char side;
        final Position position;
        final double tick;

        public Cover(final Instrument i, char side ) {
            this.instrument = i;
            this.side = side;
            this.quote = marketwindow.getQuote( i.getSecurityID().getValue() );
            position = om.getPosition(i);
            tick = i.getExchTickSize().getValue();
        }

        public void run() {
            double price = side == Side.BUY ? quote.getBid() -coverdist : quote.getOffer() + coverdist;
            placeOrder(side, size, price , this.instrument);

            while( position.getQty() != 0 ) {
                this.quote = marketwindow.getQuote( this.instrument.getSecurityID().getValue() );

                log.info("closing leg try: " + trys);
                try {
                    Thread.sleep(coverdelay);
                }
                catch( Exception exc ) {
                    exc.printStackTrace();
                }


                if(
                  (this.side==Side.BUY  && price  >= this.quote.getBid() && this.quote.getBidSize()>size )
                  || (this.side==Side.SELL && price <= this.quote.getOffer() && this.quote.getOfferSize()>size )
                  ) {
                    log.info("we are best bid " + this.quote.getBid() + " "+ price );
                    log.info("we are best ask " + this.quote.getOffer() + " "+ price );
                }
                else {
                    log.info("mkt " + this.quote.getBid() + "/"+  this.quote.getOffer() +" = "+price );

                    if( this.side==Side.BUY ) {
                        boolean up = (this.quote.getBidSize() == size && price == this.quote.getBid() )
                                                    ? false : true;
                        shiftBidsPrice(this.instrument.getSecurityID().getValue(),up);
                        //price += tick;
                        price += up ? tick : -tick;
                    }
                    else {
                        boolean up = ( this.quote.getOfferSize() == size && price == this.quote.getOffer() )
                                                    ? true : false;
                        shiftOffersPrice(this.instrument.getSecurityID().getValue(),up);
                        //price -= tick;
                        price += up ? tick : -tick;
                    }


                    // if( Math.abs(ogprice - order.m_lmtPrice) > tick*8 ) {
                    //     increment+=11;
                    // }
                }
                if( trys++ > 3 ) {
                    increment++;
                }
            }
            currentstate = State.FLAT;
        }

    }
}




