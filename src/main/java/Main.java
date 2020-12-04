import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Main {

    private static String JWT = null;

    public static void main(String[] args) throws URISyntaxException, KeyManagementException, TimeoutException, NoSuchAlgorithmException, IOException {
        ping("rabbitmq");
        ping("mysql");
        mySQL.start("jdbc:mysql://mysql:3306/","root", System.getenv("MYSQL_PASSWORD"));
        try {
            rabbitMQ.addSubscription("RequestLoginToken","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.RequestLoginToken(json);
                    System.out.println(body.toJSONString());
                    rabbitMQ.send("ReturnAuthenticationToken", body.toJSONString());
                } catch (Exception e) {
                    JSONObject body = Events.CreateResponseJson(null, 400, e.getMessage(), 23, "node-420" );
                    System.out.println(body.toJSONString());
                    try {
                        rabbitMQ.send("ReturnAuthenticationToken", body.toJSONString());
                    } catch (TimeoutException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                }
            });
            rabbitMQ.addSubscription("RequestAccountCreate","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    System.out.println("Received account create");
                    JSONObject body = Events.RequestAccountCreate(json);
                    System.out.println(body.toJSONString());
                    rabbitMQ.send("ConfirmAccountCreation", body.toJSONString());
                } catch (Exception e) {
                    JSONObject body = Events.CreateResponseJson(null, 400, e.getMessage(), 23, "node-420" );
                    System.out.println(body.toJSONString());
                    try {
                        rabbitMQ.send("ConfirmAccountCreation", body.toJSONString());
                    } catch (TimeoutException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                }
            });
            rabbitMQ.addSubscription("RequestInvalidateLoginToken","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.RequestInvalidateLoginToken(json);
                    System.out.println(body.toJSONString());
                    rabbitMQ.send("ConfirmInvalidateToken", body.toJSONString());
                } catch (Exception e) {
                    JSONObject body = Events.CreateResponseJson(null, 400, e.getMessage(), 23, "node-420" );
                    System.out.println(body.toJSONString());
                    try {
                        rabbitMQ.send("ConfirmInvalidateToken", body.toJSONString());
                    } catch (TimeoutException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                }
            });
            rabbitMQ.addSubscription("RequestAccountReset","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.ConfirmAccountReset(json);
                    System.out.println(body.toJSONString());
                    rabbitMQ.send("ConfirmAccountReset", body.toJSONString());
                } catch (Exception e) {
                    JSONObject body = Events.CreateResponseJson(null, 400, e.getMessage(), 23, "node-420" );
                    System.out.println(body.toJSONString());
                    try {
                        rabbitMQ.send("ConfirmAccountReset", body.toJSONString());
                    } catch (TimeoutException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                }
            });
            rabbitMQ.addSubscription("RequestAccountPasswordUpdate","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.RequestAccountPasswordUpdate(json);
                    System.out.println(body.toJSONString());
                    rabbitMQ.send("ConfirmSetPassword", body.toJSONString());
                } catch (Exception e) {
                    JSONObject body = Events.CreateResponseJson(null, 400, e.getMessage(), 23, "node-420" );
                    System.out.println(body.toJSONString());
                    try {
                        rabbitMQ.send("ConfirmSetPassword", body.toJSONString());
                    } catch (TimeoutException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                }
            });
            rabbitMQ.addSubscription("RequestAccountDelete","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.RequestAccountDelete(json);
                    System.out.println(body.toJSONString());
                    rabbitMQ.send("ConfirmAccountDeletion", body.toJSONString());
                } catch (Exception e) {
                    JSONObject body = Events.CreateResponseJson(null, 400, e.getMessage(), 23, "node-420" );
                    System.out.println(body.toJSONString());
                    try {
                        rabbitMQ.send("ConfirmAccountDeletion", body.toJSONString());
                    } catch (TimeoutException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                }
            });
            rabbitMQ.addSubscription("UpdateAccountPrivileges","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.UpdateAccountPrivileges(json);
                    System.out.println(body.toJSONString());
                    rabbitMQ.send("ConfirmAccountUpdate", body.toJSONString());
                } catch (Exception e) {
                    JSONObject body = Events.CreateResponseJson(null, 400, e.getMessage(), 23, "node-420" );
                    System.out.println(body.toJSONString());
                    try {
                        rabbitMQ.send("ConfirmAccountUpdate", body.toJSONString());
                    } catch (TimeoutException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                }
            });
            rabbitMQ.addSubscription("RequestAccountData","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.RequestAccountData(json);
                    System.out.println(body.toJSONString());
                    rabbitMQ.send("ReturnAccountInfo", body.toJSONString());
                } catch (Exception e) {
                    JSONObject body = Events.CreateResponseJson(null, 400, e.getMessage(), 23, "node-420" );
                    System.out.println(body.toJSONString());
                    try {
                        rabbitMQ.send("ReturnAccountInfo", body.toJSONString());
                    } catch (TimeoutException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                }
            });
            rabbitMQ.addSubscription("UpdateAccount","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.UpdateAccount(json);
                    System.out.println(body.toJSONString());
                    rabbitMQ.send("ConfirmAccountUpdate", body.toJSONString());
                } catch (Exception e) {
                    JSONObject body = Events.CreateResponseJson(null, 400, e.getMessage(), 23, "node-420" );
                    System.out.println(body.toJSONString());
                    try {
                        rabbitMQ.send("ConfirmAccountUpdate", body.toJSONString());
                    } catch (TimeoutException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                }
            });
            rabbitMQ.addSubscription("RequestBanUser","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.RequestBanUser(json);
                    System.out.println(body.toJSONString());
                    rabbitMQ.send("ConfirmBanUser", body.toJSONString());
                } catch (Exception e) {
                    JSONObject body = Events.CreateResponseJson(null, 400, e.getMessage(), 23, "node-420" );
                    System.out.println(body.toJSONString());
                    try {
                        rabbitMQ.send("ConfirmBanUser", body.toJSONString());
                    } catch (TimeoutException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                }
            });
            rabbitMQ.addSubscription("RequestFlagUser","Authentication",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                JSONParser parser = new JSONParser();
                try {
                    JSONObject json = (JSONObject) parser.parse(message);
                    JSONObject body = Events.RequestFlagUser(json);
                    System.out.println(body.toJSONString());
                    rabbitMQ.send("ConfirmFlagUser", body.toJSONString());
                } catch (Exception e) {
                    e.printStackTrace();
                    JSONObject body = Events.CreateResponseJson(null, 400, e.getMessage(), 23, "node-420" );
                    System.out.println(body.toJSONString());
                    try {
                        rabbitMQ.send("ConfirmFlagUser", body.toJSONString());
                    } catch (TimeoutException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException timeoutException) {
                        timeoutException.printStackTrace();
                    }
                }
            });
            rabbitMQ.setupReceiver("Authentication");
        } catch (IOException | TimeoutException | URISyntaxException | KeyManagementException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        /*try {
            JSONObject req = new JSONObject();
            req.put("username", "Jeppe");
            req.put("user-email", "Jeppe@");
            req.put("password", "12345");
            req.put("role", "admin");
            System.out.println("Send: RequestAccountCreate");
            rabbitMQ.send("RequestAccountCreate",req.toJSONString());
        } catch (IOException | TimeoutException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException e) {
            e.printStackTrace();
        }*/
        try {
            JSONObject req = new JSONObject();
            req.put("username", "Jeppe");
            req.put("password", "12345");
            System.out.println("Send: RequestLoginToken");
            rabbitMQ.send("RequestLoginToken",req.toJSONString());
        } catch (IOException | TimeoutException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException e) {
            e.printStackTrace();
        }
        rabbitMQ.addSubscription("ReturnAuthenticationToken","Gateway",(consumerTag, delivery) -> {
            String message = new String(delivery.getBody(),"UTF-8");
            System.out.println(message);
            /*JSONParser parser = new JSONParser();
            try {
                JSONObject json = (JSONObject) parser.parse(message);
                JWT = (String) json.get("data");
                JSONObject body = new JSONObject();
                String jwt ="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI1Iiwicm9sZSI6ImFkbWluIiwiaXNzIjoiSW1hZ2VIb3N0LnNkdS5kayIsImV4cCI6MTYzODU1NDMzNSwiaWF0IjoxNjA3MDE4MzM1fQ.zjBYhUKcGvWtZ-eTwVkOe-7vB9Fz0sb_Iqin290mhzw";
                body.put("authentication_token", jwt);
                body.put("user_id", "2");
                System.out.println("SEND CONFIRMFLAG JEPPE");
                rabbitMQ.send("RequestBanUser", body.toJSONString());
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        });
        /*rabbitMQ.addSubscription("ConfirmBanUser","Gateway",(consumerTag, delivery) -> {
            try {
                JSONObject req = new JSONObject();
                req.put("username", "Jeppe");
                req.put("password", "12345");
                System.out.println("Send: RequestLoginToken");
                rabbitMQ.send("RequestLoginToken",req.toJSONString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });*/
        rabbitMQ.setupReceiver("Gateway");


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
