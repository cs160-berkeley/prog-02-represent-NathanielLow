package com.example.nathaniel.represent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by Nathaniel on 3/9/2016.
 */
public class DocumentListAdapter extends BaseAdapter {

    private Context context;
    private String[] documents;

    public DocumentListAdapter(Context con, String[] docs) {
        context = con;
        documents = docs;
    }

    @Override
    public int getCount() {
        return documents.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View docList = inflater.inflate(R.layout.document_list_entries, parent, false);
        TextView document = (TextView) docList.findViewById(R.id.document);
        document.setText(documents[position]);
        return docList;
    }
}
