package com.example.incredibly.smarttodo.adaptor;

import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.example.incredibly.smarttodo.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import butterknife.Bind;
import butterknife.ButterKnife;

public class CategoryAdaptor extends RecyclerView.Adapter<CategoryAdaptor.VH> {

    private List<String> mCategorys;
    private OnItemClickListener mOnItemClickListener;

    public CategoryAdaptor(Set<String> items) {
        if (items == null) {
            mCategorys = new ArrayList<>();
        } else {
            this.mCategorys = new ArrayList<>(items);
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        ButterKnife.bind(this, view);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        final String category = mCategorys.get(position);
        holder.textCategoryTitle.setText(category);
        holder.textCategoryStatistic.setText(category);
    }

    @Override
    public int getItemCount() {
        return mCategorys.size();
    }

    public void replaceData(Set<String> items) {
        if (items == null) return;
        this.mCategorys = new ArrayList<>(items);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(String item, int position, Pair<View, String> pair);
        void onItemLongClick(String item, int position);
        void onItemSelected(String item, int position, boolean isChecked);
    }

    public class VH extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, View.OnLongClickListener {

        @Bind(R.id.text_category_title) TextView textCategoryTitle;
        @Bind(R.id.text_category_statistic) TextView textCategoryStatistic;
        @Bind(R.id.radio_category_checked) CheckBox radioCategoryChecked;

        public VH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            radioCategoryChecked.setOnCheckedChangeListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            String item = mCategorys.get(position);
            if (mOnItemClickListener != null) {
                Pair<View, String> pair = Pair.create((View) textCategoryStatistic, "CATEGORY_STATISTIC");
                mOnItemClickListener.onItemClick(item, position, pair);
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int position = getAdapterPosition();
            String item = mCategorys.get(position);
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemSelected(item, position, isChecked);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            String item = mCategorys.get(position);
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemLongClick(item, position);
            }
            return true;
        }
    }

}

