package com.example.gauravgupta.rapidodemo.Modules;

/**
 * Created by gauravgupta on 06/08/17.
 */

public class Distance {
    public String text;
    public int value;

    public Distance(String text, int value) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
