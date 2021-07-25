package com.example.omen.onboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>{
    int[] layouts;
    int current;
    public ViewPagerAdapter(int[] layouts){
       this.layouts =layouts;
       this.current=layouts[0];
    }

    public class ViewPagerViewHolder extends RecyclerView.ViewHolder{
        public ViewPagerViewHolder(View itemView){
            super(itemView);
        }

    }

    @NonNull
    @Override
    public ViewPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       //first it will set initial layout ie 1st in layout array
       //then onbind will set current to next layout
        View view= LayoutInflater.from(parent.getContext()).inflate(current,parent,false);
        return new ViewPagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerViewHolder holder, int position) {
      if(position<getItemCount()-1)//for last layout in array we dont want to increment
        current=layouts[position+1];
    }

    @Override
    public int getItemCount() {
        return layouts.length;
    }
}
