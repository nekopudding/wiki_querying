package cpen221.mp3.example;

import cpen221.mp3.example.FSFTRunnable;
import cpen221.mp3.fsftbuffer.Bufferable;
import cpen221.mp3.fsftbuffer.FSFTBuffer;

import java.io.InvalidObjectException;


public class FSFTRunnable<T extends Bufferable> extends FSFTBuffer implements Runnable {
    FSFTBuffer f;

    public FSFTRunnable() {
        f = new FSFTBuffer(100,1000);
    }

    @Override
    public void run() {
        Bufferable a = new Bufferable() {
            @Override
            public String id() {
                return "a";
            }
        };
        Bufferable b = new Bufferable() {
            @Override
            public String id() {
                return "a";
            }
        };
        try {
            f.put(a);
            f.get("a");
            f.put(b);
            f.get("b");
        }
        catch (InvalidObjectException t) {
            System.out.println(t);
        }


    }
}



/*
thread to use buffer methods

use a for loop to create multiple T objects, place them into the buffer using put

at the end also put in a different buffer object

---> using this we'd expect that if we get the last ID, that object would be in the buffer

 */