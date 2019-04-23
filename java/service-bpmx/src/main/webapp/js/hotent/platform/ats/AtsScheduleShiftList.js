/*******************************************************************************
 * 
 * 考勤管理-排班列表js
 * 
 * <pre>
 *  
 * 作者：hugh zhuang
 * 邮箱:zhuangxh@jee-soft.cn
 * 日期:2015-05-29-上午11:10:52
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 * 
 ******************************************************************************/
var AtsScheduleShiftListPlugin = null;
;
(function($, window, document, undefined) {

	var pluginName = "AtsScheduleShiftList", defaults = {}, me,
	_startTime = null, // 开始时间
	_endTime = null, // 结束时间
	_showType = null;
	function Plugin(element, options) {
		this.settings = $.extend({}, defaults, options);
		this._defaults = defaults;
		this._name = pluginName;
		this.init();
	}

	Plugin.prototype = {
		/**
		 * 初始化页面
		 */
		init : function() {
			me = this;
			AtsScheduleShiftListPlugin = this;
			me.searchGrid();
			$('a.fa-search').click(function(){
				me.searchGrid();
			});
			$('a.del').click(function(){
				me.del();
			});
			$("input[name='showType']").click(function(){
				me.searchGrid();
			});
			this.handleSearchKeyPress();
		},
		/**
		 * 处理回车查询
		 */
		handleSearchKeyPress:function (){
			$(".search-form :input").keypress(function(e) {
				if(	e.keyCode == 13){//回车
					$("a.fa-search").click();
				}else if(e.keyCode == 27){//ESC
				}		
		    })
		},
		del:function(){
			this.initVal();
			var ids=$("#scheduleShiftGrid").jqGrid("getGridParam","selarrrow");
			if (ids.length == 0) {
				$.ligerDialog.warn("请选择记录！");
				return false;
			}
			$.ligerDialog.confirm('确认删除吗？','提示信息',function(rtn) {
				if(rtn) {
					$.ajax({
						type : "POST",
						url : __ctx + "/platform/ats/atsScheduleShift/delData.ht",
						dataType: 'json',
						data : {
							id:ids.join(","),
							startTime : _startTime,
							endTime : _endTime,
							showType:_showType
						},
						success : function(data) {
						     if (!data.success) {
						    	 $.ligerDialog.error(data.msg);
				                return;
				             } 
						     $.ligerDialog.success(data.msg,function(){
						    	  me.searchGrid(data);
						     });
						 
						}
					});
					}
			});
			
		},
		searchGrid:function(){
			this.initVal();
			this.initGrid();
		},
		initVal:function(){
			_startTime = $("#startTime").val();
			_endTime = $("#endTime").val();
			_showType = $("input[name='showType']:checked").val();
		},
		initGrid : function() {
			if($.isEmpty(_startTime)){
				$.ligerDialog.warn("开始时间为空！");
				return;
			}
			if($.isEmpty(_endTime)){
				$.ligerDialog.warn("结束时间为空！");
				return;
			}
			$.ligerDialog.waitting("请稍后……");
			$.ajax({
				type : "POST",
				url : __ctx + "/platform/ats/atsScheduleShift/scheduleShiftGrid.ht",
				dataType: 'json',
				data : {
					startTime : _startTime,
					endTime : _endTime,
					showType:_showType
				},
				success : function(data) {
				     if (!data.success) {
				    	alert(data.msg);
				    	$.ligerDialog.closeWaitting();
		                return;
		             } 
				   me.loadGrid(data);
				}
			});
		},
		loadGrid:function(data){
			$('#dataGrid').empty();
			$('#dataGrid').append('<div id="gridPager1"></div><table id="scheduleShiftGrid" style="height:1px;"></table>');
			var options = {
					url : __ctx+ "/platform/ats/atsScheduleShift/scheduleShiftData.ht",
					datatype : "json",
					postData:{
						"orgPath":$('#orgId').val(),
						"Q_cardNumber_SL":$('#cardNumber').val(),
						"Q_fullname_SL":$('#userName').val(),
						"Q_beginattenceTime_DL":_startTime,
						"Q_endattenceTime_DE":_endTime,
						"showType":_showType
					},
					multiselect : true,
					rownumbers : false,
					colNames : data.colNames,
					colModel : data.colModel,
					gridview : true,
					pginput : true,
					autoheight : true,
					shrinkToFit : data.colModel.length > 10 ? false : true,
					height : 'auto',
					width : document.body.clientWidth - 30,
					cellEdit : false,
					sortable : false,
					viewrecords : true,// 是否在浏览导航栏显示记录总数
					rowNum : _showType ==0?20:10,// 每页显示记录数
					rowList : [ 10, 20, 50, 100, 200 ],// 用于改变显示行数的下拉列表框的元素数组。
					jsonReader : {
						root : "results",// json中代表实际模型数据的入口
						total : "total", // json中代表总页数的数据
						page : "page", // json中代表当前页码的数据
						records : "records",// json中代表数据行总数的数据
						repeatitems : false
					// 如果设为false，则jqGrid在解析json时，会根据name来搜索对应的数据元素（即可以json中元素可以不按顺序）；而所使用的name是来自于colModel中的name设定。
					},
					prmNames : {
						page : "page", // 表示请求页码的参数名称
						rows : "pageSize" // 表示请求行数的参数名称
					},
					pager : $('#gridPager1'),
					loadComplete : function(data) {
						$.ligerDialog.closeWaitting();
					}
				};

			$("#scheduleShiftGrid").html();
			$("#scheduleShiftGrid").jqGrid(options);
			
			if(_showType ==1){
				$('#scheduleShiftGrid').jqGrid('setFrozenColumns');
			}
		}
	}

	$.fn[pluginName] = function(options) {
		return this
				.each(function() {
					if (!$.data(this, "plugin_" + pluginName)) {
						$.data(this, "plugin_" + pluginName, new Plugin(this,
								options));
					}
				});
	};

})(jQuery, window, document);

$(document).ready(function() {
	$('body').AtsScheduleShiftList();
});