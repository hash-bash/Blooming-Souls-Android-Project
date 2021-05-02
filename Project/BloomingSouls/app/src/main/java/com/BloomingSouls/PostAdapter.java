package com.BloomingSouls;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> implements Filterable {
    Context mContext;
    List<ClassBlogPost> mData;
    List<ClassBlogPost> mDataFiltered;

    public PostAdapter(Context mContext, List<ClassBlogPost> mData) {
        this.mContext = mContext;
        this.mData = mData;
        this.mDataFiltered = new ArrayList(mData);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.post_row_item, parent, false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(mContext).load(mDataFiltered.get(position).getPicture()).transition(GenericTransitionOptions.with(R.anim.an_fadein)).into(holder.imgPost);
        Glide.with(mContext).load(mDataFiltered.get(position).getUserPhoto()).transition(GenericTransitionOptions.with(R.anim.an_fadein)).into(holder.imgPostProfile);
        holder.tvTitle.setText(mDataFiltered.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return mDataFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<ClassBlogPost> filtered = new ArrayList<>();
                FilterResults filterResults = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    filterResults.count = mData.size();
                    filterResults.values = new ArrayList(mData);
                } else {
                    String key = constraint.toString().toLowerCase().trim();

                    for (ClassBlogPost row : mData) {
                        if (row.getTitle().toLowerCase().trim().contains(key)) {
                            filtered.add(row);
                        }
                    }
                    filterResults.values = filtered;
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mDataFiltered.clear();
                mDataFiltered.addAll((List<ClassBlogPost>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView imgPost;
        ImageView imgPostProfile;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.row_post_title);
            imgPost = itemView.findViewById(R.id.row_post_img);
            imgPostProfile = itemView.findViewById(R.id.row_post_profile_img);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent postDetailActivity = new Intent(mContext, PostDetailActivity.class);
                    int position = getAdapterPosition();
                    postDetailActivity.putExtra("title", mDataFiltered.get(position).getTitle());
                    postDetailActivity.putExtra("postImage", mDataFiltered.get(position).getPicture());
                    postDetailActivity.putExtra("description", mDataFiltered.get(position).getDescription());
                    postDetailActivity.putExtra("postKey", mDataFiltered.get(position).getPostKey());
                    postDetailActivity.putExtra("userPhoto", mDataFiltered.get(position).getUserPhoto());
                    long timestamp = (long) mDataFiltered.get(position).getTimeStamp();
                    postDetailActivity.putExtra("postDate", timestamp);
                    mContext.startActivity(postDetailActivity);
                }
            });
        }
    }
}
