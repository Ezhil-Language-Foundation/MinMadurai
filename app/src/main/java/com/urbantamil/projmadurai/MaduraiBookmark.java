/*
This code is released under MIT License
(C) 2016-2017, Ezhil Language Foundation
<ezhillang@gmail.com>
*/
package com.urbantamil.projmadurai;

import android.app.Activity;

public class MaduraiBookmark extends RefMaduraiBookmark {
    public static String TAG = "MaduraiBookmark";
    // singleton
    private static MaduraiBookmark manager = null;
    private static boolean BOOKMARK_ORDERING = false;

    private MaduraiBookmark(Activity activity) {
        super(activity,TAG,BOOKMARK_ORDERING);
    }

    public static MaduraiBookmark getManager(Activity activity) {
        if ( manager == null ) {
            manager = new MaduraiBookmark(activity);
        }
        return manager;
    }
}
