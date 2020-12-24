package cpen221.mp3;


import cpen221.mp3.wikimediator.WikiMediator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

public class tests_task3 {
    @Test
    public void testSearch_1(){
        WikiMediator WM = new WikiMediator();
        WM.search("Cat", 5);
        WM.search("Cat", 5);
        List<String> l = WM.search("Cat", 5);
        List<String> expectedList = new ArrayList<>();
        expectedList.add("Cat");
        expectedList.add("Cat (disambiguation)");
        expectedList.add("Çat");
        expectedList.add("Ge (surname)");
        expectedList.add("Talk:Cat");

        assertEquals(5, l.size());
        assertEquals(expectedList, l);
    }
    @Test
    public void testSearch_empty(){
        WikiMediator WM = new WikiMediator();
        WM.search("", 5);
        List<String> l = WM.search("Cat", 5);
        List<String> expectedList = new ArrayList<>();
        expectedList.add("Cat");
        expectedList.add("Cat (disambiguation)");
        expectedList.add("Çat");
        expectedList.add("Ge (surname)");
        expectedList.add("Talk:Cat");

        assertEquals(5, l.size());
        assertEquals(expectedList, l);
    }
    @Test
    public void testSearch_null(){
        WikiMediator WM = new WikiMediator();
        WM.search(null, 5);
        List<String> l = WM.search("Cat", 5);
        List<String> expectedList = new ArrayList<>();
        expectedList.add("Cat");
        expectedList.add("Cat (disambiguation)");
        expectedList.add("Çat");
        expectedList.add("Ge (surname)");
        expectedList.add("Talk:Cat");

        assertEquals(5, l.size());
        assertEquals(expectedList, l);
    }

    @Test
    public void testGetPage_1(){
        WikiMediator WM = new WikiMediator(32, 3600);
        WM.getPage("Cat");
        System.out.println(WM.getPage("Cat"));
    }
    @Test
    public void testGetPage_empty(){
        WikiMediator WM = new WikiMediator(32, 3600);
        String s = WM.getPage("");
        assertEquals("", s);
    }
    @Test
    public void testGetPage_null(){
        WikiMediator WM = new WikiMediator(32, 3600);
        WM.getPage(null);
        System.out.println(WM.getPage(null));
    }

    @Test
    public void testZeitgeist_1(){
        WikiMediator WM = new WikiMediator(new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new ArrayList<>());

        WM.getPage("Cat");
        WM.search("Ten", 2);
        WM.search("Cat", 2);
        WM.search("Cat", 2);
        WM.search("Cat", 2);
        WM.search("Bob", 1);

        List<String> l = WM.zeitgeist(5);

        List<String> expected = new ArrayList<>();
        expected.add("Cat"); expected.add("Bob");
        expected.add("Ten");
        assertEquals(expected, l);
    }

    @Test
    public void testZeitgeist_2(){
        WikiMediator WM = new WikiMediator(new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new ArrayList<>());

        WM.getPage("Cat");
        WM.search("Ten", 2);
        WM.search("Cat", 2);
        WM.search("Cat", 2);
        WM.search("Cat", 2);
        WM.search("Bob", 1);

        List<String> l = WM.zeitgeist(1);

        List<String> expected = new ArrayList<>();
        expected.add("Cat");
        assertEquals(expected, l);
    }

    @Test
    public void testTrending_1() {
        WikiMediator WM = new WikiMediator();
        WM.getPage("Cat");
        WM.search("Ten", 2);
        WM.search("Cat", 2);
        WM.search("Cat", 2);
        WM.search("Cat", 2);
        WM.search("Bob", 1);

        List<String> l = WM.trending(2);

        List<String> expected = new ArrayList<>();
        expected.add("Cat"); expected.add("Bob");
        assertEquals(expected, l);
    }
    @Test
    public void testTrending_2() {
        WikiMediator WM = new WikiMediator();
        WM.getPage("Cat");
        WM.search("Ten", 2);
        WM.search("Cat", 2);
        WM.search("Cat", 2);
        try {
            System.out.println("Sleeping...");
            Thread.sleep(30000);

            WM.getPage("Cat");
            WM.search("Cat", 2);
            WM.search("Bob", 1);
        }
        catch (InterruptedException e) {
            System.out.println("Sleep interrupted");
            fail();
        }


        List<String> l = WM.trending(1);

        List<String> expected = new ArrayList<>();
        expected.add("Cat");
        assertEquals(expected, l);
    }

    @Test
    public void testTrending_3() {
        WikiMediator WM = new WikiMediator();
        WM.getPage("Cat");
        WM.search("Ten", 2);
        WM.search("Cat", 2);
        WM.search("Cat", 2);


        WM.getPage("Cat");
        WM.search("Cat", 2);
        WM.search("Bob", 1);


        List<String> l = WM.trending(10);

        List<String> expected = new ArrayList<>();
        expected.add("Cat"); expected.add("Bob"); expected.add("Ten");
        assertEquals(expected, l);
    }

    @Test
    public void testPeak_1(){
        WikiMediator WM = new WikiMediator();
        WM.getPage("Cat");
        WM.search("Ten", 2);
        WM.search("Cat", 2);
        WM.search("Cat", 2);
        WM.search("Cat", 2);
        WM.search("Bob", 1);

        int peak = WM.peakLoad30s();

        assertEquals(7, peak); //7 counting the peak method
    }
    @Test
    public void testPeak_2(){
        WikiMediator WM = new WikiMediator();
        WM.getPage("Cat");
        WM.search("Ten", 2);
        WM.search("Cat", 2);
        try {
            System.out.println("Sleeping...");
            Thread.sleep(30000);
        }
        catch (InterruptedException e) {
            System.out.println("Sleep interrupted");
            fail();
        }
        WM.search("Cat", 2);
        WM.search("Cat", 2);
        WM.search("Bob", 1);

        int peak = WM.peakLoad30s();

        assertEquals(4, peak); //4 counting the peak method
    }

    @Test
    public void test_Coverage() {
        WikiMediator WM = new WikiMediator();

        Map<String, Integer> p = WM.getPageCount();
        Map<Long, String> q = WM.getQueryTime();
        List<Long> r = WM.getRequestTime();

        Map<String, Integer> ep = new ConcurrentHashMap<>();
        Map<Long, String> eq = new ConcurrentHashMap<>();
        List<Long> er = new ArrayList<>();

        if (!ep.equals(p) || !eq.equals(q) || !er.equals(r)) {
            fail();
        }
    }
    @Test
    public void test_Coverage_2() {
        WikiMediator WM = new WikiMediator();
        WM.search("Here", 5);

        WM.getPageCount();
        WM.getQueryTime();
        WM.getRequestTime();
    }

}
/*
test cases:
- covers all 5 methods:
- test boundary cases
 */