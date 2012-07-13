
package com.prc.tt.messaging;


/*
this is how we know which client sent this order to the PRCTTgateway
*/

public abstract class Message{
    private String sourceid;

    public String getSourceID(){
        return sourceid;
    }
    public void setSourceID(String sid){
       sourceid=sid;
    }
}
