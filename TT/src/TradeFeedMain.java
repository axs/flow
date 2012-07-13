
/*
1m0z2n9x
*/



import java.util.ArrayList;

import quickfix.field.*;
import quickfix.field.Symbol;
import quickfix.FieldNotFound;
import quickfix.fix42.MarketDataIncrementalRefresh;
import quickfix.fix42.NewOrderSingle;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.UnsupportedMessageType;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import com.prc.tt.BaseApplication;
import com.prc.tt.utils.fix.FIXMessageFactory;
import com.prc.tt.IDGenerator;
import com.prc.tt.Instrument;
import com.prc.tt.MarketWindow;
import com.prc.tt.utils.DateUtils;
import com.prc.tt.utils.RabbitMQConnection;
import com.prc.tt.utils.fix.extension.AggressorSide;
import com.prc.tt.messaging.Trade;
import com.prc.tt.messaging.rabbitmq.Publisher;
import com.prc.tt.messaging.rabbitmq.ExchangeType;

import java.io.FileInputStream;



public class TradeFeedMain extends BaseApplication {

    private com.prc.tt.Configuration cfg;
    private final FIXMessageFactory fixfactory;
    private boolean publish = false;
    private Publisher publisher;
    private final Connection conn;
    private final MarketWindow marketwindow;

    public TradeFeedMain(String f) throws Exception{
        super(f);


        cfg = com.prc.tt.Configuration.getInstance();
        fixfactory = new FIXMessageFactory();
        marketwindow = MarketWindow.getInstance();
        conn =RabbitMQConnection.getConnection();

        publisher = new Publisher(conn,"TRADES", "nutused",ExchangeType.TOPIC);
    }


    private void pause() {
        try {
            Thread.sleep(5000);
        }
        catch( Exception sdf ) {
            sdf.printStackTrace();
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


        //Quote curquote = marketwindow.getQuote( quote.getSecurityID() );
        //if( ! quote.equals( curquote ) ) {
            try {
                publisher.publish(trade,  new StringBuilder( trade.getSymbol() )
                                  .append(".")
                                  .append(trade.getSecurityID())
                                  .toString()
                                 );
            }
            catch( Exception errr ) {
                errr.printStackTrace();
            }
        //}
    }



    public static void main(String args[]) throws Exception {
        com.prc.tt.Configuration cf =com.prc.tt.Configuration.getInstance() ;
        cf.subcribeReader(args[0]);

        TradeFeedMain test = new TradeFeedMain( cf.getFIXconf() );
        test.setPassword( cf.getPassword() );
        test.start();


        while( ! test.isLoggedOn() ) {
            try {
                System.out.println("Waiting for logon");
                Thread.sleep(5000);
            }
            catch( Exception ere ) {
                ere.printStackTrace();
            }
        }

        test.getTradeData();
        System.in.read();
    }
}
