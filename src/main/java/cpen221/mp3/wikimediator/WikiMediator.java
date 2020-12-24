package cpen221.mp3.wikimediator;

import java.io.InvalidObjectException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.fastily.jwiki.core.Wiki;
import cpen221.mp3.fsftbuffer.*;
import org.fastily.jwiki.dwrap.Contrib;
import query.QueryBaseListener;
import query.QueryLexer;
import query.QueryParser;


/**
 * Mediator supporting requests made in the en.wikipedia domain
 *
 * Abstraction function:
 *  wiki represents the domain of wikipedia from which all operations are being performed on
 *  searchCache stores the cache of recent search results
 *  getPageCache stores the cache of recent getPage results
 *  pageCount stores the number of times each string was requested in calls to search and getPage
 *  requestTime stores the system times when requests(method calls) were made
 *  queryTime stores queries made with the time they were made
 *
 * Representation Invariant:
 *  wiki, searchCache, getPageCache, pageCount, and requests are not null
 *  The buffer does not contain elements with the same Bufferable id
 *  pageCount does not contain duplicate or empty strings, and the values in the value set >= 1
 *  searchCache only contains BufferableList
 *  getPageCache only contains BufferableString
 *  queryTime and requestTime does not contain duplicate times.
 *
 *
 * Thread Safety Condition:
 *  public observer methods are synchronized to prevent repeated writes to cache when
 *  it can just read from the cache.
 */
public class WikiMediator {

    /* TODO: Implement this datatype

        You must implement the methods with the exact signatures
        as provided in the statement for this mini-project.

        You must add method signatures even for the methods that you
        do not plan to implement. You should provide skeleton implementation
        for those methods, and the skeleton implementation could return
        values like null.

     */

    Wiki wiki;
    FSFTBuffer<Bufferable> searchCache;
    FSFTBuffer<Bufferable> getPageCache;
    Map<String, Integer> pageCount;
    Map<Long, String> queryTime;
    List<Long> requestTime;

    public WikiMediator (int capacity, int timeout) {
        wiki = new Wiki.Builder().withDomain("en.wikipedia.org").build();
        searchCache = new FSFTBuffer<>(capacity, timeout);
        getPageCache = new FSFTBuffer<>(capacity, timeout);
        pageCount = new ConcurrentHashMap<>();
        queryTime = new ConcurrentHashMap<>();
        requestTime = new ArrayList<>();
    }

    public WikiMediator () {
        wiki = new Wiki.Builder().withDomain("en.wikipedia.org").build();
        searchCache = new FSFTBuffer<>();
        getPageCache = new FSFTBuffer<>();
        pageCount = new ConcurrentHashMap<>();
        queryTime = new ConcurrentHashMap<>();
        requestTime = new ArrayList<>();
    }

    public WikiMediator (Map pageCount, Map queryTime, List requestTime){
        wiki = new Wiki.Builder().withDomain("en.wikipedia.org").build();
        searchCache = new FSFTBuffer<>();
        getPageCache = new FSFTBuffer<>();
        this.pageCount = pageCount;
        this.queryTime = queryTime;
        this.requestTime = requestTime;
    }

    /**
     * Given a query, return up to limit page titles that
     * match the query string (per Wikipedia's search service).
     * @param query the term to search for
     * @param limit the maximum number of results to return
     * @return a list of pages matching the query
     *
     * requires: query is not null or empty
     * effects: adds the search results to searchCache
     * and records the request made in pageCount, queryTime, requestTime
     */
    synchronized public List<String> search(String query, int limit) {
        //modify counts
        if (query != null && !query.equals("")) {
            addPageCount(query);
            addQuery(query);
            addRequest();

            //modify searchCache and return list
            try {
                BufferableList l = (BufferableList) searchCache.get(query);
                return l.getList();
            }
            catch (InvalidObjectException e){
                if (e.getMessage().equals("Object not found in FSFT Buffer.")) {
                    BufferableList l = new BufferableList(query, wiki.search(query, limit));
                    searchCache.put(l);
                    return l.getList();
                }
                else {
                    throw new IllegalArgumentException(e.getMessage());
                }
            }
        }
        else {
            return new ArrayList<>();
        }


    }

    /**
     * Given a pageTitle, return the text associated with
     * the Wikipedia page that matches pageTitle.
     * @param pageTitle the page to search for
     * @return the text associated with that page
     *
     * requires: pageTitle is not null or empty
     * effects: adds the pageText to getPageCache
     * and records the request made in pageCount, queryTime, requestTime
     */
    synchronized public String getPage(String pageTitle) {
        //modify counts
        if (pageTitle != null && !pageTitle.equals("")) {
            addPageCount(pageTitle);
            addQuery(pageTitle);
            addRequest();

            //modify getPageCache and return page text
            try {
                BufferableString s = (BufferableString) getPageCache.get(pageTitle);
                return s.getText();
            }
            catch (InvalidObjectException e){
                if (e.getMessage().equals("Object not found in FSFT Buffer.")) {
                    BufferableString s = new BufferableString(pageTitle, wiki.getPageText(pageTitle));
                    getPageCache.put(s);
                    return s.getText();
                }
                else {
                    throw new IllegalArgumentException(e.getMessage());
                }
            }
        }
        else {
            return "";
        }
    }

    /**
     * Helper method for zeitgeist to add one to requested pageTitle
     * @param pageTitle the title to add count to.
     *
     * requires: pageTitle is not null or empty
     * effects: adds 1 to the associated pageTitle in pageCount, or adds it as a new key if
     * not already existing
     */
    private void addPageCount(String pageTitle) {
        if (pageCount.containsKey(pageTitle)) {
            int count = pageCount.get(pageTitle);
            count += 1;
            pageCount.put(pageTitle, count);
        }
        else {
            pageCount.put(pageTitle,1);
        }
    }

    /**
     * Helper method for zeitgeist to get the string with the highest count in pgCount
     * @param pgCount the map to get the maximum value from
     * @return the string associated with the highest value.
     */
    private String getMaxCount(Map<String, Integer> pgCount) {
        String maxString = "";
        for (String s : pgCount.keySet()) {
            if (maxString.equals("") ||
                pgCount.get(s) > pgCount.get(maxString)) {
                maxString = s;
            }
        }
        return maxString;
    }

    /**
     * Return the most common Strings used in search and
     * getPage requests, with items being sorted in
     * non-increasing count order. When many requests have
     * been made, return only limit items.
     * @param limit the maximum number of strings to return
     * @return a list of strings sorted in non-ascending order of the most common Strings requested
     *
     * effects: adds a request to requestTime
     */
    synchronized public List<String> zeitgeist(int limit) {
        addRequest();
        List<String> mostVisited = new ArrayList<>();
        Map<String, Integer> remaining = new ConcurrentHashMap<>(pageCount);

        while (remaining.size() > 0 && mostVisited.size() < limit) {
            String max = getMaxCount(remaining);
            remaining.remove(max);
            mostVisited.add(max);
        }

        return mostVisited;
    }

    /**
     * Helper method for trending which adds the query made with the time it was made to queryTime
     * @param query the query to add
     *
     * requires: query is not empty or null
     *
     * effects: adds the query to queryTime;
     */
    private void addQuery(String query) {
        queryTime.put(System.currentTimeMillis(), query);
    }

    /**
     * Similar to zeitgeist(), but returns the most frequent
     * queries requested in the last 30 seconds.
     * @param limit the maximum number of Strings to return
     * @return a list of the most frequent queries requested with a size up to the limit
     *
     * effects: adds the request to requestTime
     */
    synchronized public List<String> trending(int limit) {
        addRequest();
        List<String> mostVisited = new ArrayList<>();
        Map<String, Integer> queryCount30s = new ConcurrentHashMap<>();

        //get all the requests made in the last 30s;
        for(Long time : queryTime.keySet()) {
            if(time >= System.currentTimeMillis() - 30000) {
                String query = queryTime.get(time);
                if (queryCount30s.containsKey(query)) {
                    queryCount30s.put(query, queryCount30s.get(query) + 1 );
                }
                else {
                    queryCount30s.put(query, 1);
                }
            }
        }

        while (queryCount30s.size() > 0 && mostVisited.size() < limit) {
            String max = getMaxCount(queryCount30s);
            queryCount30s.remove(max);
            mostVisited.add(max);
        }

        return mostVisited;
    }

    private void addRequest() {
        requestTime.add(System.currentTimeMillis());
    }

    /**
     * What is the maximum number of requests seen in any
     * 30-second window? The request count is to include
     * all requests made using the public API of WikiMediator,
     * and therefore counts all five methods listed as
     * basic page requests.
     * @return the maximum number of requests made in any 30s window
     *
     * effects: adds itself as a request made to requestTime
     */
    synchronized public int peakLoad30s() {
        addRequest();

        long timeIn30s;
        int count;
        List<Integer> numReqIn30s = new ArrayList<>();

        for (int i = 0; i < requestTime.size(); i++) {
            timeIn30s = requestTime.get(i) + 30000;
            count = 0;

            for (int j = i; j < requestTime.size() && requestTime.get(j) < timeIn30s; j++) {
                count++;
            }
            numReqIn30s.add(count);
        }
        return getMax30s(numReqIn30s);
    }

    private int getMax30s(List<Integer> numReqIn30s) {
        int max = 0;
        for (Integer i : numReqIn30s) {
            if (i > max)
                max = i;
        }
        return max;
    }

    public Map<String, Integer> getPageCount(){
        Map<String, Integer> copy = new HashMap<>();
        for (String s : this.pageCount.keySet()){
            copy.put(s, this.pageCount.get(s));
        }
        return copy;
    }

    public Map<Long, String> getQueryTime(){
        Map<Long, String> copy = new HashMap<>();
        for (Long s : this.queryTime.keySet()){
            copy.put(s, this.queryTime.get(s));
        }
        return copy;
    }

    public List<Long> getRequestTime(){
        List<Long> copy = new ArrayList<>();
        for (long l : this.requestTime){
            copy.add(l);
        }
        return copy;
    }

    public static List<String> executeQuery(String query) {


        CharStream stream = new ANTLRInputStream(query);
        QueryLexer lexer = new QueryLexer(stream);
        lexer.reportErrorsAsExceptions();
        TokenStream tokens = new CommonTokenStream(lexer);
        QueryParser parser = new QueryParser(tokens);
        parser.reportErrorsAsExceptions();
        ParseTree tree = parser.query();
        System.err.println(tree.toStringTree(parser));
        new ParseTreeWalker().walk(new QueryListener_PrintEverything(), tree);

        ParseTreeWalker walker = new ParseTreeWalker();
        QueryListener_QueryCreator listener = new QueryListener_QueryCreator();
        walker.walk(listener, tree);

        HashSet<String> result = listener.getResult();
        List<String> finalResult = new ArrayList<String>(result);

        return finalResult;
    }

    private static class QueryListener_QueryCreator extends QueryBaseListener {
        Wiki wiki = new Wiki.Builder().withDomain("en.wikipedia.org").build();
        HashSet<String> result = new HashSet<String>();
        String item;

        public void exitItem(QueryParser.ItemContext ctx) {
            item = ctx.getText();
        }

        public void exitCondition(QueryParser.ConditionContext ctx) {
            if (ctx.AND() != null) {
                Iterator<String> it = result.iterator();
                while(it.hasNext()){

                }
            }
        }

        public void exitSimple_condition(QueryParser.Simple_conditionContext ctx) {
            String str = ctx.STRING().toString();
            String text = str.substring(1, str.length() - 1);
            if (ctx.TITLE() != null) {
                //List<String> list = wiki.search(text,-1);

                if (item.equals("category")) {
                    List<String> c = wiki.getCategoriesOnPage(text);
                    for (String s : c) {
                        result.add(s);
                    }

                } else if (item.equals("author")) {
                    result.add(wiki.getLastEditor(text));
                }

            } else if (ctx.AUTHOR() != null) {
                ArrayList<Contrib> c = wiki.getContribs(text, -1, true, false);
                if (item.equals("page")) {
                    for (Contrib s : c) {
                        if (wiki.getLastEditor(s.title).equals(text)) {
                            result.add(s.title);
                        }
                    }

                } else if (item.equals("category")) {
                    for (Contrib s : c) {
                        if (wiki.getLastEditor(s.title).equals(text)) {
                            List<String> category = wiki.getCategoriesOnPage(s.title);
                            for (String t : category) {
                                result.add(t);
                            }
                        }
                    }
                }

            } else if (ctx.CATEGORY() != null) {
                ArrayList<String> c = wiki.getCategoryMembers(text);

                if (item.equals("page")) {
                    for (String s : c) {
                        result.add(s);
                    }

                } else if (item.equals("author")) {
                    for (String s : c) {
                        result.add(wiki.getLastEditor(s));
                    }
                }

            }
        }

        public HashSet<String> getResult() {
            return result;
        }
    }

    private static class QueryListener_PrintEverything extends QueryBaseListener {
        public void enterQuery(QueryParser.QueryContext ctx) {
            System.err.println("entering poly: " + ctx.getText());
        }

        public void exitQuery(QueryParser.QueryContext ctx) {
            System.err.println("exiting poly: " + ctx.getText());
        }

        public void enterItem(QueryParser.ItemContext ctx) {
            System.err.println("entering item: " + ctx.getText());
        }

        public void exitItem(QueryParser.ItemContext ctx) {
            System.err.println("exiting item: " + ctx.getText());
        }

        public void enterCondition(QueryParser.ConditionContext ctx) {
            System.err.println("entering condition: " + ctx.getText());
        }

        public void exitCondition(QueryParser.ConditionContext ctx) {
            System.err.println("exiting condition: " + ctx.getText());
            System.err.println("    AND: " + ctx.AND());
            System.err.println("    OR: " + ctx.OR());
        }

        public void enterSimple_condition(QueryParser.Simple_conditionContext ctx) {
            System.err.println("entering simpleCondition: " + ctx.getText());
        }

        public void exitSimple_condition(QueryParser.Simple_conditionContext ctx) {
            System.err.println("exiting simpleCondition: " + ctx.getText());
            System.err.println("    STRING: " + ctx.STRING());
            System.err.println("    TITLE: " + ctx.TITLE());
            System.err.println("    AUTHOR: " + ctx.AUTHOR());
            System.err.println("    CATEGORY: " + ctx.CATEGORY());
        }

        public void enterSorted(QueryParser.SortedContext ctx) {
            System.err.println("entering sorted: " + ctx.getText());
        }

        public void exitSorted(QueryParser.SortedContext ctx) {
            System.err.println("exiting sorted: " + ctx.getText());
        }
    }

}

