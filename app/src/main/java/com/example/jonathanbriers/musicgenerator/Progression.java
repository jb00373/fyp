package com.example.jonathanbriers.musicgenerator;

import android.util.Log;

import java.util.Random;

/**
 * Created by Jonny on 01/04/2016.
 */
@SuppressWarnings("RedundantIfStatement")
public class Progression {
    private int numberOfBars;
    private int beatsInBar = 32;
    private int timesChordPlayed;
    private int numberOfChannels;
    private int melodySpeed;
    private int drumSpeed;
    private int hatSpeed;
    private int bassSpeed;
    private boolean hasDrums;
    private boolean hasHats;
    private boolean hasBass;
    private boolean hasExtras;
    private boolean hasChords;
    private boolean arpeggio;
    private boolean arpeggio2;
    private boolean longNoteMode;
    private int[] melodySpeeds = new int[] {2, 4, 8};
    private int[] drumSpeeds = new int[]{2, 4, 8, 16};
    private int[] fillSpeeds = new int[]{1, 2};
    private int[] hatSpeeds = new int[]{1, 2, 4, 8};
    private int[] chordTimes = new int[]{1, 2, 4, 8};
    private int[] bassSpeeds = new int[]{1, 2, 4, 8, 16};
    private int[] additionalPitches = new int[]{48, 60};
    private int beatsInProg;
    private Chord[] chordProgression;
    private Random rand;
    private int notesInChords;
    private Chord[] chords;
    private int[][][][] progression;
    private int[][] melody;
    private int additionalPitchChords;
    private int additionalPitchMelody;
    private boolean isPatternMelody;
    private boolean isPatternHats;
    private int[] scale;
    private int key;
    private boolean structured;
    private int part; //0 = verse, 1 = chorus, 2 = bridge


    public Progression(int numberOfBars, int numberOfChannels, Random rand, Chord[] chords,
                       int notesInChords, int[] scale, int key, int part, boolean structured) {
        this.numberOfBars = numberOfBars;
        this.numberOfChannels = numberOfChannels;
        this.rand = rand;
        this.notesInChords = notesInChords;
        this.chords = chords;
        this.scale = scale;
        this.key = key;
        this.structured = structured;
        this.timesChordPlayed = timesChordPlayed;
        if (structured) {
            this.part = part;
        }
        //We need both numberOfChannels and notesInChords here to distinguish between different channels and notes
        //played simultaneously in the same channel
        progression = new int[numberOfBars][beatsInBar][numberOfChannels][notesInChords];
        melody = new int[numberOfBars][beatsInBar];
        chooseAdditionalPitch();
        chooseHasChords();
        generateChords();
        if (hasChords) {
            addChords();
        }
        chooseMelodySpeed();
        chooseIsPatternMelody();
        generateMelody();
        chooseHasDrums();
        if (hasDrums) {
            addDrums();
        }
        chooseHasBass();
        if (hasBass) {
            addBass();
        }
    }

    public Progression(int bestKey, boolean shouldHaveDrums, boolean shouldHaveBass, int bestMelodySpeed, int bestMelodyStart,
                       int bestMelodyEnd, Chord[] bestChordProgression, int[][] bestMelody, int timesChordPlayed,
                       boolean progShouldHaveBass, boolean progShouldHaveDrums, int numberOfChannels, int notesInChords,
                       int bestBassSpeed, Random rand) {
        this.numberOfBars = 4;
        this.key = bestKey;
        this.hasDrums = shouldHaveDrums;
        this.melodySpeed = bestMelodySpeed;
        this.melodyStart = bestMelodyStart;
        this.melodyEnd = bestMelodyEnd;
        this.chordProgression = bestChordProgression;
        this.melody = bestMelody;
        this.timesChordPlayed = timesChordPlayed;
        this.hasBass = progShouldHaveBass;
        this.hasDrums = progShouldHaveDrums;
        this.hasDrums = false;
        this.numberOfChannels = numberOfChannels;
        this.notesInChords = notesInChords;
        this.bassSpeed = bestBassSpeed;
        this.rand = rand;
        hasChords = true;
        progression = new int[numberOfBars][beatsInBar][numberOfChannels][notesInChords];
        chooseAdditionalPitch();
        addChords();
        addMelody();
        addBass();
    }

    void addMelody() {
        for (int bar = 0; bar < numberOfBars; bar++) {
            for (int beat = melodyStart; beat < beatsInBar - melodyEnd; beat += melodySpeed) {
                //Rest
                if (melody[bar][beat] == 0) {
                    progression[bar][beat][1][0] = 0;
                }
                else {
                    progression[bar][beat][1][0] = melody[bar][beat] + additionalPitchMelody;
                }
            }
        }
    }

    void chooseHasChords() {
        if (rand.nextInt(8) > 0) {
            hasChords = true;
        }
    }

    void chooseTimesChordPlayed() {
        timesChordPlayed = chordTimes[rand.nextInt(chordTimes.length)];
    }

    void chooseDrumSpeed() {
        drumSpeed = drumSpeeds[rand.nextInt(drumSpeeds.length)];
    }

    void chooseHatSpeed() {
        hatSpeed = hatSpeeds[rand.nextInt(hatSpeeds.length)];
    }

    void chooseBassSpeed() {bassSpeed = bassSpeeds[rand.nextInt(bassSpeeds.length)];}

    void chooseHasDrums() {
        if (numberOfChannels > 2 && rand.nextInt(3) > 0) {
            hasDrums = true;
            if (rand.nextInt(3) > 0) {
                hasHats = true;
            }
            else {
                hasHats = false;
            }
        }
        else {
            hasDrums = false;
        }
    }

    void chooseLongNoteMode() {
        if (rand.nextInt(2) == 0) {
            longNoteMode = true;
        }
        else {
            longNoteMode = false;
        }
    }

    void chooseMelodySpeed() {
        melodySpeed = melodySpeeds[rand.nextInt(melodySpeeds.length)];
    }

    void chooseAdditionalPitch() {
        additionalPitchChords = additionalPitches[rand.nextInt(additionalPitches.length)];
        additionalPitchMelody = additionalPitches[rand.nextInt(additionalPitches.length)];
    }

    void chooseHasBass() {
        if (numberOfChannels > 3) {
            int r = rand.nextInt(3);
            if (r == 0) {
                hasBass = true;
            } else {
                hasBass = false;
            }
            hasBass = true;
        }
        else {
            hasBass = false;
        }
        Log.d("Has Bass = " , ""+hasBass);
    }

    void chooseProgHasArpeggio() {
        int r = rand.nextInt(6);
        if (r == 0 && notesInChords == 4) {
            arpeggio = true;
            Log.d("Arpeggio2: ", "true!");
        }
        else if (r == 1) {
            arpeggio2 = true;
            Log.d("Arpeggio: ", "true!");
        }
        else {
            arpeggio = false;
            arpeggio2 = false;
        }
    }

    public int[] getArpeggioOrder(int notesInChord) {
        int[] arp = new int[notesInChord];
        for (int i = 0; i < notesInChord; i++) {
            arp[i] = i;
        }
        //Shuffle
        for (int i = arp.length - 1; i > 0; i--) {
            int index = rand.nextInt(i + 1);
            // Simple swap
            int a = arp[index];
            arp[index] = arp[i];
            arp[i] = a;
        }
        return arp;
    }

    public void chooseStartEndMelody() {
        if (rand.nextInt(4) == 0) {
            melodyStart = rand.nextInt(beatsInBar);
        }
        else melodyStart = 0;
        if (rand.nextInt(4) == 0) {
            melodyEnd = rand.nextInt(beatsInBar);
        }
        else melodyEnd = 0;
        if (melodyStart > (beatsInBar - melodyEnd)) {
            melodyStart = 0;
        }
    }

    int bassStart = 0;
    int bassEnd = 0;
    public void chooseStartEndBass() {
        if (rand.nextInt(4) == 0) {
            bassStart = rand.nextInt(beatsInBar);
        }
        else melodyStart = 0;
        if (rand.nextInt(4) == 0) {
            bassEnd = rand.nextInt(beatsInBar);
        }
        else bassEnd = 0;
        if (bassStart > (beatsInBar - bassEnd)) {
            melodyStart = 0;
        }
    }

    int melodyStart = 0;
    int melodyEnd = 0;
    public void generateMelody() {
        int ra = 0;
//        chooseLongNoteMode();
        chooseStartEndMelody();
        if (isPatternMelody) {
            createMelodyPattern(chooseMelodyPatternLength());
            int p = 0;
            for (int bar = 0; bar < numberOfBars; bar++) {
                if (bar == numberOfBars - 1) {
                    ra = rand.nextInt(10);
                    if (ra == 0) {
                        chooseMelodySpeed();
                    }
                    else if (ra == 1) {
                        chooseStartEndMelody();
                    }
                }
                if (rand.nextInt(3) == 0){
                    mutatePattern(bar, 1, melodyPattern, false);
                }
                for (int beat = melodyStart; beat < beatsInBar - melodyEnd; beat += melodySpeed) {
                    progression[bar][beat][1][0] = melodyPattern[p] + additionalPitchMelody;
                    melody[bar][beat] = melodyPattern[p];
                    p++;
                    if (p == melodyPattern.length) {
                        p = 0;
                    }
                }
            }
        }
        else {
            for (int bar = 0; bar < numberOfBars; bar++) {
                if (bar == numberOfBars - 1) {
                    ra = rand.nextInt(10);
                    if (ra == 0) {
                        chooseMelodySpeed();
                    }
                    else if (ra == 1) {
                        chooseStartEndMelody();
                    }
                }
                for (int beat = melodyStart; beat < beatsInBar - melodyEnd; beat += melodySpeed) {
                    int r = rand.nextInt(scale.length);
                    if (rand.nextInt(3) == 1) {
                        progression[bar][beat][1][0] = scale[r] + additionalPitchMelody;
                        melody[bar][beat] = scale[r];
                    }
                    else {
                        r = rand.nextInt(50);
                        if (r < 48) {
                            r = rand.nextInt(20);
                            if (r < 17) {
                                r = rand.nextInt(notesInChords);
                                progression[bar][beat][1][0] = chordProgression[bar].getNotes()[r] + additionalPitchMelody;
                                melody[bar][beat] = chordProgression[bar].getNotes()[r];
                            }
                            else {
                                r = rand.nextInt(scale.length);
                                progression[bar][beat][1][0] = scale[r] + additionalPitchMelody;
                                melody[bar][beat] = scale[r];
                            }
                        }
                        //Rest
                        else {
                            progression[bar][beat][1][0] = -melodySpeed;
                            melody[bar][beat] = 0;
                        }
                    }
                }
            }
        }
    }

    public void mutatePattern(int bar, int channel, int[] pattern, boolean drums) {
        int numberOfNotesToMutate = rand.nextInt(pattern.length);
        for (int i = 0; i < numberOfNotesToMutate; i++) {
            if (!drums) {
                if (rand.nextInt(3) == 0) {
                    pattern[rand.nextInt(pattern.length)] = scale[rand.nextInt(scale.length)];
                } else {
                    pattern[rand.nextInt(pattern.length)] = chordProgression[bar].getNotes()[rand.nextInt(notesInChords)];
                }
            }
            else {
                drumPattern[rand.nextInt(pattern.length)] = drumSounds[rand.nextInt(drumSounds.length)];
            }
        }
    }

    int[] melodyPattern;
    public void createMelodyPattern(int patternLength) {
        melodyPattern = new int[patternLength];
        for (int i = 0; i < patternLength; i++) {
            melodyPattern[i] = scale[rand.nextInt(scale.length)];
        }
    }

    public int getMelodySpeed() {
        return melodySpeed;
    }
    public boolean getHasDrums() {return hasDrums;}
    public boolean getHasHats() {return hasHats;}

    public int getFillLength() {
        int[] fillLengths = new int[]{2, 4, 8};
        return fillLengths[rand.nextInt(fillLengths.length)];
    }

    public void addDrums() {
        int r = 0;
//        chooseHasDrums();
        if (hasDrums) {
            chooseDrumSpeed();
            createDrumPattern(chooseDrumPatternLength());
            int fillLength = getFillLength();
            int p = 0;
            for (int bar = 0; bar < numberOfBars; bar++) {
                if (bar == numberOfBars - 1) {
                    r = rand.nextInt(9);
                    if (r == 0) {
                        chooseDrumSpeed();
                    }
                }
                if (rand.nextInt(3) == 0) {
                    mutatePattern(bar, numberOfChannels - 1, drumPattern, true);
                }
                int beat = 0;
                boolean fill = false;
                for (beat = 0; beat < beatsInBar; beat += drumSpeed) {
                    progression[bar][beat][numberOfChannels - 1][0] = drumPattern[p];
                    p++;
                    //Crash
                    if (beat == 0 && bar == 0 && rand.nextInt(4) > 0) {
                        progression[bar][beat][numberOfChannels - 1][1] = 49;
                    }
                    //Fill
                    else if (bar == numberOfBars - 1 && beat >= beatsInBar/fillLength && rand.nextInt(3) == 0) {
                        fill = true;
                        break;
                    }
                    //Loop
                    if (p == drumPattern.length) {
                        p = 0;
                    }
                }
                if (fill) {
                    Log.d("fill = ", "true");
                    drumSpeed = fillSpeeds[rand.nextInt(fillSpeeds.length)];
                    for (beat = beat; beat < beatsInBar; beat += drumSpeed) {
                        progression[bar][beat][numberOfChannels - 1][0] = fillSounds[rand.nextInt(fillSounds.length)];
                    }
                }
            }
            if (hasHats) {
                chooseHatSpeed();
                chooseIsPatternHats();
                if (isPatternHats) {
//                    int beat = 0;
                    int p2 = 0;
                    createHatPattern(chooseDrumPatternLength());
                    for (int bar = 0; bar < numberOfBars; bar++) {
                        for (int beat = 0; beat < beatsInBar; beat += hatSpeed) {
                            progression[bar][beat][numberOfChannels - 1][1] = hatPattern[p2];
                            p++;
                            if (p == drumPattern.length) {
                                p = 0;
                            }
                        }
                    }
                }
                for (int bar = 0; bar < numberOfBars; bar++) {
                    for (int beat = 0; beat < beatsInBar; beat += hatSpeed) {
                        //Crash
                        if (bar == 0 && beat == 0 && rand.nextInt(4) > 0) {
                            progression[bar][beat][numberOfChannels - 1][1] = 49;
                        }
                        if (rand.nextInt(5) == 0) {
                            progression[bar][beat][numberOfChannels - 1][1] = 0;
                        }
                        //Closed hat
                        else {
                            progression[bar][beat][numberOfChannels - 1][1] = 42;
                        }
                    }
                }
            }
        }
    }

    int[] hatPattern;
    int[] hatSounds = new int[]{0, 42};
    public void createHatPattern(int patternLength) {
        hatPattern = new int[patternLength];
        for (int i = 0; i < patternLength; i++) {
            hatPattern[i] = hatSounds[rand.nextInt(hatSounds.length)];
        }
    }

    int additionalPitchBass = 24;
    public void addBass() {
//        chooseHasBass();
        if (hasBass) {
            chooseBassSpeed();
            chooseStartEndBass();
            for (int bar = 0; bar < numberOfBars; bar++) {
                for (int beat = bassStart; beat < beatsInBar - bassEnd; beat+= bassSpeed) {
                    if (rand.nextInt(5) != 0) {
                        progression[bar][beat][2][0] = chordProgression[bar].getNotes()[0] + additionalPitchBass;
                    }
                    else {
                        progression[bar][beat][2][0] = chordProgression[bar].getNotes()[rand.nextInt(notesInChords)] + additionalPitchBass;
                    }
                }
            }
        }
    }

    public void setMelodySpeed(int melodySpeed) {
        this.melodySpeed = melodySpeed;
    }

    public void removeMelody() {
        for (int bar = 0; bar < numberOfBars; bar++) {
            for (int beat = 0; beat < beatsInBar; beat++) {
                progression[bar][beat][1][0] = 0;
            }
        }
    }

    public boolean getHasBass() {
        return hasBass;
    }

    public int getBassSpeed() {
        return bassSpeed;
    }

    int[] drumPattern;
    int[] fillPattern;
    public void createDrumPattern(int patternLength) {
        drumPattern = new int[patternLength];
        for (int i = 0; i < patternLength; i++) {
            drumPattern[i] = drumSounds[rand.nextInt(drumSounds.length)];
        }
    }

    public void createFillPattern(int patternLength) {
        fillPattern = new int[patternLength];
        for (int i = 0; i < patternLength; i++) {
            fillPattern[i] = fillSounds[rand.nextInt(fillSounds.length)];
        }
    }

    int[] drumSounds = new int[] {36, 37, 38, 41, 43};
    int[] fillSounds = new int[] {37, 38, 39, 40, 41, 43, 45, 47, 48, 50};

    int[] drumPatternLengths = new int[] {1, 2, 4, 8};
    int[] melodyPatternLengths = new int[] {1, 2, 4, 8, 16};

    public int chooseDrumPatternLength() {
        return drumPatternLengths[rand.nextInt(drumPatternLengths.length)];
    }

    public int chooseMelodyPatternLength() {
        return melodyPatternLengths[rand.nextInt(melodyPatternLengths.length)];
    }

    public void chooseIsPatternMelody() {
        int r = rand.nextInt(2);
        if (r == 0) {
            isPatternMelody = true;
        }
        else {
            isPatternMelody = false;
        }
    }

    public void chooseIsPatternHats() {
        int r = rand.nextInt(2);
        if (r == 0) {
            isPatternHats = true;
        }
        else {
            isPatternHats = false;
        }
    }

    public void generateChords() {
        chordProgression = new Chord[numberOfBars];
        int r;
        chooseTimesChordPlayed();
        chooseProgHasArpeggio();
        for (int bar = 0; bar < numberOfBars; bar++) {
            if (bar == 2) {
                Log.d("", "");
            }
            if (bar == numberOfBars - 1) {
                r = rand.nextInt(10);
                if (r == 0) {
                    chooseTimesChordPlayed();
                } else if (r == 1) {
                    chooseProgHasArpeggio();
                }
            }
            chordProgression[bar] = chords[rand.nextInt(chords.length)];
        }
    }

    public void addChords() {
        for (int bar = 0; bar < numberOfBars; bar++) {
            if (arpeggio) {
                int[] arp = getArpeggioOrder(notesInChords);
                int n = 0;
                if (timesChordPlayed > notesInChords) {
                    timesChordPlayed = notesInChords;
                }
                for (int beat = 0; beat < beatsInBar; beat += beatsInBar / timesChordPlayed / notesInChords) {
                    progression[bar][beat][0][0] = chordProgression[bar].getNotes()[arp[n]] + additionalPitchChords;
                    if (n < notesInChords - 1) {
                        n++;
                    } else n = 0;
                }
            } else {
                for (int beat = 0; beat < beatsInBar; beat += beatsInBar / timesChordPlayed) {
                    for (int n = 0; n < notesInChords; n++) {
                        progression[bar][beat][0][n] = chordProgression[bar].getNotes()[n] + additionalPitchChords;
                    }
                }
            }
        }
    }

    public void addFifths() {
        for (int bar = 0; bar < numberOfBars; bar++) {
            for (int beat = 0; beat < beatsInBar; beat += beatsInBar / timesChordPlayed) {
                progression[bar][beat][3][0] = chordProgression[bar].getNotes()[2] + additionalPitchChords;
            }
        }
    }

    public void addThirds() {
        for (int bar = 0; bar < numberOfBars; bar++) {
            for (int beat = 0; beat < beatsInBar; beat += beatsInBar / timesChordPlayed) {
                progression[bar][beat][3][0] = chordProgression[bar].getNotes()[1] + additionalPitchChords;
            }
        }
    }

    public void addRoots() {
        for (int bar = 0; bar < numberOfBars; bar++) {
            for (int beat = 0; beat < beatsInBar; beat += beatsInBar / timesChordPlayed) {
                progression[bar][beat][3][0] = chordProgression[bar].getNotes()[0] + additionalPitchChords;
            }
        }
    }

    public void removeDrums() {
        for (int bar = 0; bar < numberOfBars; bar++) {
            for (int beat = 0; beat < beatsInBar; beat++) {
                progression[bar][beat][numberOfChannels - 1][0] = 0;
            }
        }
    }

    public void removeHats() {
        for (int bar = 0; bar < numberOfBars; bar++) {
            for (int beat = 0; beat < beatsInBar; beat++) {
                progression[bar][beat][numberOfChannels - 1][1] = 0;
            }
        }
    }

    public void removeBass() {
        for (int bar = 0; bar < numberOfBars; bar++) {
            for (int beat = 0; beat < beatsInBar; beat++) {
                progression[bar][beat][3][1] = 0;
            }
        }
    }

    public void removeChords() {
        for (int bar = 0; bar < numberOfBars; bar++) {
            for (int beat = 0; beat < beatsInBar; beat++) {
                for (int c = 0; c < notesInChords; c++) {
                    progression[bar][beat][0][c] = 0;
                }
            }
        }
    }

    public void stripDownChords() { //Baby
        for (int bar = 0; bar < numberOfBars; bar++) {
            for (int beat = 0; beat < beatsInBar; beat++) {
                for (int c = 1; c < notesInChords; c++) {
                    progression[bar][beat][0][c] = 0;
                }
            }
        }
    }

    public Progression stripDown() {
        int r = rand.nextInt(5);
        if (r == 0) {
            if (!hasDrums) {
                r++;
            }
            else {
                removeDrums();
                if (rand.nextInt(3) != 0 && hasHats) {
                    removeHats();
                }
            }
        }
        else if (r == 2) {
            if (!hasBass) {
                r++;
            }
            else {
                removeBass();
            }
        }
        else if (r == 3) {
            removeChords();
        }
        else if (r == 4) {
            stripDownChords();
        }
        return this;
    }

    public boolean getHasExtras() {
        return hasExtras;
    }

    public Progression mutateProgression() {
        int r = rand.nextInt(4);
        if (r == 0) {
            if (hasChords) {
                removeChords();
            }
            chooseTimesChordPlayed();
            addChords();
        }
        else if (r == 1) {
            if (hasBass) {
                removeBass();
            }
            chooseBassSpeed();
            addBass();
        }
        else if (r == 2) {
            removeDrums();
            if (hasHats) {
                hasHats = false;
                removeHats();
            }
            else {
                hasHats = true;
            }
            addDrums();
        }
        else if (r == 3) {
            if (hasDrums) {
                removeDrums();
            }
            chooseDrumSpeed();
            addDrums();
        }
        return this;
    }


    public Progression buildUp() {
        int r = rand.nextInt(6);
        if (r == 0) {
            if (!hasDrums) {
                hasDrums = true;
                if (rand.nextInt(2) == 0) {
                    hasHats = true;
                }
                addDrums();
            }
            else {
                r++;
            }
        }
        else if (r == 2) {
            if (!hasBass) {
                addBass();
            }
            else {
                r++;
            }
        }
        else if (r == 3) {
            if (numberOfChannels > 4) {
                if (!hasExtras) {
                    addFifths();
                    hasExtras = true;
                }
                else r++;
            }
        }
        else if (r == 4) {
            if (numberOfChannels > 4) {
                if (!hasExtras) {
                    addRoots();
                    hasExtras = true;
                }
                else r++;
            }
        }
        else if (r == 5) {
            if (numberOfChannels > 4) {
                if (hasExtras) {
                    addThirds();
                    hasExtras = true;
                }

            }
        }
        if (hasChords == false) {
            if (rand.nextInt(3) > 0) {
                generateChords();
            }
        }
        return this;
    }

    public int getTimesChordPlayed() {
        return timesChordPlayed;
    }

    public int getNotesInChords() {
        return notesInChords;
    }

    public int getBeatsInBar() {
        return beatsInBar;
    }

    public boolean getArpeggio() {
        return arpeggio;
    }

    public int[][][][] getProgression() {
        return progression;
    }

    public int getNumberOfBars() {
        return numberOfBars;
    }

    public int getDrumSpeed() {return drumSpeed;}

    public int getHatSpeed() {return hatSpeed;}

    public int getMelodyStart() {return melodyStart;}

    public int getMelodyEnd() {return melodyEnd;}

    public boolean getLongNoteMode() {return longNoteMode;}

    public boolean getIsPatternMelody() {return isPatternMelody;}

    public boolean getIsPatternHats() {return isPatternHats;}

    public int[] getScale() {return scale;}

    public int[][] getMelody() {
        return  melody;
    }

    public int getAdditionalPitchMelody() {
        return additionalPitchMelody;
    }

    public Chord[] getChordProgression() {return chordProgression;}

    public int getKey() {return key;}

    public int getPart() {return part;}

    public boolean getStructured() {return structured;}

}

//    void generateChords(int numberOfBars) {
//        chordProgression = new Chord[numberOfBars];
//        int b = 0;
//        int r = 0;
////        chooseNumChordNotes();
//        chooseTimesChordPlayed();
//        chooseProgHasArpeggio();
//        beatsInProg = numberOfBars * beatsInBar;
//        for (int j = 0; j < numberOfBars; j++) {
//            chordProgression[j] = chords[rand.nextInt(chords.length)];
//            for (int c = 0; c < timesChordPlayed; c++) {
//                b = (j * beatsInBar) / timesChordPlayed;
//                if (arpeggio) {
//                    int[] arp = getArpeggioOrder(notesInChords);
//                    for (int k = 0; k < notesInChords; k++) {
//                        //Play
//                        // progression[i][k][b] = chordProgression[i][j].getNotes()[k];
////                      midi.play(randomChordProgression[i][j].getNotes()[arp[k]] + 48, 0, midi.getTrack(0));
//                        //Stop
////                      midi.stop(randomChordProgression[i][j].getNotes()[arp[k]] + 48, beatsBetweenChords / timesChordPlayed / notesInChord, midi.getTrack(0));
//
//                    }
//                }
////                else if (arpeggio2) {
////                    for (int k = 0; k < notesInChords; k++) {
////                        song[i][k][b] = randomChordProgression[i][j].getNotes()[k] + 48;
////                        midi.play(song[i][k][b], 0, midi.getTrack(0));
////                        midi.play(0, 0, midi.getTrack(0));
////                        midi.stop(0, beatsBetweenChords/timesChordPlayed/notesInChords, midi.getTrack(0));
////                    }
////                    //Stop chord
////                    for (int k = 0; k < notesInChord; k++) {
////                        midi.stop(song[i][k][b], 0, midi.getTrack(0));
////                    }
////                }
//                else {
//                    for (int k = 0; k < notesInChords; k++) {
//                        //song[i][k][b] = chordProgression[i][j].getNotes()[k];
//                    }
//                    //Stop chord
//                    for (int k = 0; k < notesInChords; k++) {
//                        if (k == 0) {
////                            midi.stop(randomChordProgression[i][j].getNotes()[k] + 48, beatsBetweenChords / timesChordPlayed, midi.getTrack(0));
//                        } else {
////                            midi.stop(randomChordProgression[i][j].getNotes()[k] + 48, 0, midi.getTrack(0));
//                        }
//                    }
//                }
//
//            }
//        }
//        b = 0;
//
//    }

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
//                    r = rand.nextInt(7);
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