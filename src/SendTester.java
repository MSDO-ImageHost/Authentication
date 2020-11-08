import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import java.util.*;

public class SendTester {
    private final static String QUEUE_NAME = "authentication";

    public static void main(String[] args) throws Exception {
        Scanner sc= new Scanner(System.in);
        System.out.print("Enter");
        int a= sc.nextInt();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            if (a == 1) {
                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                String message = "RequestLoginToken";
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
                System.out.println(" [x] Sent '" + message + "'");
            } else if (a == 2) {
                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                String message = "RequestInvalidateLoginToken";
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
                System.out.println(" [x] Sent '" + message + "'");
            } else if (a == 3) {
                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                String message = "RequestAccountCreate";
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
                System.out.println(" [x] Sent '" + message + "'");
            } else if (a == 4) {
                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                String message = "RequestAccountReset";
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
                System.out.println(" [x] Sent '" + message + "'");
            } else if (a == 5) {
                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                String message = "RequestAccountPasswordUpdate";
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
                System.out.println(" [x] Sent '" + message + "'");
            } else if (a == 6) {
                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                String message = "ConfirmAccountDeletion";
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
                System.out.println(" [x] Sent '" + message + "'");
            } else {
                System.out.println("Wrong number");
            }

        }
    }
}