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
    private Chord I, II, III, IV, V, VI, VII;
    private int root;
    private Chord[] chords;
    private int[] scale;
    private int notesInChords;
    private int numberOfChannels;
    private int numberOfBars;
    private Track[] tracks;
    private int maxProgressions = 10;
    private int minProgressions = 3;
    private Progression intro, verse, chorus, bridge, outro;
    private boolean structured;
    private boolean hasIntro;
    private boolean hasOutro;
    private int rating;
    private int start, end;
    private int maxTempo = 250;
    private int minTempo = 80;
    int[] chordInstruments = new int[]{0,1,2,3,4,5,6,7,8,9,11,13,16,17,18,19,20,21,22,23,24,25,26,27
            ,28,29,30,31,40,41,42,44,45,46, 48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66
            ,67,68,69,70,71,72,73,75,77,78,79,88,89,90,91,92,93,94,95};

    int[] melodyInstruments =new int[]{0,1,2,3,4,5,6,7,8,9,11,13,16,17,18,19,20,21,22,23,24,25,26,27
            ,28,29,30,31,45,46, 48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70
            ,71,72,73,75,77,78,79,80,81,82,83,84,85,104,105,106,107,108,109,110,111,112};

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
        chooseNumberOfBars();
        chooseStructured();
        generateProgressions();
        Log.d("Done", "done");
    }

    public Song(int bestKey, int bestTempo, int bestBassInstrument, int bestChordInstrument,
                int bestMelodyInstrument, int[] scale, boolean shouldHaveDrums, Random rand,
                boolean dorian, boolean mixolydian, boolean shouldBeStructured) {
        progressions = new ArrayList<>();
        createChords();
        this.rand = rand;
        this.key = bestKey;
        Log.d("Key:", ""+numberToNote(key));
        this.tempo = bestTempo;
        this.scale = scale;
        this.hasDrums = shouldHaveDrums;
        this.hasDrums = false;
        this.dorian = dorian;
        this.mixolydian = mixolydian;
        this.structured = shouldBeStructured;
        updateNotesInChords();
        chooseNumberOfChannels();
        tracks[2].setInstrument(bestBassInstrument);
        tracks[1].setInstrument(bestMelodyInstrument);
        tracks[0].setInstrument(bestChordInstrument);
    }

//    bestKey, bestTempo, bestBassInstrument, bestChordInstrument, bestMelodyInstrument, bestScale,
//    shouldBeMixolydian, shouldBeDorian, shouldHaveDrums

    public void chooseNumberOfProgressions() {
        numberOfProgressions = rand.nextInt(maxProgressions - minProgressions) + minProgressions;
    }

    public int getNumberOfProgressions() {
        return  numberOfProgressions;
    }

    public void chooseHasDrums() {
        hasDrums = numberOfChannels > 2 && rand.nextInt(3) > 0;
    }


    public void chooseInstruments() {
        int j = numberOfChannels;
        if (hasDrums) {
            j--;
        }
        for (int i = 0; i < j; i++) {
            //Chords
            if (i == 0) {
                int r = rand.nextInt(chordInstruments.length);
                tracks[i].setInstrument(chordInstruments[r]);
                Log.d("Chord instrument: ",""+chordInstruments[r]);
            }
            //Melody
            else if (i == 1) {
                int r = rand.nextInt(melodyInstruments.length);
                tracks[i].setInstrument(melodyInstruments[r]);
                Log.d("Melody instrument: ",""+melodyInstruments[r]);

            }
            //Bass
            if (i == 2) {
                int r = rand.nextInt(8);
                tracks[i].setInstrument(32 + r);
            }
            else {
                int r = rand.nextInt(melodyInstruments.length);
                tracks[i].setInstrument(melodyInstruments[r]);
                Log.d("Generator instrument:", "" + melodyInstruments[r]);
            }
        }
    }

    void generateProgressions() {
        if (!structured) {
            for (int i = 0; i < numberOfProgressions; i++) {
                Progression p = new Progression(numberOfBars, numberOfChannels, rand, chords, notesInChords, scale, key, 0, false);
                progressions.add(i, p);
            }
        }
        else {
            chooseStructure(false);
        }
    }

    void addProgression(Progression p) {
        progressions.add(p);
    }

    public void chooseStructured() {
        structured = rand.nextInt(3) != 0;
    }

    void chooseStructure(boolean ai) {
        if (!ai) {
            verse = new Progression(numberOfBars, numberOfChannels, rand, chords, notesInChords, scale, key, 0, true);
            chorus = new Progression(numberOfBars, numberOfChannels, rand, chords, notesInChords, scale, key, 1, true);
            bridge = new Progression(numberOfBars, numberOfChannels, rand, chords, notesInChords, scale, key, 2, true);
        }
        chooseHasIntro();
        chooseHasOutro();
        Progression originalVerse;
        Progression originalChorus;
        int start = 0;
        int end = numberOfProgressions - 1;
        if (hasIntro) {
            int r = rand.nextInt(2);
            if (r == 0) {
                originalVerse = verse;
                intro = verse.stripDown();
                verse = originalVerse;
            }
            else if (r == 1) {
                originalChorus = chorus;
                intro = chorus.stripDown();
                chorus = originalChorus;
            }
            progressions.add(0, intro);
        }

        if (rand.nextInt(2) == 0) {
            for (int i = start; i + 1 < end; i = i + 2) {
                if (i > 0) {
                    if (rand.nextInt(4) > 0) {
                        if (rand.nextInt(2) == 0) {
                            progressions.add(chorus.buildUp());
                        }
                        else {
                            progressions.add(chorus.mutateProgression());
                        }
                    }
                    else {
                        progressions.add(chorus);
                    }
                    if (rand.nextInt(4) > 0) {
                        if (rand.nextInt(2) == 0) {
                            progressions.add(verse.buildUp());
                        }
                        else {
                            progressions.add(verse.mutateProgression());
                        }
                    }
                    else {
                        progressions.add(bridge);
                    }
                }
                else {
                    progressions.add(chorus);
                    progressions.add(verse);
                }
            }
        }
        else {
            for (int i = start; i + 1 < end; i = i + 2) {
                if (i > 0) {
                    if (rand.nextInt(4) > 0) {
                        if (rand.nextInt(2) == 0) {
                            progressions.add(verse.buildUp());
                        }
                        else {
                            progressions.add(verse.mutateProgression());
                        }
                    }
                    else {
                        progressions.add(verse);
                    }
                    if (rand.nextInt(4) > 0) {
                        if (rand.nextInt(2) == 0) {
                            progressions.add(chorus.buildUp());
                        }
                        else {
                            progressions.add(chorus.mutateProgression());
                        }
                    }
                    else {
                        progressions.add(bridge);
                    }
                }
                else {
                    progressions.add(verse);
                    progressions.add(chorus);
                }
            }
        }
        if (hasOutro) {
            if (rand.nextInt(2) == 0) {
                outro = verse.stripDown();
            }
            else {
                outro = chorus.stripDown();
            }
            progressions.add(outro);
        }
        numberOfProgressions = progressions.size();
        Log.d("Number of progs", ""+numberOfProgressions);
        Log.d("Real number of progs", ""+progressions.size());
    }



    void chooseHasIntro() {
        if (rand.nextInt(2) == 0) {
            hasIntro = true;
            start = start + 1;
        }
        else {
            hasIntro = false;
        }
    }

    void chooseHasOutro() {
        if (rand.nextInt(2) == 0 && numberOfProgressions > 1) {
            hasOutro = true;
            end = end - 1;
        }
        else {
            hasOutro = false;
        }
    }

    void setRating(int rating) {
        this.rating = rating;
    }

    void chooseTempo() {
        tempo = rand.nextInt(maxTempo - minTempo) + minTempo;
        Log.d("Tempo", ""+tempo);
    }

    void chooseNumberOfBars() {
        int[] numbersOfBars = {4};
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

    public int getRating() { return rating;}

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

    void updateNotesInChords() {
        if (dorian || mixolydian) {
            notesInChords = 4;
        }
        else {
            notesInChords = 3;
        }
    }

    void createScale() {
        if (key == 0) {
            root = key + 12;
        }
        else {
            root = key;
        }
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
            if (root == 6) {
                for (int i = 0; i < scale.length; i++) {
                    Log.d("scale[" + i + "]:", "" + numberToNote(scale[i]));
                }
            }
        }
    }

    void createChords() {
        if (key == 0) {
            root = key + 12;
        }
        else {
            root = key;
        }
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

    public boolean getHasBass() {
        for (Progression progression : progressions) {
            if (progression.getHasBass()) {
                return true;
            }
        }
        return false;
    }

    public boolean getHasExtras() {
        for (Progression progression : progressions) {
            if (progression.getHasExtras()) {
                return true;
            }
        }
        return false;
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

    public int[] getChordInstruments() {
        return chordInstruments;
    }

    public int[] getMelodyInstruments() {
        return melodyInstruments;
    }

    public int getChordInstrument() {
        return tracks[0].getInstrument();
    }

    public int getMelodyInstrument() {
        return tracks[1].getInstrument();
    }

    public int getBassInstrument() {
        return tracks[2].getInstrument();
    }

    public int getNotesInChords() {return notesInChords;}

    public boolean getStructured() {return structured;}

    public void setVerse(Progression verse) {this.verse = verse;}

    public void setChorus(Progression chorus) {this.chorus = chorus;}

    public void setBridge(Progression bridge) {this.bridge = bridge;}
}
