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

public class MIDIMaker
{
    // In de methode score specificeer je de melodie, door
    // aanroepen van de methode play.
    // Die methode heeft twee parameters:
    // * een int die de hoogte van de toon aangeeft (of 0 voor stilte)
    // * een int die de lengte van de toon aangeeft

    private static final long serialVersionUID = 1;

    public void score()
    {
        // dit is een voorbeeld-melodie: een toonladder
        play(c,4);
        play(e,4);
        play(g,4);
        play(b, 4);
        play(rest, 2);
        play(c2, 2);
        stop(c2, 2);
        play(b1, 2);
        stop(b1, 2);
        play(a1, 2);
        stop(a1, 2);
        stop(c, 4);
        stop(e, 4);
        stop(g, 4);
        stop(b, 4);
        stop(c1, 4);
        play(rest, 3);
        play(d, 4);
        play(f, 4);
        play(a,4);
        play(c1,4);
        play(rest, 2);
        play(e1,2);
        stop(e1,2);
        play(d1,2);
        stop(d1,2);
        play(c2,2);
        stop(c2,2);
        stop(d,4);
        stop(f,4);
        stop(a,4);
        stop(c1,4);
//        // en een drieklank:
//        play(c,4);
//        play(e,4);
//        play(g,4);
//        play(c1,8);
    }

    // er zijn constanten beschikbaar voor enkele veel-gebruikte toonhoogtes

    final int c0=48, d0=50, e0=52, f0=53, g0=55, a0=57, b0=59,
            c =60, d =62, e =64, f =65, g =67, a =69, b =71,
            c1=72, d1=74, e1=76, f1=77, g1=79, a1=81, b1=83,
            c2=84, rest=0;


    // met deze methoden kun je een noot een kruis (sharp) of een mol (flat) geven
    // of een octaaf verhogen of verlagen

    static int sharp(int x)
    {
        return x+1;
    }
    static int flat(int x)
    {
        return x-1;
    }
    static int high(int x)
    {
        return x+12;
    }
    static int low(int x)
    {
        return x-12;
    }


    //======================================================================
    // de rest van het programma dient niet veranderd te worden


    String filename;
    String tpqText;

    final byte NoteOn = (byte) 144;
    final byte NoteOff = (byte) 128;
    final byte defaultVolume = (byte) 100;
    final int  defaultTPQ = 480;

    int timeSinceLastNote;
    int tpq;
    DataOutputStream data;

    ByteArrayOutputStream track;



    public MIDIMaker(DataOutputStream data, String filename)
    {
//        this.setSize(200,200);
//        this.setTitle("Midi file generator");
//
//        filenameText = new TextField("music.midi", 20 );
//        tpqText      = new TextField(defaultTPQ+"", 5 );
//        genButton    = new Button("Generate");
//        messageLabel = new Label("enter file name and press button");
//
//        this.setLayout(new FlowLayout());
//        this.add( new Label("Filename:") );
//        this.add( filenameText );
//        this.add( new Label("ticks per quart:") );
//        this.add( tpqText );
//        this.add( genButton );
//        this.add( messageLabel );
//        genButton.addActionListener(this);
//        this.addWindowListener(this);
        Random r = new Random();
        track = new ByteArrayOutputStream();
        tpq =  200; //Tempo
        this.data = data;
        Log.v("generating track, ", "here goes..");
        track.reset();
        //tpq = Integer.parseInt(tpqText.getText());
        timeSinceLastNote = 0;
    }

    public void gen()//actionPerformed()ActionEvent ae)
    {
//        if (ae.getSource()==genButton)
//        {

           // score();

            try
            {
//                messageLabel.setText("writing file");
               // data = new DataOutputStream(new FileOutputStream(filenameText));
                data.writeBytes("MThd");
                data.writeInt(6);
                data.writeInt(65537);
                data.writeShort((short) defaultTPQ);
                data.writeBytes("MTrk");
                data.writeInt(track.size() + 4);
                //data.writeChars("00");
                int r = new Random().nextInt(96);
                data.writeShort((short)192); //c0
                data.writeByte(r);
                //data.writeByte(0);
                //data.writeChars("00c0"); //c0
                //data.writeChars("60"); //60
                //data.writeChars("00");
                data.write(track.toByteArray());
                data.writeInt(16723712);
                data.close();
                Log.v("SUC", "CES!");
            }

            catch (Exception e)
            {
                Log.v("Error writing file, ", "Oh no!");
            }
//        }
    }


    void play(int pitch, double duration)
    {
        int durat;
        durat = (int)(duration*tpq/4);

        if (pitch==0)
            timeSinceLastNote += durat;
        else
        {
            sendLength(timeSinceLastNote);
            sendByte( NoteOn );
            sendByte( (byte) pitch );
            sendByte( defaultVolume );

//            sendLength(durat);
//            sendByte( NoteOff );
//            sendByte( (byte) pitch );
//            sendByte( defaultVolume );
            timeSinceLastNote = 0;
        }
    }

    void stop(int pitch, double duration)
    {
        int durat;
        durat = (int)(duration*tpq/4);
        sendLength(durat);
        sendByte( NoteOff );
        sendByte( (byte) pitch );
        sendByte( defaultVolume );
        timeSinceLastNote = 0;
    }

    void sendLength(int x)
    {
        if (x>=2097152)
        {
            sendByte((byte)(128+x/2097152));
            x %= 2097152;
        }
        if (x>=16384)
        {
            sendByte((byte)(128+x/16384));
            x %= 16384;
        }
        if (x>=128)
        {
            sendByte((byte)(128+x/128));
            x %= 128;
        }
        sendByte( (byte)x );
    }

    void sendByte(byte b)
    {
        track.write(b);
    }

}
