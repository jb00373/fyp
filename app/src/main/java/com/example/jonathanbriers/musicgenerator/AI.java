package com.example.jonathanbriers.musicgenerator;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Jonny on 16/04/2016.
 */
public class AI {

    ArrayList<Song> songs = new ArrayList<Song>();
    SharedPreferences mPrefs;
    int numberOfSongs;
    boolean shouldHaveDrums;
    boolean shouldBeDorian;
    int bestKey;
    int bestTempo;
    int top = 10;
    int bestMelodySpeed;
    ArrayList<Progression> melodiesProgressions;


    public AI(SharedPreferences mPrefs) {
        this.mPrefs = mPrefs;
        getSongs();
    }

    public void getSongs() {
        Song song;
        Gson gson = new Gson();
        numberOfSongs = mPrefs.getAll().size();
        for (int i = 0; i < numberOfSongs; i++) {
            String json = mPrefs.getString(((Integer)(i)).toString(), "");
            song = gson.fromJson(json, Song.class);
            songs.add(song);
        }
        if (numberOfSongs < top) {
            top = numberOfSongs;
        }
        sortSongs();
    }

    public void sortSongs() {
        Collections.sort(songs, new Comparator<Song>() {
            @Override
            public int compare(Song song2, Song song1) {
                return song1.rating - song2.rating;
            }
        });
        for (int i = 0; i < numberOfSongs; i++) {
            Log.d("" + songs.get(i).getRating(), "");
        }
    }

    ArrayList<Progression> melodiesProgressionsUnpatterned = new ArrayList<>();
    public void createBestMelody(int bestMelodySpeed, boolean shouldBePatterned) {
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
                }
                else {
                    if (!p.getIsPatternMelody()) {
                        if (p.getMelodySpeed() == bestMelodySpeed) {
                            melodiesProgressionsUnpatterned.add(p);
                        }
                    }
                }

            }
        }
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
                    }
                    else {
                        for (int j = 0; j < p.getNotesInChords(); j++) {
                            if (note == chords[bar].getNotes()[j])  {
                                melodyNoteTypes[bar][beat] = 1;
                                melodyNoteIntervalsChord[bar][beat] = j;
                                melodyNoteIntervalsScale[bar][beat] = -1;
                            }
                            else {
                                melodyNoteTypes[bar][beat] = 2;
                                melodyNoteIntervalsScale[bar][beat] = j;
                                melodyNoteIntervalsChord[bar][beat] = -1;
                            }
                        }
                    }
                }
            }
            int[][][] melodyNoteDetails = new int[][][]{melodyNoteTypes, melodyNoteIntervalsChord, melodyNoteIntervalsScale};
            allMelodyDetailsUnpatterned.add(melodyNoteDetails);
        }
    }




    int bestMelodyStart = 0;
    public void getBestMelodyStart() {
        int count = 0;
        for (int i = 0; i < top; i++) {
            for (int j = 0; j < songs.get(i).getNumberOfProgressions(); j++) {
                bestMelodyStart += songs.get(i).getProgressions().get(j).getMelodyStart();
                count++;
            }
        }
        bestMelodyStart = bestMelodyStart/count;
    }

    int bestMelodyEnd = 0;
    public void getBestMelodyEnd() {
        int count = 0;
        for (int i = 0; i < top; i++) {
            for (int j = 0; j < songs.get(i).getNumberOfProgressions(); j++) {
                bestMelodyEnd += songs.get(i).getProgressions().get(j).getMelodyEnd();
                count++;
            }
        }
        bestMelodyEnd = bestMelodyEnd/count;
    }

    int nextBestMelodySpeed;
    public void bestMelodySpeed() {
        int speed2 = 0, speed4 = 0, speed8 = 0;
        for (int i = 0; i < top; i++) {
            for (int j = 0; j < songs.get(i).getNumberOfProgressions(); j++) {
                int melodySpeed = songs.get(i).getProgressions().get(j).getMelodySpeed();
                if (melodySpeed == 2) {
                    speed2++;
                }
                else if (melodySpeed == 4) {
                    speed4++;
                }
                else if (melodySpeed == 8) {
                    speed8++;
                }
            }
        }
        bestMelodySpeed = Math.max(speed2, Math.max(speed4, speed8));
        if (bestMelodySpeed == speed2) {
            nextBestMelodySpeed = Math.max(speed4, speed8);
        }
        else if (bestMelodySpeed == speed4) {
            nextBestMelodySpeed = Math.max(speed2, speed8);
        }
        else {
            nextBestMelodySpeed = Math.max(speed2, speed4);
        }
    }

    public void shouldHaveDrums() {
        int d = 0;
        for (int i = 0; i < top; i++) {
            if (songs.get(i).getHasDrums()) {
                d++;
            }
        }
        if (d/top > 0.5) {
            shouldHaveDrums = true;
        }
        else {
            shouldHaveDrums = false;
        }
    }

    public void bestKey() {
        int k = 0;
        for (int i = 0; i < top; i++) {
            k += songs.get(i).getKey();
        }
        bestKey = k/top;
    }

    public void bestTempo() {
        int t = 0;
        for (int i = 0; i < top; i++) {
            t += songs.get(i).getTempo();
        }
        bestTempo = (int)(t/top);
    }

    public void shouldBeDorian() {
        int d = 0;
        for (int i = 0; i < top; i++) {
            if (songs.get(i).getDorian()) {
                d++;
            }
        }
        if (d/top > 0.5) {
            shouldBeDorian = true;
        }
    }

    boolean shouldBeMixolydian;
    public void shouldBeMixolydian() {
        int m = 0;
        for (int i = 0; i < top; i++) {
            if (songs.get(i).getDorian()) {
                m++;
            }
        }
        if (m/top > 0.5) {
            shouldBeMixolydian = true;
        }
    }
}
