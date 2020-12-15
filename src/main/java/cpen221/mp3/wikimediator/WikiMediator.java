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
 *  pageCount stores the number of times each title was requested within a limited period of time
 *  requests stores the requests (method calls) made along with the system times which they were made
 *
 * Representation Invariant:
 *  wiki, searchCache, getPageCache, pageCount, and requests are not null
 *  The buffer does not contain elements with the same Bufferable id
 *  pageCount only contains BufferableInt, the values of BufferableInt >= 1, and are only modified through
 *      search and getPage requests
 *  searchCache only contains BufferableList
 *  getPageCache only contains BufferableString
 *  requests only contain BufferableTime
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
    FSFTBuffer pageCount;
    FSFTBuffer requests;

    public WikiMediator (int capacity, int timeout) {
        wiki = new Wiki.Builder().withDomain("en.wikipedia.org").build();
        searchCache = new FSFTBuffer(capacity, timeout);
        getPageCache = new FSFTBuffer(capacity, timeout);
        requests = new FSFTBuffer(capacity, timeout);
    }

    public WikiMediator () {
        wiki = new Wiki.Builder().withDomain("en.wikipedia.org").build();
        searchCache = new FSFTBuffer();
        getPageCache = new FSFTBuffer();
        pageCount = new FSFTBuffer();
        requests = new FSFTBuffer();
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
        editPageCount(query);
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
        editPageCount(pageTitle);
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
    private void editPageCount(String pageTitle) {
        try {
            BufferableInt b = (BufferableInt) pageCount.get(pageTitle);
            b.addCount();
        }
        catch (InvalidObjectException e) {
            if (e.getMessage() == "Object not found in FSFT Buffer.") {
                Bufferable b = new BufferableInt(pageTitle, 1);
                pageCount.put(b);
            }
            else {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
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
        List<String> mostVisited = new ArrayList<>();
        List<BufferableInt> l = pageCount.getAll();

        int i = 0;

        //get the maximum count of the list
        while (i < limit && l.size() != 0) {
            BufferableInt max = l.get(0);
            for (BufferableInt b : l) {
                if (b.getInt() > max.getInt())
                    max = b;
            }
            mostVisited.add(max.id());
            l.remove(max);
            i++;
        }

        return mostVisited;
    }

    /**
     * Similar to zeitgeist(), but returns the most frequent
     * requests made in the last 30 seconds.
     * @param limit
     * @return
     */
    synchronized List<String> trending(int limit) {
        List<String> mostVisited = new ArrayList<>();
        List<BufferableInt> l = pageCount.getAll();

        int i = 0;

        //get the maximum count of the list
        while (i < limit && l.size() != 0) {
            BufferableInt max = l.get(0);
            for (BufferableInt b : l) {
                if (b.getInt() > max.getInt())
                    max = b;
            }
            mostVisited.add(max.id());
            l.remove(max);
            i++;
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
 */
