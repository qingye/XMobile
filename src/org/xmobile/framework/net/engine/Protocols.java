package org.xmobile.framework.net.engine;

public class Protocols {

	// release
    public static String BaseUrl = "https://prod.reframehealth.com";
	
	private static final String BaseService 		= "/Service.ashx?";
	public static final String HealthService 		= "HealthService";
	
	public static String getUrl(String method){
		return BaseUrl + method;
	}
	
	public static String getUrl(String service, String method){
		return String.format("%s%sservice=%s&method=%s", BaseUrl, BaseService, service, method);
	}
}
