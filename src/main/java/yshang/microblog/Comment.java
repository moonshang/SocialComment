package yshang.microblog;

import java.util.Date;

/**
 * This interface is to get the comments from Tencent Microblog user
 * @author ys439
 *
 */
public interface Comment {
	
	
	
	public void setWritenFile(String filename);
	
	/**
	 * Return the latest tweets submited today from a user
	 * @param username
	 */
	public void getTodayComment(String username);
	
	/**
	 * Get all the comments and retweets under the tweets of a user
	 * @param username e.g.: babaqunaer5229
	 */
	public void getAllComments(String username);
	
	/**
	 * Realtime search
	 * @param user
	 * @param start
	 * @param end
	 * @param interval Time interval(Mills) between searches
	 */
	public void getRealtimeComments(String user, Date start, Date end, long interval);
	
}
