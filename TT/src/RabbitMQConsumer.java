


/*
set ddd="C:\bin\rabbitmq\rabbitmq-java-client-bin-2.3.1"
%JAVA_HOME%\bin\javac -cp %ddd%\rabbitmq-client.jar  RabbitMQConsumer.java

%JAVA_HOME%\bin\java -cp %ddd%\commons-cli-1.1.jar;%ddd%/commons-io-1.2.jar;.;%ddd%\rabbitmq-client.jar  RabbitMQConsumer
*/

import com.prc.tt.messaging.rabbitmq.Consumer;

public class RabbitMQConsumer {
    public static void main(String []args)  {
        try{
            Consumer consumer= new Consumer();
            consumer.consume();
        }
        catch(Exception err){
               err.printStackTrace();
        }
        /*
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("/");
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        Connection conn = factory.newConnection();
        Channel channel = conn.createChannel();
        String exchangeName = "crackoil";
        String queueName = "";
        String routingKey = "testRoute";
        boolean durable = false;
        channel.exchangeDeclare(exchangeName, "fanout", durable);
        channel.queueDeclare(queueName, durable,false,false,null);
        channel.queueBind(queueName, exchangeName, routingKey);
        boolean noAck = false;
        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(queueName, noAck, consumer);

        boolean runInfinite = true;

        while ( runInfinite ) {
            QueueingConsumer.Delivery delivery;
            try {
                delivery = consumer.nextDelivery();
            }
            catch ( InterruptedException ie ) {
                continue;
            }
            System.out.println("Message received"
                               + new String(delivery.getBody()));
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }
        channel.close();
        conn.close();
        */
    }
}

