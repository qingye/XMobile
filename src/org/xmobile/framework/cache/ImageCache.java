package org.xmobile.framework.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xmobile.framework.BaseApplication;
import org.xmobile.framework.cache.base.BaseFileCache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class ImageCache extends BaseFileCache {

	public ImageCache() {
		removeCache(BaseApplication.IMAGE_PATH_DIR);
	}
	
	/*************************************************************************
	 * Image By URL
	 *************************************************************************/
	public Bitmap getImageByUrl(final String url) {
		String path = BaseApplication.IMAGE_PATH_DIR + "/" + urlToFileName(url);
		return getImageFile(path);
	}

	/*************************************************************************
	 * Save Image By URL
	 *************************************************************************/
	public boolean saveImageByUrl(Bitmap bm, String url) {
		return saveImageFile(bm, urlToFileName(url));
	}
	
	/*************************************************************************
	 * Check file exist
	 *************************************************************************/
	public boolean isExist(String filename){
		File file = new File(BaseApplication.IMAGE_PATH_DIR + "/" + filename);
		return file.exists();
	}

	/*************************************************************************
	 * URL to name
	 *************************************************************************/
	private String urlToFileName(String url) {
		String[] strs = url.split("/");
		String filename = strs[strs.length - 1];
		return filename;
	}

	/*************************************************************************
	 * Save Image File
	 *************************************************************************/
	private boolean saveImageFile(Bitmap bm, String filename) {
		boolean result = false;
		if (bm == null || bm.isRecycled()) {
			return result;
		}

		if(!isEnoughSpace()){
			return result;
		}

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File dir = new File(BaseApplication.IMAGE_PATH_DIR);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File file = new File(dir, filename);
			try {
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);
				if (bm.compress(Bitmap.CompressFormat.PNG, 100, fos)) {
					fos.flush();
				}
				fos.close();
				result = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/*************************************************************************
	 * Get Image File
	 *************************************************************************/
	private Bitmap getImageFile(final String path) {
		Bitmap bm = null;
		File file = new File(path);
		if (file.exists()) {
			bm = BitmapFactory.decodeFile(path);
			if (bm == null) {
				file.delete();
			} else {
				updateFileLastTime(path);
			}
		}
	
		return bm;
	}
}
