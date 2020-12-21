import com.rabbitmq.client.*;
import org.json.simple.JSONObject;

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
    private static Connection connection;

    public static void setupRabbit() throws IOException, TimeoutException {
        String uri =  "amqp://"+System.getenv("RABBITMQ_USER") + ":"  + System.getenv("RABBITMQ_PASS") + "@" + System.getenv("RABBITMQ_HOST");
        ConnectionFactory factory = new ConnectionFactory();
        try {
            factory.setUri(uri);
        } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
            System.out.println("rabbitMQ connection failed! Cause: " + e.getCause().getMessage() + "\nHas it finished starting?");
            e.printStackTrace(System.err);
        }
        connection = factory.newConnection();
    }

    private static Channel setupChannel() throws IOException {
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(rabbitMQ.rapid,"direct");
        return channel;
    }

    public static void send(String event, String body, AMQP.BasicProperties props) throws IOException, TimeoutException {
        Channel channel = setupChannel();
        channel.basicPublish(rabbitMQ.rapid, event, props, body.getBytes("UTF-8"));
        channel.close();
    }

    public static AMQP.BasicProperties makeProps(JSONObject body, String correlationID, String contentType){
        Map<String, Object> header = new HashMap<>();
        header.put("status_code", body.get("status_code"));
        header.put("status_message", body.get("message"));
        header.put("processing_time_ns", null);
        AMQP.BasicProperties prop = new AMQP.BasicProperties()
                .builder()
                .correlationId(correlationID)
                .contentType(contentType)
                .headers(header)
                .build();
        return prop;
    }

    public static JSONObject makeSendBody(JSONObject body){
        JSONObject sendBody;
        if (body != null){
            sendBody = (JSONObject) body.get("data");
        } else {
            sendBody = body;
        }
        return sendBody;
    }

    //Bind a single event to one queue
    private static void bindQueue(String event, String queue) throws IOException {
        Channel channel = setupChannel();
        channel.queueDeclare(queue,false,false,false,null);
        channel.queueBind(queue,rabbitMQ.rapid,event);
    }

    public static void setupReceiver(String queueName) throws IOException {
        Channel channel = setupChannel();
        DeliverCallback callback = ((consumerTag, delivery) -> {
            DeliverCallback func = events.get(delivery.getEnvelope().getRoutingKey());
            if(func == null) {
                //TODO some error logging that method received event that does not exist?
                return;
            } else {
                func.handle(consumerTag, delivery);
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
            }
            System.out.println(" [x] Received event " + delivery.getEnvelope().getRoutingKey());
        });
        channel.basicConsume(queueName,callback, consumerTag -> { });
    }

    public static void addSubscription(String event,String queue, DeliverCallback func) throws IOException {
        rabbitMQ.bindQueue(event,queue);
        events.put(event,func);
    }
}
