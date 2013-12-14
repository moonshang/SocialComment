package yshang.microblog;

import yshang.microblog.t163.My163Search;
import yshang.microblog.tencent.TencentUserComments;


/**
 * Factory to create Comment object
 * @author ys439
 *
 */
public class CommentFactory {
	
	/**
	 * 
	 * @param flag now:"qq" future: "sina","163"...
	 * @return yshang.tencent.Comment
	 */
	public static Comment createComment(String flag){
		Comment newComment = null;
		
		if(flag.equals("qq")){
			newComment = new TencentUserComments();
		}
		else if(flag.equals("t163")){
			newComment = new My163Search();
		}
		return newComment;
		
	}

}
