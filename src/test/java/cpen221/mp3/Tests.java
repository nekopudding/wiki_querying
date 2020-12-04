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
    public void test3(){

        int LIMIT = 10;
        FSFTBuffer f = new FSFTBuffer(1,100);

        Thread t = new PutThread(f);
        Thread t2 = new PutThread(f);
        Thread t3 = new GetThread(f);
        Thread t4 = new GetThread(f);

        t.start();
        t2.start();


        t3.start();
        t4.start();

    }

}
