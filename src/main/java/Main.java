import com.rabbitmq.client.AMQP;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Main {

    public static void main(String[] args) throws URISyntaxException, KeyManagementException, TimeoutException, NoSuchAlgorithmException, IOException {
        ping("rabbitmq");
        ping("mysql");
        String mySQLURI = "jdbc:mysql://" + System.getenv("MYSQL_HOST") + ":" + System.getenv("MYSQL_PORT") + "/" + System.getenv("MYSQL_DB");
        mySQL.start(mySQLURI,System.getenv("MYSQL_USER"), System.getenv("MYSQL_ROOT_PASSWORD"));
        rabbitMQ.setupRabbit();
        try {
            rabbitMQ.addSubscription("RequestLoginToken","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                String correlationID = delivery.getProperties().getCorrelationId();
                String contentType = delivery.getProperties().getContentType();
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.RequestLoginToken(json);
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    System.out.println(body.toJSONString());
                    JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    rabbitMQ.send("ReturnAuthenticationToken", sendBody.toJSONString(), prop);
                } catch (Exception e) {
                    JSONObject body;
                    if (e.getMessage() == null) {
                        body = Events.CreateResponseJson(null, 400, "Malformed request syntax");
                    } else {
                        body = Events.CreateResponseJson(null, 400, e.getMessage());
                    }
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    System.out.println(body.toJSONString());
                    JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    try {
                        rabbitMQ.send("ReturnAuthenticationToken", sendBody.toJSONString(), prop);
                    } catch (TimeoutException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                }
            });
            rabbitMQ.addSubscription("RequestAccountCreate","Authentication",(consumerTag, delivery) -> {
                System.out.println("RequestAccountCreate");
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                String correlationID = delivery.getProperties().getCorrelationId();
                String contentType = delivery.getProperties().getContentType();
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.RequestAccountCreate(json);
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    rabbitMQ.send("ConfirmAccountCreation", sendBody.toJSONString(), prop);
                } catch (Exception e) {
                    JSONObject body;
                    if (e.getMessage() == null) {
                        body = Events.CreateResponseJson(null, 400, "Malformed request syntax");
                    } else {
                        body = Events.CreateResponseJson(null, 400, e.getMessage());
                    }
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    try {
                        rabbitMQ.send("ConfirmAccountCreation", sendBody.toJSONString(), prop);
                    } catch (TimeoutException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                }
            });
            rabbitMQ.addSubscription("RequestInvalidateLoginToken","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                String correlationID = delivery.getProperties().getCorrelationId();
                String contentType = delivery.getProperties().getContentType();
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.RequestInvalidateLoginToken(json);
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    rabbitMQ.send("ConfirmInvalidateToken", sendBody.toJSONString(), prop);
                } catch (Exception e) {
                    JSONObject body;
                    if (e.getMessage() == null) {
                        body = Events.CreateResponseJson(null, 400, "Malformed request syntax");
                    } else {
                        body = Events.CreateResponseJson(null, 400, e.getMessage());
                    }
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    try {
                        rabbitMQ.send("ConfirmInvalidateToken", sendBody.toJSONString(), prop);
                    } catch (TimeoutException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                }
            });
            rabbitMQ.addSubscription("RequestAccountReset","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                String correlationID = delivery.getProperties().getCorrelationId();
                String contentType = delivery.getProperties().getContentType();
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.ConfirmAccountReset(json);
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    rabbitMQ.send("ConfirmAccountReset", sendBody.toJSONString(), prop);
                } catch (Exception e) {
                    JSONObject body;
                    if (e.getMessage() == null) {
                        body = Events.CreateResponseJson(null, 400, "Malformed request syntax");
                    } else {
                        body = Events.CreateResponseJson(null, 400, e.getMessage());
                    }
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    try {
                        rabbitMQ.send("ConfirmAccountReset", sendBody.toJSONString(), prop);
                    } catch (TimeoutException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                }
            });
            rabbitMQ.addSubscription("RequestAccountPasswordUpdate","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                String correlationID = delivery.getProperties().getCorrelationId();
                String contentType = delivery.getProperties().getContentType();
                String token = String.valueOf(delivery.getProperties().getHeaders().get("jwt"));
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.RequestAccountPasswordUpdate(json, token);
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    System.out.println(body.toJSONString());
                    JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    rabbitMQ.send("ConfirmSetPassword", sendBody.toJSONString(), prop);
                } catch (Exception e) {
                    JSONObject body;
                    if (e.getMessage() == null) {
                        body = Events.CreateResponseJson(null, 400, "Malformed request syntax");
                    } else {
                        body = Events.CreateResponseJson(null, 400, e.getMessage());
                    }
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    System.out.println(body.toJSONString());
                    JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    try {
                        rabbitMQ.send("ConfirmSetPassword", sendBody.toJSONString(), prop);
                    } catch (TimeoutException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                }
            });
            rabbitMQ.addSubscription("RequestAccountDelete","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                String correlationID = delivery.getProperties().getCorrelationId();
                String contentType = delivery.getProperties().getContentType();
                String token = String.valueOf(delivery.getProperties().getHeaders().get("jwt"));
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.RequestAccountDelete(json, token);
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    rabbitMQ.send("ConfirmAccountDeletion", sendBody.toJSONString(), prop);
                } catch (Exception e) {
                    JSONObject body;
                    if (e.getMessage() == null) {
                        body = Events.CreateResponseJson(null, 400, "Malformed request syntax");
                    } else {
                        body = Events.CreateResponseJson(null, 400, e.getMessage());
                    }
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    try {
                        rabbitMQ.send("ConfirmAccountDeletion", sendBody.toJSONString(), prop);
                    } catch (TimeoutException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                }
            });
            rabbitMQ.addSubscription("UpdateAccountPrivileges","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                String correlationID = delivery.getProperties().getCorrelationId();
                String contentType = delivery.getProperties().getContentType();
                String token = String.valueOf(delivery.getProperties().getHeaders().get("jwt"));
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.UpdateAccountPrivileges(json, token);
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    rabbitMQ.send("ConfirmAccountUpdate", sendBody.toJSONString(), prop);
                } catch (Exception e) {
                    JSONObject body;
                    if (e.getMessage() == null) {
                        body = Events.CreateResponseJson(null, 400, "Malformed request syntax");
                    } else {
                        body = Events.CreateResponseJson(null, 400, e.getMessage());
                    }
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    try {
                        rabbitMQ.send("ConfirmAccountUpdate", sendBody.toJSONString(), prop);
                    } catch (TimeoutException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                }
            });
            rabbitMQ.addSubscription("RequestAccountData","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                String correlationID = delivery.getProperties().getCorrelationId();
                String contentType = delivery.getProperties().getContentType();
                String token = String.valueOf(delivery.getProperties().getHeaders().get("jwt"));
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.RequestAccountData(json, token);
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    rabbitMQ.send("ReturnAccountInfo", sendBody.toJSONString(), prop);
                } catch (Exception e) {
                    JSONObject body;
                    if (e.getMessage() == null) {
                        body = Events.CreateResponseJson(null, 400, "Malformed request syntax");
                    } else {
                        body = Events.CreateResponseJson(null, 400, e.getMessage());
                    }
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    try {
                        rabbitMQ.send("ReturnAccountInfo", sendBody.toJSONString(), prop);
                    } catch (TimeoutException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                }
            });
            rabbitMQ.addSubscription("UpdateAccount","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                String correlationID = delivery.getProperties().getCorrelationId();
                String contentType = delivery.getProperties().getContentType();
                String token = String.valueOf(delivery.getProperties().getHeaders().get("jwt"));
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.UpdateAccount(json, token);
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    rabbitMQ.send("ConfirmAccountUpdate", sendBody.toJSONString(), prop);
                } catch (Exception e) {
                    JSONObject body;
                    if (e.getMessage() == null) {
                        body = Events.CreateResponseJson(null, 400, "Malformed request syntax");
                    } else {
                        body = Events.CreateResponseJson(null, 400, e.getMessage());
                    }
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    try {
                        rabbitMQ.send("ConfirmAccountUpdate", sendBody.toJSONString(), prop);
                    } catch (TimeoutException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                }
            });
            rabbitMQ.addSubscription("RequestBanUser","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                String correlationID = delivery.getProperties().getCorrelationId();
                String contentType = delivery.getProperties().getContentType();
                String token = String.valueOf(delivery.getProperties().getHeaders().get("jwt"));
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.RequestBanUser(json, token);
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    rabbitMQ.send("ConfirmBanUser", sendBody.toJSONString(), prop);
                } catch (Exception e) {
                    JSONObject body;
                    if (e.getMessage() == null) {
                        body = Events.CreateResponseJson(null, 400, "Malformed request syntax");
                    } else {
                        body = Events.CreateResponseJson(null, 400, e.getMessage());
                    }
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    try {
                        rabbitMQ.send("ConfirmBanUser", sendBody.toJSONString(), prop);
                    } catch (TimeoutException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                }
            });
            rabbitMQ.addSubscription("RequestFlagUser","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                String correlationID = delivery.getProperties().getCorrelationId();
                String contentType = delivery.getProperties().getContentType();
                String token = String.valueOf(delivery.getProperties().getHeaders().get("jwt"));
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.RequestFlagUser(json, token);
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    rabbitMQ.send("ConfirmFlagUser", sendBody.toJSONString(), prop);
                } catch (Exception e) {
                    JSONObject body;
                    if (e.getMessage() == null) {
                        body = Events.CreateResponseJson(null, 400, "Malformed request syntax");
                    } else {
                        body = Events.CreateResponseJson(null, 400, e.getMessage());
                    }
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    try {
                        rabbitMQ.send("ConfirmFlagUser", sendBody.toJSONString(), prop);
                    } catch (TimeoutException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                }
            });
            rabbitMQ.addSubscription("RequestAllFlagged","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                String correlationID = delivery.getProperties().getCorrelationId();
                String contentType = delivery.getProperties().getContentType();
                String token = String.valueOf(delivery.getProperties().getHeaders().get("jwt"));
                try {
                    System.out.println("beginning of try");
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.RequestAllFlagged(json, token);
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    System.out.println(body.toJSONString());
                    JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    System.out.println("right before send");
                    rabbitMQ.send("ReturnAllFlagged", sendBody.toJSONString(), prop);
                } catch (Exception e) {
                    JSONObject body;
                    if (e.getMessage() == null) {
                        body = Events.CreateResponseJson(null, 400, "Malformed request syntax");
                    } else {
                        body = Events.CreateResponseJson(null, 400, e.getMessage());
                    }
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    System.out.println(body.toJSONString());
                    JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    try {
                        rabbitMQ.send("ReturnAllFlagged", sendBody.toJSONString(), prop);
                    } catch (TimeoutException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                }
            });
            rabbitMQ.setupReceiver("Authentication");
        } catch (IOException | TimeoutException | URISyntaxException | KeyManagementException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static void ping(String url){
        try {
            InetAddress geek = InetAddress.getByName(url);
            if(geek.isReachable(5000)) System.out.println("Host reached!");
        } catch (IOException e) {
            System.out.println("Sorry ! We can't reach to this host");
        }
    }

}
