package com.example.jonathanbriers.musicgenerator;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements android.widget.MediaController.MediaPlayerControl {

    Generator generator;

    EditText txtSeed;
    Button btnGenerate;
    Button btnSeed;
    Button btnExport;
    Button btnSave;
    Button btnLoad;
    Button btnRate;
    Button btnGenSmart;
    CheckBox cbxInfite;
    RatingBar ratingBar;
    Integer initialSeed;
    String filename;
    DataOutputStream data;
    FileInputStream fi;
    MediaPlayer mediaPlayer;
    FileDescriptor fd;
    TextFileReader tfr;
    SharedPreferences mPrefs;
    //service
    private AudioPlayer audioPlayer;
    private Intent playIntent;
    //binding
    private boolean musicBound=false;
    private boolean hasGenerated;
    private AudioController ac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Do not move
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        txtSeed = new EditText(this);
        txtSeed   = (EditText)findViewById(R.id.txtSeed);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open("wordlist.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        tfr = new TextFileReader(reader);

        filename = "/storage/emulated/0/MusicGenerator/music.midi";
        mediaPlayer = new MediaPlayer();

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        prepareSeed();
        newSong(false);

        ratingBar = new RatingBar(this);
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);

        btnGenerate = new Button(this);
        btnGenerate = (Button)findViewById(R.id.btnGenerate);
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newSong(false);
//                ac.show();
            }
        });

        btnSeed = new Button(this);
        btnSeed = (Button)findViewById(R.id.btnSeed);
        btnSeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareSeed();
            }
        });

        btnExport = new Button(this);
        btnExport = (Button)findViewById(R.id.btnExport);
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                export();
            }
        });


        btnSave = new Button(this);
        btnSave = (Button)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btnLoad = new Button(this);
        btnLoad = (Button)findViewById(R.id.btnLoad);
        btnLoad.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                load();
            }
        });

        btnRate = new Button(this);
        btnRate = (Button)findViewById(R.id.btnRate);
        btnRate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                rate();}
        });

        cbxInfite = new CheckBox((this));
        cbxInfite = (CheckBox)findViewById(R.id.cbxInfinite);

        btnGenSmart = new Button(this);
        btnGenSmart = (Button)findViewById(R.id.btnGenSmart);
        btnGenSmart.setOnClickListener(new View.OnClickListener(){
            Handler handler = new Handler();

            @Override
            public void onClick(View v) {
                newSong(true);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
//        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//        curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//        leftVolume  = curVolume/maxVolume;
//        rightVolume = curVolume/maxVolume;
//        priority = 1;
//        no_loop = 0;
        audioPlayer = new AudioPlayer(mediaPlayer, filename);
        findViewById(R.id.player_control).post(new Runnable() {
            public void run() {
                setController();
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                if (cbxInfite.isChecked()) {
                    prepareSeed();
                    newSong(false);
                    audioPlayer.go();
                }
            }

        });

    }

    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioPlayer.MusicBinder binder = (AudioPlayer.MusicBinder)service;

            //get service
            audioPlayer = binder.getService();
            //pass list
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    //start and bind the service when the activity starts
    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, AudioPlayer.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    private void setController(){
        if (ac == null) {
            ac = new AudioController(this);
        }
        //set previous and next button listeners
        //set and show
        ac.setMediaPlayer(this);
        ac.setAnchorView(findViewById(R.id.player_control));
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        ac.setPadding(0, (int) (metrics.heightPixels / 10.25f), 0, 0);
        ac.setEnabled(true);
        ac.show();
    }

    void prepareSeed() {
        Random random = new Random(System.currentTimeMillis());
        initialSeed = random.nextInt(122587);
        tfr.readWordlist();
        String rs = tfr.getTitle(initialSeed);
        txtSeed.setText(rs, TextView.BufferType.EDITABLE);
        getSeedFromTxt();
    }

    public int getSeedFromTxt() {
        int i = 0;
        int mainSeed = 0;
        String rs = txtSeed.getText().toString();
        char[] rsChar = rs.toCharArray();
        while (i < rs.length()) {
            mainSeed += (int)rsChar[i] * i;
            i++;
        }
        return mainSeed;
    }

    boolean isSmart;
    void newSong(boolean smart) {
        try {
            mediaPlayer.stop();
            mediaPlayer.reset();
            findViewById(R.id.player_control).post(new Runnable() {
                public void run() {
                    setController();
                }
            });
            //Delete file if there already is one
            File file = new File(filename);
            boolean deleted = file.delete();
            if (deleted) {
                Log.e("Deleted", "yes");
            }
            else {
                Log.e("Deleted", "nah");
            }
            FileOutputStream fo = new FileOutputStream(filename);
            data = new DataOutputStream(fo);
            MIDIMaker m = new MIDIMaker(data, filename);
            if (!hasGenerated) {
                generator = new Generator(m, mPrefs);
                hasGenerated = true;
            }
            else {
                generator.setMidi(m);
            }

            if (smart) {
                generator.genSmart();
                isSmart = true;
            }
            else {
                generator.setSeed(getSeedFromTxt());
                generator.newSong();
                isSmart = false;
            }
            mediaPlayer.reset();
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

    void export() {
        String title = "/storage/emulated/0/MusicGenerator/" + txtSeed.getText().toString() + ".midi";
        try {
            FileOutputStream fo = new FileOutputStream(title);
            data = new DataOutputStream(fo);
            MIDIMaker m = new MIDIMaker(data, title);
            generator = new Generator(m, mPrefs);
            if (isSmart) {
                generator.genSmart();
            }
            else {
                generator.setSeed(getSeedFromTxt());
                generator.newSong();
            }
            Context context = getApplicationContext();
            CharSequence text = "Exported as MIDI file in: " + title;
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void save() throws IOException {
        String title = "/storage/emulated/0/MusicGenerator/SavedWords.txt";
        File file = new File(title);
        FileWriter fw = new FileWriter(file, true);
        File dir = getFilesDir();
        boolean exists = file.exists();
        if (exists) {
            fw.append("\r\n" + txtSeed.getText().toString());
            fw.append("\r\n" + txtSeed.getText().toString());
        }
        else {
            fw.append(txtSeed.getText().toString());
        }
        fw.flush();
        fw.close();
//        btnLoad.setEnabled(false);
        Context context = getApplicationContext();
        CharSequence text = "Saved!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    void load() {
        ArrayList<String> savedTitles = new ArrayList<String>();
        String title = "/storage/emulated/0/MusicGenerator/SavedWords.txt";
        File file = new File(title);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String str;
            while ((str = br.readLine()) != null && str != "") {
                savedTitles.add(br.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent i = new Intent(this, LoadActivity.class);
        Bundle b = new Bundle();
        b.putStringArrayList("List", savedTitles);
        i.putExtras(b);
        startActivityForResult(i, 1);
    }

    public void rate() {
        generator.getSong().setRating((int)(ratingBar.getRating() * 2));
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int howMany = mPrefs.getAll().size();
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(generator.getSong());
        prefsEditor.putString(((Integer)(howMany)).toString(), json);
        prefsEditor.apply();
        ratingBar.setRating(0.0f);
        Log.d("mPrefs size =", ""+howMany);
        generator.getAi().setmPrefs(mPrefs);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                txtSeed.setText(data.getStringExtra("Title"));
                newSong(false);
            }
        }
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
//            Bundle b = new Bundle();
//            Gson gson = new Gson();
//            String json = gson.toJson(getApplicationContext());
//            b.putString("applicationContext", json);
            Intent i = new Intent(this, SettingsActivity.class);
//            i.putExtras(b);
            startActivity(i);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void start() {
        audioPlayer.go();
    }

    @Override
    public void pause() {
        audioPlayer.pausePlayer();
    }

    @Override
    public int getDuration() {
        return audioPlayer.getDur();
    }

    @Override
    public int getCurrentPosition() {
        return audioPlayer.getPosn();
    }

    @Override
    public void seekTo(int pos) {
        audioPlayer.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        return audioPlayer.isPng();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}

//        btnPlay = new Button(this);
//        btnPlay = (Button)findViewById(R.id.btnPlay);
//        btnPlay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mediaPlayer.start();
//                mediaPlayer.setLooping(true);
//                playPressed = true;
//            }
//        });
//
//        btnPause = new Button(this);
//        btnPause = (Button)findViewById(R.id.btnPause);
//        btnPause.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (playPressed == true) {
//                    playPressed = false;
//                    mediaPlayer.pause();
//                }
//            }
//        });

//    public void writeSongToFile() {
//        String title = "/storage/emulated/0/MusicGenerator/RatedSongs.txt";
//        File file = new File(title);
//        Song song = generator.getSong();
//        try {
//            FileWriter fw = new FileWriter(file, true);
//            if (file.exists()) {
//                fw.append(txtSeed.getText().toString() + "," + ratingBar.getRating() + "\n");
//                Track[] tracks = song.getTracks();
//                for (int i = 0; i < song.getNumberOfChannels(); i++) {
//                    fw.append(i + "," + tracks[i].getInstrument() + ",");
//                }
//                fw.append("\n");
//                fw.append(""+ song.getTempo() +"\n");
//                fw.append("" + song.getKey() + "\n");
//                fw.append("" + song.getDorian() + "\n");
//                fw.append("" + song.getMixolydian() + "\n");
//                fw.append("" + song.getHasDrums() + "\n");
//                for (int i = 0; i < song.getNumberOfProgressions(); i++) {
//                    fw.append("" +i + "\n");
//                    Progression p = song.getProgressions().get(i);
//                    fw.append(p.getTimesChordPlayed() +","+
//                            p.getMelodySpeed()+","+
//                            p.getDrumSpeed() +","+
//                            p.getHatSpeed()+","+
//                            p.getBassSpeed()+"," +
//                            p.getHasDrums()+","+
//                            p.getHasBass() +","+
//                            p.getArpeggio()+","+
//                            p.getIsPatternMelody()+","+
//                            p.getIsPatternHats());
//                    int[][][][] music = p.getProgression();
//                    for (int j = 0; j < song.getNumberOfChannels(); j++) {
//                        fw.append("" +j + "\n");
//                        for (int c = 0; c < p.getNotesInChords(); c++) {
//                            for (int bar = 0; bar < p.getNumberOfBars(); bar++) {
//                                for (int beat = 0; beat < p.getBeatsInBar(); beat++) {
//                                    fw.append(""+music[bar][beat][j][c] + ",");
//                                }
//                            }
//                            fw.append("\n");
//                        }
//                        fw.append("\n");
//                    }
//                }
//            }
//            fw.append("end\n");
//            fw.flush();
//            fw.close();
//        }
//        catch (IOException e){
//            e.printStackTrace();
//        }
//    }