package com.urbantamil.chithirai;

/**
 * Created by muthu on 9/21/2016.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

/**
 * Code from : https://gist.github.com/ialmetwally/fc57f7766ee6ab07ff52
 * Created by muthu on 9/21/2016.
 */
public class TypefaceUtil {
    public static final int EZHIL = 0;
    public static final int LATHA = 1;
    public static final int JEEVA = 2;
    public static final int AMMA1 = 3;
    public static final int AMMA2 = 4;

    /**
     * Using reflection to override default typefaces
     * NOTICE: DO NOT FORGET TO SET TYPEFACE FOR APP THEME AS DEFAULT TYPEFACE WHICH WILL BE
     * OVERRIDDEN
     *
     * @param typefaces map of fonts to replace
     */
    public static void overrideFonts(Map<String, Typeface> typefaces) {
        try {
            final Field field = Typeface.class.getDeclaredField("sSystemFontMap");
            field.setAccessible(true);
            Map<String, Typeface> oldFonts = (Map<String, Typeface>) field.get(null);
            if (oldFonts != null) {
                oldFonts.putAll(typefaces);
            } else {
                oldFonts = typefaces;
            }
            field.set(null, oldFonts);
            field.setAccessible(false);
        } catch (Exception e) {
            Log.e("TypefaceUtil", "Can not set custom fonts");
        }
    }

    private static ArrayList<String> getFontChoices() {
        ArrayList<String> font_choices = new ArrayList<String>();
        font_choices.add("Ezhil_lss.ttf");
        font_choices.add("latha.ttf");
        font_choices.add("Jeeva_lss.ttf");
        //Amma : 09 - 26
        for (int i = 9; i <= 26; i++) {
            java.util.Formatter f = new java.util.Formatter();
            font_choices.add(f.format("uniAmma-%02d.ttf", i).toString());
        }
        //sundaram : 1-10
        for (int i = 1; i <= 10; i++) {
            java.util.Formatter f = new java.util.Formatter();
            font_choices.add(f.format("Uni Ila.Sundaram-%02d.ttf", i).toString());
        }

        return font_choices;
    }


        public static Typeface getTypefaceByName(String fontname, Context context) {
            return Typeface.createFromAsset(context.getAssets(), "fonts/" + fontname);
        }

        public static Typeface getTypeface(int fontType, Context context) {
        ArrayList<String> font_choices = getFontChoices();
        // here you can load the Typeface from asset or use default ones
        switch (fontType) {
            case EZHIL:
                return Typeface.createFromAsset(context.getAssets(),"fonts/"+font_choices.get(0));
            case LATHA:
                return Typeface.createFromAsset(context.getAssets(), "fonts/" + font_choices.get(1));
            case JEEVA:
                return Typeface.createFromAsset(context.getAssets(),"fonts/"+font_choices.get(2));
            case AMMA1:
                return Typeface.createFromAsset(context.getAssets(), "fonts/" + font_choices.get(3));
            default:
                return Typeface.createFromAsset(context.getAssets(), "fonts/" + font_choices.get(4));
        }
    }
}
