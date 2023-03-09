package com.example.wifidemo1.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 用于Fragment RecyclerView.Adapter的基类
 *
 * @author: fuhejian
 * @date: 2023/3/8
 */
public class BaseDataBindingFragmentAdapter extends FragmentStateAdapter {

    private final LinkedList<Fragment> mFragments = new LinkedList<>();

    int mFragmentsSize = 0;

    public BaseDataBindingFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        this(fragmentActivity.getSupportFragmentManager(), fragmentActivity.getLifecycle());
    }

    public BaseDataBindingFragmentAdapter(@NonNull Fragment fragment) {
        this(fragment.getChildFragmentManager(), fragment.getLifecycle());
    }

    //传lifeCycle进来，用来在销毁时释放资源
    public BaseDataBindingFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        lifecycle.addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event.equals(Lifecycle.Event.ON_DESTROY)) {
                    mFragments.clear();
                    lifecycle.removeObserver(this);
                }
            }
        });
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (mFragments.size() > 0) {
            Fragment frag = mFragments.getFirst();
            mFragments.removeFirst();
            return frag;
        } else {
            return new Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return mFragmentsSize;
    }

    public void addFragment(Fragment fragment) {
        if (fragment != null) {
            mFragments.add(fragment);
            notifyItemChanged(mFragmentsSize);
            mFragmentsSize++;
        }
    }

}
