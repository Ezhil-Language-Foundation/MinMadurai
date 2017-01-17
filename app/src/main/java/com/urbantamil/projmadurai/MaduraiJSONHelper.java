/*
This code is released under MIT License
(C) 2016-2017, Ezhil Language Foundation
<ezhillang@gmail.com>
*/
package com.urbantamil.projmadurai;

import android.content.res.Resources;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by muthu on 3/13/2016.
 */
public class MaduraiJSONHelper {
    static final String TAG="JSONHelper";


    /** Usage
     ArrayList<String> list = jsonHelper(R.raw.v01);
     Crouton.showText(this, "Loaded - items = " + list.size(), Style.INFO);
     StableArrayAdapter adapter = new StableArrayAdapter(this,
     android.R.layout.simple_list_item_1, list);
     listView.setAdapter(adapter);
     */

    static JSONArray loadArrayOfObjects(InputStream is) {
        Writer writer = fileToString(is);
        // Convert JSON String to Array of JSON Objects.
        JSONArray obj = null;
        try {
            String jsonString = writer.toString();
            obj = new JSONArray(jsonString);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        return obj;
    }
    static JSONObject loadJSONObjects(InputStream is) {
        Writer writer = fileToString(is);
        // Convert JSON String to a JSON Object.
        JSONObject obj = null;
        try {
            String jsonString = writer.toString();
            obj = new JSONObject(jsonString);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        return obj;
    }

    public static Writer fileToString(InputStream is) {
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                //pass
            }
            ;
        }
        return writer;
    }

}
