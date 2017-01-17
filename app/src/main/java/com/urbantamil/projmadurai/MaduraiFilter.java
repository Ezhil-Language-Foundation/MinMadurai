/*
This code is released under MIT License
(C) 2016-2017, Ezhil Language Foundation
<ezhillang@gmail.com>
*/
package com.urbantamil.projmadurai;

import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by muthu on 3/13/2016.
 */
public class MaduraiFilter extends AbstractMaduraiFilter {
    public static final int FIELD_AUTHOR = 0;
    public static final int FIELD_TITLE = 1;
    public static final int FIELD_GENRE = 2;
    public static final int FIELD_FULLTEXT = 3;
    public static final int FIELD_BOOKMARK = 4;
    public static final int FIELD_RECENTBOOK = 5;
    public static final int FIELD_LASTREAD = 6;

    int filter_by_field;
    HashMap< String, ArrayList<MaduraiBook> > memoized;

    public MaduraiFilter(int field_name) throws Exception {
        if ( field_name < FIELD_AUTHOR || field_name >= FIELD_FULLTEXT ) {
            throw  new Exception("Cannot use field for filtering; field_name < FIELD_AUTHOR || field_name >= FIELD_FULLTEXT ");
        }
        this.filter_by_field = field_name;
        this.memoized = new HashMap< String, ArrayList<MaduraiBook> >();
    }

    public ArrayList<MaduraiBook> doSearch(String query,ArrayList<MaduraiBook> list_of_books) {
        if (this.memoized.containsKey(query))
            return this.memoized.get(query);
        ArrayList<MaduraiBook> result = super.doSearch(query,list_of_books);
        //cache it
        this.memoized.put(query,result);
        return result;
    }

    public String getField(MaduraiBook book)  {
        switch(  filter_by_field ) {
            case FIELD_AUTHOR:
                return book.getAuthor();
            case FIELD_GENRE:
                return book.getGenre();
            case FIELD_TITLE:
                return book.getTitle();
            default:
                assert(false);
        }
        return "";
    }

}
