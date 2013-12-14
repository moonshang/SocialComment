package yshang.microblog.tencent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Timer;

import yshang.microblog.Comment;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.tencent.weibo.api.StatusesAPI;
import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.oauthv2.OAuthV2;

/**
 * Using Tencent user timeline api and re_list api to get the comments for the user timeline
 * @author ys439
 *
 */
public class TencentUserComments implements Comment{
	
	private static OAuthV2 oAuth=new OAuthV2();

	static String outfile = "entry.testout.txt";

	HashSet<String> loadedCommentsIds = new HashSet<String>();
	
	public TencentUserComments(){
		init(oAuth);
	}
	
	public void setWritenFile(String filename){
		TencentUserComments.outfile = filename;
	}
	
	public void getRealtimeComments(String user, Date start, Date end, long interval){
		int times=0;
		Date current;
		do{
    		System.out.println("Real time search for username:"+user+" "+times+" times.");
    		this.getRealTimeCommentsInPeriod(user, start);
    		times++;
    		current = new Date();
    		try{
    			Thread.sleep(interval);
    		}catch(Exception e){};
    	}while(end.getTime()>current.getTime());
	}
	
	
	
	public void getAllComments(String username){

		//search params
		String response;
        String format = "json";
        String pageflag = "1";
        String pagetime = "0";
        String reqnum = "300";
        String lastid = "";
        String contenttype = "0";
        String fopenid = "";
        String name=username;
        String type = "0";
        
        //comment paras
        String cresponse;
		String cformat = "json";
		String cflag = "2";
		String crootid;
		String cpageflag = "0";
		String cpagetime = "1";
		String creqnum = "100";
		String twitterid = "1";
       
		int callLimit = 1000;
		 StatusesAPI statusesAPI= new StatusesAPI(oAuth.getOauthVersion());
		 TAPI tapi = new TAPI(oAuth.getOauthVersion());
		 try{
			 BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(TencentUserComments.outfile),"UTF-8"));
			 
			 int callcount = 0;
			 long onehourms = 3600000;
			 
	         do{
	        	 System.out.println("pageflag="+pageflag+" pagetime="+pagetime+" lastid="+lastid);
	        	 response = statusesAPI .userTimelineIds(oAuth, format, pageflag, pagetime, reqnum, lastid, name, fopenid, type, contenttype);
	        	 callcount++;
	        	 if(callcount>callLimit-10){
	        		 System.out.println("Api limit is about to reach:"+callcount + " Wait for one hour");
	        		 try { Thread.sleep ( onehourms ) ; } catch (InterruptedException ie){}
	        	 }
	        	 JSONObject responseObject = JSONObject.fromObject(response);
	        	 JSONObject dataJsonObject = responseObject.getJSONObject("data");
	        	 String hasnext = dataJsonObject.getString("hasnext");
	        	 System.out.println(dataJsonObject);
	             JSONArray infoArray = dataJsonObject.getJSONArray("info");
	             
	             //param for next page
	             JSONObject lasttweet = (JSONObject)infoArray.get(infoArray.size()-1);
	             pageflag = "1";
	        	 pagetime = lasttweet.getString("timestamp");
	        	 lastid = lasttweet.getString("id");
	             
	             for(int i = 0;i<infoArray.size();i++){//for each tweet
	            	 JSONObject tweet = (JSONObject)infoArray.get(i);

	            	 //read the comments for this tweet
	        		 crootid = tweet.getString("id");
	        		 do{
	        			 cresponse = tapi.reList(oAuth, cformat, cflag, crootid, cpageflag, cpagetime, creqnum, twitterid);
	            		 callcount++;
	            		 if(callcount > callLimit-10){
	            			 System.out.println("Api limit is about to reach:"+callcount + " Wait for one hour");
	                		 try { Thread.sleep ( onehourms ) ; } catch (InterruptedException ie){}
	                	 }
	            		 
	            		 JSONObject cresponseObject = JSONObject.fromObject(cresponse);
	                	 JSONObject cdataJsonObject = cresponseObject.getJSONObject("data");
	                	 if(cdataJsonObject.isNullObject())break;
	                	 int chasnext = cdataJsonObject.getInt("hasnext");
	                	 
	     				 if(cdataJsonObject.isNullObject())break;
	     				 JSONArray commentArray = cdataJsonObject.getJSONArray("info");
	     				 if(commentArray==null)break;
	     				 JSONObject lastcomment = (JSONObject)commentArray.get(commentArray.size()-1);
	     				 
	     				 cpageflag = "1";
	            		 cpagetime = lastcomment.getString("timestamp");
	            		 twitterid = lastcomment.getString("id");
	                	 bw.write(cresponse);
	                	 bw.newLine();
	                	 bw.flush();
	                	 
	                	 if(chasnext==1)break;//hasnext==1 表示拉取完毕，没有微博可以拉取
	                	 
	        		 }while(true);
	             }
	             
	             if(hasnext.equals("1"))break;
	         }while(true);
	         bw.close();
	         statusesAPI.shutdownConnection();
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 
	
	}
	
	
	public void getTodayComment(String username){
		Date today = new Date();
		try{
			this.getDateComments(username, today);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Get the tweets for a specific date
	 * @param username
	 * @param date
	 * @throws Exception
	 */
	private void getDateComments(String username, Date date) throws Exception{
		
		//search params
		String response;
        String format = "json";
        String pageflag = "1";
        String pagetime = "0";
        String reqnum = "300";
        String lastid = "";
        String contenttype = "0";
        String fopenid = "";
        String name=username;
        String type = "0";
        
        //comment paras
        String cresponse;
		String cformat = "json";
		String cflag = "2";
		String crootid;
		String cpageflag = "0";
		String cpagetime = "1";
		String creqnum = "100";
		String twitterid = "1";
		int callLimitation = 1000;
       
		
		 StatusesAPI statusesAPI= new StatusesAPI(oAuth.getOauthVersion());
		 TAPI tapi = new TAPI(oAuth.getOauthVersion());
		 BufferedWriter bw = new BufferedWriter(new FileWriter(TencentUserComments.outfile));
		 
		 int callcount = 0;
		 long onehourms = 3600000;
		 
         SimpleDateFormat df = new SimpleDateFormat("yyyy-DD");
         
         String cal = df.format(date);
         String[] part = cal.split("-");
         int thisyear = Integer.parseInt(part[0]);
         int todayInYear = Integer.parseInt(part[1]);
         
         do{
        	 System.out.println("pageflag="+pageflag+" pagetime="+pagetime+" lastid="+lastid);
        	 response = statusesAPI .userTimelineIds(oAuth, format, pageflag, pagetime, reqnum, lastid, name, fopenid, type, contenttype);
        	 callcount++;
        	 if(callcount>callLimitation-10){
        		 System.out.println("Api limit is about to reach:"+callcount + " Wait for one hour");
        		 try { Thread.sleep ( onehourms ) ; } catch (InterruptedException ie){}
        	 }
        	 JSONObject responseObject = JSONObject.fromObject(response);
        	 JSONObject dataJsonObject = responseObject.getJSONObject("data");
        	 if(dataJsonObject.isNullObject())break;
        	 System.out.println(dataJsonObject);
             JSONArray infoArray = dataJsonObject.getJSONArray("info");
             for(int i = 0;i<infoArray.size();i++){
            	 System.out.println(i);
            	 JSONObject tweet = (JSONObject)infoArray.get(i);
            	 //return values
            	 long timestamp = tweet.getLong("timestamp");
            	 Date latest = new Date(timestamp*1000);
            	 String cal4tweets = df.format(latest);
            	 part = cal4tweets.split("-");
                 int tweetyear = Integer.parseInt(part[0]);
                 int tweetdayInYear = Integer.parseInt(part[1]);
                 System.out.println(latest);
                 if(tweetyear<=thisyear && tweetdayInYear<todayInYear)return;
                 else{
                	 long id = tweet.getLong("id");
                	 pagetime = String.valueOf(timestamp);
                	 pageflag = "1";
                	 lastid = String.valueOf(id);
                	 
                	 if(tweetyear>=thisyear && tweetdayInYear>todayInYear)continue;
                	 else if(tweetyear==thisyear && tweetdayInYear==todayInYear){
                    	 //read the comments for this tweet
                		 crootid = String.valueOf(id);
                		 do{
                			 cresponse = tapi.reList(oAuth, cformat, cflag, crootid, cpageflag, cpagetime, creqnum, twitterid);
                    		 callcount++;
                    		 if(callcount>callLimitation-10){
                    			 System.out.println("Api limit is about to reach:"+callcount + " Wait for one hour");
                        		 try { Thread.sleep ( onehourms ) ; } catch (InterruptedException ie){}
                        	 }
                    		 
                    		 JSONObject cresponseObject = JSONObject.fromObject(cresponse);
                        	 JSONObject cdataJsonObject = cresponseObject.getJSONObject("data");
                        	 if(cdataJsonObject.isNullObject())break;
                        	 String hasnext = cdataJsonObject.getString("hasnext");
                        	 
             				 if(cdataJsonObject.isNullObject())break;
             				 JSONArray commentArray = cdataJsonObject.getJSONArray("info");
             				 JSONObject lastcomment = (JSONObject)commentArray.get(commentArray.size()-1);
             				 
             				 cpageflag = "1";
                    		 cpagetime = lastcomment.getString("timestamp");
                    		 twitterid = lastcomment.getString("id");
                        	 bw.write(cresponse);
                        	 bw.newLine();
                        	 bw.flush();
                        	 
                        	 if(hasnext.equals("1"))break;//hasnext==1 表示拉取完毕，没有微博可以拉取
                        	 
                		 }while(true);
                		 
                     }
                 }
             }
         }while(true);
         
         bw.close();
         statusesAPI.shutdownConnection();
	}
	
	/**
	 * 
	 * @param username
	 * @param start
	 */
	private void getRealTimeCommentsInPeriod(String username, Date start){
		
		//search params
		String response;
        String format = "json";
        String pageflag = "1";
        String pagetime = "0";
        String reqnum = "300";
        String lastid = "";
        String contenttype = "0";
        String fopenid = "";
        String name=username;
        String type = "0";
        
        //comment params
        String cresponse;
		String cformat = "json";
		String cflag = "2";
//		String crootid;
		String cpageflag = "0";
		String cpagetime = "1";
		String creqnum = "100";
		String lasttwitterid = "1";

		int callLimits = 1000;
		int calltimes = 0;
		long onehour = 3600000;
		
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outfile),true));
			StatusesAPI statusesAPI = new StatusesAPI(oAuth.getOauthVersion());
			TAPI tapi = new TAPI(oAuth.getOauthVersion());
			
			do{
				
				//call user timeline
				response = statusesAPI.userTimelineIds(oAuth, format, pageflag, pagetime, reqnum, lastid, name, fopenid, type, contenttype);
				
				calltimes++;
				if(calltimes>callLimits-10){
					System.out.println("Api limit is about to reach:"+calltimes + " Wait for one hour");
					Thread.sleep(onehour);
				}
				JSONObject responseJSONObject = JSONObject.fromObject(response);
				
				JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
				if(dataJSONObject.isNullObject())return;
				int timeline_hasnext = dataJSONObject.getInt("hasnext");
				JSONArray infoArray = dataJSONObject.getJSONArray("info");
				
				//for next time call
				JSONObject lasttweet = infoArray.getJSONObject(infoArray.size()-1);
				pageflag="1";
				pagetime=lasttweet.getString("timestamp");
				lastid = lasttweet.getString("id");
				
				for(int i =0;i<infoArray.size();i++){//for each tweet
					JSONObject tweet = (JSONObject)infoArray.get(i);
					String tweetid = tweet.getString("id");
					String tweetTimestamp = tweet.getString("timestamp");
					
//					System.out.println("start:"+start+"  start-unix:"+start.getTime()/1000+" tweet:"+tweetTimestamp);
					if(Long.parseLong(tweetTimestamp) < start.getTime()/1000){
						continue;
					}
					do{
						//call tweet relist
						cresponse = tapi.reList(oAuth, cformat, cflag, tweetid, cpageflag,cpagetime, creqnum, lasttwitterid);
						
						calltimes++;
						
						if(calltimes>callLimits-1){
							System.out.println("Api limit is about to reach:"+calltimes + " Wait for one hour");
							Thread.sleep(onehour);
						}
						
						//get all reviews
						JSONObject cresponseJSONObject = JSONObject.fromObject(cresponse);
						JSONObject cdataJSONObject = cresponseJSONObject.getJSONObject("data");
						if(cdataJSONObject.isNullObject())break;
						int review_hasnext = cdataJSONObject.getInt("hasnext");
						JSONArray comments_jsonarray = cdataJSONObject.getJSONArray("info");
						
						//for next time call
						JSONObject lastcomment = comments_jsonarray.getJSONObject(comments_jsonarray.size()-1);
						cpageflag="1";
						cpagetime=lastcomment.getString("timestamp");
						lasttwitterid=lastcomment.getString("id");
						
						for(int j = 0;j<comments_jsonarray.size();j++){
							JSONObject comment = comments_jsonarray.getJSONObject(j);
							String comment_id = comment.getString("id");
							if(loadedCommentsIds.contains(comment_id)){
								review_hasnext=1;
								continue;
							}
							else{
								loadedCommentsIds.add(comment_id);
								comment.accumulate("rootid", tweetid);
								comment.accumulate("root_timestamp", tweetTimestamp);
								bw.write(comment.toString());
								bw.newLine();
								bw.flush();
							}
						}
						if(review_hasnext==1)break;
					}while(true);
				}
				if(timeline_hasnext==1)break;
			}while(true);
			bw.close();
			statusesAPI.shutdownConnection();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	

//	private boolean isBetweenPeriod(Date time, Date start, Date end){
//		if(time.getTime()>start.getTime() && time.getTime()<end.getTime()){
//			return true;
//		}
//		else{
//			return false;
//		}
//	}
//	private boolean isBefore(Date time, Date compare){
//		if(time.getTime()<compare.getTime())return true;
//		else return false;
//	}
//	private boolean isAfter(Date time, Date compare){
//		if(time.getTime()>compare.getTime())return true;
//		else return false;
//	}
	

	private static void init(OAuthV2 oAuth) {
		
        oAuth.setClientId("801454503");
        oAuth.setClientSecret("b50b49cf496b0be09a744ff6acdf4547");
        oAuth.setRedirectUri("http://app.t.qq.com/app/playtest/801454503");
        oAuth.setAccessToken("7ccefe3a0537b81b68147331c76a1bb5");
        oAuth.setOpenid("B09D87E4CE161C7E116501F16D32CF95");
        oAuth.setOpenkey("8EF9E831CB488AB6693BF578826D06DC");
        oAuth.setExpiresIn("8035200");
    }	
}


