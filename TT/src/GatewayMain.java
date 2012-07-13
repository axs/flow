
import com.prc.tt.Gateway;
import com.prc.tt.Configuration;

public class GatewayMain {


    public static void main(String args[]) throws Exception {
        com.prc.tt.Configuration cf =com.prc.tt.Configuration.getInstance() ;
        cf.subcribeReader(args[0]);


        Gateway gateway = new Gateway( cf.getFIXconf() );
        gateway.setPassword( cf.getPassword() );
        gateway.start();


        while ( ! gateway.isLoggedOn() ) {
            try {
                System.out.println("Waiting for logon");
                Thread.sleep(5000);
            }
            catch ( Exception ere ) {
                ere.printStackTrace();
            }
        }


        gateway.startConsumer();
        System.in.read();
    }

}
