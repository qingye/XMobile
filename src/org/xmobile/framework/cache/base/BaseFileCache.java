package org.xmobile.framework.cache.base;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import org.xmobile.framework.utils.Log;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

public class BaseFileCache {

	private static final float SIZE_FACTOR = 0.3f;
	private static final int MB = 1024 * 1024;
	private static final int MAX_MB_CACHE = 256 * MB;
	private static final int MIN_FREE_SPACE_TO_CACHE = 10;

	/*************************************************************************
	 * Check Space
	 *************************************************************************/
	protected final boolean isEnoughSpace(){
		if (MIN_FREE_SPACE_TO_CACHE > getFreeSpaceOnSDCard()) {
			Log.d("Space not enough for cache");
			return false;
		}
		return true;
	}
	
	/*************************************************************************
	 * Get Space Size
	 *************************************************************************/
	@SuppressWarnings("deprecation")
	protected final long getFreeSpaceOnSDCard() {
		StatFs fs = new StatFs(Environment.getExternalStorageDirectory()
				.getAbsolutePath());
		long free = 0;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
			free = (long) fs.getAvailableBlocks() * (long) fs.getBlockSize() / MB;
		} else {
			free = fs.getAvailableBlocksLong() * fs.getBlockSizeLong() / MB;
		}
		return free;
	}
	
	/*************************************************************************
	 * Update File Timestamp
	 *************************************************************************/
	protected final void updateFileLastTime(String path) {
		File file = new File(path);
		file.setLastModified(System.currentTimeMillis());
	}

	/*************************************************************************
	 * Clean the cache
	 *************************************************************************/
	protected final void removeCache(String cache) {
		File dir = new File(cache);
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}

		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return;
		}

		// calculate the cache file size
		long sum = 0;
		for (File f : files) {
			sum += f.length();
		}

		// check and clear
		if (sum > MAX_MB_CACHE || MIN_FREE_SPACE_TO_CACHE > getFreeSpaceOnSDCard()) {
			Arrays.sort(files, new FileSortByLastModified());
			long size = (int) (SIZE_FACTOR * sum);
			long ds = 0;
			for (int i = 0; i < files.length; i++) {
				ds += files[i].length();
				files[i].delete();
				if (ds >= size) {
					break;
				}
			}
		}
	}

	private class FileSortByLastModified implements Comparator<File> {
		@Override
		public int compare(File lhs, File rhs) {
			return (int) (lhs.lastModified() - rhs.lastModified());
		}
	}
}
