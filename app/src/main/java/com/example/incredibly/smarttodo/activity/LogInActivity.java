package com.example.incredibly.smarttodo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.incredibly.smarttodo.R;
import com.example.incredibly.smarttodo.model.MyUser;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener, PlatformActionListener {

    @Bind(R.id.login_qq)
    ImageView loginQq;
    @Bind(R.id.login_weibo)
    ImageView loginWeibo;
    @Bind(R.id.login_wechart)
    ImageView loginWechart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        ButterKnife.bind(this);
        loginQq.setOnClickListener(this);
        loginWeibo.setOnClickListener(this);
        loginWechart.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.login_weibo) {
            loginWithWeibo();
        } else if (id == R.id.login_wechart) {
            loginWithDouban();
        } else if (id == R.id.login_qq) {
            loginWithQQ();
        }
    }

    void loginWithQQ() {
        Platform qq = ShareSDK.getPlatform(QQ.NAME);
        qq.setPlatformActionListener(this);
        qq.showUser(null);
    }

    void loginWithWeibo() {
        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        weibo.setPlatformActionListener(this);
        weibo.showUser(null);
    }

    void loginWithDouban() {
        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
        wechat.setPlatformActionListener(this);
        wechat.showUser(null);
    }

    @Override
    public void onComplete(final Platform platform, int action, HashMap<String, Object> hashMap) {
        if (action == Platform.ACTION_USER_INFOR) {
            PlatformDb platDB = platform.getDb();
            String userId = platDB.getUserId();
            final String nickname = platDB.getUserName();
            final String userImage = platDB.getUserIcon();
            String token = platDB.getToken();
            String expiresIn = String.valueOf(platDB.getExpiresIn());
            Log.i("main", platform.getName()+": "+token + " "+ expiresIn+" "+userId);

            String snsName = getSnsName(platform);

            MyUser.BmobThirdUserAuth thirdUserAuth = new MyUser.BmobThirdUserAuth(snsName, token, expiresIn, userId);
            MyUser.loginWithAuthData(thirdUserAuth, new LogInListener<JSONObject>() {
                @Override
                public void done(JSONObject jsonObject, BmobException e) {
                    if (e == null) {
                        Toast.makeText(LogInActivity.this, "授权登录" + platform.getName() + "成功", Toast.LENGTH_SHORT).show();
                        Log.i("main", "done: "+userImage+" "+nickname);
                        updateUser(nickname, userImage);
                    } else {
                        Toast.makeText(LogInActivity.this, "授权登录" + platform.getName() + "失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void updateUser(String nickname, String userImage) {
        MyUser newUser = new MyUser();
        newUser.setNickName(nickname);
        newUser.setHeaderImage(userImage);
        MyUser bmobUser = MyUser.getCurrentUser(MyUser.class);
        newUser.update(bmobUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.i("main", "更新用户信息成功: ");
                    setResult(RESULT_OK);
                    finish();
                }else{
                    Log.i("main", "更新用户信息成功: " + e.getMessage());
                }
            }
        });
    }

    private String getSnsName(Platform platform) {
        if(QQ.NAME.equals(platform.getName())){
            return "qq";
        } else if(SinaWeibo.NAME.equals(platform.getName())){
            return "weibo";
        } else if(Wechat.NAME.equals(platform.getName())){
            return "weixin";
        } else {
            return "";
        }
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {

    }

    @Override
    public void onCancel(Platform platform, int i) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

}
