package org.xmobile.framework.qrcode.utils;

import java.util.Collection;
import java.util.Map;

import org.xmobile.framework.activity.BaseCaptureActivity;
import org.xmobile.framework.qrcode.camera.CameraManager;
import org.xmobile.framework.qrcode.decode.DecodeThread;
import org.xmobile.framework.qrcode.ui.ViewfinderResultPointCallback;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;

/**
 * This class handles all the messaging which comprises the state machine for
 * capture.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class CaptureActivityHandler extends Handler {

	private final BaseCaptureActivity activity;
	private final DecodeThread decodeThread;
	private State state;
	private final CameraManager cameraManager;

	private enum State {
		PREVIEW, SUCCESS, DONE
	}

	public CaptureActivityHandler(BaseCaptureActivity activity,
			Collection<BarcodeFormat> decodeFormats,
			Map<DecodeHintType, ?> baseHints, String characterSet,
			CameraManager cameraManager) {
		this.activity = activity;
		decodeThread = new DecodeThread(activity, decodeFormats, baseHints, characterSet, 
				new ViewfinderResultPointCallback(activity.getViewfinderView()));
		decodeThread.start();
		state = State.SUCCESS;

		// Start ourselves capturing previews and decoding.
		this.cameraManager = cameraManager;
		cameraManager.startPreview();
		restartPreviewAndDecode();
	}

	@Override
	public void handleMessage(Message message) {

		if (message.what == BaseCaptureActivity.RESTART_PREVIEW) {
			restartPreviewAndDecode();
		} else if (message.what == BaseCaptureActivity.DECODE_SUCCEEDED) {
			state = State.SUCCESS;
			Bundle bundle = message.getData();
			Bitmap barcode = null;
			float scaleFactor = 1.0f;
			if (bundle != null) {
				byte[] compressedBitmap = bundle.getByteArray(DecodeThread.BARCODE_BITMAP);
				if (compressedBitmap != null) {
					barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
					// Mutable copy:
					barcode = barcode.copy(Bitmap.Config.ARGB_8888, true);
				}
				scaleFactor = bundle.getFloat(DecodeThread.BARCODE_SCALED_FACTOR);
			}
			activity.handleDecode((Result) message.obj, barcode, scaleFactor);
		} else if (message.what == BaseCaptureActivity.DECODE_FAILED) {
			state = State.PREVIEW;
			cameraManager.requestPreviewFrame(decodeThread.getHandler(), BaseCaptureActivity.DECODE);
		} else if (message.what == BaseCaptureActivity.RETURN_SCAN_RESULT) {
			activity.setResult(Activity.RESULT_OK, (Intent) message.obj);
			activity.finish();
		}
	}

	public void quitSynchronously() {
		state = State.DONE;
		cameraManager.stopPreview();
		Message quit = Message.obtain(decodeThread.getHandler(), BaseCaptureActivity.QUIT);
		quit.sendToTarget();
		try {
			// Wait at most half a second; should be enough time, and onPause()
			// will timeout quickly
			decodeThread.join(500L);
		} catch (InterruptedException e) {
		}

		// Be absolutely sure we don't send any queued up messages
		removeMessages(BaseCaptureActivity.DECODE_SUCCEEDED);
		removeMessages(BaseCaptureActivity.DECODE_FAILED);
	}

	private void restartPreviewAndDecode() {
		if (state == State.SUCCESS) {
			state = State.PREVIEW;
			cameraManager.requestPreviewFrame(decodeThread.getHandler(), BaseCaptureActivity.DECODE);
			activity.drawViewfinder();
		}
	}
}
