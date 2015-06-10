package org.xmobile.framework.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;

import org.xmobile.framework.BaseApplication;
import org.xmobile.framework.cache.base.BaseFileCache;

import android.os.Environment;

public class FileCache extends BaseFileCache {

	public FileCache() {
		removeCache(BaseApplication.FILE_PATH_DIR);
	}
	
	/*************************************************************************
	 * Get File Path By File Name
	 *************************************************************************/
	public String getFileByName(final String filename) {
		String path = BaseApplication.FILE_PATH_DIR + "/" + filename;
		return path;
	}

	/*************************************************************************
	 * Check file exist
	 *************************************************************************/
	public boolean isExist(String filename){
		File file = new File(BaseApplication.FILE_PATH_DIR + "/" + filename);
		return file.exists();
	}

	/*************************************************************************
	 * Save File
	 *************************************************************************/
	public boolean saveFile(FilterInputStream fis, String filename){
		boolean result = false;
		if (fis == null) {
			return result;
		}

		if(!isEnoughSpace()){
			return result;
		}
		
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File dir = new File(BaseApplication.FILE_PATH_DIR);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File file = new File(dir, filename);
			try {
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);
				
				byte buffer[]=new byte[16*1024];
				int count = 0;
				while ((count = fis.read(buffer)) != -1){
	                fos.write(buffer, 0, count);
	            }
				fos.close();
				result = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
}
