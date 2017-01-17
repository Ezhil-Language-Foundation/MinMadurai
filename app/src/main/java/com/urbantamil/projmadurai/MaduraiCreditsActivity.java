/*
This code is released under MIT License
(C) 2016-2017, Ezhil Language Foundation
<ezhillang@gmail.com>
*/
package com.urbantamil.projmadurai;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.urbantamil.chithirai.TypefaceUtil;

import static java.security.AccessController.getContext;

// simply display the credits page
public class MaduraiCreditsActivity extends AppCompatActivity implements Button.OnClickListener,  View.OnTouchListener {
    private static final String TAG = "MaduraiCreditActivity";
    Button btn= null;
    TextView txt=null;
    RatingBar m_rating = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_madurai_credits);
        btn = (Button) findViewById(R.id.projmad_credits_close);
        btn.setOnClickListener(this);
        txt = (TextView) findViewById(R.id.projmad_credits_details);
        Typeface latha = TypefaceUtil.getTypefaceByName(new MaduraiPreferences(this).getFontname(),
                getApplicationContext());
        txt.setTypeface(latha, Typeface.ITALIC);
        m_rating = (RatingBar) findViewById(R.id.projmad_credits_rating);
        m_rating.setOnTouchListener(this);
    }

    // ratings bar
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.d(TAG,"Ratings activity completed; take rating "+ m_rating.getRating() +" and send it to google");
            performAppRatingActivity();
            return true;
        }
        return v.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        //Log.d(TAG, "handling click from view" + v.toString());
        Toast.makeText(getApplicationContext(),R.string.projmad_credits_close,Toast.LENGTH_SHORT).show();
        finish();
    }

    private void performAppRatingActivity() {
        //Ref: SO:Q10816757
        final Context context = getApplicationContext();
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }
}
