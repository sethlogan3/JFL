package superbot;

public class Kink {
    public String name,category;
    public int id;
    
    public Kink(String kinkName,int kinkId,String cat) {
        name=kinkName;
        id=kinkId;
        category=cat;
    }
    
    public int getID() {
        return id;
    }
    
    @Override public String toString() {
        return name+"- id: "+id+" category: "+category;
    }
}
