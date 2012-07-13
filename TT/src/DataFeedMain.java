/*
1m0z2n9x
*/



import java.util.ArrayList;

import quickfix.field.*;
import quickfix.field.Symbol;
import quickfix.FieldNotFound;
import quickfix.fix42.MarketDataSnapshotFullRefresh;
import quickfix.fix42.NewOrderSingle;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.UnsupportedMessageType;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.apache.commons.cli.*;

import com.prc.tt.BaseApplication;
import com.prc.tt.utils.fix.FIXMessageFactory;
import com.prc.tt.IDGenerator;
import com.prc.tt.Instrument;
import com.prc.tt.MarketWindow;
import com.prc.tt.utils.DateUtils;
import com.prc.tt.utils.RabbitMQConnection;
import com.prc.tt.messaging.Quote;
import com.prc.tt.messaging.Trade;
import com.prc.tt.messaging.rabbitmq.Publisher;
import com.prc.tt.messaging.rabbitmq.ExchangeType;

import java.io.FileInputStream;



public class DataFeedMain extends BaseApplication {

    private com.prc.tt.Configuration cfg;
    private final FIXMessageFactory fixfactory;
    private boolean publish = false;
    private Publisher quotepublisher;
    private Publisher tradepublisher;
    private final Connection conn;
    private final MarketWindow marketwindow;

    public DataFeedMain(String f) throws Exception{
        super(f);


        cfg = com.prc.tt.Configuration.getInstance();
        fixfactory = new FIXMessageFactory();
        marketwindow = MarketWindow.getInstance();
        conn =RabbitMQConnection.getConnection();

        quotepublisher = new Publisher(conn,"QUOTES", "nutused",ExchangeType.TOPIC);
        tradepublisher = new Publisher(conn,"TRADES", "nutused",ExchangeType.TOPIC);
    }


    private void pause() {
        try {
            Thread.sleep(5000);
        }
        catch( Exception sdf ) {
            sdf.printStackTrace();
        }
    }


    public void getMktData() {
        ArrayList<Instrument> instru =cfg.getInstruments();

        Message mdr = fixfactory.newMarketStreamRequest(IDGenerator.getID(getQuoteSessionID()),instru );

        try {
            send(mdr, getQuoteSessionID());
        }
        catch( SessionNotFound e ) {
            e.printStackTrace();
        }
        catch( FieldNotFound ferr ) {
            ferr.printStackTrace();
        }
    }


    @Override
    public void onMessage( quickfix.fix42.MarketDataSnapshotFullRefresh msg, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {

        quickfix.fix42.MarketDataSnapshotFullRefresh.NoMDEntries group =
        new quickfix.fix42.MarketDataSnapshotFullRefresh.NoMDEntries();

        MDEntryType MDEntryType = new MDEntryType();
        MDEntryPx MDEntryPx     = new MDEntryPx();
        MDEntrySize MDEntrySize = new MDEntrySize();


        Quote quote = new Quote();
        quote.setSecurityID( msg.getSecurityID().getValue() );
        quote.setSymbol( msg.getSymbol().getValue() );
        quote.setSendingTime( DateUtils.dateToString( msg.getHeader().getField( new SendingTime() ).getValue() ));

        msg.getGroup(1, group);
        quote.setBid( group.get(MDEntryPx).getValue() );
        quote.setBidSize( group.get(MDEntrySize).getValue() );

        msg.getGroup(2, group);
        quote.setOffer( group.get(MDEntryPx).getValue() );
        quote.setOfferSize( group.get(MDEntrySize).getValue() );


        Quote curquote = marketwindow.getQuote( quote.getSecurityID() );
        if( ! quote.equals( curquote ) ){
            try {
                quotepublisher.publish(quote,  new StringBuilder( quote.getSymbol() )
                                  .append(".")
                                  .append(quote.getSecurityID())
                                  .toString()
                                 );
            }
            catch( Exception errr ) {
                errr.printStackTrace();
            }
        }
    }


    public void getTradeData() {
        ArrayList<Instrument> instru =cfg.getInstruments();

        Message mdr = fixfactory.newTradeStreamRequest(IDGenerator.getID(getQuoteSessionID()),instru );

        try {
            send(mdr, getQuoteSessionID());
        }
        catch( SessionNotFound e ) {
            e.printStackTrace();
        }
        catch( FieldNotFound ferr ) {
            ferr.printStackTrace();
        }
    }


    @Override
    public void onMessage( quickfix.fix42.MarketDataIncrementalRefresh msg, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {

        quickfix.fix42.MarketDataIncrementalRefresh.NoMDEntries group =
                new quickfix.fix42.MarketDataIncrementalRefresh.NoMDEntries();


        MDEntryPx MDEntryPx     = new MDEntryPx();
        MDEntrySize MDEntrySize = new MDEntrySize();

        Trade trade = new Trade();
        trade.setSendingTime( DateUtils.dateToString( msg.getHeader().getField( new SendingTime() ).getValue() ));

        msg.getGroup(1, group);

        trade.setPrice( group.get( MDEntryPx ).getValue() );
        trade.setSize( group.get( MDEntrySize ).getValue() );
        trade.setSymbol( group.get( new Symbol() ).getValue() );
        trade.setSecurityID( group.get( new SecurityID() ).getValue() );

        try {
            tradepublisher.publish(trade,  new StringBuilder( trade.getSymbol() )
                              .append(".")
                              .append(trade.getSecurityID())
                              .toString()
                             );
        }
        catch( Exception errr ) {
            errr.printStackTrace();
        }
    }



    public static void main(String args[]) throws Exception {
        com.prc.tt.Configuration cf =com.prc.tt.Configuration.getInstance() ;


        // create the command line parser
        CommandLineParser parser = new PosixParser();

        // create the Options
        Options options = new Options();
        options.addOption( "h", "help",false, "help ." );
        options.addOption( "q", "quotes", true, "not required quotes consume." );
        options.addOption( "t", "trades", true, "not required trades consume." );
        options.addOption( "s", "subscibe", true, "subsciption file." );

        HelpFormatter formatter = new HelpFormatter();

        String subscribeconf = null;
        String quotes = null;
        String trades = null;

        try {
            CommandLine line = parser.parse( options, args );

            if( line.hasOption( "help" ) ) {
                formatter.printHelp( "java -cp  \"%CLASSPATH%\" DataFeedMain  -h", options );
                System.exit(-1);
            }

            subscribeconf = line.getOptionValue( "subscibe" );
            quotes = line.getOptionValue( "quotes", "N" );
            trades = line.getOptionValue( "trades" ,"N");

            if( subscribeconf == null ){
                formatter.printHelp( "java -cp  \"%CLASSPATH%\" DataFeedMain  -h", options );
                System.exit(-1);
            }

        }
        catch( ParseException exp ) {
            formatter.printHelp( "java -cp  \"%CLASSPATH%\" DataFeedMain -h", options );
            System.out.println( "Unexpected exception:" + exp.getMessage() );
            System.exit(-1);
        }



        cf.subcribeReader(subscribeconf);

        DataFeedMain test = new DataFeedMain( cf.getFIXconf() );
        test.setPassword( cf.getPassword() );
        test.start();


        while( ! test.isLoggedOn() ){
            try {
                System.out.println("Waiting for logon");
                Thread.sleep(5000);
            }
            catch( Exception ere ) {
                ere.printStackTrace();
            }
        }


        if(! quotes.equals("N") ) {
            System.out.println("Subscribing to quote data "  + quotes);
            test.getMktData();
        }

        if(! trades.equals("N") ){
            System.out.println("Subscribing to trade data "  + trades);
            test.getTradeData();
        }


        System.in.read();
    }





}
