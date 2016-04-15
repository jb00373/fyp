package com.example.jonathanbriers.musicgenerator;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Jonny on 13/04/2016.
 */
public class LoadActivity extends Activity {

    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_load);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width * .8), (int)(height * .8));
        Bundle b = getIntent().getExtras();
        ArrayList<String> savedWords = b.getStringArrayList("List");
        //Removes null item at the end. Why does it exist though??
        String s = savedWords.get(savedWords.size() - 1);
        if (s == null){
            savedWords.remove(savedWords.size() - 1);
        }
        lv = (ListView)findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                savedWords
        );
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = lv.getItemAtPosition(position).toString();
                Bundle b = new Bundle();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                b.putString("Title", s);
                i.putExtras(b);
                setResult(RESULT_OK, i);
                finish();
            }


        });
    }

}
