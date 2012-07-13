

/*
1m0z2n9x
*/



import quickfix.Session;
import java.util.ArrayList;
import quickfix.field.Symbol;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.field.*;
import quickfix.FieldNotFound;
import quickfix.SessionNotFound;
import quickfix.UnsupportedMessageType;
import quickfix.IncorrectTagValue;
import quickfix.fix42.MarketDataSnapshotFullRefresh;
import quickfix.fix42.NewOrderSingle;



import com.rabbitmq.client.Connection;
//import com.rabbitmq.client.ConnectionFactory;

import com.prc.tt.messaging.Order;
import com.prc.tt.messaging.OrderBuilder;
import com.prc.tt.messaging.rabbitmq.Publisher;
import com.prc.tt.messaging.rabbitmq.ExchangeType;
import com.prc.tt.utils.RabbitMQConnection;
import com.prc.tt.Instrument;

import java.io.FileInputStream;
import java.io.IOException;

import com.google.gson.Gson;



public class TTtestOrder {
    public static com.prc.tt.Configuration cfg =com.prc.tt.Configuration.getInstance() ;
    public static Connection conn;
    public static Gson gson =new Gson();
    public static Instrument myinstrument;

    public static String sendOrder() throws Exception{

        Order ord = new OrderBuilder()
                    .withSide(Side.BUY)
                    .withOrdType(OrdType.LIMIT)
                    .withPrice(10309)
                    .withOrderQty(4)
                    .withInstrument(myinstrument)
                    .withSourceID(cfg.getSourceID())
                    .create();

        String json = gson.toJson(ord);
        System.out.println(json);

        Publisher publisher = new Publisher(conn,"ORDERS", "ORDERS",ExchangeType.FANOUT);
        publisher.publish(json);

        return ord.getClOrdID();
    }

    public static String sendCxlReplace(String ogclordid,Publisher xpublisher) throws IOException {
        Order cxlord = new OrderBuilder()
                       .withOrigClOrdID( ogclordid )
                       .withPrice(10161)
                       .withOrderQty(2)
                       .withSide(Side.BUY)
                       .withOrdType(OrdType.LIMIT)
                       .withSourceID(cfg.getSourceID())
                       .withMsgType(MsgType.ORDER_CANCEL_REPLACE_REQUEST)
                       .withInstrument(myinstrument)
                       .create();

        String jsonocr = gson.toJson(cxlord);
        System.out.println(jsonocr);




        xpublisher.publish(jsonocr);

        return cxlord.getClOrdID();
    }


    public static void sendCxl(String cxlrpordid, Publisher xpublisher) throws IOException{
        Order zcxlord = new OrderBuilder()
                        .withOrigClOrdID(cxlrpordid)
                        .withOrdType(OrdType.LIMIT)
                        .withSourceID(cfg.getSourceID())
                        .withMsgType(MsgType.ORDER_CANCEL_REQUEST)
                        .withSecurityExchange("TTSIM")
                        .withSymbol("CL")
                        .withSecurityID("CL01110400000000NN")
                        .create();

        String jsonxxx = gson.toJson(zcxlord);
        xpublisher.publish(jsonxxx);
    }


    public static void pause(long duration) {
        // now cancel replace it
        try {
            Thread.sleep(duration);
        }
        catch ( Exception ert ) {
            //ert;
        }

    }

    public static void main(String args[]) throws Exception {

        cfg.subcribeReader(args[0]);
        cfg.setSourceID(args[1]);

        conn =RabbitMQConnection.getConnection();
        ArrayList<Instrument> intru = cfg.getInstruments();
        myinstrument = intru.get(0);



        Publisher xpublisher = new Publisher(conn,"CXL", "CXL",ExchangeType.FANOUT);

        for ( int i=0;i<4;i++ ) {
            String ordclordid = sendOrder();

            pause(3000);


            String cxlrpordid = sendCxlReplace(ordclordid,  xpublisher);

            pause(5000);

            sendCxl(cxlrpordid,  xpublisher);

            pause(2456);
        }
    }
}
