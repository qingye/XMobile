package org.xmobile.framework.activity;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.xmobile.framework.qrcode.camera.CameraManager;
import org.xmobile.framework.qrcode.ui.ViewfinderView;
import org.xmobile.framework.qrcode.utils.AmbientLightManager;
import org.xmobile.framework.qrcode.utils.CaptureActivityHandler;
import org.xmobile.framework.qrcode.utils.FinishListener;
import org.xmobile.framework.qrcode.utils.InactivityTimer;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;

public abstract class BaseCaptureActivity extends BaseActivity implements SurfaceHolder.Callback {

	public static final int DECODE = 0x8000;
	public static final int DECODE_FAILED = 0x8001;
	public static final int DECODE_SUCCEEDED = 0x8002;
	public static final int LAUNCH_PRODUCT_QUERY = 0x8003;
	public static final int QUIT = 0x8004;
	public static final int RESTART_PREVIEW = 0x8005;
	public static final int RETURN_SCAN_RESULT = 0x8006;

	private ViewfinderView viewfinderView;
	private SurfaceView surfaceView;
	
	private InactivityTimer inactivityTimer;
	private AmbientLightManager ambientLightManager;
	
	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}
	public Handler getHandler() {
		return handler;
	}
	public CameraManager getCameraManager() {
		return cameraManager;
	}
	
	/************************************************************************************************
	 * System Override Initialize
	 ************************************************************************************************/
	@Override
	public final void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		ambientLightManager = new AmbientLightManager(this);
		cameraManager = new CameraManager(getApplication());
		
		initLayout();
	}
	
	private void initLayout(){
		RelativeLayout layout = new RelativeLayout(this);

		surfaceView = new SurfaceView(this);
		surfaceView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		layout.addView(surfaceView);
		
		viewfinderView = new ViewfinderView(this, null);
		viewfinderView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		layout.addView(viewfinderView);
		
		addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected final void onResume() {
		super.onResume();

		viewfinderView.setCameraManager(cameraManager);
		handler = null;
		resetStatusView();

		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		ambientLightManager.start(cameraManager);
		inactivityTimer.onResume();
		decodeFormats = null;
		characterSet = null;
	}
	
	@Override
	protected final void onPause() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		ambientLightManager.stop();
		cameraManager.closeDriver();
		if (!hasSurface) {
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		super.onPause();
	}
	
	@Override
	protected final void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}
	
	/************************************************************************************************
	 * View Finder
	 ************************************************************************************************/
	private void resetStatusView() {
		viewfinderView.setVisibility(View.VISIBLE);
	}
	
	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
			handler.sendEmptyMessageDelayed(RESTART_PREVIEW, delayMS);
		}
		resetStatusView();
	}
	
	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}
	
	/************************************************************************************************
	 * KeyEvents
	 ************************************************************************************************/
	@Override
	public final boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_FOCUS:
		case KeyEvent.KEYCODE_CAMERA:
			// Handle these events so they don't launch the Camera app
			return true;
			// Use volume up/down to turn on light
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			cameraManager.setTorch(false);
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			cameraManager.setTorch(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/************************************************************************************************
	 * Surface
	 ************************************************************************************************/
	private boolean hasSurface;
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}
	
	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private Collection<BarcodeFormat> decodeFormats;
	private Map<DecodeHintType, ?> decodeHints;
	private String characterSet;
	
	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			return;
		}
		if (cameraManager.isOpen()) {
			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			if (handler == null) {
				handler = new CaptureActivityHandler(this, decodeFormats,
						decodeHints, characterSet, cameraManager);
			}
		} catch (IOException e) {
			handleExceptionAndExit();
		} catch (RuntimeException e) {
			handleExceptionAndExit();
		}
	}
	
	private void handleExceptionAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Warning");
		builder.setMessage("Camera not ready!");
		builder.setPositiveButton("OK", new FinishListener(this));
		builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}
	
	/*******************************************************************************************
	 * A valid barcode has been found, so give an indication of success and show
	 * the results.
	 * 
	 * @param rawResult
	 *            The contents of the barcode.
	 * @param scaleFactor
	 *            amount by which thumbnail was scaled
	 * @param barcode
	 *            A greyscale bitmap of the camera data which was decoded.
	 *******************************************************************************************/
	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
		inactivityTimer.onActivity();
		handleDecodeInternally(rawResult, barcode);
	}

	private void handleDecodeInternally(Result rawResult, Bitmap barcode) {
		if (barcode != null) {
			ImageView iv = new ImageView(this);
			iv.setImageBitmap(barcode);
		}

		Bundle bundle = new Bundle();
		bundle.putString("type", "TEXT");
		bundle.putString("content", rawResult.getText());
		bundle.putString("barFormat", rawResult.getBarcodeFormat().name());
		resultQR(bundle);
	}
	
	/*******************************************************************************************
	 * Abstract interface
	 * 
	 *     Inherit this activity that should realize this interface.
	 *******************************************************************************************/
	public abstract void resultQR(Bundle bundle);
}
