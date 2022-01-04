package Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager{

    private static Connection connection;
    private static final String url = "jdbc:mysql://sql11.freesqldatabase.com:3306/sql11458050"; //ricordati di nascondere tutte le credenziali del db
    private static final String userOwner = "sql11458050";
    private static final String pass = "snwSID3MtK";
    private static final String urlTest = "jdbc:h2:file:C:\\Users\\gigid\\IdeaProjects\\CSPLoot_Bot\\CornettoBot_DBTest";
    private static final String userOwnerTest = "User";
    private static final String passTest = "pass";

    public static Connection getConnection(boolean isTest) throws SQLException {
        if(connection == null){
            return !isTest ? DriverManager.getConnection(url, userOwner, pass) : DriverManager.getConnection(urlTest, userOwnerTest, passTest);
        }
        else{
            return connection;
        }
    }
}
