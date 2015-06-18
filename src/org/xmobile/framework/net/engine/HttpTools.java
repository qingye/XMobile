package org.xmobile.framework.net.engine;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.xmobile.framework.utils.Log;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class HttpTools {

	private static HttpClient mHttpClient;

	public static final String NETWORK_NOT_AVAILABLE = "1";
	public static final String SERVER_ERR = "2";
	public static final String TIME_OUT = "3";

	public final static int NETWORK_STATUS_NOT_AVAILABLE = 0;
	public final static int NETWORK_STATUS_IS_WIFI = 1;
	public final static int NETWORK_STATUS_IS_GPRS = 2;
	
	private HttpTools() {
	}
	
	public static int getNetworkStatus(Context context){
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if(info != null){
			if(cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()){
				return NETWORK_STATUS_IS_WIFI;
			}else if(cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()){
				return NETWORK_STATUS_IS_GPRS; // 2G/2.5G/3G
			}
		}
		return NETWORK_STATUS_NOT_AVAILABLE;
	}
	
	private static SSLSocketFactory addCACert(Context context){
		InputStream is = null;
		KeyStore trustStore = null;
		SSLSocketFactory sf = null;
		if(context != null){
			try {
				is = context.getAssets().open("ca.cer");
				CertificateFactory cerFactory = CertificateFactory.getInstance("X.509");
	            Certificate cer = cerFactory.generateCertificate(is);
				trustStore = KeyStore.getInstance("PKCS12", "BC");
				trustStore.load(null, null);
				trustStore.setCertificateEntry("trust", cer);
				sf = new SSLSocketFactoryImp(trustStore);
			} catch (Exception e) {
//				e.printStackTrace();
			}
		}
		
		return sf;
	}

	private static synchronized HttpClient getHttpClient(Context context) {
		if (null == mHttpClient) {
			HttpParams params = new BasicHttpParams();

			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			HttpProtocolParams.setHttpElementCharset(params, HTTP.UTF_8);
			HttpProtocolParams.setUseExpectContinue(params, true);

			ConnManagerParams.setTimeout(params, 60000);
			HttpConnectionParams.setConnectionTimeout(params, 60000);
			HttpConnectionParams.setSoTimeout(params, 60000);

			SSLSocketFactory sf = addCACert(context);
			if(sf == null){
				try {
					KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
					trustStore.load(null, null);
					sf = new SSLSocketFactoryImp(trustStore);
				} catch (Exception e) {
//					e.printStackTrace();
				}
			}

			SchemeRegistry schReg = new SchemeRegistry();
			schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schReg.register(new Scheme("https", sf, 443));

			ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
					params, schReg);
			mHttpClient = new DefaultHttpClient(conMgr, params);
		}
		return mHttpClient;
	}

	public static String post(Context context, String url, String content/*, String sesseionid*/) {
		if(getNetworkStatus(context) == NETWORK_STATUS_NOT_AVAILABLE){
			return NETWORK_NOT_AVAILABLE;
		}
		return http(context, url, content);
	}
	
	public static String http(Context context, String url, String content){
		String result = null;
		HttpPost reqeust = new HttpPost(url);
		String s = content;
		StringEntity entity = null;
		HttpResponse res = null;
		
		Log.d("url = " + url);
		try {
			entity = new StringEntity(s,HTTP.UTF_8);
			reqeust.setEntity(entity);
			//reqeust.addHeader("sessionId", sesseionid);
			res = getHttpClient(context).execute(reqeust);
			if (res != null && res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity resEntity = res.getEntity();
				result = (resEntity == null) ? null : EntityUtils.toString(resEntity, "UTF-8");
			}
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
			return TIME_OUT;
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			return TIME_OUT;
		} catch (IOException e1) {
			e1.printStackTrace();
			return SERVER_ERR;
		}
		
		if (result != null) {
			return result;
		} else {
			return SERVER_ERR;
		}
	}
	
	public static HttpEntity get(String url){
		HttpGet request = new HttpGet(url);
		HttpEntity resEntity = null;
		HttpResponse res = null;
		try {
			res = getHttpClient(null).execute(request);
			if (res != null && res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				resEntity = res.getEntity();
			}
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return resEntity;
	}
}
