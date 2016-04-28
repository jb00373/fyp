package com.example.jonathanbriers.musicgenerator;

import android.util.Log;

/**
 * Created by Jonny on 30/10/2015.
 */

public class Chord {
    int root;
    int[] notes;
    String name;

    public Chord(int root, int[] notes, String name) {
        this.root = root;
        this.notes = notes;
        this.name = name;
    }

    public Chord(int root, int[] notes) {
        this.root = root;
        this.notes = notes;
    }

    public String getName() {
        return name;
    }

    public int[] getNotes() {
        return notes;
    }

    public void printNotes() {
        for (int i = 0; i < 3; i++) {
            Log.v ("note in chord: ", numberToNote(notes[i]).toString());
        }
    }

    public int getRoot() {
        return root;
    }

    String numberToNote(int note) {
        switch (note % 12) {
            case 0:
                return "C";
            case 1:
                return "C Sharp";
            case 2:
                return "D";
            case 3:
                return "D Sharp";
            case 4:
                return "E";
            case 5:
                return "F";
            case 6:
                return "F Sharp";
            case 7:
                return "G";
            case 8:
                return "G Sharp";
            case 9:
                return "A";
            case 10:
                return "A Sharp";
            case 11:
                return "B";
            case 12:
                return "C";
            case 13:
                return "C Sharp";
            case 14:
                return "D";
            case 15:
                return "D Sharp";
            case 16:
                return "E";
            case 17:
                return "F";
            case 18:
                return "F Sharp";
            case 19:
                return "G";
            case 20:
                return "G Sharp";
            case 21:
                return "A";
            case 22:
                return "A Sharp";
            case 23:
                return "B";
            case 24:
                return "C";
            case 25:
                return "C Sharp";
            case 26:
                return "D";
            case 27:
                return "D Sharp";
            case 28:
                return "E";
            case 29:
                return "F";
            case 30:
                return "F Sharp";
            case 31:
                return "G";
            case 32:
                return "G Sharp";
        }
        return null;
    }

}

