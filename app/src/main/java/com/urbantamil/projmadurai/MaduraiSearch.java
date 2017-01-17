/*
This code is released under MIT License
(C) 2016-2017, Ezhil Language Foundation
<ezhillang@gmail.com>
*/
package com.urbantamil.projmadurai;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class MaduraiSearch extends AppCompatActivity {
    public static final String ARG_ITEM_AUTHOR = "bundle_author";
    public static final String ARG_ITEM_TITLE =  "bundle_title";
    public static final String ARG_ITEM_GENRE =  "bundle_genre";

    private MaduraiLibrary maduraiLibrary = null;
    private String TAG = MaduraiSearch.class.getName();
    ArrayAdapter<String> authorAdapter = null;
    ArrayAdapter<String> titleAdapter = null;
    ArrayAdapter<String> genreAdapter = null;
    MaduraiPreferences m_prefs = null;
    ListView searchResults = null;
    AutoCompleteTextView searchEntryTextView = null;
    Spinner spinner_search_options;
    //RadioGroup radio_search_grp;
    //RadioButton authorRadio, titleRadio, genreRadio;
    Button btn_madurai_search;

    MaduraiLibrary getMaduraiLibrary() {
        return maduraiLibrary;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_prefs.save();
    }

    @Override
    protected void onPause() {
        super.onPause();
        m_prefs.save();
    }

    void setSearchOption(String field) {
        int pos = 3;
        if (field.contentEquals("title")) {
            pos = 1;
        } else if (field.contentEquals("author")) {
            pos = 0;
        } else if (field.contentEquals("genre")) {
            pos = 2;
        } else  {
            pos = 3;
        }
        spinner_search_options.setSelection(pos);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_madurai_search);
        m_prefs = new MaduraiPreferences(this);

        //hide soft kbd
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        try {
            maduraiLibrary = MaduraiBook.loadFromAsset(getResources(), R.raw.projmad);
            Log.d(TAG,"loaded Madurai Book library");
            m_prefs.setFontname(getAssets().list("fonts/")[0]);
            Log.d(TAG,"Setting font as "+getAssets().list("fonts/")[0]);
        } catch ( Exception e) {
            Log.d(TAG, e.toString());
        }

        /// get data models ready
        authorAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,maduraiLibrary.getAuthorsList());
        genreAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,maduraiLibrary.getGenresList());
        titleAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,maduraiLibrary.getTitlesList());

        btn_madurai_search = (Button) findViewById(R.id.btn_madurai_search);
        /// result hooks - contains titles associated with the author
        searchResults = (ListView) findViewById(R.id.listview_search_results);
        spinner_search_options =(Spinner) findViewById(R.id.projmad_search_spinner_options);
        spinner_search_options.setSelection(0);//force something to be selected at init

        //radio_search_grp = (RadioGroup) findViewById(R.id.radio_search_group);

        /// start with author search
        //titleRadio = (RadioButton) findViewById(R.id.radio_search_title);
        //genreRadio = (RadioButton) findViewById(R.id.radio_search_genre);
        //authorRadio = (RadioButton) findViewById(R.id.radio_search_author);

        /// get GUI element hooks - autocomplete search
        searchEntryTextView = (AutoCompleteTextView) findViewById(R.id.autocomplete_search_textview);

        Bundle bundle = getIntent().getExtras();
        if ( bundle != null ) {
            String searchval = "";
            if ( bundle.getString(ARG_ITEM_TITLE) != null ) {
                searchval = bundle.getString(ARG_ITEM_TITLE);
                searchEntryTextView.setAdapter(titleAdapter);
                setSearchOption("title");
            } else if ( bundle.getString(ARG_ITEM_GENRE) != null  ) {
                searchval = bundle.getString(ARG_ITEM_GENRE);
                searchEntryTextView.setAdapter(genreAdapter);
                setSearchOption("genre");
            } else {
                //default to author
                searchval = bundle.getString(ARG_ITEM_AUTHOR);
                searchEntryTextView.setAdapter(authorAdapter);
                setSearchOption("author");
            }
            final String searchq = searchval == null ? "" : searchval;

            searchEntryTextView.setText(searchq);
            Log.d(TAG,"using the searchval => "+searchval);
            //execute the search
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    searchEntryTextView.performClick();
                    if ( btn_madurai_search != null)
                        btn_madurai_search.performClick();
                    Log.d(TAG,"performing click for search value ("+searchq+")");
                }
            },250);
        } else {
            Log.d(TAG,"Setting default adapter -> author");
            spinner_search_options.setSelection(0);//Author selection: (A-T-G-F : order of options on spinner)
            /// by default do the search on author list.
            setSearchOption("author");
            searchEntryTextView.setAdapter(authorAdapter);
        }

        //force a 7:1 split of available width.
        int root_width = searchEntryTextView.getRootView().getWidth();
        int p75 = 7*root_width>>3;
        searchEntryTextView.setWidth(p75);
        btn_madurai_search.setWidth(root_width-p75);

        searchEntryTextView.invalidate();
        btn_madurai_search.invalidate();
        searchEntryTextView.getRootView().invalidate();

        LinearLayout projmad_search_linearlayout = (LinearLayout) findViewById(R.id.linearlayout_search_projmad);

        //not right now!
        //projmad_search_linearlayout
        /**
            FragmentManager fm = getSupportFragmentManager();
            Fragment newTamilInfoFragment = TamilInfoFragment.newInstance(getMaduraiLibrary().getAuthorsList(),
                    getMaduraiLibrary().getGenresList());
            fm.beginTransaction().add(R.id.linearlayout_search_projmad,newTamilInfoFragment).commit();
         */

        btn_madurai_search.setOnClickListener(new View.OnClickListener() {
            ArrayList<MaduraiBook> get_partial_matches(String kind, String attribute_value) {
                ArrayList<MaduraiBook> books = new ArrayList<MaduraiBook>();
                ArrayList<String> partial_matches = new ArrayList<String>();
                Iterator<String> category_values = null;
                MaduraiLibrary lib = getMaduraiLibrary();
                if (kind.equals("AUTHOR")) {
                    category_values = lib.getAuthors();
                } else if (kind.equals("GENRE")) {
                    category_values = lib.getGenres();
                } else { //TITLE
                    category_values = lib.getTitlesList().iterator();
                }

                if (category_values == null) {
                    return books;
                }

                while (category_values.hasNext()) {
                    String curr = category_values.next();
                    if (curr.contains(attribute_value))
                        partial_matches.add(curr);
                }

                if (kind.equals("AUTHOR")) {
                    for (int itr = 0; itr < partial_matches.size(); itr++) {
                        books.addAll(lib.getBooksForAuthor(partial_matches.get(itr)));
                    }
                } else if (kind.equals("GENRE")) {
                    for (int itr = 0; itr < partial_matches.size(); itr++) {
                        books.addAll(lib.getBooksForGenre(partial_matches.get(itr)));
                    }
                } else { //TITLE
                    for (int itr = 0; itr < partial_matches.size(); itr++) {
                        books.add(lib.getBookForTitle(partial_matches.get(itr)));
                    }
                }
                return books;
            }

            String getResultSummary(int n_results) {
                StringBuilder sb = new StringBuilder();
                if (n_results < 1) {
                    sb.append( getResources().getString(R.string.projmad_search_empty) );
                } else {
                    // search found %d items
                    sb.append("பொருத்தமான தேடல் விடைகள் "+n_results+" கிடைத்தன.");
                }

                String val = sb.toString();
                Toast.makeText(getApplicationContext(),val,Toast.LENGTH_SHORT).show();
                return val;
            }

            @Override
            public void onClick(View v) {
                MaduraiLibrary lib = getMaduraiLibrary();
                MaduraiBookComparator book_compare_obj = null;

                Log.d(TAG, "Library Info");
                Log.d(TAG, lib.toString());

                //get the matched item [author name, genre name, title name etc]
                String attribute_value = searchEntryTextView.getText().toString();
                Log.d(TAG, "Attribute value =>" + attribute_value);

                //populate the books matching the attribute_value
                ArrayAdapter<String> book_adapter = null;
                ArrayList<MaduraiBook> books;

                if (attribute_value.length() == 0) {
                    Log.d(TAG, "empty attribute");
                }

                switch ( spinner_search_options.getSelectedItemPosition() ) {
                    case 0:
                        Log.d(TAG, "SEARCH AUTHOR");
                        books = get_partial_matches("AUTHOR", attribute_value);
                        book_compare_obj = new MaduraiBookComparator(MaduraiBookComparator.AUTHOR);
                        break;
                    case 1:
                        Log.d(TAG, "SEARCH TITLE");
                        books = get_partial_matches("TITLE", attribute_value);
                        book_compare_obj = new MaduraiBookComparator(MaduraiBookComparator.TITLE);
                        break;
                    case 2:
                        Log.d(TAG, "SEARCH GENRE");
                        books = get_partial_matches("GENRE", attribute_value);
                        book_compare_obj = new MaduraiBookComparator(MaduraiBookComparator.GENRE);
                        break;
                    case 3:
                    default:
                        book_adapter = new ArrayAdapter<String>(searchResults.getContext(),
                                android.R.layout.simple_list_item_1, new String[]{""});
                        searchResults.setAdapter(book_adapter);
                        Log.d(TAG, "full text search not implemented");
                        return;
                }

                //display books sorted by the same style (up or down also needs to be specified)
                if ( book_compare_obj == null )
                    book_compare_obj = new MaduraiBookComparator(MaduraiBookComparator.TITLE);

                if (books == null || books.size() < 1) {
                    Log.d(TAG, "No matches found! books => null");
                }

                TextView result_summary = (TextView) findViewById(R.id.textview_search_results_summary);

                int n_matches = books.size();
                Log.d(TAG, "Matches for (" + attribute_value + ") => " + n_matches);

                result_summary.setText(getResultSummary(n_matches));

                MaduraiBookAdapter cplx_book_adapter =
                        new MaduraiBookAdapter(searchResults.getContext(), R.layout.madurai_book_adapter, books);

                cplx_book_adapter.sort( book_compare_obj );

                searchResults.setAdapter(cplx_book_adapter);
                searchResults.invalidate();
                searchResults.setVisibility(View.VISIBLE);
            }
          });

        /// let the spinner button decide the filters
        spinner_search_options.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                {
                    String msg = null;
                    switch (position) {
                        case 0:
                            msg = "author";
                            //author
                            searchEntryTextView.setAdapter(authorAdapter);
                            break;
                        case 1:
                            //title
                            msg = "title";
                            searchEntryTextView.setAdapter(titleAdapter);
                            break;
                        case 2:
                            //genre
                            msg ="genre";
                            searchEntryTextView.setAdapter(genreAdapter);
                            break;
                        default:
                            //full text
                            msg = "Fulltext search not implemented";
                            Log.d(TAG, "Full text search not implemented!");
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            //searchEntryTextView.setAdapter(titleAdapter);
                            break;
                    }
                    searchEntryTextView.invalidate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
