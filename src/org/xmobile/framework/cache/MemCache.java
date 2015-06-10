package org.xmobile.framework.cache;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Map;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

public class MemCache {

	private static LruCache<String, Bitmap> mLruCache = null;
	private static LinkedHashMap<String, SoftReference<Bitmap>> mImageCache = null;
	
	private static final int CAPACITY = 16;
	private static final float LOAD_FACTOR = 0.75f;
	
	public MemCache(Context context){
		int memClass = ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();

		/*******************************************************************************************
		 * Max cache size = System Allocate for APP's memory / 4
		 *******************************************************************************************/
		int cacheSize = 1024 * 1024 * memClass / 4;
		
		mLruCache = new LruCache<String, Bitmap>(cacheSize){
			@Override
			protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
				if(oldValue != null){
					mImageCache.put(key, new SoftReference<Bitmap>(oldValue));
				}
			}

			@Override
			protected int sizeOf(String key, Bitmap value) {
				if(value != null){
					return value.getRowBytes() * value.getHeight();
				}
				return 0;
			}
		};
		
		/*******************************************************************************************
		 * Set the Capacity, and override the removeEldestEntry,
		 * when size > capacity, replace the oldest <K,V>
		 *******************************************************************************************/
		mImageCache = new LinkedHashMap<String, SoftReference<Bitmap>>(CAPACITY, LOAD_FACTOR, true){
			private static final long serialVersionUID = 8837589946902447908L;
			@Override
			protected boolean removeEldestEntry(Map.Entry<String, SoftReference<Bitmap>> eldest) {
				return (size() > CAPACITY);
			}
		};
	}

	/*******************************************************************************************
	 * Get Bitmap Cache
	 *******************************************************************************************/
	public Bitmap getBitmapFromCache(String key){
		Bitmap bm = null;
		
		synchronized(mLruCache){
			bm = mLruCache.get(key);
			if(bm != null && !bm.isRecycled()){
				mLruCache.remove(key);
				mLruCache.put(key, bm);
				return bm;
			}
		}
		
		synchronized(mImageCache){
			SoftReference<Bitmap> ref = mImageCache.get(key);
			if(ref != null){
				bm = ref.get();
				if(bm != null && !bm.isRecycled()){
					mLruCache.put(key, bm);
					mImageCache.remove(key);
					return bm;
				}else{
					mImageCache.remove(key);
				}
			}
		}

		return null;
	}

	/*******************************************************************************************
	 * Set Bitmap Cache
	 *******************************************************************************************/
	public void addBitmapToCache(String key, Bitmap bm){
		if(bm != null && !bm.isRecycled()){
			synchronized(mLruCache){
				mLruCache.put(key, bm);
			}
		}
	}
}
