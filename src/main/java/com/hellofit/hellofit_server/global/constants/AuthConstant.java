package com.hellofit.hellofit_server.global.constants;

public class AuthConstant {
    public static String ACCESS_TOKEN_COOKIE = "attk";
    public static String REFRESH_TOKEN_COOKIE = "rttk";
    public static String XSRF_TOKEN_COOKIE = "xsrftk";

    public static Integer ACCESS_TOKEN_LIFETIME = 15 * 60;
    public static Integer REFRESH_TOKEN_LIFETIME = 7  * 24 * 3600;
}
