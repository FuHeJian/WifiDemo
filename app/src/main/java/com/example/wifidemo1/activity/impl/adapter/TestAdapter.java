package com.example.wifidemo1.activity.impl.adapter;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.wifidemo1.R;
import com.example.wifidemo1.adapter.BaseDataBindingAdapter2;
import com.example.wifidemo1.databinding.ImageItemBinding;

/**
 * @author: fuhejian
 * @date: 2023/5/26
 */
public class TestAdapter extends BaseDataBindingAdapter2<Uri> {

    public TestAdapter(@NonNull DiffUtil.ItemCallback<Uri> diffCallback, @NonNull LifecycleOwner lifecycleOwner) {
        super(diffCallback, lifecycleOwner);
    }

    RequestOptions requestOptions = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    private RequestManager mGlide;

    @Override
    public void onBindItem(ViewDataBinding binding, Uri item, int position) {
        try {
            System.out.println("当前加载位置：" + position);
            if (mGlide == null) {
                mGlide = Glide.with(binding.getRoot());
            }
            mGlide.load(item).apply(requestOptions).into(((ImageItemBinding) binding).img);
        } catch (Exception e) {

        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.image_item;
    }

}
