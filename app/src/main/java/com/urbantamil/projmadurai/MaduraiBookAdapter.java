/*
This code is released under MIT License
(C) 2016-2017, Ezhil Language Foundation
<ezhillang@gmail.com>
*/
package com.urbantamil.projmadurai;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.tamil.utf8;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by muthu on 3/19/2016.
 */
public class MaduraiBookAdapter extends ArrayAdapter<MaduraiBook> implements Button.OnClickListener {
    static final String TAG = MaduraiBookAdapter.class.getName();

    Context mContext;
    int layoutResourceId;
    ArrayList<MaduraiBook> m_data;

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    // sort by title
 //   public int comparator_title(MaduraiBook a, MaduraiBook b) {
 //       return m_comparator.compare(a.getTitle(),b.getTitle());
 //   }

    public MaduraiBookAdapter(Context mContext, int layoutResourceId, ArrayList<MaduraiBook> data) {

        super(mContext, layoutResourceId, data);
   //     this.m_comparator = utf8.comparator;
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.m_data = data;
    }

    @Override
    public void onClick(View v) {

        MaduraiBook book = (MaduraiBook)v.getTag();
        //Toast.makeText(v.getContext(), "Clicked item ->" + book.toString(), Toast.LENGTH_LONG).show();
        Context context = v.getContext();
        Intent intent = new Intent(context, BookDetailActivity.class);

        try {
            intent.putExtra(BookDetailFragment.ARG_ITEM_JSON, book.SaveToJSON());
            context.startActivity(intent);
        } catch (Exception e) {
            Log.d(TAG, "Exception at inner handler for search start activity");
            Log.d(TAG, "-> message was " + e.toString());
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        // object item based on the position
        MaduraiBook book = m_data.get(position);

        TextView author = (TextView) convertView.findViewById(R.id.adapter_maduraibook_author);
        TextView title = (TextView) convertView.findViewById(R.id.adapter_maduraibook_title);
        TextView genre = (TextView) convertView.findViewById(R.id.adapter_maduraibook_genre);

        author.setText(book.getAuthor());
        title.setText(book.getTitle());
        genre.setText(book.getGenre());

        Button btn = (Button) convertView.findViewById(R.id.adapter_maduraibook_button);
        btn.setOnClickListener(this);
        btn.setTag(book);

        return convertView;
    }
}
