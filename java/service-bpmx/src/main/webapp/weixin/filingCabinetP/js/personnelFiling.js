/**
 * Created by xucuiyun on 2017/5/3.
 */
$(function(){
     var userName = request.QueryString("sessionId") == null ? '' : request.QueryString("sessionId");
	 
     $('.departDoc').on('click',function(){
         getData('4','个人文件柜','1');
     });

     $('.departDoc').trigger('click');

    var depBranch = '个人文件柜';

    var fileName = '';

    var reqId = 4;
    
    var isNew = false;
    
    getClickData = function(a,b,c){
        getData(a,b,c);
    }
    
    //上传功能
    function uploadFile(url2){
        $('.newFile').remove();
        $('body').append('<input type="file" name="myfile" class="newFile">');
        //上传功能
        $('.newFile').on('change',function(){
            if($('.progress').css('display') == 'block'){
                alert('已有文件上传！请稍后。。。');
                return;
            }
            var proSpeed = 0;
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
                			getData('4','个人文件柜','1');
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
                    			getData('4','个人文件柜','1');
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
   
    var FoldsaveUrl = _ctx + '/me/docType/save.ht?sessionId=' + userName;
    var FilesaveUrl = _ctx + '/me/document/save.ht?sessionId=' + userName;
    var deleteUrl = _ctx + '/me/docType/frontDel.ht';
    
    //重命名函数
    reNameOne = function(pid,index,docName,id,type){
    	event.stopPropagation();
    	$('tbody tr').eq(index).html("<tr><td class='checkOne'><input type='checkbox'></td><td class='docName'><input type='text' class='newName'><img class='sure' src='img/save.png'><img class='cancle' src='img/no.png'></td></tr>");
    	var lastIndex = docName.lastIndexOf('.');
    	$('.newName').focus().val(docName);
    	if(lastIndex != -1){
    		$('.newName').get(0).setSelectionRange(0,lastIndex);
    	}else{
    		$('.newName').get(0).setSelectionRange(0,5);
    	}
    	
    	$('.newName').on('click',function(e){
    		e.stopPropagation();
    	})
    	$('.sure').on('click',function(e){
    		e.stopPropagation();
    		var newDocName = $('.newName').val();
    		if(type == 0){
    			$.ajax({
        			type:'post',
        		    url:FoldsaveUrl,
        		    data:{id:id,parentId:pid,isFront:1,typeName:newDocName},
        		    success:function(result){
        		    	if(result.status == 1){
        		    		alert('恭喜重命名成功！！');
        		    		if($('ul.breadList li:last-child').text() == '全部文件'){
        		    			getData('4','个人文件柜','1');
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
        		    data:{id:id,isFront:1,name:newDocName,isPrivate:1},
        		    success:function(result){
        		    	if(result.status == 1){
        		    		alert('恭喜重命名成功！！');
        		    		if($('ul.breadList li:last-child').text() == '全部文件'){
        		    			getData('4','个人文件柜','1');
        		    		}else{
        		    			$('ul.breadList li:last-child').trigger('click');
        		    		}
        		    	}
        		    }
        		});
    		}
    		
    	})
    	$('.cancle').on('click',function(e){
    		e.stopPropagation();
    		if($('ul.breadList li:last-child').text() == '全部文件'){
    			getData('4','个人文件柜','1');
    		}else{
    			$('ul.breadList li:last-child').trigger('click');
    		}
    	})
      }//重命名函数结束
    
    //删除一个函数开始
    deleteOne = function(id,type){
    	event.stopPropagation();
    	var dataPar = [{id:id,type:type}];
    	var ans = confirm('确定要删除所选文件吗？');
    	if(!ans){
    		return;
    	}
    	$.ajax({
			type:'post',
		    url:deleteUrl,
		    data:{ ds:JSON.stringify(dataPar)},
		    success:function(result){
		    	if(result.status == 1){
		    		alert('恭喜删除成功！！');
		    		if($('ul.breadList li:last-child').text() == '全部文件'){
		    			getData('4','个人文件柜','1');
		    		}else{
		    			$('ul.breadList li:last-child').trigger('click');
		    		}
		    	}else{
		    		alert('删除文件失败！');
		    	}
		    }
		});
    }

     
    //显示的页面函数，获取数据渲染页面
    function getData(idName,docName,PidName){
    	//全选框不选中
    	$('.checkedAll').get(0).checked = false;
    	isNew = false;
        reqId = idName;
        var url = _ctx + '/me/docType/newFrontList.ht';
        var url1 = _ctx + '/me/document/download.ht';
        var url2 = _ctx + '/me/document/frontSave.ht?sessionId='+ userName;
        
        //定义三个数组，一个是存储id,一个存储type,一个存储删除的数据
        var idArray = [];
        var typeArray = [];
        var deleteArray = [];
        
        //面包屑导航的显示开始
        if(PidName == 1){
            $('.breadList').html('<li class="upload"><button class="uploadFile">上传文件</button></li><li class="newOne"><button class="newFileFold">新增文件夹</button></li><li class="deleteMany"><button class="deleteFile">删除</button></li><li>全部文件</li>');    
        }else if(PidName == 4){
            $('.breadList').html('<li class="upload"><button class="uploadFile">上传文件</button></li><li class="newOne"><button class="newFileFold">新增文件夹</button></li><li class="deleteMany"><button class="deleteFile">删除</button></li><li class="gobackOne"> 返回上一级 | </li><li class="gobackAll">'+depBranch+'</li><li onclick='+'getClickData('+idName+','+'"'+docName+'"'+','+PidName+')> /'+docName+'</li>');
            $('ul.breadList li:last-child').siblings().css('color', '#37A7F7');
            //回归到全部文件，没有导航的时候
                   $('.gobackAll').on("click",function(){
                               $('.departDoc').trigger('click');
                        });

            //返回上一级功能
                        $('.gobackOne').get(0).onclick = function(){
                            $('ul.breadList li:last-child').prev().trigger("click");
                        }               
                        $('.newFile').remove();
                        $('body').append('<input type="file" name="myfile" class="newFile">');
                        //显示上传按钮，触发上传事件
                        $('.uploadFile').get(0).onclick = function(){
                            $('.newFile').trigger('click');
                        }
                    
        }else{
            var li = document.createElement("li");
            $(li).text("/" + docName);
            $(li).get(0).onclick = function() {
                $(this).nextAll("li").remove();
                $(this).remove();
                getClickData(idName,docName,PidName);
            }
            $('ul.breadList').append(li);
            $('ul.breadList li:last-child').siblings().css('color', '#37A7F7');
        }//面包屑导航结束
        
        
        //新增文件夹开始
          $('.newOne').get(0).onclick = function(){
        	 if(!isNew){
        		$tr = $('<tr></tr>');
            	 $tr.html("<td class='checkOne'><input type='checkbox'></td><td class='docName'><input type='text' class='newName'><img class='sure' src='img/save.png'><img class='cancle' src='img/no.png'></td>");
            	 $('#fContent_tbody').prepend($tr);
            	 isNew = true;
            	 $('.newName').focus().val('新建文件夹');
            	 $('.newName').get(0).setSelectionRange(0,5);
            	 //确定新增
            	 $('.sure').on('click',function(){
            		var newDocName = $('.newName').val();
          			$.ajax({
              			type:'post',
              		    url:FoldsaveUrl,
              		    data:{parentId:reqId,isFront:1,typeName:newDocName},
              		    success:function(result){
              		    	console.log(result);
              		    	console.log(result.status);
              		    	if(result.status == 1){
              		    		alert('新增文件夹成功！！');
              		    		if($('ul.breadList li:last-child').text() == '全部文件'){
              		    			getData('4','个人文件柜','1');
              		    		}else{
              		    			$('ul.breadList li:last-child').trigger('click');
              		    		}
              		    	}
              		    }
              		});
            	 })
            	 //取消
            	 $('.cancle').on('click',function(e){
          		e.stopPropagation();
          		if($('ul.breadList li:last-child').text() == '全部文件'){
          			getData('4','个人文件柜','1');
          		}else{
          			$('ul.breadList li:last-child').trigger('click');
          		}
          	})
            	 
        	 }else{
        		 return;
        	 }
        	 
         }//新增文件夹结束
        
        //列表数据请求开始
            $.ajax({
                type:'post',
                url: url+'?id='+idName+'&isPrivate=true',
                success:function(result) {
                    var data = result.data.varList;
                    var str = ''; 
                    //如果没有数据的处理方式
                    if (data.length == 0) {
                    	//文件上传功能开始
                    	uploadFile(url2);
                    	
                        $('.uploadFile').get(0).onclick = function(){
                            $('.newFile').trigger('click');
                        } //文件上传功能结束
                       
                        //没有数据的渲染方式
                        str += "<tr><td>此文件夹为空</td></tr>"
                        $('#fContent_tbody').html(str);
                        $('td').css("width",'100%');
                        return;
                    } //没有数据的渲染结束
                    
                    else {
                    	//如果有数据，则渲染列表
                         for(var i = 0; i < data.length; i++){
                        	 idArray.push(parseInt(data[i].id));
                        	 if(!data[i].documentIPath){
                        		 typeArray[i] = 0;
                        		 str += "<tr onclick=" + "getClickData(" + data[i].id + ',' + "'" + data[i].documentName + "'" + ',' + data[i].parentId + ")" + "><td class='checkOne'><input type='checkbox'  class='checkedOne'></td><td class='docName'><img src='img/file.png'>" + data[i].documentName + "</td><td> - </td><td>" + data[i].uploadTime + "</td><td>" + data[i].creatorName + "</td><td><a class='deleteOne' onclick="+"deleteOne("+data[i].id+",0)>删除</a><a class='reName' onclick="+"reNameOne("+reqId+","+i+","+"'"+data[i].documentName+"'"+","+data[i].id+",0)>重命名</a></td></tr>";
                        	 }else{
                        		 typeArray[i] = 1;
                        		 str += '<tr><td class="checkOne"><input type="checkbox" class="checkedOne"></td><td class="docName" title="'+data[i].documentName+'">' + data[i].documentName + '</td><td>' + data[i].docuemntSize + '</td><td>' + data[i].uploadTime + '</td><td>' + data[i].creatorName + '</td><td><a href="' + url1 + '?sessionId='+ userName + '&documentIPath=' + data[i].documentIPath +'&id=' + data[i].id + '">下载</a><a class="deleteOne" onclick='+'deleteOne('+data[i].id+',1)>删除</a><a class="reName" onclick='+'reNameOne('+reqId+','+i+','+'"'+data[i].documentName+'"'+','+data[i].id+',1)>重命名</a></td></tr>';
                        	 }
                         }
                         $('#fContent_tbody').html(str);
                         //文件上传功能开始
                         uploadFile(url2);
                         //显示上传按钮，触发上传事件
                         $('.uploadFile').get(0).onclick = function(){
                             $('.newFile').trigger('click');
                         }//文件上传功能结束
     
	                    //点击单选框
                        $('.checkedOne').on('click',function(e){
                        	e.stopPropagation();
                        	var $check = $('.checkedOne');
                        	var len = $check.length;
                        	var checkIndex = 0;
                        	//遍历所有的CheckBox,计算选中的checkbox的数量
                        	for(var i = 0; i < len; i++){
                        		if($check.get(i).checked){
                        			checkIndex++;
                        		}
                        	}
                        	//对选中的个数进行处理
                        	if(checkIndex > 0){
                        		$('.deleteFile').css('display','inline-block');
                        	}else{
                        		$('.deleteFile').css('display','none');
                        	}
                        	if(checkIndex == len){
                        		$('.checkedAll').prop('checked',true);
                        	}else{
                        		$('.checkedAll').prop('checked',false);
                        	} 	
                       })//对所有单选框处理结束
                        
                        //点击全选框
                        $('.checkedAll').get(0).onclick = function(){
                        	$('.checkedOne').prop('checked',$('.checkedAll').prop('checked')); 
                        	if($(this).prop('checked')){
                        		$('.deleteFile').css('display','inline-block');
                        	}else{
                        		$('.deleteFile').css('display','none');
                        	}
                        } //对全选框的处理结束 
                         
                        //删除多个文件或文件夹的处理
                        $('.deleteFile').get(0).onclick = function(){
                        	var ans = confirm('确定要删除所选文件吗？');
                        	if(!ans){
                        		$('.checkedOne').prop('checked',false);
                        		$('.checkedAll').prop('checked',false);
                        		return;
                        	}
                        	var $check = $('.checkedOne');
                        	var len = $check.length;
                        	for(var i = 0; i < len; i++){
                        		if($check.get(i).checked){
                        			deleteArray.push({id:idArray[i],type:typeArray[i]});
                        		}
                        	}
                        $.ajax({
                    			type:'post',
                    		    url:deleteUrl,
                    		    data:{ ds:JSON.stringify(deleteArray)},
                    		    success:function(result){
                    		    	if(result.status == 1){
                    		    		alert('恭喜删除成功！！');
                    		    		if($('ul.breadList li:last-child').text() == '全部文件'){
                    		    			getData('4','个人文件柜','1');
                    		    		}else{
                    		    			$('ul.breadList li:last-child').trigger('click');
                    		    		}
                    		    	}else{
                    		    		alert('删除文件失败！');
                    		    		$('.checkedOne').prop('checked',false);
                                		$('.checkedAll').prop('checked',false);
                    		    	}
                    		    }
                    		});
                        }
                        
                    }//else渲染列表结束    
                          
                }//success结束
            });
    }
})