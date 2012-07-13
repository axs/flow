

import com.prc.tt.Configuration;
import com.prc.tt.cep.query.Volatility;
import com.prc.tt.cep.listeners.VolatilityListener;


public class CEPtest {

    public static void main(String[] arg) {
        Configuration cfg = Configuration.getInstance();
        cfg.subcribeReader(arg[0]);
        cfg.setSourceID("cep");

        Volatility v = new Volatility();
        VolatilityListener  vlisten =new VolatilityListener();
        v.setListener( vlisten );

        v.start();
    }

}
