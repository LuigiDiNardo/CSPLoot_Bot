package persistence.DAO;

import persistence.Entity.Utente;
import Connection.*;

import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtenteDAO {

    private Connection connection;

    public UtenteDAO(){}

    public void addUtente(@NotNull Utente user) throws SQLException{
        connection = ConnectionManager.getConnection();
        String query = "INSERT INTO utenti values(?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setLong(1 ,user.getId());
        preparedStatement.setString(2, user.getTag());
        preparedStatement.setInt(3, user.getKarma());
        preparedStatement.executeUpdate();
        connection.close();
    }

    public boolean existsUtente(long id) throws SQLException {
        connection = ConnectionManager.getConnection();
        String query = "SELECT * FROM utenti WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setLong(1, id);
        if(preparedStatement.executeQuery().next()){
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    public Utente findUtentebyId(long id) throws SQLException, NullPointerException{
        connection = ConnectionManager.getConnection();
        String query = "SELECT * FROM utenti WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setLong(1, id);
        if(preparedStatement.executeQuery() == null){
            throw new NullPointerException();
        } else {
            Utente user = preparedStatement.getResultSet().unwrap(Utente.class);
            connection.close();
            return user;
        }
    }

    public void upgradeKarmaUtente(long id, int value) throws SQLException{
        connection = ConnectionManager.getConnection();
        String query = "UPDATE utente set karma = karma ? WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, value);
        preparedStatement.setLong(2,  id);
        preparedStatement.executeUpdate();
        connection.close();
    }

    public String getKarmaRanking() throws SQLException{
        connection = ConnectionManager.getConnection();
        String query = "SELECT tag,karma FROM utenti ORDER BY karma DESC";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        if(preparedStatement.executeQuery() == null){
            connection.close();
            throw new NullPointerException();
        } else {
            String result = adaptStringForTelegramMessage(preparedStatement.getResultSet());
            connection.close();
            return result;
        }
    }

    private String adaptStringForTelegramMessage(ResultSet queryResult) throws SQLException {
        Pattern pattern = Pattern.compile("[^A-Za-z0-9]");
        StringBuilder result = new StringBuilder("*Classifica karma: *\n");

        if(queryResult != null) {
            int i = 1;
            Matcher matcher;
            while (queryResult.next()) {
                matcher = pattern.matcher(queryResult.getString(1));
                if (matcher.find())
                    result.append(i + "° " + queryResult.getString(1).replace(matcher.group(), "\\" + matcher.group()) + ": " + queryResult.getInt(2) + "\n");
                else
                    result.append(i + "° " + queryResult.getString(1) + ": " + queryResult.getInt(2) + "\n");
                i++;
            }
            return result.toString();
        } else throw new SQLException();

    }
}
