
    var $modalS =null;
  
    function closeModalS(){
	  if($modalS){
		  $modalS.modal('close');
	  }
    }
  
var EventsListS = function(element, options) {
  var aryHtml=['<div class="am-modal am-modal-loading am-modal-no-btn" tabindex="-1" id="my-modals">',
            '<div class="am-modal-dialog">',
            '<div class="am-modal-hd">没有更多记录了.</div>',
            '</div>',
            '</div>']
  
  var objBody=$("body");
  $("#wrapperSearch").append(aryHtml.join(""));

  var $main = $('#wrapperSearch');
  var $list = $main.find('#events-lists');
  var $pullDown = $main.find('#pull-downs');
  var $pullDownLabel = $main.find('#pull-down-labels');
  var $pullUp = $main.find('#pull-ups');
  var topOffset = -$pullDown.outerHeight();
  this.compiler = Handlebars.compile($('#tpi-list-search').html());
  this.prev = this.next = this.page = options.params.page;
  this.total = null;

  this.getURL = function(params) {
	    var queries = [];
	    for (var key in  params) {
		      if (key !== 'page' && key !='callback') {
		        queries.push(key + '=' + params[key]);
		      }
	    }
	    queries.push('page=');
	    var rtn= options.url + '?' + queries.join('&');
	    return rtn;
  };

  this.renderList = function(start, type) {
	    var _this = this;
	    var $el = $pullDown;
	    //var $el = $pullUp;
	
	    if (type === 'load') {
	    	$el = $pullUp;
	    }
	    options.params.page = start;
	    $.post(options.url,options.params).then(function(data) {
	      if(options.params.callback!=null) {
	    	  options.params.callback(data);
	      }
	      console.log(data)
	      //console.log("total:"+typeof(_this.total))
	      if(options.dataS==1){
	    	  //alert(Math.ceil(data.totalCounts/options.params.pageSize)+"Math")
	    	  _this.total = Math.ceil(data.totalCounts/options.params.pageSize);
	    	  //if(Math.ceil(data.totalCounts/options.params.pageSize)==1||data.totalCounts==0){ $pullDown.hide();$pullDown.css("opacity","0");$pullUp.css("opacity","0");}
	    	  //else{$pullDown.show();$pullDown.css("opacity","1");$pullUp.css("opacity","1");}
	    	  console.log("total:"+_this.total,data.totalCounts/options.params.pageSize)
	    	 var html = _this.compiler(data.results); 
		      if(data.totalCounts==0){$list.html('<div class="pull-action"><span class="am-icon-smile-o pull-label"> 抱歉，没有找到您查询的内容</span></div>');}
		      else if (type === 'refresh') {
		    	  $list.html(html);
		      } else if (type === 'load') {
		    	  $list.append(html);
		      } else {
		    	 $list.html(html);
		    	 //alert(this.next+"next");
		      }
	      }
	      else {

	      _this.total = data.total;
	      //if(data.total==1||data.total==0){ $pullDown.hide();$pullDown.css("opacity","0");$pullUp.css("opacity","0");}
	      // else{$pullDown.show();$pullDown.css("opacity","1");$pullUp.css("opacity","1");}
	      var html = _this.compiler(data.rowList);
	      //返回数据结构判断
          if(!data.total||!data.rowList){
              if(!data.data.taskList){
                  if(!data.data.copyList){
                      _this.total = data.data.TransToList.total;
                      html = _this.compiler(data.data.TransToList.rowList)
                  }else {
                      _this.total = data.data.copyList.total;
                      html = _this.compiler(data.data.copyList.rowList)
                  }
              }
              else {
                  _this.total = data.data.taskList.total;
                  html = _this.compiler(data.data.taskList.rowList)
              }
          }
	      if(_this.total==0){$list.html('<div class="pull-action"><span class="am-icon-smile-o pull-label"> 抱歉，没有找到您查询的内容</span></div>');}
	      
	      else if (type === 'refresh') {
	        $list.html(html);
	        
	      } else if (type === 'load') {
	        $list.append(html);
	      } else {
	        $list.html(html);
	      }
	      }
	      // refresh iScroll
	      setTimeout(function() {
	        _this.iScroll.refresh();
	      }, 100);
	    }, 
	    function() {
	    	console.log('Error...')
	    }).always(function() {
	    	_this.resetLoading($el);
		    if (type !== 'load') {
		        _this.iScroll.scrollTo(0, topOffset, 800, $.AMUI.iScroll.utils.circular);
		    }
	    });
  };

  this.setLoading = function($el) {
    $el.addClass('loading');
  };

  this.resetLoading = function($el) {
    $el.removeClass('loading');
  };

  this.init = function() {
    var myScroll = this.iScroll = new $.AMUI.iScroll('#wrapperSearch', {
      click: true
    });
    // myScroll.scrollTo(0, topOffset);
    var _this = this;
    var pullFormTop = false;
    var pullStart;

    this.URL = this.getURL(options.params);
    this.renderList(options.params.page);

    myScroll.on('scrollStart', function() {
      if (this.y >= topOffset) {
        pullFormTop = true;
      }
      pullStart = this.y;
    });

    myScroll.on('scrollEnd', function() {
     
      if (pullFormTop && this.directionY === -1) {
        _this.handlePullDown();
      }
      pullFormTop = false;
      
      console.info("pullStart:" + pullStart +",this.y" + this.y +",directionY:" +this.directionY);

      // pull up to load more
      if (pullStart === this.y && (this.directionY === 1)) {
        _this.handlePullUp();
      }
    });
    $("#events-lists").css("min-height",document.documentElement.clientHeight-55);
  };
  
  

  this.handlePullDown = function() {
    console.log('handle pull down');
//    if (this.prev > 1) {
      this.setLoading($pullDown);
      this.prev =this.next=1 ;
      
      this.renderList(this.prev, 'refresh');
//    } 
  };

  this.handlePullUp = function() {
    console.log('handle pull up');
    //alert(this.total+"total")
    if (this.next < this.total) {
      this.setLoading($pullUp);
      this.next += options.params.startpage;
      this.renderList(this.next, 'load');
    } else {
      	$modalS = $("#my-modals");
      	$modalS.modal();
      	setTimeout("closeModalS()",1000);
    }
  }
};

