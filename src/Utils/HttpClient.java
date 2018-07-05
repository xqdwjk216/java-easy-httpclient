package Utils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.ByteArrayEntity;

public class HttpClient {

	/**
	 *
	 */
	private static final CloseableHttpClient httpclient = createHttpsClient();

	public static byte[] post(String url,byte[] body){
		return doPostToString(url,body,false);
	}
	public static byte[] doPostToString(String url,byte[] body,boolean output){
		HttpPost httpPost = new HttpPost(url);
		HttpEntity rspEntity = null;
		try{
			if( null != body ){
				ByteArrayEntity entity = new ByteArrayEntity(body);
				httpPost.setEntity(entity);
			}
			CloseableHttpResponse response = httpclient.execute(httpPost);
			Header[] headers = response.getAllHeaders();
			
			for( Header header:headers){
				System.out.println(header);
			}

			rspEntity = response.getEntity();

		}catch(Exception e){
			e.printStackTrace();
		}

		if( output ){
			try{
				System.out.println("=================================");
				System.out.println(EntityUtils.toString(rspEntity,Consts.UTF_8));
				System.out.println("=================================");
	    	 }catch(Exception e){

			}
		}else{
			System.out.println("=================================");
		}

		try{
			return EntityUtils.toByteArray(rspEntity);
		}catch(Exception e){
			e.printStackTrace();
		}

		 return null;
	}	
	
	public static String doPostToString(String url, String param) {
		List<NameValuePair> pList = getParam(param);
		return doPostToString(url,pList);
	}

	/**
	 * 发送请求报文，得到响应报文
	 *
	 * @param url 登录请求URL
	 * @param pList 是否包含请求参数
	 * @return
	 * @throws KeyManagementException, NoSuchAlgorithmException,
	 * ClientProtocolException, IOException
	 * @throws java.security.NoSuchAlgorithmException
	 * @throws org.apache.http.client.ClientProtocolException
	 */
	public static String doPostToString(
		String url, List<NameValuePair> pList){
		
		HttpPost httpPost = new HttpPost(url);
		try{
			if (pList != null) {
				httpPost.setEntity(new UrlEncodedFormEntity(pList));
			}
			CloseableHttpResponse response = httpclient.execute(httpPost);
			String value = EntityUtils.toString(response.getEntity(),Consts.UTF_8);
			return value;
		}catch(Exception e){
			e.printStackTrace();	
		}

		return "";
	}

	public static List<NameValuePair> getParam(String url) {
		int pos = url.indexOf('?');
		if (pos == -1) {
			return null;
		}
		List<NameValuePair> formparams = new ArrayList<>();
		url = url.substring(pos + 1);
		String[] params = url.split("&");
		String reg = "(.+?)=(.+)";
		Pattern pattern = Pattern.compile(reg);
		for (String p : params) {
			String key = "";
			String value = "";
			Matcher matcher = pattern.matcher(p);
			if (matcher.find()) {
				key = matcher.group(1);
				value = matcher.group(2);
			}
			formparams.add(new BasicNameValuePair(key, value));
		}
		return formparams;
	}

	public static String get(String url){
		return doGetToString(url,"");
	}

	public static String doGetToString(String url, String cookie){
		return doGetToString(url,"utf-8","");
	}

	public static String doGetToString(String url, String encoding, String cookie) {
		String content = "";
		HttpGet httpGet = new HttpGet(url);
		if( cookie!=null && !cookie.isEmpty() ){
			httpGet.setHeader("Cookie",cookie);
		}else{
			httpGet.setHeader("Cookie","");
		}
		try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
			HttpEntity entity = response.getEntity();
			InputStream in = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, encoding));
			String line;
			while ((line = reader.readLine()) != null) {
				content += line;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return content;
	}

	public static List<NameValuePair> createNameValuePair(String params) {
		List<NameValuePair> nvps = new ArrayList<>();
		if (null != params && !params.trim().equals("")) {
			String[] _params = params.split("&");
			for (String _param : _params) {
				int _i = _param.indexOf("=");
				if (_i != -1) {
					String name = _param.substring(0, _i);
					String value = _param.substring(_i + 1);
					nvps.add(new BasicNameValuePair(name, value));
				}
			}
		}
		return nvps;
	}

	public static CloseableHttpClient createHttpsClient() {
		X509TrustManager x509mgr = new X509TrustManager() {

			@Override
			public void checkClientTrusted(X509Certificate[] xcs,
				String authType) throws CertificateException {

			}

			@Override
			public void checkServerTrusted(X509Certificate[] xcs,
				String authType) throws CertificateException {

			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[]{};
			}
		};

		SSLContext sslContext;
		try {
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, new TrustManager[]{x509mgr}, null);
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				sslContext,
				SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			return HttpClients.custom().setSSLSocketFactory(sslsf).build();

		} catch (KeyManagementException | NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private HttpClient() {
	}
}
