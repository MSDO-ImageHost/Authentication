import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Main {

    public static void main(String[] args) {
        mySQL.start("jdbc:mysql://localhost:3306/authentication","root","1234");
        try {
            rabbitMQ.addSubscription("RequestLoginToken","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.RequestLoginToken(json);
                    rabbitMQ.send("ReturnAuthenticationToken", body.toJSONString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            rabbitMQ.addSubscription("RequestAccountCreate","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    System.out.println("Received account create");
                    JSONObject body = Events.RequestAccountCreate(json);
                    rabbitMQ.send("ConfirmAccountCreation", body.toJSONString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            rabbitMQ.addSubscription("RequestInvalidateLoginToken","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.RequestInvalidateLoginToken(json);
                    rabbitMQ.send("ConfirmInvalidateToken", body.toJSONString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            rabbitMQ.addSubscription("RequestAccountReset","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.ConfirmAccountReset(json);
                    rabbitMQ.send("ConfirmAccountReset", body.toJSONString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            rabbitMQ.addSubscription("RequestAccountPasswordUpdate","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.RequestAccountPasswordUpdate(json);
                    rabbitMQ.send("ConfirmSetPassword", body.toJSONString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            rabbitMQ.addSubscription("RequestAccountDelete","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.RequestAccountDelete(json);
                    rabbitMQ.send("ConfirmAccountDeletion", body.toJSONString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            rabbitMQ.addSubscription("UpdateAccountPrivileges","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.UpdateAccountPrivileges(json);
                    rabbitMQ.send("ConfirmAccountUpdate", body.toJSONString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            rabbitMQ.addSubscription("RequestAccountData","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.RequestAccountData(json);
                    rabbitMQ.send("ReturnAccountInfo", body.toJSONString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            rabbitMQ.addSubscription("UpdateAccount","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.UpdateAccount(json);
                    rabbitMQ.send("ConfirmAccountUpdate", body.toJSONString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            rabbitMQ.setupReceiver("Authentication");
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }

    }

}
