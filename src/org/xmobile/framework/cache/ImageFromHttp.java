package org.xmobile.framework.cache;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.xmobile.framework.cache.impl.FlushInputStream;
import org.xmobile.framework.net.engine.HttpTools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageFromHttp {

	public ImageFromHttp(){
	}
	
	public Bitmap getImageByUrl(String url){
		Bitmap bm = null;
		HttpEntity entity = HttpTools.get(url);
		if(entity != null){
			try {
				InputStream is = entity.getContent();
				FilterInputStream fis = new FlushInputStream(is);
				bm = BitmapFactory.decodeStream(fis);
				is.close();
				entity.consumeContent();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return bm;
	}
}
