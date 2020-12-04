package cpen221.mp3.example;

import cpen221.mp3.fsftbuffer.Bufferable;
import cpen221.mp3.fsftbuffer.FSFTBuffer;

import java.io.InvalidObjectException;

public class GetThread extends Thread {
    FSFTBuffer f;


    public GetThread(FSFTBuffer f) {
        this.f = f;
    }

    @Override
    public void run() {
        try {
            System.out.println(f.get("a"));
            f.get("b");
            System.out.println("get");
        }
        catch (InvalidObjectException t) {
            System.out.println(t);
        }

    }
}