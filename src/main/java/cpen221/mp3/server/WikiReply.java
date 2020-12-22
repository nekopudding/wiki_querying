package cpen221.mp3.server;

public class WikiReply {
    private String id;
    private String status;
    private Object response;

    public WikiReply(String id, String status, Object response){
        this.id = id;
        this.status = status;
        this.response = response;
    }
}
