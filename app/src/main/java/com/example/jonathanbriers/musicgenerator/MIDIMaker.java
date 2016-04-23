package com.example.jonathanbriers.musicgenerator;
/**
 * Created by Jonny on 30/12/2015.
 */
import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.*;
import java.util.Random;

// Dit programma genereert een MIDI-file met de muziek
// die in de methode score wordt gespecificeerd.

public class MIDIMaker {
    // In de methode score specificeer je de melodie, door
    // aanroepen van de methode play.
    // Die methode heeft twee parameters:
    // * een int die de hoogte van de toon aangeeft (of 0 voor stilte)
    // * een int die de lengte van de toon aangeeft

    private static final long serialVersionUID = 1;

    final byte NoteOn = (byte) 144;
    final byte NoteOff = (byte) 128;
    final byte defaultVolume = (byte) 75;
    final byte drumVolume = (byte) 100;
    final int defaultTPQ = 480;

    // int timeSinceLastNote;
    int tpq;
    int tempo;
    DataOutputStream data;

    int numberOfTracks;

    //    Track track0 = new Track(0);
//    Track track1 = new Track(1);
//    Track track2 = new Track(9);
    Track tracks[] = new Track[3];

    public MIDIMaker(DataOutputStream data, String filename) {
        Random r = new Random();

//        tracks[0] = track0;
//        tracks[1] = track1;
//        tracks[2] = track2;
        this.data = data;
        Log.v("generating track, ", "here goes..");
//        track0.getStream().reset();
//        track1.getStream().reset();
//        track2.getStream().reset();
        //tpq = Integer.parseInt(tpqText.getText());
        // timeSinceLastNote = 0;
    }


    public void setTpq(int tpq) {
        this.tpq = tpq;
        // tpq = 10000;
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

    public Track getTrack(int i) {
        return tracks[i];
    }

    public void setTracks(Track[] tracks) {
        this.tracks = tracks;
        numberOfTracks = tracks.length;
    }

    public void setNumberOfTracks(int numberOfTracks, boolean hasDrums) {
        this.numberOfTracks = numberOfTracks;
        tracks = new Track[numberOfTracks];
        for (int i = 0; i < numberOfTracks - 1; i++) {
            tracks[i] = new Track(i);
            tracks[i].getStream().reset();
        }
        if (hasDrums) {
            tracks[numberOfTracks - 1] = new Track(9);
            tracks[numberOfTracks - 1].getStream().reset();
        }
        else {
            tracks[numberOfTracks - 1] = new Track(numberOfTracks - 1);
            tracks[numberOfTracks - 1].getStream().reset();
        }
    }

    public void gen(boolean hasDrums) {
        try {
            data.writeBytes("MThd");
            data.writeInt(6);
            //Ends in 8 because 3 tracks not 1
            data.writeInt(65537 + numberOfTracks);
            Log.d("TempoMIDI", "" + tpq);
            data.writeShort((short) tpq);
            writeTrackTempo();
            Log.d("HasDrums =" , ""+hasDrums);
            if (hasDrums) {
                for (int i = 0; i < numberOfTracks - 1; i++) {
                    writeTrack(i);
                }
                //Write drum track
                int r = new Random().nextInt(96);

                data.writeBytes("MTrk");
                data.writeInt(tracks[numberOfTracks - 1].getStream().size() + 7);

                data.writeShort((short) 201); //c9
                data.writeByte(r);
                data.write(tracks[numberOfTracks - 1].getStream().toByteArray());
                data.writeInt(16723712);
                data.close();
                Log.v("SUC", "CES!");
            } else {
                for (int i = 0; i < numberOfTracks; i++) {
                    writeTrack(i);
                }
                data.close();
                Log.v("SUC", "CES!");
            }
        } catch (Exception e) {
            Log.v("Error writing file, ", "Oh no!");
        }
    }

    public void writeTrack(int i) {
        try {
            data.writeBytes("MTrk");
            data.writeInt(tracks[i].getStream().size() + 7);
            data.writeShort((short) 192 + i); //192 = c0
            data.writeByte(tracks[i].getInstrument());
            data.write(tracks[i].getStream().toByteArray());
            data.writeInt(16723712);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeTrackTempo() {
        try {
            data.writeBytes("MTrk");
            data.writeInt(19);
            //ff5804
            data.writeInt(16734212);
            //04021808
            data.writeInt(67246088);
            //ff5103
            data.writeInt(16732419);
//            data.writeInt(400000);
//            tempo = 400000;
            int hexTempo = intToHex(100000 * 60/tempo);
            data.writeByte(Integer.parseInt(Integer.toString(hexTempo).substring(0, 2)));
            data.writeByte(Integer.parseInt(Integer.toString(hexTempo).substring(2, 4)));
            data.writeByte(Integer.parseInt(Integer.toString(hexTempo).substring(4, 6)));
//            data.writeByte(6);
//            data.writeByte(26);
//            data.write(128);
//            data.writeShort((short)1000000 * 60/tempo);
//            data.writeShort((short) 192 + i); //c0
//            data.writeByte(tracks[i].getInstrument());
//            data.write(tracks[i].getStream().toByteArray());
            data.writeInt(16723712);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int intToHex(int theInt) {
        return Integer.valueOf(String.valueOf(theInt), 16);
    }


    void play(int pitch, double duration, Track track) {
        int durat;
        durat = (int) (duration * tpq / 4);

        if (pitch == 0) {
            track.setTimeSinceLastNote(track.getTimeSinceLastNote() + durat);
        }
//        if (track.getNumber() == 1) {
//            Log.d("Track", "1");
//        }
        else {
            sendLength(track.getTimeSinceLastNote(), track);
            sendByte((byte) (NoteOn + track.getNumber()), track);
            sendByte((byte) pitch, track);
            if (track.getNumber() == 9) {
                sendByte(drumVolume, track);
            } else {
                sendByte(defaultVolume, track);
            }
            track.setTimeSinceLastNote(0);
        }
    }

    void stop(int pitch, double duration, Track track) {
        int durat;
        durat = (int) (duration * tpq / 4);
        sendLength(durat, track);
        sendByte((byte) (NoteOff + track.getNumber()), track);
        sendByte((byte) pitch, track);
        if (track.getNumber() == 9) {
            sendByte(drumVolume, track);
        }
        else {
            sendByte(defaultVolume, track);
        }
        track.setTimeSinceLastNote(0);
    }

    void sendLength(int x, Track track) {
        if (x >= 2097152) {
            sendByte((byte) (128 + x / 2097152), track);
            x %= 2097152;
        }
        if (x >= 16384) {
            sendByte((byte) (128 + x / 16384), track);
            x %= 16384;
        }
        if (x >= 128) {
            sendByte((byte) (128 + x / 128), track);
            x %= 128;
        }
        sendByte((byte) x, track);
    }

    void sendByte(byte b, Track track) {
        track.getStream().write(b);
    }
}
//    public void gen2Tracks()//actionPerformed()ActionEvent ae)
//    {
//        try
//        {
//            data.writeBytes("MThd");
//            data.writeInt(6);
//            //Ends in 8 because 2 tracks not 1
//            data.writeInt(65538);
//            Log.d("TempoMIDI", "" + tpq);
//            data.writeShort((short) tpq);
//
//            int r = new Random().nextInt(96);
//            data.writeBytes("MTrk");
//            //+4 is for 00 FF 2F 00
//            data.writeInt(track0.getStream().size() + 7);
//            data.writeShort((short) 192); //c0
//            data.writeByte(tracks[0].getInstrument());
//            Log.d("MIDIMaker instrument", "" + tracks[0].getInstrument());
//            data.write(track0.getStream().toByteArray());
//            Log.d("gen2tracks", "" + tracks[0].getStream().toByteArray());
//            data.writeInt(16723712);
//
//            r = new Random().nextInt(96);
//            data.writeBytes("MTrk");
//            data.writeInt(track1.getStream().size() + 7);
//            data.writeShort((short) 193); //c1
//            data.writeByte(track1.getInstrument());
//            data.write(track1.getStream().toByteArray());
//            data.writeInt(16723712);
//            data.close();
//            Log.v("SUC", "CES!");
//        }
//
//        catch (Exception e)
//        {
//            Log.v("Error writing file, ", "Oh no!");
//        }
////        }
//    }
//
//    public void gen3Tracks()//actionPerformed()ActionEvent ae)
//    {
//        try
//        {
//            data.writeBytes("MThd");
//            data.writeInt(6);
//            //Ends in 8 because 3 tracks not 1
//            data.writeInt(65539);
//            Log.d("TempoMIDI", "" + tpq);
//            data.writeShort((short) tpq);
//
//            int r = new Random().nextInt(96);
//            data.writeBytes("MTrk");
//            //+4 is for 00 FF 2F 00
//            data.writeInt(track0.getStream().size() + 7);
//            data.writeShort((short)192); //c0
//            data.writeByte(r);
//            data.write(track0.getStream().toByteArray());
//            data.writeInt(16723712);
//
//            r = new Random().nextInt(96);
//            data.writeBytes("MTrk");
//            data.writeInt(track1.getStream().size() + 7);
//            data.writeShort((short) 193); //c1
//            data.writeByte(r);
//            data.write(track1.getStream().toByteArray());
//            data.writeInt(16723712);
//
//            r = new Random().nextInt(96);
//            data.writeBytes("MTrk");
//            data.writeInt(track2.getStream().size() + 7);
//            data.writeShort((short) 201); //c9
//            data.writeByte(r);
//            data.write(track2.getStream().toByteArray());
//            data.writeInt(16723712);
//            data.close();
//            Log.v("SUC", "CES!");
//        }
//
//        catch (Exception e)
//        {
//            Log.v("Error writing file, ", "Oh no!");
//        }
////        }
//    }

//    public void genOld()//actionPerformed()ActionEvent ae)
//    {
//            try
//            {
//                data.writeBytes("MThd");
//                data.writeInt(6);
//                data.writeInt(65537);
//                Log.d("TempoMIDI", "" + tpq);
//                data.writeShort((short) tpq);
//                data.writeBytes("MTrk");
//                data.writeInt(track0.getStream().size() + 4);
//                //data.writeChars("00");
//                int r = new Random().nextInt(96);
//                data.writeShort((short) 192); //c0
//                data.writeByte(r);
//                data.write(track0.getStream().toByteArray());
//                data.writeInt(16723712);
//                data.close();
//                Log.v("SUC", "CES!");
//            }
//
//            catch (Exception e)
//            {
//                Log.v("Error writing file, ", "Oh no!");
//            }
////        }
//    }

//
//            if (i == 0) {
//                data.writeInt(tracks[i].getStream().size() + 14);
//                //ff 5103
//                data.writeInt(16732419);
////                data.writeShort((short)(1000 * 60 / tempo));
//                data.writeShort((short)5);
//                data.writeShort((short)184);
//                data.writeShort((short)216);
//                //ff5804
//                data.writeInt(16734212);
//                //04021808
//                data.writeInt(67246088);
//            }
//            else {