/*
 $Id$
*/


package com.prc.tt.algo;


import com.prc.tt.utils.RabbitMQConnection;
import com.prc.tt.Configuration;
import com.prc.tt.messaging.rabbitmq.Consumer;
import com.prc.tt.messaging.rabbitmq.QuoteConsumer;
import com.prc.tt.messaging.rabbitmq.Publisher;
import com.prc.tt.messaging.rabbitmq.ExchangeType;
import com.prc.tt.algo.oms.OrderManager;
import com.prc.tt.algo.oms.Position;
import com.prc.tt.MarketWindow;
import com.prc.tt.Instrument;
import com.prc.tt.BaseApplication;
import com.prc.tt.messaging.Order;
import com.prc.tt.messaging.OrderBuilder;

import com.rabbitmq.client.Connection;
import quickfix.field.Side;
import quickfix.field.OrdType;
import quickfix.field.OrdStatus;
import quickfix.field.MsgType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.ArrayList;
import java.io.IOException;



public abstract class Algorithm {
    protected enum State { QUOTING, IDLE, FLAT};

    private final static Log log = LogFactory.getLog(Algorithm.class);

    protected final Configuration cfg =Configuration.getInstance() ;
    protected QuoteConsumer quoteConsumer;
    protected Consumer executionConsumer;
    protected Publisher orderPublisher,cxlorderPublisher;
    protected final MarketWindow marketwindow = MarketWindow.getInstance();
    protected OrderManager om;
    private final static ExecutorService exec =Executors.newFixedThreadPool(2);
    protected State currentstate= State.IDLE;

    protected void startFeeds() {
        Connection conn =RabbitMQConnection.getConnection();
        om = OrderManager.getInstance();

        try {
            log.info( "client is=" + cfg.getSourceID() );

            ArrayList<Instrument> intru = cfg.getInstruments();
            Instrument myinstrument = intru.get(0);
            String rkey = myinstrument.getSymbol().getValue() +"."+ myinstrument.getSecurityID().getValue();
            log.info("Subscribing to " + rkey );

            quoteConsumer = new QuoteConsumer(conn,"QUOTES",rkey, ExchangeType.TOPIC);
            executionConsumer = new Consumer(conn,"EXECUTIONS",cfg.getSourceID(), ExchangeType.DIRECT);
            //executionConsumer = new Consumer(conn,"EXECUTIONS","fred", ExchangeType.DIRECT);
            orderPublisher = new Publisher( conn,"ORDERS","notused", ExchangeType.FANOUT );
            cxlorderPublisher = new Publisher( conn,"CXL","notused", ExchangeType.FANOUT );
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
            System.exit(2);
        }

        startConsumers();
    }

    public abstract void start(String sourceid,String conffile);




    private void startConsumers() {
        log.info("starting consumers");
        try {
            exec.submit( new ConsumersThread(quoteConsumer) );
        }
        catch( Exception ert ) {
            log.info(ert.getStackTrace());
        }


        try {
            exec.submit( new ConsumersThread(executionConsumer) );
        }
        catch( Exception pwi ) {
            log.info(pwi.getStackTrace());
        }
    }


    protected void sendOrder(Order ord) {
        try {
            log.info("placing order "  + ord.getClOrdID() );
            orderPublisher.publish(ord);
        }
        catch( IOException ioe ) {
            log.info( ioe.getStackTrace() );
        }

        om.add(ord);
    }


    protected void sendCxlOrder(Order ord) {
        try {
            log.info("sending cxl order=" + ord.getOrigClOrdID() + " "+ord.getClOrdID() );
            Order origorder = om.getOrder( ord.getOrigClOrdID());
            origorder.setOrdStatus(OrdStatus.PENDING_CANCEL);
            cxlorderPublisher.publish(ord);
        }
        catch( IOException ioe ) {
            log.info( ioe.getStackTrace() );
        }

        om.add(ord);
    }


    public void cxlAll() {
        log.info("cxlAll" );
        currentstate = State.IDLE;
        ArrayList<Order> olist = om.getOrders();
        for( Order oldorder : olist ) {
            Order cxlord = OrderBuilder.createCancel( oldorder.getClOrdID(),oldorder.getSourceID(),cfg.getInstrument(oldorder.getSecurityID() ) );
            sendCxlOrder(cxlord);
        }
    }


    //XXX    these 4 are cxlreplaces
    public void shiftBidsPrice(String secid, boolean up) {
        log.info("shiftBidsPrice up " + up);
        ArrayList<Order> bidorders = om.getBidOrders(secid);

        double tick = cfg.getInstrument(secid).getExchTickSize().getValue();
        for( Order oldorder : bidorders ) {
            double price = up ? oldorder.getPrice() +1*tick : oldorder.getPrice() -1 *tick;
            double quantity = oldorder.getOrderQty();
            Order cxlord = OrderBuilder.createCxlReplace( oldorder.getClOrdID(),oldorder.getSourceID()
                                                          ,cfg.getInstrument(oldorder.getSecurityID() )
                                                          ,price,quantity,Side.BUY);
            sendCxlOrder(cxlord);
        }
    }


    public void flatposition( String secid ){
        cxlAll();
        log.info("flatten position ");
        Instrument myin =cfg.getInstrument( secid );
        Position p = om.getPosition(myin);
        double currentpos= p.getQty();
        log.info("Position=>" + currentpos );

        if( currentpos != 0 ){
            double tick = cfg.getInstrument(secid).getExchTickSize().getValue();
            char side = currentpos >0 ? Side.SELL : Side.BUY;
            double price = side == Side.SELL
                                ? marketwindow.getQuote(secid).getBid() - 3*tick
                                : marketwindow.getQuote(secid).getOffer() + 3*tick ;

            Order ord = new OrderBuilder()
                        .withSide( side )
                        .withOrdType( OrdType.LIMIT )
                        .withPrice( price )
                        .withOrderQty( Math.abs(currentpos) )
                        .withMsgType( MsgType.ORDER_SINGLE )
                        .withInstrument( myin )
                        .withSourceID( cfg.getSourceID() )
                        .create();

            currentstate = State.FLAT;
            sendOrder(ord);
        }
    }


    public void shiftOffersPrice(String secid, boolean up) {
        log.info("shiftOffersPrice up " + up);
        ArrayList<Order> offerorders = om.getOfferOrders(secid);

        double tick = cfg.getInstrument(secid).getExchTickSize().getValue();
        for( Order oldorder : offerorders ) {
            double price = up ? oldorder.getPrice() +1*tick : oldorder.getPrice() -1*tick;
            double quantity = oldorder.getOrderQty();
            Order cxlord = OrderBuilder.createCxlReplace( oldorder.getClOrdID(),oldorder.getSourceID()
                                                          ,cfg.getInstrument(oldorder.getSecurityID() )
                                                          ,price,quantity,Side.SELL);
            sendCxlOrder(cxlord);
        }
    }


    public void changeBidsQty(String secid, boolean increase) {
        log.info("changeBidsQty increase " + increase );
        ArrayList<Order> bidorders = om.getBidOrders(secid);

        for( Order oldorder : bidorders ) {
            double price =   oldorder.getPrice();
            double quantity = increase ? oldorder.getOrderQty() + 1 : oldorder.getOrderQty() - 1 ;
            if( quantity==0 ) {
                continue;
            }
            Order cxlord = OrderBuilder.createCxlReplace( oldorder.getClOrdID(),oldorder.getSourceID()
                                                          ,cfg.getInstrument(oldorder.getSecurityID() )
                                                          ,price,quantity,Side.BUY);
            sendCxlOrder(cxlord);
        }
    }


    public void changeOffersQty(String secid, boolean increase) {
        log.info("changeOffersQty increase " + increase );
        ArrayList<Order> offerorders = om.getOfferOrders(secid);

        for( Order oldorder : offerorders ) {
            double price =   oldorder.getPrice();
            double quantity = increase ? oldorder.getOrderQty() + 1 : oldorder.getOrderQty() - 1 ;
            if( quantity==0 ) {
                continue;
            }

            Order cxlord = OrderBuilder.createCxlReplace( oldorder.getClOrdID(),oldorder.getSourceID()
                                                          ,cfg.getInstrument(oldorder.getSecurityID() )
                                                          ,price,quantity,Side.SELL);
            sendCxlOrder(cxlord);
        }
    }





    class ConsumersThread implements Runnable {
        Consumer consumer;

        public ConsumersThread(Consumer c) {
            this.consumer = c;
        }

        public void run() {
            try {
                consumer.consume();
            }
            catch( IOException err ) {
                log.info(err.getStackTrace());
            }
        }
    }
}
