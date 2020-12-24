package cpen221.mp3.server;

import cpen221.mp3.wikimediator.WikiMediator;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
/**
 * Abstraction Function:
 * WikiRequest represents a single request sent by a client. This includes what the client wants
 * to do, and the parameters that are given. Includes timeout clause.
 * id represents the internal id of the request.
 * type represents the specific action requested.
 * query represents the keywords to be searched.
 * limit represents the number of entries in output lists.
 * pageTitle represents the title of the wikipedia page that is requested.
 * timeout represents the time in seconds that a request can last before timing out.
 *
 * Representation Invariant:
 * id, type, query, limit, pageTitle are all Strings.
 *
 * Requires: type must be either "search", "getPage", "zeitgeist", "trending", or "peakLoad30s"
 * timeout must be an integer represented by a string.
 * If type = "search",
 * query must be a non empty string, and limit must be an integer represented by a string.
 *
 * If type = "pageTitle",
 * pageTitle must be a non empty string.
 *
 * If type = "zeitgeist",
 * limit must be an integer represented by a string.
 *
 * If type = "trending",
 * limit must be an integer represented by a string.
 *
 *
 *
 */
public class WikiRequestTimeout {
    private String id;
    private String type;
    private String query;
    private String limit;
    private String pageTitle;
    private String timeout;

    public WikiRequestTimeout(String id, String type, String query, String limit, String timeout){
        this.id = id;
        this.type = type;
        this.query = query;
        this.limit = limit;
        this.timeout = timeout;
    }

    public WikiRequestTimeout(String id, String type, String query, String limit){
        this.id = id;
        this.type = type;
        this.query = query;
        this.limit = limit;
    }

    public WikiRequestTimeout(String id, String type, String pageTitle){
        this.id = id;
        this.type = type;
        this.pageTitle = pageTitle;
    }

    public WikiRequestTimeout(String id, String type){
        this.id = id;
        this.type = type;
    }

    /**
     *  Parses the operation requested, and returns a corresponding WikiReply object.
     * @param wk The WikiMediator instance that the server wraps. Must not be null.
     * @return WikiReply object that represents the three reply fields, id, status, and reponse.
     */
    public WikiReply runOperation(WikiMediator wk){
        Callable<WikiReply> search = () -> {
            return new WikiReply(id, "success", wk.search(this.query, Integer.parseInt(this.limit)));
        };
        Callable<WikiReply> getPage = () -> {
            return new WikiReply(id, "success", wk.getPage(this.pageTitle));
        };
        Callable<WikiReply> zeitgeist = () -> {
            return new WikiReply(id, "success", wk.zeitgeist(Integer.parseInt(this.limit)));
        };
        Callable<WikiReply> trending = () -> {
            return new WikiReply(id,"success", wk.trending(Integer.parseInt(this.limit)));
        };
        Callable<WikiReply> peakLoad30s = () -> {
            return new WikiReply(id, "success", wk.peakLoad30s());
        };
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Future<WikiReply> futureSearch = executorService.submit(search);
        Future<WikiReply> futureGetPage = executorService.submit(getPage);
        Future<WikiReply> futureZeitgeist = executorService.submit(zeitgeist);
        Future<WikiReply> futureTrending = executorService.submit(trending);
        Future<WikiReply> futurePeakLoad30s = executorService.submit(peakLoad30s);

        switch (type) {
            case "search":
                if(limit != null){
                    try {
                        return futureSearch.get(Integer.parseInt(timeout) * 1000, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException | ExecutionException ee) {
                        ee.printStackTrace();
                    } catch (TimeoutException e) {
                        return new WikiReply(id, "failed", "Operation timed out");
                    }
                }
                else{
                    return new WikiReply(id, "failed", "Invalid type of operation.");
                }
            case "getPage":
                try {
                    return futureGetPage.get(Integer.parseInt(timeout)*1000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException | ExecutionException ee) {
                    ee.printStackTrace();
                } catch (TimeoutException e) {
                    return new WikiReply(id, "failed", "Operation timed out");
                }
            case "zeitgeist":
                try {
                    return futureZeitgeist.get(Integer.parseInt(timeout)*1000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException | ExecutionException ee) {
                    ee.printStackTrace();
                } catch (TimeoutException e) {
                    return new WikiReply(id, "failed", "Operation timed out");
                }
            case "trending":
                try {
                    return futureTrending.get(Integer.parseInt(timeout)*1000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException | ExecutionException ee) {
                    ee.printStackTrace();
                } catch (TimeoutException e) {
                    return new WikiReply(id, "failed", "Operation timed out");
                }
            case "peakLoad30s":
                try {
                    return futurePeakLoad30s.get(Integer.parseInt(timeout)*1000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException | ExecutionException ee) {
                    ee.printStackTrace();
                } catch (TimeoutException e) {
                    return new WikiReply(id, "failed", "Operation timed out");
                }
        }
        return new WikiReply(id, "failed", "Invalid type of operation.");
    }

    public String getId(){ return id; }

    public String getType(){ return type; }

    public String getQuery(){ return query; }

    public String getLimit(){ return limit; }

    public String getPageTitle(){ return pageTitle; }


}
