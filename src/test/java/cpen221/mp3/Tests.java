package cpen221.mp3;

import cpen221.mp3.fsftbuffer.*;
import org.junit.Test;

import java.io.InvalidObjectException;

import static org.junit.Assert.assertEquals;

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

}
