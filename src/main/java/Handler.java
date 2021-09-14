import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.xml.sax.SAXException;

import javax.validation.constraints.Null;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.InputMismatchException;

public class Handler extends TelegramLongPollingBot {

    XMLManager xmlManager = XMLManager.getXMLManager();
    @Override
    public void onUpdateReceived(Update update) {


        if(update.getMessage().isSuperGroupMessage()){
            String[] com = update.getMessage().getText().toLowerCase().split(" ");
            String risposta;
            switch (com[0]){
                case "/vocazioni":
                    InviaMessaggio(update,"_https://telegra.ph/Lista-Vocazioni-di-Loot-01-26_");
                    break;
                case "/artefatti":
                    try {
                        risposta= xmlManager.ManageFileArtefatti();
                        InviaMessaggio(update, risposta);
                    } catch (IOException e) {
                        InviaMessaggio(update,"Errore nell'apertura del file *Artefatti.xml*");
                        e.printStackTrace();
                    } catch (ParserConfigurationException | SAXException e) {
                        InviaMessaggio(update,e.getMessage());
                        e.printStackTrace();
                    } catch (InputMismatchException e){
                        InviaMessaggio(update,"Puoi inserire un solo parametro per volta con questo comando!");
                        e.printStackTrace();
                    }
                    break;
                case "/talismani":
                    try{
                        if(com.length == 1){
                            risposta=xmlManager.ManageFileTalismani(null);
                        }else if(com.length == 2){
                            risposta=xmlManager.ManageFileTalismani(com[1]);
                        }else{
                            risposta="Non puoi inserire più di un parametro!";
                        }
                        InviaMessaggio(update,risposta);
                    } catch (SAXException | ParserConfigurationException e) {
                        InviaMessaggio(update,e.getMessage());
                        e.printStackTrace();
                    } catch (IOException e) {
                        InviaMessaggio(update,"Errore nell'apertira del file *Talismani.xml*");
                        e.printStackTrace();
                    } catch(InputMismatchException e) {
                        InviaMessaggio(update, "Devi inserire un parametro valido per la ricerca!");
                        e.printStackTrace();
                    }
                    break;
                case"/incarichi":
                    try{
                        if(com.length==1){
                            risposta = xmlManager.ManageFileIncarichi(null);
                            InviaMessaggio(update,risposta);
                        }else if(com.length==2){
                            risposta = xmlManager.ManageFileIncarichi(com[1]);
                            InviaMessaggio(update,risposta);
                        } else{
                            throw new InputMismatchException();
                        }

                    } catch (IOException e) {
                        InviaMessaggio(update,"Impossibile reperire il file *Incarichi.xml*");
                        e.printStackTrace();
                    } catch (ParserConfigurationException | SAXException e) {
                        InviaMessaggio(update,e.getMessage());
                        e.printStackTrace();
                    }catch (InputMismatchException e){
                        InviaMessaggio(update,"Devi inserire solo una parola valida per la ricerca, tipo \"fame\" oppure \"formiche\"!");
                        e.printStackTrace();
                    }
                default:break;
            }

        }
    }
    @Override
    public String getBotUsername() {
        return "CSPBot";
    }

    @Override
    public String getBotToken() {
        return null;
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
