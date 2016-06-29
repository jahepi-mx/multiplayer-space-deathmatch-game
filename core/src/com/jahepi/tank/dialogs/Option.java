package com.jahepi.tank.dialogs;

/**
 * Created by jahepi on 28/06/16.
 */
public class Option {

    private int index;
    private String value;

    public Option(int index, String value) {
        this.index = index;
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
