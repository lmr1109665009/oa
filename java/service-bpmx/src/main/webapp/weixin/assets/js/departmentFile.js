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
        getData('2','知识管理','1');
    });
    $('.departDoc').trigger('click');
    var depBranch = '文件柜';
    var searchUrl = _ctx + '/me/document/searchFile.ht';
    var url = _ctx + '/me/docType/newFrontList.ht';
    var host = window.location.protocol+'//' + window.location.host ;
    var url1 = host+ _ctx + '/me/document/download.ht';
    console.log("sessionId",userName);
    //如果为ios端，下载时进入浏览器

    getOutBrower = function(url2,fileName,fileSize){
        event.stopPropagation();
        console.log(url2,"app");
        cordova.exec((function (data) {}), (function (err) {
            console.log(err);
        }),'OpenUrlPlugin', 'sendUrl', ['', '', {"name":fileName, "size":fileSize, "url":url2}]);
    }
    //如果为ios端，下载时进入浏览器
    downloadWJ = function downloadWJ(href) {
        event.stopPropagation();
        console.log(url2,"ios");
        cordova.exec((function (data) {}), (function (err) {
            console.log(err);
        }),'OpenUrlPlugin', 'sendUrl', ['', '', {"name":fileName, "size":fileSize, "url":url2}]);
    }
     
    getClickData = function(a,b,c){
        getData(a,b,c);
    }
    
    // 时间戳转成 yy-mm-dd
    function fmtDate(obj){
        var date =  new Date(obj);
        var y = 1900+date.getYear();
        var m = "0"+(date.getMonth()+1);
        var d = "0"+date.getDate();
        return y+"-"+m.substring(m.length-2,m.length)+"-"+d.substring(d.length-2,d.length);
    }
    
  //识别文件格式进行分类
    function fileClassify(data){
    	var index = data.lastIndexOf(".");
    	var fileName;
    	if(index == -1){
    		return "file";
    	}else{
    		fileName =	data.substr(index+1,data.length-1).toLowerCase();
    		if((/^(doc|docx)$/).test(fileName)){
    			return "word";
    		}else if((/^(xls|xlsx)$/).test(fileName)){
    			return "excel";
    		}else if((/^(ppt|pptx)$/).test(fileName)){
    			return "ppt";
    		}else if((/^(jpg|jpeg|gif|png|bmp|psd)$/).test(fileName)){
    			return "jpg";
    		}else if((/^(rar|zip)$/).test(fileName)){
    			return "rar";
    		}else if((/^(pdf)$/).test(fileName)){
    			return "pdf";
    		}else if((/^(txt|log|wps)$/).test(fileName)){
    			return "txt";
    		}else if((/^(cd|ogg|mp3|asfwma|wav|mp3pro|rm|real|ape|module|midi|vqf|amr)$/).test(fileName)){
    			return "radio";
    		}else if((/^(avi|rmvb|rm|asf|divx|mpg|mpeg|mpe|wmv|mp4|mkv|vob|mov)$/).test(fileName)){
    			return "video";
    		}else{
    			return "file";
    		}
    	}
    	
    }
    
	//模板
	function template(data){
		var str="";
		for(var i = 0; i < data.length; i++){
			var upTime = fmtDate(data[i].upTime);
    		if (!data[i].path) {
                //如果是文件夹，则如下方式渲染页面
    			str += '<li onclick="getClickData(' + data[i].id + ',' + data[i].name + ',' + data[i].docTypeId + ')">'+
                '<div class="content"><span><img src="../assets/css/sea/folder.png"></span>'+
                '<div class="name"><div class="title">'+data[i].name+'</div><div class="dateTime">'+upTime+'</div>'+
                '</div></div>'+
            '</li>'
    	    }else{
            //文件在页面的渲染方式
            if(isiOS){
                str += '<li onclick="getClickData(' + data[i].id + ',' + data[i].name + ',' + data[i].docTypeId + ')">'+
                '<div class="content" onclick='+'event.stopPropagation();$(this).next().toggle();$(this).find("i").toggleClass('+'"rotate"'+')><span><img src="../assets/css/sea/'+ fileClassify(data[i].name) +'.png"></span>'+
                '<div class="name"><div class="title">'+data[i].name+'</div><div class="dateTime">'+upTime+'</div>'+
                '<i></i></div></div>'+
                '<div class="edit">'+
                    '<ul>'+
                        '<li onclick="downloadWJ('+ '"' + url1 + '?sessionId='+ userName +'&documentIPath=' + data[i].path +'&id=' + data[i].id + '&openIt=true'+'"'+')"><img src="../assets/css/sea/icon_download.png"><div>下载</div></li>'+
                    '</ul>'+
                '</div>'+
            '</li>'
            }
            else if(isAndroid){
//                str += '<div class="oneList clearfloat"><div class="oneList-left fl"><p class="docName">' + data[i].documentName + '</p><p class="createTime">' + data[i].uploadTime + '</p></div><div class="oneList-right fr"><p class="creator" onclick='+'event.stopPropagation();$(this).parent().next().css('+'"display"'+','+'"block");><img src="images/edit.png"></p></div><div class="operateFile"><div class="contain"><a target="_blank" class="downloadF" onclick=' + 'getOutBrower('+ '"' + url1 + '?sessionId='+ userName +'&documentIPath=' + data[i].documentIPath +'&id=' + data[i].id + '"'+')><img src="images/download1.png"><p>&nbsp;&nbsp;&nbsp;下 载</p></a></div><div class="contain"><div class="delete" onclick='+'deleteOne('+data[i].id+',1)><img src="images/delete.png"><p>&nbsp;&nbsp;&nbsp;删 除</p></div></div><div class="contain"><div class="rename" onclick='+'reNameOne('+reqId+','+'"'+data[i].documentName+'"'+','+data[i].id+',1)><img src="images/rename.png"><p>&nbsp;&nbsp;&nbsp;&nbsp;重命名</p></div></div><div class="close"><div class="cancle" onclick='+'event.stopPropagation();$(this).parent().parent().css('+'"display"'+','+'"none")><img src="images/close.png"></div></div></div></div></div>';
            	 str += '<li onclick="getClickData(' + data[i].id + ',' + data[i].name + ',' + data[i].docTypeId + ')">'+
                 '<div class="content"  onclick='+'event.stopPropagation();$(this).next().toggle();$(this).find("i").toggleClass('+'"rotate"'+')><span><img src="../assets/css/sea/'+ fileClassify(data[i].name) +'.png"></span>'+
                 '<div class="name"><div class="title">'+data[i].name+'</div><div class="dateTime">'+upTime+'</div>'+
                 '<i></i></div></div>'+
                 '<div class="edit">'+
                     '<ul>'+
                         '<li onclick="getOutBrower('+ '"' + url1 + '?sessionId='+ userName +'&documentIPath=' + data[i].path +'&id=' + data[i].id +'"'+')"><img src="../assets/css/sea/icon_download.png"><div>下载</div></li>'+
                     '</ul>'+
                 '</div>'+
             '</li>'
            }
            else{
                str += '<li onclick="getClickData(' + data[i].id + ',' + data[i].name + ',' + data[i].docTypeId + ')">'+
                '<div class="content"  onclick='+'event.stopPropagation();$(this).next().toggle();$(this).find("i").toggleClass('+'"rotate"'+')><span><img src="../assets/css/sea/'+ fileClassify(data[i].name) +'.png"></span>'+
                '<div class="name"><div class="title">'+data[i].name+'</div><div class="dateTime">'+upTime+'</div>'+
                '<i></i></div></div>'+
                '<div class="edit">'+
                    '<ul>'+
                        '<li><a class="downloadF" href="' + url1 + '?sessionId='+ userName +'&documentIPath=' + data[i].path +'&id=' + data[i].id +'"'+')><img src="../assets/css/sea/icon_download.png"><div>下载</div></a></li>'+
                    '</ul>'+
                '</div>'+
            '</li>'
            }
            }
        }
		return str;
	}
	
	
    $(document).ready(function(){
    	if(iPad){$("h2").addClass("list-title");}
		$('#lcListInput').click(function(e){
		$("header").hide();
		$(".searchList").show();
		$(".searchList #topSearchTxt").focus();
		$(".h60").hide();
		$(".searchList #topSearchTxt").on("input change",function (e) {
			//getSearchData($(".searchList #topSearchTxt").val())
			$("#fContent_mobile ul").empty();
			    // 页数
			    var page = 0;
			    // 每页展示5个
			    var size = 20;
			    var totalCount;
			    var key = $(".searchList #topSearchTxt").val();
			    if(key==''){return false;}
			    else{
			            // 拼接HTML
			            var html = '';
			            $.ajax({
			                type: 'POST',
			                url: searchUrl,
			                data:{"Q_name_SL":key,flag:"2"},
			                dataType: 'json',
			                success: function(data){
			                	console.log(data);
			                	if (undefined != data.results && data.results != null && data.results.length > 0) {
			        				totalCount=data.totalCounts;
			            			html = template(data.results);
			            			$("#fContent_mobile ul").html(html);
			                    // 如果没有数据
			                    }else{
				                		$("#fContent_mobile ul").html("<center style='color:#f60;line-height:40px;'>您的查询搜索没有内容！</center>");
			                    }
			                },
			                error: function(xhr, type){
			                	console.log('Ajax error!');
			                }
			            });
			        }
		});
		$('.qxBtn').click(function(e){
		$(".searchList").hide();
		$("header").show();
		$(".h60").show();
		$(".searchList #topSearchTxt").val("");
		getData('2','知识管理','1');
		});
		});
	})
	
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
            if(iPad){ $(".am-header").addClass("header-type");}
            $('.logoNav').css('display','block');
        }else if(PidName == 2 || PidName == 3){
           // 手机版的返回功能
            $('.tabframe').css('display','none');
            $('.am-with-fixed-header').css("padding",'60px 0 0 0 ');
            $('.gohistory').css("display",'block');
            if(iPad){ $(".am-header").removeClass("header-type");}
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
                // console.log(result);
                var data = result.data.varList;
                console.log(data);
                
                var str = '';
                if (data.length == 0) {
                     str += '<div class="content" style="text-align: center;line-height: 55px">该文件夹为空</div>';
                    $('#fContent_mobile ul').html(str);
                } else {
                	for(var i = 0; i < data.length; i++){
                		if (!data[i].documentIPath) {
                            //如果是文件夹，则如下方式渲染页面
                			str += '<li onclick=' + 'getClickData(' + data[i].id + ',\''+data[i].documentName+'\',' + data[i].parentId + ')>'+
                            '<div class="content"><span><img src="../assets/css/sea/folder.png"></span>'+
                            '<div class="name"><div class="title">'+data[i].documentName+'</div><div class="dateTime">'+data[i].uploadTime+'</div>'+
                            '</div></div>'+
                        '</li>'
                	    }else{
                        //文件在页面的渲染方式
                        if(isiOS){
                            str += '<li onclick="getClickData(' + data[i].id + ',\''+data[i].documentName+'\',' + data[i].parentId + ')">'+
                            '<div class="content" onclick='+'event.stopPropagation();$(this).next().toggle();$(this).find("i").toggleClass('+'"rotate"'+')><span><img src="../assets/css/sea/'+ fileClassify(data[i].documentName) +'.png"></span>'+
                            '<div class="name"><div class="title">'+data[i].documentName+'</div><div class="dateTime">'+data[i].uploadTime+'</div>'+
                            '<i></i></div></div>'+
                            '<div class="edit">'+
                                '<ul data-id="'+ data[i].id +'">'+
                                    '<li onclick=' + 'downloadWJ('+ '"' + url1 + '?sessionId='+ userName +'&documentIPath=' + data[i].documentIPath +'&id=' + data[i].id + '&openIt=true'+'"'+')><img src="../assets/css/sea/icon_download.png"><div>下载</div></li>'+
                            '</div>'+
                        '</li>'
                        }
                        else if(isAndroid){
//                            str += '<div class="oneList clearfloat"><div class="oneList-left fl"><p class="docName">' + data[i].documentName + '</p><p class="createTime">' + data[i].uploadTime + '</p></div><div class="oneList-right fr"><p class="creator" onclick='+'event.stopPropagation();$(this).parent().next().css('+'"display"'+','+'"block");><img src="images/edit.png"></p></div><div class="operateFile"><div class="contain"><a target="_blank" class="downloadF" onclick=' + 'getOutBrower('+ '"' + url1 + '?sessionId='+ userName +'&documentIPath=' + data[i].documentIPath +'&id=' + data[i].id + '"'+')><img src="images/download1.png"><p>&nbsp;&nbsp;&nbsp;下 载</p></a></div><div class="contain"><div class="delete" onclick='+'deleteOne('+data[i].id+',1)><img src="images/delete.png"><p>&nbsp;&nbsp;&nbsp;删 除</p></div></div><div class="contain"><div class="rename" onclick='+'reNameOne('+reqId+','+'"'+data[i].documentName+'"'+','+data[i].id+',1)><img src="images/rename.png"><p>&nbsp;&nbsp;&nbsp;&nbsp;重命名</p></div></div><div class="close"><div class="cancle" onclick='+'event.stopPropagation();$(this).parent().parent().css('+'"display"'+','+'"none")><img src="images/close.png"></div></div></div></div></div>';
                        	 str += '<li onclick=' + 'getClickData(' + data[i].id + ',\''+data[i].documentName+'\',' + data[i].parentId + ')>'+
                             '<div class="content"  onclick='+'event.stopPropagation();$(this).next().toggle();$(this).find("i").toggleClass('+'"rotate"'+')><span><img src="../assets/css/sea/'+ fileClassify(data[i].documentName) +'.png"></span>'+
                             '<div class="name"><div class="title">'+data[i].documentName+'</div><div class="dateTime">'+data[i].uploadTime+'</div>'+
                             '<i></i></div></div>'+
                             '<div class="edit">'+
                                 '<ul data-id='+ data[i].id +'>'+
                                     '<li onclick=' + 'getOutBrower('+ '"' + url1 + '?sessionId='+ userName +'&documentIPath=' + data[i].documentIPath +'&id=' + data[i].id +'"'+')><img src="../assets/css/sea/icon_download.png"><div>下载</div></li>'+
                                 '</ul>'+
                             '</div>'+
                         '</li>'
                        }
                        else{
                            str += '<li onclick=' + 'getClickData(' + data[i].id + ',' + '"' + data[i].documentName + '"' + ',' + data[i].parentId + ')>'+
                            '<div class="content" onclick='+'event.stopPropagation();$(this).next().toggle();$(this).find("i").toggleClass('+'"rotate"'+')><span><img src="../assets/css/sea/'+ fileClassify(data[i].documentName) +'.png"></span>'+
                            '<div class="name"><div class="title">'+data[i].documentName+'</div><div class="dateTime">'+data[i].uploadTime+'</div>'+
                            '<i></i></div></div>'+
                            '<div class="edit">'+
                                '<ul data-id='+ data[i].id +'>'+
                                    '<li><a class="downloadF" href="' + url1 + '?sessionId='+ userName +'&documentIPath=' + data[i].documentIPath +'&id=' + data[i].id +'"'+')><img src="../assets/css/sea/icon_download.png"><div>下载</div></a></li>'+
                                '</ul>'+
                            '</div>'+
                        '</li>'
                        }
                    }	
                } 
                	$('#fContent_mobile ul').html(str);     
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