/*
Date:

$Id$
*/

package com.prc.tt.cep.listeners;


import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.util.JSONEventRenderer;

import com.prc.tt.messaging.rabbitmq.ExchangeType;
import com.prc.tt.utils.RabbitMQConnection;
import com.prc.tt.messaging.rabbitmq.Publisher;
import com.prc.tt.utils.Reformatter;




public class KeyReversalListener implements UpdateListener {
    private static final Log log = LogFactory.getLog(KeyReversalListener.class);


    public void update(EventBean[] newData, EventBean[] oldData) {
        try {

            String symbol   = newData[0].get("securityID").toString();
            String fopen   = newData[0].get("fopen").toString();
            String fhigh   = newData[0].get("fhigh").toString();
            String flow   = newData[0].get("flow").toString();
            String fclose   = newData[0].get("fclose").toString();

            String lopen   = newData[0].get("lopen").toString();
            String lhigh   = newData[0].get("lhigh").toString();
            String llow   = newData[0].get("llow").toString();
            String lclose   = newData[0].get("lclose").toString();

            if( Double.parseDouble(llow) < Double.parseDouble(flow)
                && Double.parseDouble(lopen) < Double.parseDouble(lclose)
              ) {
                log.info("Key UP " + symbol);
            }
            else if( Double.parseDouble(lhigh) > Double.parseDouble(fhigh)
                     && Double.parseDouble(lopen) > Double.parseDouble(lclose)
                   ) {
                log.info("Key DOWN " + symbol);
            }

            log.info(symbol  +
                     " \nFIRST~ O=" + fopen +
                     " H=" + fhigh +
                     " L=" + flow +
                     " C=" + fclose +

                     " \nLAST~ O=" + lopen +
                     " H=" + lhigh +
                     " L=" + llow +
                     " C=" + lclose

                    );
        }
        catch( Exception ecf ) {
            log.error(ecf.getStackTrace());
        }

    }

}

