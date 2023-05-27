package com.ozalp.instagram;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ozalp.instagram.databinding.FollowerRowBinding;

import java.util.ArrayList;
import java.util.List;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.FollowHolder> {
    List followList = new ArrayList();

    public FollowAdapter(List followList){
        this.followList = followList;
    }

    @NonNull
    @Override
    public FollowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FollowerRowBinding followerRowBinding = FollowerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new FollowHolder(followerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowHolder holder, int position) {
        System.out.println("follow list: " + followList);
        holder.followerRowBinding.followId.setText(followList.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return followList.size();
    }

    public class FollowHolder extends RecyclerView.ViewHolder {

        FollowerRowBinding followerRowBinding;
        public FollowHolder(FollowerRowBinding followerRowBinding) {
            super(followerRowBinding.getRoot());
            this.followerRowBinding = followerRowBinding;
        }
    }
}
