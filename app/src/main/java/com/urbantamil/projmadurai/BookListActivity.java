/*
This code is released under MIT License
(C) 2016-2017, Ezhil Language Foundation
<ezhillang@gmail.com>
*/
package com.urbantamil.projmadurai;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.tamil.TamilLetters;
import com.tamil.utf8;
import com.urbantamil.chithirai.TypefaceUtil;
import com.urbantamil.chithirai.verbs.VerbContent;
import com.urbantamil.projmadurai.MaduraiFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * An activity representing a list of Books. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link BookDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class BookListActivity extends AppCompatActivity implements View.OnClickListener, MaduraiBookCoverFragment.OnFragmentInteractionListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private SimpleItemRecyclerViewAdapter adapter = null;
    private boolean mTwoPane;
    private MaduraiLibrary maduraiLibrary = null;
    private String TAG = "BookListAct";
    private int m_filter_by = MaduraiFilter.FIELD_TITLE; //default
    public static final String ARG_FILTER_BY = "maduraifilter_BookListActivity_filter_by";
    public static Typeface latha = null;
    private View recyclerView;
    private MaduraiPreferences mPref = null;

    MaduraiLibrary getMaduraiLibrary() {
        return maduraiLibrary;
    }

    void fireToast() {
        new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), R.string.projmad_please_wait, Toast.LENGTH_LONG).show();
            }
        }.run();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        fireToast();
        switch( item.getItemId() ) {
            case R.id.menu_clear_all:
                do_clear_action();
                break;
            case R.id.menu_sort_ascending:
                do_sort(true);
                break;
            case R.id.menu_sort_descending:
                do_sort(false);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void do_sort(boolean ascending) {
       // adapter.setupSortedList(ascending);
       // adapter.notifyDataSetChanged();
        // lazy way out! quick too..
        adapter.updateSortedList(ascending);
        recyclerView.postInvalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mnu = new MenuInflater(getApplicationContext());
        if ( inBookmarkMode() ||
                inRecentBookMode() ) {
            mnu.inflate(R.menu.clear_menu, menu);
        } else {
            mnu.inflate(R.menu.sort_menu,menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle b = getIntent().getExtras();
        mPref = new MaduraiPreferences(this);

        if (latha == null)
            latha = TypefaceUtil.getTypeface(TypefaceUtil.LATHA, getApplicationContext());
        if (b != null) {
            m_filter_by = b.getInt(ARG_FILTER_BY, MaduraiFilter.FIELD_TITLE);
        }
        super.onCreate(savedInstanceState);

        Log.d(TAG, "m_filter_by => " + String.valueOf(m_filter_by));
        setContentView(R.layout.activity_book_list);
        try {
            maduraiLibrary = MaduraiBook.loadFromAsset(getResources(), R.raw.projmad);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //hide this button - replace with menu
        Button bklist_sort_btn = (Button) findViewById(R.id.projmad_sort_button);
        bklist_sort_btn.setOnClickListener(this);
        bklist_sort_btn.setVisibility(View.INVISIBLE);

        if (m_filter_by == MaduraiFilter.FIELD_TITLE) {
            toolbar.setTitle(R.string.projmad_books);
        } else if (m_filter_by == MaduraiFilter.FIELD_AUTHOR) {
            toolbar.setTitle(R.string.projmad_authors);
        } else if (m_filter_by == MaduraiFilter.FIELD_BOOKMARK) {
            toolbar.setTitle(R.string.projmad_bookmarks);
            //bklist_sort_btn.setVisibility(View.VISIBLE);
            bklist_sort_btn.setText(R.string.projmad_clear_saved);
        } else if (m_filter_by == MaduraiFilter.FIELD_RECENTBOOK) {
            toolbar.setTitle(R.string.projmad_recentbooks);
            //bklist_sort_btn.setVisibility(View.VISIBLE);
            bklist_sort_btn.setText(R.string.projmad_clear_saved);
        } else if ( m_filter_by  == MaduraiFilter.FIELD_LASTREAD) {
            // just popup the last read book
            // get last most-recent read book and pop it open in the activity
            // FIXME
            MaduraiBook recent = MaduraiRecentBook.getManager(getParent()).getMostRecent();
            if ( recent == null) {
                Toast.makeText(this,R.string.projmad_recentbooks_empty,Toast.LENGTH_LONG);
                new Runnable(){
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        finish();
                    }
                }.run();
                return;
            }

            Intent intent = new Intent(getApplicationContext(), BookDetailActivity.class);
            try {
                intent.putExtra(BookDetailFragment.ARG_ITEM_JSON, recent.SaveToJSON());
                intent.putExtra(BookDetailFragment.ARG_ITEM_OPEN,true);
            } catch (Exception e) {
                //pass
            }
            startActivity(intent);
            finish();
            return;
        }

        setTitle(toolbar.getTitle());

        recyclerView = findViewById(R.id.book_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.book_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = new SimpleItemRecyclerViewAdapter();
        new Runnable() {
            @Override
            public void run() {
                adapter.setupSortedList(true);
            }
        }.run();

        recyclerView.setAdapter(adapter);
        //feedback to the user on number of items - Total Items
        Toast.makeText(getApplicationContext(),getString(R.string.MessageTotalItems) + adapter.getItemCount(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        Button btn = (Button) v;
        do_clear_action();
        v.postInvalidate();
    }

    private void do_clear_action() {
        if (m_filter_by == MaduraiFilter.FIELD_BOOKMARK) {
            MaduraiBookmark.getManager(getParent()).erase();
            Log.d(TAG, "clear bookmark");
            this.adapter.clear();
            //update storage too! TBD
        } else if (m_filter_by == MaduraiFilter.FIELD_RECENTBOOK) {
            MaduraiRecentBook.getManager(getParent()).erase();
            Log.d(TAG, "clear recent books");
            this.adapter.clear();
            //update storage too!
        } else {
            //change sort order
            adapter.flipOrder();
        }
        //notify all involved in the process
        adapter.notifyDataSetChanged();
        recyclerView.postInvalidate();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    boolean inGenreMode() {
        return (m_filter_by == MaduraiFilter.FIELD_GENRE);
    }

    boolean inBookmarkMode() {
        return (m_filter_by == MaduraiFilter.FIELD_BOOKMARK);
    }

    boolean inRecentBookMode() {
        return (m_filter_by == MaduraiFilter.FIELD_RECENTBOOK);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

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

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
        private String TAG = "ItemViewAdapter";
        private MaduraiLibrary maduraiLibrary;
        private boolean m_adapterSortedAscending = true;

        private String mTitle;
        private String mTopText;
        private String mAuthor;
        private String mAnimalCode;
        private String mColorCode;
        private String mGuideText;

        private ArrayList<String> genre_list = null;
        final private ArrayList<Object> sort_lookup = new ArrayList<Object>();

        private String getGenreColorCode(String genre) {
            int pos = Collections.binarySearch(genre_list,genre, utf8.comparator);
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

        public SimpleItemRecyclerViewAdapter() {
            if ( genre_list == null ) {
                MaduraiLibrary mlib = MaduraiBook.loadFromAssetDefault();
                genre_list = mlib.getGenresList();
                TamilLetters.sort(genre_list);
            }
            maduraiLibrary = getMaduraiLibrary();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View rootV = inflater
                    .inflate(R.layout.book_list_content, parent, false);
            ViewHolder rootVH = new ViewHolder(rootV);

            return rootVH;
        }

        // map the item at #position to the viewholder
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            MaduraiBook mBook = null;
            String genre = null;
            Log.d(TAG, "Total size of lookup array => " + String.valueOf(sort_lookup.size()));

            // request row alternation ??
            if ( mPref.getRowAlternate() ) {
                if (position % 2 == 1) {
                    holder.mView.setBackgroundColor(Color.BLUE);
                    holder.mView.getBackground().setAlpha(100);
                } else {
                    holder.mView.setBackgroundColor(Color.BLUE);
                    holder.mView.getBackground().setAlpha(51);
                }
            }

            //set row # (min row = 1)
            holder.mRowPos.setText(String.valueOf(1 + position));

            if (inGenreMode()) {
                genre = (String) sort_lookup.get(position);
                setupGenreView(genre, holder);
                return;
            }

            mBook = (MaduraiBook) sort_lookup.get(position);
            holder.mItem = mBook;

            mTitle = mBook.getTitle();
            mTopText = getResources().getString(R.string.projmad_bookcover_toptext);
            mAuthor = mBook.getAuthor();
            mAnimalCode = getGenreAnimalCode(mBook.getGenre());
            mGuideText = mBook.getGenre();
            mColorCode = getGenreColorCode(mBook.getGenre());

            short [] rgba = colors_rgba[Integer.valueOf(mColorCode)];
            int bgcolor = Color.argb(rgba[3], rgba[0], rgba[1], rgba[2]);
            holder.mFloatbar.setBackgroundColor(bgcolor);
            holder.mAnimal.setImageResource( animal_ids[Integer.valueOf(mAnimalCode)] );
            holder.mView.setBackgroundColor(bgcolor^0x00FFFFFF);

            if (m_filter_by == MaduraiFilter.FIELD_AUTHOR) {
                holder.mIdView.setText(mBook.getAuthor());
                holder.mContentView.setText(mBook.getTitle());
            } else {
                holder.mIdView.setText(mBook.getTitle());
                holder.mContentView.setText(mBook.getAuthor());
            }

            //holder.mRowPos.setVisibility(View.INVISIBLE);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (mTwoPane) {
                            Bundle arguments = new Bundle();

                            arguments.putString(BookDetailFragment.ARG_ITEM_JSON, holder.mItem.SaveToJSON());
                            BookDetailFragment fragment = new BookDetailFragment();
                            fragment.setArguments(arguments);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.book_detail_container, fragment)
                                    .commit();
                        } else {
                            Context context = v.getContext();
                            Intent intent = new Intent(context, BookDetailActivity.class);
                            intent.putExtra(BookDetailFragment.ARG_ITEM_JSON, holder.mItem.SaveToJSON());
                            context.startActivity(intent);
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "Failed to serialize JSON for object mItem with error \n->" + e.toString());
                    }
                }
            });

            //allow deleting books if necessary from the said lists
            if (inRecentBookMode() ||
                    inBookmarkMode()) {

                holder.mView.setTag(holder);
                holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    final int rel_position = position;

                    @Override
                    public boolean onLongClick(View v) {
                        OKCancelDialog dlg = new OKCancelDialog();
                        getSupportFragmentManager().beginTransaction().add(dlg,"OK_CANCEL_DIALOG").commit();
                        //already shown the dialog here..
                        boolean OK = dlg.m_OK;
                        if (!OK) {
                            Log.d(TAG, "Cancelled deletion item");
                            return true;
                        }

                        if (inRecentBookMode()) {
                            MaduraiRecentBook.getManager(getParent()).remove(rel_position);
                        } else if (inBookmarkMode()) {
                            MaduraiBookmark.getManager(getParent()).remove(rel_position);
                        }
                        Log.d(TAG, "removed item @ " + String.valueOf(rel_position));
                        View recycler = findViewById(R.id.book_list);
                        recycler.invalidate();
                        adapter.notifyDataSetChanged();
                        return true; //handled the event OK so far
                    }
                });
            }
        }

        private void setupGenreView(String arg_genre, ViewHolder holder) {
            final String genre = arg_genre;
            holder.mIdView.setText(genre);
            holder.mContentView.setText(GenreDescription.get(genre));
            holder.mIdView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //open the search view with the category set to 'genre' etc.

                    Context context = v.getContext();
                    Intent intent = new Intent(context, MaduraiSearch.class);
                    intent.putExtra(MaduraiSearch.ARG_ITEM_GENRE, genre);

                    context.startActivity(intent);

                }
            });

            mColorCode = getGenreColorCode(arg_genre);
            mAnimalCode =getGenreAnimalCode(arg_genre);

            short [] rgba = colors_rgba[Integer.valueOf(mColorCode)];
            int bgcolor = Color.argb(rgba[3], rgba[0], rgba[1], rgba[2]);
            holder.mFloatbar.setBackgroundColor(bgcolor)

            ;
            holder.mAnimal.setImageResource( animal_ids[Integer.valueOf(mAnimalCode)] );
            holder.mView.setBackgroundColor(bgcolor^0x00FFFFFF);
        }

        private void flipOrder() {
            Collections.reverse(sort_lookup);
        }

        private Object getObject(int idx) {
            Object book_or_str = null;
            if (inBookmarkMode())
                book_or_str = MaduraiBookmark.getManager(getParent()).getBookAt(idx);
            else if (inRecentBookMode())
                book_or_str = MaduraiRecentBook.getManager(getParent()).getBookAt(idx);
            else if (inGenreMode())
                book_or_str = getMaduraiLibrary().getGenresList().get(idx);
            else
                book_or_str = maduraiLibrary.getBookAt(idx);
            return book_or_str;
        }

        private void updateSortedList(boolean is_ascending) {
            if ( m_adapterSortedAscending == is_ascending)
                return;
            //reverse the list if current state don't match
            flipOrder();
            m_adapterSortedAscending = is_ascending;
            notifyDataSetChanged();
        }

        private void setupSortedList(final boolean ascending) {
            m_adapterSortedAscending = ascending;
            sort_lookup.clear();
            for (int idx = 0; idx < getItemCount(); idx++) {
                Object book_or_str = getObject(idx);
                sort_lookup.add(book_or_str);
            }

            Log.d(TAG, "sort_lookup has size => " + sort_lookup.size());

            // no sorting for recent book mode
            if (inRecentBookMode())
                return;

            if (inGenreMode()) {
                //compare by genre
                new Runnable() {
                    @Override
                    public void run() {
                        final boolean ascend = ascending;
                        Collections.sort(sort_lookup, new Comparator<Object>()

                                {
                                    @Override
                                    public int compare(Object lhs, Object rhs) {
                                        String s_lhs = (String) lhs;
                                        String s_rhs = (String) rhs;
                                        return ascend ? s_lhs.compareTo(s_rhs) : s_rhs.compareTo(s_rhs);
                                    }
                                }
                        );
                    }
                }.run();
            } else {
                int mode = 0;
                if (m_filter_by == MaduraiFilter.FIELD_TITLE) {
                    mode = MaduraiBookComparator.TITLE;
                } else if (m_filter_by == MaduraiFilter.FIELD_AUTHOR) {
                    mode = MaduraiBookComparator.AUTHOR;
                } else if (m_filter_by == MaduraiFilter.FIELD_RECENTBOOK
                        || m_filter_by == MaduraiFilter.FIELD_BOOKMARK) {
                    mode = MaduraiBookComparator.TITLE;
                } else {
                    mode = MaduraiBookComparator.TITLE;
                }


                final MaduraiBookComparator maduraiBookComparator = new MaduraiBookComparator(mode);

                new Runnable() {
                    @Override
                    public void run() {
                        final boolean ascend = ascending;
                        Collections.sort(sort_lookup, new Comparator<Object>() {
                                    @Override
                                    public int compare(Object lhs, Object rhs) {
                                        if (ascend) {
                                            return maduraiBookComparator.compare((MaduraiBook) lhs, (MaduraiBook) rhs);
                                        }
                                        return maduraiBookComparator.compare((MaduraiBook) rhs, (MaduraiBook) lhs);
                                    }
                                }

                        );
                    }
                }.run();
            }
            this.notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            int size = 0;
            if (inBookmarkMode())
                size = MaduraiBookmark.getManager(getParent()).size();
            else if (inRecentBookMode())
                size = MaduraiRecentBook.getManager(getParent()).size();
            else if (inGenreMode())
                size = getMaduraiLibrary().getGenresList().size();
            else
                size = maduraiLibrary.size();
            return (int) size;
        }

        public void clear() {
            sort_lookup.clear();

            if (inBookmarkMode())
               MaduraiBookmark.getManager(getParent()).erase();
            else if (inRecentBookMode())
               MaduraiRecentBook.getManager(getParent()).erase();

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public final TextView mRowPos;
            public MaduraiBook mItem;
            public final ImageView mAnimal;
            public final View mFloatbar;

            public ViewHolder(View rootV) {
                super(rootV);

                mView = (View) rootV.findViewById(R.id.id_parent);
                mIdView = (TextView) rootV.findViewById(R.id.id);
                mContentView = (TextView) rootV.findViewById(R.id.content);
                mRowPos = (TextView) rootV.findViewById(R.id.row_pos);

                mFloatbar = (View) rootV.findViewById(R.id.projmad_list_bookcover_floatbar_top);

                TextView toptext = (TextView) rootV.findViewById(R.id.projmad_list_bookcover_toptext);
                toptext.setText(mTopText);

                TextView author = (TextView) rootV.findViewById(R.id.projmad_list_bookcover_author_name);
                author.setText(mAuthor);

                TextView title = (TextView) rootV.findViewById(R.id.projmad_list_bookcover_title);
                title.setText(mTitle);

                TextView guide = (TextView) rootV.findViewById(R.id.projmad_list_bookcover_guide_text);
                guide.setText(mGuideText);

                mAnimal = (ImageView) rootV.findViewById(R.id.projmad_list_bookcover_animal_image);

                // intialize with Latha font
                mIdView.setTypeface(latha, Typeface.BOLD);
                mContentView.setTypeface(latha, Typeface.ITALIC);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
