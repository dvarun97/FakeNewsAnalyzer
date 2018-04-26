package com.fakestudios.devs.fakenewsanalyzer;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class HIstoryItemAdapter  extends ArrayAdapter<HistoryItem> implements View.OnClickListener{

    private ArrayList<HistoryItem> historyItems;
    Context mContext;



    // View lookup cache
    private static class ViewHolder {
        TextView url;
    }

    public HIstoryItemAdapter(ArrayList<HistoryItem> data, Context context) {
        super(context, R.layout.history_item, data);
        this.historyItems = data;
        this.mContext=context;
    }

    @Override
    public void onClick(View v) {

//        int position=(Integer) v.getTag();
//        Object object= getItem(position);
//        HistoryItem HistoryItem=(HistoryItem)object;
//
//        switch (v.getId())
//        {
//            case R.id.history_item_url_tv:
//                Snackbar.make(v, ((HistoryItem) object).getUrl(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();
//                break;
//        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        HistoryItem HistoryItem = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.history_item, parent, false);
            viewHolder.url = (TextView) convertView.findViewById(R.id.history_item_url_tv);
            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        try {
            if (HistoryItem != null) {
                viewHolder.url.setText(SourceInputActivity.getDomainName((HistoryItem.getUrl())));
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        viewHolder.url.setOnClickListener(this);
        viewHolder.url.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}