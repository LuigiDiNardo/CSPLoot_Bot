import org.glassfish.hk2.utilities.reflection.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JSONManager {

    private static JSONManager jsonManager=null;


    private JSONManager(){}


    public static JSONManager getJSONManager() throws NullPointerException {
        if(jsonManager == null){
            jsonManager = new JSONManager();
            return jsonManager;
        }
        else {
            throw new NullPointerException();
        }
    }

    public void SaveUser(long userId, String tag, int typeKarma) throws IOException, SAXException, ParseException {
        String path = "src/main/resources/Utenti/Utenti.json";
        File file = new File(path);


        if(file.exists()){
            JSONObject userWrapper = new JSONObject();
            if(file.length()==0){
                JSONObject firstUser = new JSONObject();
                firstUser.put("karma",typeKarma);
                firstUser.put("name",tag);
                firstUser.put("id",userId);
                JSONArray users = new JSONArray();
                users.add(firstUser);
                userWrapper.put("utenti",users);
                FileWriter writer = new FileWriter(file);
                writer.write(userWrapper.toJSONString());
                writer.flush();
                writer.close();
            }else{
                JSONParser jsonParser = new JSONParser();
                FileReader reader = new FileReader(file);
                Object fileContextObject = jsonParser.parse(reader);
                JSONObject fileObject = (JSONObject) fileContextObject;
                JSONArray users = (JSONArray) fileObject.get("utenti");
                reader.close();
                JSONObject userComp;
                boolean trovato = false;
                for(Object user : users){
                    userComp = (JSONObject) user;
                    if(Long.parseLong(userComp.get("id").toString())==userId){
                        userComp.replace("karma",userComp.get("karma"),Integer.parseInt(userComp.get("karma").toString())+typeKarma);
                        trovato=true;
                        break;
                    }
                }
                if(!trovato){
                    JSONObject user = new JSONObject();
                    user.put("karma",typeKarma);
                    user.put("name",tag);
                    user.put("id",userId);
                    users.add(user);
                    userWrapper.clear();
                    userWrapper.put("utenti",users);
                    FileWriter writer = new FileWriter(file);
                    writer.write(userWrapper.toJSONString());
                    writer.flush();
                    writer.close();
                }
            }
        }
        else throw new IOException();

    }


    public String KarmaRanking() throws IOException, SAXException, ParseException{//AGGIUSTARE
        String path = "src/main/resources/Utenti/Utenti.json";
        File file = new File(path);
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader(file);
        Object fileContextObject = jsonParser.parse(reader);
        JSONObject fileContext = (JSONObject) fileContextObject;
        JSONArray utenti = (JSONArray) fileContext.get("utenti");
        JSONObject jsonUtente;
        StringBuilder risultato = new StringBuilder("*Classifica Karma*:\n\n");
        HashMap<String,Integer> userMap = new HashMap<>();

        for(Object utente:utenti) {
            jsonUtente = (JSONObject) utente;
            userMap.put(jsonUtente.get("name").toString(),Integer.valueOf(jsonUtente.get("karma").toString()));
        }

        Comparator comparator= new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {//le tratta come HashMap quindi provo a trattarli come tale
                Map.Entry<String,Integer> element1 = (Map.Entry<String, Integer>) o1;
                Map.Entry<String,Integer> element2 = (Map.Entry<String,Integer>) o2;
                int value1,value2;
                value1 = Integer.parseInt(String.valueOf(element1.getValue()));
                value2 = Integer.parseInt(String.valueOf(element2.getValue()));
                return Integer.compare(value2,value1);
            }
        };

        LinkedList<Map.Entry<String,Integer>> listaUserMap = new LinkedList<>(userMap.entrySet());
        Collections.sort(listaUserMap,comparator);

        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : listaUserMap)
            sortedMap.put(entry.getKey(), entry.getValue());

        int i=1;
        Pattern pattern = Pattern.compile("[^A-Za-z0-9]");
        Matcher match;
        for(Map.Entry<String,Integer> element:sortedMap.entrySet()){
            match = pattern.matcher(element.getKey());
            if(match.find()){
                risultato.append(i+"° "+element.getKey().replace(match.group(0),"\\"+match.group(0))+": "+element.getValue()+"\n");
            }else{
                risultato.append(i+"° "+element.getKey()+": "+element.getValue()+"\n");
            }
            i++;
        }

        return risultato.toString();
    }

    public void UpdateKarma(long userId, int i) throws IOException, ParseException {
        String path = "src/main/resources/Utenti/Utenti.json";
        File file = new File(path);
        JSONObject fileContext;
        JSONArray users;
        FileReader reader = new FileReader(file);
        JSONParser parser = new JSONParser();
        fileContext = (JSONObject) parser.parse(reader);
        users = (JSONArray) fileContext.get("utenti");
        JSONObject jsonUser;

        reader.close();
        for(Object user : users){
            jsonUser = (JSONObject) user;
            if(Long.parseLong(jsonUser.get("id").toString()) == userId){
                int value = Integer.parseInt(jsonUser.get("karma").toString())+i;
                jsonUser.remove("karma");
                users.remove(jsonUser);
                jsonUser.put("karma",value);
                users.add(jsonUser);
                fileContext.remove("utenti");
                fileContext.put("utenti",users);
                FileWriter writer = new FileWriter(file);
                writer.write(fileContext.toJSONString());
                writer.flush();
                writer.close();
                break;
            }

        }
    }
}
