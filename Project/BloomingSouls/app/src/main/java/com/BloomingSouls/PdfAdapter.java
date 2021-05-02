package com.BloomingSouls;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PdfAdapter extends RecyclerView.ViewHolder {
    TextView textView;
    ImageView thumbnail;

    private Clicklistener mClickListener;

    public PdfAdapter(@NonNull View itemView) {
        super(itemView);

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.onItemLongClick(view, getAdapterPosition());
                return false;
            }
        });
    }

    public void setPdf(String pdfUrl, String title, String imageUrl) {
        textView = itemView.findViewById(R.id.tv_item_name);
        thumbnail = itemView.findViewById(R.id.thumbnail);

        Glide.with(itemView.getContext())
                .load(imageUrl)
                .transition(GenericTransitionOptions.with(R.anim.an_fadein))
                .into(thumbnail);
        textView.setText(title);

        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), PdfView.class);
            intent.putExtra("Url", pdfUrl);
            view.getContext().startActivity(intent);
        });
    }

    public void setOnClicklistener(Clicklistener clicklistener) {
        mClickListener = clicklistener;
    }

    public interface Clicklistener {
        void onItemLongClick(View view, int position);
    }
}

