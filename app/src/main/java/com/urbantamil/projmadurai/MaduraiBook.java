/*
This code is released under MIT License
(C) 2016-2017, Ezhil Language Foundation
<ezhillang@gmail.com>
*/
package com.urbantamil.projmadurai;

import android.content.res.Resources;
import android.util.JsonWriter;
import android.util.Log;

import com.tamil.utf8;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import static junit.framework.Assert.assertTrue;

class MaduraiOrg {
    final static String ref_url_pdf = "http://projectmadurai.org/pm_etexts/pdf/";
    final static String ref_url_html = "http://projectmadurai.org/pm_etexts/utf8/";

    final static String ref_url_assets = "file:///android_asset/datamad/";

    static String getFullUrlPDF(String dest) {
        return ref_url_pdf+dest;
    }

    static String getFullUrlHTML(String dest) {
        return ref_url_html+dest;
    }

    static String getFullUrlAssetsHTML(String dest) {
        return ref_url_assets+dest;
    }
}

/**
 * Created by muthu on 3/13/2016.
 */
public class MaduraiBook {
    final public static String TAG = "MaduraiBook";
    public static MaduraiBook dummy(int idx) {
        String id = String.valueOf(idx);
        return new MaduraiBook("title"+id,"author"+id,"genre"+id,new ArrayList<String>(),new ArrayList<String>());
    }

    public MaduraiBook(String title, String author, String genre, ArrayList<String> url_pdf, ArrayList<String> url_html) {
        this.author = author;
        this.title = title;
        this.genre = genre;
        this.url_html = url_html;
        this.url_pdf = url_pdf;
    }

    //see if book 1 is almost the same as book 2
    public boolean matches(MaduraiBook other) {
        if ( other == this )
            return true;
        //see if T-A-G match up
        return (getAuthor().contentEquals( other.getAuthor() ) &&
                getTitle().contentEquals( other.getTitle() ) &&
                getGenre().contentEquals( other.getGenre() ) );
    }

    /// toString
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nTitle => "+getTitle());
        sb.append("\nAuthor => "+getAuthor());
        sb.append("\nGenre =>"+getGenre());
        return sb.toString();
    }

    /// serializer
    String SaveToJSON() throws IOException {

        /// java boilerplate
        StringWriter sw = new StringWriter();
        JsonWriter jsw = new JsonWriter(sw);
        jsw.beginObject();
        // being actual serializing
        jsw.name("title").value(getTitle());
        jsw.name("author").value(getAuthor());
        jsw.name("genre").value(getGenre());

        jsw.name("url_pdf").beginObject();
        Iterator<String> pdfitr = getUrl_pdf();
        while(pdfitr.hasNext()) {
            String pdfstr = pdfitr.next();
            jsw.name(pdfstr).value(MaduraiOrg.getFullUrlPDF(pdfstr));
        };
        jsw.endObject();

        jsw.name("url_html").beginObject();
        Iterator<String> htmitr = getUrl_html();
        while(htmitr.hasNext()) {
            String htmlstr = htmitr.next();
            jsw.name(htmlstr).value(MaduraiOrg.getFullUrlHTML(htmlstr) );
        };
        jsw.endObject();
        // end object
        jsw.endObject();
        jsw.close();
        sw.close();
        return sw.toString();
    }
    /// de-serialize
    public static MaduraiBook LoadFromJSON(JSONObject obj) throws JSONException {
        ArrayList<String> url_pdf = new ArrayList<String>(  );
        Iterator<String> url_pdf_itr = obj.getJSONObject("url_pdf").keys();
        while ( url_pdf_itr.hasNext() ) {
            url_pdf.add(url_pdf_itr.next());
        }

        ArrayList<String> url_html = new ArrayList<String>(  );
        Iterator<String> url_html_itr = obj.getJSONObject("url_html").keys();
        while ( url_html_itr.hasNext() ) {
            url_html.add(url_html_itr.next());
        }

        MaduraiBook newobj = new MaduraiBook(obj.getString("title"),
                obj.getString("author"),
                obj.getString("genre"),
                url_pdf,
                url_html);
        return newobj;
    }

    private static MaduraiLibrary default_lib = null;
    public static MaduraiLibrary loadFromAssetDefault() {
        if ( default_lib == null) {
            try {
                MaduraiBook.loadFromAsset(Resources.getSystem(), R.raw.projmad);
            } catch (Exception except) {
                Log.d(TAG, "exception loading asset data");
            }
        }
        return default_lib;
    }

    // load a list of MaduraiBook objects into the memory from a given asset class
    public static MaduraiLibrary loadFromAsset(Resources resources, int resource_id) throws JSONException {
        MaduraiLibrary library = new MaduraiLibrary();
        JSONArray arrayOfObjects = MaduraiJSONHelper.loadArrayOfObjects(resources.openRawResource(resource_id));
        for(int idx = 0; idx < arrayOfObjects.length(); idx++) {
            JSONObject obj = arrayOfObjects.getJSONObject(idx);
            library.addBook(LoadFromJSON(obj));
        }
        if ( default_lib == null)
            default_lib = library;
        return library;
    }

    /// iterate through URLs
    public Iterator<String> getUrl_html() {
        return url_html.iterator();
    }

    public void setUrl_html(String url_html) {
        this.url_html.add(  url_html );
    }

    public Iterator<String> getUrl_pdf() {
        return url_pdf.iterator();
    }

    public void setUrl_pdf(String url_pdf) {
        this.url_pdf.add(url_pdf);
    }

    //// scalar properties
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    private ArrayList<String> url_html;
    private ArrayList<String> url_pdf;
    private String genre;
    private String author;
    private String title;
}

//sorting helper
class MaduraiBookComparator implements Comparator<MaduraiBook> {
    Comparator<String> m_comparator;
    int m_comparison_field = 1;

    final public static int AUTHOR = 1;
    final public static int GENRE = 2;
    final  public static int TITLE = 3;

    MaduraiBookComparator(int field) {
        m_comparison_field = field;
        m_comparator  = utf8.comparator;
        assertTrue( (field <= TITLE) && (field >= AUTHOR) );
    }

    // sort by title
    @Override
    public int compare(MaduraiBook a, MaduraiBook b) {
        switch (m_comparison_field) {
            case AUTHOR:
                return m_comparator.compare(a.getAuthor(), b.getAuthor());
            case TITLE:
                return m_comparator.compare(a.getTitle(), b.getTitle());
            case GENRE:
                return m_comparator.compare(a.getGenre(), b.getGenre());
            default:
        }
        return 0;
    }
}


// a BookArray aliasing as a String Array
class BookAlias extends ArrayList<String> {
    ArrayList<MaduraiBook> ref_book_list;

    BookAlias() {
        super();
        ref_book_list = new ArrayList<MaduraiBook>();
    }

    public void add(int pos, MaduraiBook book) {
        ref_book_list.add(pos, book);
    }

    public void add(MaduraiBook book) {
        ref_book_list.add(book);
    }

    @Override
    public String get(int position) {
        return getBook(position).toString();
    }

    public MaduraiBook getBook(int position) {
        return ref_book_list.get(position);
    }
};
