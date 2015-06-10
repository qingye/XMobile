package org.xmobile.framework.net.engine;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import android.util.Base64;

public class DES {

	public static final String ALGORITHM_DES = "DES/ECB/PKCS5Padding";

	public static String encode(String key, String data) throws Exception {
		return encode(key, data.getBytes());
	}

	public static String encode(String key, byte[] data) throws Exception {
		try {
			DESKeySpec dks = new DESKeySpec(key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] bytes = cipher.doFinal(data);
			return Base64.encodeToString(bytes, Base64.DEFAULT);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	public static byte[] decode(String key, byte[] data) throws Exception {
		try {
			DESKeySpec dks = new DESKeySpec(key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return cipher.doFinal(data);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	public static String decodeValue(String key, String data) {
		byte[] datas;
		String value = null;
		try {
			if (System.getProperty("os.name") != null&& 
			   (System.getProperty("os.name").equalsIgnoreCase("sunos") || 
			    System.getProperty("os.name").equalsIgnoreCase("linux"))) {
				datas = decode(key, Base64.decode(data, Base64.DEFAULT));
			} else {
				datas = decode(key, Base64.decode(data, Base64.DEFAULT));
			}
			value = new String(datas);
		} catch (Exception e) {
			value = "";
		}
		return value;
	}
}
