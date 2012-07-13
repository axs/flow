



import com.prc.tt.algo.QuickQuote;

import quickfix.field.Side;



public class QuickQuoteMain{

    public static void main(String[] gf){
        /*
        QuickQuote bmm = new QuickQuote();
        bmm.setSide(Side.BUY);
        bmm.start("fredbuy","C:/work/TT/conf/subscribe/tt.xml");
        */


        QuickQuote smm = new QuickQuote();
        smm.setSide(Side.SELL);
        smm.start("fredsell","C:/work/TT/conf/subscribe/tt.xml");
    }

}
