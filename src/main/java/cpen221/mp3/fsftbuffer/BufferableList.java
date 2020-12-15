package cpen221.mp3.fsftbuffer;

import java.util.List;

public class BufferableList implements Bufferable{
    String id;
    List<String> l;

    public BufferableList(String id, List<String> l) {
        this.id = id;
        this.l = l;
    }

    public List<String> getList() { return this.l; }

    @Override
    public String id() {
        return this.id;
    }
}
