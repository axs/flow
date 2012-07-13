
import com.prc.tt.cep.query.*;
import com.prc.tt.cep.listeners.*;
import com.prc.tt.Configuration;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.apache.commons.cli.*;

import com.espertech.esper.client.*;


public class CEPMain{

    public static void main(String args[]) {

        // create the command line parser
        CommandLineParser parser = new PosixParser();

        // create the Options
        Options options = new Options();
        options.addOption( "h", "help",false, "help ." );
        options.addOption( "s", "springconf", true, "required spring config ." );
        options.addOption( "c", "subscriptionConf", true, "required instrument config." );
        options.addOption( "q", "query", true, "required esper query." );
        options.addOption( "i", "sourceid", true, "sourceid required esper query." );

        HelpFormatter formatter = new HelpFormatter();

        String springconf = null;
        String subscriptionConf     = null;
        String query      = null;
        String sourceid   = null;

        try {
            CommandLine line = parser.parse( options, args );

            if( line.hasOption( "help" ) ) {
                formatter.printHelp( "java -cp  \"%CLASSPATH%\" SpringMain  -h", options );
                System.exit(-1);
            }

            springconf = line.getOptionValue( "springconf" );
            subscriptionConf = line.getOptionValue( "subscriptionConf" );
            query = line.getOptionValue( "query" );
            sourceid = line.getOptionValue( "sourceid" );

            if( springconf == null || query == null || subscriptionConf == null || sourceid == null ){
                formatter.printHelp( "java -cp  \"%CLASSPATH%\" SpringMain  -h", options );
                System.exit(-1);
            }

        }
        catch( ParseException exp ) {
            formatter.printHelp( "java -cp  \"%CLASSPATH%\" SpringMain -h", options );
            System.out.println( "Unexpected exception:" + exp.getMessage() );
            System.exit(-1);
        }

        Configuration cfg = Configuration.getInstance();
        cfg.subcribeReader(subscriptionConf);
        cfg.setSourceID(sourceid);


        ApplicationContext ctx = new ClassPathXmlApplicationContext( springconf );

        Query qry = (Query) ctx.getBean( query );
        qry.start();
     }


    /*
        public static void main(String[] arg) {
        Configuration cfg = Configuration.getInstance();
        cfg.subcribeReader(arg[0]);
        cfg.setSourceID("cep");

        Volatility v = new Volatility();
        VolatilityListener  vlisten =new VolatilityListener();
        v.setListener( vlisten );

        v.start();
    }

    */

}


