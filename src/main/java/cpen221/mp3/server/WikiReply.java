package cpen221.mp3.server;


/**
 * Abstraction Function:
 * WikiReply represents a reply to a WikiMediatorServer request.
 * id represents the internal request id.
 * status represents if the request was fulfilled successfully or failed.
 * response represents the object (list, string, or int) that the client requested.
 *
 *
 * Representation Invariant:
 * id and status are strings, reponse is an object.
 * Status is either "success" or "failed".
 * response is a List<String> if the original request was of type = search, zeitgeist, or trending.
 * response is a String if the original request was of type = getPage or the request failed.
 * response is an int if the original request was of the type = peakLoad30s.
 */
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
