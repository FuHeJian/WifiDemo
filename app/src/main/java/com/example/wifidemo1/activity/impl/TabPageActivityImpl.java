package com.example.wifidemo1.activity.impl;

import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import com.example.wifidemo1.R;
import com.example.wifidemo1.activity.i.InitView;
import com.example.wifidemo1.adapter.ViewPager2FragmentAdapter;
import com.example.wifidemo1.databinding.TabPageBinding;
import com.example.wifidemo1.fragment.HomeFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import cn.bingoogolapple.badgeview.BGABadgeTextView;

/**
 * @author: fuhejian
 * @date: 2023/5/30
 */
public class TabPageActivityImpl implements InitView<TabPageBinding> {
    @Override
    public void initView(TabPageBinding binding, LifecycleOwner lifecycleOwner) {
//      TabLayout.Tab tab1 = binding.tab.newTab();
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(binding.tab, binding.viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                BGABadgeTextView view = (BGABadgeTextView) LayoutInflater.from(tab.view.getContext()).inflate(R.layout.tab_view, binding.tab, false);
                view.showTextBadge("tab" + position);
//                view.showCirclePointBadge();
                view.setText("tab" + position);
                tab.setCustomView(view);
            }
        });

        binding.tab.setSelectedTabIndicator(null);

/*      binding.tab.addTab(tab1);
        TabLayout.Tab tab2 = binding.tab.newTab();
        binding.tab.addTab(tab2);*/

        ViewPager2FragmentAdapter adapter = new ViewPager2FragmentAdapter(((AppCompatActivity) (binding.getRoot().getContext())).getSupportFragmentManager(), lifecycleOwner.getLifecycle());

        adapter.addFragment(new HomeFragment());
        adapter.addFragment(new HomeFragment());
        adapter.addFragment(new HomeFragment());

        binding.viewPager2.setAdapter(adapter);

        tabLayoutMediator.attach();

    }
}
