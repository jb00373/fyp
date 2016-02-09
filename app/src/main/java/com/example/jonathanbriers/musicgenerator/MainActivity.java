package com.example.jonathanbriers.musicgenerator;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    SoundPool soundPool;
    HashMap<Integer, Integer> soundPoolMap;
    Generator generator;

    int soundIDC1 = 1;
    int soundIDCSharp1 = 2;
    int soundIDD1 = 3;
    int soundIDDSharp1 = 4;
    int soundIDE1 = 5;
    int soundIDF1 = 6;
    int soundIDFSharp1 = 7;
    int soundIDG1 = 8;
    int soundIDGSharp1 = 9;
    int soundIDA1 = 10;
    int soundIDASharp1 = 11;
    int soundIDB1 = 12;
    int soundIDC2 = 13;
    int soundIDCSharp2 = 14;
    int soundIDD2 = 15;
    int soundIDDSharp2 = 16;
    int soundIDE2 = 17;
    int soundIDF2 = 18;
    int soundIDFSharp2 = 19;
    int soundIDG2 = 20;
    int soundIDGSharp2 = 21;
    int soundIDA2 = 22;
    int soundIDASharp2 = 23;
    int soundIDB2 = 24;
    int soundIDKick = 25;
    int soundIDSnare = 26;
    int soundIDClap = 27;
    int soundIDHat1 = 28;
    int soundIDHat2 = 29;

    float maxVolume, curVolume, leftVolume, rightVolume, normalVolume;
    int maxSoundsAtOnce = 20;
    int priority,  no_loop;
    boolean playPressed;
    boolean chordBeatPlayed;
    boolean melodyBeatPlayed;
    boolean bassBeatPlayed;
    boolean drumBeatPlayed;

    int bassBeat;
    int chordBeat;
    int melodyBeat;
    private int drumBeat;

    int progNumber;
    int drumProgNumber;
    int melodyProgNumber;
    int timesProgPlayed;
    int[][][] song;
    EditText txtSeed;
    Button btnGenerate;
    Button btnPlay;
    Button btnPause;
    Button btnSeed;
    Integer randomSeed;
    String filename;
    DataOutputStream data;
    FileInputStream fi;
    MediaPlayer mediaPlayer;
    FileDescriptor fd;

    ScheduledExecutorService executor;
    ScheduledFuture<?> t;
    ScheduledFuture<?> u;
    ScheduledFuture<?> v;
    ScheduledFuture<?> w;
    ScheduledFuture<?> x;
    boolean cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Do not move
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtSeed = new EditText(this);
        txtSeed   = (EditText)findViewById(R.id.txtSeed);
        Random random = new Random(System.currentTimeMillis());
        randomSeed = random.nextInt(10000);
        String rs =  randomSeed.toString();
        txtSeed.setText(rs, TextView.BufferType.EDITABLE);

        //File file = new File(getApplicationContext().getFilesDir(), "music.midi");
        filename = "/storage/emulated/0/MusicGenerator/music.midi";

        mediaPlayer = new MediaPlayer();
        newSong();

//        try {
//            //Delete file if there already is one
//            File dir = getFilesDir();
//            File file = new File(filename);
//            boolean deleted = file.delete();
//            if (deleted) {
//                Log.e("Deleted", "yes");
//            }
//            else {
//                Log.e("Deleted", "nah");
//            }
//            FileOutputStream fo = new FileOutputStream(filename);
//            //fo = openFileOutput(filename, Context.MODE_PRIVATE);
//            data = new DataOutputStream(fo);
//            MIDIMaker m = new MIDIMaker(data, filename);
//            generator = new Generator(m);
//            generator.setSeed(randomSeed);
//            generator.newSong();
//            //m.gen();
//           // fi = openFileInput(filename);
//            fi = new FileInputStream(filename);
//            try {
//                fd = fi.getFD();
//                mediaPlayer.setDataSource(fd);
//                mediaPlayer.prepare();
//
//            }
//            catch (IOException f) {
//                f.printStackTrace();
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        //mediaPlayer = MediaPlayer.create(this, R.raw.moonlightmovement1);

        mediaPlayer.setLooping(true);


        btnGenerate = new Button(this);
        btnGenerate = (Button)findViewById(R.id.btnGenerate);
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newSong();
            }
        });
        btnPlay = new Button(this);
        btnPlay = (Button)findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.start();
                playPressed = true;
            }
        });

        btnPause = new Button(this);
        btnPause = (Button)findViewById(R.id.btnPause);
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPressed = false;
                mediaPlayer.pause();
            }
        });

        btnSeed = new Button(this);
        btnSeed = (Button)findViewById(R.id.btnSeed);
        btnSeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random random = new Random(System.currentTimeMillis());
                randomSeed = random.nextInt(10000);
                String rs = randomSeed.toString();
                txtSeed.setText(rs, TextView.BufferType.EDITABLE);
                generator.setSeed(randomSeed);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        soundPool = new SoundPool(maxSoundsAtOnce, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap<Integer, Integer>();
        //loadSounds();

        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        leftVolume  = curVolume/maxVolume;
        rightVolume = curVolume/maxVolume;
        priority = 1;
        no_loop = 0;

        //Number of threads
        executor = Executors.newScheduledThreadPool(7);
//
//        //Schedule each channel
//        t = executor.scheduleAtFixedRate(play, 0, 250 - (int) generator.getTempo(), TimeUnit.MILLISECONDS);
//        w = executor.scheduleAtFixedRate(chord2, 0, 250 - (int)generator.getTempo(), TimeUnit.MILLISECONDS);
//        x = executor.scheduleAtFixedRate(chord3, 0, 250 - (int)generator.getTempo(), TimeUnit.MILLISECONDS);
//        u = executor.scheduleAtFixedRate(drums, 0, 250 - (int)generator.getTempo(), TimeUnit.MILLISECONDS);
//        v = executor.scheduleAtFixedRate(melody, 0, 250 - (int)generator.getTempo(), TimeUnit.MILLISECONDS);

    }

    void newSong() {
        try {
            //Delete file if there already is one
            File dir = getFilesDir();
            File file = new File(filename);
            boolean deleted = file.delete();
            if (deleted) {
                Log.e("Deleted", "yes");
            }
            else {
                Log.e("Deleted", "nah");
            }
            FileOutputStream fo = new FileOutputStream(filename);
            //fo = openFileOutput(filename, Context.MODE_PRIVATE);
            data = new DataOutputStream(fo);
            MIDIMaker m = new MIDIMaker(data, filename);
            generator = new Generator(m);
            generator.setSeed(randomSeed);
            generator.newSong();
            mediaPlayer.reset();
            //m.gen();
            // fi = openFileInput(filename);
            fi = new FileInputStream(filename);
            try {
                fd = fi.getFD();
                mediaPlayer.setDataSource(fd);
                mediaPlayer.prepare();

            }
            catch (IOException f) {
                f.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

//    void newSong() {
//        t.cancel(false);
//        u.cancel(false);
//        v.cancel(false);
//        w.cancel(false);
//        x.cancel(false);
//        executor = Executors.newScheduledThreadPool(7);
//        playPressed = false;
//        leftVolume = 0;
//        rightVolume = 0;
//        if (txtSeed.getText().toString() != "") {
//            generator.setSeed((long) txtSeed.getText().toString().hashCode());
//            chordBeat = 0;
//            chord2Beat = 0;
//            chord3Beat = 0;
//            drumBeat = 0;
//            melodyBeat = 0;
//            generator.newSong();
//            song = generator.getSong();
//            t = executor.scheduleAtFixedRate(play, 0, 250 - (int) generator.getTempo(), TimeUnit.MILLISECONDS);
//            w = executor.scheduleAtFixedRate(chord2, 0, 250 - (int) generator.getTempo(), TimeUnit.MILLISECONDS);
//            x = executor.scheduleAtFixedRate(chord3, 0, 250 - (int)generator.getTempo(), TimeUnit.MILLISECONDS);
//            u = executor.scheduleAtFixedRate(drums, 0, 250 - (int)generator.getTempo(), TimeUnit.MILLISECONDS);
//            v = executor.scheduleAtFixedRate(melody, 0, 250 - (int)generator.getTempo(), TimeUnit.MILLISECONDS);
//
//
//        }
//        else {
//            Random random = new Random(System.currentTimeMillis());
//            Integer randomSeed = random.nextInt(10000);
//            String rs =  randomSeed.toString();
//            txtSeed.setText(rs, TextView.BufferType.EDITABLE);
//            generator.setSeed(randomSeed);
//            chordBeat = 0;
//            chord2Beat = 0;
//            chord3Beat = 0;
//            drumBeat = 0;
//            melodyBeat = 0;
//            generator.newSong();
//            song = generator.getSong();
//            t = executor.scheduleAtFixedRate(play, 0, 250 - (int) generator.getTempo(), TimeUnit.MILLISECONDS);
//            w = executor.scheduleAtFixedRate(chord2, 0, 250 - (int) generator.getTempo(), TimeUnit.MILLISECONDS);
//            x = executor.scheduleAtFixedRate(chord3, 0, 250 - (int)generator.getTempo(), TimeUnit.MILLISECONDS);
//            u = executor.scheduleAtFixedRate(drums, 0, 250 - (int)generator.getTempo(), TimeUnit.MILLISECONDS);
//            v = executor.scheduleAtFixedRate(melody, 0, 250 - (int)generator.getTempo(), TimeUnit.MILLISECONDS);
//
//
//
//        }
//    }

    Runnable play = new Runnable() {
        public void run() {
            if (playPressed) {
//                leftVolume  = curVolume/maxVolume;
//                rightVolume = curVolume/maxVolume;
                if (!chordBeatPlayed) {
                    for (int i = 0; i < generator.getChannels(); i++) {
                        if (song[progNumber][i][chordBeat] != 0) {
                            if (i == 0) {
                                soundPool.play(getNote(song[progNumber][i][chordBeat], i), leftVolume, 0, priority, no_loop, 1.0f);
                            }
//                            else if (i == 1) {
//                                soundPool.play(getNote(song[progNumber][i][chordBeat], i), leftVolume/2, rightVolume/2, priority, no_loop, 1.0f);
//
//                            }
//                            else if (i == 2) {
//                                soundPool.play(getNote(song[progNumber][i][chordBeat], i), leftVolume, 0, priority, no_loop, 1.0f);
//                            }
//                            else if (i == 5) {
//                                soundPool.play(getNote(song[progNumber][i][chordBeat], i), 0, rightVolume, priority, no_loop, 1.0f);
//                                //Log.v("Note:", (generator.numberToNote(getNote(song[progNumber][i][chordBeat], i)).toString()));
//                            }
//                            else if (i == 9) {
//                                soundPool.play(getNote(song[progNumber][i][chordBeat], i), leftVolume, rightVolume, priority, no_loop, 1.0f);
//                            }

                        }
                    }
                    chordBeatPlayed = true;
                }
                //If at end of progression, move to next progression
                if (chordBeat == generator.getBeatsInProg()[progNumber] - 1) {
                    if (progNumber != generator.getNumberOfProgressions() - 1) {
                        progNumber++;
                    } else {
                        //If at end of progression, go back to first progression
                        //i.e. Loop
                        progNumber = 0;
                    }
                    chordBeatPlayed = false;
                    timesProgPlayed = 0;
                    chordBeat = 0;

                }
                //Else move on to next chordBeat
                else {
                    chordBeat++;
                    chordBeatPlayed = false;
                }
            }

        }
    };




    public void loadSounds() {
        //Piano octave 1
        soundPoolMap.put(soundIDC1, soundPool.load(this, R.raw.c1, 1));
        soundPoolMap.put(soundIDCSharp1, soundPool.load(this, R.raw.c_sharp1, 2));
        soundPoolMap.put(soundIDD1, soundPool.load(this, R.raw.d1, 3));
        soundPoolMap.put(soundIDDSharp1, soundPool.load(this, R.raw.d_sharp1, 4));
        soundPoolMap.put(soundIDE1, soundPool.load(this, R.raw.e1, 5));
        soundPoolMap.put(soundIDF1, soundPool.load(this, R.raw.f1, 6));
        soundPoolMap.put(soundIDFSharp1, soundPool.load(this, R.raw.f_sharp1, 7));
        soundPoolMap.put(soundIDG1, soundPool.load(this, R.raw.g1, 8));
        soundPoolMap.put(soundIDGSharp1, soundPool.load(this, R.raw.g_sharp1, 9));
        soundPoolMap.put(soundIDC1, soundPool.load(this, R.raw.a1, 10));
        soundPoolMap.put(soundIDCSharp1, soundPool.load(this, R.raw.a_sharp1, 11));
        soundPoolMap.put(soundIDB1, soundPool.load(this, R.raw.b1, 12));

        //Piano octave 2
        soundPoolMap.put(soundIDC2, soundPool.load(this, R.raw.c2, 13));
        soundPoolMap.put(soundIDCSharp2, soundPool.load(this, R.raw.c_sharp2, 14));
        soundPoolMap.put(soundIDD2, soundPool.load(this, R.raw.d2, 15));
        soundPoolMap.put(soundIDDSharp2, soundPool.load(this, R.raw.d_sharp2, 16));
        soundPoolMap.put(soundIDE2, soundPool.load(this, R.raw.e2, 17));
        soundPoolMap.put(soundIDF2, soundPool.load(this, R.raw.f2, 18));
        soundPoolMap.put(soundIDFSharp2, soundPool.load(this, R.raw.f_sharp2, 19));
        soundPoolMap.put(soundIDG2, soundPool.load(this, R.raw.g2, 20));
        soundPoolMap.put(soundIDGSharp2, soundPool.load(this, R.raw.g_sharp2, 21));
        soundPoolMap.put(soundIDA2, soundPool.load(this, R.raw.a2, 22));
        soundPoolMap.put(soundIDASharp2, soundPool.load(this, R.raw.a_sharp2, 23));
        soundPoolMap.put(soundIDB2, soundPool.load(this, R.raw.b2, 24));

        //Hiphopheavy drums
        soundPoolMap.put(soundIDKick, soundPool.load(this, R.raw.kick, 25));
        soundPoolMap.put(soundIDSnare, soundPool.load(this, R.raw.snare, 26));
        soundPoolMap.put(soundIDClap, soundPool.load(this, R.raw.clap, 27));
        soundPoolMap.put(soundIDHat1, soundPool.load(this, R.raw.hat1, 28));
        soundPoolMap.put(soundIDHat2, soundPool.load(this, R.raw.hat2, 29));

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean chord2BeatPlayed;
    private int chord2ProgNumber;
    private int chord2Beat;
    Runnable chord2 = new Runnable() {
        @Override
        public void run() {
            if (playPressed) {
                if (!chord2BeatPlayed) {
                    if (song[chord2ProgNumber][1][chord2Beat] != 0) {
                        soundPool.play(getNote(song[chord2ProgNumber][1][chord2Beat], 1), leftVolume/4, rightVolume/4, priority, no_loop, 1.0f);
                        // Log.v("Note:", (generator.numberToNote(getNote(song[progNumber][5][melodyBeat], 5)).toString()));
                    }
                    chord2BeatPlayed = true;
                }

                if (chord2Beat == generator.getBeatsInProg()[chord2ProgNumber] - 1) {
                    if (chord2ProgNumber != generator.getNumberOfProgressions() - 1) {
                        chord2ProgNumber++;
                    } else {
                        chord2ProgNumber = 0;
                    }
                    chord2BeatPlayed = false;
                    timesProgPlayed = 0;
                    chord2Beat = 0;

                } else {
                    chord2Beat++;
                    chord2BeatPlayed = false;
                }
            }
        }
    };

    private boolean chord3BeatPlayed;
    private int chord3ProgNumber;
    private int chord3Beat;
    Runnable chord3 = new Runnable() {
        @Override
        public void run() {
            if (playPressed) {
                if (!chord3BeatPlayed) {
                    if (song[chord3ProgNumber][2][chord3Beat] != 0) {
                        soundPool.play(getNote(song[chord3ProgNumber][2][chord3Beat], 2), leftVolume/4, rightVolume/4, priority, no_loop, 1.0f);
                        // Log.v("Note:", (generator.numberToNote(getNote(song[progNumber][5][melodyBeat], 5)).toString()));
                    }
                    chord3BeatPlayed = true;
                }

                if (chord3Beat == generator.getBeatsInProg()[chord3ProgNumber] - 1) {
                    if (chord3ProgNumber != generator.getNumberOfProgressions() - 1) {
                        chord3ProgNumber++;
                    } else {
                        chord3ProgNumber = 0;
                    }
                    chord3BeatPlayed = false;
                    timesProgPlayed = 0;
                    chord3Beat = 0;

                } else {
                    chord3Beat++;
                    chord3BeatPlayed = false;
                }
            }
        }
    };

    Runnable melody = new Runnable() {
        @Override
        public void run() {
            if (playPressed) {
                if (!melodyBeatPlayed) {
                    if (song[melodyProgNumber][5][melodyBeat] != 0) {
                        soundPool.play(getNote(song[melodyProgNumber][5][melodyBeat], 5), 0, rightVolume, priority, no_loop, 1.0f);
                        // Log.v("Note:", (generator.numberToNote(getNote(song[progNumber][5][melodyBeat], 5)).toString()));
                    }
                    melodyBeatPlayed = true;
                }

                if (melodyBeat == generator.getBeatsInProg()[melodyProgNumber] - 1) {
                    if (melodyProgNumber != generator.getNumberOfProgressions() - 1) {
                        melodyProgNumber++;
                    } else {
                        melodyProgNumber = 0;
                    }
                    melodyBeatPlayed = false;
                    timesProgPlayed = 0;
                    melodyBeat = 0;

                } else {
                    melodyBeat++;
                    melodyBeatPlayed = false;
                }
            }
        }
    };
    Runnable drums = new Runnable() {
        @Override
        public void run() {
            if (playPressed) {
                if (!drumBeatPlayed) {
                    if (song[drumProgNumber][9][drumBeat] != 0) {
                        soundPool.play(getNote(song[drumProgNumber][9][drumBeat], 9), leftVolume, rightVolume, priority, no_loop, 1.0f);
                    }
                    drumBeatPlayed = true;
                }
                if (drumBeat == generator.getBeatsInProg()[drumProgNumber] - 1) {
                    if (drumProgNumber != generator.getNumberOfProgressions() - 1) {
                        drumProgNumber++;
                    } else {
                        drumProgNumber = 0;
                    }
                    drumBeatPlayed = false;
                    timesProgPlayed = 0;
                    drumBeat = 0;

                } else {
                    drumBeat++;
                    drumBeatPlayed = false;
                }
            }
        }
    };

    int getNote(int note, int channel) {
        if (channel == 9) {
            switch (note + 1) {
                case 1:
                    return soundIDKick;
                case 2:
                    return soundIDSnare;
                case 3:
                    return soundIDClap;
                case 4:
                    return soundIDHat1;
                case 5:
                    return soundIDHat2;
                case 6:
                    return soundIDKick;
                case 7:
                    return soundIDSnare;
                case 8:
                    return soundIDClap;
                case 9:
                    return soundIDHat1;
                case 10:
                    return soundIDHat2;
            }
        }
        if (channel == 5) {
            switch (note + 1) {
                case 1:
                    return soundIDC2;
                case 2:
                    return soundIDCSharp2;
                case 3:
                    return soundIDD2;
                case 4:
                    return soundIDDSharp2;
                case 5:
                    return soundIDE2;
                case 6:
                    return soundIDF2;
                case 7:
                    return soundIDFSharp2;
                case 8:
                    return soundIDG2;
                case 9:
                    return soundIDGSharp2;
                case 10:
                    return soundIDA2;
                case 11:
                    return soundIDASharp2;
                case 12:
                    return soundIDB2;
                case 13:
                    return soundIDC2;
                case 14:
                    return soundIDCSharp2;
                case 15:
                    return soundIDD2;
                case 16:
                    return soundIDDSharp2;
                case 17:
                    return soundIDE2;
                case 18:
                    return soundIDF2;
                case 19:
                    return soundIDFSharp2;
                case 20:
                    return soundIDG2;
                case 21:
                    return soundIDGSharp2;
                case 22:
                    return soundIDA2;
                case 23:
                    return soundIDASharp2;
                case 24:
                    return soundIDB2;
                case 25:
                    return soundIDC2;
                case 26:
                    return soundIDCSharp2;
                case 27:
                    return soundIDD2;
                case 28:
                    return soundIDDSharp2;
                case 29:
                    return soundIDE2;
                case 30:
                    return soundIDF2;
                case 31:
                    return soundIDFSharp2;
                case 32:
                    return soundIDG2;
                case 33:
                    return soundIDGSharp2;
                case 34:
                    return soundIDA2;
                case 35:
                    return soundIDASharp2;
                case 36:
                    return soundIDB2;
            }
        } else {
            switch (note + 1) {
                case 1:
                    return soundIDC1;
                case 2:
                    return soundIDCSharp1;
                case 3:
                    return soundIDD1;
                case 4:
                    return soundIDDSharp1;
                case 5:
                    return soundIDE1;
                case 6:
                    return soundIDF1;
                case 7:
                    return soundIDFSharp1;
                case 8:
                    return soundIDG1;
                case 9:
                    return soundIDGSharp1;
                case 10:
                    return soundIDA1;
                case 11:
                    return soundIDASharp1;
                case 12:
                    return soundIDB1;
                case 13:
                    return soundIDC1;
                case 14:
                    return soundIDCSharp1;
                case 15:
                    return soundIDD1;
                case 16:
                    return soundIDDSharp1;
                case 17:
                    return soundIDE1;
                case 18:
                    return soundIDF1;
                case 19:
                    return soundIDFSharp1;
                case 20:
                    return soundIDG1;
                case 21:
                    return soundIDGSharp1;
                case 22:
                    return soundIDA1;
                case 23:
                    return soundIDASharp1;
                case 24:
                    return soundIDB1;
                case 25:
                    return soundIDC1;
                case 26:
                    return soundIDCSharp1;
                case 27:
                    return soundIDD1;
                case 28:
                    return soundIDDSharp1;
                case 29:
                    return soundIDE1;
                case 30:
                    return soundIDF1;
                case 31:
                    return soundIDFSharp1;
                case 32:
                    return soundIDG1;
                case 33:
                    return soundIDGSharp1;
                case 34:
                    return soundIDA1;
                case 35:
                    return soundIDASharp1;
                case 36:
                    return soundIDB1;
            }
        }
        return 0;
    }

}
