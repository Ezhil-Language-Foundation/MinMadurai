package com.urbantamil.chithirai.verbs;

import android.app.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.urbantamil.projmadurai.*;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class VerbContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<VerbItem> ITEMS = new ArrayList<VerbItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, VerbItem> ITEM_MAP = new HashMap<String, VerbItem>();

    private static final int COUNT = 9;

    public final static HashMap<String,Integer> translation = new LinkedHashMap<String,Integer>();
    static {
        translation.put("LastRead", Integer.valueOf(R.string.projmad_LastRead) );
        translation.put("Books", Integer.valueOf(R.string.projmad_books) );
        translation.put("Authors", Integer.valueOf(R.string.projmad_authors) );
        translation.put("Genres", Integer.valueOf(R.string.projmad_genre));
        translation.put("Search", Integer.valueOf(R.string.projmad_search) );
        translation.put("Recent Books", Integer.valueOf(R.string.projmad_recentbooks) );
        translation.put("Bookmarks", Integer.valueOf(R.string.projmad_bookmarks) );
        translation.put("Settings", Integer.valueOf(R.string.projmad_settings) );
        translation.put("Credits", Integer.valueOf(R.string.projmad_credits) );
    }

    static {
        /// DEFINE the UI Elements and link it with the launcher activities
        int i = 1;
        // TBD: The intent associated with each verb item should be unique since otherwise
        //      we have no ways to disambiguate the verb from another should collisions arise.
        //      If we passed bundle args this would not happen but don't do this yet.
        addItem(createVerbItem(i++,"LastRead","Read last-read book",LastReadListActivity.class,R.drawable.ic_local_library_black_24dp));
        addItem(createVerbItem(i++,"Books","browse books by title",BookListActivity.class,R.drawable.ic_library_books_black_24dp));
        addItem(createVerbItem(i++,"Authors","browse books by authors",AuthorListActivity.class,R.drawable.ic_contacts_black_24dp));
        addItem(createVerbItem(i++,"Genres","browse books by genres",GenreListActivity.class,R.drawable.ic_collections_bookmark_black_24dp));
        addItem(createVerbItem(i++,"Search","search books",MaduraiSearch.class,R.drawable.ic_search_black_24dp));
        addItem(createVerbItem(i++,"Recent Books","browse recent books",MaduraiRecentbookActivity.class,R.drawable.ic_list_black_24dp));
        addItem(createVerbItem(i++,"Bookmarks","browse bookmarks",MaduraiBookmarkActivity.class,R.drawable.ic_book_black_24dp));
        addItem(createVerbItem(i++,"Settings","change app settings",MaduraiOptionsActivity.class,R.drawable.ic_settings_black_24dp));
        addItem(createVerbItem(i,"Credits","see credits, copyright notices",MaduraiCreditsActivity.class,R.drawable.ic_info_black_24dp));
        assert i == COUNT;
    }

    private static void addItem(VerbItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static VerbItem createVerbItem(int position,String action, String descr, Class<? extends Activity> intent) {
        return new VerbItem(String.valueOf(position), action, descr, intent,-1);
    }

    private static VerbItem createVerbItem(int position,String action, String descr,
                                           Class<? extends Activity> intent,int _drawable_id) {
        return new VerbItem(String.valueOf(position), action, descr, intent,_drawable_id);
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A Verb item representing a piece of content.
     */
    public static class VerbItem {
        public final String id;
        public final int drawable_id;
        public final int content;
        public final String details;
        public final Class<? extends Activity> intent;
        private final boolean is_author_or_books;

        public VerbItem(String id, String action, String details,Class<? extends Activity> intent,int _drawable_id) {
            this.id = id;
            this.is_author_or_books = (action == "Authors") || (action == "Books");
            this.drawable_id = _drawable_id;
            this.content = translation.get(action).intValue();
            this.details = details;
            this.intent = intent;
        }

        public boolean isAuthorOrBook() {
            return is_author_or_books;
        }

        public int getDrawableId() {
            return drawable_id;
        }

        public boolean hasIntent() {
            return (intent != null);
        }

        public String toString(Activity activity) {
            return activity.getResources().getString(  content );
        }
    }
}
