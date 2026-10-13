package com.awisogera.votingpostermaker;

public class TextItem {
    public String text;
    public float x, y, size;
    public int color;
    public boolean shadow = true;
    public TextItem(String text) {
        this.text = text;
        this.x = 100; this.y = 200; this.size = 50; this.color = 0xff000000;
    }
}
