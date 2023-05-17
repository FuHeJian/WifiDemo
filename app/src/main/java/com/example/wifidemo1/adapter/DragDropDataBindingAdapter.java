package com.example.wifidemo1.adapter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * @author: fuhejian
 * @date: 2023/5/10
 */
public class DragDropDataBindingAdapter extends RecyclerView.Adapter<DragDropDataBindingAdapter.DragDropViewHolder> {

    public ArrayList<View> datas;

    public DragDropDataBindingAdapter(ArrayList<View> views, ClickListener listener) {
        mListener = listener;
        datas = new ArrayList<>(views);
    }

    public static class DragDropViewHolder extends RecyclerView.ViewHolder {

        public DragDropViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public  View isoView;
    public View fView;
    public View sView;
    int p = 0;
    @NonNull
    @Override
    public DragDropViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

//        View view = StubviewBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false).getRoot();

        View view = datas.get(p);
        p++;

        return new DragDropViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DragDropViewHolder holder, int position) {

//        ViewStub viewStub  = holder.itemView.findViewById(R.id.stub);
//        viewStub.setLayoutResource();

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    private ClickListener mListener;

    public void setListener(ClickListener listener) {
        this.mListener = listener;
    }

    public interface ClickListener {
        void onClick(View view);

        void onCreateHolder(ViewDataBinding dataBinding);

    }

    public static class DrawableItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDrawable;

        public DrawableItemDecoration(Drawable drawable) {
            mDrawable = drawable;
        }

        private Paint paint = new Paint();
        int gap = 50;
        @Override
        public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

        }

        @Override
        public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            int leng = parent.getChildAt(0).getRight() + 50;

            c.drawCircle(leng,parent.getHeight()/2f,20,paint);
            leng = parent.getChildAt(1).getLeft();
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.right = gap;
        }
    }

    public static class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
        private  RecyclerView.Adapter ap;
        public ItemTouchHelperCallback(RecyclerView.Adapter a) {
            ap = a;
        }

        //线性布局和网格布局都可以使用
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFrlg = 0;
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager){
                dragFrlg = ItemTouchHelper.UP|ItemTouchHelper.DOWN|ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
            }else if(recyclerView.getLayoutManager() instanceof LinearLayoutManager){
                dragFrlg = ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
            }
            return makeMovementFlags(dragFrlg,0);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            //得到当拖拽的viewHolder的Position  每一次Position改变，该方法都回调
            int fromPosition = viewHolder.getAdapterPosition();
            //拿到当前拖拽到的item的viewHolder
            int toPosition = target.getAdapterPosition();

            ap.notifyItemMoved(fromPosition, toPosition);
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
//                viewHolder.itemView.setBackgroundColor(Color.RED);
                viewHolder.itemView.setScaleX(1.2f);
                viewHolder.itemView.setScaleY(1.2f);
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
//            viewHolder.itemView.setBackgroundColor(Color.parseColor("#21f1f1"));
            viewHolder.itemView.setScaleX(1.0f);
            viewHolder.itemView.setScaleY(1.0f);
            ap.notifyDataSetChanged();  //完成拖动后刷新适配器，这样拖动后删除就不会错乱
        }
    }

}
