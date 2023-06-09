package com.example.wifidemo1.activity.impl.adapter;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;

import com.example.wifidemo1.R;
import com.example.wifidemo1.adapter.BaseDataBindingAdapter;

/**
 * @author: fuhejian
 * @date: 2023/5/26
 */
public class TestAdapter extends BaseDataBindingAdapter<String> {

    public TestAdapter(@NonNull DiffUtil.ItemCallback<String> diffCallback, @NonNull LifecycleOwner lifecycleOwner) {
        super(diffCallback, lifecycleOwner);
    }

    @Override
    public void onBindItem(ViewDataBinding binding, String item, int position) {

    }

    @Override
    public int getLayoutId(int position) {
        return R.layout.bluetoothdeviceitem;
    }

}
