/*
This code is released under MIT License
(C) 2016-2017, Ezhil Language Foundation
<ezhillang@gmail.com>
*/
package com.urbantamil.projmadurai;

import android.os.Bundle;
import android.os.Handler;
import android.util.JsonReader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONObject;

/**
 * An activity representing a single Book detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link BookListActivity}.
 */
public class BookDetailActivity extends AppCompatActivity {
    BookDetailFragment fragment = null;
    boolean doubleBackToExitPressedOnce = false;
    final String TAG = "BookDetailActivity";

    //volume keys mapped to font size of fragment
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (fragment!=null) {
            if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
                fragment.fontSizeMinus();
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                fragment.fontSizePlus();
                return true;
            }
        }
        //pass to parent activity
        return super.onKeyDown(keyCode,event);
    }
    
    ////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            String jsonStr = getIntent().getStringExtra(BookDetailFragment.ARG_ITEM_JSON);
            try {
                MaduraiBook mItem = MaduraiBook.LoadFromJSON(new JSONObject(jsonStr));
                setTitle(mItem.getTitle());
            } catch (Exception e) {
                //pass
            }

            Bundle arguments = new Bundle();
            // fwd both the arguments bundle into the fragment bundle
            arguments.putString(BookDetailFragment.ARG_ITEM_JSON,
                    jsonStr);
            arguments.putBoolean(BookDetailFragment.ARG_ITEM_OPEN,
                    getIntent().getBooleanExtra(BookDetailFragment.ARG_ITEM_OPEN,false));
            fragment = new BookDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.book_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if ( id == R.id.action_font_increase) {
            if ( fragment != null ) fragment.fontSizePlus();
            return true;
        }
        
        if ( id == R.id.action_font_decrease ) {
            if ( fragment != null ) fragment.fontSizeMinus();
            return true;
        }

        if ( id == R.id.action_bookmark_toggled ) {
             if ( fragment != null ) fragment.toggleBookmark();
            return true;
         }

        return super.onOptionsItemSelected(item);
    }
    
    /// options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_book_detail, menu);
        Log.d(TAG,"create options menu");
        return super.onCreateOptionsMenu(menu);
    }

    /// double tap to exit
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.MessageDoubleTapToExit, Toast.LENGTH_SHORT).show();

        // clear the flag if no action happens between now and 2s later
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}
