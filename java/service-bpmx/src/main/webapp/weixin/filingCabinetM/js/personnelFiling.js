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
        getData('4','文件柜','1');
    });
    $('.departDoc').trigger('click');
    var depBranch = '文件柜';
    var reqId = '4';
    var FoldsaveUrl = _ctx + '/me/docType/save.ht?sessionId=' + userName;
    var FilesaveUrl = _ctx + '/me/document/save.ht?sessionId=' + userName;
    var deleteUrl = _ctx + '/me/docType/frontDel.ht';
    
    console.log("sessionId",sessionId);
    //如果为ios端，下载时进入浏览器
    getOutBrower = function(url2){console.log(url2);
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
    

    //上传功能
    function uploadFile(){
        $('.newFile').remove();
        $('body').append('<input type="file" name="myfile" class="newFile">');
        $('.newFile').trigger('click');
        //上传功能
        $('.newFile').on('change',function(){
            if($('.progress').css('display') == 'block'){
                alert('已有文件上传！请稍后。。。');
                return;
            }
            var proSpeed = 0;
            console.log(reqId);
            var flag = false;
            var isComplete = false;
            var file = $(this).val();
            fileName = getFileName(file);
            function getFileName(o){
                var pos=o.lastIndexOf("/");
                if(-1==pos){
                    pos=o.lastIndexOf("\\");
                }
                return o.substring(pos+1);
            }
            $('.fileName').text(fileName);
            var data = new FormData();
            var file = $('.newFile')[0].files[0];
            var size = $('.newFile')[0].files[0].size;
            if(size/1024/1024 > 100){
                alert('文件太大，不能上传');
                return;
            }
            $('.progress').css('display','block');
            var timer = setInterval(function(){
                proSpeed += 1;
                $('.comProgress').css('width',proSpeed+'%');
                $('.speed').text(proSpeed+'%');
                if(flag){
                    clearInterval(timer);
                    $('.comProgress').css('width','100%');
                    $('.speed').text('100%');
                    setTimeout(function(){
                        alert('文件上传成功！！');
                        $('.progress').css('display','none');
                        if($('ul.breadList li:last-child').text() == '全部文件'){
                			getData('4','文件柜','1');
                		}else{
                			$('ul.breadList li:last-child').trigger('click');
                		}
                        $('.newFile').remove();
                        $('body').append('<input type="file" name="myfile" class="newFile" style="display:none">');
                    },1000);
                }else {
                    if(proSpeed > 98){
                        clearInterval(timer);
                        isComplete = true;
                    }
                }

            },80);

            data.append('myfile', file);
            data.append('name', fileName);
            data.append('docTypeId', reqId);
            var xhr=new XMLHttpRequest();
            $.ajax({
                url:url2,
                type:'POST',
                data:data,
                cache: false,
                contentType: false,
                processData: false,
                success:function(data){
                    console.log('完成');
                    flag = true;
                    if(isComplete){
                    	$('.comProgress').css('width','100%');
                        $('.speed').text('100%');
                        setTimeout(function(){
                            alert('文件上传成功！！');
                            $('.progress').css('display','none');
                            if($('ul.breadList li:last-child').text() == '全部文件'){
                    			getData('4','文件柜','1');
                    		}else{
                    			$('ul.breadList li:last-child').trigger('click');
                    		}
                            $('.newFile').remove();
                            $('body').append('<input type="file" name="myfile" class="newFile" style="display:none">');
                        },1000);
                    }
                },
                error:function(){
                    alert('上传出错');
                    $('.progress').css('display','none');
                    $('ul.breadList li:last-child').trigger('click');
                    $('.newFile').get(0).outerHTML=$('.newFile').get(0).outerHTML;
                },

            });

        });
    }
  //上传功能
    $('.uploadImg').on('click',function(){
    	uploadFile();
    })
    
    
    //关闭删除框
    function closeDel(){
    	$('#deleteTran').css('display','none');
    	$('#mask').css('display','none');
    }
    //打开删除框
    function openDel(){
    	$('#deleteTran').css('display','block');
    	$('#mask').css('display','block');
    }
    //关闭重命名框
    function closeRe(){
    	$('#renameTran').css('display','none');
    	$('#mask').css('display','none');
    }
    //打开重命名框
    function openRe(){
    	$('#renameTran').css('display','block');
    	$('#mask').css('display','block');
    }
    
    //重命名函数
    reNameOne = function(pid,docName,id,type){
    	event.stopPropagation();
    	openRe();
    	var lastIndex = docName.lastIndexOf('.');
    	$('.newName').focus().val(docName);
    	if(lastIndex != -1){
    		$('.newName').get(0).setSelectionRange(0,lastIndex);
    	}else{
    		$('.newName').get(0).setSelectionRange(0,5);
    	}
    	$('.clearName').on('click',function(){
    		$('.newName').val('');
    	})
    	$('.sure').on('click',function(e){
    		var newDocName = $('.newName').val();
    		if(type == 0){
    			$.ajax({
        			type:'post',
        		    url:FoldsaveUrl,
        		    data:{id:id,parentId:pid,isFront:1,typeName:newDocName},
        		    success:function(result){
        		    	if(result.status == 1){
        		    		closeRe();
        		    		if($('ul.breadList li:last-child').text() == '全部文件'){
        		    			getData('4','文件柜','1');
        		    		}else{
        		    			$('ul.breadList li:last-child').trigger('click');
        		    		}
        		    	}
        		    }
        		});
    		}else{
    			$.ajax({
        			type:'post',
        		    url:FilesaveUrl,
        		    data:{id:id,isFront:1,name:newDocName},
        		    success:function(result){
        		    	if(result.status == 1){
        		    		closeRe();
        		    		if($('ul.breadList li:last-child').text() == '全部文件'){
        		    			getData('4','文件柜','1');
        		    		}else{
        		    			$('ul.breadList li:last-child').trigger('click');
        		    		}
        		    	}
        		    }
        		});
    		}
    		
    	})
    	$('.cancle').on('click',function(e){
    		closeRe();
    		if($('ul.breadList li:last-child').text() == '全部文件'){
    			getData('4','文件柜','1');
    		}else{
    			$('ul.breadList li:last-child').trigger('click');
    		}
    	})
      }//重命名函数结束
    
    //删除一个函数开始
    deleteOne = function(id,type){
    	event.stopPropagation();
    	openDel();
    	var dataPar = [{id:id,type:type}];
    	$('.sure').on('click',function(){
    		$.ajax({
    			type:'post',
    		    url:deleteUrl,
    		    data:{ ds:JSON.stringify(dataPar)},
    		    success:function(result){
    		    	if(result.status == 1){
    		    		closeDel();
    		    		if($('ul.breadList li:last-child').text() == '全部文件'){
    		    			getData('4','文件柜','1');
    		    		}else{
    		    			$('ul.breadList li:last-child').trigger('click');
    		    		}
    		    	}else{
    		    		alert('删除文件失败！');
    		    	}
    		    }
    		});
    	})
    	
    	$('.cancle').on('click',function(){
    		$('.cancle').on('click',function(e){
        		closeDel();
        		if($('ul.breadList li:last-child').text() == '全部文件'){
        			getData('4','文件柜','1');
        		}else{
        			$('ul.breadList li:last-child').trigger('click');
        		}
        	})
    	})
    	
    }
    
    
    //显示的页面函数,获取数据渲染页面
    function getData(idName,docName,PidName){
        $('h2').text(docName);
        reqId = idName;
        url = _ctx + '/me/docType/newFrontList.ht';
        var host = window.location.protocol+'//' + window.location.host ;
        url1 = host+ _ctx  + '/me/document/download.ht';
        url2 = _ctx + '/me/document/frontSave.ht?sessionId='+ userName;
        //面包屑导航的显示
        if(PidName == 1){
            $('.breadList').html('<li>全部文件</li>');
            if(idName == 2){
                depBranch = '部门文件柜';
            }else if(idName == 3){
                depBranch = '公共文件柜';
            }
            $('.tabframe').css('display','block');
            $('.am-with-fixed-header').css("padding",'107px 0 0 0');
            
            $('.gohistory').css("display",'none');
            $('.logoNav').css('display','block');
        }else if(PidName == 4){
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
            url: url+'?id='+idName+'&isPrivate=true',
            success:function(result) {
                console.log(result);
                var data = result.data.varList;
                console.log(data);
                var str = '';
                if (data.length == 0) {
                     str += '<div class="oneList" style="text-align: center;line-height: 55px">该文件夹为空</div>';
                    $('#fContent_mobile').html(str);
                } else {
                	for(var i = 0; i < data.length; i++){
                		if (!data[i].documentIPath) {
                            //如果是文件夹，则如下方式渲染页面
                            str += '<div class="oneList clearfloat" onclick=' + 'getClickData(' + data[i].id + ',' + '"' + data[i].documentName + '"' + ',' + data[i].parentId + ')><div class="oneList-left fl"><p class="docName">' + data[i].documentName + '</p><p class="createTime">' + data[i].uploadTime + '</p></div><div class="oneList-right fr"><p class="creator" onclick='+'event.stopPropagation();$(this).parent().next().css('+'"display"'+','+'"block");><img src="images/edit.png"></p></div><div class="operateCab"><div class="contain"><div class="delete" onclick='+'deleteOne('+data[i].id+',0)><img src="images/delete.png"><p>&nbsp;&nbsp;&nbsp;删 除</p></div></div><div class="contain"><div class="rename" onclick='+'reNameOne('+reqId+','+'"'+data[i].documentName+'"'+','+data[i].id+',0)><img src="images/rename.png"><p>&nbsp;&nbsp;&nbsp;&nbsp;重命名</p></div></div><div class="close"><div class="cancle" onclick='+'event.stopPropagation();$(this).parent().parent().css('+'"display"'+','+'"none")><img src="images/close.png"></div></div></div></div>';
                	    }else{
                        //文件在页面的渲染方式
                        if(isiOS){
                            str += '<div class="oneList clearfloat"><div class="oneList-left fl"><p class="docName">' + data[i].documentName + '</p><p class="createTime">' + data[i].uploadTime + '</p></div><div class="oneList-right fr"><p class="creator" onclick='+'event.stopPropagation();$(this).parent().next().css('+'"display"'+','+'"block");><img src="images/edit.png"></p></div><div class="operateFile"><div class="contain"><a target="_blank" class="downloadF" onclick=' + 'downloadWJ('+ '"' + url1 + '?sessionId='+ userName +'&documentIPath=' + data[i].documentIPath +'&id=' + data[i].id + '&openIt=true'+'"'+')><img src="images/download1.png"><p>&nbsp;&nbsp;&nbsp;下 载</p></a></div><div class="contain"><div class="delete" onclick='+'deleteOne('+data[i].id+',1)><img src="images/delete.png"><p>&nbsp;&nbsp;&nbsp;删 除</p></div></div><div class="contain"><div class="rename" onclick='+'reNameOne('+reqId+','+'"'+data[i].documentName+'"'+','+data[i].id+',1)><img src="images/rename.png"><p>&nbsp;&nbsp;&nbsp;&nbsp;重命名</p></div></div><div class="close"><div class="cancle" onclick='+'event.stopPropagation();$(this).parent().parent().css('+'"display"'+','+'"none")><img src="images/close.png"></div></div></div></div></div>';
                        }
                        else if(isAndroid){
                            str += '<div class="oneList clearfloat"><div class="oneList-left fl"><p class="docName">' + data[i].documentName + '</p><p class="createTime">' + data[i].uploadTime + '</p></div><div class="oneList-right fr"><p class="creator" onclick='+'event.stopPropagation();$(this).parent().next().css('+'"display"'+','+'"block");><img src="images/edit.png"></p></div><div class="operateFile"><div class="contain"><a target="_blank" class="downloadF" onclick=' + 'getOutBrower('+ '"' + url1 + '?sessionId='+ userName +'&documentIPath=' + data[i].documentIPath +'&id=' + data[i].id + '"'+')><img src="images/download1.png"><p>&nbsp;&nbsp;&nbsp;下 载</p></a></div><div class="contain"><div class="delete" onclick='+'deleteOne('+data[i].id+',1)><img src="images/delete.png"><p>&nbsp;&nbsp;&nbsp;删 除</p></div></div><div class="contain"><div class="rename" onclick='+'reNameOne('+reqId+','+'"'+data[i].documentName+'"'+','+data[i].id+',1)><img src="images/rename.png"><p>&nbsp;&nbsp;&nbsp;&nbsp;重命名</p></div></div><div class="close"><div class="cancle" onclick='+'event.stopPropagation();$(this).parent().parent().css('+'"display"'+','+'"none")><img src="images/close.png"></div></div></div></div></div>';
                        }
                        else{
                           str += '<div class="oneList clearfloat"><div class="oneList-left fl"><p class="docName">' + data[i].documentName + '</p><p class="createTime">' + data[i].uploadTime + '</p></div><div class="oneList-right fr"><p class="creator" onclick='+'event.stopPropagation();$(this).parent().next().css('+'"display"'+','+'"block");><img src="images/edit.png"></p></div><div class="operateFile"><div class="contain"><a class="downloadF" href="' + url1 + '?sessionId='+ userName +'&documentIPath=' + data[i].documentIPath +'&id=' + data[i].id + '"><img src="images/download1.png"><p>&nbsp;&nbsp;&nbsp;下 载</p></a></div><div class="contain"><div class="delete" onclick='+'deleteOne('+data[i].id+',1)><img src="images/delete.png"><p>&nbsp;&nbsp;&nbsp;删 除</p></div></div><div class="contain"><div class="rename" onclick='+'reNameOne('+reqId+','+'"'+data[i].documentName+'"'+','+data[i].id+',1)><img src="images/rename.png"><p>&nbsp;&nbsp;&nbsp;&nbsp;重命名</p></div></div><div class="close"><div class="cancle" onclick='+'event.stopPropagation();$(this).parent().parent().css('+'"display"'+','+'"none")><img src="images/close.png"></div></div></div></div>'
                                                 }
                    }	
                } 
                	$('#fContent_mobile').html(str);     
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