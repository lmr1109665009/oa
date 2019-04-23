/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var phone_gap = {

 	// Application Constructor
    initialize: function() {
		phone_gap.bindEvents();
    },
    
    // Bind Event Listeners
    //
    // Bind any events that are required on startup. Common events are:
    // 'load', 'deviceready', 'offline', and 'online'.
    bindEvents: function() {
        document.addEventListener("deviceready", phone_gap.onDeviceReady, false);
    },
    
    // deviceready Event Handler
    //
    // The scope of 'this' is the event. In order to call the 'receivedEvent'
    // function, we must explicity call 'app.receivedEvent(...);'
    onDeviceReady: function() {
       document.addEventListener("backbutton", phone_gap.onBackKeyDown, false);
    },
    
    //手机物理键返回按钮处理
    onBackKeyDown: function() {
		phone_gap.back();
    },
    
    // /**
	//  * [返回]
	//  * url: 指定返回页面url，web页面一般为锚点
	//  * sucCallback: 成功回调
	//  * failCallback：失败回调
	//  **/
	// back : function(url, sucCallback, failCallback) {
	// 	url = url || "";
	// 	sucCallback = sucCallback || function(){};
	// 	failCallback = failCallback || function(){};
	//     cordova.exec(sucCallback, failCallback, "CPlugin", "back", [url]);
	// },
	
	
	// /**
	//  * [返回]
	//  * model: 要特殊处理的模块，
	//  * 值为exponent时，表示指数模块点击返回直接回到首页
	//  * sucCallback: 成功回调
	//  * failCallback：失败回调
	//  **/
	// setBackFlag : function(models, sucCallback, failCallback) {
	// 	model = model || "";
	// 	sucCallback = sucCallback || function(){};
	// 	failCallback = failCallback || function(){};
	//     cordova.exec(sucCallback, failCallback, "CPlugin", "setBackFlag", [model]);
	// },
	
	// /**
	//  * [获取系统参数]
	//  * 返回结果如 {"appCode":"213231","serverIp":"23423","encryptCode":"23424"}
	//  * sucCallback: 成功回调
	//  * failCallback：失败回调
	//  **/
	// getSysParams : function(sucCallback, failCallback) {
	// 	sucCallback = sucCallback || function(){};
	// 	failCallback = failCallback || function(){};
	//     cordova.exec(sucCallback, failCallback, "CPlugin", "getSysParams", []);
	// },
	
	
    /**
	 * [判断是否登陆, 如果登陆了返回用户所有信息]
	 * sucCallback: 成功回调，返回true
	 * failCallback：失败回调
	 **/
	isLogin :function(sucCallback, failCallback) {
		sucCallback = sucCallback || function(){};
		failCallback = failCallback || function(){};
	    cordova.exec(sucCallback, failCallback, "CPlugin", "isLogin", []);
	},

	/**
	 * [接口签名]
	 * sucCallback: 成功回调，返回true
	 * failCallback：失败回调
	 */
	signParamerter: function(jsonData,sucCallback,failCallback){
		sucCallback = sucCallback || function(){};
		failCallback = failCallback || function(){};
		cordova.exec(sucCallback, failCallback, "CPlugin", "signParamerter", [jsonData]);
	},
	
	/**
	 * [打电话]
	 * phone：电话号码, 格式：tel:18900001234
	 * sucCallback: 成功回调，返回true
	 * failCallback：失败回调
	 **/
	callPhone :function(phone, sucCallback, failCallback) {
		phone = phone || "";
		sucCallback = sucCallback || function(){};
		failCallback = failCallback || function(){};
	    cordova.exec(sucCallback, failCallback, "CPlugin", "callPhone", [phone]);
	},

		/**
	 * [图片上传]
	 * sucCallback: 成功回调，返回true
	 * failCallback：失败回调
	 **/
	uploadImg :function(sucCallback, failCallback) {
		sucCallback = sucCallback || function(){};
		failCallback = failCallback || function(){};
	    cordova.exec(sucCallback, failCallback, "CPlugin", "uploadImage", []);
	},

	/**
	 * [清缓存]
	 * sucCallback: 成功回调，返回true
	 * failCallback：失败回调
	 **/
	clearCache :function(sucCallback, failCallback) {
		sucCallback = sucCallback || function(){};
		failCallback = failCallback || function(){};
	    cordova.exec(sucCallback, failCallback, "CPlugin", "clearCache", []);
	},

	/**
	 * [应用升级]
	 * sucCallback: 成功回调，返回true
	 * failCallback：失败回调
	 **/
	upgrade :function(sucCallback, failCallback) {
		sucCallback = sucCallback || function(){};
		failCallback = failCallback || function(){};
	    cordova.exec(sucCallback, failCallback, "CPlugin", "upgrade", []);
	},

	/**
	 * [支付SDK]
	 * params, 支付参数
	 * sucCallback: 成功回调，返回true
	 * failCallback：失败回调
	 **/
	pay :function(params, sucCallback, failCallback) {
		params = params || null;
		sucCallback = sucCallback || function(){};
		failCallback = failCallback || function(){};
	    cordova.exec(sucCallback, failCallback, "CPlugin", "payActivity", [params]);
	},
	
	// /**
	//  * [打开新页面]
	//  * url: 新页面url
	//  * sucCallback: 成功回调，返回结果
	//  * failCallback：失败回调
	//  **/
	// showpage : function(url, sucCallback, failCallback) {
	// 	url = url || "";
	// 	sucCallback = sucCallback || function(){};
	// 	failCallback = failCallback || function(){};
	//     cordova.exec(sucCallback, failCallback, "CPlugin", "showpage", [url]);
	// },
	
	// /**
	//  * [打开浏览器页面]
	//  * url: 新页面url
	//  * sucCallback: 成功回调，返回结果
	//  * failCallback：失败回调
	//  **/
	// openBrowser : function(url, sucCallback, failCallback) {
	// 	url = url || "";
	// 	sucCallback = sucCallback || function(){};
	// 	failCallback = failCallback || function(){};
	//     cordova.exec(sucCallback, failCallback, "CPlugin", "openBrowser", [url]);
	// },
	
	/**
	 * [跳转Native原生页面]
	 * activity: 页面名称
	 * jsonData: json参数
	 * 备注：
	 *   LoginActivity：跳转到登陆页面
	 * 	 VideoActivity: 跳转到直播点播页面
	 *   MySocialActivity：跳转到点滴页面
	 *
	 * sucCallback: 成功回调，返回结果
	 * failCallback：失败回调
	 **/
	startActivity : function(activity, jsonData, sucCallback, failCallback) {
		activity = activity || "";
		jsonData = jsonData || "";
		sucCallback = sucCallback || function(){};
		failCallback = failCallback || function(){};
	    cordova.exec(sucCallback, failCallback, "CPlugin", "startActivity", [activity, jsonData]);
	},

	
	// /**
	//  * [头部显示或隐藏]
	//  * title: 页面标题，title有值为子页面，显示webActivity的头部；title没值为tab页面，显示tab的头部
	//  * visiable: 头部是否可见，true可见，false隐藏
	//  * model: 模块名称，区别不同模块设置不同的头部背景。[air--空气， rank--排行]
	//  * sucCallback: 成功回调，返回结果
	//  * failCallback：失败回调
	//  **/
	// showhead : function(title, visiable, model, sucCallback, failCallback) {
	// 	title = title || "";
	// 	visiable = visiable || false;
	// 	model = model || "";
	// 	sucCallback = sucCallback || function(){};
	// 	failCallback = failCallback || function(){};
	//     cordova.exec(sucCallback, failCallback, "CPlugin", "showhead", [title, visiable, model]);
	// },
	
	// /**
	//  * [设置标题]
	//  * title: 页面标题，title有值为子页面，显示webActivity的头部；title没值为tab页面，显示tab的头部
	//  * sucCallback: 成功回调，返回结果
	//  * failCallback：失败回调
	//  **/
	// setTitle:function(title, sucCallback, failCallback) {
	// 	title = title || "";
	// 	sucCallback = sucCallback || function(){};
	// 	failCallback = failCallback || function(){};
	//     cordova.exec(sucCallback, failCallback, "CPlugin", "setTitle", [title]);
	// },
	
	
	// /**
	//  * [分享]
	//  * title: 标题
	//  * content: 内容
	//  * url: 链接
	//  * sdcardImagePath: sd图片路径
	//  * sucCallback: 成功回调，返回结果
	//  * failCallback：失败回调
	//  **/
	// showShare : function(title, content, url, sdcardImagePath, sucCallback, failCallback) {
	// 	title = title || "";
	// 	content = content || "";
	// 	url = url || "";
	// 	sdcardImagePath = sdcardImagePath || "";
	// 	sucCallback = sucCallback || function(){};
	// 	failCallback = failCallback || function(){};
	//     cordova.exec(sucCallback, failCallback, "CPlugin", "showShare", [title, content, url, sdcardImagePath]);
	// },
	
	// /**
	//  * [弹出选择图片框]
	//  * sucCallback: 成功回调，返回结果。
	//  * failCallback：失败回调
	//  **/
	// showPhoto : function( sucCallback, failCallback) {
	// 	sucCallback = sucCallback || function(){};
	// 	failCallback = failCallback || function(){};
	//     cordova.exec(sucCallback, failCallback, "CPlugin", "showPhoto", []);
	// },
	
	// /**
	//  * [获取经纬度]
	//  * 返回结果：{"lat":"22.8007953997", "lon":"108.2906566832", "city":"南宁市"}
	//  * sucCallback: 成功回调，返回结果
	//  * failCallback：失败回调
	//  **/
	// getLocation : function(sucCallback, failCallback) {
	// 	sucCallback = sucCallback || function(){};
	// 	failCallback = failCallback || function(){};
	//     cordova.exec(sucCallback, failCallback, "CPlugin", "getLocation", []);
	// },
	
	
	// /**
	//  * [提示消息]
	//  * message: 消息
	//  * sucCallback: 成功回调
	//  * failCallback：失败回调
	//  **/
	// toast : function(message) {
	//     cordova.exec(function(){}, function(){}, "CPlugin", "toast", [message]);
	// },
	
	
	// /**
	//  * [弹出框]
	//  * message: 消息
	//  * sucCallback: 成功回调
	//  * failCallback：失败回调
	//  **/
	// alertDialog : function(message, sucCallback, failCallback) {
	// 	message = message || "";
	//  	sucCallback = sucCallback || function(){};
	// 	failCallback = failCallback || function(){};
	//     cordova.exec(sucCallback, failCallback, "CPlugin", "alertDialog", [message]);
	// },
	
	// /**
	//  * [隐藏加载框]
	//  * message: 消息
	//  * sucCallback: 成功回调
	//  * failCallback：失败回调
	//  **/
	// hideLoadDialog : function(sucCallback, failCallback) {
	//  	sucCallback = sucCallback || function(){};
	// 	failCallback = failCallback || function(){};
	//     cordova.exec(sucCallback, failCallback, "CPlugin", "hideLoadDialog", []);
	// },
	
	// /**
	//  * [弹出加载框]
	//  * message: 消息
	//  * sucCallback: 成功回调
	//  * failCallback：失败回调
	//  **/
	// showLoadDialog : function(title, sucCallback, failCallback) {
	// 	title = title || "";
	//  	sucCallback = sucCallback || function(){};
	// 	failCallback = failCallback || function(){};
	//     cordova.exec(sucCallback, failCallback, "CPlugin", "showLoadDialog", [title]);
	// },
	
	// /**
	//  * [退出系统]
	//  * sucCallback: 成功回调
	//  * failCallback：失败回调
	//  **/
	// exit : function(sucCallback, failCallback) {
	// 	sucCallback = sucCallback || function(){};
	// 	failCallback = failCallback || function(){};
	//     cordova.exec(sucCallback, failCallback, "CPlugin", "exit", []);
	// }
};

window.phoneGap = phone_gap;
