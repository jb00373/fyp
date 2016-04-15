package com.example.jonathanbriers.musicgenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Jonny on 12/03/2016.
 */
public class TextFileReader {

    String title;
    File file;
    BufferedReader reader;
    ArrayList words = new ArrayList<String>();

    public String getTitle(int random) {
        return words.get(random).toString();
    }

    public TextFileReader(BufferedReader br) {
        this.reader = br;
    }

    public void readWordlist() {
        try {
            // do reading, usually loop until melodyEnd of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                words.add(mLine);
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
    }
}
