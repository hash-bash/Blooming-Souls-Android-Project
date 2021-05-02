package com.BloomingSouls;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class YoutubeAdapter extends RecyclerView.ViewHolder {
    TextView textView;
    ImageView thumbnail, play;
    ProgressBar progress;
    Handler handler;

    private Clicklistener mClickListener;

    public YoutubeAdapter(@NonNull View itemView) {
        super(itemView);

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.onItemLongClick(view, getAdapterPosition());
                return false;
            }
        });
    }

    public void setYoutube(String videoId, String VideoName, String ImageUrl) {
        textView = itemView.findViewById(R.id.tv_item_name);
        thumbnail = itemView.findViewById(R.id.thumbnail);
        play = itemView.findViewById(R.id.btnPlay);
        progress = itemView.findViewById(R.id.progressBar);
        handler = new Handler();

        Glide.with(itemView.getContext())
                .load(ImageUrl)
                .transition(GenericTransitionOptions.with(R.anim.an_fadein))
                .into(thumbnail);
        textView.setText(VideoName);

        play.setVisibility(View.VISIBLE);
        progress.setVisibility(View.INVISIBLE);

        play.setOnClickListener(view -> {
            play.setVisibility(View.INVISIBLE);
            progress.setVisibility(View.VISIBLE);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(view.getContext(), YoutubeFullscreen.class);
                    intent.putExtra("id", videoId);
                    view.getContext().startActivity(intent);

                    play.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.INVISIBLE);
                }
            }, 1000);
        });
    }

    public void setOnClicklistener(Clicklistener clicklistener) {
        mClickListener = clicklistener;
    }

    public interface Clicklistener {
        void onItemLongClick(View view, int position);
    }
}

