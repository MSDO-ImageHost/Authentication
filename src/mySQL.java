import java.sql.*;

public class mySQL {
    private Connection con;
    private Statement stmt;
    protected void start(String URL,String User, String psw){
        try {
            System.out.println("mySQL connection starting up");
            con = DriverManager.getConnection(URL,User,psw);
            stmt = con.createStatement();
        } catch (SQLException throwables) {
    throwables.printStackTrace();
        }
    }

    protected void stop(){
        try {
            System.out.println("mySQL connection shutting down");
            con.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    protected boolean receiveUser(String user, String password){
        try {
            String sql = String.format("SELECT User_name FROM login WHERE User_name = '%s' AND password = '%s'",user,password);
            System.out.println(sql);
            ResultSet rs = stmt.executeQuery(sql);
            return rs.next(); //One or more input received
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    protected boolean changePassword(String user, String old_password,String new_password){
        try {
            String sql = String.format("UPDATE Login.login SET password='%s' WHERE User_name='%s' AND password='%s'",new_password,user,old_password);
            System.out.println(sql);
            int i = stmt.executeUpdate(sql);
            return i==1; //One password changed
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    protected boolean createUser(String user, String password){
        try {
            String sql = String.format("INSERT INTO Login.login (User_name, password) VALUES ('%s','%s')",user,password);
            System.out.println(sql);
            int i = stmt.executeUpdate(sql);
            return i==1; //One user created
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    protected void printTable(String table){ //This function should only be there in development
        try {
            String sql = String.format("SELECT * FROM %s",table);
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                System.out.println(rs.getString(1) + ", " + rs.getString(2));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    protected Boolean deleteUser(String User,String password){
        try {
            String sql = String.format("DELETE FROM login WHERE User_name='%s' AND password = '%s'",User,password);
            return stmt.executeUpdate(sql) == 1;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }
}
