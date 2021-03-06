package com.brins.lightmusic.api.service

import com.brins.lightmusic.common.AppConfig.*
import com.brins.lightmusic.model.userlogin.LogoutResult
import com.brins.lightmusic.model.userlogin.UserLoginResult
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface LoginService {

    /*
* 登录网络接口
* */
    @GET(LOGIN.LOGIN_EMAIL)
    fun loginEmail(@Query("email") email: String, @Query("password") password: String): Observable<UserLoginResult>

    @GET(LOGIN.LOGOUT)
    fun logout(): Observable<LogoutResult>

    @GET(LOGIN.CHECK_CODE)
    fun getCheckCode(@Query("phone") phone: String): Observable<String>

    @GET(LOGIN.VERIFY_CODE)
    fun verifyCode(@Query("phone") phone: String, @Query("captcha") code: String): Observable<UserLoginResult>

    @GET(LOGIN.LOGIN_CELLPHONE)
    fun loginCellphone(@Query("phone") phone: String, @Query("password") password: String): Observable<UserLoginResult>
}