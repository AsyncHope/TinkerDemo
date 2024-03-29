/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Shengjie Sim Sun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.hope.demo;


import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.widget.Toast;

import com.tencent.tinker.entry.ApplicationLike;
import com.tencent.tinker.lib.listener.DefaultPatchListener;
import com.tencent.tinker.lib.patch.UpgradePatch;
import com.tencent.tinker.lib.reporter.DefaultLoadReporter;
import com.tencent.tinker.lib.reporter.DefaultPatchReporter;
import com.tencent.tinker.lib.service.PatchResult;
import com.tinkerpatch.sdk.TinkerPatch;
import com.tinkerpatch.sdk.loader.TinkerPatchApplicationLike;
import com.tinkerpatch.sdk.server.callback.ConfigRequestCallback;
import com.tinkerpatch.sdk.server.callback.RollbackCallBack;
import com.tinkerpatch.sdk.server.callback.TinkerPatchRequestCallback;
import com.tinkerpatch.sdk.tinker.callback.ResultCallBack;
import com.tinkerpatch.sdk.tinker.service.TinkerServerResultService;

import java.util.HashMap;

public class SampleApplication extends Application {
    private static final String TAG = "lty.SampleApppatch";

    private ApplicationLike tinkerApplicationLike;
    public SampleApplication() {

    }

    @Override
    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //you must install multiDex whatever tinker is installed!
        MultiDex.install(base);
    }


    /**
     * 由于在onCreate替换真正的Application,
     * 我们建议在onCreate初始化TinkerPatch,而不是attachBaseContext
     */
    @Override
    public void onCreate() {
        super.onCreate();
        initTinkerPatch();
    }

    /**
     * 我们需要确保至少对主进程跟patch进程初始化 TinkerPatch
     */
    private void initTinkerPatch() {
        // 我们可以从这里获得Tinker加载过程的信息
        Log.e(TAG, "tinker 是否可用" + BuildConfig.TINKER_ENABLE);
        if (BuildConfig.TINKER_ENABLE) {
            tinkerApplicationLike = TinkerPatchApplicationLike.getTinkerPatchApplicationLike();
           
            TinkerPatch.init(tinkerApplicationLike)
                    //是否自动反射Library路径,无须手动加载补丁中的So文件
                    //注意,调用在反射接口之后才能生效,你也可以使用Tinker的方式加载Library
                    .reflectPatchLibrary()
                    //向后台获取是否有补丁包更新,默认的访问间隔为3个小时
                    //若参数为true,即每次调用都会真正的访问后台配置
                    .fetchPatchUpdate(false)
                    //设置访问后台补丁包更新配置的时间间隔,默认为3个小时, -1 则不请求后台
                    .setFetchPatchIntervalByHours(3)
                    //向后台获得动态配置,默认的访问间隔为3个小时
                    //若参数为true,即每次调用都会真正的访问后台配置
                    .fetchDynamicConfig(new ConfigRequestCallback() {
                        @Override
                        public void onSuccess(HashMap<String, String> hashMap) {
                            Log.e(TAG, "动态配置:" + hashMap);
                        }
                
                        @Override
                        public void onFail(Exception e) {
                    
                        }
                    }, false)
                    //设置访问后台动态配置的时间间隔,默认为3个小时
                    .setFetchDynamicConfigIntervalByHours(3)
                    //设置当前渠道号,对于某些渠道我们可能会想屏蔽补丁功能
                    //设置渠道后,我们就可以使用后台的条件控制渠道更新
//                    .setAppChannel("default")
                    //屏蔽部分渠道的补丁功能
//                    .addIgnoreAppChannel("googleplay")
                    //设置tinkerpatch平台的条件下发参数
//                    .setPatchCondition("test", "1")
                    //设置补丁合成成功后,锁屏重启程序
                    //默认是等应用自然重启
                    .setPatchRestartOnSrceenOff(true)
                    //我们可以通过ResultCallBack设置对合成后的回调
                    //例如弹框什么
                    //注意，setPatchResultCallback 的回调是运行在 intentService 的线程中
                    .setPatchResultCallback(new ResultCallBack() {
                        @Override
                        public void onPatchResult(PatchResult patchResult) {
                            String message = "修复补丁" + (patchResult.isSuccess ? "成功" : "失败");
                            Log.e(TAG, message);
                            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                        }
                    })
                    //设置收到后台回退要求时,锁屏清除补丁
                    //默认是等主进程重启时自动清除
                    .setPatchRollbackOnScreenOff(true)
                    //我们可以通过RollbackCallBack设置对回退时的回调
                    .setPatchRollBackCallback(new RollbackCallBack() {
                        @Override
                        public void onPatchRollback() {
                            Log.e(TAG, "回退补丁 onPatchRollback callback here");
                        }
                    });

            // fetchPatchUpdateAndPollWithInterval 与 fetchPatchUpdate(false)
            // 不同的是，会通过handler的方式去轮询
            Log.e(TAG, "获取当前的补丁版本 " + TinkerPatch.with().getPatchVersion());
            TinkerPatch.with().fetchPatchUpdateAndPollWithInterval();
        }
    }

    /**
     * 在这里给出TinkerPatch的所有接口解释
     * 更详细的解释请参考:http://tinkerpatch.com/Docs/api
     */
    private void useSample() {
        TinkerPatch.init(tinkerApplicationLike)
            //是否自动反射Library路径,无须手动加载补丁中的So文件
            //注意,调用在反射接口之后才能生效,你也可以使用Tinker的方式加载Library
            .reflectPatchLibrary()
            //向后台获取是否有补丁包更新,默认的访问间隔为3个小时
            //若参数为true,即每次调用都会真正的访问后台配置
            .fetchPatchUpdate(false)
            //设置访问后台补丁包更新配置的时间间隔,默认为3个小时
            .setFetchPatchIntervalByHours(3)
            //向后台获得动态配置,默认的访问间隔为3个小时
            //若参数为true,即每次调用都会真正的访问后台配置
            .fetchDynamicConfig(new ConfigRequestCallback() {
                @Override
                public void onSuccess(HashMap<String, String> hashMap) {

                }

                @Override
                public void onFail(Exception e) {

                }
            }, false)
            //设置访问后台动态配置的时间间隔,默认为3个小时
            .setFetchDynamicConfigIntervalByHours(3)
            //设置当前渠道号,对于某些渠道我们可能会想屏蔽补丁功能
            //设置渠道后,我们就可以使用后台的条件控制渠道更新
            .setAppChannel("default")
            //屏蔽部分渠道的补丁功能
            .addIgnoreAppChannel("googleplay")
            //设置tinkerpatch平台的条件下发参数
            .setPatchCondition("test", "1")
            //设置补丁合成成功后,锁屏重启程序
            //默认是等应用自然重启
            .setPatchRestartOnSrceenOff(true)
            //我们可以通过ResultCallBack设置对合成后的回调
            //例如弹框什么
            //注意，setPatchResultCallback 的回调是运行在 intentService 的线程中
            .setPatchResultCallback(new ResultCallBack() {
                @Override
                public void onPatchResult(PatchResult patchResult) {
                    Log.i(TAG, "onPatchResult callback here");
                }
            })
            //设置收到后台回退要求时,锁屏清除补丁
            //默认是等主进程重启时自动清除
            .setPatchRollbackOnScreenOff(true)
            //我们可以通过RollbackCallBack设置对回退时的回调
            .setPatchRollBackCallback(new RollbackCallBack() {
                @Override
                public void onPatchRollback() {
                    Log.i(TAG, "onPatchRollback callback here");
                }
            });
    }

    /**
     * 自定义Tinker类的高级用法, 使用更灵活，但是需要对tinker有更进一步的了解
     * 更详细的解释请参考:http://tinkerpatch.com/Docs/api
     */
    private void complexSample() {
        //修改tinker的构造函数,自定义类
        TinkerPatch.Builder builder = new TinkerPatch.Builder(tinkerApplicationLike)
            .listener(new DefaultPatchListener(this))
            .loadReporter(new DefaultLoadReporter(this))
            .patchReporter(new DefaultPatchReporter(this))
            .resultServiceClass(TinkerServerResultService.class)
            .upgradePatch(new UpgradePatch())
            .patchRequestCallback(new TinkerPatchRequestCallback());
            //.requestLoader(new OkHttpLoader());

        TinkerPatch.init(builder.build());
    }
}
