/*
This code is released under MIT License
(C) 2016-2017, Ezhil Language Foundation
<ezhillang@gmail.com>
*/
package com.urbantamil.projmadurai;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import com.tamil.*;

/** Parameters required:
 * Title
 * TopText
 * Author
 * AnimalCode
 * ColorCode
 * GuideText
 * GuideTextPlacement
 */

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MaduraiBookCoverFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MaduraiBookCoverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MaduraiBookCoverFragment extends Fragment  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TITLE = "TITLE";
    private static final String ARG_TOPTEXT = "TOPTEXT";
    private static final String ARG_ANIMALCODE = "ANIMAL_CODE";
    private static final String ARG_COLORCODE = "COLOR_CODE";
    private static final String ARG_GUIDE_TEXT = "GUIDE_TEXT";
    public static final String ARG_BOOK_JSON = "JSON";
    private static final String TAG = "MaduraiBookCoverFrag";
    private MaduraiBook mBook;
    private static ArrayList<String> genre_list = null;

    private static final int animal_ids [] = new int [] {
            R.drawable.img_1,
            R.drawable.img_2,
            R.drawable.img_3,
            R.drawable.img_4,
            R.drawable.img_5,
            R.drawable.img_6,
            R.drawable.img_7,
            R.drawable.img_8,
            R.drawable.img_9,
            R.drawable.img_10,
            R.drawable.img_11,
            R.drawable.img_12,
            R.drawable.img_13,
            R.drawable.img_14,
            R.drawable.img_15,
            R.drawable.img_16,
            R.drawable.img_17,
            R.drawable.img_18,
            R.drawable.img_19,
            R.drawable.img_20,
            R.drawable.img_21,
            R.drawable.img_22,
            R.drawable.img_23,
            R.drawable.img_24,
            R.drawable.img_25,
            R.drawable.img_26,
            R.drawable.img_27,
            R.drawable.img_28,
            R.drawable.img_29,
            R.drawable.img_30,
            R.drawable.img_31,
            R.drawable.img_32,
            R.drawable.img_33,
            R.drawable.img_34,
            R.drawable.img_35,
            R.drawable.img_36,
            R.drawable.img_37,
            R.drawable.img_38,
            R.drawable.img_39,
            R.drawable.img_40
    };

    public static final short colors_rgba[][] = new short[][] {
            {85,19,93,255},
            {113,112,110,255},
            {128,27,42,255},
            {184,7,33,255},
            {101,22,28,255},
            {80,61,189,255},
            {225,17,5,255},
            {6,123,176,255},
            {247,181,0,255},
            {0,15,118,255},
            {168,0,155,255},
            {0,132,69,255},
            {0,153,157,255},
            {1,66,132,255},
            {177,0,52,255},
            {55,142,25,255},
            {133,152,0,255}
    };

    private static final int MAX_ANIMALS = 40;
    private static final int MAX_COLORS = colors_rgba.length;

    private String mTitle;
    private String mTopText;
    private String mAuthor;
    private String mAnimalCode;
    private String mColorCode;
    private String mGuideText;

    private OnFragmentInteractionListener mListener;

    public MaduraiBookCoverFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static MaduraiBookCoverFragment newInstance(MaduraiBook book) {
        MaduraiBookCoverFragment fragment = new MaduraiBookCoverFragment();
        Bundle args = new Bundle();

        try {
            args.putString(ARG_BOOK_JSON, book.SaveToJSON());
        } catch (Exception e) {
            //pass
        }

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ( genre_list == null ) {
            MaduraiLibrary mlib = MaduraiBook.loadFromAssetDefault();
            genre_list = mlib.getGenresList();
            TamilLetters.sort(genre_list);
        }

        if (getArguments() != null) {
            try {
                mBook = MaduraiBook.LoadFromJSON(new JSONObject(getArguments().getString(ARG_BOOK_JSON)));
            } catch (Exception e) {
                Log.d(TAG,"Cannot get JSON object!");
            }
        } else {
            mBook = new MaduraiBook("title","author","genre",null,null);
        }
        mTitle = mBook.getTitle();
        mTopText = getResources().getString(R.string.projmad_bookcover_toptext);
        mAuthor = mBook.getAuthor();
        mAnimalCode = getGenreAnimalCode(mBook.getGenre());
        mGuideText = mBook.getGenre();
        mColorCode = getGenreColorCode(mBook.getGenre());
    }

    private String getGenreColorCode(String genre) {
        int pos = Collections.binarySearch(genre_list,genre,utf8.comparator);
        pos = Math.max(pos,0);
        // 0-based
        return String.valueOf(pos%MAX_COLORS);
    }

    private String getGenreAnimalCode(String genre) {
        int pos = Collections.binarySearch(genre_list,genre,utf8.comparator);
        pos = Math.max(pos,0);
        // 0-based
        return String.valueOf(pos%MAX_ANIMALS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootV = inflater.inflate(R.layout.fragment_madurai_book_cover, container, false);
        View floatbar = (View) rootV.findViewById(R.id.projmad_bookcover_floatbar_top);
        short [] rgba = colors_rgba[Integer.valueOf(mColorCode)];
        int bgcolor = Color.argb(rgba[3], rgba[0], rgba[1], rgba[2]);
        floatbar.setBackgroundColor(bgcolor);

        TextView toptext = (TextView) rootV.findViewById(R.id.projmad_bookcover_toptext);
        toptext.setText(mTopText);

        TextView author = (TextView) rootV.findViewById(R.id.projmad_bookcover_author_name);
        author.setText(mAuthor);

        TextView title = (TextView) rootV.findViewById(R.id.projmad_bookcover_title);
        title.setText(mTitle);

        TextView guide = (TextView) rootV.findViewById(R.id.projmad_bookcover_guide_text);
        guide.setText(mGuideText);

        ImageView animal = (ImageView) rootV.findViewById(R.id.projmad_bookcover_animal_image);
        animal.setImageResource( animal_ids[Integer.valueOf(mAnimalCode)] );

        rootV.setBackgroundColor(bgcolor^0x00FFFFFF);
        return rootV;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
