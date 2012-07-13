

/*
  $Id$
*/


package com.prc.tt.utils.fix.extension;



import quickfix.StringField;


public class TTUsername extends StringField {


    public TTUsername() {
        super(10553);
    }

    public TTUsername(String data) {
        super(10553, data);
    }
}
