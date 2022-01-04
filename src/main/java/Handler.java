import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.xml.sax.SAXException;
import persistence.DAO.UtenteDAO;
import persistence.Entity.Utente;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.InputMismatchException;

public class Handler extends TelegramLongPollingBot {

    private static final String[] KARMA_POSITIVE_TRIGGER_MESSAGES = {"+","up","UP","BASED","based","+1"};
    private static final String[] KARMA_NEGATIVE_TRIGGER_MESSAGES = {"-","DOWN","CRINGE","down","cringe","-1"};
    private static final XMLManager xmlManager = XMLManager.getXMLManager();
    private static final UtenteDAO utenteDAO = new UtenteDAO(false);

    public Handler() {}

    @Override
    public void onUpdateReceived(Update update) {

            if(update.getMessage().isSuperGroupMessage()) {

                long userId = update.getMessage().getFrom().getId();
                String tag = update.getMessage().getFrom().getUserName();

                try {
                    if(!utenteDAO.existsUtente(userId)){
                        utenteDAO.addUtente(new Utente(userId,tag));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    //InviaMessaggio(update, "Problema con la base di dati: salvataggio non riuscito!");
                }

                if(update.getMessage().isReply()){
                    KarmaOperation(userId,tag,update);
                }
    }

        if(update.getMessage().isCommand()){
            StringBuilder firstPart = new StringBuilder();
            if(update.getMessage().getText().contains("@")){
                StringBuilder secondPart = new StringBuilder();
                int index= update.getMessage().getText().indexOf("@");
                firstPart.append(update.getMessage().getText(), 0, index);
                secondPart.append(update.getMessage().getText().substring(index+12));
                firstPart.append(secondPart.toString().toLowerCase());
            } else {
                firstPart.append(update.getMessage().getText().toLowerCase());
            }
            String[] com = firstPart.toString().split(" ");
            String risposta;

            switch (com[0]) {
                case "/vocazioni":
                    InviaMessaggio(update, "_https://telegra.ph/Lista-Vocazioni-di-Loot-01-26_");
                    break;
                case "/artefatti":
                    try {
                        risposta = xmlManager.ManageFileArtefatti();
                        InviaMessaggio(update, risposta);
                    } catch (IOException e) {
                        InviaMessaggio(update, "Errore nell'apertura del file *Artefatti.xml*");
                        e.printStackTrace();
                    } catch (ParserConfigurationException | SAXException e) {
                        InviaMessaggio(update, e.getMessage());
                    e.printStackTrace();
                    } catch (InputMismatchException e) {
                        InviaMessaggio(update, "Puoi inserire un solo parametro per volta con questo comando!");
                        e.printStackTrace();
                    }
                    break;
                case "/talismani":
                    try {
                        if (com.length == 1) {
                            risposta = xmlManager.ManageFileTalismani(null);
                        } else if (com.length == 2) {
                            risposta = xmlManager.ManageFileTalismani(com[1]);
                        } else {
                            risposta = "Non puoi inserire più di un parametro!";
                        }
                        InviaMessaggio(update, risposta);
                    } catch (SAXException | ParserConfigurationException e) {
                        InviaMessaggio(update, e.getMessage());
                        e.printStackTrace();
                    } catch (IOException e) {
                        InviaMessaggio(update, "Errore nell'apertira del file *Talismani.xml*");
                        e.printStackTrace();
                    } catch (InputMismatchException e) {
                        InviaMessaggio(update, "Devi inserire un parametro valido per la ricerca!");
                        e.printStackTrace();
                    }
                    break;
                case "/incarichi":
                    try {
                        if (com.length == 1) {
                            risposta = xmlManager.ManageFileIncarichi(null);
                            InviaMessaggio(update, risposta);
                        } else if (com.length == 2) {
                            risposta = xmlManager.ManageFileIncarichi(com[1]);
                            InviaMessaggio(update, risposta);
                        } else {
                            throw new InputMismatchException();
                        }
                    } catch (IOException e) {
                        InviaMessaggio(update, "Impossibile reperire il file *Incarichi.xml*");
                        e.printStackTrace();
                    } catch (ParserConfigurationException | SAXException e) {
                        InviaMessaggio(update, e.getMessage());
                        e.printStackTrace();
                    } catch (InputMismatchException e) {
                        InviaMessaggio(update, "Devi inserire solo una parola valida per la ricerca, tipo \"fame\" oppure \"formiche\"!\n Nel caso dei dungeon devi specificare quale ti interessa!");
                        e.printStackTrace();
                    }
                    break;
                case "/karma":
                    if (!update.getMessage().isReply()) {
                        try {
                            InviaMessaggio(update, utenteDAO.getKarmaRanking());
                        } catch (SQLException e) {
                            e.printStackTrace();
                            InviaMessaggio(update, "Impossibile recapitare un rank del karma");
                        }
                    }
                    break;
                case "/help":
                    try {
                        InviaMessaggio(update,xmlManager.ShowHelpFile());
                    } catch (IOException e) {
                        InviaMessaggio(update,"Impossibile recuperare il file *Help.xml*");
                        e.printStackTrace();
                    } catch (ParserConfigurationException | SAXException e) {
                        InviaMessaggio(update,e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void KarmaOperation(long userId, String tag, Update update) {
        boolean trovato = false;
        boolean stop = false;
        for(String POSITIVE : Arrays.asList(KARMA_POSITIVE_TRIGGER_MESSAGES)){
            if(POSITIVE.equals(update.getMessage().getText())) {
                if (update.getMessage().getReplyToMessage().getFrom().getId() == userId) {
                    stop = true;
                } else {
                    try {
                        utenteDAO.upgradeKarmaUtente(update.getMessage().getFrom().getId(),+1);
                        InviaMessaggio(update, "*" + tag + "* hai dato +1 a *" + update.getMessage().getReplyToMessage().getFrom().getUserName() + "*");
                    } catch (SQLException e) {
                        e.printStackTrace();
                        InviaMessaggio(update, "Impossibile aggiungere karma all'utente!");
                    }
                    trovato = true;
                    break;
                }
            }
        }
        if(!trovato){
            for(String NEGATIVE : Arrays.asList(KARMA_NEGATIVE_TRIGGER_MESSAGES)){
                if(NEGATIVE.equals(update.getMessage().getText())){
                    if (update.getMessage().getReplyToMessage().getFrom().getId() == userId) {
                        stop = true;
                    }else {
                        try {
                            utenteDAO.upgradeKarmaUtente(update.getMessage().getFrom().getId(),-1);
                            InviaMessaggio(update, "*" + tag + "* hai dato -1 a *" + update.getMessage().getReplyToMessage().getFrom().getUserName() + "*");
                        } catch (SQLException e) {
                            e.printStackTrace();
                            InviaMessaggio(update, "Impossibile rimuovere karma all'utente!");
                        }
                        break;
                    }
                }
            }
        }
        if(stop){
            InviaMessaggio(update,"No, non credo te lo lascerò fare.");
        }

    }

    @Override
    public String getBotUsername() {
        return "CornettoBot";
    }

    @Override
    public String getBotToken() {
        return "xxx";
    }

    public void InviaMessaggio(Update update, String mex){
        String id = update.getMessage().getChatId().toString();
        SendMessage send = new SendMessage();
        send.setText(mex);
        send.enableMarkdown(true);
        try{
            send.setChatId(id);
            execute(send);
        }catch(TelegramApiException e){
            e.printStackTrace();
        }
    }
}
