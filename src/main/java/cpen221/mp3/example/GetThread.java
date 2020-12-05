package cpen221.mp3.example;

import cpen221.mp3.fsftbuffer.Bufferable;
import cpen221.mp3.fsftbuffer.FSFTBuffer;

import java.io.InvalidObjectException;

public class GetThread extends Thread {
    FSFTBuffer f;
    String test;

    public GetThread(FSFTBuffer f, String test) {
        this.f = f;
        this.test = test;
    }

    @Override
    public void run() {


        try {
            for (int j = 0; j < 50; j++) {
               // int finalJ = j;
               // f.get(String.valueOf(finalJ));

                f.get(test);
                Thread.sleep(1000);
            }


        }
        catch (InvalidObjectException | InterruptedException t) {
            System.out.println(t);
        }

    }
}