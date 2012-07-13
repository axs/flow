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




public class VolatilityListener extends Reformatter implements UpdateListener {
    private static final Log log = LogFactory.getLog(VolatilityListener.class);
    private Publisher ceppublisher;

    public VolatilityListener() {
        try {
            Connection conn =RabbitMQConnection.getConnection();
            ceppublisher = new Publisher( conn,"CEP","volatility", ExchangeType.DIRECT );
        }
        catch( IOException err ) {
            log.info(err.getStackTrace());
        }
    }

    public void update(EventBean[] newData, EventBean[] oldData) {
        for( int i=0;i<newData.length;i++ ) {

            String sdbid   = newData[i].get("sdbid").toString();
            String securityid   = newData[i].get("securityID").toString();

            //   String volume   = newData[i].get("volume").toString();
            //String size   = newData[i].get("size").toString();

            // String pvol   = newData[i].get("pvol").toString();


            log.info(i + " secid=" + securityid +  " sdbid=" +sdbid    );

            try {
                ceppublisher.publish( jsonRenderer.render("PriceEvent", newData[i]),"volatility" );
            }
            catch ( IOException ioe ) {
                ioe.printStackTrace();
            }

        }

    }

}

