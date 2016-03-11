package com.example.jonathanbriers.musicgenerator;

import android.util.Log;

import java.util.Random;
//import java.awt.*;
//import java.awt.event.*;
//import java.io.*;

/**
 * Created by Jonny on 30/10/2015.
 */
public class Generator {

    int key;
    int[] notes;
    int root;
    Chord I, II, III, IV, V, VI, VII;
    Chord[] chords;
    Chord[][] randomChordProgression;
    int[] scale;
    int numberOfChords = 4;
    int numberOfProgressions = 8;
    int tempo;
    int melodySpeed;
    int[] melodySpeeds = new int[] {4, 8, 16};
    int drumSpeed;
    int[] drumSpeeds = new int[] {4, 8, 16};
    boolean progHasHats;
    int notesInChord = 3;
    int beatsBetweenChords = 16;
    int[] beatsInProg;
    int[][][] song;
    Random rand;
    boolean dorian;
    int beats;
    int channels = 10;
    long seed;
    boolean hasDrums;
    boolean progHasDrums;
    MIDIMaker midi;

    public Generator(MIDIMaker m) {

        rand = new Random(seed);
        midi = m;
    }

    void chooseKey() {
        key = rand.nextInt(12);
        String s = numberToNote(key);
        Log.v("key", s);
    }

    void chooseTempo() {
        tempo = rand.nextInt(80) + 10;
        //tempo = 1;
        Log.d("Tempo: ", "" + tempo);
        midi.setTempo(tempo);
    }

    void chooseHasDrums() {
        if (rand.nextInt(2) == 1) {
            hasDrums = true;
        }
        //else hasdrums = false by default
    }


    void addNotes() {
        notes = new int[] {root, root + 2, root + 4, root + 5, root + 7, root + 9, root + 11};
    }

    void createScale() {
        scale = new int[]
                {root, root + 2, root + 4, root + 5, root + 7, root + 9, root + 11};
    }


    public void newSong() {
        beats = beatsBetweenChords * numberOfChords;
        song = new int[numberOfProgressions][channels][beats];
        chooseTempo();
        chooseKey();
        chooseHasDrums();
        if (key == 0) {
            root = key + 12;
        }
        else {
            root = key;
        }
        createScale();
        createChords();
       // generateChordProgession(numberOfProgressions, numberOfChords);
        generateMusic(numberOfProgressions, numberOfChords);
//        generateMelody();
        if (hasDrums) {
            addDrums();
        }
        midi.gen3Tracks();
    }

    void createChords() {
        if (!dorian) {
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
        else {
            I = new Chord (root, new int[]{root, root + 4, root + 7, root + 11}, numberToNote(root) + " Major");
            root = root + 2;
            II = new Chord (root, new int[]{root, root + 3, root + 7, root + 10}, numberToNote(root) + " Minor");
            root = root + 2;
            III = new Chord (root, new int[]{root, root + 3, root + 7,  root + 10}, numberToNote(root) + " Minor");
            root = root + 1;
            IV = new Chord (root, new int[]{root, root + 4, root + 7,  root + 11}, numberToNote(root) + " Major");
            root = root + 2;
            V = new Chord (root, new int[]{root, root + 3, root + 7, root + 10}, numberToNote(root) + " Major");
            root = root + 2;
            VI = new Chord (root, new int[]{root, root + 3, root + 7, root + 10}, numberToNote(root) + " Minor");
            root = root + 2;
            VII = new Chord (root, new int[]{root,  root + 3, root + 6}, numberToNote(root) + " Diminished");
            chords = new Chord[]{I, II, III, IV, V, VI, VII};
        }
    }

    void chooseMelodySpeed() {
        melodySpeed = melodySpeeds[rand.nextInt(3)];
    }

    //Chooses the number of notes to play from the chorda at a timer
    void chooseNumChordNotes() {
        notesInChord = rand.nextInt(2) + 1;
    }

//    void generateChordProgession(int numberOfProgressions, int numberOfChords) {
//        beatsInProg = new int[numberOfProgressions];
//        randomChordProgression = new Chord[numberOfProgressions][numberOfChords];
//        int b = 0;
//        for (int i = 0; i < numberOfProgressions; i++) {
//            //chooseNumChordNotes();
//            for (int j = 0; j < numberOfChords; j++) {
//                randomChordProgression[i][j] = chords[rand.nextInt(6)];
//                for (int k = 0; k < notesInChord; k++) {
//                    song[i][k][b] = randomChordProgression[i][j].getNotes()[k];
//                    midi.play(randomChordProgression[i][j].getNotes()[k] + 48, beatsBetweenChords);
//                }
//                b += beatsBetweenChords;
//                for (int k = 0; k < notesInChord; k++) {
//                    midi.stop(randomChordProgression[i][j].getNotes()[k] + 48, beatsBetweenChords);
//                }
//                //Debug.Log (randomChordProgression[i,j].getName());
//            }
//            beatsInProg[i] = numberOfChords * beatsBetweenChords;
//            b = 0;
//        }
//        midi.gen();
//    }

    void generateMusic(int numberOfProgressions, int numberOfChords) {
        beatsInProg = new int[numberOfProgressions];
        randomChordProgression = new Chord[numberOfProgressions][numberOfChords];
        int b = 0;
        int r = 0;
        for (int i = 0; i < numberOfProgressions; i++) {
            //chooseNumChordNotes();
            chooseMelodySpeed();
            beatsInProg[i] = numberOfChords * beatsBetweenChords;
            for (int j = 0; j < numberOfChords; j++) {
                randomChordProgression[i][j] = chords[rand.nextInt(6)];
                b = j * beatsBetweenChords;
                for (int k = 0; k < notesInChord; k++) {
                    song[i][k][b] = randomChordProgression[i][j].getNotes()[k];
                    midi.play(randomChordProgression[i][j].getNotes()[k] + 48, 0, midi.getTrack(0));
                }
                //Stop chord
                for (int k = 0; k < notesInChord; k++) {
                    if (k == 0) {
                        midi.stop(randomChordProgression[i][j].getNotes()[k] + 48, beatsBetweenChords, midi.getTrack(0));
                    }
                    else {
                        midi.stop(randomChordProgression[i][j].getNotes()[k] + 48, 0, midi.getTrack(0));
                    }
                }
                for (b = j * beatsBetweenChords; b < (beatsInProg[i]/numberOfChords) * (j + 1); b += melodySpeed) {
                    Log.d("b = ", "" +b);
                    Log.d("less than ", "" + beatsInProg[i]/numberOfChords * (j+1));
                    r = rand.nextInt(7);
                    if (rand.nextInt (3) == 1) {
                        song[i][5][b] = scale[r];
                        midi.play(song[i] [5] [b] + 72, 0, midi.getTrack(1));
                    }
                    else {
                        r = rand.nextInt(3);
                        if (r < 3) {
                            song[i] [5] [b] = randomChordProgression[i] [j].getNotes()[r];
                            midi.play(song[i] [5] [b] + 72, 0, midi.getTrack(1));
                        }
                        //Rest
                        else {
                            midi.play(0, 0, midi.getTrack(1));
                        }
                    }
                    midi.stop(song[i] [5] [b] + 72, melodySpeed, midi.getTrack(1));
                }
            }
            b = 0;
        }
    }





    void generateMelody() {
        for (int i = 0; i < numberOfProgressions; i++) {
            chooseMelodySpeed();
            for (int j = 0; j < numberOfChords; j++) {
                for (int b = j * beatsBetweenChords; b < (beatsInProg[i]/numberOfChords) * (j + 1) ; b += melodySpeed) {
                    if (rand.nextInt (3) == 1 && b % beatsBetweenChords != 0) {
                        int r = rand.nextInt(7);
                        song[i][5][b] = scale[rand.nextInt(7)];
                    }
                    else {
                        int r = rand.nextInt(4);
                        if (r < 3 && b % beatsBetweenChords != 0) {
                            song[i] [5] [b] = randomChordProgression[i] [j].getNotes()[r];
                        }
                    }
                }
            }
        }
    }

    public void chooseDrumSpeed() {
        drumSpeed = drumSpeeds[rand.nextInt(3)];
    }

    public void chooseHasHats() {
        if (rand.nextInt(2) == 0) {
            progHasHats = true;
        }
    }

    void chooseProgHasDrums() {
        int r = rand.nextInt(3);
        Log.d("r", ""+r);
        if (r == 1) {
            progHasDrums = false;
        }
        else {
            progHasDrums = true;
        }
    }

    public void addDrums() {
        for (int i = 0; i < numberOfProgressions; i++) {
            chooseProgHasDrums();
            if (progHasDrums) {
                chooseDrumSpeed();
                chooseHasHats();
                for (int j = 0; j < numberOfChords; j++) {
                    //For b = start of progression to end of progression, incrementing by drumSpeed
                    for (int b = j * beatsBetweenChords; b < (beatsInProg[i]/numberOfChords) * (j + 1) ; b += drumSpeed) {
                        //Kick on every chord
                        if (b % beatsBetweenChords == 0) {
                            song[i][9][b] = 36;
                            midi.play(song[i][9][b], 0, midi.getTrack(2));
//                            midi.stop(song[i][9][b], beatsBetweenChords/2, midi.getTrack(2));

                        }
//                        Clap on every half beat
                        else if (b % (beatsBetweenChords/2) == 0) {
                            song[i][9][b] = 39;
                            midi.play(song[i][9][b], 0, midi.getTrack(2));
//                            midi.stop(song[i] [9] [b], beatsBetweenChords/2, midi.getTrack(2));
                        }
                        //Chance of hihat on others
                        else if (progHasHats) {
                            if (rand.nextInt(3) == 0) {
                                song[i][9][b] = 42;
                                midi.play(song[i][9][b], 0, midi.getTrack(2));

                            }
                            else if (rand.nextInt(5) == 0) {
                                song[i][9][b] = 44;
                                midi.play(song[i][9][b], 0, midi.getTrack(2));

                            }
                            else {
                                song[i][9][b] = 0;
                                midi.play(song[i][9][b], 0, midi.getTrack(2));
                            }
                        }
                        else {
                            song[i][9][b] = 0;
                            midi.play(song[i][9][b], 0, midi.getTrack(2));
                        }
                        midi.stop(song[i] [9] [b], drumSpeed, midi.getTrack(2));
                    }
                }
            }
        }
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

    public int[][][] getSong() {
        return song;
    }

    public int getChannels() {
        return channels;
    }

    public int[] getBeatsInProg() {
        return beatsInProg;
    }

    public int getNumberOfProgressions() {
        return numberOfProgressions;
    }

    public float getTempo() {
        return tempo;
    }

    public void setSeed(long seed) {
        this.seed = seed;
        rand = new Random(seed);
    }

}


//    public void addDrums() {
//        int b = 0;
//        for (int i = 0; i < numberOfProgressions; i++) {
//            for (int j = 0; j < numberOfChords; j++) {
//                if (b % beatsBetweenChords == 0) {
//                    song[i][9][b] = 5;
//                }
//                if (b % beatsBetweenChords  == beatsBetweenChords/2) {
//                    song[i][9][b] = 2;
//                }
//                b += 1;
//            }
//            b = 0;
//        }
//    }

//    void generateMusicOld(int numberOfProgressions, int numberOfChords) {
//        beatsInProg = new int[numberOfProgressions];
//        randomChordProgression = new Chord[numberOfProgressions][numberOfChords];
//        int b = 0;
//        int r = 0;
//        for (int i = 0; i < numberOfProgressions; i++) {
//            //chooseNumChordNotes();
//            chooseMelodySpeed();
//            beatsInProg[i] = numberOfChords * beatsBetweenChords;
//            for (int j = 0; j < numberOfChords; j++) {
//                randomChordProgression[i][j] = chords[rand.nextInt(6)];
//                b = j * beatsBetweenChords;
//                for (int k = 0; k < notesInChord; k++) {
//                    song[i][k][b] = randomChordProgression[i][j].getNotes()[k];
//                    midi.play(randomChordProgression[i][j].getNotes()[k] + 48, beatsBetweenChords);
//                }
//                for (b = b; b < (beatsInProg[i]/numberOfChords) * (j + 1); b += melodySpeed) {
//                    r = rand.nextInt(7);
//                    if (rand.nextInt (3) == 1) {
//                        song[i][5][b] = scale[r];
//                        midi.play(song[i] [5] [b] + 48, melodySpeed);
//                    }
//                    else {
//                        r = rand.nextInt(4);
//                        if (r < 3) {
//                            song[i] [5] [b] = randomChordProgression[i] [j].getNotes()[r];
//                            midi.play(song[i] [5] [b] + 48, melodySpeed);
//                        }
//                        //Rest
//                        else {
//                            midi.play(0, melodySpeed);
//                        }
//                    }
//                    midi.stop(song[i] [5] [b] + 48, melodySpeed);
//                }
//                //b += melodySpeed;
//                //Stop chord
//                for (int k = 0; k < notesInChord; k++) {
//                    midi.stop(randomChordProgression[i][j].getNotes()[k] + 48, 0);
//                }
////                if (b%melodySpeed == 0) {
////                    midi.stop(scale[r] + 72, melodySpeed);
////                }
//                //Debug.Log (randomChordProgression[i,j].getName());
//            }
//
//            b = 0;
//        }
//        midi.gen();
//    }

//    void generateMusic3(int numberOfProgressions, int numberOfChords) {
//        beatsInProg = new int[numberOfProgressions];
//        randomChordProgression = new Chord[numberOfProgressions][numberOfChords];
//        int b = 0;
//        int r = 0;
//        for (int i = 0; i < numberOfProgressions; i++) {
//            //chooseNumChordNotes();
//            chooseMelodySpeed();
//            beatsInProg[i] = numberOfChords * beatsBetweenChords;
//            for (int j = 0; j < numberOfChords; j++) {
//                randomChordProgression[i][j] = chords[rand.nextInt(6)];
//                b = j * beatsBetweenChords;
//                for (int k = 0; k < notesInChord; k++) {
//                    song[i][k][b] = randomChordProgression[i][j].getNotes()[k];
//                    midi.play(randomChordProgression[i][j].getNotes()[k] + 48, 0, midi.getTrack(0));
//                }
//                //Stop chord
//                for (int k = 0; k < notesInChord; k++) {
//                    if (k == 0) {
//                        midi.stop(randomChordProgression[i][j].getNotes()[k] + 48, beatsBetweenChords, midi.getTrack(0));
//                    }
//                    else {
//                        midi.stop(randomChordProgression[i][j].getNotes()[k] + 48, 0, midi.getTrack(0));
//
//                    }
//                }
//                //Melody
//                for (b = j * beatsBetweenChords; b < (beatsInProg[i]/numberOfChords) * (j + 1); b += melodySpeed) {
////                    Log.d("b = ", "" +b);
////                    Log.d("less than ", "" + beatsInProg[i]/numberOfChords * (j+1));
//                    r = rand.nextInt(7);
//                    if (rand.nextInt (3) == 1) {
//                        song[i][5][b] = scale[r];
//                        midi.play(song[i] [5] [b] + 72, 0, midi.getTrack(1));
//                    }
//                    else {
//                        r = rand.nextInt(3);
//                        if (r < 3) {
//                            song[i] [5] [b] = randomChordProgression[i] [j].getNotes()[r];
//                            midi.play(song[i] [5] [b] + 72, 0, midi.getTrack(1));
//                        }
//                        //Rest
//                        else {
//                            midi.play(0, 0, midi.getTrack(1));
//                        }
//                    }
//                    midi.stop(song[i] [5] [b] + 72, melodySpeed, midi.getTrack(1));
//                }
//
//            }
//            b = 0;
//        }
//        midi.gen3Tracks();
//    }
//}