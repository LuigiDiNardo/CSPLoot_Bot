package persistence.Entity;

public class Utente {

    private long id;
    private String tag;
    private int karma;

    public Utente(){};

    public Utente(long id, String tag){
        this.id = id;
        this.karma = 0;
        this.tag = tag;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getKarma() {
        return karma;
    }

    public void setKarma(int karma) {
        this.karma = karma;
    }
}
