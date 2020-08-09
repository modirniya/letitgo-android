package app.crows.letitgo;

public class Message {

    private String id;
    private String text;
    private String name;
    private int vote_count;

    public Message() {

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public int getVote_count() {
        return vote_count;
    }

}
