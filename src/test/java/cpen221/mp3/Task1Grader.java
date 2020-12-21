package cpen221.mp3;

import cpen221.mp3.fsftbuffer.*;
import org.junit.Test;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class Task1Grader {

    public static final int DSIZE = 4;      // DSIZE should be greater than 3
    public static final int DTIMEOUT = 2;

    @Test
    public void testSimplePutAndGet() {
        FSFTBuffer<BufferableItem> buffer = new FSFTBuffer<>(DSIZE, DTIMEOUT);
        int val = 10;
        BufferableItem bit = new BufferableItem(val);
        buffer.put(bit);
        try {
            assertEquals("FSFTBuffer: get does not match put", bit, buffer.get(String.valueOf(val)));
        }
        catch (Exception e) {
            fail("FSFTBuffer: No exception expected");
        }
    }

    @Test
    public void testPutAndTimedGet() {
        FSFTBuffer<BufferableItem> buffer = new FSFTBuffer<>(DSIZE, DTIMEOUT);
        int val = 10;
        BufferableItem bit = new BufferableItem(val);
        buffer.put(bit);
        try {
            Thread.sleep((DTIMEOUT - 1) * 1000);
        }
        catch (InterruptedException ie) {
            // should not have anything to do
        }
        try {
            assertEquals("FSFTBuffer: get does not match put", bit, buffer.get(String.valueOf(val)));
        }
        catch (Exception e) {
            fail("FSFTBuffer: No exception expected");
        }
    }

    @Test
    public void testPutAndTimedOutGet() {
        FSFTBuffer<BufferableItem> buffer = new FSFTBuffer<>(DSIZE, DTIMEOUT);
        int val = 10;
        BufferableItem bit = new BufferableItem(val);
        buffer.put(bit);
        try {
            Thread.sleep((DTIMEOUT + 1) * 1000);
        }
        catch (InterruptedException ie) {
            // should not have anything to do
        }
        try {
            buffer.get(String.valueOf(val));
            fail("FSFTBuffer: Item should have timed out");
        }
        catch (Exception e) {
            // test passes
        }
    }

    @Test
    public void testTouch() throws InterruptedException {
        int TIMEOUT = DTIMEOUT + 4;
        FSFTBuffer<BufferableItem> buffer = new FSFTBuffer<>(DSIZE, TIMEOUT);
        int val = 10;
        BufferableItem bit = new BufferableItem(val);
        buffer.put(bit);
        Thread.sleep((TIMEOUT / 2 + 1) * 1000);
        try {
            buffer.touch(bit.id());
        }
        catch (Exception e) {
            fail("FSFTBuffer: unexpected exception in touch");
        }
        Thread.sleep((TIMEOUT / 2) * 1000);
        try {
            buffer.get(String.valueOf(val));
        }
        catch (Exception e) {
            fail("FSFTBuffer: Item should not have timed out");
        }
    }

    @Test
    public void testUpdate() throws InterruptedException {
        FSFTBuffer<Text> buffer = new FSFTBuffer<>(DSIZE, DTIMEOUT);
        Text original = new Text("a title", "some text");
        Text modified = new Text("a title", "some modified text");
        buffer.put(original);
        try {
            buffer.update(modified);
            assertEquals(modified, buffer.get(original.id()));
        }
        catch (Exception e) {
            fail("FSFTBuffer: unexpected exception");
        }
    }

    @Test
    public void testPutGetAndTimedOutGet() throws InterruptedException {
        FSFTBuffer<BufferableItem> buffer = new FSFTBuffer<>(DSIZE, DTIMEOUT);
        int val = 10;
        BufferableItem bit = new BufferableItem(val);
        buffer.put(bit);
        Thread.sleep(DTIMEOUT / 2 * 1000);
        try {
            buffer.get(String.valueOf(val));
        }
        catch (Exception e) {
            fail("FSFTBuffer: No exception expected");
        }
        Thread.sleep((DTIMEOUT / 2 + 1) * 1000);
        try {
            buffer.get(String.valueOf(val));
            fail("FSFTBuffer: Item should have timed out");
        }
        catch (Exception e) {
            // test passes
        }
    }

    @Test
    public void testBufferCapacity() {
        FSFTBuffer<BufferableItem> buffer = new FSFTBuffer<>(DSIZE, DTIMEOUT);
        int initialValue = 0;

        List<BufferableItem> items = new ArrayList<>();

        for (int i = 0; i < DSIZE; i++) {
            BufferableItem item = new BufferableItem(initialValue + i);
            items.add(item);
            buffer.put(item);
        }

        try {
            for (BufferableItem item : items) {
                assertEquals(item, buffer.get(item.id()));
            }
        }
        catch (Exception e) {
            fail("FSFTBuffer: No exception expected");
        }
    }

    @Test
    public void testBufferEviction() throws InterruptedException {
        final int PAUSE_BETWEEN_PUTS = 100;
        FSFTBuffer<BufferableItem> buffer = new FSFTBuffer<>(DSIZE, DTIMEOUT);
        int initialValue = 0;

        List<BufferableItem> items = new ArrayList<>();

        for (int i = 0; i <= DSIZE; i++) {
            BufferableItem item = new BufferableItem(initialValue + i);
            items.add(item);
            buffer.put(item);
            Thread.sleep(PAUSE_BETWEEN_PUTS);
        }

        try {
            for (int i = 1; i <= DSIZE; i++) {
                assertEquals(items.get(i), buffer.get(items.get(i).id()));
            }
        }
        catch (Exception e) {
            fail("FSFTBuffer: No exception expected");
        }

        try {
            buffer.get(items.get(0).id());
            fail("FSFTBuffer: item should have been evicted but was not");
        }
        catch (Exception e) {
            // OK
        }
    }

    @Test
    public void testBufferEvictionAfterGet() throws InterruptedException {
        final int PAUSE_BETWEEN_PUTS = 1000;
        final int TIMEOUT = DTIMEOUT + 12;
        FSFTBuffer<BufferableItem> buffer = new FSFTBuffer<>(DSIZE, TIMEOUT);
        int initialValue = 0;

        List<BufferableItem> items = new ArrayList<>();

        for (int i = 0; i < DSIZE; i++) {
            BufferableItem item = new BufferableItem(initialValue + i);
            items.add(item);
            buffer.put(item);
            Thread.sleep(PAUSE_BETWEEN_PUTS);
        }

        try {
            buffer.get(items.get(0).id());
            buffer.get(items.get(1).id());
            buffer.get(items.get(DSIZE - 1).id());
            BufferableItem item = new BufferableItem(DSIZE);
            items.add(item);
            buffer.put(item);
        }
        catch (Exception e) {
            fail("FSFTBuffer: no exception expected");
        }

        try {
            buffer.get(items.get(2).id());
            fail("FSFTBuffer: item should have been evicted but was not");
        }
        catch (Exception e) {
            // OK
        }

        try {
            assertEquals(items.get(DSIZE), buffer.get(items.get(DSIZE).id()));
        }
        catch (Exception e) {
            fail("FSFTBuffer: No exception expected");
        }
    }

    @Test
    public void testCapacityEvictionAndTimeout() throws InterruptedException {
        final int PAUSE_BETWEEN_PUTS = 3000;
        final int TIMEOUT = DTIMEOUT + 12;
        FSFTBuffer<BufferableItem> buffer = new FSFTBuffer<>(DSIZE, TIMEOUT);
        int initialValue = 0;

        List<BufferableItem> items = new ArrayList<>();

        for (int i = 0; i < DSIZE; i++) {
            BufferableItem item = new BufferableItem(initialValue + i);
            items.add(item);
            buffer.put(item);
            Thread.sleep(PAUSE_BETWEEN_PUTS);
        }

        try {
            buffer.get(items.get(0).id());
            buffer.get(items.get(1).id());
            buffer.get(items.get(DSIZE - 1).id());
            Thread.sleep((TIMEOUT + 1) * 1000);
            BufferableItem item = new BufferableItem(DSIZE);
            items.add(item);
            buffer.put(item);
        }
        catch (Exception e) {
            fail("FSFTBuffer: no exception expected");
        }

        try {
            buffer.get(items.get(0).id());
            fail("FSFTBuffer: item should have timed out");
        }
        catch (Exception e) {
            // OK
        }

        try {
            assertEquals(items.get(DSIZE), buffer.get(items.get(DSIZE).id()));
        }
        catch (Exception e) {
            fail("FSFTBuffer: No exception expected");
        }
    }
}
