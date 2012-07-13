
package com.prc.tt.utils;



import com.espertech.esper.client.util.JSONEventRenderer;



public abstract class Reformatter{
    protected JSONEventRenderer jsonRenderer;

    public void setRenderer(JSONEventRenderer  jr){
        this.jsonRenderer = jr;
    }
}
