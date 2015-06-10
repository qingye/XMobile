package org.xmobile.framework.activity;

import java.util.ArrayList;

import org.xmobile.framework.BaseApplication;
import org.xmobile.framework.cache.AsyncImageLoader.OnImageLoadListener;
import org.xmobile.framework.net.DataPacket;
import org.xmobile.framework.net.manager.PostResListener;
import org.xmobile.framework.storage.db.DBOpType;
import org.xmobile.framework.storage.db.ISQListener;
import org.xmobile.framework.storage.db.SqlResult;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

public abstract class BaseActivity extends FragmentActivity 
	implements PostResListener, OnImageLoadListener, ISQListener {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		BaseApplication.getInstance().addActivitToStack(this);
	}

	@Override
	protected final void onStop() {
		postRequestCancel();
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		BaseApplication.getInstance().removeActivityToStack(this);
		super.onDestroy();
	}

	/*****************************************************************************
	 * Network Engine Interfaces
	 * 
	 * If an activity has more than one request, then you can set tag to
	 * distinguish the response data for which request.
	 *****************************************************************************/
	protected final <T> void postRequest(String service, T req){
		postRequestWithTag(service, req, 0);
	}
	
	protected final <T> void postRequestWithTag(String service, T req, int tag){
		DataPacket<T> packet = new DataPacket<T>(this, service, req, tag);
		BaseApplication.getInstance().postRequest(packet);
	}
	
	protected final void postRequestCancel(){
		BaseApplication.getInstance().postRequestCancel(this);
	}
	
	@Override
	public void onResult(Object result) {
	}

	/*****************************************************************************
	 * DB Interfaces
	 *****************************************************************************/
	private void checkUiThread(){
		if(Looper.myLooper() != Looper.getMainLooper()){
			new Throwable("call db must in UIThread!");
		}
	}
	
	protected final void dbRequest(int tag, Class<?> clz, DBOpType type, String sql){
		checkUiThread();
		BaseApplication.getInstance().dbRequest(this, tag, clz, type, sql);
	}
	
	protected final void dbRequest(int tag, Class<?> clz, DBOpType type, ArrayList<String> sql){
		checkUiThread();
		BaseApplication.getInstance().dbRequest(this, tag, clz, type, sql);
	}
	
	protected final void dbRequest(int tag, Class<?> clz, DBOpType type, String sql, String[] params){
		checkUiThread();
		BaseApplication.getInstance().dbRequest(this, tag, clz, type, sql, params);
	}
	
	protected final void addDBRequest(int tag, Class<?> clz, DBOpType type, ArrayList<String> sql, ArrayList<String[]> params){
		checkUiThread();
		BaseApplication.getInstance().dbRequest(this, tag, clz, type, sql, params);
	}
	
	@Override
	public void onDbResult(SqlResult sqlResult) {
	}

	/*****************************************************************************
	 * AsyncImageLoader
	 * 
	 * Load bitmap for image view
	 *****************************************************************************/
	public void loadBitmap(ImageView view, String url, int defaultResId){
		view.setTag(url);
		Bitmap bm = (Bitmap) BaseApplication.getInstance().loadBitmap(this, view, url);
		if(bm != null && !bm.isRecycled()){
			view.setImageBitmap(bm);
		}else{
			if(defaultResId != 0){
				view.setImageResource(defaultResId);
			}
		}
	}

	@Override
	public void ImageLoadFinished(Object view, String url, Bitmap bitmap) {
		if(view != null && bitmap != null && !bitmap.isRecycled()){
			ImageView v = (ImageView) view;
			if(v.getTag().equals(url)){
				v.setImageBitmap(bitmap);
			}
		}
	}
}
