/**
 * Created by xucuiyun on 2017/5/3.
 */
$(function(){
	var userName = sessionId;
	$(".tabframe_ul li a").each(function(index, item){
		$(this).attr('href',item.title+'?sessionId='+sessionId);
		});
    var u = navigator.userAgent;
    var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端   
    var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1; //android终端
    if (isAndroid) {
    	  $LAB
    	    .script("js/cordova/android/cordova.js");
    	} else if (isiOS) {
    	  $LAB
    	    .script("js/cordova/ios/cordova.js");
    	}
    
    $('.departDoc').on('click',function(){
        getData('2','文件柜','1');
    });
    $('.departDoc').trigger('click');
    var depBranch = '文件柜';
    console.log("sessionId",userName);
    //如果为ios端，下载时进入浏览器
    
     getOutBrower =function(url2){
    	 console.log(url2);
    	 cordova.exec((function (data) {}), (function (err) {
    	    console.log(err);
    	  }),'OpenUrlPlugin', 'sendUrl', ['', '', url2]);
    }
     downloadWJ = function downloadWJ(href) {
     	var obj = {}; 
     	obj.download= href;
     	//console.log(href)
     	window.parent.postMessage(obj,'*');
     }
     
    getClickData = function(a,b,c){
        getData(a,b,c);
    }
    //显示的页面函数,获取数据渲染页面
    function getData(idName,docName,PidName){
        $('h2').text(docName);
        if($(".operate")){
            $(".operate").remove();
        }
        var url = _ctx + '/me/docType/newFrontList.ht';
        var host = window.location.protocol+'//' + window.location.host ;
        var url1 = host+ _ctx + '/me/document/download.ht';
        //面包屑导航的显示
        if(PidName == 1){
            $('.breadList').html('<li>全部文件</li>');
            if(idName == 2){
                depBranch = '文件柜';
            }else if(idName == 3){
                depBranch = '公共文件柜';
            }
            $('.tabframe').css('display','block');
            $('.am-with-fixed-header').css("padding",'107px 0 0 0');
            $('.gohistory').css("display",'none');
            $('.logoNav').css('display','block');
        }else if(PidName == 2 || PidName == 3){
           // 手机版的返回功能
            $('.tabframe').css('display','none');
            $('.am-with-fixed-header').css("padding",'60px 0 0 0 ');
            $('.gohistory').css("display",'block');
            $('.logoNav').css('display','none');
                        $('.gohistory').get(0).onclick = function(){
                            $('.gobackOne').trigger('click');
                        }
            $('.breadList').html('<li class="gobackOne"> 返回上一级 | </li><li class="gobackAll">'+depBranch+'</li><li onclick='+'getClickData('+idName+','+'"'+docName+'"'+','+PidName+')> /'+docName+'</li>');
            $('ul.breadList li:last-child').siblings().css('color', 'blue');
            //回归到全部文件，没有导航的时候
            $('.gobackAll').on("click",function(){
                if($('.gobackAll').text() == '文件柜'){
                    $('.departDoc').trigger('click');
                }else{
                    $('.publicDoc').trigger('click');
                }
                // $('ul.breadList').empty();
                $('ul.breadList').html('<li>全部文件</li>');
            });

            //返回上一级功能
            $('.gobackOne').get(0).onclick = function(){
                $('ul.breadList li:last-child').prev().trigger("click");
            }

        }else{
            //每一级面包屑导航功能
            var li = document.createElement("li");
            console.log(docName);
            $(li).text("/" + docName);
            $(li).on("click",function() {
                $(this).nextAll("li").remove();
                $(this).remove();
                getClickData(idName,docName,PidName);
            })
            $('ul.breadList').append(li);
            $('ul.breadList li:last-child').siblings().css('color', 'blue');
        }
        //请求获取该文件夹下面的所有文件
        $.ajax({
            type:'post',
            url: url+'?id='+idName,
            
            success:function(result) {
                console.log(result);
                var data = result.data.varList;
                console.log(data);
                var str = '';
                if (data.length == 0) {
                     str += '<div class="oneList" style="text-align: center;line-height: 55px">该文件夹为空</div>';
                    $('#fContent_mobile').html(str);
                } else {
                    if (!data[0].documentIPath) {
                        //如果是文件夹，则如下方式渲染页面
                        for (var i = 0; i < data.length; i++) {
                            str += '<div class="oneList clearfloat" onclick=' + 'getClickData(' + data[i].id + ',' + '"' + data[i].documentName + '"' + ',' + data[i].parentId + ')><div class="oneList-left fl"><p class="docName">' + data[i].documentName + '</p><p class="createTime">' + data[i].uploadTime + '</p></div><div class="oneList-right fr"><p class="creator">' + data[i].creatorName + '</p></div></div>'
                            $('#fContent_mobile').html(str);
                        }
                    } else {
                        //文件在页面的渲染方式
                        if(isiOS){
                            for (var i = 0; i < data.length; i++) {
                                str += '<div class="oneList clearfloat"><div class="oneList-left fl"><p class="docName">' + data[i].documentName + '</p>'
                                +'<p class="createTime">' + data[i].uploadTime + '</p></div><div class="oneList-right fr"><p class="creator">' + data[i].creatorName + '<a target="_blank" class="operate" '
                                +'onclick=' + 'downloadWJ('+ '"' + url1 + '?sessionId='+ userName +'&documentIPath=' + data[i].documentIPath +'&id=' + data[i].id + '&openIt=true'+'"'+')><img src="images/download.png"></a></p></div></div>'
                            }
                            $('#fContent_mobile').html(str);
                            
                           }
                            else if(isAndroid){
                                for (var i = 0; i < data.length; i++) {
                                    str += '<div class="oneList clearfloat"><div class="oneList-left fl"><p class="docName">' + data[i].documentName + '</p>'
                                    +'<p class="createTime">' + data[i].uploadTime + '</p></div><div class="oneList-right fr"><p class="creator">' + data[i].creatorName + '<a target="_blank" class="operate" '
                                    +'onclick=' + 'getOutBrower('+ '"' + url1 + '?sessionId='+ userName +'&documentIPath=' + data[i].documentIPath +'&id=' + data[i].id + '"'+')><img src="images/download.png"></a></p></div></div>'
                                }
                                $('#fContent_mobile').html(str);
                            }
                        else{
                        for (var i = 0; i < data.length; i++) {
                            str += '<div class="oneList clearfloat"><div class="oneList-left fl"><p class="docName">' + data[i].documentName + '</p><p class="createTime">' + data[i].uploadTime + '</p></div><div class="oneList-right fr"><p class="creator">' + data[i].creatorName + '<a target="_blank" class="operate" href="' + url1 + '?sessionId='+ userName +'&documentIPath=' + data[i].documentIPath +'&id=' + data[i].id + '"><img src="images/download.png"></a></p></div></div>'
                        }
                        $('#fContent_mobile').html(str);
                       }
                    }
                }
            }
        });
    }
})

/**
 * burger
 */
$(function() {
	var obj = {}; 
	obj.firstPage = false;
	$(this).removeClass("menu-opened");
	window.parent.postMessage(obj,'*');
	$('.burger').on("click", function(){ 
	    obj.firstPage = true; 
	    window.parent.postMessage(obj,'*');
	    if($(this).hasClass("menu-opened")){$(this).removeClass("menu-opened");}
	    else{ $(this).addClass("menu-opened");}
	    //resultUserInfo()
	    //console.log("resultUserInfo():",resultUserInfo());
	    });
})