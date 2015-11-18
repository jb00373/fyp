package com.example.jonathanbriers.musicgenerator;

import android.util.Log;

import java.util.Random;

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
    int numberOfProgressions = 4;
    float tempo;
    int melodySpeed;
    int[] melodySpeeds = new int[] {1, 2, 4};
    int drumSpeed;
    int[] drumSpeeds = new int[] {1, 2, 4};
    boolean progHasHats;
    int notesInChord;
    int beatsBetweenChords = 8;
    int[] beatsInProg;
    int[][][] song;
    Random rand;
    boolean dorian;
    int beats;
    int channels = 10;
    long seed;
    boolean hasDrums;
    boolean progHasDrums;

    void init() {
        rand = new Random(seed);
    }

    void chooseKey() {
        key = rand.nextInt(12);
        String s = numberToNote(key);
        Log.v("key", s);
    }

    void chooseTempo() {
        tempo = rand.nextInt(100);
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
        generateChordProgession(numberOfProgressions, numberOfChords);
        generateMelody();
        if (hasDrums) {
            addDrums();
        }
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

    void generateChordProgession(int numberOfProgressions, int numberOfChords) {
        beatsInProg = new int[numberOfProgressions];
        randomChordProgression = new Chord[numberOfProgressions][numberOfChords];
        int b = 0;
        for (int i = 0; i < numberOfProgressions; i++) {
            chooseNumChordNotes();
            for (int j = 0; j < numberOfChords; j++) {
                randomChordProgression[i][j] = chords[rand.nextInt(6)];
                for (int k = 0; k < notesInChord; k++) {
                    song[i][k][b] = randomChordProgression[i][j].getNotes()[k];
                }
                b += beatsBetweenChords;
                //Debug.Log (randomChordProgression[i,j].getName());
            }
            beatsInProg[i] = numberOfChords * beatsBetweenChords;
            b = 0;
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
        if (rand.nextInt(2) == 1) {
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
                            song[i][9][b] = 5;
                        }
                        //Clap on every half beat
                        else if (b % (beatsBetweenChords/2) == 0) {
                            song[i][9][b] = 2;
                        }
                        //Chance of hihat on others
                        else if (progHasHats) {
                            if (rand.nextInt(3) == 0) {
                                song[i][9][b] = 3;
                            }
                            else if (rand.nextInt(5) == 0) {
                                song[i][9][b] = 4;
                            }
                        }
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