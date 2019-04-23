package com.suneee.eas.common.bean.push;

/**
 * @program: ems
 * @description: 阿里云推送调用中心常量类
 * @author: liuhai
 * @create: 2018-06-21 15:27
 **/
public class PushConstants {

    //推送设备类型 android:安卓， ios: 苹果
    /**
     * 设备类型：android/安卓
     * **/
    public static final String Android = "android";
    /**
     * 设备类型：ios:/苹果
     * **/
    public static final String Ios = "ios";

    /* --------------------------------------------- */

    //推送消息类型 message:消息， notice:通知
    /**
     * 消息类型：message/消息
     * **/
    public static final String PUSH_MESSAGE = "message";
    /**
     * 消息类型：notice/通知
     * **/
    public static final String PUSH_NOTICE = "";

    /* --------------------------------------------- */

    //推送操作类型
    /**
     * 推送消息/通知
     * **/
    public static final String OPERATION_PUSH = "mobilePush";

    /* --------------------------------------------- */

    //推送提示音类型
    /**
     * 提示音类型:default/默认
     * **/
    public static final String PUSH_VOICE_DEFAULT = "default";
}
