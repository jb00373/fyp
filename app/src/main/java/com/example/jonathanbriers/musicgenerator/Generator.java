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
    int[] melodySpeeds = new int[] {1, 2, 4, 8};
    int drumSpeed;
    int[] drumSpeeds = new int[] {2, 4, 8, 16};
    int[] chordTimes = new int[] {1, 2, 4, 8};
    boolean progHasHats;
    int notesInChord = 3;
    int beatsBetweenChords = 16;
    int[] beatsInProg;
    int[][][] song;
    Random rand;
    boolean dorian;
    boolean mixolydian;
    int beats;
    int channels = 10;
    long seed;
    boolean hasDrums;
    boolean progHasDrums;
    boolean arpeggio;
    boolean arpeggio2;
    MIDIMaker midi;
    int timesChordPlayed;
    int numberOfTracks;
    Song theSong;

    public Generator(MIDIMaker m) {

        rand = new Random(seed);
        midi = m;
    }

//    void chooseKey() {
//        key = rand.nextInt(12);
//        String s = numberToNote(key);
//        Log.v("key", s);
//    }

    void arrayToMIDI() {
        for (Progression p:theSong.getProgressions()) {
            int[][][][] pm = p.getProgression();
            //Chords/Channel0
            for (int bar = 0; bar < p.getNumberOfBars(); bar++) {
                notesInChord = p.getNotesInChords();
                timesChordPlayed = p.getTimesChordPlayed();
                int beatsInBar = p.getBeatsInBar();
                if (p.getArpeggio()) {
                    for (int beat = 0; beat < beatsInBar; beat += beatsInBar / timesChordPlayed / notesInChord) {
                        midi.play(pm[bar][beat][0][0], 0, midi.getTrack(0));
                        midi.stop(pm[bar][beat][0][0], beatsInBar / timesChordPlayed / notesInChord, midi.getTrack(0));
                    }
                }
                else {
                    for (int beat = 0; beat < beatsInBar; beat += beatsInBar/timesChordPlayed) {
                        for (int n = 0; n < notesInChord; n++) {
                            midi.play(pm[bar][beat][0][n], 0, midi.getTrack(0));
                        }
                        for (int n = 0; n < notesInChord; n++) {
                            if (n == 0) {
                                midi.stop(pm[bar][beat][0][n], beatsInBar / timesChordPlayed, midi.getTrack(0));
                            } else {
                                midi.stop(pm[bar][beat][0][n], 0, midi.getTrack(0));
                            }
                        }
                    }
                }
            }
            //Melody/Channel1
            for (int bar = 0; bar < p.getNumberOfBars(); bar++) {
                notesInChord = p.getNotesInChords();
                timesChordPlayed = p.getTimesChordPlayed();
                int beatsInBar = p.getBeatsInBar();
                int melodySpeed = p.getMelodySpeed();
//                boolean rest = false;
                for (int beat = 0; beat < beatsInBar; beat += melodySpeed) {
                    if (pm[bar][beat][1][0] < 0) {
                        midi.play(0, 0, midi.getTrack(1));
                        midi.stop(0, melodySpeed, midi.getTrack(1));
//                        rest = true;
                    }
                    else {
                        midi.play(pm[bar][beat][1][0], 0, midi.getTrack(1));
                        midi.stop(pm[bar][beat][1][0], melodySpeed, midi.getTrack((1)));
                    }
                }
            }
            //Bass/Channel2
            if (p.getHasBass()) {
                for (int bar = 0; bar < p.getNumberOfBars(); bar++) {
                    int beatsInBar = p.getBeatsInBar();
                    int bassSpeed = p.getBassSpeed();
                    for (int beat = 0; beat < beatsInBar; beat += bassSpeed) {
                        if (pm[bar][beat][2][0] < 0) {
                            midi.play(0, 0, midi.getTrack(2));
                            midi.stop(0, bassSpeed, midi.getTrack(2));
//                        rest = true;
                        }
                        else {
                            midi.play(pm[bar][beat][2][0], 0, midi.getTrack(2));
                            midi.stop(pm[bar][beat][2][0], bassSpeed, midi.getTrack((2)));
                        }
                    }
                }
            }
            //Drums
            if (p.getHasDrums()) {
                Log.d("hasDrums = ", ""+p.getHasDrums());
                for (int bar = 0; bar < p.getNumberOfBars(); bar++) {
                    int beatsInBar = p.getBeatsInBar();
                    int drumSpeed = p.getDrumSpeed();
                    if (p.getHatSpeed() > drumSpeed) {
                        drumSpeed = p.getHatSpeed();
                    }
                    int drumTrack = theSong.getNumberOfChannels() - 1;
                    for (int beat = 0; beat < beatsInBar; beat += drumSpeed) {
                        //Rest
                        if (pm[bar][beat][drumTrack][0] < 0) {
                            midi.play(0, 0, midi.getTrack(drumTrack));
                            midi.stop(0, drumSpeed, midi.getTrack(drumTrack));
                        }
                        else {
                            if (p.getHasHats()) {
                                if (pm[bar][beat][drumTrack][0] != 0) {
                                    midi.play(pm[bar][beat][drumTrack][0], 0, midi.getTrack(drumTrack));
                                    midi.play(pm[bar][beat][drumTrack][1], 0, midi.getTrack(drumTrack));
                                    midi.stop(pm[bar][beat][drumTrack][1], drumSpeed, midi.getTrack(drumTrack));
                                    midi.stop(pm[bar][beat][drumTrack][0], 0, midi.getTrack(drumTrack));
                                }
                                else {
                                    midi.play(pm[bar][beat][drumTrack][1], 0, midi.getTrack(drumTrack));
                                    midi.stop(pm[bar][beat][drumTrack][1], drumSpeed, midi.getTrack(drumTrack));
                                }
                            }
                            else {
                                midi.play(pm[bar][beat][drumTrack][0], 0, midi.getTrack(drumTrack));
                                midi.stop(pm[bar][beat][drumTrack][0], drumSpeed, midi.getTrack(drumTrack));
                            }
                        }
                    }
                }
            }
        }
    }

//    void chooseNumberOfTracks() {
//        numberOfTracks = 3;
////    }

    void chooseTempo() {
//        tempo = rand.nextInt(200) + 200;
        //tempo = 1;
        tempo = 800;
        Log.d("Tempo: ", "" + tempo);
        midi.setTempo(tempo);
    }

//    void chooseHasDrums() {
//        if (numberOfTracks > 2 && rand.nextInt(3) > 0) {
//            hasDrums = true;
//        }
//        else {
//            hasDrums = false;
//        }
//        //else hasdrums = false by default
//    }
//
//    void chooseTimesChordPlayed() {
//        timesChordPlayed = chordTimes[rand.nextInt(3)];
//    }
//
//
//    void addNotes() {
//        notes = new int[] {root, root + 2, root + 4, root + 5, root + 7, root + 9, root + 11};
//    }
//
//    void createScale() {
//        if (dorian) {
//            scale = new int[]
//                    {root, root + 2, root + 4, root + 6, root + 7, root + 9, root + 11};
//        }
//        else if (mixolydian) {
//            scale = new int[] {
//                    root, root + 2, root + root + 4, root + 5, root + 7, root + 9, root + 10};
//            }
//        else {
//            scale = new int[]
//                    {root, root + 2, root + 4, root + 5, root + 7, root + 9, root + 11};
//        }
//
//    }
//
//    void chooseMode() {
//        int r = rand.nextInt(5);
//        if (r == 0) {
//            dorian = true;
//            notesInChord = 4;
//            Log.d("Mode:", "Dorian");
//        }
//        else if (r == 1) {
//            mixolydian = true;
//            notesInChord = 4;
//            Log.d("Mode:" , "Mixolydian");
//        }
//    }
//
//    void chooseProgHasArpeggio() {
//        int r = rand.nextInt(6);
//        if (r == 0 && notesInChord == 4) {
//            arpeggio = true;
//            Log.d("Arpeggio2: ", "true!");
//        }
//        else if (r == 1) {
//            arpeggio2 = true;
//            Log.d("Arpeggio: ", "true!");
//        }
//        else {
//            arpeggio = false;
//            arpeggio2 = false;
//        }
//    }
//
//    public void chooseInstruments() {
//        for (int i = 0; i < numberOfTracks; i++) {
//            int r = rand.nextInt(96);
//            midi.getTrack(i).setInstrument(r);
//            Log.d("Generator instrument:", "" + r);
//        }
//    }

    public Song getSong() {
        return theSong;
    }



    public void newSong() {
//        beats = beatsBetweenChords * numberOfChords;
//        song = new int[numberOfProgressions][channels][beats];
        chooseTempo();
//        chooseKey();
//        chooseNumberOfTracks();
//        chooseHasDrums();
//        if (key == 0) {
//            root = key + 12;
//        }
//        else {
//            root = key;
//        }
//        chooseMode();
//        createScale();
//        createChords();
//       // generateChordProgession(numberOfProgressions, numberOfChords);
//        midi.setNumberOfTracks(numberOfTracks, hasDrums);
//        generateChords(numberOfProgressions, numberOfChords);
//        hasDrums = true;
//        if (hasDrums) {
//            addDrums2();
//        }
//        generateMelody2();
////        generateMelody();
//        chooseInstruments();
//        midi.gen(hasDrums);
//        printSong();
//        midi.gen2Tracks();

        //New Version
        theSong = new Song(rand);
        midi.setTracks(theSong.getTracks());
        arrayToMIDI();
        midi.gen(theSong.getHasDrums());
    }

//    void createChords() {
//        if (dorian) {
//            I = new Chord (root, new int[]{root, root + 4, root + 7, root + 11}, numberToNote(root) + " Major 7th");
//            root = root + 2;
//            II = new Chord (root, new int[]{root, root + 3, root + 7, root + 10}, numberToNote(root) + " Minor 7th");
//            root = root + 2;
//            III = new Chord (root, new int[]{root, root + 3, root + 7,  root + 10}, numberToNote(root) + " Minor 7th");
//            root = root + 1;
//            IV = new Chord (root, new int[]{root, root + 4, root + 7,  root + 11}, numberToNote(root) + " Major 7th");
//            root = root + 2;
//            V = new Chord (root, new int[]{root, root + 4, root + 7, root + 10}, numberToNote(root) + " 7th");
//            root = root + 2;
//            VI = new Chord (root, new int[]{root, root + 3, root + 7, root + 10}, numberToNote(root) + " Minor 7th");
//            root = root + 2;
//            VII = new Chord (root, new int[]{root,  root + 3, root + 6, root + 10}, numberToNote(root) + " Diminished");
//            chords = new Chord[]{I, II, III, IV, V, VI, VII};
//
//        }
//        else if (mixolydian) {
//            I = new Chord (root, new int[]{root, root + 4, root + 7, root + 10}, numberToNote(root) + " 7th");
//            root = root + 2;
//            II = new Chord (root, new int[]{root, root + 3, root + 7, root + 10}, numberToNote(root) + " Minor 7th");
//            root = root + 2;
//            III = new Chord (root, new int[]{root, root + 3, root + 7,  root + 10}, numberToNote(root) + " Minor 7th");
//            root = root + 1;
//            IV = new Chord (root, new int[]{root, root + 4, root + 7,  root + 10}, numberToNote(root) + " 7th");
//            root = root + 2;
//            V = new Chord (root, new int[]{root, root + 4, root + 2, root + 9}, numberToNote(root) + " 9th");
//            root = root + 2;
//            VI = new Chord (root, new int[]{root, root + 3, root + 7, root + 10}, numberToNote(root) + " Minor 7th");
//            root = root + 2;
//            VII = new Chord (root, new int[]{root,  root + 3, root + 6, root + 10}, numberToNote(root) + " Diminished");
//            chords = new Chord[]{I, II, III, IV, V, VI, VII};
//        }
//        else {
//            I = new Chord (root, new int[]{root, root + 4, root + 7}, numberToNote(root) + " Major");
//            root = root + 2;
//            II = new Chord (root, new int[]{root, root + 3, root + 7}, numberToNote(root) + " Minor");
//            root = root + 2;
//            III = new Chord (root, new int[]{root, root + 3, root + 7}, numberToNote(root) + " Minor");
//            root = root + 1;
//            IV = new Chord (root, new int[]{root, root + 4, root + 7}, numberToNote(root) + " Major");
//            root = root + 2;
//            V = new Chord (root, new int[]{root, root + 4, root + 7}, numberToNote(root) + " Major");
//            root = root + 2;
//            VI = new Chord (root, new int[]{root, root + 3, root + 7}, numberToNote(root) + " Minor");
//            root = root + 2;
//            VII = new Chord (root, new int[]{root, root + 3, root + 6}, numberToNote(root) + " Diminished");
//            chords = new Chord[]{I, II, III, IV, V, VI};//, VII};
//        }
//    }
//
//    void chooseMelodySpeed() {
//        melodySpeed = melodySpeeds[rand.nextInt(melodySpeeds.length)];
//    }
//
//    //Chooses the number of notes to play from the chorda at a timer
//    void chooseNumChordNotes() {
//        notesInChord = rand.nextInt(2) + 1;
//    }
//
//    public int[] getArpeggioOrder(int notesInChord) {
//        int[] arp = new int[notesInChord];
//        for (int i = 0; i < notesInChord; i++) {
//            arp[i] = i;
//        }
//        //Shuffle
//        for (int i = arp.length - 1; i > 0; i--) {
//            int index = rand.nextInt(i + 1);
//            // Simple swap
//            int a = arp[index];
//            arp[index] = arp[i];
//            arp[i] = a;
//        }
//        return arp;
//    }
//
//    void generateChords(int numberOfProgressions, int numberOfChords) {
//        beatsInProg = new int[numberOfProgressions];
//        randomChordProgression = new Chord[numberOfProgressions][numberOfChords];
//        int b = 0;
//        int r = 0;
//        for (int i = 0; i < numberOfProgressions; i++) {
//            //chooseNumChordNotes();
//            chooseTimesChordPlayed();
//            chooseProgHasArpeggio();
//            beatsInProg[i] = numberOfChords * beatsBetweenChords;
//            for (int j = 0; j < numberOfChords; j++) {
//                randomChordProgression[i][j] = chords[rand.nextInt(6)];
//                for (int c = 0; c < timesChordPlayed; c++) {
//                    b = (j * beatsBetweenChords) / timesChordPlayed;
//                    if (arpeggio) {
//                        int[] arp = getArpeggioOrder(notesInChord);
//                        for (int k = 0; k < notesInChord; k++) {
//                            song[i][k][b] = randomChordProgression[i][j].getNotes()[k];
//                            midi.play(randomChordProgression[i][j].getNotes()[arp[k]] + 48, 0, midi.getTrack(0));
//                            midi.stop(randomChordProgression[i][j].getNotes()[arp[k]] + 48, beatsBetweenChords / timesChordPlayed / notesInChord, midi.getTrack(0));
//                        }
//                    }
//                    else if (arpeggio2) {
//                        for (int k = 0; k < notesInChord; k++) {
//                            song[i][k][b] = randomChordProgression[i][j].getNotes()[k];
//                            midi.play(randomChordProgression[i][j].getNotes()[k] + 48, 0, midi.getTrack(0));
//                            midi.play(0, 0, midi.getTrack(0));
//                            midi.stop(0, beatsBetweenChords/timesChordPlayed/notesInChord, midi.getTrack(0));
//                        }
//                        //Stop chord
//                        for (int k = 0; k < notesInChord; k++) {
//                            midi.stop(randomChordProgression[i][j].getNotes()[k] + 48, 0, midi.getTrack(0));
//                        }
//                    }
//                    else {
//                        for (int k = 0; k < notesInChord; k++) {
//                            song[i][k][b] = randomChordProgression[i][j].getNotes()[k];
//                            midi.play(randomChordProgression[i][j].getNotes()[k] + 48, 0, midi.getTrack(0));
//                        }
//                        //Stop chord
//                        for (int k = 0; k < notesInChord; k++) {
//                            if (k == 0) {
//                                midi.stop(randomChordProgression[i][j].getNotes()[k] + 48, beatsBetweenChords / timesChordPlayed, midi.getTrack(0));
//                            } else {
//                                midi.stop(randomChordProgression[i][j].getNotes()[k] + 48, 0, midi.getTrack(0));
//                            }
//                        }
//                    }
//                }
//            }
//            b = 0;
//        }
//    }
//
//    void generateMelody() {
//        for (int i = 0; i < numberOfProgressions; i++) {
//            chooseMelodySpeed();
//            for (int j = 0; j < numberOfChords; j++) {
//                for (int b = j * beatsBetweenChords; b < (beatsInProg[i]/numberOfChords) * (j + 1) ; b += melodySpeed) {
//                    if (rand.nextInt (3) == 1 && b % beatsBetweenChords != 0) {
//                        int r = rand.nextInt(7);
//                        song[i][5][b] = scale[rand.nextInt(7)];
//                    }
//                    else {
//                        int r = rand.nextInt(4);
//                        if (r < 3 && b % beatsBetweenChords != 0) {
//                            song[i] [5] [b] = randomChordProgression[i] [j].getNotes()[r];
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    void generateMelody2() {
//        int b = 0;
//        int r = 0;
//        for (int i = 0; i < numberOfProgressions; i++) {
//            //chooseNumChordNotes();
//            chooseMelodySpeed();
//            beatsInProg[i] = numberOfChords * beatsBetweenChords;
//            for (int j = 0; j < numberOfChords; j++) {
//                for (b = j * beatsBetweenChords; b < (beatsInProg[i] / numberOfChords) * (j + 1); b += (int)beatsBetweenChords/melodySpeed) {
//                    boolean rest = false;
//                    Log.d("b = ", "" + b);
//                    Log.d("less than ", "" + beatsInProg[i] / numberOfChords * (j + 1));
//                    r = rand.nextInt(scale.length);
//                    if (rand.nextInt(3) == 1) {
//                        song[i][5][b] = scale[r];
//                        midi.play(song[i][5][b] + 72, 0, midi.getTrack(1));
//                    }
//                    else {
//                        r = rand.nextInt(50);
//                        if (r < 48) {
//                            r = rand.nextInt(20);
//                            if (r < 17) {
//                                r = rand.nextInt(notesInChord);
//                                song[i][5][b] = randomChordProgression[i][j].getNotes()[r];
//                                midi.play(song[i][5][b] + 72, 0, midi.getTrack(1));
//                            }
//                            else {
//                                r = rand.nextInt(scale.length);
//                                song[i][5][b] = scale[r];
//                                midi.play(song[i][5][b] + 72, 0, midi.getTrack(1));
//                            }
//                        }
//                        //Rest
//                        else {
//                            song[i][5][b] = 0;
//                            midi.play(song[i][5][b], 0, midi.getTrack(1));
//                            midi.stop(song[i][5][b], (int)beatsBetweenChords/melodySpeed, midi.getTrack(1));
//                            rest = true;
//                        }
//                    }
//                    if (!rest) {
//                        midi.stop(song[i][5][b] + 72, (int)beatsBetweenChords/melodySpeed, midi.getTrack(1));
////                        midi.play(0, 0, midi.getTrack(1));
////                        midi.stop(0, 1, midi.getTrack(1));
//                    }
//                }
//            }
//            b = 0;
//        }
//    }
//
//    public void chooseDrumSpeed() {
//        drumSpeed = drumSpeeds[rand.nextInt(drumSpeeds.length)];
//    }
//
//    public void chooseHasHats() {
//        if (rand.nextInt(4) != 0) {
//            progHasHats = true;
//        }
//    }
//
//    void chooseProgHasDrums() {
//        int r = rand.nextInt(5);
//        if (r == 1) {
//            progHasDrums = false;
//            Log.d("Prog has drums", "false");
//        }
//        else {
//            progHasDrums = true;
//        }
//    }
//
//    public void addDrums2() {
//        int b = 0;
//        int r = 0;
//        for (int i = 0; i < numberOfProgressions; i++) {
//            chooseProgHasDrums();
//            chooseDrumSpeed();
//            if (progHasDrums) {
//                createDrumPattern(chooseDrumPatternLength());
//                beatsInProg[i] = numberOfChords * beatsBetweenChords;
//                int p = 0;
//                for (b = 0; b < (beatsInProg[i]); b += drumSpeed) {
//                    song[i][9][b] = pattern[p];
//                    midi.play(song[i][9][b], 0, midi.getTrack(numberOfTracks - 1));
//                    //Chance of crash on start of progression
//                    if (b == 0 && rand.nextInt(2) == 0) {
//                        midi.play(57, 0, midi.getTrack(numberOfTracks - 1));
//                        midi.stop(57, drumSpeed, midi.getTrack(numberOfTracks - 1));
//                        midi.stop(song[i][9][b], 0, midi.getTrack(numberOfTracks - 1));
//                    }
//                    else {
//                        midi.stop(song[i][9][b], drumSpeed, midi.getTrack(numberOfTracks - 1));
//                    }
//                    p++;
//                    if (p == pattern.length) {
//                        p = 0;
//                    }
//                }
//            }
//            b = 0;
//        }
//    }
//
//    int[] pattern;
//    public void createDrumPattern(int patternLength) {
//        pattern = new int[patternLength];
//        for (int i = 0; i < patternLength; i++) {
//            pattern[i] = drumSounds[rand.nextInt(drumSounds.length)];
//        }
//    }
//
//    int[] drumSounds = new int[] {42, 36, 37, 38};
//
//    int[] drumPatternLengths = new int[] {1, 2, 4, 8};
//
//    public int chooseDrumPatternLength() {
//        return drumPatternLengths[rand.nextInt(drumPatternLengths.length)];
//    }
//
//    public void addDrums() {
//        for (int i = 0; i < numberOfProgressions; i++) {
//            chooseProgHasDrums();
//            if (progHasDrums) {
//                chooseDrumSpeed();
//                chooseHasHats();
//                for (int j = 0; j < numberOfChords; j++) {
//                    //For b = start of progression to melodyEnd of progression, incrementing by drumSpeed
//                    for (int b = j * beatsBetweenChords; b < (beatsInProg[i]/numberOfChords) * (j + 1) ; b ++) {
//                        //Kick on every chord
//                        if (b % beatsBetweenChords/drumSpeed == 0) {
//                            song[i][9][b] = 36;
//                            midi.play(song[i][9][b], 0, midi.getTrack(numberOfTracks - 1));
////                            midi.stop(song[i][9][b], beatsBetweenChords/2, midi.getTrack(2));
//                        }
////                      Clap on every half beat
//                        else if (b % (beatsBetweenChords/drumSpeed) == 0) {
//                            song[i][9][b] = 37;
//                            midi.play(song[i][9][b], 0, midi.getTrack(numberOfTracks - 1));
////                            midi.stop(song[i] [9] [b], beatsBetweenChords/2, midi.getTrack(2));
//                        }
//                        //Chance of hihat on others
//                        else if (progHasHats) {
//                            if (rand.nextInt(3) == 0) {
//                                song[i][9][b] = 42;
//                                midi.play(song[i][9][b], 0, midi.getTrack(numberOfTracks - 1));
//
//                            }
//                            else if (rand.nextInt(5) == 0) {
//                                song[i][9][b] = 42;
//                                midi.play(song[i][9][b], 0, midi.getTrack(numberOfTracks - 1));
//
//                            }
//                            else {
//                                song[i][9][b] = 0;
//                                midi.play(song[i][9][b], 0, midi.getTrack(numberOfTracks - 1));
//                            }
//                        }
//                        else {
//                            song[i][9][b] = 0;
//                            midi.play(song[i][9][b], 0, midi.getTrack(numberOfTracks - 1));
//                        }
//                        midi.stop(song[i][9][b], drumSpeed, midi.getTrack(numberOfTracks - 1));
//                    }
//                }
//            }
//        }
//    }

    public void printSong() {
        String s;
        for (int progression = 0; progression < numberOfProgressions; progression++) {
            for (int channel = 0; channel < 6; channel++) {
                for (int beat = 0; beat < beatsInProg[progression]; beat++) {
                    int toPrint = song[progression][channel][beat];
                    if (toPrint < 10) {
                        System.out.print(0);
                    }
                    System.out.print(toPrint);
                    System.out.print("-");
                    //s += (Integer)(song[progression][channel][beat]).toString();
                }
                System.out.println("\n");
            }
            System.out.println("\n\n");
        }
        System.out.println("\n\n");

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