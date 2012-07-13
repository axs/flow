

package com.prc.tt.messaging;



import com.prc.tt.messaging.MessageType;



public interface MessageListener<T> {

     public void onMessage(MessageType t, T message);
}

