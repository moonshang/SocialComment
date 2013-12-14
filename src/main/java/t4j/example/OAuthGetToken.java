package t4j.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import t4j.TBlog;
import t4j.data.Status;
import t4j.data.User;
import t4j.http.AccessToken;
import t4j.http.RequestToken;

/**
 * 
 * 这个例子演示了如何获取 access token，是使用API的第一步。
 * 务必看懂这个例子的代码，熟悉oauth协议
 * 
 * @author gcwang
 *
 */
public class OAuthGetToken {
	
	
	public static void main(String[] args) throws Exception{

		// 设置 consumer key, consumer secret
		// 也可以在 t4j.properties 中设置，这个文件应当放置在：源代码目录的根目录
		System.setProperty("tblog4j.oauth.consumerKey", "p416VBOgMq6MRtzu");
    	System.setProperty("tblog4j.oauth.consumerSecret", "0q1A21x2vKVhgb4XJzLxQgiBDaq0DsU6");
    	
    	// 暂时把debug关了。减少干扰信息
    	System.setProperty("tblog4j.debug", "true");
    	
		TBlog tblog = new TBlog();
		
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
		tblog.setToken(accessToken.getToken(), accessToken.getTokenSecret());
		
		// 检查登录的用户
		User user = tblog.verifyCredentials();
		System.out.println("登录的用户： " + user.getName());
		System.out.println("");
		
		System.out.println("我的主页 home timeline： " + user.getName());
		// 读取我的微博主页
//		List<Status> statuses = tblog.getHomeTimeline();
//		for(Status status: statuses){
//			System.out.println(status.getUser().getName() + ": " + status.getText());
//		}
		
		// 发微博
//		tblog.updateStatus("update status from JAVA SDK");
		
		//search blog
		
//		BufferedWriter bw = new BufferedWriter(new FileWriter("baba_out.txt"));
//		SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
//		String baba_start = "29-11-2013 6:00:00";
//		Date broadcast_time  = sdf.parse(baba_start);
//		String baba_end = "29-11-2013 8:00:00";
//		Date end_time  = sdf.parse(baba_end);
//		for(int page = 0;;page++){
//			
//			List<Status> statuses = tblog.searchStatus("爸爸去哪儿",page,20);
//			if(statuses==null)break;
//			for(Status status:statuses){
//				Date create_at = status.getCreatedAt();
//				if(create_at.compareTo(broadcast_time)>0 && create_at.compareTo(end_time)<0){
//					System.out.println(status.getCreatedAt().toString()+" : "+status.getText());
//					bw.write(status.getCreatedAt().toString()+" : "+status.getText());
//					bw.flush();
//				}
//				
//			}
//			
//		}
//		bw.close();
		
		long defaultInterval = 10000;
		long onehourms = 3600000;
		int count = 0;
		String[] queries = new String[]{
			"爸爸去哪儿",
			"张亮",
			"郭涛",
			"王岳伦",
			"田亮",
			"林志颖",
			"王诗龄",
			"Kimi",
			"田雨橙",
			"森蝶",
			"郭子睿",
			"张悦轩",
			"Angela",
			"Cindy",
		};
//		String[] queries = new String[]{
//			"咱们结婚吧",
//			"果然 杨桃",
//			"黄海波 高圆圆"
//		};
		
//		String[] queries = new String[]{
//				"我们结婚吧",
//				"结婚吧",
//			};
		BufferedWriter bw = new BufferedWriter(new FileWriter("jiehunba_baba_whole_part2.txt"));
		for(String q:queries){
			for(int page = 1;;page++){
				
				List<Status> statuses = tblog.search(q,page,20);
				count++;
				if(statuses.size()==0)break;
				for(Status status:statuses){
						System.out.println(status.getCreatedAt().toString()+" : "+status.getText());
						User thisuser = status.getUser();
						
						bw.write(status.getCreatedAt().toString()+"@@@"+thisuser.getName()+"@@@"+thisuser.getStatusesCount()+"@@@"+thisuser.getFriendsCount()+"@@@"+thisuser.getFollowersCount()+"@@@"+status.getCommentsCount()+"@@@"+status.getRetweetCount()+"@@@"+status.getText()+"\n");
						bw.flush();
				}
				if(page%5==0)Thread.sleep(defaultInterval);
				if(count > 190){
					Thread.sleep(onehourms);
					count=0;
				}
			}
			
		}
		bw.close();
		
		
		
		
	}
}
