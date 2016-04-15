package com.example.jonathanbriers.musicgenerator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.MediaController;

/**
 * Created by Jonny on 12/04/2016.
 */
public class AudioController extends MediaController {


    public AudioController(Context c) {
        super(c);
    }

    @Override
    public void hide() {

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
//            ((Activity) getContext()).finish();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            getContext().startActivity(intent);
        return super.dispatchKeyEvent(event);
    }
}
