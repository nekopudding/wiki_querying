package cpen221.mp3.fsftbuffer;

public class BufferableString implements Bufferable{
    String id;
    String text;

    public BufferableString (String id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getText() { return this.text; }

    @Override
    public String id() {
        return this.id;
    }
}
