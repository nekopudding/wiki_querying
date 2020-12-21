package cpen221.mp3.wikimediator;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.fastily.jwiki.core.Wiki;
import cpen221.mp3.fsftbuffer.*;


/**
 * Mediator supporting requests made in the en.wikipedia domain
 *
 * Abstraction function:
 *  wiki represents the domain of wikipedia from which all operations are being performed on
 *  searchCache stores the cache of recent search results
 *  getPageCache stores the cache of recent getPage results
 *  pageCount stores the number of times each string was requested in calls to search and getPage
 *  requestTime stores the requests (method calls) made along with the system times which they were made
 *
 * Representation Invariant:
 *  wiki, searchCache, getPageCache, pageCount, and requests are not null
 *  The buffer does not contain elements with the same Bufferable id
 *  pageCount does not contain duplicate or empty strings, and the values in the value set >= 1
 *  searchCache only contains BufferableList
 *  getPageCache only contains BufferableString
 *  requestTime does not contain duplicate times.
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
    FSFTBuffer searchCache;
    FSFTBuffer getPageCache;
    Map<String, Integer> pageCount;
    Map<Long, String> requestTime;

    public WikiMediator (int capacity, int timeout) {
        wiki = new Wiki.Builder().withDomain("en.wikipedia.org").build();
        searchCache = new FSFTBuffer(capacity, timeout);
        getPageCache = new FSFTBuffer(capacity, timeout);
        pageCount = new ConcurrentHashMap<>();
        requestTime = new ConcurrentHashMap<>();
    }

    public WikiMediator () {
        wiki = new Wiki.Builder().withDomain("en.wikipedia.org").build();
        searchCache = new FSFTBuffer();
        getPageCache = new FSFTBuffer();
        pageCount = new ConcurrentHashMap<>();
        requestTime = new ConcurrentHashMap<>();
    }

    /**
     * Given a query, return up to limit page titles that
     * match the query string (per Wikipedia's search service).
     * @param query
     * @param limit
     * @return
     */
    synchronized List<String> search(String query, int limit) {
        //modify pageCount
        addPageCount(query);
        addRequest("search");
        //modify searchCache and return list
        try {
            BufferableList l = (BufferableList) searchCache.get(query);
            return l.getList();
        }
        catch (InvalidObjectException e){
            if (e.getMessage() == "Object not found in FSFT Buffer.") {
                Bufferable l = new BufferableList(query, wiki.search(query, limit));
                searchCache.put(l);
                return ((BufferableList) l).getList();
            }
            else {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }

    /**
     * Given a pageTitle, return the text associated with
     * the Wikipedia page that matches pageTitle.
     * @param pageTitle
     * @return
     */
    synchronized String getPage(String pageTitle) {
        //modify pageCount
        addPageCount(pageTitle);
        addRequest("getPage");
        //modify getPageCache and return page text
        try {
            BufferableString s = (BufferableString) getPageCache.get(pageTitle);
            return s.getText();
        }
        catch (InvalidObjectException e){
            if (e.getMessage() == "Object not found in FSFT Buffer.") {
                Bufferable s = new BufferableString(pageTitle, wiki.getPageText(pageTitle));
                getPageCache.put(s);
                return ((BufferableString) s).getText();
            }
            else {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }

    /**
     * Helper method to add one to pageCount each time search and getPage is called.
     * @param pageTitle
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
     * Helper method to get the string with the maximum integer value in stringMap
     * @param stringMap
     * @return
     */
    private String getMaxCount(Map<String, Integer> stringMap) {
        String maxString = "";
        for (String s : stringMap.keySet()) {
            if (maxString == "" ||
                stringMap.get(s) > stringMap.get(maxString)) {
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
     * @param limit
     * @return
     */
    synchronized List<String> zeitgeist(int limit) {
        addRequest("zeitgeist");
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
     * Helper method that adds the request made with its timestamp to requestTime
     * @param req
     */
    private void addRequest(String req) {
        requestTime.put(System.currentTimeMillis(), req);
    }

    /**
     * Similar to zeitgeist(), but returns the most frequent
     * requests made in the last 30 seconds.
     * @param limit
     * @return
     */
    synchronized List<String> trending(int limit) {
        addRequest("trending");
        List<String> mostVisited = new ArrayList<>();
        Map<String, Integer> requestCount30s = new ConcurrentHashMap<>();

        //get all the requests made in the last 30s;
        for(Long time : requestTime.keySet()) {
            if(time >= System.currentTimeMillis() - 30000) {
                String req = requestTime.get(time);
                if (requestCount30s.containsKey(req)) {
                    requestCount30s.put(req, requestCount30s.get(req) + 1 );
                }
                else {
                    requestCount30s.put(req, 1);
                }
            }
        }

        while (requestCount30s.size() > 0 && mostVisited.size() < limit) {
            String max = getMaxCount(requestCount30s);
            requestCount30s.remove(max);
            mostVisited.add(max);
        }

        return mostVisited;
    }

    /**
     * What is the maximum number of requests seen in any
     * 30-second window? The request count is to include
     * all requests made using the public API of WikiMediator,
     * and therefore counts all five methods listed as
     * basic page requests.
     * @return
     */
    synchronized int peakLoad30s() {
        addRequest("peakLoad30s");


        return -1;
    }
}
/*
Task 3: Wiki Mediator
will access wikipedia using Jwiki API to obtain pages
should cache pages to minimize network accesses (most likely
using buffer)
also collect statistical info about requests - trending, peak, and zeitgeist methods

to cache search results, we need a concurrent hashmap mapping search term to list of results
and place it in the buffer

to cache page text, we place the page text as string id in bufferable
the cache also needs to keep track of time and which method calls were made

bufferableInt is used for zeitgeist
BufferableList is used for search
BufferableString is used for getPage

how do we keep track of time?
make a BufferableTime that contains the system time with the requests used (method signatures) - for trending
and peak

requests are only concerned with the names of the methods called, while pageCount is only concerned
with the query and pageTitles used.
https://campuswire.com/c/GA7B1C726/feed/2925 MP3 definition of requests


check - make sure that for zeitgeist, the FSFT buffer get returns the original instance of the bufferable,
not a new object.

since the buffer cannot have duplicate ids, for Bufferable time, the id is the time, and the name is
passed in as a parameter

implement pagecount as a hashmap that adds one to count each time called
implement method to find the maximum of the hashmap and put the max into the list,
then recursively get the next maximum until limit

peakLoad30s
 */
