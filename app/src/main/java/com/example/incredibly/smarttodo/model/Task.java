package com.example.incredibly.smarttodo.model;
import org.joda.time.DateTime;
import java.util.List;
import cn.bmob.v3.BmobObject;
public class Task extends BmobObject {

    public static final int TIME_TYPE_NONE = 1;
    public static final int TIME_TYPE_DURATION = 2;
    public static final int TIME_TYPE_FIX = 3;

    private int id;
    private boolean important;
    private boolean hard;
    private String content;
    private String category;
    private String comment;
    private long updateTime;
    private long createTime;
    private long countDownTime;
    private long executeDuration;
    private long doneTime;

    private int executeTimeType;
    private long executeTime;
    private long executeStartTime;
    private long executeEndTime;
    private int repeat;

    private List<Notify> notifies;
    private List<Review> reviews;

    private MyUser myUser;

    public Task() {
        executeTimeType = TIME_TYPE_NONE;
        important = true;
        hard = false;
        category = Category.CATEGORY_DEFAULT;
        createTime = System.currentTimeMillis();
        updateTime = System.currentTimeMillis();

        countDownTime = 0;
        executeDuration = 0;
        executeTime = -1;
        doneTime = -1;
        repeat = 0;
        id = -1;
        executeStartTime = new DateTime().withTimeAtStartOfDay().getMillis();
        executeEndTime = new DateTime().plusDays(1).withTimeAtStartOfDay().getMillis();
    }

    public Task clone() {
        if (this == null) return null;
        Task task = new Task();
        task.setId(getId());
        task.setHard(isHard());
        task.setImportant(isImportant());
        task.setCategory(getCategory());
        task.setCreateTime(getCreateTime());
        task.setUpdateTime(getUpdateTime());
        task.setExecuteTimeType(getExecuteTimeType());
        task.setCountDownTime(getCountDownTime());
        task.setExecuteDuration(getExecuteDuration());
        task.setExecuteTime(getExecuteTime());
        task.setComment(getComment());
        task.setDoneTime(getDoneTime());
        task.setContent(getContent());
        task.setMyUser(getMyUser());
        return task;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public boolean isDelay() {
        return (doneTime==-1L && System.currentTimeMillis()>executeEndTime) || (doneTime!=-1L && doneTime>executeEndTime);
    }

    public boolean isDone() {
        return (doneTime!=-1L);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public boolean isHard() {
        return hard;
    }

    public void setHard(boolean hard) {
        this.hard = hard;
    }

    public String getContent() {
        return content;
    }

    public List<Notify> getNotifies() {
        return notifies;
    }

    public void setNotifies(List<Notify> notifies) {
        this.notifies = notifies;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getCountDownTime() {
        return countDownTime;
    }

    public void setCountDownTime(long countDownTime) {
        this.countDownTime = countDownTime;
    }

    public long getExecuteDuration() {
        return executeDuration;
    }

    public void setExecuteDuration(long executeDuration) {
        this.executeDuration = executeDuration;
    }

    public long getDoneTime() {
        return doneTime;
    }

    public void setDoneTime(long doneTime) {
        this.doneTime = doneTime;
    }

    public int getExecuteTimeType() {
        return executeTimeType;
    }

    public void setExecuteTimeType(int executeTimeType) {
        this.executeTimeType = executeTimeType;
    }

    public long getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(long executeTime) {
        this.executeTime = executeTime;
    }

    public MyUser getMyUser() {
        return myUser;
    }

    public void setMyUser(MyUser myUser) {
        this.myUser = myUser;
    }

    public long getExecuteStartTime() {
        return executeStartTime;
    }

    public void setExecuteStartTime(long executeStartTime) {
        this.executeStartTime = executeStartTime;
    }

    public long getExecuteEndTime() {
        return executeEndTime;
    }

    public void setExecuteEndTime(long executeEndTime) {
        this.executeEndTime = executeEndTime;
    }
}
