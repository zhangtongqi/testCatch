package com.test.main;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MainInCom {

	public static final String FILE_DIR = "C://abc";
	public static final String BASE_HOST = "https://www.255mk.com";
	public static final String CURRENT_PAGE = "/pic/5/2020-01-10/25477.html";
	public static final int CURRENT_INDEX = 1;
	public static ExecutorService executor = Executors.newFixedThreadPool(32);

	public static void main(String[] args) throws IOException {
		
		String nextPage = downloadPage(BASE_HOST+CURRENT_PAGE,CURRENT_INDEX);
		int titleIndex = CURRENT_INDEX;

		while (nextPage != null) {
			String currentPage = nextPage;
			while (true) {
				ThreadPoolExecutor tpe = ((ThreadPoolExecutor) executor);
				if (tpe.getQueue().size()<=17) {
					break;
				}
			}
			titleIndex ++;
			try {
				nextPage = downloadPage(BASE_HOST + nextPage,titleIndex);
			} catch (Exception e) {
			}
			
			if(nextPage == null){
				try {
					nextPage = downloadPage(BASE_HOST + currentPage,titleIndex);
				} catch (Exception e) {
				}
				
			}
			if(nextPage == null){
				try {
					nextPage = downloadPage(BASE_HOST + currentPage,titleIndex);
				} catch (Exception e) {
				}
			}
			if(nextPage == null){
				System.out.println("nextPage ==> "+currentPage+"titleIndex ==> " + titleIndex);
			}
		}

	}

	public static String downloadPage(String url,final int titleIndex) {
		// 获得一个和网站的链接，注意是Jsoup的connect
		URL urldd = null;
		try {
			urldd = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		HttpUtil cu = new HttpUtil();
		String html = cu.sendGet(urldd);
		Document doc = Jsoup.parse(html);
		Elements title = doc.select("title");
		System.out.println(title.html());
		Elements content = doc.select("div[class=content]").select("p").select("img");
		for (int i = 0; i < content.size(); i++) {
			final int index = i + 1;
			Element link = content.get(i);
			final String linkHref = link.attr("src");
			final String titleStr = title.html();
			executor.execute(new Runnable() {
				@Override
				public void run() {
					String fileName = FILE_DIR + File.separator + titleIndex +"_";
					if(titleStr.contains("[")){
						fileName += titleStr.substring(0, titleStr.lastIndexOf("[")) + "_"
								+ index;
					}else{
						fileName += titleStr+ "_" + index;
					}
					
					try {
						HttpUtil.downloadImage(linkHref, fileName);
					} catch (Exception e) {
						try {
							HttpUtil.downloadImage(linkHref, fileName);
						} catch (Exception e3) {
							try {
								HttpUtil.downloadImage(linkHref, fileName);
							} catch (Exception e2) {
								System.out.println("linkHref ==> "+linkHref+"         fileName ==> "+fileName);
							}
						}
					}
				}
			});
		}
		for (Element next : doc.select("td")) {
			String htmlStr = next.html();
			if (htmlStr.contains("上一篇")) {
				System.out.println(next.select("a").attr("href"));
				return next.select("a").attr("href");
			}

		}
		return null;
	}
}
