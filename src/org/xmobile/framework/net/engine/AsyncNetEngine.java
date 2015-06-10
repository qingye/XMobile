package org.xmobile.framework.net.engine;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.xmobile.framework.net.DataPacket;

import android.content.Context;
import android.os.Handler;

public class AsyncNetEngine {

	private final static int MAX_THREAD_NUMBER = 5;
	private ExecutorService mThreadPool = null;
	private WeakHashMap<Context, ArrayList<WeakReference<Future<?>>>> mTask = null;

	private static AsyncNetEngine mInstance = null;
	public synchronized static AsyncNetEngine getInstance(){
		if(mInstance == null){
			mInstance = new AsyncNetEngine();
		}
		return mInstance;
	}
	
	private AsyncNetEngine(){
		mThreadPool = Executors.newFixedThreadPool(MAX_THREAD_NUMBER);
		mTask = new WeakHashMap<Context, ArrayList<WeakReference<Future<?>>>>();
	}

	/**
	 * add task associated with the passed Context.
	 * 
	 * @param context
	 * 			the android Context instance associated to the tasks.
	 * @param notifyHandler
	 * 			feedback whatever success/failure
	 */
	public void addTask(Context context, Handler notifyHandler, DataPacket<?> packet){
		Future<?> request = mThreadPool.submit(new AsyncRequest(new AsyncResponse(notifyHandler), packet));
		if(context != null){
			ArrayList<WeakReference<Future<?>>> list = mTask.get(context);
			if(list == null){
				list = new ArrayList<WeakReference<Future<?>>>();
				mTask.put(context, list);
			}
			list.add(new WeakReference<Future<?>>(request));
		}
	}
	
	/**
	 * Cancels any pending (or potentially active) tasks associated with the
	 * passed Context.
	 * <p>
	 * <b>Note:</b>: call this method at onStop/onDestroy
	 * 
	 * @param context
	 * 			the android Context instance associated to the tasks.
	 * @param mayInterruptIfRunning - 
	 * 			true if the thread executing this task should be interrupted; 
	 * 			otherwise, in-progress tasks are allowed to complete
	 */
	public void cancelTask(Context context, boolean mayInterruptIfRunning){
		if(context != null){
			ArrayList<WeakReference<Future<?>>> list = mTask.get(context);
			if(list != null){
				for(WeakReference<Future<?>> ref : list){
					Future<?> future = ref.get();
					if(future != null){
						future.cancel(mayInterruptIfRunning);
					}
				}
			}
		}
	}
	public void cancelTask(Context context){
		cancelTask(context, true);
	}
	
	/**
	 * Cancels all pending (or potentially active) tasks
	 * <p>
	 * <b>Note:</b>: call this method at Application exit
	 */
	public void cancelAllTask(){
		Collection<ArrayList<WeakReference<Future<?>>>> map = mTask.values();
		if(map != null){
			Iterator<ArrayList<WeakReference<Future<?>>>> it = map.iterator();
			while(it.hasNext()){
				ArrayList<WeakReference<Future<?>>> list = it.next();
				if(list != null){
					for(WeakReference<Future<?>> ref : list){
						Future<?> future = ref.get();
						if(future != null){
							future.cancel(true);
						}
					}
				}
			}
		}
		
		mThreadPool.shutdownNow();
		mThreadPool = null;
		mTask.clear();
		mTask = null;
	}
}
