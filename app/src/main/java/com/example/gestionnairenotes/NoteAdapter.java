package com.example.gestionnairenotes;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    public interface OnNoteInteractionListener {
        void onNoteClick(Note note);
        void onNoteDoubleClick(Note note);
    }

    private List<Note> noteList;
    private final OnNoteInteractionListener listener;

    private long lastClickTime = 0;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable singleClickRunnable;

    public NoteAdapter(List<Note> noteList, OnNoteInteractionListener listener) {
        this.noteList = noteList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);

        holder.tvTitle.setText(note.getTitle());
        holder.tvDate.setText(note.getCreatedAt());

        try {
            holder.container.setBackgroundColor(Color.parseColor(note.getColor()));
        } catch (IllegalArgumentException e) {
            holder.container.setBackgroundColor(Color.parseColor("#828282"));
        }

        if (note.isFavorite()) {
            holder.ivStar.setVisibility(View.VISIBLE);
        } else {
            holder.ivStar.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            long currentTime = System.currentTimeMillis();
            long elapsed = currentTime - lastClickTime;

            if (elapsed < 300) {
                handler.removeCallbacks(singleClickRunnable);
                listener.onNoteDoubleClick(note);
                lastClickTime = 0;
            } else {
                lastClickTime = currentTime;
                singleClickRunnable = () -> listener.onNoteClick(note);
                handler.postDelayed(singleClickRunnable, 300);
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public void updateList(List<Note> newList) {
        this.noteList = newList;
        notifyDataSetChanged();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {

        final RelativeLayout container;
        final TextView tvTitle;
        final TextView tvDate;
        final ImageView ivStar;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.noteContainer);
            tvTitle   = itemView.findViewById(R.id.tvNoteTitle);
            tvDate    = itemView.findViewById(R.id.tvNoteDate);
            ivStar    = itemView.findViewById(R.id.ivFavoriteStar);
        }
    }
}