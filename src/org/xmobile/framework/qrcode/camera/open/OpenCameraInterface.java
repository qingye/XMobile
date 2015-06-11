package org.xmobile.framework.qrcode.camera.open;

import org.xmobile.framework.utils.Log;

import android.hardware.Camera;

public final class OpenCameraInterface {

	public static final int NO_REQUESTED_CAMERA = -1;
	private OpenCameraInterface() {
	}

	public static Camera open(int cameraId) {

		int numCameras = Camera.getNumberOfCameras();
		if (numCameras == 0) {
			Log.w("No cameras!");
			return null;
		}

		cameraId = validCameraId(cameraId, numCameras);
		return getCamera(cameraId, numCameras);
	}
	
	private static int validCameraId(int cameraId, int numCameras){
		if (cameraId < 0) {
			int index = 0;
			while (index < numCameras) {
				Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
				Camera.getCameraInfo(index, cameraInfo);
				if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
					break;
				}
				index++;
			}
			cameraId = index;
		}
		return cameraId;
	}
	
	private static Camera getCamera(int cameraId, int numCameras){
		Camera camera = null;
		if (cameraId < numCameras) {
			camera = Camera.open(cameraId);
		} else {
			if (cameraId >= 0) {
				camera = null;
			} else {
				camera = Camera.open(0);
			}
		}
		return camera;
	}
}
