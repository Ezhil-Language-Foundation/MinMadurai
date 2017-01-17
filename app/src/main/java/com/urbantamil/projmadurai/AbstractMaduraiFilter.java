/*
This code is released under MIT License
(C) 2016-2017, Ezhil Language Foundation
<ezhillang@gmail.com>
*/
package com.urbantamil.projmadurai;

import java.util.ArrayList;

/**
 * Created by muthu on 9/7/2016.
 */
public abstract class AbstractMaduraiFilter {
    public abstract String getField(MaduraiBook book);
    public ArrayList<MaduraiBook> doSearch(String query,ArrayList<MaduraiBook> list_of_books) {
        ArrayList<MaduraiBook> matches = new ArrayList<MaduraiBook>();
        for(MaduraiBook book : list_of_books  ) {
            if (this.getField(book).contains(query)) {
                matches.add(book);
            }
        }
        return matches;
    }
}
