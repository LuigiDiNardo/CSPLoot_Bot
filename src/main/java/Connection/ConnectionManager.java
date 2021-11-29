package Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager{

    private static Connection connection;
    private static final String url = "jdbc:mysql://xxx";
    private static final String userOwner = "Gigi";
    private static final String pass = "xxx";

    public static Connection getConnection() throws SQLException {
        if(connection == null){
            return DriverManager.getConnection(url, userOwner, pass);
        }
        else{
            return connection;
        }
    }

}
