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

    public static JSONObject RequestLoginToken(JSONObject req) throws SQLException {
        String userName = (String) req.get("username");
        String password = (String) req.get("password");
        Long ttl = (Long) req.get("ttl");
        String hash = Encryption.PassHash(password);
        String user_id = mySQL.receiveUser(userName,hash);
        if (user_id != null){
            Integer role = mySQL.getRole(user_id);
            Boolean banned = mySQL.isBanned(user_id);
            if (!banned){
                boolean success = mySQL.addlogin(user_id);
                if (success){
                    JSONObject userInfo = mySQL.getUser(user_id);
                    String jwt = Encryption.encodeJWT(user_id, ttl, role);
                    userInfo.put("jwt", jwt);
                    return CreateResponseJson(userInfo, 200, "token created");
                } else {
                    return CreateResponseJson(null, 400, "error accessing database");
                }
            }
            return CreateResponseJson(null, 400, "User is banned");
        }
        return CreateResponseJson(null, 400, "Wrong username or password");
    }

    // /todo Maybe not needed
    public static JSONObject RequestInvalidateLoginToken(JSONObject req) {
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

    public static JSONObject RequestAccountCreate(JSONObject req) throws SQLException {
        String userName = (String) req.get("username");
        String userEmail = (String) req.get("user_email");
        int tempRole = ((Long) req.get("role")).intValue();
        String hash = Encryption.PassHash((String) req.get("password"));
        String userID = mySQL.createUser(userName,hash,userEmail, tempRole);
        JSONObject res;
        if (userID != null){
            JSONObject userData = new JSONObject();
            userData.put("user_id", userID);
            java.sql.Timestamp now = new java.sql.Timestamp(new java.util.Date().getTime());
            userData.put("created_at", now.toString());
            userData.put("username", userName);
            userData.put("user_email", userEmail);
            userData.put("role", tempRole);
            res = CreateResponseJson(userData, 200, "user created");
        } else {
            res = CreateResponseJson(null, 400, "user not created");
        }
        return res;
    }

    public static JSONObject RequestAccountPasswordUpdate(JSONObject req, String jwt) throws SQLException {
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

    public static JSONObject RequestAccountDelete(JSONObject req, String jwt) throws SQLException {
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
                String userIdToDelete = (String) req.get("user_id");
                boolean success = mySQL.deleteUser(userIdToDelete);
                if (success) {
                    res = CreateResponseJson(null, 200, "username: "+userIdToDelete+" deleted");
                } else {
                    res = CreateResponseJson(null, 400, "could not delete account");
                }
            }
            return res;
        }
        return CreateResponseJson(null, 400, "authentication token not valid");
    }

    // /todo should be worked on with gateway
    public static JSONObject ConfirmAccountReset(JSONObject req) throws SQLException {
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

    public static JSONObject UpdateAccount(JSONObject req, String jwt) throws SQLException {
        String username = (String) req.get("username");
        String email = (String) req.get("user_email");
        DecodedJWT verified = Encryption.decodeJWT(jwt);
        JSONObject res;
        if (verified != null){
            boolean success = mySQL.updateAccount(verified.getSubject(), username, email);
            if (success){
                JSONObject userInfo = mySQL.getUser(verified.getSubject());
                JSONObject resBody = new JSONObject();
                resBody.put("username", userInfo.get("username"));
                resBody.put("user_email", userInfo.get("user_email"));
                resBody.put("updated_at", userInfo.get("updated_at"));
                res = CreateResponseJson(resBody, 200, "account updated");
            } else {
                res = CreateResponseJson(null, 400, "account could not be updated");
            }
            return res;
        } else {
            res = CreateResponseJson(null, 400, "token not valid");
        }
        return res;
    }

    public static JSONObject UpdateAccountPrivileges(JSONObject req, String jwt) throws SQLException {
        int newRole = ((Long) req.get("new_role")).intValue();
        String idOfUser = (String) req.get("user_id");
        DecodedJWT verified = Encryption.decodeJWT(jwt);
        JSONObject res;
        if (verified != null) {
            int role = verified.getClaim("role").asInt();
            if ( role > 19) {
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

    public static JSONObject RequestAccountData(JSONObject req, String jwt) throws SQLException {
        DecodedJWT verified = Encryption.decodeJWT(jwt);
        JSONObject res;
        if (verified != null){
            int role = verified.getClaim("role").asInt();
            JSONObject data;
            if (role < 9){
                data = mySQL.getUser(verified.getSubject());
            } else {
                String userID = (String) req.get("user_id");
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

    public static JSONObject RequestBanUser(JSONObject req, String jwt) throws SQLException {
        DecodedJWT verified = Encryption.decodeJWT(jwt);
        String permanent = String.valueOf(req.get("permanent"));
        JSONObject res;
        if (verified != null){
            int role = verified.getClaim("role").asInt();
            if (role > 9) {
                String idOfUser = (String) req.get("user_id");
                boolean success;
                if (permanent.equals("true")){
                    long permBanned = (1000 * 60 * 60 * 24 * 365L * 17L); //17 years
                    success = mySQL.banUser(idOfUser, permBanned);
                } else {
                    long tempBanned = (1000 * 60 * 60 * 48); //Two days
                    success = mySQL.banUser(idOfUser, tempBanned);
                }
                if (success) {
                    res = CreateResponseJson(null, 200, "user "+idOfUser+" was banned");
                } else {
                    res = CreateResponseJson(null, 400, "user "+idOfUser+" could not be banned");
                }
                return res;
            }
        }
        return CreateResponseJson(null, 400, "permission denied");
    }

    public static JSONObject RequestFlagUser(JSONObject req, String jwt) throws SQLException {
        DecodedJWT verified = Encryption.decodeJWT(jwt);
        JSONObject res;
        if (verified != null){
            int role = verified.getClaim("role").asInt();
            if ( role > 9) {
                String idOfUser = (String) req.get("user_id");
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

    public static JSONObject RequestAllFlagged(JSONObject req, String jwt) throws SQLException {
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

    public static void main(String[] args) throws Exception {}
}
