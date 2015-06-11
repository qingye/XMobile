package org.xmobile.framework.qrcode.camera;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.RejectedExecutionException;

import org.xmobile.framework.utils.Log;

import android.content.Context;
import android.hardware.Camera;
import android.os.AsyncTask;

final class AutoFocusManager implements Camera.AutoFocusCallback {

	private static final long AUTO_FOCUS_INTERVAL_MS = 1000L;
	private static final Collection<String> FOCUS_MODES_CALLING_AF;
	static {
		FOCUS_MODES_CALLING_AF = new ArrayList<String>(2);
		FOCUS_MODES_CALLING_AF.add(Camera.Parameters.FOCUS_MODE_AUTO);
		FOCUS_MODES_CALLING_AF.add(Camera.Parameters.FOCUS_MODE_MACRO);
	}

	private boolean stopped;
	private boolean focusing;
	private final boolean useAutoFocus;
	private final Camera camera;
	private AsyncTask<?, ?, ?> outstandingTask;

	AutoFocusManager(Context context, Camera camera) {
		this.camera = camera;
		String currentFocusMode = camera.getParameters().getFocusMode();
		useAutoFocus = FOCUS_MODES_CALLING_AF.contains(currentFocusMode);
		start();
	}

	@Override
	public synchronized void onAutoFocus(boolean success, Camera theCamera) {
		focusing = false;
		autoFocusAgainLater();
	}

	private synchronized void autoFocusAgainLater() {
		if (!stopped && outstandingTask == null) {
			AutoFocusTask newTask = new AutoFocusTask();
			try {
				newTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				outstandingTask = newTask;
			} catch (RejectedExecutionException ree) {
				Log.w("Could not request auto focus " + ree);
			}
		}
	}

	private synchronized void cancelOutstandingTask() {
		if (outstandingTask != null) {
			if (outstandingTask.getStatus() != AsyncTask.Status.FINISHED) {
				outstandingTask.cancel(true);
			}
			outstandingTask = null;
		}
	}

	public synchronized void start() {
		if (useAutoFocus) {
			outstandingTask = null;
			if (!stopped && !focusing) {
				try {
					camera.autoFocus(this);
					focusing = true;
				} catch (RuntimeException re) {
					Log.w("Unexpected exception while focusing " + re);
					autoFocusAgainLater();
				}
			}
		}
	}
	
	public synchronized void stop() {
		stopped = true;
		if (useAutoFocus) {
			cancelOutstandingTask();
			try {
				camera.cancelAutoFocus();
			} catch (RuntimeException re) {
				Log.w("Unexpected exception while cancelling focusing" + re);
			}
		}
	}

	private final class AutoFocusTask extends AsyncTask<Object, Object, Object> {
		@Override
		protected Object doInBackground(Object... voids) {
			try {
				Thread.sleep(AUTO_FOCUS_INTERVAL_MS);
			} catch (InterruptedException e) {
			}
			start();
			return null;
		}
	}
}
