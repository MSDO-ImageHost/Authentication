import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;

import java.nio.charset.StandardCharsets;

public class Receive {
    private final static String QUEUE_NAME = "authentication";
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        mySQL mySQL = new mySQL();
        mySQL.start("jdbc:mysql://172.17.0.2:3306/Login","root","1234");
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        GetResponse response = channel.basicGet(QUEUE_NAME, true);
        String message = new String(response.getBody(), StandardCharsets.UTF_8);
        System.out.println(" [x] Received '" + message + "'");
        if (message.equals("RequestLoginToken")) {
            //connect to db
            Boolean resp = mySQL.receiveUser("Thor","1234");
            //Get if possible to login
            Send sender = new Send();
            sender.ReturnAuthenticationToken(resp);//Change such token is returned
        } else if (message.equals("RequestInvalidateLoginToken")){
            //Get invalid token
            Send sender = new Send();
            sender.ConfirmInvalidateToken("token");//Placeholder token
        } else if (message.equals("RequestAccountCreate")){
            //create account in db
            Boolean resp = mySQL.createUser("Thor","1234");
            if(resp){
                //if created ok
                Send sender = new Send();
                sender.ConfirmAccountCreation();
            } else {
                //TODO
            }
            //else handle error
        } else if (message.equals("RequestAccountReset")){
            //Reset in db
            //if reset okay
            Send sender = new Send();
            sender.ConfirmSetPassword(); //Maybe?
            //else handle error
        } else if (message.equals("RequestAccountPasswordUpdate")){
            //update password in db
            Boolean resp = mySQL.changePassword("Thor","1234","WeakestAvenger");
            //if update ok
            if(resp) {
                Send sender = new Send();
                sender.ConfirmSetPassword();
            } else {
                //TODO
            }
            //else handle error
            //maybe merge PasswordUpdate and AccountReset
        } else if (message.equals("ConfirmAccountDeletion")){
            //delete in db
            Boolean resp = mySQL.deleteUser("Thor","WeakestAvenger");
            if(resp) {
                //if delete ok
                Send sender = new Send();
                sender.ConfirmAccountDeletion();
            } else {
                //TODO
            }
        }
        mySQL.stop();
    }
}