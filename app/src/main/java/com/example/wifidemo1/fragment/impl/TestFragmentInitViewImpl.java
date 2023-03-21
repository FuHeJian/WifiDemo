package com.example.wifidemo1.fragment.impl;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;

import com.example.wifidemo1.databinding.TestFragmentBinding;
import com.example.wifidemo1.fragment.adapter.ScaleAdapter;
import com.example.wifidemo1.fragment.base.BaseDataBindingFragment;
import com.example.wifidemo1.fragment.i.FragmentInitView;

import java.util.ArrayList;

/**
 * @author: fuhejian
 * @date: 2023/3/21
 */
public class TestFragmentInitViewImpl implements FragmentInitView<TestFragmentBinding> {

    @Override
    public void initView(TestFragmentBinding binding, LifecycleOwner lifecycleOwner) {
        ScaleAdapter scaleAdapter = new ScaleAdapter(lifecycleOwner);
        binding.scaleContainer.setAdapter(scaleAdapter);
        binding.scaleContainer.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext(),LinearLayoutManager.HORIZONTAL,false));

        LinearSnapHelper linearSnapHelper = new LinearSnapHelper();
        linearSnapHelper.attachToRecyclerView(binding.scaleContainer);

        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(String.valueOf(i+1));
        }
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        scaleAdapter.submitList(list);

    }

}
