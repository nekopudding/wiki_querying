package cpen221.mp3;

import cpen221.mp3.fsftbuffer.Bufferable;

public class Text implements Bufferable {
    private final String title;
    private String text;

    public Text(String title, String text) {
        this.title = title;
        this.text  = text;
    }

    public String id() {
        return title;
    }

    public String text() {
        return text;
    }

}
