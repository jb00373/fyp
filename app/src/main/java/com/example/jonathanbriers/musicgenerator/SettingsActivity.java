package com.example.jonathanbriers.musicgenerator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by Jonny on 22/04/2016.
 */
public class SettingsActivity extends Activity {

    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_settings);
        lv = (ListView)findViewById(R.id.listView);
        ArrayList<String> items = new ArrayList<>();
        items.add(new String("Delete user preferences"));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                items
        );
//        Gson gson = new Gson();
//        Bundle b = getIntent().getExtras();
//        String json = b.getString("applicationContext");
//        final Context applicationContext = gson.fromJson(json, Context.class);

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = lv.getItemAtPosition(position).toString();
                if (s.equals("Delete user preferences")) {
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    sp.edit().clear().commit();
                }
            }


        });



    }

}
