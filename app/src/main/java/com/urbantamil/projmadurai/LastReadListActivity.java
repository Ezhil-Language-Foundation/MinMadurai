/*
This code is released under MIT License
(C) 2016-2017, Ezhil Language Foundation
<ezhillang@gmail.com>
*/
package com.urbantamil.projmadurai;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


/**
 * Created by muthu on 11/20/2016.
 * சமீபத்திய
 */
public class LastReadListActivity extends Activity {
    @Override
    public void onCreate(Bundle saved_instance) {
        super.onCreate(saved_instance);
        Intent intent = new Intent(getApplicationContext(), BookListActivity.class);
        try {
            intent.putExtra(BookListActivity.ARG_FILTER_BY, MaduraiFilter.FIELD_LASTREAD);
        } catch (Exception e) {
            //pass
        }
        startActivity(intent);
        finish();
    }
}
