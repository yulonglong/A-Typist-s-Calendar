package com.licensetokil.atypistcalendar.gcal;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.JsonObject;

// TODO handle non 200 replies

public class HttpsConnectionHelper {
	public static final String REQUEST_METHOD_POST = "POST";
	public static final String REQUEST_METHOD_GET = "GET";
	public static final String REQUEST_METHOD_DELETE = "DELETE";
	public static final String REQUEST_METHOD_PUT = "PUT";
	public static final String REQUEST_METHOD_PATCH = "PATCH";
	
	public static final String CONTENT_TYPE_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
	public static final String CONTENT_TYPE_JSON = "application/json";
	
	public static String sendJsonRequest(String url, String requestMethod, Map<String, String> additionalHeaders, JsonObject requestBody)
			throws IOException {
		//TODO handle get
		//TODO assert get means requestBody == null
		URL urlObject = new URL(url);
		HttpsURLConnection httpsConnection = (HttpsURLConnection)urlObject.openConnection();

		httpsConnection.setRequestMethod(requestMethod);
		httpsConnection.setRequestProperty("Content-Type", CONTENT_TYPE_JSON);
		
		if(additionalHeaders != null) {
			addHeaders(httpsConnection, additionalHeaders);
		}
		
		if(requestBody != null) {
			return sendAndReceiveRequest(httpsConnection, requestBody.toString());
		}
		return sendAndReceiveRequest(httpsConnection, null);
	}
	
	public static String sendUrlencodedFormRequest(String url, String requestMethod, Map<String, String> additionalHeaders, Map<String, String> formParameters)
			throws IOException {
		//TODO handle get
		URL urlObject = new URL(url);
		HttpsURLConnection httpsConnection = (HttpsURLConnection)urlObject.openConnection();

		httpsConnection.setRequestMethod(requestMethod);
		httpsConnection.setRequestProperty("Content-Type", CONTENT_TYPE_WWW_FORM_URLENCODED);

		if(additionalHeaders != null) {
			addHeaders(httpsConnection, additionalHeaders);
		}
		
		if(formParameters != null) {
			String parametersAsString = "";
			Iterator<String> parametersKeyIterator = formParameters.keySet().iterator();
			while (parametersKeyIterator.hasNext()) {
				String currentKey = parametersKeyIterator.next();
				parametersAsString += currentKey + "=" + formParameters.get(currentKey) + "&";
			}
			return sendAndReceiveRequest(httpsConnection, parametersAsString);
		}

		return sendAndReceiveRequest(httpsConnection, null);
	}
	
	private static void addHeaders(HttpsURLConnection httpsConnection, Map<String, String> headers) {
		Iterator<String> headersIterator = headers.keySet().iterator();
		while (headersIterator.hasNext()) {
			String currentKey = headersIterator.next();
			httpsConnection.setRequestProperty(currentKey, headers.get(currentKey));
		}
	}
	
	private static String sendAndReceiveRequest(HttpsURLConnection httpsConnection, String content) 
			throws IOException {
		if(content != null) {
			httpsConnection.setDoOutput(true);
			DataOutputStream dataOutputStream = new DataOutputStream(httpsConnection.getOutputStream());
			dataOutputStream.writeBytes(content);
			dataOutputStream.flush();
			dataOutputStream.close();
		}

		BufferedInputStream serverReplyBufferedInputStream = new BufferedInputStream(httpsConnection.getInputStream(), 10000);
		StringBuilder serverReplyStringBuilder = new StringBuilder(10000);

		int characterFromInputStream = serverReplyBufferedInputStream.read();
		while (characterFromInputStream != -1) {
			serverReplyStringBuilder.append((char)characterFromInputStream);
			characterFromInputStream = serverReplyBufferedInputStream.read();
		}
		serverReplyBufferedInputStream.close();
		return serverReplyStringBuilder.toString();
	}
}
