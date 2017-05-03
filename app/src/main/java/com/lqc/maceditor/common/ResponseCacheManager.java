package com.lqc.maceditor.common;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.text.TextUtils;

/**
 * API响应缓存管理器
 *
 * 使用SoftReference管理缓存
 *
 */
public class ResponseCacheManager {

    private static ResponseCacheManager mInstance;
    private static Object mLock = new Object();

    private HashMap<String, Object> mResponsePool;
    private SoftReference<HashMap<String, Object>> mResponseCache;

    private ResponseCacheManager() {
        mResponsePool = new HashMap<String, Object>();
        mResponseCache = new SoftReference<HashMap<String, Object>>(mResponsePool);
    }

    public static ResponseCacheManager getInstance() {
        synchronized (mLock) {
            if (mInstance == null) {
                mInstance = new ResponseCacheManager();
            }
        }
        return mInstance;
    }

    /**
     * 从缓存中获取API访问结果
     */
    public Object getResponse(String key) {

        if (TextUtils.isEmpty(key) || mResponseCache == null) {
            return null;
        }

        return mResponseCache.get().get(key);
    }

    /**
     * 缓存API访问结果
     */
    public void putResponse(String key, Object value) {
        if(mResponseCache != null) {
            mResponseCache.get().put(key, value);
        }
    }

    /**
     * 清除所有API缓存
     */
    public void clear() {
        if (mResponseCache != null) {
            mResponseCache.clear();
            mResponseCache = null;
        }
        if (mResponsePool != null) {
            mResponsePool.clear();
            mResponsePool = null;
        }
        mInstance = null;
    }

}