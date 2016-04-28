package com.example.jonathanbriers.musicgenerator;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

/**
 * Created by Jonny on 16/04/2016.
 */
public class AI  {

    ArrayList<Song> songs = new ArrayList<Song>();
    SharedPreferences mPrefs;
    int numberOfSongs;
    boolean shouldHaveDrums;
    boolean shouldBeDorian;
    boolean shouldBeStructured;
    boolean progShouldHaveDrums;
    int bestKey;
    int bestTempo;
    int top = 10;
    int[] chordTimes = new int[]{1, 2, 4, 8};
    int[] bassSpeeds = new int[]{1, 2, 4, 8, 16};
    int bestMelodySpeed;
    ArrayList<Progression> melodiesProgressions;
    int numberOfChordInProgression = 4;
    Random rand;
    int scaleLength = 7;
    int beatsInBar = 32;
    int[][] bestMelody = new int[numberOfChordInProgression][beatsInBar];
    boolean progShouldHaveBass;
    ArrayList<Progression> progressions = new ArrayList<>();
    ArrayList<Progression> progressions2 = new ArrayList<>();
    int[][] bestRoots1 = new int[numberOfChordInProgression][scaleLength];
    int[][] bestRoots2 = new int[numberOfChordInProgression][scaleLength];
    Chord[] bestChordProgression1 = new Chord[numberOfChordInProgression];
    Chord[] bestChordProgression2 = new Chord[numberOfChordInProgression];
    ArrayList<Progression> melodiesProgressionsUnpatterned = new ArrayList<>();
    int[] bestScale;
    int bestMelodyStart = 0;
    int bestMelodyEnd = 0;
    int nextBestMelodySpeed;
    boolean shouldBeMixolydian;
    int bestChordInstrument = 0;
    int bestMelodyInstrument = 0;
    int bestBassInstrument = 0;
    int bestBassSpeed = 0;

    public AI(SharedPreferences mPrefs, Random rand) {
        this.mPrefs = mPrefs;
        this.rand = rand;
        getSongs();
    }

    public Song smartGenerate() {
        getSongs();
        bestKey();
        bestTempo();
        shouldBeDorian();
        if (!shouldBeDorian) {
            shouldBeMixolydian();
        }
        shouldHaveDrums();
        bestBassSpeed();
        bestMelodySpeed();
        bestChordInstrument();
        progShouldHaveBass();
        progShouldHaveDrums();
        bestBassInstrument();
        bestMelodyInstrument();
        getBestMelodyStart();
        getBestMelodyEnd();
        getScale();
        bestChordProgression(bestScale);
        bestTimesChordPlayed();
        createBestMelody(bestMelodySpeed, false, bestChordProgression1);
        Log.d("Succeesss??", "Suck Cess!!!!");
        Song s = new Song(bestKey, bestTempo, bestBassInstrument, bestChordInstrument,
                bestMelodyInstrument, bestScale, shouldHaveDrums, rand, shouldBeDorian, shouldBeMixolydian,
                shouldBeStructured);
        Progression p = new Progression(bestKey, progShouldHaveDrums, progShouldHaveBass,
                bestMelodySpeed, bestMelodyStart, bestMelodyEnd, bestChordProgression1, bestMelody,
                bestTimesChordPlayed, progShouldHaveBass, progShouldHaveDrums, s.getNumberOfChannels(),
                s.getNotesInChords(), bestBassSpeed, rand);
        Progression p2 = new Progression(bestKey, progShouldHaveDrums, progShouldHaveBass,
                bestMelodySpeed, bestMelodyStart, bestMelodyEnd, bestChordProgression2, bestMelody,
                bestTimesChordPlayed, progShouldHaveBass, progShouldHaveDrums, s.getNumberOfChannels(),
                s.getNotesInChords(), bestBassSpeed, rand);
        Progression p3 = new Progression(bestKey, progShouldHaveDrums, progShouldHaveBass,
                bestMelodySpeed, bestMelodyStart, bestMelodyEnd, randomChordProgression(), bestMelody,
                bestTimesChordPlayed, progShouldHaveBass, progShouldHaveDrums, s.getNumberOfChannels(),
                s.getNotesInChords(), bestBassSpeed, rand);
        if (!shouldBeStructured) {
            s.addProgression(p);
            s.addProgression(p2);
            s.addProgression(p3);
        }
        else {
            s.setVerse(p);
            s.setChorus(p2);
            s.setBridge(p3);
        }
        return s;
    }

    public void getSongs() {
        Song song;
        Gson gson = new Gson();
        numberOfSongs = mPrefs.getAll().size();
        for (int i = 0; i < numberOfSongs; i++) {
            String json = mPrefs.getString(((Integer) (i)).toString(), "");
            song = gson.fromJson(json, Song.class);
            songs.add(song);
        }
        if (numberOfSongs < top) {
            top = numberOfSongs;
            Log.d("Number of songs:", "" + top);
        }
        sortSongs();
    }

    public void sortSongs() {
        Collections.sort(songs, new Comparator<Song>() {
            @Override
            public int compare(Song song2, Song song1) {
                return song1.getRating() - song2.getRating();
            }
        });
        for (int i = 0; i < numberOfSongs; i++) {
            Log.d("" + songs.get(i).getRating(), "");
        }
    }

    public void bestChordProgression(int[] bestScale) {
        for (int i = 0; i < top; i += 2) {
            Song song = songs.get(i);
            for (int j = 0; j < song.getNumberOfProgressions(); j++) {
                Progression p = song.getProgressions().get(j);
                progressions.add(p);
            }
            for (int chord = 0; chord < bestRoots1.length; chord++) {
                for (int j = 0; j < progressions.size(); j++) {
                    boolean found = false;
                    for (int k = 0; k < progressions.get(j).getScale().length; k++) {
                        if (!found) {
                            if (progressions.get(j).getChordProgression()[chord].getNotes()[0] == progressions.get(j).getScale()[k]) {
                                bestRoots1[chord][k] += 1;
                                found = true;
                            }
                        }
                    }
                }
            }
        }
        for (int i = 1; i < top; i += 2) {
            Song song = songs.get(i);
            for (int j = 0; j < song.getNumberOfProgressions(); j++) {
                Progression p = song.getProgressions().get(j);
                progressions2.add(p);
            }
            for (int chord = 0; chord < bestRoots2.length; chord++) {
                for (int j = 0; j < progressions2.size(); j++) {
                    boolean found = false;
                    for (int k = 0; k < progressions2.get(j).getScale().length; k++) {
                        if (!found) {
                            if (progressions2.get(j).getChordProgression()[chord].getNotes()[0] == progressions2.get(j).getScale()[k]) {
                                bestRoots2[chord][k] += 1;
                                found = true;
                            }
                        }
                    }
                }
            }
        }
//            bestRoots1[chord] = bestRoots1[chord] / progressions.size();
//            bestChordProgression1[chord] = rootToChord(bestRoots1[chord]);
        for (int chord = 0; chord < numberOfChordInProgression; chord++) {
            int biggest = 0;
            int theRoot = 0;
            for (int k = 0; k < scaleLength; k++) {
                if (bestRoots1[chord][k] > biggest) {
                    biggest = bestRoots1[chord][k];
                    theRoot = bestScale[k];
                }
            }
            bestChordProgression1[chord] = rootToChord(theRoot);
        }
        for (int chord = 0; chord < bestRoots2.length; chord++) {
            int biggest = 0;
            int theRoot = 0;
            for (int k = 0; k < scaleLength; k++) {
                if (bestRoots2[chord][k] > biggest) {
                    biggest = bestRoots2[chord][k];
                    theRoot = bestScale[k];
                }
            }
            bestChordProgression2[chord] = rootToChord(theRoot);
        }
    }

    public Chord[] randomChordProgression() {
        Chord[] chordProgression = new Chord[numberOfChordInProgression];
        for (int i = 0; i < numberOfChordInProgression; i++) {
            chordProgression[i] = rootToChord(bestScale[rand.nextInt(bestScale.length - 1)]);
        }
        return chordProgression;
    }

    public void createBestMelody(int bestMelodySpeed, boolean shouldBePatterned, Chord[] chordProg) {
        //Get melodies
        for (int i = 0; i < top; i++) {
            Song song = songs.get(i);
            for (int j = 0; j < song.getNumberOfProgressions(); j++) {
                Progression p = song.getProgressions().get(j);
                if (shouldBePatterned) {
                    if (p.getIsPatternMelody()) {
                        if (p.getMelodySpeed() == bestMelodySpeed) {
                            melodiesProgressions.add(p);
                        }
                        //If the melody speed isn't best, make it best!
                        else {
                            p.removeMelody();
                            p.setMelodySpeed(bestMelodySpeed);
                            p.generateMelody();
                            melodiesProgressions.add(p);
                        }
                    }
                } else {
                    if (!p.getIsPatternMelody()) {
                        if (p.getMelodySpeed() == bestMelodySpeed) {
                            melodiesProgressionsUnpatterned.add(p);
                        }
                    }
                }
            }
        }

        ArrayList<int[][][]> allMelodyDetailsPatterned = new ArrayList<>();

        if (!shouldBePatterned) {
            //Create best unpatterned melody
            ArrayList<int[][][]> allMelodyDetailsUnpatterned = new ArrayList<>();
            for (int i = 0; i < melodiesProgressionsUnpatterned.size(); i++) {
                //Get chords, respective to key
                Progression p = melodiesProgressionsUnpatterned.get(i);
                Chord[] chords = new Chord[4];
                for (int j = 0; j < chords.length; j++) {
                    chords[j] = p.getChordProgression()[j];
                }
                //0 = rest, 1 = arpeggio, 2 = rest of scale
                int[][] melodyNoteTypes = new int[p.getNumberOfBars()][p.getBeatsInBar()];
                int[][] melodyNoteIntervalsChord = new int[p.getNumberOfBars()][p.getBeatsInBar()];
                int[][] melodyNoteIntervalsScale = new int[p.getNumberOfBars()][p.getBeatsInBar()];
                for (int bar = 0; bar < p.getNumberOfBars(); bar++) {
                    for (int beat = 0; beat < p.getBeatsInBar(); beat++) {
                        int note = melodiesProgressionsUnpatterned.get(i).getMelody()[bar][beat];
                        if (note == 0) { //Rest
                            melodyNoteTypes[bar][beat] = 0;
                            melodyNoteIntervalsChord[bar][beat] = -1;
                            melodyNoteIntervalsScale[bar][beat] = -1;
                        } else {
                            boolean done = false;
                            for (int j = 0; j < p.getNotesInChords(); j++) {
                                if (note == chords[bar].getNotes()[j]) {
                                    melodyNoteTypes[bar][beat] = 1;
                                    melodyNoteIntervalsChord[bar][beat] = j;
                                    melodyNoteIntervalsScale[bar][beat] = -1;
                                    done = true;
                                }
                            }
                            if (!done) {
                                melodyNoteTypes[bar][beat] = 2;
                                for (int k = 0; k < p.getScale().length; k++) {
                                    if (note == p.getScale()[k]) {
                                        melodyNoteIntervalsScale[bar][beat] = k;
                                        melodyNoteIntervalsChord[bar][beat] = -1;
                                    } else {
                                        Log.d("Wtf is this note?!", "");
                                    }
                                }
                            }
                        }
                    }
                }
                int[][][] melodyNoteDetails = new int[][][]{melodyNoteTypes, melodyNoteIntervalsChord, melodyNoteIntervalsScale};
                allMelodyDetailsUnpatterned.add(melodyNoteDetails);
            }

            for (int bar = 0; bar < 4; bar++) {
                int bestType = 0;
                for (int beat = 0; beat < 32; beat++) { //beat += melodySpeed???
                    for (int[][][] melodyNoteDetails : allMelodyDetailsUnpatterned) {
                        bestType += melodyNoteDetails[0][bar][beat];
                    }
                    bestType = bestType / melodiesProgressionsUnpatterned.size();
                    if (bestType == 0) { //Rest
                        bestMelody[bar][beat] = 0;
                    }
                    else if (bestType == 1) { //Chord note
                        //Int array with length = numberOfNotesInChord
                        int[] bestChordIntervals = new int[chordProg[bar].getNotes().length];
                        for (int[][][] melodyNoteDetails : allMelodyDetailsUnpatterned) {
                            for (int i = 0; i < bestChordIntervals.length; i++) {
                                if (melodyNoteDetails[1][bar][beat] == bestChordIntervals[i]) {
                                    bestChordIntervals[i]++;
                                }
                            }
                        }
                        int biggest = 0;
                        for (int i = 0; i < bestChordIntervals.length; i++) {
                            if (bestChordIntervals[i] > biggest) {
                                biggest = i;
                            }
                        }
                        bestMelody[bar][beat] = chordProg[bar].getNotes()[biggest];
                    }
                    else { //bestType == 2 scale
                        int[] bestScaleNotes = new int[bestScale.length];
                        for (int[][][] melodyNoteDetails : allMelodyDetailsUnpatterned) {
                            for (int i = 0; i < bestScaleNotes.length; i++) {
                                if (melodyNoteDetails[1][bar][beat] == bestScaleNotes[i]) {
                                    bestScaleNotes[i]++;
                                }
                            }
                        }
                        int biggest = 0;
                        for (int i = 0; i < bestScaleNotes.length; i++) {
                            if (bestScaleNotes[i] > biggest) {
                                biggest = i;
                            }
                        }
                        bestMelody[bar][beat] = bestScale[biggest];
                    }
                }
            }
        }
    }

    public void getScale() {
        int root = bestKey;
        if (shouldBeDorian) {
            bestScale = new int[]
                    {root, root + 2, root + 5, root + 7, root + 9, root + 11};
        } else if (shouldBeMixolydian) {
            bestScale = new int[]{
                    root, root + 2, root + 4, root + 5, root + 7, root + 9, root + 10};
        } else {
            bestScale = new int[]
                    {root, root + 2, root + 4, root + 5, root + 7, root + 9, root + 11};
        }
    }

    public Chord[] chordCombo() {
        Chord[] mixedProgression = new Chord[4];
        for (int i = 0; i < numberOfChordInProgression; i++) {
            if (rand.nextInt(2) == 0) {
                mixedProgression[i] = bestChordProgression1[i];
            } else {
                mixedProgression[i] = bestChordProgression2[i];
            }
        }
        return mixedProgression;
    }

    public void getBestMelodyStart() {
        int count = 0;
        for (int i = 0; i < top; i++) {
            for (int j = 0; j < songs.get(i).getNumberOfProgressions(); j++) {
                bestMelodyStart += songs.get(i).getProgressions().get(j).getMelodyStart();
                count++;
            }
        }
        bestMelodyStart = bestMelodyStart / count;
    }

    public void getBestMelodyEnd() {
        int count = 0;
        for (int i = 0; i < top; i++) {
            for (int j = 0; j < songs.get(i).getNumberOfProgressions(); j++) {
                bestMelodyEnd += songs.get(i).getProgressions().get(j).getMelodyEnd();
                count++;
            }
        }
        bestMelodyEnd = bestMelodyEnd / count;
    }

    public void bestMelodySpeed() {
        int speed2 = 0, speed4 = 0, speed8 = 0;
        for (int i = 0; i < top; i++) {
            for (int j = 0; j < songs.get(i).getNumberOfProgressions(); j++) {
                int melodySpeed = songs.get(i).getProgressions().get(j).getMelodySpeed();
                if (melodySpeed == 2) {
                    speed2++;
                } else if (melodySpeed == 4) {
                    speed4++;
                } else if (melodySpeed == 8) {
                    speed8++;
                }
            }
        }
        bestMelodySpeed = Math.max(speed2, Math.max(speed4, speed8));
        if (bestMelodySpeed == speed2) {
            bestMelodySpeed = 2;
            nextBestMelodySpeed = Math.max(speed4, speed8);
            if (nextBestMelodySpeed == speed4) {
                nextBestMelodySpeed = 4;
            } else {
                nextBestMelodySpeed = 8;
            }
        } else if (bestMelodySpeed == speed4) {
            bestMelodySpeed = 4;
            nextBestMelodySpeed = Math.max(speed2, speed8);
            if (nextBestMelodySpeed == speed2) {
                nextBestMelodySpeed = 2;
            } else {
                nextBestMelodySpeed = 8;
            }
        } else {
            bestMelodySpeed = 8;
            nextBestMelodySpeed = Math.max(speed2, speed4);
            if (nextBestMelodySpeed == speed2) {
                nextBestMelodySpeed = 2;
            } else {
                nextBestMelodySpeed = 4;
            }
        }
    }

    Integer[] bestBassSpeeds = new Integer[bassSpeeds.length];
    public void bestBassSpeed() {
        for (int i = 0; i < bassSpeeds.length; i++) {
            bestBassSpeeds[i] = 0;
        }
        for (int i = 0; i < top; i++) {
            Song song = songs.get(i);
            for (int j = 0; j < song.getProgressions().size(); j++) {
                int o = song.getProgressions().get(j).getBassSpeed();
                switch (o) {
                    case 1:
                        bestBassSpeeds[0]++;
                        break;
                    case 2:
                        bestBassSpeeds[1]++;
                        break;
                    case 4:
                        bestBassSpeeds[2]++;
                        break;
                    case 8:
                        bestBassSpeeds[3]++;
                }
            }
        }
    int biggest = 0;
    for (int i = 0; i < bestBassSpeeds.length; i++) {
        if (bestBassSpeeds[i] > bestBassSpeeds[biggest]) {
            biggest = i;
        }
    }
    bestBassSpeed = bassSpeeds[biggest];

    }

    public void shouldHaveDrums() {
        int d = 0;
        for (int i = 0; i < top; i++) {
            if (songs.get(i).getHasDrums()) {
                d++;
            }
        }
        shouldHaveDrums = d / top > 0.5;
    }

    public void progShouldHaveDrums() {
        int d = 0;
        int numberOfProgs = 0;
        for (int i = 0; i < top; i++) {
            if (songs.get(i).getHasDrums()) {
                Song song = songs.get(i);
                for (int j = 0; j < song.getProgressions().size(); j++) {
                    if (song.getProgressions().get(j).getHasDrums()) {
                        d++;
                    }
                    numberOfProgs++;
                }
            }
        }
        progShouldHaveDrums = d / numberOfProgs > 0.5;
    }

    public void progShouldHaveBass() {
        int d = 0;
        int numberOfProgs = 0;
        for (int i = 0; i < top; i++) {
            if (songs.get(i).getHasDrums()) {
                Song song = songs.get(i);
                for (int j = 0; j < song.getProgressions().size(); j++) {
                    if (song.getProgressions().get(j).getHasBass()) {
                        d++;
                    }
                    numberOfProgs++;
                }
            }
        }
        progShouldHaveBass = d / numberOfProgs > 0.5;
    }

    public void bestKey() {
        int k = 0;
        for (int i = 0; i < top; i++) {
            k += songs.get(i).getKey();
        }
        bestKey = k / top;
    }

    public void bestTempo() {
        int t = 0;
        for (int i = 0; i < top; i++) {
            t += songs.get(i).getTempo();
        }
        bestTempo = t / top;
    }

    public void shouldBeDorian() {
        int d = 0;
        for (int i = 0; i < top; i++) {
            if (songs.get(i).getDorian()) {
                d++;
            }
        }
        if (d / top > 0.5) {
            shouldBeDorian = true;
        }
    }

    public void shouldBeMixolydian() {
        int m = 0;
        for (int i = 0; i < top; i++) {
            if (songs.get(i).getDorian()) {
                m++;
            }
        }
        if (m / top > 0.5) {
            shouldBeMixolydian = true;
        }
    }

    public void bestChordInstrument() {
        int cI;
        int[] instruments = new int[0];
        for (int i = 0; i < top; i++) {
            Song song = songs.get(i);
            instruments = song.getChordInstruments();
            cI = song.getChordInstrument();
            for (int j = 0; j < instruments.length; j++) {
                if (cI == song.getChordInstruments()[j]) {
                    bestChordInstrument += j;
                }
            }
        }
        bestChordInstrument /= top;
        bestChordInstrument = instruments[bestChordInstrument];
    }

    public void bestMelodyInstrument() {
        int mI;
        int[] instruments = new int[0];
        for (int i = 0; i < top; i++) {
            Song song = songs.get(i);
            instruments = song.getMelodyInstruments();
            mI = song.getMelodyInstrument();
            for (int j = 0; j < instruments.length; j++) {
                if (mI == instruments[j]) {
                    bestMelodyInstrument += j;
                }
            }
        }
        bestMelodyInstrument /= top;
        bestMelodyInstrument = instruments[bestMelodyInstrument];
    }

    public void bestBassInstrument() {
        for (int i = 0; i < top; i++) {
            Song song = songs.get(i);
            bestBassInstrument += song.getBassInstrument();
        }
        bestBassInstrument /= top;
    }

    int bestTimesChordPlayed;
    Integer[] bestChordTimes = new Integer[chordTimes.length];
    public void bestTimesChordPlayed() {
        for (int i = 0; i < bestChordTimes.length; i++) {
            bestChordTimes[i] = 0;
        }
        for (int i = 0; i < top; i++) {
            Song song = songs.get(i);
            for (int j = 0; j < song.getProgressions().size(); j++) {
                int o = song.getProgressions().get(j).getTimesChordPlayed();
                switch (o) {
                    case 1:
                        bestChordTimes[0]++;
                        break;
                    case 2:
                        bestChordTimes[1]++;
                        break;
                    case 4:
                        bestChordTimes[2]++;
                        break;
                    case 8:
                        bestChordTimes[3]++;
                }
            }
        }
        int biggest = 0;
        for (int i = 0; i < bestChordTimes.length; i++) {
            if (bestChordTimes[i] > bestChordTimes[biggest]) {
                biggest = i;
            }
        }
        bestTimesChordPlayed = chordTimes[biggest];
    }

    public void shouldBeStructured() {
        int s = 0;
        for (int i = 0; i < top; i++) {
            if (songs.get(i).getStructured()) {
                s++;
            }
        }
        if (s/top > 0.5f) {
            shouldBeStructured = true;
        }
    }

    public Chord rootToChord(int chordRoot) {
        Chord I, II, III, IV, V, VI, VII;
        Chord[] chords;
        int root = bestKey;
        if (shouldBeDorian) {
            I = new Chord(root, new int[]{root, root + 4, root + 7, root + 11});
            root = root + 2;
            II = new Chord(root, new int[]{root, root + 3, root + 7, root + 10});
            root = root + 2;
            III = new Chord(root, new int[]{root, root + 3, root + 7, root + 10});
            root = root + 1;
            IV = new Chord(root, new int[]{root, root + 4, root + 7, root + 11});
            root = root + 2;
            V = new Chord(root, new int[]{root, root + 4, root + 7, root + 10});
            root = root + 2;
            VI = new Chord(root, new int[]{root, root + 3, root + 7, root + 10});
            chords = new Chord[]{I, II, III, IV, V, VI};
        } else if (shouldBeMixolydian) {
            I = new Chord(root, new int[]{root, root + 4, root + 7, root + 10});
            root = root + 2;
            II = new Chord(root, new int[]{root, root + 3, root + 7, root + 10});
            root = root + 2;
            III = new Chord(root, new int[]{root, root + 3, root + 7, root + 10});
            root = root + 1;
            IV = new Chord(root, new int[]{root, root + 4, root + 7, root + 10});
            root = root + 2;
            V = new Chord(root, new int[]{root, root + 4, root + 2, root + 9});
            root = root + 2;
            VI = new Chord(root, new int[]{root, root + 3, root + 7, root + 10});
            chords = new Chord[]{I, II, III, IV, V, VI};
        } else {
            I = new Chord(root, new int[]{root, root + 4, root + 7});
            root = root + 2;
            II = new Chord(root, new int[]{root, root + 3, root + 7});
            root = root + 2;
            III = new Chord(root, new int[]{root, root + 3, root + 7});
            root = root + 1;
            IV = new Chord(root, new int[]{root, root + 4, root + 7});
            root = root + 2;
            V = new Chord(root, new int[]{root, root + 4, root + 7});
            root = root + 2;
            VI = new Chord(root, new int[]{root, root + 3, root + 7});
            chords = new Chord[]{I, II, III, IV, V, VI};
        }
        Chord toReturn = new Chord(root, new int[]{root, root, root});
        for (Chord chord : chords) {
            if (chord.getRoot() == chordRoot) {
                toReturn = chord;
            }
        }
        return toReturn;
    }

    public void setmPrefs(SharedPreferences mPrefs) {
        this.mPrefs = mPrefs;
    }


}