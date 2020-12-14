package cpen221.mp3.wikimediator;

import java.util.List;
import org.fastily.jwiki.core.Wiki;


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

    public WikiMediator () {
        wiki = new Wiki.Builder().withDomain("en.wikipedia.org").build();
    }

    /**
     * Given a query, return up to limit page titles that
     * match the query string (per Wikipedia's search service).
     * @param query
     * @param limit
     * @return
     */
    List<String> search(String query, int limit) {
        return wiki.search(query, limit);
    }

    /**
     * Given a pageTitle, return the text associated with
     * the Wikipedia page that matches pageTitle.
     * @param pageTitle
     * @return
     */
    String getPage(String pageTitle) {

        return wiki.getPageText(pageTitle);
    }

    /**
     * Return the most common Strings used in search and
     * getPage requests, with items being sorted in
     * non-increasing count order. When many requests have
     * been made, return only limit items.
     * @param limit
     * @return
     */
    List<String> zeitgeist(int limit) {

        return null;
    }

    /**
     * Similar to zeitgeist(), but returns the most frequent
     * requests made in the last 30 seconds.
     * @param limit
     * @return
     */
    List<String> trending(int limit) {
        return null;
    }

    /**
     * What is the maximum number of requests seen in any
     * 30-second window? The request count is to include
     * all requests made using the public API of WikiMediator,
     * and therefore counts all five methods listed as
     * basic page requests.
     * @return
     */
    int peakLoad30s() {
        return -1;
    }
}
/*
Task 3: Wiki Mediator
will access wikipedia using Jwiki API to obtain pages
should cache pages to minimize network accesses (most likely
using buffer)
also collect statistical info about requests

need to support operations:
 */
