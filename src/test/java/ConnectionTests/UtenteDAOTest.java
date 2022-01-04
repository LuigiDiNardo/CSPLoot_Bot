package ConnectionTests;

import Connection.ConnectionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;
import persistence.DAO.UtenteDAO;
import persistence.Entity.Utente;

import java.sql.ResultSet;
import java.sql.SQLException;

@Testable
public class UtenteDAOTest {

    private static final UtenteDAO utenteDAO = new UtenteDAO(true);
    private static final Utente utente = new Utente(69420619,"DummyRitardev");
    private static final String KARMARANKING = "*Classifica karma: *\n" +
                                                "1° Michelangelo: 20\n" +
                                                "2° HolyMoly23: 10\n" +
                                                "3° DummyRitardev: 0\n";

    @BeforeEach
    private void insertDummyUser(){
        try {
            utenteDAO.addUtente(utente);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    private void clearDB(){
        utenteDAO.clearAllData();
    }

    @Test
    public void addUtenteWithNormalTag_Test(){
        try{
            utenteDAO.addUtente(new Utente(12345678,"Dummy"));
            Assertions.assertTrue(utenteDAO.existsUtente(12345678), "User '12345678' has not been saved");
        } catch(SQLException e){
            System.err.println("addUtenteWitNormalTag_Test ----> SQL EXCEPTION ERROR");
        }
    }

    @Test
    public void addUtenteWithSpecialTag_Test(){
        try{
            utenteDAO.addUtente(new Utente(12345678,"Dummy_123"));
            Assertions.assertTrue(utenteDAO.existsUtente(12345678), "User '12345678' has not been saved");
        } catch(SQLException e){
            System.err.println("addUtenteWitSpecialTag_Test ----> SQL EXCEPTION ERROR");
        }
    }

    @Test
    public void existsUtenteWithWrongId_Test(){
        long id = 274226431;
        try {
            Assertions.assertFalse(utenteDAO.existsUtente(id),"The user identified by '274226431' is on the table Utenti while there " +
                    "is only an user identified by the code '69420619'");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("UtenteWithWrongId_Test ----> SQL EXCEPTION ERROR");
        }
    }

    @Test
    public void existsUtenteWithGoodId_Test(){
        long id = 69420619;
        try {
            Assertions.assertTrue(utenteDAO.existsUtente(id),"The user identified by '69420619' is not on the table Utenti while there should be.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("UtenteWithGoodId_Test ----> SQL EXCEPTION ERROR");
        }
    }

    @Test
    public void findUtenteByGoodId_Test(){
        long id = 69420619;
        try {
            Assertions.assertTrue(utenteDAO.findUtentebyId(id).getId() == utente.getId() &&
                    utenteDAO.findUtentebyId(id).getTag().equals(utente.getTag()), "User identified as '69420619' has not been found.");
            //fore some reason assertEquals between utenteDAO result and utente doesn't work fine
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("findUtenteByGoodId_Test ----> SQL EXCEPTION ERROR");
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("findUtenteByGoodId_Test ----> NULL EXCEPTION ERROR");
        }
    }

    @Test
    public void findUtenteByWrongId_Test(){
        long id = 12345678;
        try {
            utenteDAO.findUtentebyId(id);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("findUtenteByWrongId_Test ----> SQL EXCEPTION ERROR");
        } catch (NullPointerException e) {
            Assertions.assertNotNull(e);
        }
    }

    @Test
    public void upgradeKarmaUtenteWithPositiveValue_Test(){
        long id = 69420619;
        try{
            utenteDAO.upgradeKarmaUtente(id,1);
            Assertions.assertTrue(1 == getKarmaPoint(id),"upgradeKarmaUtenteWithPositiveValue_Test ----> COUNTING ERROR");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("upgradeKarmaUtenteWithPositiveValue_Test ----> SQL EXCEPTION ERROR");
        }
    }

    @Test
    public void upgradeKarmaUtenteWithNegativeValue_Test(){
        long id = 69420619;
        try{
            utenteDAO.upgradeKarmaUtente(id,-1);
            Assertions.assertTrue(-1 == getKarmaPoint(id),"upgradeKarmaUtenteWithNegativeValue_Test ----> COUNTING ERROR");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("upgradeKarmaUtenteWithNegativeValue_Test ----> SQL EXCEPTION ERROR");
        }
    }

    @Test
    public void getKarmaRanking_Test() {
        Utente utente1 = new Utente(12345678, "Michelangelo");
        Utente utente2 = new Utente(87654321, "HolyMoly23");
        utente1.setKarma(20);
        utente2.setKarma(10);
        try {
            utenteDAO.addUtente(utente1);
            utenteDAO.addUtente(utente2);
        } catch(SQLException e){
            System.err.println("getKarmaRanking_Test ----> SQL EXCEPTION ERROR");
        }
        try{
            Assertions.assertEquals(KARMARANKING,utenteDAO.getKarmaRanking(), "String does not match");
        } catch(SQLException e) {
            System.err.println("getKarmaRanking_Test ----> SQL EXCEPTION ERROR ON KARMARANKING");
        }
    }

    private int getKarmaPoint(long id){
        try {
            ResultSet resultSet = ConnectionManager
                    .getConnection(true)
                    .createStatement()
                    .executeQuery("SELECT karma FROM utenti where id = " + id);
            return (resultSet.next() ? resultSet.getInt(1) : -99999999);
        } catch (SQLException e) {
            return -99999999;
        }
    }



}
