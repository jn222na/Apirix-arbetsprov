package com.example.jocke.joakim_nilsson_apirix_arbetsprov.Firebase;

/**
 * Created by Jocke on 2016-12-05.
 */

public enum DatabaseStrings {
    SENDCOORDINATES(2),
    REGISTER(3),
    PUSHMESSAGE(4);

    private final int value;

     DatabaseStrings(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
