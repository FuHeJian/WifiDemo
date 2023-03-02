package com.example.wifidemo1.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseDataBindingAdapter<T> extends ListAdapter<T,RecyclerView.ViewHolder> {

    ///ssssssssssssssss
    protected BaseDataBindingAdapter(@NonNull DiffUtil.ItemCallback diffCallback) {
        super(diffCallback);
    }

    protected BaseDataBindingAdapter(@NonNull AsyncDifferConfig config) {
        super(config);
    }

    class  VH extends RecyclerView.ViewHolder{
        public VH(@NonNull View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding view = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),getLayoutId(),parent,false);
        return new VH(view.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
       ViewDataBinding binding = DataBindingUtil.getBinding(holder.itemView);
       if(binding!=null){
           onBindItem(binding,getItem(position),position);
       }
    }

    abstract void onBindItem(ViewDataBinding binding,T item,int position);

    abstract @LayoutRes int getLayoutId();

}
