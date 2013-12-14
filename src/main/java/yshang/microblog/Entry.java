package yshang.microblog;

import java.text.SimpleDateFormat;
import java.util.Date;

import yshang.microblog.tencent.TencentUserComments;

/**
 * Main entry for the program
 * if auth is not authorized 
 * QQ: Run TestOAuthV2AuthorizeCodeGrantAndAPI.java
 * t163: Run
 * @author ys439
 *
 */
public class Entry {

	public static void main(String[] args) throws Exception{
	    try {
	    	
	    	/**
	    	 * Tencent Official blog search
	    	 * using user timeline API and re_list API
	    	 */
//	    	String username_jiehunba = "VIPpoet";
//	    	String username_baba = "babaqunaer5229";
//	    
//	    	Comment babacomment = CommentFactory.createComment("qq");
//	    	babacomment.setWritenFile("qq_babaqunaer.txt");
	    	/**
	    	 * search all the existing tweets from babaqunaer5229
	    	 */
//	    	babacomment.getAllComments(username_baba);
	    	
	    	/**
	    	 * real time search, set a start time and terminated time
	    	 */
//	    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			Date start = df.parse("2013-12-13 08:00:00");
//			Date end = df.parse("2013-12-13 15:00:00");
//			long interval = 60000;
//	    	babacomment.getRealtimeComments(username_baba, start, end, interval);
	    	
	    	
	    	/**
	    	 * t163 search
	    	 * using searchAPI
	    	 */
	    	Comment babasearch = CommentFactory.createComment("t163");
	    	babasearch.setWritenFile("t163_baba_test.txt");
	    	String query = "爸爸去哪儿";
	    	
	    	/**
	    	 * search all
	    	 */
	    	babasearch.getAllComments(query);
	    	
	    	long onehour=3600000;
	    	try{Thread.sleep(onehour);}catch(Exception e){e.printStackTrace();}
	    	query = "咱们结婚吧";
	    	babasearch.getAllComments(query);
	    	
	    	/**
	    	 * real time search
	    	 */
//	    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			Date start = df.parse("2013-12-13 08:00:00");
//			Date end = df.parse("2013-12-13 15:00:00");
//			long interval = 60000;
//	    	babasearch.getRealtimeComments(query, start, end, interval);
	    	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
