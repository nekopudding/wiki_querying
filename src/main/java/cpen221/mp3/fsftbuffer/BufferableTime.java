package cpen221.mp3.fsftbuffer;

public class BufferableTime implements Bufferable {
    String id;
    long time;

    public BufferableTime(String id, long time) {
        this.id = id;
        this.time = time;
    }

    public long getTime() { return this.time; }

    @Override
    public String id() {
        return this.id;
    }
}
