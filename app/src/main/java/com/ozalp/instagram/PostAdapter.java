package com.ozalp.instagram;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.ozalp.instagram.databinding.RecycleRowBinding;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

    private ArrayList<Post> postArrayList;

    public PostAdapter(ArrayList<Post> postArrayList) {
        this.postArrayList = postArrayList;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecycleRowBinding recycleRowBinding = RecycleRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new PostHolder(recycleRowBinding);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        holder.recycleRowBinding.email.setText(postArrayList.get(position).username);
        holder.recycleRowBinding.username.setText(postArrayList.get(position).username);
        //postArrayList.get(position).date

        holder.recycleRowBinding.date.setText(postArrayList.get(position).date);
        if(!postArrayList.get(position).comment.isEmpty()){
            holder.recycleRowBinding.comment.setText(postArrayList.get(position).comment);
        }else {
            holder.recycleRowBinding.comment.setVisibility(View.GONE);
        }
        Picasso.get().load(postArrayList.get(position).downloadUri).into(holder.recycleRowBinding.image);
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder{

        RecycleRowBinding recycleRowBinding;

        public PostHolder(RecycleRowBinding recycleRowBinding) {
            super(recycleRowBinding.getRoot());
            this.recycleRowBinding = recycleRowBinding;
        }
    }
}
