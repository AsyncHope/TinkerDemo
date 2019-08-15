package com.hope.demo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.meituan.android.walle.WalleChannelReader;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tinkerpatch.sdk.TinkerPatch;
import com.tinkerpatch.sdk.server.callback.ConfigRequestCallback;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    
    private TextView mTextMessage;
    
    private static final String TAG = "lty.MainActivity";
    
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };
    private Button bt_load_patch;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mTextMessage = (TextView) findViewById(R.id.message);
        TextView tv_version_channel = (TextView) findViewById(R.id.tv_version_channel);
        tv_version_channel.setText(
                "当前版本: "+getPackageInfo(this).versionName
                        +", 当前渠道: "+ getChannel(this));
        bt_load_patch = (Button) findViewById(R.id.bt_load_patch);
        Button bt_request_patch = (Button) findViewById(R.id.bt_request_patch);
        Button bt_request_config = (Button) findViewById(R.id.bt_request_config);
        
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        
        bt_load_patch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPatch();
            }
        });
    
        bt_request_patch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPatch();
            }
        });
    
        bt_request_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestConfig();
            }
        });
        mTextMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,TinkerMessageActivity.class));
            }
        });
       
    }
    
    
    private void loadPatch() {
        if (BuildConfig.TINKER_ENABLE) {
            String path = Environment.getExternalStorageDirectory()+ "/1/patch_signed_7zip.apk";
            TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(), path);
        }
    }
    
    private void requestPatch() {
        if (BuildConfig.TINKER_ENABLE) {
            TinkerPatch.with().fetchPatchUpdate(true);
        }
    }
    
    private void requestConfig() {
        if (BuildConfig.TINKER_ENABLE) {
            TinkerPatch.with().fetchDynamicConfig(new ConfigRequestCallback() {
        
                @Override
                public void onSuccess(HashMap<String, String> configs) {
                    Log.e(TAG, "动态配置: onSuccess " + configs);
                }
        
                @Override
                public void onFail(Exception e) {
                    Log.e(TAG, "动态配置: onFail" + e);
                }
            }, true);
        }
    }
    
    private PackageInfo getPackageInfo(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private String getChannel(Context context) {
        String channel = TextUtils.isEmpty(WalleChannelReader.getChannel(context)) ?
                "TinkerDemo":WalleChannelReader.getChannel(context);
        return channel;
    }
}
