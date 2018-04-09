package com.OmnifyTask.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.OmnifyTask.R;
import com.OmnifyTask.databaseTable.StoryDetails;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.RealmList;

/**
 * Created by chavali on 2018-04-08.
 */

public class StoryListAdapter extends RecyclerView.Adapter<StoryListAdapter.ViewHolder> {

    Context context;
    RealmList<StoryDetails> mdata;
    private OnItemClickListener mOnItemClickListener;


    public StoryListAdapter(Context context, RealmList<StoryDetails> mdata) {
        this.context = context;
        this.mdata = mdata;
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.story_list_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.votes.setText(mdata.get(position).getScore() + "");
        holder.title.setText(mdata.get(position).getTitle());
        holder.url.setText(mdata.get(position).getUrl());
        holder.author.setText("." + mdata.get(position).getBy());
        holder.comments.setText(mdata.get(position).getComments().size() + "");


        // convert seconds to milliseconds
        Date date = new java.util.Date(mdata.get(position).getTime() * 1000L);
        // the format of your date
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // give a timezone reference for formatting (see comment at the bottom)
        //        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+5:30"));
        String formattedDate = sdf.format(date);
        System.out.println(formattedDate);
        holder.time.setText(formattedDate + "");

    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, String articleId);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView votes, title, url, time, author, comments;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            votes = itemView.findViewById(R.id.votes);
            time = itemView.findViewById(R.id.time);
            title = itemView.findViewById(R.id.title);
            url = itemView.findViewById(R.id.url);
            author = itemView.findViewById(R.id.author);
            comments = itemView.findViewById(R.id.comments);

        }

        @Override
        public void onClick(View view) {
            Log.e("View", String.valueOf(view) + getAdapterPosition());
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(view, String.valueOf(mdata.get(getAdapterPosition()).getId()));
            }
        }
    }

}
