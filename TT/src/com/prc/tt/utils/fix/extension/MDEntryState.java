




package com.prc.tt.utils.fix.extension;

import quickfix.IntField;



public class MDEntryState extends IntField {

    final public  static int NONE =0;
    final public  static int PUBLIC_WORKUP_AGGRESSOR_HIT =1;
    final public  static int PUBLIC_WORKUP_AGGRESSOR_TAKE =2;
    final public  static int PRIVATE_WORKUP_AGGRESSOR_HIT =3;
    final public  static int PRIVATE_WORKUP_AGGRESSOR_TAKE =4;


    public MDEntryState() {
        super(16486);
    }

    public MDEntryState(int data) {
        super(16486, data);
    }
}
