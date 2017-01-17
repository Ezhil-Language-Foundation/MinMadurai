/*
This code is released under MIT License
(C) 2016-2017, Ezhil Language Foundation
<ezhillang@gmail.com>
*/
package com.urbantamil.projmadurai;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONObject;
import org.w3c.dom.Text;

import static android.R.attr.fragment;


class CustomWebViewClient extends WebViewClient {
    private int running = 0; // Could be public if you want a timer to check.

    Button btn;

    CustomWebViewClient() {
        super();
        btn = null;
    }

    CustomWebViewClient(Button b) {
        super();
        btn = b;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String urlNewString) {
        running++;
        webView.loadUrl(urlNewString);
        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        running = Math.max(running, 1); // First request move it to 1.
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (--running == 0) { // just "running--;" if you add a timer.
            // TODO: finished... if you want to fire a method.
            if (btn != null) {
                btn.setText(R.string.projmad_open_browser);
                btn.setEnabled(true);
            }

            /// interesting pieces to horizontal scroll
            ObservableWebView myWebView = (ObservableWebView) view;
            String varMySheet = "var mySheet = document.styleSheets[0];";

            String addCSSRule = "function addCSSRule(selector, newRule) {"
                    + "ruleIndex = mySheet.cssRules.length;"
                    + "mySheet.insertRule(selector + '{' + newRule + ';}', ruleIndex);"

                    + "}";

            String insertRule1 = "addCSSRule('html', 'padding: 0px; height: "
                    + (myWebView.getMeasuredHeight()/view.getContext().getResources().getDisplayMetrics().density )
                    + "px; -webkit-column-gap: 0px; -webkit-column-width: "
                    + myWebView.getMeasuredWidth() + "px;')";

            myWebView.loadUrl("javascript:" + varMySheet);
            myWebView.loadUrl("javascript:" + addCSSRule);
            myWebView.loadUrl("javascript:" + insertRule1);

        }
    }
}

/**
 * A fragment representing a single Book detail screen.
 * This fragment is either contained in a {@link BookListActivity}
 * in two-pane mode (on tablets) or a {@link BookDetailActivity}
 * on handsets.
 */
public class BookDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_JSON = "item_json";
    public static final String ARG_ITEM_OPEN = "item_open"; //force open ?

    /**
     * The dummy content this fragment is presenting.
     */
    private MaduraiBook mItem = null;
    private int fontSize = 10;
    private ObservableWebView mWebView = null;
    private View.OnClickListener m_bookmarkListener = null;
    private boolean m_open_book = false;

    public void toggleBookmark() {
        // TBD : NOT IMPLEMENTED YET
    }

    class BookmarkClickListener implements View.OnClickListener {
                public void onClick(View v) {
                    Switch sw = (Switch) v;
                    Log.d(TAG, "Updating bookmark");
                    MaduraiBookmark bookmark_mgr = MaduraiBookmark.getManager(getActivity());
                    //update book : on recent list
                    if (sw.isChecked()) {
                        //add to bookmark manager
                        bookmark_mgr.add(mItem);
                    } else {
                        //remove bookmark manager
                        int pos = MaduraiBookmark.getManager(getActivity()).findBook(mItem);
                        if ( pos >= 0) {
                            bookmark_mgr.remove(pos);
                        }
                    }
                }
        }
    
    //Ref: SO - 4188168/adjust-font-size-of-android-webview
    public void fontSizePlus() {
        if ( mWebView == null)
            return;
        fontSize++;
        this.changeFontSize(fontSize);
    }

    public void fontSizeMinus() {
        if ( mWebView == null )
            return;
        if ( mWebView.getSettings().getMinimumFontSize() >= fontSize )
            return;
        fontSize--;
        this.changeFontSize(fontSize);
    }

    private void changeFontSize(int value) {
        if ( mWebView != null ) {
            mWebView.getSettings().setDefaultFontSize(value);
            mWebView.reload();
            mWebView.invalidate();
        }
        Log.d(TAG,"set font value => "+String.valueOf(value));
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BookDetailFragment() {
    }

    final String TAG="Madurai.BookDetailFrag";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments().containsKey(ARG_ITEM_JSON)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider
            if ( getArguments().containsKey(ARG_ITEM_OPEN)) {
                m_open_book = getArguments().getBoolean(ARG_ITEM_OPEN);
                Log.d(TAG,"ARG_ITEM_OPEN was requested as "+m_open_book);
            }

            String jsonStr = getArguments().getString(ARG_ITEM_JSON);
            try {
                Log.d(TAG,"JSON received ->\n"+jsonStr);
                mItem = MaduraiBook.LoadFromJSON(new JSONObject(jsonStr));
            } catch ( Exception e) {
                Log.d(TAG, "cannot deserialize string to JSON object; error \n"+e.toString());
            }
        }
        m_bookmarkListener = new BookmarkClickListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.book_detail, container, false);
        final View book_use_pdf = rootView.findViewById(R.id.book_use_pdf);
        setHasOptionsMenu(true);
        // book-use-PDF is not featured for the app
        book_use_pdf.setVisibility(View.INVISIBLE);

        if ( getArguments().containsKey(ARG_ITEM_OPEN)) {
            m_open_book = getArguments().getBoolean(ARG_ITEM_OPEN);
            Log.d(TAG,"ARG_ITEM_OPEN was requested as "+m_open_book);
        }

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.book_detail)).setText(mItem.getTitle());
            ((TextView) rootView.findViewById(R.id.book_author)).setText(mItem.getAuthor());
            ((CheckBox) rootView.findViewById(R.id.book_use_pdf)).setChecked(true);
            ((TextView) rootView.findViewById(R.id.book_genre)).setText(mItem.getGenre());

            final Switch sw = (Switch) rootView.findViewById(R.id.book_bookmark);
            if ( Build.VERSION.SDK_INT >= 14 ) {
                sw.setTextOn(getResources().getString(R.string.mark));
                sw.setTextOff(getResources().getString(R.string.clear));
            }

            //load current state of bookmarking for the book
            boolean isBookMarked = false;
            try {
                MaduraiBookmark mgr = MaduraiBookmark.getManager(getActivity());
                if ( mgr != null) {
                    isBookMarked = mgr.findBook(mItem) >= 0;
                }
            } catch(Exception e) {
                //pass
            }

            if ( Build.VERSION.SDK_INT >= 14 ) {
                sw.setChecked(isBookMarked);
            }

            sw.postInvalidate();
            sw.setOnClickListener(m_bookmarkListener);
            final Button btn = (Button) rootView.findViewById(R.id.book_open_browser);
            mWebView = (ObservableWebView) rootView.findViewById(R.id.book_webView);

            final ObservableWebView wv = (ObservableWebView) rootView.findViewById(R.id.book_webView);

            wv.getSettings().setJavaScriptEnabled(true);
            fontSize = wv.getSettings().getDefaultFontSize();

            wv.setKeepScreenOn(true);

            wv.setWebViewClient(new CustomWebViewClient(btn));
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = MaduraiOrg.getFullUrlAssetsHTML(mItem.getUrl_html().next());

                    //update book : on recent list
                    MaduraiRecentBook.getManager(getActivity()).add(mItem);

                    wv.setVisibility(View.VISIBLE);
                    wv.invalidate();
                    wv.loadUrl(url);

                    btn.setEnabled(false);
                    btn.setText(R.string.projmad_please_wait);
                }
            });

            wv.setOnScrollChangedCallback(new ObservableWebView.OnScrollChangedCallback(){
                public void onScroll(int l, int t){
                    //Do stuff
                    Log.d(TAG,"We Scrolled by "+l+" and t = "+t);
                }
            });

            if ( m_open_book ) {
                Log.d(TAG,"Book requested to be opened");
                // delayed open book
                wv.setVisibility(View.VISIBLE);
                wv.invalidate();
                String url = MaduraiOrg.getFullUrlAssetsHTML(mItem.getUrl_html().next());
                wv.loadUrl(url);
            } else {
                Log.d(TAG,"Book requested cannot be opened");
            }
        } else {
            Log.d(TAG, "mItem - cannot be loaded - some error occured on dispatch");
        }

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(getActivity(), new Intent(getActivity(), BookListActivity.class));
            return true;

        }

        if ( id == R.id.action_font_increase) {
            fontSizePlus();
            return true;
        }

        if ( id == R.id.action_font_decrease ) {
            fontSizeMinus();
            return true;
        }

        /**
         if ( id == R.id.action_bookmark_toggled ) {
         if ( fragment ) fragment.changeBookmark(item);
         return true;
         }*/

        return super.onOptionsItemSelected(item);
    }

    /// options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.activity_book_detail, menu);
        return true;
    }
}
