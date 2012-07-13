

/*
1m0z2n9x
*/



import quickfix.Session;
import java.util.ArrayList;
import quickfix.field.Symbol;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.field.Side;
import quickfix.field.TimeInForce;
import quickfix.FieldNotFound;
import quickfix.field.SecurityType;
import quickfix.SessionNotFound;
import quickfix.UnsupportedMessageType;
import quickfix.IncorrectTagValue;
import quickfix.fix42.MarketDataSnapshotFullRefresh;
import quickfix.fix42.NewOrderSingle;

import com.prc.tt.BaseApplication;
import com.prc.tt.utils.fix.FIXMessageFactory;
import com.prc.tt.IDGenerator;
import com.prc.tt.Instrument;

import java.io.FileInputStream;



public class TTtest extends BaseApplication {

    private com.prc.tt.Configuration cfg;
    private SessionID msees;
    private SessionID osees;
    private final FIXMessageFactory fixfactory;


    public TTtest(String f) throws Exception{
        super(f);

        cfg = com.prc.tt.Configuration.getInstance();
        fixfactory = new FIXMessageFactory();
    }

    public void sendOrder(double price) {
        ArrayList<Instrument> instru =cfg.getInstruments();

        Message mdr = null;
        try {

            mdr = fixfactory.newLMTworder42(IDGenerator.getID(getTradingSessionID())
                                            ,instru.get(0),price,1,new Side(Side.BUY) );
        }
        catch( FieldNotFound ert ) {
            ert.printStackTrace();
        }

        try {
            send(mdr, getTradingSessionID());
        }
        catch( SessionNotFound  e ) {
            e.printStackTrace();
        }
        catch( FieldNotFound ferr ) {
            ferr.printStackTrace();
        }


        //cancelMessage(mdr);
        //cancelReplacePrice((NewOrderSingle)mdr);
        //cancelReplaceQty( (NewOrderSingle)mdr );
    }


    public void cancelMessage(Message mdr) {
        pause();

        Message cxlmsg=null;
        try {
            cxlmsg = fixfactory.newCancelFromMessage(IDGenerator.getID(getTradingSessionID()),mdr);
        }
        catch( Exception rtr ) {
            rtr.printStackTrace();
        }

        try {
            send(cxlmsg, getTradingSessionID());
        }
        catch( SessionNotFound  e ) {
            e.printStackTrace();
        }
        catch( FieldNotFound ferr ) {
            ferr.printStackTrace();
        }
        //=================   cxl here after 5 sec
    }



    private void pause() {
        try {
            Thread.sleep(5000);
        }
        catch( Exception sdf ) {
            sdf.printStackTrace();
        }
    }



    public void cancelReplaceQty(NewOrderSingle mdr) {
        pause();

        Message crpmsg=null;
        try {

            crpmsg = fixfactory.newCancelReplaceQty( IDGenerator.getID(getTradingSessionID())
                                                     ,mdr
                                                     ,2 );
        }
        catch( Exception rtr ) {
            rtr.printStackTrace();
        }

        try {
            send(crpmsg, getTradingSessionID());
        }
        catch( SessionNotFound  e ) {
            e.printStackTrace();
        }
        catch( FieldNotFound ferr ) {
            ferr.printStackTrace();
        }
    }



    public void cancelReplacePrice(NewOrderSingle mdr) {
        pause();

        Message crpmsg=null;
        try {
            crpmsg = fixfactory.newCancelReplacePrice( IDGenerator.getID(getTradingSessionID())
                                                       ,mdr
                                                       ,mdr.getPrice().getValue() -13 );
        }
        catch( Exception rtr ) {
            rtr.printStackTrace();
        }

        try {
            send(crpmsg, getTradingSessionID());
        }
        catch( SessionNotFound  e ) {
            e.printStackTrace();
        }
        catch( FieldNotFound ferr ) {
            ferr.printStackTrace();
        }
    }

    public void mktOrder(char side) {
        pause();

        Message crpmsg=null;
        try {
            /*
            crpmsg = fixfactory.newCancelReplacePrice( IDGenerator.getID(getTradingSessionID())
                                                       ,mdr
                                                       ,mdr.getPrice().getValue() -13 );
             */
            crpmsg = fixfactory.newMarketOrder(
                                              IDGenerator.getID(getTradingSessionID())
                                              ,side
                                              ,1
                                              ,cfg.getInstrument("CL01110400000000NN")
                                              ,TimeInForce.DAY
                                              ,cfg.getAccount()
                                              );
        }


        catch( Exception rtr ) {
            rtr.printStackTrace();
        }

        try {
            send(crpmsg, getTradingSessionID());
        }
        catch( SessionNotFound  e ) {
            e.printStackTrace();
        }
        catch( FieldNotFound ferr ) {
            ferr.printStackTrace();
        }
    }



    public void StopOrder(char side,double stoppx) {
        pause();

        Message crpmsg=null;
        try {
            /*
            crpmsg = fixfactory.newCancelReplacePrice( IDGenerator.getID(getTradingSessionID())
                                                       ,mdr
                                                       ,mdr.getPrice().getValue() -13 );
             */
            crpmsg = fixfactory.newStopOrder(
                                            IDGenerator.getID(getTradingSessionID())
                                            ,side
                                            ,1
                                            ,cfg.getInstrument("CL01110400000000NN")
                                            ,TimeInForce.DAY
                                            ,cfg.getAccount()
                                            ,stoppx
                                            );
        }


        catch( Exception rtr ) {
            rtr.printStackTrace();
        }

        try {
            send(crpmsg, getTradingSessionID());
        }
        catch( SessionNotFound  e ) {
            e.printStackTrace();
        }
        catch( FieldNotFound ferr ) {
            ferr.printStackTrace();
        }
    }



    public void StopLimitOrder(char side,double stoppx,double lmtprice) {
        pause();

        Message crpmsg=null;
        try {
            /*
            crpmsg = fixfactory.newCancelReplacePrice( IDGenerator.getID(getTradingSessionID())
                                                       ,mdr
                                                       ,mdr.getPrice().getValue() -13 );
             */
            crpmsg = fixfactory.newStopLimitOrder(
                                                 IDGenerator.getID(getTradingSessionID())
                                                 ,side
                                                 ,1
                                                 ,cfg.getInstrument("CL01110400000000NN")
                                                 ,TimeInForce.DAY
                                                 ,cfg.getAccount()
                                                 ,stoppx
                                                 ,lmtprice      );
        }


        catch( Exception rtr ) {
            rtr.printStackTrace();
        }

        try {
            send(crpmsg, getTradingSessionID());
        }
        catch( SessionNotFound  e ) {
            e.printStackTrace();
        }
        catch( FieldNotFound ferr ) {
            ferr.printStackTrace();
        }
    }

    public void getSecurityDefinition(String symbol, String securityType, String securityexchange) {
        pause();

        Message crpmsg=null;
        try {
            crpmsg = fixfactory.newSecurityDefinitionRequest(
                                                            IDGenerator.getID( getQuoteSessionID() )
                                                            ,symbol
                                                            ,securityType
                                                            ,securityexchange );
        }


        catch( Exception rtr ) {
            rtr.printStackTrace();
        }

        try {
            send(crpmsg, getQuoteSessionID());
        }
        catch( SessionNotFound  e ) {
            e.printStackTrace();
        }
        catch( FieldNotFound ferr ) {
            ferr.printStackTrace();
        }

    }


    public String getMktData() {
        ArrayList<Instrument> instru =cfg.getInstruments();

        String reqid = IDGenerator.getID(getQuoteSessionID());
        Message mdr = fixfactory.newMarketStreamRequest(reqid,instru );

        try {
            send(mdr, getQuoteSessionID());
        }
        catch( SessionNotFound e ) {
            e.printStackTrace();
        }
        catch( FieldNotFound ferr ) {
            ferr.printStackTrace();
        }

        return reqid;
    }


    public void cxlMktData(String reqid) {
        ArrayList<Instrument> instru =cfg.getInstruments();

        Message mdr = fixfactory.newCancelMarketStreamRequest(reqid,instru);

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






    public static void main(String args[]) throws Exception {
        com.prc.tt.Configuration cf =com.prc.tt.Configuration.getInstance() ;
        cf.subcribeReader(args[0]);

        TTtest test = new TTtest( cf.getFIXconf() );
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


        /*
        String mktreqid = test.getMktData();
        test.pause();
        test.cxlMktData(mktreqid);
        */
        //test.getSecurityDefinition("HOCL",SecurityType.MULTI_LEG_INSTRUMENT,"CME");
        test.getSecurityDefinition("HO",SecurityType.FUTURE,"CME");
       // test.sendOrder(2340);
        //test.mktOrder(Side.SELL);
        //test.StopOrder(Side.BUY,9899);
        //test.StopOrder(Side.SELL,9743);
        //test.StopLimitOrder(Side.SELL,9703,9702);
        //test.StopLimitOrder(Side.BUY,9755,9755);


        System.in.read();
    }





}
