package com.example.jonathanbriers.musicgenerator;

import java.io.ByteArrayOutputStream;

/**
 * Created by Jonny on 04/03/2016.
 */
public class Track {

    ByteArrayOutputStream stream;
    int number;
    int timeSinceLastNote;

    public int getTimeSinceLastNote() {
        return timeSinceLastNote;
    }

    public void setTimeSinceLastNote(int timeSinceLastNote) {
        this.timeSinceLastNote = timeSinceLastNote;
    }

    public Track(int number) {
        stream = new ByteArrayOutputStream();
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public ByteArrayOutputStream getStream() {
        return stream;
    }


}
