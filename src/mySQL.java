import org.json.simple.JSONObject;

import java.sql.*;

public class mySQL {
    private static Connection con;
    private static Statement stmt;
    private static final long DURATION = (1000 * 60 * 60 * 5);

    protected static void start(String URL,String User, String psw){
        try {
            System.out.println("mySQL connection starting up");
            con = DriverManager.getConnection(URL,User,psw);
            stmt = con.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    protected static void stop(){
        try {
            System.out.println("mySQL connection shutting down");
            con.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    protected static String receiveUser(String user, String password){
        try {
            String sql = String.format("SELECT user_id FROM authentication.users WHERE username = '%s' AND password_hash = '%s'",user,password);
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            return rs.getString("user_id"); //One or more input received
        } catch (SQLException throwables) {
            return null;
        }
    }

    protected static JSONObject getUser(String userId){
        try {
            String sql = String.format("SELECT * FROM authentication.users WHERE user_id = '%s'",userId);
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            JSONObject userData = createUserJSON(rs.getString("username"), rs.getString("email"), rs.getString("role"), rs.getString("last_changed"), rs.getString("created_at"), rs.getString("last_login"));
            return userData; //One or more input received
        } catch (SQLException throwables) {
            return null;
        }
    }

    protected static String getRole(String userid){
        try {
            String sql = String.format("SELECT role FROM authentication.users WHERE user_id = '%s'",userid);
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            return rs.getString("role"); //One or more input received
        } catch (SQLException throwables) {
            return null;
        }
    }

    protected static JSONObject createUserJSON(String userName, String email, String role, String lastChanged, String createdAt, String lastLogin){
        JSONObject userData = new JSONObject();
        userData.put("username", userName);
        userData.put("user-email", email);
        userData.put("role", role);
        userData.put("created_at", createdAt);
        userData.put("updated_at", lastChanged);
        userData.put("last_login", lastLogin);
        return userData;

    }

    protected static boolean changePassword(String userid, String old_password,String new_password){
        try {
            String sql = String.format("UPDATE authentication.users SET password_hash='%s' WHERE user_id='%s' AND password_hash='%s'",new_password,userid,old_password);
            int i = stmt.executeUpdate(sql);
            return i==1; //One password changed
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    protected static boolean setPasswordToDefault(String userid, String defaultPassword){
        try {
            String sql = String.format("UPDATE authentication.users SET password_hash='%s' WHERE user_id='%s'",defaultPassword,userid);
            int i = stmt.executeUpdate(sql);
            return i==1; //One password changed
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    protected static boolean updateAccount(String userid, String username, String email){
        try {
            String sql = String.format("UPDATE authentication.users SET username='%s', email='%s' WHERE user_id='%s'",username,email,userid);
            int i = stmt.executeUpdate(sql);
            return i==1; //One password changed
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    protected static boolean updateRole(String userid, String newRole) {
        try {
            String sql = String.format("UPDATE authentication.users SET role='%s' WHERE user_id='%s'", newRole, userid);
            int i = stmt.executeUpdate(sql);
            return i == 1; //One password changed
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    protected static String createUser(String user, String password, String email, String role){
        try {
            String sql = String.format("INSERT INTO authentication.users (username, email, password_hash, role) VALUES ('%s','%s','%s','%s')",user,email, password, role);
            int i = stmt.executeUpdate(sql);
            if (i == 1){
                return receiveUser(user, password);
            } else {
                return null;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    protected static boolean addlogin(String userid){
        try {
            java.sql.Timestamp loginAt = new java.sql.Timestamp(new java.util.Date().getTime());
            String sql = String.format("UPDATE authentication.users SET last_login='%s' WHERE user_id='%s'",loginAt, userid);
            int i = stmt.executeUpdate(sql);
            return i==1; //One user updated
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }


    protected static boolean deleteUser(String userid){
        try {
            String sql = String.format("DELETE FROM authentication.users WHERE user_id='%s'",userid);
            return stmt.executeUpdate(sql) == 1;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }


    public static void main(String[] args) {
        start("jdbc:mysql://localhost:3306/authentication","root","1234");
        System.out.println(getUser( "1"));
    }
}