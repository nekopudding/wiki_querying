package cpen221.mp3;

import cpen221.mp3.fsftbuffer.Bufferable;
import cpen221.mp3.fsftbuffer.FSFTBuffer;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class Task2Grader {
    public static final int DSIZE = 4;      // DSIZE should be greater than 3
    public static final int DTIMEOUT = 2;

    @Test
    public void testPutFirstGetNext() throws InterruptedException {
        FSFTBuffer<BufferableItem> buffer = new FSFTBuffer<>(DSIZE, DTIMEOUT);
        int val = 10;
        BufferableItem bit = new BufferableItem(val);

        Thread putterThread = new Thread(new FSFTBufferPutter(buffer, bit));
        putterThread.start();
        putterThread.join();

        try {
            assertEquals("FSFTBuffer: get does not match put", bit,
                buffer.get(String.valueOf(val)));
        }
        catch (Exception e) {
            fail("FSFTBuffer: No exception expected");
        }
    }

    @Test
    public void testGetFirstPutNext() throws InterruptedException {
        FSFTBuffer<BufferableItem> buffer = new FSFTBuffer<>(DSIZE, DTIMEOUT);
        int val = 10;
        BufferableItem bit = new BufferableItem(val);

        Status status = new Status();

        Thread getterThread = new Thread(new FSFTBufferGetter(buffer, String.valueOf(val), status));
        Thread putterThread = new Thread(new FSFTBufferPutter(buffer, bit));

        getterThread.start();
        Thread.sleep(5);
        putterThread.start();
        getterThread.join();
        putterThread.join();

        assertTrue("FSFTBuffer: get before put should lead to an exception",
            status.fail());
    }

    @Test
    public void testMultiplePutsAndGets() throws InterruptedException {
        FSFTBuffer<BufferableItem> buffer = new FSFTBuffer<>(DSIZE, DTIMEOUT);
        int initialValue = 0;
        int values = DSIZE + 6;
        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<BufferableItem> items = new ArrayList<>();

        for (int val = initialValue; val < initialValue + values; val++) {
            BufferableItem item = new BufferableItem(val);
            items.add(item);
            Thread putterThread = new Thread(new FSFTBufferPutter(buffer, item));
            threads.add(putterThread);
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }

        int itemCount = 0;
        for (BufferableItem item : items) {
            try {
                buffer.get(item.id());
                itemCount++;
            }
            catch (Exception e) {
                // do not count items when they are not in the buffer
            }
        }

        assertEquals("FSFTBuffer: incorrect number of items with multiple threads", DSIZE,
            itemCount);
    }

    @Test
    public void testUpdate() throws InterruptedException {
        FSFTBuffer<Text> buffer = new FSFTBuffer<>(DSIZE, DTIMEOUT);
        String title = "a title";
        Text original0 = new Text(title, "0");

        buffer.put(original0);

        List<Text> modifiedText = new ArrayList<>();
        List<Thread> modifierThreads = new ArrayList<>();
        int modifications = 10;

        for (int i = 0; i < modifications; i++) {
            Text newText = new Text(title, String.valueOf(i));
            modifiedText.add(newText);
            modifierThreads.add(new Thread(new FSFTBufferUpdater(buffer, newText)));
        }

        for (Thread thread : modifierThreads) {
            thread.start();
        }

        for (Thread thread : modifierThreads) {
            thread.join();
        }

        boolean updateOK = false;

        try {
            for (Text t : modifiedText) {
                updateOK = updateOK || t.equals(buffer.get(title));
            }
        }
        catch (Exception e) {
            fail("FSFTBuffer: concurrent update failure");
        }

        assertTrue("FSFTBuffer: concurrent update failure", updateOK);
    }


    private class FSFTBufferPutter implements Runnable {
        private FSFTBuffer buffer;
        private Bufferable item;

        public FSFTBufferPutter(FSFTBuffer buffer, Bufferable item) {
            this.buffer = buffer;
            this.item = item;
        }

        public void run() {
            Thread.yield();
            buffer.put(item);
        }
    }

    private class FSFTBufferUpdater implements Runnable {
        private FSFTBuffer buffer;
        private Bufferable item;

        public FSFTBufferUpdater(FSFTBuffer buffer, Bufferable item) {
            this.buffer = buffer;
            this.item = item;
        }

        public void run() {
            Thread.yield();
            buffer.update(item);
        }
    }


    private class FSFTBufferGetter implements Runnable {
        private FSFTBuffer buffer;
        private String itemId;
        private Status opStatus;

        public FSFTBufferGetter(FSFTBuffer buffer, String itemId, Status status) {
            this.buffer = buffer;
            this.itemId = itemId;
            this.opStatus = status;
        }

        public void run() {
            Thread.yield();
            try {
                buffer.get(itemId);
                opStatus.setTrue();
            }
            catch (Exception e) {
                opStatus.setFalse();
            }
        }
    }


    private class Status {
        private boolean status = false;

        public void setTrue() {
            status = true;
        }

        public void setFalse() {
            status = false;
        }

        public boolean success() {
            return status == true;
        }

        public boolean fail() {
            return status == false;
        }
    }
}