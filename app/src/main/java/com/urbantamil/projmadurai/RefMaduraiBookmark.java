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
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/** Combines LRU and Bookmarking (i.e. not an infinite bookmark)
 * Created by muthu on 9/8/2016.
 */
abstract class RefMaduraiBookmark {
    public final static String TAG = "RefMaduraiBookmark";
    // LRU element is at the end of the list
    // head of this list is the most recently used
    private List<MaduraiBook> LRUCache = null;
    public String filename;
    private boolean m_keep_ordering;
    private Activity m_activity = null;

    // for pure bookmarks : m_keep_ordering = false
    public RefMaduraiBookmark(Activity activity,String ref_filename,boolean keep_ordering) {
        LRUCache = new LinkedList<MaduraiBook>();
        filename = ref_filename;
        //// DEBUG mode /////
        int count = 0;
        //prime with some books by "கல்கி"
        m_keep_ordering = keep_ordering;
        m_activity = activity;
    }

    public MaduraiBook getMostRecent() {
        ListIterator<MaduraiBook> itr = LRUCache.listIterator();
        if(!itr.hasNext()) {
            Log.d(TAG,"LRU Cache - is empty");
            return null;
        }
        return itr.next();
    }

    public void load() {
        SharedPreferences pref = m_activity.getSharedPreferences(filename, Context.MODE_PRIVATE);
        if ( pref == null)
            return;
        int n_keys = pref.getAll().size();
        int loaded = 0;
        for(int idx=0;idx < n_keys; idx++) {
            MaduraiBook book = null;
            try {
                //todo
                String bookJSON = pref.getString("book_" + String.valueOf(idx),"");
                if ( bookJSON.length() == 0 )
                    continue;
                LRUCache.add( MaduraiBook.LoadFromJSON(new JSONObject(bookJSON)) );
                loaded++;
            } catch (Exception ex) {
                //pass
            }
        }
        Log.d(TAG,"loaded -> "+String.valueOf(loaded));
        printBooks();
    }

    public void save( ) {
        SharedPreferences pref = m_activity.getSharedPreferences(filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        for(int idx=0;idx < LRUCache.size(); idx++) {
            MaduraiBook book = LRUCache.get(idx);
            try {
                edit.putString("book_" + String.valueOf(idx), book.SaveToJSON());
            } catch (Exception ex) {
                //pass
            }
        }

        edit.apply();
        edit.commit();
    }

    public MaduraiBook getBookAt(int position) {
        return LRUCache.get(position);
    }

    //size of elements
    public int size() {
        return LRUCache.size();
    }

    //display your contents
    private void printBooks() {
        ListIterator<MaduraiBook> itr = LRUCache.listIterator();
        int pos = 0;
        while(itr.hasNext()) {
            MaduraiBook refBook = itr.next();
            Log.d(TAG, String.valueOf(pos)+") reference book "+ refBook.getAuthor()+" / "+refBook.getTitle());
            pos++;
        }
    }

    //find position of the book in the list
    public int findBook(MaduraiBook book) {
        ListIterator<MaduraiBook> itr = LRUCache.listIterator();
        int pos = 0;
        int rval = -1;
        while(itr.hasNext()) {
            MaduraiBook refBook = itr.next();
            if (refBook.matches(book)) {
                return pos;
            }
            pos++;
        }
        return -1;
    }

    //erase the whole entire preferences containers
    public void erase( ) {
        //clear cache
        LRUCache.clear();
        //clear the reference prefs
        SharedPreferences pref = m_activity.getSharedPreferences(filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.clear();
        edit.commit();
        return;
    }

    //find position of the book in the list
    public void remove(int epos) {
        LRUCache.remove(epos);
    }

    public void add(MaduraiBook book) {
        int idx = this.findBook(book); //see if the book is in LRUCache and its index
        Log.d(TAG,"Matched book = "+String.valueOf(idx));
        // FIXME: Debug message to be removed in production
        Toast.makeText( m_activity.getApplicationContext(),
                "Matched book = "+book.toString()+" at position "+ String.valueOf(idx),Toast.LENGTH_LONG).show();
        // for pure bookmarks : m_keep_ordering = false
        if ( !m_keep_ordering && idx >= 0 )
            return;

        if ( idx >= 0 ) {
            //this item is no longer the least recently used.
            LRUCache.remove(idx);
        }

        //add at head of the list
        LRUCache.add(0,book);
        return;
    }
}
