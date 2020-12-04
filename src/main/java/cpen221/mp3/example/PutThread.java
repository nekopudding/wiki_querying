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
        for (int i = 0; i < 1000; i++) {
            boolean killthread = false;
            while (!killthread) {
                try {
                    if(f.put(a))
                        System.out.println(a);
                    Thread.sleep(10);
                    if (f.put(b))
                        System.out.println(b);

                } catch (InterruptedException t) {
                    killthread = true;
                }
            }
        }
    }
}