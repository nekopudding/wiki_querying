package cpen221.mp3.fsftbuffer;

public class BufferableTime implements Bufferable {
    String id;
    String name;

    public BufferableTime(String name) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.name = name;
    }

    public String getName() { return this.name; }

    @Override
    public String id() {
        return this.id;
    }
}
