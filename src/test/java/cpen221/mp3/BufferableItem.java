package cpen221.mp3;

import cpen221.mp3.fsftbuffer.Bufferable;

/**
 * This is a trivial datatype for testing FSFTBuffer.
 */
public class BufferableItem implements Bufferable {
    private final int data;

    public BufferableItem(int input) {
        data = input;
    }

    @Override
    public String id() {
        return String.valueOf(data);
    }
}
