package com.example.wifidemo1.fragment.impl;

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.wifidemo1.databinding.TestFragmentBinding;
import com.example.wifidemo1.fragment.adapter.ScaleAdapter;
import com.example.wifidemo1.fragment.i.FragmentInitView;

import java.util.ArrayList;

/**
 * @author: fuhejian
 * @date: 2023/3/21
 */
public class TestFragmentInitViewImpl implements FragmentInitView<TestFragmentBinding> {

    @Override
    public void initView(TestFragmentBinding binding, LifecycleOwner lifecycleOwner) {

        binding.searchSuggest.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext(),LinearLayoutManager.VERTICAL,false));

        ScaleAdapter scaleAdapter = new ScaleAdapter(lifecycleOwner);

        ArrayList<String> testList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            testList.add(String.valueOf(i));
        }
        binding.searchSuggest.setAdapter(scaleAdapter);

        scaleAdapter.submitList(testList);

    }

}
