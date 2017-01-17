/*
This code is released under MIT License
(C) 2016-2017, Ezhil Language Foundation
<ezhillang@gmail.com>
*/
package com.urbantamil.projmadurai;

/**
 * Created by muthu on 9/18/2016.
 */

import android.app.Activity;

public class MaduraiRecentBook extends RefMaduraiBookmark {
    public static String TAG = "MaduraiRecentBook";
    // singleton
    private static boolean RECENTBOOKS_ORDERING = true;
    private static MaduraiRecentBook manager = null;

    private MaduraiRecentBook(Activity activity) {
        super(activity,TAG,RECENTBOOKS_ORDERING);
    }

    public static MaduraiRecentBook getManager(Activity activity) {
        if ( manager == null ) {
            manager = new MaduraiRecentBook(activity);
        }
        return manager;
    }
}
