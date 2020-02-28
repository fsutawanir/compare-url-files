package com.keselekjembut.cuf;

import java.io.IOException;
import java.util.concurrent.Future;

import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class Https {

	private static Https INSTANCE;
	
	private CloseableHttpAsyncClient httpclient;
	
	private Https() {
		
	}
	
	public static Https getInstance() {
		if(Https.INSTANCE == null) {
			Https.INSTANCE = new Https();
		}
		return Https.INSTANCE;
	}
	
	public String get(final String url) throws IOException, ParseException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet request = new HttpGet(url);
		CloseableHttpResponse response = httpclient.execute(request);
		String body = EntityUtils.toString(response.getEntity() );
		return body;
	}
	
	public void getAsync(final String url, final FutureCallback<SimpleHttpResponse> callback) {
		if(this.httpclient == null) {
			this.httpclient = HttpAsyncClients.createDefault();
			this.httpclient.start();
		}
		SimpleHttpRequest request = new SimpleHttpRequest("GET", url);
		httpclient.execute(request, callback);
	}
	
}
