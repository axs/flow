


import java.util.Date;
import quickfix.*;
import quickfix.Session;
import quickfix.field.*;
import java.util.ArrayList;
import quickfix.fix42.NewOrderSingle;

import com.prc.tt.TestBaseApplication;
import com.prc.tt.utils.fix.FIXMessageFactory;
import com.prc.tt.IDGenerator;

import java.io.FileInputStream;





public class AppMain {


    public static void main(String args[]) throws Exception {

        TestBaseApplication application = new TestBaseApplication("C:/work/TT/conf/fix/Alex.cfg");
        application.start();
        SessionID osees = application.getTradingSessionID();


        while(! application.isLoggedOn() ){
            try{
                System.out.println("Waiting for logon");
                Thread.sleep(3000);
            }
            catch(Exception ere){
                ere.printStackTrace();
            }
        }

        //submitOrder( osees );

        FIXMessageFactory fixfactory = new FIXMessageFactory(osees);
      //  Message m = fixfactory.newLMTworder42(IDGenerator.getID(osees),"TUM",100.22,300,new Side(Side.BUY));
      //  application.send(m, osees);

        /*
        if ( Session.sendToTarget(m, osees ) ) {
            System.out.println("submit order sent");
        }
        */

     //   Message g = fixfactory.newLMTworder42(IDGenerator.getID(osees),"GOOG",52.81,300,new Side(Side.BUY));

     //   application.send(g, osees);

        /*
        if ( Session.sendToTarget(g, osees ) ) {
            System.out.println("submit order sent");
        }
        */

        System.in.read();
    }





    //


    public static void submitOrder(SessionID s) throws Exception{

        // the constructor includes ALL required fields.
        NewOrderSingle message = new NewOrderSingle(
                                                   new ClOrdID("9834333")
                                                   ,new HandlInst(HandlInst.AUTOMATED_EXECUTION_ORDER_PUBLIC)
                                                   ,new Symbol("IBM")
                                                   ,new Side(Side.BUY)
                                                   ,new TransactTime( new Date() )
                                                   ,new OrdType( OrdType.LIMIT )
                                                   );


        // required user-defined field (CME)
        // message.setField(new CorrelationClOrdID(order.getClientOrderId()));

        // market order fields
        message.setDouble(OrderQty.FIELD, 2236);
        message.setDouble(Price.FIELD, 82.83);


        if ( Session.sendToTarget(message, s ) ) {
            System.out.println("submit message sent");
        }

         /*
        Order order = new OrderBuilder()
                           .withOrdType( new OrdType(OrdType.LIMIT) )
                           .withSide( new Side(Side.BUY) )
                           .withSymbol(new Symbol("CSCO"))
                           .withOrderQty( new OrderQty(100) )
                           .withPrice( new Price(52.46) )
                           .create();
        NewOrderSingle ggmessage = order.newOrderSingle42();
        if ( Session.sendToTarget(message, s ) ) {
           System.out.println("submit message sent");
       }
       */


    }
//

    public static void sendOrderCancelRequest( SessionID s) throws Exception{

        Message message = new Message();
        // BeginString
        message.getHeader().setField(new StringField(8, "FIX.4.2"));
        // SenderCompID
        message.getHeader().setField(new StringField(49, "sensdMS"));
        // TargetCompID, with enumeration
        message.getHeader().setField(new StringField(56, "sdCH"));
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
