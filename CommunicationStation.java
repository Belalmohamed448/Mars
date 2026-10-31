package lab5;

public class CommunicationStation extends Facility {
    private boolean linkActive;
    public CommunicationStation(String id, String name) {
        super(id, name);
        this.linkActive = true;
    }
    public boolean isLinkActive(){return linkActive;}
    public void setLink(boolean active){ linkActive = active; }
}