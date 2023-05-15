package com.example.wifidemo1.activity.impl;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wifidemo1.activity.i.InitView;
import com.example.wifidemo1.databinding.CompassMainBinding;
import com.example.wifidemo1.databinding.TestBinding;

import java.util.ArrayList;

/**
 * com.example.wifidemo1.activity.impl
 */
public class CompassActivityInitViewImpl implements InitView<CompassMainBinding> {

    private boolean isCompute = false;


    @SuppressLint("RestrictedApi")
    @Override
    public void initView(CompassMainBinding binding, LifecycleOwner lifecycleOwner) {

        View view = TestBinding.inflate(LayoutInflater.from(binding.getRoot().getContext())).getRoot();
        ArrayList<View> data = new ArrayList<>();


        data.add(view);
        data.add(TestBinding.inflate(LayoutInflater.from(binding.getRoot().getContext())).getRoot());
        data.add(TestBinding.inflate(LayoutInflater.from(binding.getRoot().getContext())).getRoot());
        data.add(TestBinding.inflate(LayoutInflater.from(binding.getRoot().getContext())).getRoot());
        data.add(TestBinding.inflate(LayoutInflater.from(binding.getRoot().getContext())).getRoot());

        Adapter adapter = new Adapter(new DiffUtil.ItemCallback() {
            @Override
            public boolean areItemsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
                return false;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
                return false;
            }
        });

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            //线性布局和网格布局都可以使用
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFrlg = 0;
                if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    dragFrlg = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                } else if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                    dragFrlg = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                }
                return makeMovementFlags(dragFrlg, 0);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //得到当拖拽的viewHolder的Position  每一次Position改变，该方法都回调
                int fromPosition = viewHolder.getAdapterPosition();
                //拿到当前拖拽到的item的viewHolder
                int toPosition = target.getAdapterPosition();
/*                if (fromPosition < toPosition) {
                    if (toPosition < data.size() - 1) {//此处表明最后一个item不可替换，一般最后一个item是添加更多图片+
                        for (int i = fromPosition; i < toPosition; i++) {
                            Collections.swap(data, i, i + 1);
                        }
                        adapter.notifyItemMoved(fromPosition, toPosition);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(data, i, i - 1);
                    }

                }*/
                adapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //侧滑删除可以使用；
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            /**
             * 长按选中Item的时候开始调用
             * 长按高亮
             * @param viewHolder
             * @param actionState
             */
            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    viewHolder.itemView.setBackgroundColor(Color.RED);
                    viewHolder.itemView.setScaleX(1.2f);
                    viewHolder.itemView.setScaleY(1.2f);
                    //获取系统震动服务//震动70毫秒
//                Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
//                vib.vibrate(70);
                }
                super.onSelectedChanged(viewHolder, actionState);
            }

            /**
             * 手指松开的时候还原高亮
             * @param recyclerView
             * @param viewHolder
             */
            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setBackgroundColor(Color.parseColor("#21f1f1"));
                viewHolder.itemView.setScaleX(1.0f);
                viewHolder.itemView.setScaleY(1.0f);
//                adapter.notifyDataSetChanged();  //完成拖动后刷新适配器，这样拖动后删除就不会错乱
            }
        });


        binding.recyclerView.setAdapter(adapter);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(binding.recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false));

        helper.attachToRecyclerView(binding.recyclerView);

        adapter.submitList(data);

    }

    public class Adapter extends ListAdapter<View,Adapter.ViewHolder> {

        public Adapter(@NonNull DiffUtil.ItemCallback diffCallback) {
            super(diffCallback);
        }

        @NonNull
        @Override
        public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(getItem(viewType));
        }

        @Override
        public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {

        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }


    }

}
