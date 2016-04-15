package com.example.jonathanbriers.musicgenerator;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Jonny on 01/04/2016.
 */
public class Song {

    private int numberOfProgressions;
    private int tempo;
    private int key;
    private ArrayList<Progression> progressions;
    private boolean dorian;
    private boolean mixolydian;
    private boolean hasDrums;
    private Random rand;
    Chord I, II, III, IV, V, VI, VII;
    int root;
    Chord[] chords;
    int[] scale;
    int notesInChords;
    int numberOfChannels;
    int numberOfBars;
    Track[] tracks;
    int maxProgressions = 10;
    Progression intro, verse, chorus, bridge, outro;
    boolean structured;
    boolean hasIntro;
    boolean hasOutro;

    public ArrayList<Progression> getProgressions() {
        return progressions;
    }

    public Song(Random rand) {
        this.rand = rand;
        progressions = new ArrayList<Progression>();
        chooseNumberOfProgressions();
        chooseKey();
        chooseMode();
        createScale();
        createChords();
        Log.d("First chord:", "" + I);
        for (int i = 0; i < scale.length; i++) {
            Log.d("Scale:", "" + numberToNote(scale[i]));
        }
        chooseTempo();
        chooseNumberOfChannels();
        chooseInstruments();
        generateProgressions();
        Log.d("Done", "done");
    }

    public void chooseNumberOfProgressions() {
        numberOfProgressions = rand.nextInt(maxProgressions) + 1;
    }

    public int getNumberOfProgressions() {
        return  numberOfProgressions;
    }

    public void chooseHasDrums() {
        if (numberOfChannels > 2 && rand.nextInt(3) > 0) {
            hasDrums = true;
        }
        else {
            hasDrums = false;
        }
    }

    public void chooseInstruments() {
        int j = numberOfChannels;
        if (hasDrums) {
            j--;
        }
        for (int i = 0; i < j; i++) {
            //Bass
            if (i == 2) {
                int r = rand.nextInt(9);
                tracks[i].setInstrument(32 + r);
            }
            else {
                int r = rand.nextInt(96);
                tracks[i].setInstrument(r);
                Log.d("Generator instrument:", "" + r);
            }
        }
    }

    void generateProgressions() {
        if (!structured) {
            for (int i = 0; i < numberOfProgressions; i++) {
                chooseNumberOfBars();
                Progression p = new Progression(numberOfBars, numberOfChannels, rand, chords, notesInChords, scale);
                progressions.add(i, p);
            }
        }
        else {
            chooseStructure();
        }
    }

    void chooseStructure() {
        verse = new Progression(numberOfBars, numberOfChannels, rand, chords, notesInChords, scale);
        chorus = new Progression(numberOfBars, numberOfChannels, rand, chords, notesInChords, scale);
        bridge = new Progression(numberOfBars, numberOfChannels, rand, chords, notesInChords, scale);
        chooseHasIntro();
        chooseHasOutro();
        int start = 0;
        int end = numberOfProgressions - 1;
        if (hasIntro) {
            int r = rand.nextInt(2);
            if (r == 0) {
                intro = verse.stripDown();
            }
            else if (r == 1) {
                intro = chorus.stripDown();
            }
            progressions.add(0, intro);
            start = start + 1;
        }
        if (hasOutro) {
            outro = new Progression(numberOfBars, numberOfChannels, rand, chords, notesInChords, scale);
            progressions.add(numberOfProgressions - 1, outro);
            end = end - 1;
        }
        if (rand.nextInt(2) == 0) {
            for (int i = start; i + 1 < end; i++) {
                progressions.add(i, chorus);
                progressions.add(i + 1, verse);
            }
        }
        else {
            for (int i = start; i + 1 < end; i++) {
                progressions.add(i, verse);
                progressions.add(i + 1, chorus);
            }
        }

    }

    void chooseHasIntro() {
        if (rand.nextInt(2) == 0) {
            hasIntro = true;
        }
        else {
            hasIntro = false;
        }
    }

    void chooseHasOutro() {
        if (rand.nextInt(2) == 0) {
            hasOutro = true;
        }
        else {
            hasOutro = false;
        }
    }

    void chooseTempo() {
        //????
    }

    void chooseNumberOfBars() {
        int[] numbersOfBars = {4, 8};
        numberOfBars = numbersOfBars[rand.nextInt(numbersOfBars.length)];
    }


    void chooseNumberOfChannels() {
        int[] numbersOfChannels = {4, 5, 6, 7 , 8 ,9};
        numberOfChannels = numbersOfChannels[rand.nextInt(numbersOfChannels.length)];
        tracks = new Track[numberOfChannels];
        Log.d("Number of channels", "" + numbersOfChannels);
        chooseHasDrums();
        for (int i = 0; i < numberOfChannels - 1; i++) {
            tracks[i] = new Track(i);
            tracks[i].getStream().reset();
        }
        if (hasDrums) {
            tracks[numberOfChannels - 1] = new Track(9);
            tracks[numberOfChannels - 1].getStream().reset();
        }
        else {
            tracks[numberOfChannels - 1] = new Track(numberOfChannels - 1);
            tracks[numberOfChannels - 1].getStream().reset();
        }
    }

    public int getTempo() {
        return  tempo;
    }

    public int getKey() {
        return key;
    }

    public boolean getMixolydian() {
        return  mixolydian;
    }

    public boolean getDorian() {
        return dorian;
    }

    public int getNumberOfChannels() {
        return numberOfChannels;
    }

    public Track[] getTracks() {
        return tracks;
    }

    void chooseKey() {
        key = rand.nextInt(12);
        String s = numberToNote(key);
        Log.v("key", s);
        if (key == 0) {
            root = key + 12;
        }
        else {
            root = key;
        }
    }

    void chooseMode() {
        int r = rand.nextInt(5);
        if (r == 0) {
            dorian = true;
            notesInChords = 4;
            Log.d("Mode:", "Dorian");
        }
        else if (r == 1) {
            mixolydian = true;
            notesInChords = 4;
            Log.d("Mode:" , "Mixolydian");
        }
        else {
            dorian = mixolydian = false;
            notesInChords = 3;
        }
    }

    void createScale() {
        if (dorian) {
            scale = new int[]
                    {root, root + 2, root + 5, root + 7, root + 9, root + 11};
        }
        else if (mixolydian) {
            scale = new int[] {
                    root, root + 2, root + 4, root + 5, root + 7, root + 9, root + 10};
        }
        else {
            scale = new int[]
                    {root, root + 2, root + 4, root + 5, root + 7, root + 9, root + 11};
        }
    }

    void createChords() {
        if (dorian) {
            I = new Chord (root, new int[]{root, root + 4, root + 7, root + 11}, numberToNote(root) + " Major 7th");
            root = root + 2;
            II = new Chord (root, new int[]{root, root + 3, root + 7, root + 10}, numberToNote(root) + " Minor 7th");
            root = root + 2;
            III = new Chord (root, new int[]{root, root + 3, root + 7,  root + 10}, numberToNote(root) + " Minor 7th");
            root = root + 1;
            IV = new Chord (root, new int[]{root, root + 4, root + 7,  root + 11}, numberToNote(root) + " Major 7th");
            root = root + 2;
            V = new Chord (root, new int[]{root, root + 4, root + 7, root + 10}, numberToNote(root) + " 7th");
            root = root + 2;
            VI = new Chord (root, new int[]{root, root + 3, root + 7, root + 10}, numberToNote(root) + " Minor 7th");
            root = root + 2;
            VII = new Chord (root, new int[]{root,  root + 3, root + 6, root + 10}, numberToNote(root) + " Diminished");
            chords = new Chord[]{I, II, III, IV, V, VI};

        }
        else if (mixolydian) {
            I = new Chord (root, new int[]{root, root + 4, root + 7, root + 10}, numberToNote(root) + " 7th");
            root = root + 2;
            II = new Chord (root, new int[]{root, root + 3, root + 7, root + 10}, numberToNote(root) + " Minor 7th");
            root = root + 2;
            III = new Chord (root, new int[]{root, root + 3, root + 7,  root + 10}, numberToNote(root) + " Minor 7th");
            root = root + 1;
            IV = new Chord (root, new int[]{root, root + 4, root + 7,  root + 10}, numberToNote(root) + " 7th");
            root = root + 2;
            V = new Chord (root, new int[]{root, root + 4, root + 2, root + 9}, numberToNote(root) + " 9th");
            root = root + 2;
            VI = new Chord (root, new int[]{root, root + 3, root + 7, root + 10}, numberToNote(root) + " Minor 7th");
            root = root + 2;
            VII = new Chord (root, new int[]{root,  root + 3, root + 6, root + 10}, numberToNote(root) + " Diminished");
            chords = new Chord[]{I, II, III, IV, V, VI};
        }
        else {
            I = new Chord (root, new int[]{root, root + 4, root + 7}, numberToNote(root) + " Major");
            root = root + 2;
            II = new Chord (root, new int[]{root, root + 3, root + 7}, numberToNote(root) + " Minor");
            root = root + 2;
            III = new Chord (root, new int[]{root, root + 3, root + 7}, numberToNote(root) + " Minor");
            root = root + 1;
            IV = new Chord (root, new int[]{root, root + 4, root + 7}, numberToNote(root) + " Major");
            root = root + 2;
            V = new Chord (root, new int[]{root, root + 4, root + 7}, numberToNote(root) + " Major");
            root = root + 2;
            VI = new Chord (root, new int[]{root, root + 3, root + 7}, numberToNote(root) + " Minor");
            root = root + 2;
            VII = new Chord (root, new int[]{root, root + 3, root + 6}, numberToNote(root) + " Diminished");
            chords = new Chord[]{I, II, III, IV, V, VI};//, VII};
        }
    }

    public boolean getHasDrums() {
        return hasDrums;
    }


    String numberToNote(int note) {
        switch (note) {
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
        }
        return null;
    }
}
