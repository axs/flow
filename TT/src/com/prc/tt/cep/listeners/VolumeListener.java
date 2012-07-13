
/*
Date:


$Id$
*/

package com.prc.tt.cep.listeners;



import java.io.IOException;
import com.rabbitmq.client.Connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.util.JSONEventRenderer;

import com.prc.tt.messaging.rabbitmq.ExchangeType;
import com.prc.tt.utils.RabbitMQConnection;
import com.prc.tt.messaging.rabbitmq.Publisher;
import com.prc.tt.utils.Reformatter;




public class VolumeListener extends Reformatter implements UpdateListener {

    private static final Log log = LogFactory.getLog(VolumeListener.class);
    private Publisher ceppublisher;
    private static Connection conn = RabbitMQConnection.getConnection();;

    public VolumeListener() {
        try {
            ceppublisher = new Publisher( conn,"CEP","volume", ExchangeType.DIRECT );
        }
        catch( IOException err ) {
            log.info(err.getStackTrace());
        }
    }


    public void update(EventBean[] newData, EventBean[] oldData) {


        try {

            for( int i=0;i<newData.length;i++ ) {

                String volume = newData[i].get("volume").toString();
                String price  = newData[i].get("price").toString();


                log.info(i +  "NEW volume=" + volume  + " price=" + price + "\n"  );


                /*
                try {
                    ceppublisher.publish( jsonRenderer.render("TradeEvent", newData[i]),"volume" );
                }
                catch( IOException ioe ) {
                    ioe.printStackTrace();
                }
                */
            }
        }
        catch( Exception err ) {
            err.printStackTrace();
        }




    }

}

