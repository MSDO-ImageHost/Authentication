import org.json.simple.JSONObject;

import java.sql.*;
import java.util.ArrayList;

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

    public static boolean checkConn(String URL,String User, String psw){
        try {
            DriverManager.getConnection(URL,User,psw);
            return true;
        } catch (SQLException throwables) {
            return false;
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

    protected static String receiveUser(String user, String password) throws SQLException {
        String sql = String.format("SELECT user_id FROM authentication.users WHERE username = '%s' AND password_hash = '%s'",user,password);
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        return rs.getString("user_id"); //One or more input received
    }

    protected static JSONObject getUser(String userid) throws SQLException {
        String sql = String.format("SELECT * FROM authentication.users WHERE user_id = '%s'",userid);
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        JSONObject userData = createUserJSON(rs.getString("username"), rs.getString("email"), rs.getString("role"), rs.getString("last_changed"), rs.getString("created_at"), rs.getString("last_login"));
        return userData; //One or more input received
    }

    protected static Integer getRole(String userid) throws SQLException {
        String sql = String.format("SELECT role FROM authentication.users WHERE user_id = '%s'",userid);
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        return rs.getInt("role"); //One or more input received
    }

    protected static String getId(String username) throws SQLException {
        String sql = String.format("SELECT user_id FROM authentication.users WHERE username = '%s'",username);
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        return rs.getString("user_id"); //One or more input received
    }

    protected static Boolean isBanned(String userid) throws SQLException {
        String sql = String.format("SELECT banned_until FROM authentication.users WHERE user_id = '%s'",userid);
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        Timestamp bannedUntil = rs.getTimestamp("banned_until"); //One or more input received
        Timestamp now = new Timestamp(new java.util.Date().getTime());
        if (bannedUntil == null) {
            return false;
        } else if (now.compareTo(bannedUntil) > 0)  {
            return false;
        } else {
            return true;
        }
    }

    protected static String getFlagged(String userid) throws SQLException {
        String sql = String.format("SELECT flagged FROM authentication.users WHERE user_id = '%s'",userid);
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        return rs.getString("flagged"); //One or more input received
    }

    protected static ArrayList<String> getAllFlagged() throws SQLException {
        String sql = String.format("SELECT * FROM authentication.users WHERE flagged = 1");
        ResultSet rs = stmt.executeQuery(sql);
        ArrayList<String> flaggedUsers = new ArrayList<String>();
        while (rs.next()){
            flaggedUsers.add(rs.getString("username"));
        }
        return flaggedUsers; //One or more input received
    }

    protected static JSONObject createUserJSON(String userName, String email, String role, String lastChanged, String createdAt, String lastLogin){
        JSONObject userData = new JSONObject();
        userData.put("username", userName);
        userData.put("user_email", email);
        userData.put("role", role);
        userData.put("created_at", createdAt);
        userData.put("updated_at", lastChanged);
        userData.put("last_login", lastLogin);
        return userData;

    }

    protected static boolean changePassword(String userid, String old_password,String new_password) throws SQLException {
        String sql = String.format("UPDATE authentication.users SET password_hash='%s' WHERE user_id='%s' AND password_hash='%s'",new_password,userid,old_password);
        int i = stmt.executeUpdate(sql);
        return i==1; //One password changed
    }

    protected static boolean setPasswordToDefault(String userid, String defaultPassword) throws SQLException {
        String sql = String.format("UPDATE authentication.users SET password_hash='%s' WHERE user_id='%s'",defaultPassword,userid);
        int i = stmt.executeUpdate(sql);
        return i==1; //One password changed
    }

    protected static boolean updateAccount(String userid, String username, String email) throws SQLException {
        String sql = String.format("UPDATE authentication.users SET username='%s', email='%s' WHERE user_id='%s'",username,email,userid);
        int i = stmt.executeUpdate(sql);
        return i==1; //One password changed
    }

    protected static boolean updateRole(String userid, int newRole) throws SQLException {
        String sql = String.format("UPDATE authentication.users SET role=%d WHERE user_id='%s'", newRole, userid);
        int i = stmt.executeUpdate(sql);
        return i == 1; //One password changed
    }

    protected static boolean flagUser(String userid) throws SQLException {
        String sql = String.format("UPDATE authentication.users SET flagged='%d' WHERE user_id='%s'", 1, userid);
        int i = stmt.executeUpdate(sql);
        return i == 1; //One password changed
    }

    protected static boolean banUser(String userid, long timeBanned) throws SQLException {
        Timestamp banned_until = new Timestamp(new java.util.Date().getTime()+timeBanned);
        String sql = String.format("UPDATE authentication.users SET banned_until='%s' WHERE user_id='%s'", banned_until, userid);
        int i = stmt.executeUpdate(sql);
        return i == 1;
    }

    protected static String createUser(String user, String password, String email, Integer role) throws SQLException {
        String sql = String.format("INSERT INTO authentication.users (username, email, password_hash, role) VALUES ('%s','%s','%s','%d')",user,email, password, role);
        int i = stmt.executeUpdate(sql);
        if (i == 1){
            return receiveUser(user, password);
        } else {
            return null;
        }
    }

    protected static boolean addlogin(String userid) throws SQLException {
        java.sql.Timestamp loginAt = new java.sql.Timestamp(new java.util.Date().getTime());
        String sql = String.format("UPDATE authentication.users SET last_login='%s' WHERE user_id='%s'",loginAt, userid);
        int i = stmt.executeUpdate(sql);
        return i==1; //One user updated
    }


    protected static boolean deleteUser(String userid) throws SQLException {
        String sql = String.format("DELETE FROM authentication.users WHERE user_id='%s'",userid);
        return stmt.executeUpdate(sql) == 1;
    }


    public static void main(String[] args) throws SQLException {
        start("jdbc:mysql://localhost:3306/authentication","root","1234");
        System.out.println(getUser( "1"));
    }
}