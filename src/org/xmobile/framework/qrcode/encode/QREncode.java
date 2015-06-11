package org.xmobile.framework.qrcode.encode;

import java.util.Hashtable;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class QREncode {

	private final int DEFAULT_DIMENSION = 256;
	
	/************************************************************************************************
	 * @param content		(input:  String to be encode)
	 * @param dimension		(input:  Width & Height)
	 * @return Bitmap		(output: QRCode bitmap)
	 ************************************************************************************************/
	public Bitmap encodeQRCode(String content, int dimension) {
		int dimen = (dimension <= 0 ? DEFAULT_DIMENSION : dimension);
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

		Bitmap bitmap = null;
		BitMatrix matrix = null;
		try {
			matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, dimen, dimen, hints);
			int width = matrix.getWidth();
			int height = matrix.getHeight();
			int[] pixels = new int[width * height];

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (matrix.get(x, y)) {
						pixels[y * width + x] = Color.BLACK;
					}
				}
			}
			
			bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
}
