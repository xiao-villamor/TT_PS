package es.udc.psi.tt_ps.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import es.udc.psi.tt_ps.R;

public class tagAdapter extends BaseAdapter {
    private String[] tags;

    public tagAdapter(String[] tags) {
        this.tags = tags;
    }

    @Override
    public int getCount() {
        return tags.length;
    }

    @Override
    public Object getItem(int position) {
        return tags[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.taglist_item, null);
        }
        TextView tag = view.findViewById(R.id.chip);
        tag.setText(tags[position]);
        return view;
    }
}

