var companyData = {
    "1" : {
        "company" : "象翌集团公司",
        "zichan-key" : "公司资产分布数量：",
        "zichan-value" : "3332件",
        "address" : "南宁市民族大道131号航洋国际城1号",
        "status" : "办公使用中",
        "link" : "http://suneee.oa.weilian.cn/lizilian/quanjing/jituan/index.html",
        "pic": "./img/jituan.jpg",
        "pic_phone": "./img/jituan.jpg"
    },
    "7" : {
        "company" : "翌捷公司",
        "zichan-key" : "最大可容库存：",
        "zichan-value" : "28378个货位",
        "address" : "南宁市良庆区银海大道1219号南宁保税物流中心",
        "status" : "使用中",
        "link" : "http://suneee.oa.weilian.cn/lizilian/quanjing/yijie/index.html",
        "pic": "./img/yijie.jpg",
        "pic_phone": "./img/yijie.jpg"
    },
    "8" : {
        "company" : "翌方公司",
        "zichan-key" : "公司资产分布数量：",
        "zichan-value" : "1711件",
        "address" : "南宁市总部路3号中国-东盟科技企业孵化基地",
        "status" : "使用中",
        "link" : "http://suneee.oa.weilian.cn/lizilian/quanjing/yifang/index.html",
        "pic": "./img/yifang.jpg",
        "pic_phone": "./img/yifang.jpg"
    }
};

$(function(){
    // $('.pos-bg').css("visibility", "visible");
    // $('.pos-bg').hide();
    $('#posdata').find('.pos01').click(function(){
        // $(this).addClass('selected').siblings().removeClass('selected');
        // $('#index-demo').show();
    });
    $('#picdtl-list').find('li').click(function(e){
        var url = $(e.target).attr('src');
        $('.changimg').attr('src',url);
        $(this).addClass('selected').siblings().removeClass('selected')
    })
    var temp = $('.picdtl-top');
    var temph = temp.height(),tempw = $('.picdtl-detail').width();
    $('.picdtl-detail').on('touchmove',function(e){
        e.preventDefault();
      var touch = e.originalEvent.targetTouches[0]; 
      var y = touch.pageY-110,x = touch.pageX-300;
      if(x<0){
        x=0;
      }else if(x>window.innerWidth-tempw*2){
          x=window.innerWidth-tempw;
      }
      if(y<0){
          y=0;
      }else if(y>temph-110*2){
        y=temph-110;
      }
      $(this).css({
          'left':x+'px',
          'top':y+'px',
          'margin':'0px'
      })
    })

});

//index: 公司索引（1：集团公司，7：翌捷公司，8：翌方公司）
//type: 设备类型（1：pc，2：手机）
function showContent(index , type){
    window.event.stopPropagation();
    //先隐藏所有，再单独显示
    // $('.pos-bg').hide();
    $('.content-div').removeClass('selected');
    $('.content-div').css('z-index', '1');

    var bgCls = ".pos-bg.pos" + index;
    var pointCls = ".pos-icon.pos" + index;
    var divCls = ".content-div.pos" + index;
    $(divCls).css('z-index', '999');
    // if ($(bgCls).is(':visible')){
    //     $(bgCls).hide();
    // }else{
    //     $(bgCls).show();
        $(divCls).addClass('selected');
        showContentWithData(index, type);
        $('#index-demo').show();
        $('#index-demo').css('z-index', '999');
    // }

}

function showContentWithData(index, type){
    var data = companyData[index];
    $('.title').text(data["company"]);
    $('.status').text("状态：" + data["status"]);
    $('.zichan-key').text(data["zichan-key"]);
    $('.zichan-value').text(data["zichan-value"]);
    $('.address').text(data["address"]);
    $('.linkblock').attr('href', data["link"]);
    if (type == 1){
        $('.company-pic').attr('src', data["pic"]);
    }else if (type == 2){
        $('.company-pic').attr('src', data["pic_phone"]);
    }
}

function hideDetail(){
    if ($('#index-demo').is(':visible')){
        $('.content-div').removeClass('selected');
        $('#index-demo').hide();
    }
}