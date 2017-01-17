package com.urbantamil.chithirai;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.urbantamil.chithirai.verbs.VerbContent;
import com.urbantamil.projmadurai.R;

/**
 * A fragment representing a single Seyal detail screen.
 * This fragment is either contained in a {@link SeyalListActivity}
 * in two-pane mode (on tablets) or a {@link SeyalDetailActivity}
 * on handsets.
 */
public class SeyalDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private VerbContent.VerbItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SeyalDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = VerbContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            if (mItem.hasIntent())
                return;

            Activity activity;
            activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle( getResources().getString( mItem.content) );
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.seyal_detail, container, false);

        if (mItem != null) {
            if ( mItem.hasIntent() ) {
                Intent intent = new Intent(getActivity(),mItem.intent);
                intent.putExtra(SeyalDetailFragment.ARG_ITEM_ID,mItem.id);
                intent.putExtra(SeyalListActivity.TWO_PANE_MODE,true);
                startActivity(intent);
                this.getFragmentManager().beginTransaction().detach(this).commit();
                return null;
            } else {
                // Show the dummy content as text in a TextView.
                Toast.makeText(this.getContext(), "Cannot find intent for item ("+mItem.toString()+")",
                        Toast.LENGTH_SHORT).show();
                ((TextView) rootView.findViewById(R.id.seyal_detail)).setText(mItem.details);
            }
        }

        return rootView;
    }
}
