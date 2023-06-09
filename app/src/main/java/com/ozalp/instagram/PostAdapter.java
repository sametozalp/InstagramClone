package com.ozalp.instagram;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ozalp.instagram.databinding.RecycleRowBinding;
import com.ozalp.instagram.pages.MyProfile;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

    private ArrayList<Post> postArrayList;

    public PostAdapter(ArrayList<Post> postArrayList) {
        this.postArrayList = postArrayList;
    }

    @NonNull
    @Override    //Creates a view for each list item.
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecycleRowBinding recycleRowBinding = RecycleRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new PostHolder(recycleRowBinding);
    }

    @Override   //binds data to views.
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        holder.recycleRowBinding.email.setText(postArrayList.get(position).username);
        holder.recycleRowBinding.username.setText(postArrayList.get(position).username);

        long c = Long.parseLong(String.valueOf(postArrayList.get(position).date.substring(18,28)));
        c = c * 1000;
        Timestamp a = new Timestamp(c);
        holder.recycleRowBinding.date.setText(a.toString().substring(0,a.toString().indexOf(".")-3));

        if(!postArrayList.get(position).comment.isEmpty()){
            holder.recycleRowBinding.comment.setText(postArrayList.get(position).comment);
        }else {
            holder.recycleRowBinding.comment.setVisibility(View.GONE);
        }
        Picasso.get().load(postArrayList.get(position).downloadUri).into(holder.recycleRowBinding.image);
        Picasso.get().load(postArrayList.get(position).profilePhoto).into(holder.recycleRowBinding.profilePhoto);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(holder.recycleRowBinding.username.getText());
                Intent intent = new Intent(holder.itemView.getContext(), MyProfile.class);
                intent.putExtra("sendUsername", holder.recycleRowBinding.username.getText());
                holder.itemView.getContext().startActivity(intent);
            }
        });
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
