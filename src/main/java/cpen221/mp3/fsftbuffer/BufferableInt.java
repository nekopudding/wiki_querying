package cpen221.mp3.fsftbuffer;

public class BufferableInt implements Bufferable {
    String id;
    int i;

    public BufferableInt(String id, int i) {
        this.id = id;
        this.i = i;
    }

    public int getInt() { return this.i; }

    public void addCount() { this.i += 1;}

    @Override
    public String id() {
        return this.id;
    }
}

