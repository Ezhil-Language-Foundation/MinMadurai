package com.urbantamil.chithirai;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.urbantamil.chithirai.verbs.VerbContent;
import com.urbantamil.projmadurai.MaduraiBookmark;
import com.urbantamil.projmadurai.MaduraiBookmarkActivity;
import com.urbantamil.projmadurai.MaduraiCreditsActivity;
import com.urbantamil.projmadurai.MaduraiPreferences;
import com.urbantamil.projmadurai.MaduraiRecentBook;
import com.urbantamil.projmadurai.R;
import com.urbantamil.projmadurai.MaduraiSearch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An activity representing a list of Seyalgal. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link SeyalDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and`
 * item details side-by-side using two vertical panes.
 */
public class SeyalListActivity extends AppCompatActivity {
    public static final String TWO_PANE_MODE = "TwoPaneMode";
    public static String APP_TITLE = "மின் மதுரை";
    public static String TAG = "Seyal List Activity";
    public static Typeface customFontOrLatha = null;
    private MaduraiPreferences m_prefs = null;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_chithirai_search) {
            Log.d(TAG, "search");
            Intent i = new Intent(this,MaduraiSearch.class);
            startActivity(i);
            return true;
        } else if ( id == R.id.action_chithirai_credits ) {
            Log.d(TAG,"credits");
            Intent credit_i = new Intent(this, MaduraiCreditsActivity.class);
            startActivity(credit_i);
            return true;
        } else if ( id == R.id.action_chithirai_favorite) {
            Log.d(TAG,"favorite");
            Intent ifont = new Intent(this, MaduraiBookmarkActivity.class);
            startActivity(ifont);
            return true;
        } else if ( id == R.id.action_chithirai_settings ) {
            Log.d(TAG,"settings");
            ///Intent i = new Intent(this, MaduraiSettingsActivity.class);
            ///startActivity(i);
            return true;
        }
        //jee boom ba!
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final Activity activity = this;
        getMenuInflater().inflate(R.menu.activity_chithirai, menu);
        final SearchView sv = (SearchView) menu.findItem(0);

        if (sv != null) {
            sv.setOnSearchClickListener(new SearchView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String query = sv.getQuery().toString();
                    Intent i = new Intent(activity, MaduraiSearch.class);
                    i.putExtra(MaduraiSearch.ARG_ITEM_AUTHOR, query);
                    startActivity(i);
                    return;
                }
            });
        }

        return true;
    }

        /**
         * Whether or not the activity is in two-pane mode, i.e. running on a tablet
         * device.
         */
        private boolean mTwoPane;


    @Override
    protected void onStop() {
        MaduraiRecentBook.getManager(this).save();
        MaduraiBookmark.getManager(this).save();

        super.onStop();
    }

    @Override
        protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seyal_list);
        final Context context = getApplicationContext();
        m_prefs = new MaduraiPreferences(this);

        //start new thread on creating/loading fonts
        new Runnable() {
            @Override
            public void run() {
                String fontName = m_prefs.getFontname();
                Typeface regular = TypefaceUtil.getTypefaceByName(fontName, context);
                customFontOrLatha = regular;
                Typeface light = regular; //TypefaceUtil.getTypeface(TypefaceUtil.LATHA, context);
                Typeface condensed = regular; //TypefaceUtil.getTypeface(TypefaceUtil.AMMA1, context);
                Typeface thin = regular; //TypefaceUtil.getTypeface(, context);
                Typeface medium = regular; //TypefaceUtil.getTypefaceByName(fontName, context);
                Map<String, Typeface> fonts = new HashMap<>();
                fonts.put("sans-serif", regular);
                fonts.put("sans-serif-light", light);
                fonts.put("sans-serif-condensed", condensed);
                fonts.put("sans-serif-thin", thin);
                fonts.put("sans-serif-medium", medium);
                TypefaceUtil.overrideFonts(fonts);
            }
        }.run();

        //load the bookmark and recent books
        MaduraiRecentBook.getManager(this).load();
        MaduraiBookmark.getManager(this).load();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(APP_TITLE);
        setTitle(APP_TITLE);
        toolbar.showOverflowMenu();
        //toolbar.setSystemUiVisibility(View.VISIBLE);
        findViewById(R.id.fab).setVisibility(View.INVISIBLE);

        View recyclerView = findViewById(R.id.seyal_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.seyal_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(VerbContent.ITEMS));
    }

    public static int [] colors = new int [] { Color.GRAY, Color.LTGRAY};
    public static int [] fgcolors = new int [] {Color.WHITE,Color.BLACK};

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<VerbContent.VerbItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<VerbContent.VerbItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.seyal_list_content, parent, false);
            return new ViewHolder(view);
        }

        private int getColor(int pos) {
            return colors[pos%colors.length];
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);

            if ( m_prefs != null && m_prefs.getRowAlternate()) {
                //alternate colors requested
                holder.mView.setBackgroundColor(getColor(position));
                holder.mContentView.setTextColor(getFgColor(position));
            } else {
                // identical color rows by default
            }

            holder.mImage.setBackgroundResource( holder.mItem.drawable_id );
            holder.mContentView.setText(mValues.get(position).content);
            holder.mContentView.setTypeface(customFontOrLatha, Typeface.BOLD_ITALIC);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putBoolean(TWO_PANE_MODE,mTwoPane);
                        arguments.putString(SeyalDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        SeyalDetailFragment fragment = new SeyalDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.seyal_detail_container, fragment)
                                .commit();
                    } else {
                        final Context context = v.getContext();
                        Intent intent;
                        if ( holder.mItem.hasIntent() ) {
                            intent = new Intent(context,holder.mItem.intent);
                        } else {
                            //default handler
                            intent = new Intent(context, SeyalDetailActivity.class);
                            intent.putExtra(SeyalDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        }
                        intent.putExtra(TWO_PANE_MODE,mTwoPane);
                        new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, R.string.projmad_please_wait, Toast.LENGTH_LONG).show();
                            }
                        }.run();
                        context.startActivity(intent);
                    }
                }
            });
        }

        private int getFgColor(int position) {
            return fgcolors[position%colors.length];
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        // widget wrapper class for elements in list item view
        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView mImage;
            public final TextView mContentView;
            public VerbContent.VerbItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (TextView) view.findViewById(R.id.seyal_item_content);
                mImage = (ImageView) view.findViewById(R.id.seyal_item_image);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    /// double tap to exit
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.MessageDoubleTapToExit, Toast.LENGTH_SHORT).show();

        // clear the flag if no action happens between now and 2s later
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
