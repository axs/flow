



package com.prc.tt.utils.fix.extension;

import quickfix.StringField;



public class SecurityAltID extends StringField {

    public SecurityAltID() {
        super(10455);
    }

    public SecurityAltID(String data) {
        super(10455, data);
    }
}
