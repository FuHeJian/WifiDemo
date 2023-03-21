package com.example.wifidemo1.fragment.adapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;

import com.example.wifidemo1.R;
import com.example.wifidemo1.adapter.BaseDataBindingAdapter;
import com.example.wifidemo1.databinding.GraduatedItemBinding;

/**
 * @author: fuhejian
 * @date: 2023/3/21
 */
public class ScaleAdapter extends BaseDataBindingAdapter<String> {

    public ScaleAdapter(@NonNull LifecycleOwner lifecycleOwner) {
        super(new DiffUtil.ItemCallback<String>() {
            @Override
            public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                return true;
            }
        }, lifecycleOwner);
    }

    public ScaleAdapter(@NonNull AsyncDifferConfig<String> config, @NonNull LifecycleOwner lifecycleOwner) {
        super(config, lifecycleOwner);
    }

    @Override
    public void onBindItem(ViewDataBinding binding, String item, int position) {
        ((GraduatedItemBinding)binding).scale.setText(item);
    }

    @Override
    public int getLayoutId() {
        return R.layout.graduated_item;
    }

}
