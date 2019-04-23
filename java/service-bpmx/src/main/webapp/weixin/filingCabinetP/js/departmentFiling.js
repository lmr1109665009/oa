/**
 * Created by xucuiyun on 2017/5/3.
 */
$(function(){
     var userName = request.QueryString("sessionId") == null ? '' : request.QueryString("sessionId");
	 
     $('.departDoc').on('click',function(){
         getData('2','部门文件柜','1');
     });

     $('.departDoc').trigger('click');

    var depBranch = '部门文件柜';

    var fileName = '';

    var reqId = '';

    getClickData = function(a,b,c){
        getData(a,b,c);
    }
    
   //上传文件功能开始
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
                        $('ul.breadList li:last-child').trigger('click');
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
                            $('ul.breadList li:last-child').trigger('click');
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
   //文件上传功能结束 
   
   //显示的页面函数，获取数据渲染页面
    function getData(idName,docName,PidName){
          reqId = idName;
           if($(".operate")){
               $(".operate").remove();
               $('th').css('width', '20%');
               $('th:nth-child(1)').css('width', '40%');
               $('td').css('width', '20%');
               $('td.docName').css('width', '40%');
            }
            if($('.upload')){
                $('.upload').css('display','none');
            }
        var url = _ctx + '/me/docType/newFrontList.ht';
        var url1 = _ctx + '/me/document/download.ht';
        var url2 = _ctx + '/me/document/frontSave.ht?sessionId='+ userName;
        
        //面包屑导航的显示
        if(PidName == 1){
            $('.breadList').html('<li>全部文件</li>');
            if(idName == 2){
                depBranch = '部门文件柜';
            }else if(idName == 3){
                depBranch = '公共文件柜';
            }
        }else if(PidName == 2 || PidName == 3){
            $('.breadList').html('<li class="upload"><button class="uploadFile">上传文件</button></li><li class="gobackOne"> 返回上一级 | </li><li class="gobackAll">'+depBranch+'</li><li onclick='+'getClickData('+idName+','+'"'+docName+'"'+','+PidName+')> /'+docName+'</li>');
            $('ul.breadList li:last-child').siblings().css('color', '#37A7F7');
            //回归到全部文件，没有导航的时候
                   $('.gobackAll').on("click",function(){
                           if($('.gobackAll').text() == '部门文件柜'){
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
            var li = document.createElement("li");
            $(li).text("/" + docName);
            $(li).get(0).onclick = function() {
                $(this).nextAll("li").remove();
                $(this).remove();
                getClickData(idName,docName,PidName);
            }
            $('ul.breadList').append(li);
            $('ul.breadList li:last-child').siblings().css('color', '#37A7F7');
        }
        //面包屑导航显示结束
        //请求文件夹列表数据显示
            $.ajax({
                type:'post',
                url: url+'?id='+idName,
                success:function(result) {
                    var data = result.data.varList;
                    var str = '';
                    //列表内容为空的展示
                    if (data.length == 0) {
                        //上传文件
                    	uploadFile(url2);
                    	//显示上传按钮，触发上传事件
                        $('.upload').css('display','inline-block');
                        $('.uploadFile').get(0).onclick = function(){
                            $('.newFile').trigger('click');
                        }
                        str += "<tr><td>此文件夹为空</td></tr>"
                        $('#fContent_tbody').html(str);
                        $('td').css("width",'100%');
                        return;
                    } else {
                    	//文件夹的展示
                        if (!data[0].documentIPath) {
                            for (var i = 0; i < data.length; i++) {
                                str += "<tr onclick=" + "getClickData(" + data[i].id + ',' + "'" + data[i].documentName + "'" + ',' + data[i].parentId + ")" + "><td class='docName'><img src='img/file.png'>" + data[i].documentName + "</td><td>" + data[i].docuemntSize + "</td><td>" + data[i].uploadTime + "</td><td>" + data[i].creatorName + "</td></tr>";
                                $('#fContent_tbody').html(str);
                            }
                        }
                        //文件的展示
                        else {
                        	//上传功能
                        	uploadFile(url2);
                            //显示上传按钮，触发上传事件
                            $('.upload').css('display','inline-block');
                            $('.uploadFile').get(0).onclick = function(){
                                $('.newFile').trigger('click');
                            }
                            var th = document.createElement("th");
                            $(th).attr('class', 'operate');
                            $(th).text("操作");
                            $('thead tr').append(th);
                            $('th').css('width', '20%');
                            for (var i = 0; i < data.length; i++) {
                                str += '<tr><td class="docName" title="'+data[i].documentName+'">' + data[i].documentName + '</td><td>' + data[i].docuemntSize + '</td><td>' + data[i].uploadTime + '</td><td>' + data[i].creatorName + '</td><td><a href="' + url1 + '?sessionId='+ userName + '&documentIPath=' + data[i].documentIPath +'&id=' + data[i].id + '">下载</a></td></td></tr>';
                            }
                            $('#fContent_tbody').html(str);
                            $('th').css('width', '15%');
                            $('th:nth-child(1)').css('width', '40%');
                            $('td').css('width', '15%');
                            $('td.docName').css('width', '40%');
                        }
                    }
                }
            });
    }
})