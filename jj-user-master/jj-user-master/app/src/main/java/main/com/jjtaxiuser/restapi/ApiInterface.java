package main.com.jjtaxiuser.restapi;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by technorizen on 14/2/18.
 */

public interface ApiInterface {
    @GET("login?")
    Call<ResponseBody> loginCall(@Query("mobile") String email, @Query("password") String password, @Query("register_id") String register_id, @Query("40.178088333333335") String lat, @Query("lon") String lon, @Query("type") String type,@Query("continue") String login_sts) ;
    @GET("signup?")
    Call<ResponseBody> SignupCall(@Query("first_name") String first_name, @Query("last_name") String last_name, @Query("mobile") String phone, @Query("email") String email, @Query("password") String password, @Query("register_id") String register_id, @Query("lat") String lat, @Query("lon") String lon, @Query("type") String type) ;
    @GET("forgot_password?")
    Call<ResponseBody> ForgotCall(@Query("email") String email,@Query("type") String type) ;
    @GET("favorite_driver?")
    Call<ResponseBody> addRemFavorite(@Query("user_id") String user_id, @Query("driver_id") String driver_id) ;

    @GET("getcountryapp")
    Call<ResponseBody> GetCountryCall();
    @GET("getstateapp?")
    Call<ResponseBody> GetStateCall(@Query("pais") String pais);
    @GET("getcityapp?")
    Call<ResponseBody> GetCityCall(@Query("cityid") String cityid);
    @GET("getcategorytype")
    Call<ResponseBody> getMainCategoryCall();
    @GET("getcategoriesapp?")
    Call<ResponseBody> getSubCategory(@Query("type") String type);
    @GET("showsubapp?")
    Call<ResponseBody> getExtraSubCategory(@Query("type") String type, @Query("id") String id);
    @GET("showitemapp?")
    Call<ResponseBody> getAutoExtaraSub(@Query("type") String type, @Query("id") String id, @Query("subc") String subc);
    @GET("plangetapp")
    Call<ResponseBody> getAppPricePlan();
//http://testing.bigclicki.com/webservice/plangetapp
//http://testing.bigclicki.com/webservice/getcategorytype
// http://testing.bigclicki.com/webservice/getcategoriesapp?type=atacado
//http://testing.bigclicki.com/webservice/showsubapp?type=servicos&id=2240
// http://testing.bigclicki.com/webservice/showitemapp?id=24&type=automoveis&subc=3042
}
