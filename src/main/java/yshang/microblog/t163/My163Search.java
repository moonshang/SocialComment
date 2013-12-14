package yshang.microblog.t163;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import t4j.TBlog;
import t4j.TBlogException;
import t4j.data.Status;
import t4j.http.AccessToken;
import t4j.http.RequestToken;
import yshang.microblog.Comment;

/**
 * Using T163 search API to retrieve the tweets containing query terms
 * @author ys439
 *
 */
public class My163Search implements Comment{
	
	private static TBlog tblog;
	public static String outfile;
	private HashSet<Long> loadedIds;
	
	public void setWritenFile(String filename){
		My163Search.outfile = filename;
	}
	
	public My163Search(){
		try{
			My163Search.init();
			this.loadedIds = new HashSet<Long>();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	static private void oauthInit() throws TBlogException{

		// 设置 consumer key, consumer secret
		// 也可以在 t4j.properties 中设置，这个文件应当放置在：源代码目录的根目录
		System.setProperty("tblog4j.oauth.consumerKey", "p416VBOgMq6MRtzu");
		System.setProperty("tblog4j.oauth.consumerSecret",
				"0q1A21x2vKVhgb4XJzLxQgiBDaq0DsU6");

		// 暂时把debug关了。减少干扰信息
		System.setProperty("tblog4j.debug", "true");
		
		tblog = new TBlog();
		
		RequestToken requestToken = tblog.getOAuthRequestToken();
		
		// 因为request token是临时生成的。授权后就没有保存的必要了
		// 这里演示一下
		System.out.println("这是request token: " + requestToken.getToken());
		System.out.println("这是token secret: " + requestToken.getTokenSecret());
		
		// 这个url很重要，就是你需要授权的页面，在浏览器中打开这个页面，完成授权
		System.out.println("在浏览器中打开这个页面授权：" + requestToken.getAuthenticationURL());
				
		// 这里停一下，等授权完成后，继续进行
		System.out.println("授权完成后，才能输入回车继续 ...");
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		AccessToken accessToken = tblog.getOAuthAccessToken(requestToken);
		System.out.println("授权后的access token和 secret， 可以保存下来长久使用");
		System.out.println("access token: " + accessToken.getToken());
		System.out.println("access token secret: " + accessToken.getTokenSecret());
		
		// 基本功能的演示
		System.out.println("基本功能的演示, 回车继续 ...");
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 以后使用的时候，授权过的用户，只需要读取已经保存的 access token，就可以重复使用
		tblog.setToken("d5e52e1c8833daec2c30c797d183c2f0", "c0ac0d6e0f4fca53e31b6e2db4cbd2da");
	
	}
	
	static private void init() throws Exception {
		// 设置 consumer key, consumer secret
		// 也可以在 t4j.properties 中设置，这个文件应当放置在：源代码目录的根目录
		System.setProperty("tblog4j.oauth.consumerKey", "p416VBOgMq6MRtzu");
		System.setProperty("tblog4j.oauth.consumerSecret",
				"0q1A21x2vKVhgb4XJzLxQgiBDaq0DsU6");

		// 暂时把debug关了。减少干扰信息
		System.setProperty("tblog4j.debug", "true");

		tblog = new TBlog();

		// 以后使用的时候，授权过的用户，只需要读取已经保存的 access token，就可以重复使用
		tblog.setToken("d5e52e1c8833daec2c30c797d183c2f0",
				"c0ac0d6e0f4fca53e31b6e2db4cbd2da");
	}
	
	public void getAllComments(String username){
		try{
			this.searchAll(username);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * username is the query for search
	 */
	public void getTodayComment(String username){
		Date today = new Date();
		SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		String today_ymd = ymd.format(today);
		try{
			Date start = ymdhms.parse(today_ymd+" 00:00:00");
			Date end = ymdhms.parse(today_ymd+" 23:59:59");
			this.searchPeriod(start, end, username);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void getRealtimeComments(String user, Date start, Date end, long interval){
		Date current = new Date();
		long tenmin = 600000;
		while(true){
			System.out.println(current);
			if(current.getTime()<start.getTime()){
				try{Thread.sleep(tenmin);} catch(Exception e){e.printStackTrace();};
				current = new Date();
			}
			else break;
		}
		int times=0;
		do{
    		System.out.println("Real time search for query:"+user+"  "+times+" times.");
    		try{
    			this.searchRealtime(user);
        		times++;
    			Thread.sleep(interval);
    			current = new Date();
    		}catch(Exception e){};
    	}while(end.getTime()>current.getTime());
	}
	
	/**
	 * Use the global Hashset to implement a realtime search
	 * private
	 * @param query
	 * @throws Exception
	 */
	private void searchRealtime(String query) throws Exception{
		
		int callLimit = 200;
		long defaultInterval = 10000;
		long onehourms = 3600000;
		int count = 0;
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
		for (int page = 1;; page++) {

			List<Status> statuses = tblog.search(query, page, 20);
			count++;
			if (statuses.size() == 0)break;
			for (Status status : statuses) {
				long id = status.getId();
				if(this.loadedIds.contains(id))continue;
				else{
					this.loadedIds.add(id);
					bw.write(status.getJson());
					bw.newLine();
					bw.flush();
				}
			}
			if (page % 5 == 0)
				Thread.sleep(defaultInterval);
			if (count > callLimit-10) {
				Thread.sleep(onehourms);
				count = 0;
			}
		}
		bw.close();
	}
	
	/**
	 * One time search for the tweets published in a specific time window.
	 * @param start
	 * @param end
	 * @param query
	 * @throws Exception
	 */
	private void searchPeriod(Date start, Date end, String query) throws Exception{
		int callLimit = 200;
		long defaultInterval = 10000;
		long onehourms = 3600000;
		long tenmin = 600000;
		int count = 0;
		long starttime = start.getTime();
		long endtime = end.getTime();
		
		//wait for start time, ten mins interval
		Date current = new Date();
		while(true){
			if(current.getTime()<start.getTime()){
				Thread.sleep(tenmin);
				current = new Date();
			}
			else break;
		}
		//time's up!
		BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
		for (int page = 1;; page++) {
			List<Status> statuses = tblog.search(query, page, 20);
			count++;
			if (statuses.size() == 0)break;
			for (Status status : statuses) {
				long create_at = status.getCreatedAt().getTime();
				if(create_at>starttime && create_at<endtime){
					bw.write(status.getJson());
					bw.newLine();
					bw.flush();
				}
			}
			//163's interface is a little bit fragile,
			//so it's necessary to pretend the program is a real person....
			if (page % 5 == 0)
				Thread.sleep(defaultInterval);
			if (count > callLimit-10) {
				System.out.println("Api limit is about to reach:"+count+" Wait for one hour");
				Thread.sleep(onehourms);
				count = 0;
			}
		}
		bw.close();
	}
	
	private void searchAll(String query) throws Exception{
		int callLimit = 200;
		long defaultInterval = 10000;
		long onehourms = 3600000;
		int count = 0;
		BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));

		for (int page = 1;; page++) {
			
			List<Status> statuses = tblog.search(query, page, 20);
			count++;
			if (statuses.size() == 0)break;
			for (Status status : statuses) {
				bw.write(status.getJson());
				bw.newLine();
				bw.flush();
			}
			if (page % 5 == 0)
				Thread.sleep(defaultInterval);
			if (count > callLimit-10) {
				Thread.sleep(onehourms);
				count = 0;
			}
		}
		bw.close();
	}
}
