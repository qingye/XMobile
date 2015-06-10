package org.xmobile.framework.cache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

public class AsyncImageLoader {

	public interface OnImageLoadListener{
		public void ImageLoadFinished(Object view, String url, Bitmap bitmap);
	}

	private static final int nThreadPoolSize = 3;
	private static ExecutorService mExecutorService = Executors.newFixedThreadPool(nThreadPoolSize);
	
	private static ImageFromHttp mImageFromHttp = null;
	private static ImageCache mImageCache = null;
	private static MemCache mMemCache = null;

	public AsyncImageLoader(Context context){
		if(mImageFromHttp == null){
			mImageFromHttp = new ImageFromHttp();
		}
		if(mImageCache == null){
			mImageCache = new ImageCache();
		}
		if(mMemCache == null){
			mMemCache = new MemCache(context);
		}
	}
	
	protected final static Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0:
				/***********************************************************
				 * Objects:
				 * [0]: Context implement OnImageLoadListener
				 * [1]: ImageView
				 * [2]: URL
				 * [3]: Bitmap
				 ***********************************************************/
				Object[] obj = (Object[]) msg.obj;
				if(obj != null && obj.length == 4){
					OnImageLoadListener l = (OnImageLoadListener) obj[0];
					if(l != null){
						l.ImageLoadFinished(obj[1], (String) obj[2], (Bitmap) obj[3]);
					}
				}
				break;
				
			default:
				break;
			}
		}
	};
	
	/***********************************************************
	 * Get specified picture from SDCard or Remote server
	 ***********************************************************/
	private class AsyncRunnable implements Runnable{
		private String url = null;
		private Object context = null;
		private Object view = null;
		public AsyncRunnable(Object context, Object view, String url){
			this.context = context;
			this.view = view;
			this.url = url;
		}
		
		@Override
		public void run() {
			Bitmap bm = mImageCache.getImageByUrl(url);
			if(bm == null){
				bm = mImageFromHttp.getImageByUrl(url);
			}
			
			if(bm != null){
				mImageCache.saveImageByUrl(bm, url);
				mMemCache.addBitmapToCache(url, bm);
			}
			
			Message msg = mHandler.obtainMessage();
			msg.obj = new Object[]{context, view, url, bm};
			msg.what = 0;
			mHandler.sendMessage(msg);
		}
	}

	public Bitmap loadBitmap(Object context, Object view, String url){
		Bitmap bm = mMemCache.getBitmapFromCache(url);
		if(bm == null || bm.isRecycled()){
			mExecutorService.execute(new AsyncRunnable(context, view, url));
		}
		return bm;
	}
}
