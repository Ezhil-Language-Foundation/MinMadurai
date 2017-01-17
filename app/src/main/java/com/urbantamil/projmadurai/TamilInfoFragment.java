/*
This code is released under MIT License
(C) 2016-2017, Ezhil Language Foundation
<ezhillang@gmail.com>
*/
package com.urbantamil.projmadurai;

import com.tamil.utf8;
import com.tamil.TamilLetters;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TamilInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TamilInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TamilInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_GENRE = "genre";
    private static final String ARG_AUTHOR = "author";

    // TODO: Rename and change types of parameters
    private List<String> mGENRE = null;
    private List<String> mAUTHOR = null;

    private OnFragmentInteractionListener mListener;

    public TamilInfoFragment() {
        // Required empty public constructor
    }

    public static TamilInfoFragment newInstance(ArrayList<String> author, ArrayList<String> genre) {
        TamilInfoFragment fragment = new TamilInfoFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_AUTHOR, author);
        args.putStringArrayList(ARG_GENRE, genre);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAUTHOR = getArguments().getStringArrayList(ARG_AUTHOR);
            mGENRE = getArguments().getStringArrayList(ARG_GENRE);
        }
        if (mAUTHOR == null) {
            //default
            mAUTHOR = Arrays.asList(new String [] {
                    "ஜகந்நாதன்",
                    "வரதராசன்",
                    "தகடூர",
                    "திருத்தக்க தேவர்",
                    "சோணாசல பாரதியார்"
            });

            mGENRE = Arrays.asList(new String[] {"Uncagteorized","Everything"});
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         ArrayAdapter<String> adapter_tamil_strings;


        //  Inflate the layout for this fragment
        final View tamilinfo = inflater.inflate(R.layout.fragment_tamil_info, container, false);
        final ListView lv_tamilinfo = (ListView) tamilinfo.findViewById(R.id.listview_tamil_info_sort);
        adapter_tamil_strings = new ArrayAdapter<String>(tamilinfo.getContext(),
                android.R.layout.simple_list_item_1,mAUTHOR);
        lv_tamilinfo.setAdapter(adapter_tamil_strings);

        // use toggle button to flip the genre/author info
        // then sort the data
        final Button btn_tamilinfo = (Button) tamilinfo.findViewById(R.id.button_tamil_info_sort);
        final ToggleButton toggle_btn = (ToggleButton) tamilinfo.findViewById(R.id.toggle_tamil_info_category);
        toggle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> ref_array =
                toggle_btn.isChecked() ? mGENRE : mAUTHOR;
                lv_tamilinfo.setAdapter(new ArrayAdapter<String>(tamilinfo.getContext(),
                        android.R.layout.simple_list_item_1,ref_array));
                lv_tamilinfo.invalidate();
                lv_tamilinfo.setTag(ref_array);
            }
        });

        /// sorting activity
        btn_tamilinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> ref_array =
                        toggle_btn.isChecked() ? mGENRE : mAUTHOR;
                ArrayAdapter<String> adapter_str = new ArrayAdapter<String>(lv_tamilinfo.getContext(),
                        android.R.layout.simple_list_item_1,ref_array);
                adapter_str.sort(utf8.comparator);
                lv_tamilinfo.setAdapter(adapter_str);
                lv_tamilinfo.invalidate();
            }
        });

        return tamilinfo;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
