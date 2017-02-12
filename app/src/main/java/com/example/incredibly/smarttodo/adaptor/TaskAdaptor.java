package com.example.incredibly.smarttodo.adaptor;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.incredibly.smarttodo.App;
import com.example.incredibly.smarttodo.R;
import com.example.incredibly.smarttodo.model.Notify;
import com.example.incredibly.smarttodo.model.Review;
import com.example.incredibly.smarttodo.model.Task;
import com.example.incredibly.smarttodo.util.Constant;
import com.example.incredibly.smarttodo.util.Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TaskAdaptor extends RecyclerView.Adapter<TaskAdaptor.VH> {

    private static final int TYPE_IMPORTANT_EASY = 1;
    private static final int TYPE_IMPORTANT_HARD = 2;
    private static final int TYPE_EASY = 3;
    private static final int TYPE_HARD = 4;
    private static final int TYPE_DELAY = 5;
    private static final int TYPE_IMPORTANT_EASY_LABEL = 15;
    private static final int TYPE_IMPORTANT_HARD_LABEL = 11;
    private static final int TYPE_EASY_LABEL = 12;
    private static final int TYPE_HARD_LABEL = 13;
    private static final int TYPE_DELAY_LABEL = 14;

    private List<Task> tasks = new ArrayList<>();
    private List<Task> mImportantEasyTasks = new ArrayList<>();
    private List<Task> mImportantHardTasks = new ArrayList<>();
    private List<Task> mEasyTasks = new ArrayList<>();
    private List<Task> mHardTasks = new ArrayList<>();
    private List<Task> mDelayTasks = new ArrayList<>();
    private Set<Task> deleteTasks = new HashSet<>();
    boolean isDeleteState;

    public TaskAdaptor(List<Task> items) {
        if (items == null) items = new ArrayList<>();
        this.tasks = items;
        reBuildTasks();
    }

    @Override
    public int getItemViewType(int position) {
        int pos1 = 0;
        int pos2 = mImportantEasyTasks.size() + 1;
        int pos3 = mImportantEasyTasks.size() + mImportantHardTasks.size() + 2;
        int pos4 = mImportantEasyTasks.size() + mImportantHardTasks.size() + mEasyTasks.size() + 3;
        int pos5 = mImportantEasyTasks.size() + mImportantHardTasks.size() + mHardTasks.size() + mEasyTasks.size() + 4;
        if (position == pos1) {
            return TYPE_IMPORTANT_EASY_LABEL;
        } else if (position == pos2) {
            return TYPE_IMPORTANT_HARD_LABEL;
        } else if (position == pos3) {
            return TYPE_EASY_LABEL;
        } else if (position == pos4) {
            return TYPE_HARD_LABEL;
        } else if (position == pos5) {
            return TYPE_DELAY_LABEL;
        }
        if (position > pos1 && position < pos2) {
            return TYPE_IMPORTANT_EASY;
        } else if (position > pos2 && position < pos3) {
            return TYPE_IMPORTANT_HARD;
        } else if (position > pos3 && position < pos4) {
            return TYPE_EASY;
        } else if (position > pos4 && position < pos5) {
            return TYPE_HARD;
        } else {
            return TYPE_DELAY;
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == TYPE_IMPORTANT_EASY_LABEL || viewType == TYPE_IMPORTANT_HARD_LABEL
                || viewType == TYPE_EASY_LABEL || viewType == TYPE_HARD_LABEL || viewType == TYPE_DELAY_LABEL) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_label, parent, false);
            return new LabelHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
            return new ItemHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        int pos1 = 0;
        int pos2 = mImportantEasyTasks.size() + 1;
        int pos3 = mImportantEasyTasks.size() + mImportantHardTasks.size() + 2;
        int pos4 = mImportantEasyTasks.size() + mImportantHardTasks.size() + mEasyTasks.size() + 3;
        int pos5 = mImportantEasyTasks.size() + mImportantHardTasks.size() + mEasyTasks.size() + mHardTasks.size() + 4;
        int pos6 = mImportantEasyTasks.size() + mImportantHardTasks.size() + mEasyTasks.size() + mHardTasks.size() + mDelayTasks.size() + 5;
        int type = getItemViewType(position);
        Task item = null;
        if (type > 10) {
            prepareLabelItem(((LabelHolder) holder), type);
        } else if (type == TYPE_IMPORTANT_EASY) {
            item = mImportantEasyTasks.get(position - 1);
            prepareTaskItem(item, ((ItemHolder) holder));
        } else if (type == TYPE_IMPORTANT_HARD) {
            item = mImportantHardTasks.get(position - pos2 - 1);
            prepareTaskItem(item, ((ItemHolder) holder));
        } else if (type == TYPE_EASY) {
            item = mEasyTasks.get(position - pos3 - 1);
            prepareTaskItem(item, ((ItemHolder) holder));
        } else if (type == TYPE_HARD) {
            item = mHardTasks.get(position - pos4 - 1);
            prepareTaskItem(item, ((ItemHolder) holder));
        } else if (type == TYPE_DELAY) {
            item = mDelayTasks.get(position - pos5 - 1);
            prepareTaskItem(item, ((ItemHolder) holder));
        }
    }

    private void prepareTaskItem(Task item, final ItemHolder itemHolder) {
        if (item.isDone()) {
            long done = item.getDoneTime();
            String doneTime = Util.longToTimeString(done);
            itemHolder.dateText.setText(doneTime);
        } else {
            long created = item.getCreateTime();
            String createTime = Util.longToTimeString(created);
            itemHolder.dateText.setText(createTime);
        }
        itemHolder.titleText.setText(item.getContent());
        boolean important = item.isImportant();
        boolean hard = item.isHard();
        boolean useColorHint = App.getPrefsApi().getTaskColorHint(true);
        int mainContainerColor = -1;
        int stateColor = -1;
        if (important && hard) {
            stateColor = Constant.COLOR_IMPORTANT_HARD;
            mainContainerColor = Constant.COLOR_IMPORTANT_HARD_WEAK;
        } else if (!important && hard) {
            stateColor = Constant.COLOR_HARD;
            mainContainerColor = Constant.COLOR_HARD_WEAK;
        } else if (important && !hard) {
            stateColor = Constant.COLOR_IMPORTANT_EASY;
            mainContainerColor = Constant.COLOR_IMPORTANT_EASY_WEAK;
        } else if (!important && !hard) {
            stateColor = Constant.COLOR_EASY;
            mainContainerColor = Constant.COLOR_EASY_WEAK;
        }

        if (!useColorHint) {
            mainContainerColor = Constant.COLOR_TASK_INIT;
        }

        itemHolder.stateImage.setBackgroundColor(stateColor);
        if (isDeleteState) {
            itemHolder.taskDeleteChecked.setVisibility(View.VISIBLE);
            if (!deleteTasks.contains(item)) {
                itemHolder.containerLayout.setBackgroundColor(Constant.COLOR_TASK_INIT);
                itemHolder.taskDeleteChecked.setChecked(false);
            } else {
                itemHolder.taskDeleteChecked.setChecked(true);
                itemHolder.containerLayout.setBackgroundColor(Constant.COLOR_TASK_DONE);
            }
        } else {
            itemHolder.taskDeleteChecked.setVisibility(View.GONE);
            itemHolder.containerLayout.setBackgroundColor(mainContainerColor);
        }

        if (item.isDone()) {
            itemHolder.titleText.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            itemHolder.titleText.setTextColor(0xFF333333);
        } else {
            itemHolder.titleText.getPaint().setFlags(Paint.LINEAR_TEXT_FLAG);
            itemHolder.titleText.setTextColor(0xFF000000);
        }

        List<Notify> notifies = item.getNotifies();
        int repeat =item.getRepeat();
        List<Review> reviews = item.getReviews();
        boolean needNotify = notifies!=null;
        boolean needRepeat = (repeat==0);
        boolean needReview = reviews!=null;
        boolean isDelay = item.isDelay();
        String comment = item.getComment();

        if (!needNotify && !needRepeat && !needReview && TextUtils.isEmpty(comment)) {
            itemHolder.taskInfoLayout.setVisibility(View.GONE);
        } else {
            itemHolder.taskInfoLayout.setVisibility(View.VISIBLE);
            if (needNotify) {
                itemHolder.imageTaskNotify.setVisibility(View.VISIBLE);
            } else {
                itemHolder.imageTaskNotify.setVisibility(View.GONE);
            }

            if (needRepeat) {
                itemHolder.imageTaskRepeat.setVisibility(View.VISIBLE);
            } else {
                itemHolder.imageTaskRepeat.setVisibility(View.GONE);
            }

            if (needReview) {
                itemHolder.imageTaskReview.setVisibility(View.VISIBLE);
            } else {
                itemHolder.imageTaskReview.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(comment)) {
                itemHolder.imageTaskComment.setVisibility(View.VISIBLE);
            } else {
                itemHolder.imageTaskComment.setVisibility(View.GONE);
            }
        }

    }

    private void prepareLabelItem(LabelHolder holder, int type) {
        if (type == TYPE_IMPORTANT_EASY_LABEL) {
            holder.textLabel.setText(Constant.TYPE_IMPORTANT_EASY_LABEL);
            holder.textLabel.setVisibility((mImportantEasyTasks.size() == 0) ? View.GONE : View.VISIBLE);
        } else if (type == TYPE_IMPORTANT_HARD_LABEL) {
            holder.textLabel.setText(Constant.TYPE_IMPORTANT_HARD_LABEL);
            holder.textLabel.setVisibility((mImportantHardTasks.size() == 0) ? View.GONE : View.VISIBLE);
        } else if (type == TYPE_EASY_LABEL) {
            holder.textLabel.setText(Constant.TYPE_EASY_LABEL);
            holder.textLabel.setVisibility((mEasyTasks.size() == 0) ? View.GONE : View.VISIBLE);
        } else if (type == TYPE_HARD_LABEL) {
            holder.textLabel.setText(Constant.TYPE_HARD_LABEL);
            holder.textLabel.setVisibility((mHardTasks.size() == 0) ? View.GONE : View.VISIBLE);
        } else if (type == TYPE_DELAY_LABEL) {
            holder.textLabel.setText(Constant.TYPE_DONE_LABEL);
            holder.textLabel.setVisibility((mDelayTasks.size() == 0) ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mImportantEasyTasks.size() + mImportantHardTasks.size() + mEasyTasks.size() + mHardTasks.size() + mDelayTasks.size() + 5;
    }

    public void replaceData(List<Task> items) {
        if (items == null) {
            tasks.clear();
        } else {
            this.tasks = items;
        }
        reBuildTasks();
        notifyDataSetChanged();
    }

    public List<Task> getDeleteTasks() {
        List<Task> tasks = new ArrayList<>(deleteTasks);
        return tasks;
    }

    public int getDeleteTasksCount() {
        if (!isDeleteState || deleteTasks == null) {
            return 0;
        } else {
            return deleteTasks.size();
        }
    }

    public void setDeleteState(boolean isDeleteState) {
        this.isDeleteState = isDeleteState;
        notifyDataSetChanged();
    }

    public void addDeleteTask(int position) {
        Task task = getTaskFromPosition(position);
        if (task != null && !deleteTasks.contains(task)) {
            this.deleteTasks.add(task);
        }
    }

    public void toggleDeleteTask(int position) {
        Task task = getTaskFromPosition(position);
        if (task == null) {
            return;
        }
        if (!deleteTasks.contains(task)) {
            deleteTasks.add(task);
        } else {
          deleteTasks.remove(task);
        }
        notifyItemChanged(position);
    }

    public class VH extends RecyclerView.ViewHolder {

        public VH(View itemView) {
            super(itemView);
        }
    }

    public class LabelHolder extends VH {

        @Bind(R.id.text_label)
        TextView textLabel;

        public LabelHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class ItemHolder extends VH implements CompoundButton.OnCheckedChangeListener {

        @Bind(R.id.linear_main_container)
        LinearLayout containerLayout;
        @Bind(R.id.image_task_state)
        FrameLayout stateImage;
        @Bind(R.id.checked_task_delete)
        CheckBox taskDeleteChecked;
        @Bind(R.id.text_task_title)
        TextView titleText;
        @Bind(R.id.text_task_date)
        TextView dateText;

        @Bind(R.id.linear_task_info)
        LinearLayout taskInfoLayout;
        @Bind(R.id.image_task_notify) ImageView imageTaskNotify;
        @Bind(R.id.image_task_repeat) ImageView imageTaskRepeat;
        @Bind(R.id.image_task_review) ImageView imageTaskReview;
        @Bind(R.id.image_task_comment) ImageView imageTaskComment;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            taskDeleteChecked.setOnCheckedChangeListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int position = getAdapterPosition();
            Task item = getTaskFromPosition(position);
            if (isChecked) {
                if (!deleteTasks.contains(item)) {
                    deleteTasks.add(item);
                }
            } else {
                if (deleteTasks.contains(item)) {
                    deleteTasks.remove(item);
                }
            }
        }

    }

    private void reBuildTasks() {
        mImportantEasyTasks.clear();
        mImportantHardTasks.clear();
        mEasyTasks.clear();
        mHardTasks.clear();
        mDelayTasks.clear();
        int count = tasks.size();
        for (int i = 0; i < count; i++) {
            Task item = tasks.get(i);
            if (item.isDelay()) {
                mDelayTasks.add(item);
            } else {
                if (item.isImportant() && !item.isHard()) {
                    mImportantEasyTasks.add(item);
                } else if (item.isImportant() && item.isHard()) {
                    mImportantHardTasks.add(item);
                } else if (!item.isImportant() && !item.isHard()) {
                    mEasyTasks.add(item);
                } else if (!item.isImportant() && item.isHard()) {
                    mHardTasks.add(item);
                }
            }
        }
        Util.sortTasksByCreateTime(mImportantEasyTasks);
        Util.sortTasksByCreateTime(mImportantHardTasks);
        Util.sortTasksByCreateTime(mEasyTasks);
        Util.sortTasksByCreateTime(mEasyTasks);
        Util.sortTasksByDoneTime(mDelayTasks);
    }

    public Task getTaskFromPosition(int position) {
        int pos1 = 0;
        int pos2 = mImportantEasyTasks.size() + 1;
        int pos3 = mImportantEasyTasks.size() + mImportantHardTasks.size() + 2;
        int pos4 = mImportantEasyTasks.size() + mImportantHardTasks.size() + mEasyTasks.size() + 3;
        int pos5 = mImportantEasyTasks.size() + mImportantHardTasks.size() + mEasyTasks.size() + mHardTasks.size() + 4;
        int pos6 = mImportantEasyTasks.size() + mImportantHardTasks.size() + mEasyTasks.size() + mHardTasks.size() + mDelayTasks.size() + 5;
        if (position > pos1 && position < pos2) {
            return mImportantEasyTasks.get(position - 1);
        } else if (position > pos2 && position < pos3) {
            return mImportantHardTasks.get(position - pos2 - 1);
        } else if (position > pos3 && position < pos4) {
            return mEasyTasks.get(position - pos3 - 1);
        } else if (position > pos4 && position < pos5) {
            return mHardTasks.get(position - pos4 - 1);
        } else if (position == pos1 || position == pos2 || position == pos3 || position == pos4 || position == pos5) {
            return null;
        } else if (position > pos5 && position < pos6) {
            return mDelayTasks.get(position - pos5 - 1);
        } else {
            return mDelayTasks.get(pos6 - 1);
        }
    }

}
