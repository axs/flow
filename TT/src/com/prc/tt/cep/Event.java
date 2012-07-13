/*
  $Id: Event.java,v 1.1 2010/10/17 22:06:42 axs Exp $
*/


package com.prc.tt.cep;



import org.joda.time.DateTime;
import com.prc.tt.utils.DateUtils;
import java.util.concurrent.atomic.AtomicInteger;




public abstract class Event{
    private static AtomicInteger counter= new AtomicInteger(1);
    protected DateTime datetime;
    protected int eventID;


    public Event() {
        this.eventID = counter.getAndIncrement();
        this.datetime = new DateTime();
    }

    public String getDateTime(){
        return datetime.toString( DateUtils.MILLIS_CHI );
    }


    public int getEventID(){
        return this.eventID;
    }

}
