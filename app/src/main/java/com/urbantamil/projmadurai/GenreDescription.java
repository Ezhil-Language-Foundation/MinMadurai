/*
This code is released under MIT License
(C) 2016-2017, Ezhil Language Foundation
<ezhillang@gmail.com>
*/
package com.urbantamil.projmadurai;

import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by muthu on 9/18/2016.
 */
public class GenreDescription {
    static HashMap<String,String> desc = new HashMap<String,String>();
    static {
        //FIXME: add more detailed descriptions :TODO
        desc.put("கவிதை","poems");
        desc.put("சிறுகதை","short stories");
        desc.put("நாவல்","novels");
        desc.put("இலக்கியம்","literature");
    }

    public static String get(String key) {
        String rval = desc.get(key.trim());
        if ( rval != null)
            return rval;
        return desc.get("இலக்கியம்");
    }
}
