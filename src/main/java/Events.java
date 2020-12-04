import com.auth0.jwt.interfaces.DecodedJWT;
import org.json.simple.JSONObject;


public class Events {

    public static JSONObject CreateResponseJson(Object data, int statusCode, String message, int processingTime, String nodeRespondant ) {
        JSONObject response = new JSONObject();
        response.put("data", data);
        response.put("status_code", statusCode);
        response.put("message", message);
        response.put("processing_time", processingTime);
        response.put("node_respondant", nodeRespondant);
        return response;
    }

    public static JSONObject RequestLoginToken(JSONObject req) throws Exception {
        String userName = (String) req.get("username");
        String password = (String) req.get("password");
        Long ttl = (Long) req.get("ttl");
        String hash = Encryption.PassHash(password);
        String user_id = mySQL.receiveUser(userName,hash);
        String role = mySQL.getRole(user_id);
        String banned = mySQL.getBanned(user_id);
        JSONObject res;
        if (user_id != null && banned.equals("0")){
            boolean success = mySQL.addlogin(user_id);
            if (success){
                //Create JWT
                String jwt = Encryption.encodeJWT(user_id, ttl, role);
                res = CreateResponseJson(jwt, 200, "token created", 23, "node-420");
            } else {
                res = CreateResponseJson(null, 400, "error accessing database", 23, "node-420");
            }
        } else {
            res = CreateResponseJson(null, 400, "Wrong username or password or user is banned", 23, "node-420");
        }

        return res;
    }

    // /todo Maybe not needed
    public static JSONObject RequestInvalidateLoginToken(JSONObject req) throws Exception {
        String jwt = (String) req.get("authentication_token");
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
            res = CreateResponseJson(null, 400, "could not invalidate token", 23, "node-420");
            return res;
        } else {
            res = CreateResponseJson(null, 400, "authentication token not valid", 23, "node-420");
            return res;
        }
    }

    public static JSONObject RequestAccountCreate(JSONObject req) throws Exception {
        String userName = (String) req.get("username");
        String userEmail = (String) req.get("user-email");
        String role = (String) req.get("role");
        String hash = Encryption.PassHash((String) req.get("password"));
        String userID = mySQL.createUser(userName,hash,userEmail, role);
        JSONObject res;
        if (userID != null){
            JSONObject userData = new JSONObject();
            userData.put("user-id", userID);
            java.sql.Timestamp now = new java.sql.Timestamp(new java.util.Date().getTime());
            userData.put("created-at", now);
            res = CreateResponseJson(userData, 200, "user created", 23, "node-420");
        } else {
            res = CreateResponseJson(null, 400, "user not created", 23, "node-420");
        }
        return res;
    }

    public static JSONObject RequestAccountPasswordUpdate(JSONObject req) throws Exception {
        String jwt = (String) req.get("authentication_token");
        DecodedJWT verified = Encryption.decodeJWT(jwt);
        JSONObject res;
        if (verified != null){
            String hash_new = Encryption.PassHash((String) req.get("old-password"));
            String hash_old = Encryption.PassHash((String) req.get("new-password"));
            boolean success = mySQL.changePassword(verified.getSubject(), hash_new, hash_old);
            if (success){
                res = CreateResponseJson(null, 200, "password updated", 23, "node-420");
            } else {
                res = CreateResponseJson(null, 400, "could not update password", 23, "node-420");
            }
            return res;
        } else {
            res = CreateResponseJson(null, 400, "authentication token not valid", 23, "node-420");
        }
        return res;
    }

    public static JSONObject RequestAccountDelete(JSONObject req) throws Exception {
        String jwt = (String) req.get("authentication_token");
        DecodedJWT verified = Encryption.decodeJWT(jwt);
        JSONObject res;
        String role = verified.getClaim("role").asString(); // TODO change to range and numbers
        if (verified != null && role.equals("user")) {
            boolean success = mySQL.deleteUser(verified.getSubject());
            if (success) {
                res = CreateResponseJson(null, 200, "account deleted", 23, "node-420");
            } else {
                res = CreateResponseJson(null, 400, "could not delete account", 23, "node-420");
            }
            return res;
        } else if (verified != null && (role.equals("admin") || role.equals("manager"))){ // TODO change to range and numbers
            String idToDelete = (String) req.get("user_id");
            boolean success = mySQL.deleteUser(idToDelete);
            if (success) {
                res = CreateResponseJson(null, 200, "account deleted", 23, "node-420");
            } else {
                res = CreateResponseJson(null, 400, "could not delete account", 23, "node-420");
            }
            return res;
        } else {
            res = CreateResponseJson(null, 400, "authentication token not valid", 23, "node-420");
        }
        return res;
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
                res = CreateResponseJson(data, 200, "account reset", 23, "node-420");
            } else {
                res = CreateResponseJson(null, 400, "account could not be reset", 23, "node-420");
            }
            return res;
        } else {
            res = CreateResponseJson(null, 400, "authentication token not valid", 23, "node-420");
            return res;

        }
    }

    public static JSONObject UpdateAccount(JSONObject req) {
        String jwt = (String) req.get("authentication_token");
        String username = (String) req.get("username");
        String email = (String) req.get("user-email");
        DecodedJWT verified = Encryption.decodeJWT(jwt);
        JSONObject res;
        if (verified != null){
            boolean success = mySQL.updateAccount(verified.getSubject(), username, email);
            if (success){
                res = CreateResponseJson(null, 200, "account updated", 23, "node-420");
            } else {
                res = CreateResponseJson(null, 400, "account could not be updated", 23, "node-420");
            }
            return res;
        } else {
            res = CreateResponseJson(null, 400, "token not valid", 23, "node-420");
        }
        return res;
    }

    public static JSONObject UpdateAccountPrivileges(JSONObject req) {
        String jwt = (String) req.get("authentication_token");
        String newRole = (String) req.get("new-role");
        DecodedJWT verified = Encryption.decodeJWT(jwt);
        JSONObject res;
        String role = verified.getClaim("role").asString(); // TODO change to range and numbers
        if (verified != null && role.equals("manager")){
            boolean success = mySQL.updateRole(verified.getSubject(), newRole);
            if (success){
                res = CreateResponseJson(null, 200, "account updated", 23, "node-420");
            } else {
                res = CreateResponseJson(null, 400, "account could not be updated", 23, "node-420");
            }
            return res;
        } else {
            res = CreateResponseJson(null, 400, "token not valid", 23, "node-420");
        }
        return res;
    }

    public static JSONObject RequestAccountData(JSONObject req) {
        String jwt = (String) req.get("authentication_token");
        DecodedJWT verified = Encryption.decodeJWT(jwt);
        JSONObject res;
        if (verified != null){
            JSONObject data = mySQL.getUser(verified.getSubject());
            if (data != null){
                res = CreateResponseJson(data, 200, "account info returned", 23, "node-420");
            } else {
                res = CreateResponseJson(null, 400, "account info could not be returned", 23, "node-420");
            }
            return res;
        } else {
            res = CreateResponseJson(null, 400, "authentication token not valid", 23, "node-420");
            return res;
        }

    }

    public static JSONObject RequestBanUser(JSONObject req){
        String jwt = (String) req.get("authentication_token");
        DecodedJWT verified = Encryption.decodeJWT(jwt);
        JSONObject res;
        String role = verified.getClaim("role").asString(); // TODO change to range and numbers
        if (verified != null && (role.equals("admin") || role.equals("manager"))) {
            String idOfUser = (String) req.get("user_id");
            boolean success = mySQL.banUser(idOfUser);
            if (success){
                res = CreateResponseJson(null, 200, "user was banned", 23, "node-420");
            } else {
                res = CreateResponseJson(null, 400, "user could not be banned", 23, "node-420");
            }
            return res;
        } else {
            res = CreateResponseJson(null, 400, "permission denied", 23, "node-420");
            return res;
        }
    }

    public static JSONObject RequestFlagUser(JSONObject req){
        String jwt = (String) req.get("authentication_token");
        DecodedJWT verified = Encryption.decodeJWT(jwt);
        JSONObject res;
        String role = verified.getClaim("role").asString(); // TODO change to range and numbers
        if (verified != null && (role.equals("admin") || role.equals("manager"))) {
            String idOfUser = (String) req.get("user_id");
            System.out.println("USERID: "+idOfUser);
            boolean success = mySQL.flagUser(idOfUser);
            System.out.println("SUCCESS: "+success);
            if (success){
                res = CreateResponseJson(null, 200, "user was flagged", 23, "node-420");
            } else {
                res = CreateResponseJson(null, 400, "user could not be flagged", 23, "node-420");
            }
            return res;
        } else {
            res = CreateResponseJson(null, 400, "permission denied", 23, "node-420");
            return res;
        }
    }

    public static void main(String[] args) throws Exception {
        mySQL.start("jdbc:mysql://localhost:3306/authentication","root","1234");
        //JSONObject req = new JSONObject();
        //req.put("username", "lsoer");
        //req.put("user-email", "lsoer@live.dk");
        //req.put("password", "12345");
        //System.out.println(ConfirmAccountCreation(req));
        JSONObject login = new JSONObject();
        login.put("username", "lsoer");
        login.put("password", "12345");
        JSONObject res = RequestLoginToken(login);
        JSONObject pass = new JSONObject();
        pass.put("authentication_token", res.get("data"));
        pass.put("old-password", "12345");
        pass.put("new-password", "anneersej");
        System.out.println(RequestAccountPasswordUpdate(pass));

    }
}
