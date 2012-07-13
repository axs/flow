



package com.prc.tt.utils.fix.extension;

import quickfix.StringField;



public class RequestTickTable extends StringField {
    static public String YES = "Y";
    static public String NO = "N";


    public RequestTickTable() {
        super(17000);
    }

    public RequestTickTable(String data) {
        super(17000, data);
    }
}
