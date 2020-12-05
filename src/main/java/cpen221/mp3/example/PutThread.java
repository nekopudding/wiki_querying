package cpen221.mp3.example;

import cpen221.mp3.fsftbuffer.Bufferable;
import cpen221.mp3.fsftbuffer.FSFTBuffer;

public class PutThread extends Thread {
    FSFTBuffer f;


    public PutThread(FSFTBuffer f) {
        this.f = f;
    }

    @Override
    public void run() {

        for (int j = 0; j < 25; j++) {
            int finalJ = j;
            Bufferable a = new Bufferable() {
                @Override
                public String id() {
                    return String.valueOf(finalJ);
                }

            };
            f.put(a);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Sleep interrupted.");
            }


        }

    }
}