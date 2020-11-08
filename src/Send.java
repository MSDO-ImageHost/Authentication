import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

public class Send {
    private final static String GATEWAY = "gateway";
    private final static String PLACE_HOLDER = "placeHolder";

    public void ReturnAuthenticationToken(boolean login) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(GATEWAY, false, false, false, null);
            String message = String.valueOf(login);
            channel.basicPublish("", GATEWAY, null, message.getBytes());
            System.out.println(" [x] ReturnAuthenticationToken login equals "+message+"");
        }
    }

    public void ConfirmInvalidateToken(String token) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(PLACE_HOLDER, false, false, false, null);
            channel.basicPublish("", PLACE_HOLDER, null, token.getBytes());
            System.out.println(" [x] Invalid token fetched");
        }
    }

    public void ConfirmAccountCreation() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(GATEWAY, false, false, false, null);
            String message = "ConfirmAccountCreation";
            channel.basicPublish("", GATEWAY, null, message.getBytes());
            System.out.println(" [x] Account authentication have been created");
        }
    }

    public void ConfirmSetPassword() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(GATEWAY, false, false, false, null);
            String message = "ConfirmSetPassword";
            channel.basicPublish("", GATEWAY, null, message.getBytes());
            System.out.println(" [x] Password have been set");
        }
    }

    public void ConfirmAccountDeletion() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(GATEWAY, false, false, false, null);
            String message = "ConfirmAccountDeletion";
            channel.basicPublish("", GATEWAY, null, message.getBytes());
            System.out.println(" [x] Account authentication have been deleted");
        }
    }
}