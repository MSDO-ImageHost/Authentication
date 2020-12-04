import com.rabbitmq.client.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class rabbitMQ {
    private static final String rapid = "rapid";
    private static Map<String, DeliverCallback> events = new HashMap<>();

    public static Channel setupChannel() throws TimeoutException, IOException, NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
        String uri = System.getenv("AMQP_URI");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(uri);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(rabbitMQ.rapid,"direct");
        return channel;
    }

    public static void send(String event, String body) throws IOException, TimeoutException, NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
        Channel channel = setupChannel();
        channel.basicPublish(rabbitMQ.rapid, event,null, body.getBytes("UTF-8"));
        channel.close();
    }

    //Bind a single event to one queue
    private static void bindQueue(String event, String queue) throws IOException, TimeoutException, NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
        Channel channel = setupChannel();
        channel.queueDeclare(queue,false,false,false,null);
        channel.queueBind(queue,rabbitMQ.rapid,event);
    }

    public static void setupReceiver(String queueName) throws TimeoutException, IOException, NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
        Channel channel = setupChannel();
        DeliverCallback callback = ((consumerTag, delivery) -> {
            DeliverCallback func = events.get(delivery.getEnvelope().getRoutingKey());
            if(func == null) {
                //TODO some error logging that method received event that does not exist?
                return;
            } else {
                func.handle(consumerTag, delivery);
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
                System.out.println(consumerTag);
            }
            System.out.println(" [x] Received event " + delivery.getEnvelope().getRoutingKey());
        });
        channel.basicConsume(queueName,callback, consumerTag -> { });
    }

    public static void addSubscription(String event,String queue, DeliverCallback func) throws IOException, TimeoutException, NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
        rabbitMQ.bindQueue(event,queue);
        events.put(event,func);
    }
}
