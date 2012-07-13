
/*
  $Id$
*/


import java.io.FileInputStream;
import java.util.List;
import java.util.Date;
import java.util.ArrayList;

import quickfix.*;
import quickfix.Session;
import quickfix.field.*;
import quickfix.fix42.NewOrderSingle;
import quickfix.fix44.MarketDataRequest;

import com.prc.tt.MarketDataRequestBuilder;
import com.prc.tt.TestBaseApplication;
import com.prc.tt.utils.fix.FIXMessageFactory;
import com.prc.tt.IDGenerator;
import com.prc.tt.Configuration;
import com.prc.tt.Instrument;

import quickfix.field.Symbol;



public class AppMkt {


    public static void main(String args[]) throws Exception {

        com.prc.tt.Configuration cfg = com.prc.tt.Configuration.getInstance();
        cfg.subcribeReader("C:/work/TT/conf/subscribe/cl.xml");

        TestBaseApplication application = new TestBaseApplication("C:/work/TT/conf/fix/sd.cfg");
        application.start();
        SessionID msees = application.getQuoteSessionID();

        while(! application.isLoggedOn() ){
            try{
                System.out.println("Waiting for logon");
                Thread.sleep(3000);
            }
            catch(Exception ere){
                ere.printStackTrace();
            }
        }





        FIXMessageFactory fixfactory = new FIXMessageFactory(msees);

        /*
        ArrayList syms = new ArrayList<String>();
        syms.add("CME");
        syms.add("INTC");
        syms.add("FCX");
        syms.add("RIG");
        syms.add("GLD");
        */
        ArrayList<Instrument> syms = cfg.getInstruments();

        Message msg = fixfactory.newMarketDataRequest(IDGenerator.getID(msees),syms);
        Message seclist=fixfactory.newSecurityListRequest(IDGenerator.getID(msees));
        Message mdr = fixfactory.newMarketStreamRequest(IDGenerator.getID(msees),syms );

        try {
            //sendOrderCancelRequest( (SessionID)sessions.get(0) );
            //submitMktReq( msees,"AAPL","1" );
            // submitMktReq( msees,"CSCO","2" );
            // xsubmitMktReq( msees,"GOOG","3" );



            application.send(mdr, msees);

            /*
                if ( Session.sendToTarget(seclist, msees ) ) {
                    System.out.println("seclist submit message sent");
                }
              */

        }
        catch ( Exception e ) {
            e.printStackTrace();
        }



        System.out.println("press to quit");
        System.in.read();
    }



    public static void xsubmitMktReq(SessionID s,String symbol, String reqId) throws Exception{

        MarketDataRequest.NoRelatedSym symbolGroup = new MarketDataRequest.NoRelatedSym();
        symbolGroup.setField( new Symbol(symbol) );


        MarketDataRequest message = new MarketDataRequestBuilder()
                                    .withMDReqID( new MDReqID(reqId) )
                                    .withMarketDepth( new MarketDepth(1) )
                                    .withSubscriptionRequestType( new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES) )
                                    .create();

        message.addGroup(symbolGroup);

         if ( Session.sendToTarget(message, s ) ) {
            System.out.println("Req submit message sent");
        }
    }

    // XXX
    public static void submitMktReq(SessionID s,String symbol, String reqId) throws Exception{

        // MarketDataRequest.NoMDEntryTypes marketDataEntryGroup =  new MarketDataRequest.NoMDEntryTypes();
        //  marketDataEntryGroup.set( new MDEntryType(MDEntryType.BID) );


        MarketDataRequest.NoRelatedSym symbolGroup = new MarketDataRequest.NoRelatedSym();
        symbolGroup.setField( new Symbol(symbol) );

        MarketDataRequest message = new MarketDataRequest(
                                                         new MDReqID(reqId)
                                                         ,new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES)
                                                         ,new MarketDepth(1)
                                                         ) ;
        // message.set( new MDUpdateType( MDUpdateType.INCREMENTAL_REFRESH ) );
        //  message.set( new AggregatedBook( true ) );
        //  message.set( new MDImplicitDelete( true ) );

        //
        message.setInt(NoMDEntryTypes.FIELD, 2);
        MarketDataRequest.NoMDEntryTypes entryTypeGroup =  new MarketDataRequest.NoMDEntryTypes();
        entryTypeGroup.set(new MDEntryType(MDEntryType.BID));
        message.addGroup(entryTypeGroup);

        // entryTypeGroup.set(new MDEntryType(MDEntryType.OFFER));
        // message.addGroup(entryTypeGroup);

        //

        //  message.addGroup(marketDataEntryGroup);
        message.addGroup(symbolGroup);



        if ( Session.sendToTarget(message, s ) ) {
            System.out.println("Req submit message sent");
        }

    }



    public static void submitOrder(SessionID s) throws Exception{

        // the constructor includes ALL required fields.
        NewOrderSingle message = new NewOrderSingle(
                                                   new ClOrdID( IDGenerator.getID(s) )
                                                   ,new HandlInst(HandlInst.AUTOMATED_EXECUTION_ORDER_PUBLIC)
                                                   ,new Symbol("AAPL")
                                                   ,new Side(Side.BUY)
                                                   ,new TransactTime( new Date() )
                                                   ,new OrdType( OrdType.LIMIT )
                                                   );


        // required user-defined field (CME)
        // message.setField(new CorrelationClOrdID(order.getClientOrderId()));

        // market order fields
        message.setDouble(OrderQty.FIELD, 2236);
        message.setDouble(Price.FIELD, 173.53);

        if ( Session.sendToTarget(message, s ) ) {
            System.out.println("submit message sent");
        }

    }
//






    public static void sendOrderCancelRequest( SessionID s) throws Exception{

        Message message = new Message();
        // BeginString
        message.getHeader().setField(new StringField(8, "FIX.4.2"));
        // SenderCompID
        message.getHeader().setField(new StringField(49, "sender-10289-OMS"));
        // TargetCompID, with enumeration
        message.getHeader().setField(new StringField(56, "MRKTC-EXCH"));
        // MsgType

        message.getHeader().setString(MsgType.FIELD, "D") ;
        // required fields

        message.setChar(HandlInst.FIELD, HandlInst.AUTOMATED_EXECUTION_ORDER_PUBLIC);
        message.setString(Symbol.FIELD, "IBM");
        message.setChar(Side.FIELD, Side.BUY);
        message.setChar(OrdType.FIELD,OrdType.MARKET);


        /*
        // OrigClOrdID
        message.setField(new StringField(41, "123"));
        // ClOrdID
        message.setField(new StringField(11, "321"));
        // Symbol
        message.setField(new StringField(55, "IBM"));
        // Side, with value enumeration
        message.setField(new CharField(54, Side.BUY));

        // Text
        message.setField(new StringField(58, "Cancel My Order!"));
        */

        message.setString(ClOrdID.FIELD, "543343434");

        message.setUtcTimeStamp(TransactTime.FIELD, new Date() );



        // market order fields
        message.setDouble(OrderQty.FIELD, 200);
        message.setDouble(Price.FIELD, 95.22);



        if ( Session.sendToTarget(message, s ) ) {
            System.out.println("message sent");
        }
    }








}
