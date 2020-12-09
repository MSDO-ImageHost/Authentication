import com.auth0.jwt.interfaces.DecodedJWT;
import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;


public class Events {

    public static JSONObject CreateResponseJson(Object data, int statusCode, String message) {
        JSONObject response = new JSONObject();
        response.put("data", data);
        response.put("status_code", statusCode);
        response.put("message", message);
        return response;
    }

    public static JSONObject RequestLoginToken(JSONObject req) throws Exception {
        String userName = (String) req.get("username");
        String password = (String) req.get("password");
        Long ttl = (Long) req.get("ttl");
        String hash = Encryption.PassHash(password);
        String user_id = mySQL.receiveUser(userName,hash);
        Integer role = mySQL.getRole(user_id);
        Boolean banned = mySQL.isBanned(user_id);
        JSONObject res;
        if (banned == null || role == null){
            throw new SQLException("SQL connection failed");
        }
        if (user_id != null && !banned){
            boolean success = mySQL.addlogin(user_id);
            if (success){
                String jwt = Encryption.encodeJWT(user_id, ttl, role);
                res = CreateResponseJson(jwt, 200, "token created");
            } else {
                res = CreateResponseJson(null, 400, "error accessing database");
            }
            return res;
        }
        return CreateResponseJson(null, 400, "Wrong username or password or user is banned");
    }

    // /todo Maybe not needed
    public static JSONObject RequestInvalidateLoginToken(JSONObject req) throws Exception {
        String jwt = (String) req.get("authentication_token");//Skal ændres
        DecodedJWT verified = Encryption.decodeJWT(jwt);
        JSONObject res;
        if (verified != null){
            //Boolean deleted = mySQL.deleteLogin(verified.getSubject());
            /*if (deleted){
                JSONObject data = new JSONObject();
                java.sql.Timestamp now = new java.sql.Timestamp(new java.util.Date().getTime());
                data.put("invalidated_at", now);
                res = CreateResponseJson(data, 200, "token invalidated", 23, "node-420");
            } else {
                res = CreateResponseJson(null, 400, "could not invalidate token", 23, "node-420");
            }*/
            res = CreateResponseJson(null, 400, "could not invalidate token");
            return res;
        } else {
            res = CreateResponseJson(null, 400, "authentication token not valid");
            return res;
        }
    }

    public static JSONObject RequestAccountCreate(JSONObject req) throws Exception {
        System.out.println("Inside account create");
        String userName = (String) req.get("username");
        String userEmail = (String) req.get("user_email");
        String tempRole = (String) req.get("role");
        Integer role = Integer.parseInt(tempRole);
        String hash = Encryption.PassHash((String) req.get("password"));
        String userID = mySQL.createUser(userName,hash,userEmail, role);
        JSONObject res;
        if (userID != null){
            JSONObject userData = new JSONObject();
            userData.put("user_id", userID);
            java.sql.Timestamp now = new java.sql.Timestamp(new java.util.Date().getTime());
            userData.put("created_at", now);
            res = CreateResponseJson(userData, 200, "user created");
        } else {
            res = CreateResponseJson(null, 400, "user not created");
        }
        return res;
    }

    public static JSONObject RequestAccountPasswordUpdate(JSONObject req, String jwt) throws Exception {
        DecodedJWT verified = Encryption.decodeJWT(jwt);
        JSONObject res;
        if (verified != null){
            String hash_new = Encryption.PassHash((String) req.get("old_password"));
            String hash_old = Encryption.PassHash((String) req.get("new_password"));
            boolean success = mySQL.changePassword(verified.getSubject(), hash_new, hash_old);
            if (success){
                res = CreateResponseJson(null, 200, "password updated");
            } else {
                res = CreateResponseJson(null, 400, "could not update password");
            }
            return res;
        }
        return CreateResponseJson(null, 400, "authentication token not valid");
    }

    public static JSONObject RequestAccountDelete(JSONObject req, String jwt) throws Exception {
        DecodedJWT verified = Encryption.decodeJWT(jwt);
        JSONObject res;
        if(verified != null){
            int role = verified.getClaim("role").asInt();
            if (role < 10) {
                boolean success = mySQL.deleteUser(verified.getSubject());
                if (success) {
                    res = CreateResponseJson(null, 200, "user id: "+verified.getSubject()+" deleted");
                } else {
                    res = CreateResponseJson(null, 400, "could not delete account");
                }
            } else {
                String userToDelete = (String) req.get("username");
                String userId = mySQL.getId(userToDelete);
                boolean success = mySQL.deleteUser(userId);
                if (success) {
                    res = CreateResponseJson(null, 200, "username: "+userToDelete+" deleted");
                } else {
                    res = CreateResponseJson(null, 400, "could not delete account");
                }
            }
            return res;
        }
        return CreateResponseJson(null, 400, "authentication token not valid");
    }

    // /todo should be worked on with gateway
    public static JSONObject ConfirmAccountReset(JSONObject req) throws Exception{
        String jwt = (String) req.get("authentication_token");
        DecodedJWT verified = Encryption.decodeJWT(jwt);
        JSONObject res;
        if (verified != null){
            String resetCode = Encryption.generateRandomHexToken(4);
            String defaultPassword = "default";
            boolean success = mySQL.setPasswordToDefault(verified.getSubject(), defaultPassword);
            if (success){
                JSONObject data = new JSONObject();
                data.put("reset_code", resetCode);
                data.put("default_password", defaultPassword);
                res = CreateResponseJson(data, 200, "account reset");
            } else {
                res = CreateResponseJson(null, 400, "account could not be reset");
            }
            return res;
        }
        return CreateResponseJson(null, 400, "authentication token not valid");
    }

    public static JSONObject UpdateAccount(JSONObject req, String jwt) {
        String username = (String) req.get("username");
        String email = (String) req.get("user_email");
        DecodedJWT verified = Encryption.decodeJWT(jwt);
        JSONObject res;
        if (verified != null){
            boolean success = mySQL.updateAccount(verified.getSubject(), username, email);
            if (success){
                res = CreateResponseJson(null, 200, "account updated");
            } else {
                res = CreateResponseJson(null, 400, "account could not be updated");
            }
            return res;
        } else {
            res = CreateResponseJson(null, 400, "token not valid");
        }
        return res;
    }

    public static JSONObject UpdateAccountPrivileges(JSONObject req, String jwt) {
        String tempRole = (String) req.get("new_role");
        int newRole = Integer.parseInt(tempRole);
        String nameOfUser = (String) req.get("username");
        DecodedJWT verified = Encryption.decodeJWT(jwt);
        JSONObject res;
        if (verified != null) {
            int role = verified.getClaim("role").asInt();
            if ( role > 19) {
                String idOfUser = mySQL.getId(nameOfUser);
                boolean success = mySQL.updateRole(idOfUser, newRole);
                if (success) {
                    res = CreateResponseJson(null, 200, "account updated");
                } else {
                    res = CreateResponseJson(null, 400, "account could not be updated");
                }
                return res;
            }
        }
        return CreateResponseJson(null, 400, "token not valid");
    }

    public static JSONObject RequestAccountData(JSONObject req, String jwt) {
        DecodedJWT verified = Encryption.decodeJWT(jwt);
        JSONObject res;
        if (verified != null){
            int role = verified.getClaim("role").asInt();
            JSONObject data;
            if (role < 9){
                data = mySQL.getUser(verified.getSubject());
            } else {
                String userID = mySQL.getId((String) req.get("username"));
                data = mySQL.getUser(userID);
            }
            if (data != null){
                res = CreateResponseJson(data, 200, "account info returned");
            } else {
                res = CreateResponseJson(null, 400, "account info could not be returned");
            }
        } else {
            res = CreateResponseJson(null, 400, "authentication token not valid");
        }
        return res;

    }

    public static JSONObject RequestBanUser(JSONObject req, String jwt){
        DecodedJWT verified = Encryption.decodeJWT(jwt);
        String permanent = String.valueOf(req.get("permanent"));
        System.out.println("value of permanent: "+permanent);
        JSONObject res;
        if (verified != null){
            int role = verified.getClaim("role").asInt();
            if (role > 9) {
                String nameOfUser = (String) req.get("username");
                String idOfUser = mySQL.getId(nameOfUser);
                System.out.println("idOfUser: "+idOfUser);
                boolean success;
                if (permanent.equals("true")){
                    System.out.println("permanent is true");
                    long permBanned = (1000 * 60 * 60 * 24 * 365L * 17L); //17 years
                    success = mySQL.banUser(idOfUser, permBanned);
                } else {
                    long tempBanned = (1000 * 60 * 60 * 48); //Two days
                    success = mySQL.banUser(idOfUser, tempBanned);
                }
                if (success) {
                    res = CreateResponseJson(null, 200, "user "+nameOfUser+" was banned");
                } else {
                    res = CreateResponseJson(null, 400, "user "+nameOfUser+" could not be banned");
                }
                return res;
            }
        }
        return CreateResponseJson(null, 400, "permission denied");
    }

    public static JSONObject RequestFlagUser(JSONObject req, String jwt){
        DecodedJWT verified = Encryption.decodeJWT(jwt);
        JSONObject res;
        if (verified != null){
            int role = verified.getClaim("role").asInt();
            if ( role > 9) {
                String nameOfUser = (String) req.get("username");
                String idOfUser = mySQL.getId(nameOfUser);
                boolean success = mySQL.flagUser(idOfUser);
                if (success) {
                    res = CreateResponseJson(null, 200, "user was flagged");
                } else {
                    res = CreateResponseJson(null, 400, "user could not be flagged");
                }
                return res;
            }
        }
        return CreateResponseJson(null, 400, "permission denied");
    }

    public static JSONObject RequestAllFlagged(JSONObject req, String jwt){
        DecodedJWT verified = Encryption.decodeJWT(jwt);
        JSONObject res;
        if (verified != null){
            int role = verified.getClaim("role").asInt();
            if ( role > 9) {
                ArrayList<String> allUsers = mySQL.getAllFlagged();
                if (allUsers != null) {
                    JSONObject data = new JSONObject();
                    data.put("users", allUsers);
                    res = CreateResponseJson(data, 200, "returned all flagged users");
                } else {
                    res = CreateResponseJson(null, 400, "could not get flagged users");
                }
                return res;
            }
        }
        return CreateResponseJson(null, 400, "permission denied");
    }

    public static void main(String[] args) throws Exception {
        mySQL.start("jdbc:mysql://localhost:3306/authentication","root","1234");
        JSONObject req = new JSONObject();
        req.put("username", "lsoer");
        req.put("user_email", "lsoer@live.dk");
        req.put("password", "12345");
        req.put("role","20");
        System.out.println(RequestAccountCreate(req));
        /*JSONObject login = new JSONObject();
        login.put("username", "lsoer");
        login.put("password", "12345");
        JSONObject res = RequestLoginToken(login);
        JSONObject pass = new JSONObject();
        pass.put("authentication_token", res.get("data"));
        pass.put("old_password", "12345");
        pass.put("new_password", "anneersej");
        System.out.println(RequestAccountPasswordUpdate(pass));*/

    }
}
