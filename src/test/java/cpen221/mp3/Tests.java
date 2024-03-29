package cpen221.mp3;

import cpen221.mp3.example.GetThread;
import cpen221.mp3.example.PutThread;
import cpen221.mp3.fsftbuffer.*;
import org.junit.Test;

import java.io.InvalidObjectException;

import static org.junit.Assert.*;

public class Tests {

    /*
        You can add your tests here.
        Remember to import the packages that you need, such
        as cpen221.mp3.fsftbuffer.
     */
    @Test
    public void test1(){
        Bufferable a = new Bufferable() {
            @Override
            public String id() {
                return "a";
            }
        };
        FSFTBuffer<Bufferable> buffer = new FSFTBuffer<>(5, 8);
        buffer.put(a);
        try {
            Thread.sleep(3000);
        }
        catch (InterruptedException e){
            System.out.println("Thread interrupted.");
        }
        try {
            assertEquals(buffer.get("a"), a);
        }
        catch (InvalidObjectException e){
            System.out.println("Could not find 'a'.");
            assertEquals(0,1);
        }
        try {
            Thread.sleep(6000);
        }
        catch (InterruptedException e){
            System.out.println("Thread interrupted.");
        }
        try {
            assertEquals(buffer.get("a"), a);
        }
        catch (InvalidObjectException e){
            System.out.println("Could not find 'a'.");
            assertEquals(0,1);
        }
        try {
            Thread.sleep(10000);
        }
        catch (InterruptedException e){
            System.out.println("Thread interrupted.");
        }
        try {
            assertEquals(buffer.get("a"), null);
        }
        catch (InvalidObjectException e){
            System.out.println("Could not find 'a'. (Correct)");
            assertEquals(1,1);
        }

    }

    @Test
    public void testCapacity(){
        Bufferable a = new Bufferable() {
            @Override
            public String id() {
                return "a";
            }
        };
        Bufferable b = new Bufferable() {
            @Override
            public String id() {
                return "b";
            }
        };
        Bufferable c = new Bufferable(){
            @Override
            public String id() {
                return "c";
            }
        };
        Bufferable d = new Bufferable() {
            @Override
            public String id() {
                return "d";
            }
        };
        FSFTBuffer<Bufferable> buffer = new FSFTBuffer<>(3, 8);
        buffer.put(a);
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException e){
            System.out.println("Thread interrupted.");
        }
        buffer.put(b);
        buffer.put(c);
        buffer.put(d);
        try {
            assertEquals(buffer.get("a"), null);
        }
        catch (InvalidObjectException e){
            System.out.println("Could not find 'a'. (Correct)");
            assertEquals(1,1);
        }
        try {
            assertEquals(buffer.get("b"), b);
        }
        catch (InvalidObjectException e){
            System.out.println("Could not find 'b'.");
            assertEquals(0,1);
        }
        try {
            assertEquals(buffer.get("c"), c);
        }
        catch (InvalidObjectException e){
            System.out.println("Could not find 'c'.");
            assertEquals(0,1);
        }
        try {
            assertEquals(buffer.get("d"), d);
        }
        catch (InvalidObjectException e){
            System.out.println("Could not find 'd'.");
            assertEquals(0,1);
        }

    }

    @Test
    public void test2(){
        Bufferable a = new Bufferable() {
            @Override
            public String id() {
                return "a";
            }
        };
        FSFTBuffer<Bufferable> buffer = new FSFTBuffer<>(5, 8);
        buffer.put(a);
        try {
            Thread.sleep(3000);
        }
        catch (InterruptedException e){
            System.out.println("Thread interrupted.");
        }
        assertTrue(buffer.touch("a"));
        try {
            Thread.sleep(6000);
        }
        catch (InterruptedException e){
            System.out.println("Thread interrupted.");
        }
        assertTrue(buffer.update(a));
        try {
            Thread.sleep(10000);
        }
        catch (InterruptedException e){
            System.out.println("Thread interrupted.");
        }
        try {
            assertEquals(buffer.get("a"), null);
        }
        catch (InvalidObjectException e){
            System.out.println("Could not find 'a'. (Correct)");
            assertEquals(1,1);
        }

    }
    @Test
    public void test2Put(){
        //if the put order is the same as the order of removal once
        //max capacity is reached, then the test succeeds
        FSFTBuffer<Bufferable> f = new FSFTBuffer<>();

        Thread t = new PutThread(f);
        Thread t2 = new PutThread(f);

        t.start();
        try {
            Thread.sleep(500);
        }
        catch (InterruptedException e){
            System.out.println("Thread interrupted.");
            fail();
        }
        t2.start();

        try {
            Thread.sleep(30000);
            t.join();
            t2.join();
        }
        catch (InterruptedException e){
            System.out.println("Thread interrupted.");
            fail();
        }


    }

    @Test
    public void test2Get(){
        //test to see if the get methods work concurrently
        FSFTBuffer<Bufferable>f = new FSFTBuffer<>(10, 3600);

        Bufferable a = new Bufferable() {
            @Override
            public String id() {
                return "1";
            }
        };
        Bufferable b = new Bufferable() {
            @Override
            public String id() {
                return "2";
            }
        };

        f.put(a);
        f.put(b);

        Thread t = new GetThread(f, "1");
        Thread t2 = new GetThread(f,"2");

        t.start();
        try {
            Thread.sleep(500);
        }
        catch (InterruptedException e){
            System.out.println("Thread interrupted.");
            fail();
        }
        t2.start();

        try {
            Thread.sleep(10000);
            t.join();
            t2.join();
        }
        catch (InterruptedException e){
            System.out.println("Thread interrupted.");
            fail();
        }
    }

    @Test
    public void test_PutGet(){
        //test to see if put and get work concurrently
        FSFTBuffer<Bufferable> f = new FSFTBuffer<>(10, 3600);

        Thread t = new PutThread(f);
        Thread t2 = new GetThread(f,"1");

        t.start();
        try {
            Thread.sleep(2100);
        }
        catch (InterruptedException e){
            System.out.println("Thread interrupted.");
            fail();
        }
        t2.start();

        try {
            Thread.sleep(10000);
            t.join();
            t2.join();
        }
        catch (InterruptedException e){
            System.out.println("Thread interrupted.");
            fail();
        }

    }

    @Test
    public void test_get(){

        FSFTBuffer<Bufferable> f = new FSFTBuffer<>(1, 1000);

        GetThread t = new GetThread(f,"1");

        Bufferable b = new Bufferable() {
            @Override
            public String id() {
                return "1";
            }
        };
        f.put(b);

        t.start();

        try {
            Thread.sleep(1000);
            t.join();
        } catch (InterruptedException ie){
            System.out.println("Sleep Interrupted");
            fail();
        }

    }

}
