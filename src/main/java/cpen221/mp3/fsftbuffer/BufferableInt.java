package cpen221.mp3.fsftbuffer;

public class BufferableInt implements Bufferable {
    String id;
    int i;

    public BufferableInt(String id, int i) {
        this.id = id;
        this.i = i;
    }

    public int getInt() { return this.i; }

    @Override
    public String id() {
        return this.id;
    }
}

