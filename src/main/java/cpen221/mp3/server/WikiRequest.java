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

public class WikiRequest {
    private String id;
    private String type;
    private String query;
    private String limit;
    private String pageTitle;

    public WikiRequest(String id, String type, String query, String limit, String timeout){
        this.id = id;
        this.type = type;
        this.query = query;
        this.limit = limit;
    }

    public WikiRequest(String id, String type, String query, String limit){
        this.id = id;
        this.type = type;
        this.query = query;
        this.limit = limit;
    }

    public WikiRequest(String id, String type, String pageTitle){
        this.id = id;
        this.type = type;
        this.pageTitle = pageTitle;
    }

    public WikiRequest(String id, String type){
        this.id = id;
        this.type = type;
    }


    public WikiReply runOperation(WikiMediator wk){
        switch (type) {
            case "search":
                return new WikiReply(id, "success", wk.search(this.query, Integer.parseInt(this.limit)));
            case "getPage":
                return new WikiReply(id, "success", wk.getPage(this.pageTitle));
            case "zeitgeist":
                return new WikiReply(id, "success", wk.zeitgeist(Integer.parseInt(this.limit)));
            case "trending":
                return new WikiReply(id,"success", wk.trending(Integer.parseInt(this.limit)));
            case "peakLoad30s":
                return new WikiReply(id, "success", wk.peakLoad30s());
            case "stop":
                return new WikiReply(id, null, "bye");
        }
        return new WikiReply(id, "failed", "Invalid type of operation.");
    }

    public String getId(){ return id; }

    public String getType(){ return type; }

    public String getQuery(){ return query; }

    public String getLimit(){ return limit; }

    public String getPageTitle(){ return pageTitle; }


}
