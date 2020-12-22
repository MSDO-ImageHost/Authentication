import com.rabbitmq.client.AMQP;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Main {

    public static void main(String[] args) throws TimeoutException, IOException {
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
                    if (sendBody == null){
                        rabbitMQ.send("ReturnAuthenticationToken", "", prop);
                    } else {
                        rabbitMQ.send("ReturnAuthenticationToken", sendBody.toJSONString(), prop);
                    }
                } catch (TimeoutException | ParseException e) {
                    JSONObject body;
                    if (e.getMessage() == null) {
                        body = Events.CreateResponseJson(null, 400, "Malformed request syntax");
                    } else {
                        body = Events.CreateResponseJson(null, 400, e.getMessage());
                    }
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    System.out.println(body.toJSONString());
                    //JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    try {
                        rabbitMQ.send("ReturnAuthenticationToken", "", prop);
                    } catch (TimeoutException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                } catch (SQLException throwables) {
                    String sqlError = throwables.getSQLState();
                    if (sqlError.equals("08S01") || sqlError.equals("S1009")){
                        JSONObject body = Events.CreateResponseJson(null, 503, "SQL connection failed");
                        AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                        try {
                            rabbitMQ.send("ReturnAuthenticationToken", "", prop);
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                        System.exit(-1);
                    } else {
                        JSONObject body = Events.CreateResponseJson(null, 400, "Wrong username or password");
                        AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                        try {
                            rabbitMQ.send("ReturnAuthenticationToken", "", prop);
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
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
                    if (sendBody == null){
                        rabbitMQ.send("ConfirmAccountCreation", "", prop);
                    } else {
                        rabbitMQ.send("ConfirmAccountCreation", sendBody.toJSONString(), prop);
                    }
                } catch (ParseException | TimeoutException e) {
                    JSONObject body;
                    if (e.getMessage() == null) {
                        body = Events.CreateResponseJson(null, 400, "Malformed request syntax");
                    } else {
                        body = Events.CreateResponseJson(null, 400, e.getMessage());
                    }
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    //JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    try {
                        rabbitMQ.send("ConfirmAccountCreation", "", prop);
                    } catch (TimeoutException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                } catch (SQLException throwables) {
                    String sqlError = throwables.getSQLState();
                    if (sqlError.equals("08S01") || sqlError.equals("S1009")){
                        JSONObject body = Events.CreateResponseJson(null, 503, "SQL connection failed");
                        AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                        try {
                            rabbitMQ.send("ConfirmAccountCreation", "", prop);
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                        System.exit(-1);
                    } else {
                        JSONObject body = Events.CreateResponseJson(null, 400, "User could not be created");
                        AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                        try {
                            rabbitMQ.send("ConfirmAccountCreation", "", prop);
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
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
                    if (sendBody == null){
                        rabbitMQ.send("ConfirmInvalidateToken", "", prop);
                    } else {
                        rabbitMQ.send("ConfirmInvalidateToken", sendBody.toJSONString(), prop);
                    }
                } catch (TimeoutException | ParseException e) {
                    JSONObject body;
                    if (e.getMessage() == null) {
                        body = Events.CreateResponseJson(null, 400, "Malformed request syntax");
                    } else {
                        body = Events.CreateResponseJson(null, 400, e.getMessage());
                    }
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    //JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    try {
                        rabbitMQ.send("ConfirmInvalidateToken", "", prop);
                    } catch (TimeoutException timeoutException) {
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
                    if (sendBody == null){
                        rabbitMQ.send("ConfirmAccountReset", "", prop);
                    } else {
                        rabbitMQ.send("ConfirmAccountReset", sendBody.toJSONString(), prop);
                    }
                } catch (TimeoutException | ParseException e) {
                    JSONObject body;
                    if (e.getMessage() == null) {
                        body = Events.CreateResponseJson(null, 400, "Malformed request syntax");
                    } else {
                        body = Events.CreateResponseJson(null, 400, e.getMessage());
                    }
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    //JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    try {
                        rabbitMQ.send("ConfirmAccountReset", "", prop);
                    } catch (TimeoutException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
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
                    if (sendBody == null){
                        rabbitMQ.send("ConfirmSetPassword", "", prop);
                    } else {
                        rabbitMQ.send("ConfirmSetPassword", sendBody.toJSONString(), prop);
                    }
                } catch (TimeoutException | ParseException e) {
                    JSONObject body;
                    if (e.getMessage() == null) {
                        body = Events.CreateResponseJson(null, 400, "Malformed request syntax");
                    } else {
                        body = Events.CreateResponseJson(null, 400, e.getMessage());
                    }
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    System.out.println(body.toJSONString());
                    //JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    try {
                        rabbitMQ.send("ConfirmSetPassword", "", prop);
                    } catch (TimeoutException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                } catch (SQLException throwables) {
                    String sqlError = throwables.getSQLState();
                    if (sqlError.equals("08S01") || sqlError.equals("S1009")){
                        JSONObject body = Events.CreateResponseJson(null, 503, "SQL connection failed");
                        AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                        try {
                            rabbitMQ.send("ConfirmSetPassword", "", prop);
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                        System.exit(-1);
                    } else {
                        JSONObject body = Events.CreateResponseJson(null, 400, "Password could not be updated");
                        AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                        try {
                            rabbitMQ.send("ConfirmSetPassword", "", prop);
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
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
                    if (sendBody == null){
                        rabbitMQ.send("ConfirmAccountDeletion", "", prop);
                    } else {
                        rabbitMQ.send("ConfirmAccountDeletion", sendBody.toJSONString(), prop);
                    }
                } catch (TimeoutException | ParseException e) {
                    JSONObject body;
                    if (e.getMessage() == null) {
                        body = Events.CreateResponseJson(null, 400, "Malformed request syntax");
                    } else {
                        body = Events.CreateResponseJson(null, 400, e.getMessage());
                    }
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    //JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    try {
                        rabbitMQ.send("ConfirmAccountDeletion", "", prop);
                    } catch (TimeoutException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                } catch (SQLException throwables) {
                    String sqlError = throwables.getSQLState();
                    if (sqlError.equals("08S01") || sqlError.equals("S1009")){
                        JSONObject body = Events.CreateResponseJson(null, 503, "SQL connection failed");
                        AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                        try {
                            rabbitMQ.send("ConfirmAccountDeletion", "", prop);
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                        System.exit(-1);
                    } else {
                        JSONObject body = Events.CreateResponseJson(null, 400, "User could not be deleted");
                        AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                        try {
                            rabbitMQ.send("ConfirmAccountDeletion", "", prop);
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
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
                    if (sendBody == null){
                        rabbitMQ.send("ConfirmAccountUpdate", "", prop);
                    } else {
                        rabbitMQ.send("ConfirmAccountUpdate", sendBody.toJSONString(), prop);
                    }
                } catch (TimeoutException | ParseException e) {
                    JSONObject body;
                    if (e.getMessage() == null) {
                        body = Events.CreateResponseJson(null, 400, "Malformed request syntax");
                    } else {
                        body = Events.CreateResponseJson(null, 400, e.getMessage());
                    }
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    //JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    try {
                        rabbitMQ.send("ConfirmAccountUpdate", "", prop);
                    } catch (TimeoutException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                } catch (SQLException throwables) {
                    String sqlError = throwables.getSQLState();
                    if (sqlError.equals("08S01") || sqlError.equals("S1009")){
                        JSONObject body = Events.CreateResponseJson(null, 503, "SQL connection failed");
                        AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                        try {
                            rabbitMQ.send("ConfirmAccountUpdate", "", prop);
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                        System.exit(-1);
                    } else {
                        JSONObject body = Events.CreateResponseJson(null, 400, "Role could not be updated for user");
                        AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                        try {
                            rabbitMQ.send("ConfirmAccountUpdate", "", prop);
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            rabbitMQ.addSubscription("RequestAccountData","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                String correlationID = delivery.getProperties().getCorrelationId();
                String contentType = delivery.getProperties().getContentType();
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.RequestAccountData(json);
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    System.out.println("sendbody for request account data: "+sendBody.toString());
                    System.out.println(sendBody);
                    if (sendBody == null){
                        rabbitMQ.send("ReturnAccountInfo", "", prop);
                    } else {
                        rabbitMQ.send("ReturnAccountInfo", sendBody.toJSONString(), prop);
                    }
                } catch (TimeoutException | ParseException e) {
                    JSONObject body;
                    if (e.getMessage() == null) {
                        body = Events.CreateResponseJson(null, 400, "Malformed request syntax");
                    } else {
                        body = Events.CreateResponseJson(null, 400, e.getMessage());
                    }
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    //JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    try {
                        rabbitMQ.send("ReturnAccountInfo", "", prop);
                    } catch (TimeoutException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                } catch (SQLException throwables) {
                    String sqlError = throwables.getSQLState();
                    if (sqlError.equals("08S01") || sqlError.equals("S1009")){
                        JSONObject body = Events.CreateResponseJson(null, 503, "SQL connection failed");
                        AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                        try {
                            rabbitMQ.send("ReturnAccountInfo", "", prop);
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                        System.exit(-1);
                    } else {
                        JSONObject body = Events.CreateResponseJson(null, 400, "Account info could not be returned");
                        AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                        try {
                            rabbitMQ.send("ReturnAccountInfo", "", prop);
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
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
                    if (sendBody == null){
                        rabbitMQ.send("ConfirmAccountUpdate", "", prop);
                    } else {
                        rabbitMQ.send("ConfirmAccountUpdate", sendBody.toJSONString(), prop);
                    }
                } catch (TimeoutException | ParseException e) {
                    JSONObject body;
                    if (e.getMessage() == null) {
                        body = Events.CreateResponseJson(null, 400, "Malformed request syntax");
                    } else {
                        body = Events.CreateResponseJson(null, 400, e.getMessage());
                    }
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    //JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    try {
                        rabbitMQ.send("ConfirmAccountUpdate", "", prop);
                    } catch (TimeoutException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                } catch (SQLException throwables) {
                    String sqlError = throwables.getSQLState();
                    if (sqlError.equals("08S01") || sqlError.equals("S1009")){
                        JSONObject body = Events.CreateResponseJson(null, 503, "SQL connection failed");
                        AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                        try {
                            rabbitMQ.send("ConfirmAccountUpdate", "", prop);
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                        System.exit(-1);
                    } else {
                        JSONObject body = Events.CreateResponseJson(null, 400, "Account info could not be updated");
                        AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                        try {
                            rabbitMQ.send("ConfirmAccountUpdate", "", prop);
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
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
                    if (sendBody == null){
                        rabbitMQ.send("ConfirmBanUser", "", prop);
                    } else {
                        rabbitMQ.send("ConfirmBanUser", sendBody.toJSONString(), prop);
                    }
                } catch (TimeoutException | ParseException e) {
                    JSONObject body;
                    if (e.getMessage() == null) {
                        body = Events.CreateResponseJson(null, 400, "Malformed request syntax");
                    } else {
                        body = Events.CreateResponseJson(null, 400, e.getMessage());
                    }
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    //JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    try {
                        rabbitMQ.send("ConfirmBanUser", "", prop);
                    } catch (TimeoutException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                } catch (SQLException throwables) {
                    String sqlError = throwables.getSQLState();
                    if (sqlError.equals("08S01") || sqlError.equals("S1009")){
                        JSONObject body = Events.CreateResponseJson(null, 503, "SQL connection failed");
                        AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                        try {
                            rabbitMQ.send("ConfirmBanUser", "", prop);
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                        System.exit(-1);
                    } else {
                        JSONObject body = Events.CreateResponseJson(null, 400, "User could not be banned");
                        AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                        try {
                            rabbitMQ.send("ConfirmBanUser", "", prop);
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
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
                    if(sendBody == null){
                        rabbitMQ.send("ConfirmFlagUser", "", prop);
                    } else {
                        rabbitMQ.send("ConfirmFlagUser", sendBody.toJSONString(), prop);
                    }
                } catch (TimeoutException | ParseException e) {
                    JSONObject body;
                    if (e.getMessage() == null) {
                        body = Events.CreateResponseJson(null, 400, "Malformed request syntax");
                    } else {
                        body = Events.CreateResponseJson(null, 400, e.getMessage());
                    }
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    //JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    try {
                        rabbitMQ.send("ConfirmFlagUser", "", prop);
                    } catch (TimeoutException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                } catch (SQLException throwables) {
                    String sqlError = throwables.getSQLState();
                    if (sqlError.equals("08S01") || sqlError.equals("S1009")){
                        JSONObject body = Events.CreateResponseJson(null, 503, "SQL connection failed");
                        AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                        try {
                            rabbitMQ.send("ConfirmFlagUser", "", prop);
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                        System.exit(-1);
                    } else {
                        JSONObject body = Events.CreateResponseJson(null, 400, "User could not be flagged");
                        AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                        try {
                            rabbitMQ.send("ConfirmFlagUser", "", prop);
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
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
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.RequestAllFlagged(json, token);
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    System.out.println(body.toJSONString());
                    JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    if(sendBody == null){
                        rabbitMQ.send("ReturnAllFlagged", "", prop);
                    } else {
                        rabbitMQ.send("ReturnAllFlagged", sendBody.toJSONString(), prop);
                    }
                } catch (TimeoutException | ParseException e) {
                    JSONObject body;
                    if (e.getMessage() == null) {
                        body = Events.CreateResponseJson(null, 400, "Malformed request syntax");
                    } else {
                        body = Events.CreateResponseJson(null, 400, e.getMessage());
                    }
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    //JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    try {
                        rabbitMQ.send("ReturnAllFlagged", "", prop);
                    } catch (TimeoutException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                } catch (SQLException throwables) {
                    String sqlError = throwables.getSQLState();
                    if (sqlError.equals("08S01") || sqlError.equals("S1009")){
                        JSONObject body = Events.CreateResponseJson(null, 503, "SQL connection failed");
                        AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                        try {
                            rabbitMQ.send("ReturnAllFlagged", "", prop);
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                        System.exit(-1);
                    } else {
                        JSONObject body = Events.CreateResponseJson(null, 400, "Could not get flagged users");
                        AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                        try {
                            rabbitMQ.send("ReturnAllFlagged", "", prop);
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            rabbitMQ.addSubscription("RequestUsername","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                String correlationID = delivery.getProperties().getCorrelationId();
                String contentType = delivery.getProperties().getContentType();
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.RequestUsername(json);
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    if (sendBody == null){
                        rabbitMQ.send("ReturnUsername", "", prop);
                    } else {
                        rabbitMQ.send("ReturnUsername", sendBody.toJSONString(), prop);
                    }
                } catch (TimeoutException | ParseException e) {
                    JSONObject body;
                    if (e.getMessage() == null) {
                        body = Events.CreateResponseJson(null, 400, "Malformed request syntax");
                    } else {
                        body = Events.CreateResponseJson(null, 400, e.getMessage());
                    }
                    AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                    //System.out.println(body.toJSONString());
                    //JSONObject sendBody = rabbitMQ.makeSendBody(body);
                    try {
                        rabbitMQ.send("ReturnUsername", "", prop);
                    } catch (TimeoutException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                } catch (SQLException throwables) {
                    String sqlError = throwables.getSQLState();
                    if (sqlError.equals("08S01") || sqlError.equals("S1009")){
                        JSONObject body = Events.CreateResponseJson(null, 503, "SQL connection failed");
                        AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                        try {
                            rabbitMQ.send("ReturnUsername", "", prop);
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                        System.exit(-1);
                    } else {
                        JSONObject body = Events.CreateResponseJson(null, 400, "Username could not be returned");
                        AMQP.BasicProperties prop = rabbitMQ.makeProps(body, correlationID, contentType);
                        try {
                            rabbitMQ.send("ReturnUsername", "", prop);
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            rabbitMQ.setupReceiver("Authentication");
        } catch (IOException e) {
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
