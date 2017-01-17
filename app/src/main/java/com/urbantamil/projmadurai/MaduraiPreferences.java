/*
This code is released under MIT License
(C) 2016-2017, Ezhil Language Foundation
<ezhillang@gmail.com>
*/
package com.urbantamil.projmadurai;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by muthu on 11/27/2016.
 */

// data structure
public class MaduraiPreferences {
    final static String PREF_FILE_NAME = "MaduraiOptionsActivity";
    MaduraiFontSetting m_font_setting;
    boolean m_row_alternate;
    String m_fontname = "customFontOrLatha.ttf";

    final Activity m_ref;
    final private MaduraiFontSetting default_font_setting = MaduraiFontSetting.FONT_MEDIUM;

    public MaduraiPreferences(Activity ref) {
        m_ref = ref;
        SharedPreferences pref = ref.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        String font_setting = pref.getString("FontSetting","FONT_SMALL");
        if ( font_setting.equalsIgnoreCase("FONT_SMALL"))
            m_font_setting = MaduraiFontSetting.FONT_SMALL;
        else if ( font_setting.equalsIgnoreCase("FONT_LARGE"))
            m_font_setting = MaduraiFontSetting.FONT_LARGE;
        else if ( font_setting.equalsIgnoreCase("FONT_MEDIUM"))
            m_font_setting = MaduraiFontSetting.FONT_MEDIUM;

        m_row_alternate = pref.getBoolean("RowAlternate",true);
        m_fontname = pref.getString("Fontname","customFontOrLatha.ttf");
    }

    public void save() {
        SharedPreferences pref = m_ref.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("FontSetting",m_font_setting.toString());
        edit.putBoolean("RowAlternate",m_row_alternate);
        edit.putString("Fontname",m_fontname);
        edit.apply();
        edit.commit();
    }

    public String getFontname() {
        return m_fontname;
    }

    void putFontname(String f) {
        m_fontname = f;
    }

    public boolean getRowAlternate() {
        return m_row_alternate;
    }

    public MaduraiFontSetting getFontSetting() {
        return m_font_setting;
    }

    public void setFontSetting(MaduraiFontSetting fontSetting) {
        this.m_font_setting = fontSetting;
    }

    public void setRowAlternate(boolean rowAlternate) {
        this.m_row_alternate = rowAlternate;
    }

    public void reset() {
        m_row_alternate = true;
        m_font_setting = MaduraiFontSetting.FONT_MEDIUM;
        m_fontname = "customFontOrLatha.ttf";
        save();
    }

    public void setFontname(String fontname) {
        m_fontname = fontname;
    }

    public float getFontSettingSize() {
        switch ( getFontSetting() ) {
            case FONT_LARGE:
                return (14.0f);
            case FONT_MEDIUM:
                return (12.0f);
            case FONT_SMALL:
                return (10.0f);
        }
        return 10.0f;
    }
}