package com.test.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import javax.imageio.ImageIO;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
/**
 * �����������󹤾���
 * @author admin
 *
 */
public class HttpUtil {
	/**
	 * ����get���󣬻�ȡhtml�ַ���
	 * @param url
	 * @return
	 */
	public String sendGet(URL url) {
		HttpURLConnection connection = null;
		InputStreamReader reader = null;
		try {
			trustAllHttpsCertificates();
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			InputStream is = connection.getInputStream();
			System.out.println(connection.getContentEncoding());
			String encoding = connection.getContentEncoding();
			if ("gzip".equals(encoding)) {
				GZIPInputStream gZIPInputStream = new GZIPInputStream(is);
	             reader = new InputStreamReader(gZIPInputStream, "UTF-8");
	           
	        } else {
	        	 reader = new InputStreamReader(is, "UTF-8");
	        }
			BufferedReader br = new BufferedReader(reader);
			String str = "";
			String readLine = "";
			while ((readLine = br.readLine()) != null) {
				str += readLine + "\n";
			}
			
			
			return str;

		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		} catch (final Exception e1) {
			e1.printStackTrace();
			return null;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
	/**
	 * ����ͼƬ����
	 * @param url ͼƬurl
	 * @param formatName �ļ���ʽ����
	 * @param localFile ���ص������ļ�
	 * @return �����Ƿ�ɹ�
	 */
	public static boolean downloadImage(String imageUrl,String filePath) {
		String formatName = imageUrl.substring(imageUrl.lastIndexOf(".")+1);
		File outFile = new File(filePath+"."+formatName);
		File dir = outFile.getParentFile();
		if(!dir.exists()){
			dir.mkdirs();
		}
		try {
			trustAllHttpsCertificates();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		boolean isSuccess = false;
		URL url = null;
		try {
			url = new URL(imageUrl);
			isSuccess = ImageIO.write(ImageIO.read(url), formatName, outFile);
		} catch (Exception e) {
		}
		return isSuccess;
	}
/**
 * ����ssl
 * @throws Exception
 */
	private static void trustAllHttpsCertificates() throws Exception {
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
		javax.net.ssl.TrustManager tm = new miTM();
		trustAllCerts[0] = tm;
		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String urlHostName, SSLSession session) {
				System.out.println("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
				return true;
			}
		};

		HttpsURLConnection.setDefaultHostnameVerifier(hv);
	}
}
