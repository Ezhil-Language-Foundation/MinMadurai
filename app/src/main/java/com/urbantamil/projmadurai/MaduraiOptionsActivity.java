/*
This code is released under MIT License
(C) 2016-2017, Ezhil Language Foundation
<ezhillang@gmail.com>
*/
package com.urbantamil.projmadurai;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

enum MaduraiFontSetting {
    FONT_SMALL,
    FONT_MEDIUM,
    FONT_LARGE
}

// activity
public class MaduraiOptionsActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    MaduraiPreferences m_=null;
    private final static String TAG = "ProjectMaduraiOptionsActivity";
    private RadioGroup rad_font_setting = null;
    private RadioGroup btn_row_alternate = null;
    private TextView textView = null;
    private Spinner font_chooser = null;
    private List<String> font_choices = null;

    private Button btn_bookmark_cleared = null;
    private Button btn_recentbooks_cleared = null;
    private Button btn_reset_options = null;

    void updateFontName(String fontname) {
        Typeface typeface = Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontname);
        textView.setTypeface(typeface);
        textView.setTextSize( m_.getFontSettingSize() );
        textView.invalidate();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_ = new MaduraiPreferences(this); //loads the prefs
        this.setContentView(R.layout.activity_madurai_options);
        // associate widgets
        rad_font_setting = (RadioGroup) findViewById(R.id.options_font_settings);
        rad_font_setting.setOnCheckedChangeListener(this);

        btn_row_alternate = (RadioGroup) findViewById(R.id.options_row_alternation);
        btn_row_alternate.setOnCheckedChangeListener(this);

        btn_bookmark_cleared = (Button) findViewById(R.id.options_bookmark_clear_saved);
        btn_recentbooks_cleared = (Button) findViewById(R.id.options_recentbooks_clear_saved);
        btn_bookmark_cleared.setOnClickListener(this);
        btn_recentbooks_cleared.setOnClickListener(this);

        btn_reset_options = (Button) findViewById(R.id.options_reset_to_default);
        btn_reset_options.setOnClickListener(this);

        textView = (TextView) findViewById(R.id.textView);
        textView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        textView.setText(R.string.tamilfont_cinderella_story);

        font_chooser = (Spinner) findViewById(R.id.spinner);
        font_choices = new ArrayList<String>();
        final String [] fonts = {"customFontOrLatha.ttf","Ezhil_lss.ttf","Jeeva_lss.ttf","ArimaMadurai-Regular.ttf",
                "uniAmma-10.ttf","ArimaKoshi-Regular.ttf","Catamaran-Regular.ttf"};
        for(String font : fonts) {
            font_choices.add(font);
        }
        for (int i = 1; i <= 10; i++) {
            java.util.Formatter f = new java.util.Formatter();
            font_choices.add(f.format("Uni Ila.Sundaram-%02d.ttf", i).toString());
        }

        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this
                , android.R.layout.simple_spinner_item,font_choices );
        dataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        font_chooser.setAdapter(dataAdapter);
        font_chooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String fontname = dataAdapter.getItem(position);
                Log.d(TAG,"set font =>"+fontname);
                m_.setFontname(fontname);
                updateFontName(fontname);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //pass
            }
        });

        // update prefs
        update();
    }

    // update the widgets
    private void update() {

        RadioButton btn_row_alternate = (RadioButton) findViewById(R.id.options_row_alternation_btn);
        btn_row_alternate.setChecked( m_.getRowAlternate() );
        RadioButton btn_row_similar = (RadioButton) findViewById(R.id.options_row_similar_btn);
        btn_row_similar.setChecked( !m_.getRowAlternate() );
        btn_row_alternate.invalidate();
        btn_row_similar.invalidate();

        // font size setting
        int ref_font_setting = R.id.options_font_medium;
        switch ( m_.getFontSetting() ) {
            case FONT_LARGE:
                ref_font_setting = R.id.options_font_large;
                break;
            case FONT_MEDIUM:
                ref_font_setting = R.id.options_font_medium;
                break;
            case FONT_SMALL:
                ref_font_setting = R.id.options_font_small;
                break;
        }
        rad_font_setting.check(ref_font_setting);
        rad_font_setting.invalidate();

        //font setting widget
        String fontname = m_.getFontname();
        ListIterator<String> itr = font_choices.listIterator();
        int pos = 0;
        while( itr.hasNext() ) {
            if ( itr.next().equalsIgnoreCase(fontname) ) {
                font_chooser.setSelection(pos);
                break;
            }
            pos++;
        }
        font_chooser.invalidate();
        updateFontName(fontname);
    }

    @Override
    public void onPause() {
        m_.save();
        super.onPause();
    }

    void resetPreferences() {
        m_.reset();
        update();
    }

    @Override
    public void onClick(View v) {
        Button src_btn = (Button) v;
        switch ( v.getId() ) {
            case R.id.options_bookmark_clear_saved:
                break;
            case R.id.options_recentbooks_clear_saved:
                break;
            case R.id.options_reset_to_default:
                resetPreferences();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        Log.d(TAG,"onCheckedChanged => "+group.toString());
        switch ( group.getId()  ) {
            case R.id.options_row_alternation:
                RadioButton btn_row_alternate = (RadioButton) findViewById(R.id.options_row_alternation_btn);
                m_.setRowAlternate( btn_row_alternate.isChecked() );
                break;
            case R.id.options_font_settings:
                if ( checkedId == R.id.options_font_small) {
                    m_.setFontSetting(MaduraiFontSetting.FONT_SMALL);
                } else if ( checkedId == R.id.options_font_medium) {
                    m_.setFontSetting(MaduraiFontSetting.FONT_MEDIUM);
                } else if ( checkedId == R.id.options_font_large ) {
                    m_.setFontSetting(MaduraiFontSetting.FONT_LARGE);
                }
                break;
        }
        update();
    }
}
