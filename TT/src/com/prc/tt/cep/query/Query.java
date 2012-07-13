/*

$Id$
*/

package com.prc.tt.cep.query;


import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.client.StatementAwareUpdateListener;
import com.espertech.esper.client.util.JSONEventRenderer;

import com.rabbitmq.client.Connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.prc.tt.cep.Event;
import com.prc.tt.utils.Reformatter;
//import com.prc.tt.Configuration;
import com.prc.tt.messaging.rabbitmq.Consumer;
import com.prc.tt.messaging.rabbitmq.ExchangeType;
import com.prc.tt.messaging.Quote;
import com.prc.tt.messaging.Trade;
import com.prc.tt.messaging.Message;
import com.prc.tt.messaging.MessageListener;
import com.prc.tt.messaging.MessageType;
import com.prc.tt.utils.RabbitMQConnection;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.io.IOException;



public abstract class Query implements MessageListener<Message>{
    private final static Log log = LogFactory.getLog(Query.class);
    protected final com.prc.tt.Configuration cfg =com.prc.tt.Configuration.getInstance() ;

    EPRuntime zepRuntime;
    EPServiceProvider zepService;
    UpdateListener ulistener;
    StatementAwareUpdateListener salistener;
    protected Consumer quoteConsumer;
    protected Consumer tradeConsumer;
    private final static ExecutorService exec =Executors.newFixedThreadPool(2);
    private final Connection conn;
    private String quotes="N";
    private String trades="N";



    public abstract void start();


    public void sendEvent( Message evt ) {
        try {
            zepRuntime.sendEvent( evt);
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }


    public void setStatement(String qry) {
        EPStatement zstmt = mkStatement( qry );
        if ( ulistener != null ) {
            zstmt.addListener(ulistener);
            //XXX
            if( ulistener instanceof com.prc.tt.utils.Reformatter ){
                Reformatter cs = (Reformatter)ulistener;
                cs.setRenderer( getRenderer( zstmt ) );
            }
        }
        if ( salistener != null ) {
            zstmt.addListener(salistener);
        }
    }



    public void setListener( Object listener ) {
        if( listener instanceof com.espertech.esper.client.UpdateListener ) {
            this.ulistener = (UpdateListener)listener;
        }
        else if( listener instanceof com.espertech.esper.client.StatementAwareUpdateListener ) {
            this.salistener = (StatementAwareUpdateListener)listener;
        }
    }



    private EPStatement mkStatement(String qry) {
        return zepService.getEPAdministrator().createEPL( qry );
    }

    private JSONEventRenderer getRenderer( EPStatement statement ) {
        return zepRuntime.getEventRenderer().getJSONRenderer(statement.getEventType());
    }

    public void onMessage( MessageType t, Message message ) {
        sendEvent(message);
    }



    public Query() {
        com.espertech.esper.client.Configuration configuration = new Configuration();

        configuration.addEventType("PriceEvent", Quote.class.getName());
        configuration.addEventType("TradeEvent", Trade.class.getName());
        this.zepService = EPServiceProviderManager.getDefaultProvider(configuration);
        this.zepRuntime = zepService.getEPRuntime();

        conn =RabbitMQConnection.getConnection();
    }


    /*
    the spring xml file sets what data you want
    each subclass must call consumeData()
    */
    public void setQuotes(String t){
        this.quotes = t;
    }
    public void setTrades(String t){
        this.trades = t;
    }


    protected void consumeData(){
        if( this.quotes.equals("Y") ){
            quoteConsumption();
        }

        if( this.trades.equals("Y") ){
            tradeConsumption();
        }
    }


    private void quoteConsumption(){
        try {
            log.info( "client is=" + cfg.getSourceID() );

            String rkey = "*.*";
            log.info("Subscribing to quotes " + rkey );

            quoteConsumer = new Consumer(conn,"QUOTES",rkey, ExchangeType.TOPIC);
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
            System.exit(2);
        }


        //consume in other thread
        exec.submit( new Runnable() {
                         public void run() {
                             try {
                                 quoteConsumer.consumeQuotes();
                             }
                             catch( Exception err ) {
                                 log.info(err.getStackTrace());
                             }

                         }
                     });

        //listen to quotes
        quoteConsumer.addListener(this,MessageType.QUOTE);
    }


    private void tradeConsumption(){
        try {
            log.info( "client is=" + cfg.getSourceID() );

            String rkey = "*.*";
            log.info("Subscribing to trades " + rkey );

            tradeConsumer = new Consumer(conn,"TRADES",rkey, ExchangeType.TOPIC);
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
            System.exit(2);
        }


        //consume in other thread
        exec.submit( new Runnable() {
                         public void run() {
                             try {
                                 tradeConsumer.consumeTrades();
                             }
                             catch( Exception err ) {
                                 log.info(err.getStackTrace());
                             }

                         }
                     });

        //listen to trades
        tradeConsumer.addListener(this,MessageType.TRADE);
    }
}
