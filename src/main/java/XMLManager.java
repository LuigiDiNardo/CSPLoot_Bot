import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.InputMismatchException;
import java.util.Locale;

public class XMLManager {

    private static XMLManager XMLMng = null;


    private XMLManager(){}

    public static XMLManager getXMLManager() throws NullPointerException {
        if(XMLMng == null){
            XMLMng = new XMLManager();
            return XMLMng;
        }
        else {
            throw new NullPointerException();
        }
    }

    private Document getFile(String fileName) throws IOException, ParserConfigurationException, SAXException {
        String path = "src/main/resources/"+fileName+"/"+fileName+".xml";
        File file = new File(path);
        InputStream is = new FileInputStream(file);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        documentBuilder = documentBuilderFactory.newDocumentBuilder();
        return documentBuilder.parse(is);
    }

    public String ManageFileArtefatti() throws IOException, ParserConfigurationException, SAXException {
            StringBuilder risultato= new StringBuilder();
            Document doc=this.getFile("Artefatti");
            NodeList nameNode = doc.getElementsByTagName("nome");
            NodeList emojiNode = doc.getElementsByTagName("emoji");
            NodeList descrizioneNode = doc.getElementsByTagName("descrizione");
            String nome, emoji, descrizione;
            for(int i=0;i<nameNode.getLength();i++){
                nome=nameNode.item(i).getTextContent();
                emoji=emojiNode.item(i).getTextContent();
                descrizione=descrizioneNode.item(i).getTextContent().replace('^','\n');
                risultato.append(emoji).append(" ").append(nome).append("\n").append(descrizione).append("\n");
            }

            return risultato.toString();
    }

    public String ManageFileTalismani(String args)throws IOException, ParserConfigurationException, SAXException, InputMismatchException, NullPointerException {
        StringBuilder risultato = new StringBuilder();
        StringBuilder nome = new StringBuilder();
        StringBuilder rinascita = new StringBuilder();
        StringBuilder descrizione = new StringBuilder();
        NodeList nomeNode, rinascitaNode, descrizioneNode;

        Document doc = this.getFile("Talismani");

        nomeNode = doc.getElementsByTagName("nome");
        rinascitaNode = doc.getElementsByTagName("rinascita");
        descrizioneNode = doc.getElementsByTagName("descrizione");

        if (args == null) {
            for (int i = 0; i < nomeNode.getLength(); i++) {
                nome.append(nomeNode.item(i).getTextContent());
                rinascita.append(rinascitaNode.item(i).getTextContent());
                descrizione.append(descrizioneNode.item(i).getTextContent().replace('^','_'));
                risultato.append(nome + " (R: " + rinascita + "): " + descrizione+"\n");
                nome.delete(0, nome.length());
                descrizione.delete(0, descrizione.length());
                rinascita.delete(0, rinascita.length());
            }
        } else {
            try {
                int research = Integer.parseInt(args);
                if(research>=0 && research<=2) {
                    for (int i = 0; i < nomeNode.getLength(); i++) {
                        if (rinascitaNode.item(i).getTextContent().contains(String.valueOf(research))) {
                            nome.append(nomeNode.item(i).getTextContent());
                            descrizione.append(descrizioneNode.item(i).getTextContent().replace('^','_'));
                            rinascita.append(rinascitaNode.item(i).getTextContent());
                            risultato.append(nome + " (R: " + rinascita + "): " + descrizione+"\n");
                            nome.delete(0, nome.length());
                            descrizione.delete(0, descrizione.length());
                            rinascita.delete(0, rinascita.length());
                        }
                    }
                } else throw new InputMismatchException();
            } catch (NumberFormatException e) {
                boolean found=false;
                for (int i = 0; i < nomeNode.getLength(); i++) {
                    if (descrizioneNode.item(i).getTextContent().toLowerCase().contains(args.toLowerCase())) {
                        nome.append(nomeNode.item(i).getTextContent());
                        descrizione.append(descrizioneNode.item(i).getTextContent().replace('^','_'));
                        rinascita.append(rinascitaNode.item(i).getTextContent());
                        risultato.append(nome + " (R: " + rinascita + "): " + descrizione+"\n");
                        found=true;
                        nome.delete(0, nome.length());
                        descrizione.delete(0, descrizione.length());
                        rinascita.delete(0, rinascita.length());
                    }
                }

                if(!found) throw new InputMismatchException();
            }
        }

        return risultato.toString();
    }

    public String ManageFileIncarichi(String args)throws IOException, ParserConfigurationException, SAXException, InputMismatchException, NullPointerException{
        StringBuilder risultato = new StringBuilder();
        StringBuilder nome = new StringBuilder();
        StringBuilder scelta = new StringBuilder();
        StringBuilder requisiti = new StringBuilder();
        StringBuilder ricompensa = new StringBuilder();
        NodeList nomeNode, sceltaNode, requisitiNode, ricompensaNode;

        Document doc = this.getFile("Incarichi");

        nomeNode = doc.getElementsByTagName("nome");
        requisitiNode = doc.getElementsByTagName("requisiti");
        sceltaNode = doc.getElementsByTagName("scelta");
        ricompensaNode = doc.getElementsByTagName("ricompensa");

        if(args == null) {
            for (int i = 0; i < nomeNode.getLength(); i++) {
                nome.append(nomeNode.item(i).getTextContent());
                scelta.append(sceltaNode.item(i).getTextContent());
                risultato.append(nome + ": " + scelta + "\n");
                nome.delete(0, nome.length());
                scelta.delete(0, scelta.length());
            }
        } else if(!args.toLowerCase().equals("dungeon")){
            {
                int i = 0;
                boolean trovato = false;
                while (i < nomeNode.getLength() && !trovato) {
                    if (nomeNode.item(i).getTextContent().toLowerCase().contains(args.toLowerCase())) {
                        nome.append(nomeNode.item(i).getTextContent());
                        requisiti.append(requisitiNode.item(i).getTextContent().replace('^', '\n'));
                        scelta.append(sceltaNode.item(i).getTextContent());
                        ricompensa.append(ricompensaNode.item(i).getTextContent().replace('^', '\n'));
                        trovato = true;
                    }
                    i++;
                }
                if (trovato)
                    risultato.append(nome + "\n\n*Requisiti*:\n" + requisiti + "\n*Scelta*: " + scelta + "\n*Ricompense*:\n" + ricompensa);
                else throw new NullPointerException();
            }
        }
        else throw new InputMismatchException();

        return risultato.toString();
    }

    public String ShowHelpFile() throws IOException, ParserConfigurationException, SAXException {
        Document doc = this.getFile("Help");
        Node stringa = doc.getElementsByTagName("string").item(0);
        String output = stringa.getTextContent().replace('^','\n').replace('-','`');
        return output;
    }
}
