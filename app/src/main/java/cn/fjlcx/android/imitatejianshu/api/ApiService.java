package cn.fjlcx.android.imitatejianshu.api;

import java.math.BigInteger;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 请求接口
 * @author ling_cx
 * @date 2017/11/02.
 */

public interface ApiService {

	/**
	 * 获取当前登录用户及其所关注（授权）用户的最新微博
	 */

	/*eg.
	@GET("2/statuses/home_timeline.json")
	Observable<HomeResult> home_timeline(@Query("access_token") String access_token,
										 @Query("since_id") BigInteger since_id,
										 @Query("max_id") BigInteger max_id,
										 @Query("count") int count,
										 @Query("page") int page,
										 @Query("base_app") int base_app,
										 @Query("feature") int feature,
										 @Query("trim_user") int trim_user);*/

}
