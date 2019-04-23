/*******************************************************************************
 * 
 * 考勤管理-考勤计算js
 * 
 * <pre>
 *  
 * 作者：hugh zhuang
 * 邮箱:zhuangxh@jee-soft.cn
 * 日期:2015-06-01-上午11:10:52
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 * 
 ******************************************************************************/
var AtsAttenceCalculatePlugin = null;
;(function($, window, document, undefined) {
	var pluginName = "AtsAttenceCalculate",
	defaults = {
	 },
	 me,
	 _attencePolicyId = null,
	 _startTime = null,
	 _endTime = null,
	_sidValue = [];
	function Plugin(element, options) {
		this.settings = $.extend({}, defaults, options);
		this._defaults = defaults;
		this._name = pluginName;
		this.init();
	}
	
	Plugin.prototype = {
		init:function(){
			me = this;
			AtsAttenceCalculatePlugin = this;
			this.initAttenceCycle();
			//选择tab
			$('.nav-tabs > li').click(function(){
			    var $this    = $(this);
				if($this.hasClass('active'))
					   return ;
			 $this.siblings('li').removeClass('active');
			   $this.addClass("active");
			  me. searchGrid();
			
			});
			//加载数据
			this.loadGrid();
			//查询
			$('a.fa-search').click(function(){
				me.searchGrid();
			});
			$('a.allCalculate').click(function(){
				me.startUpdateCalculatingStatus();
				me.calculate();
			});
			
			$('a.calculateSelect').click(function(){
				  var selectedIds = $("#reportGrid").jqGrid('getGridParam','selarrrow');
			
				if (selectedIds == null || selectedIds.length < 1) {
					$.ligerDialog.alert("请选择要计算的人员！","提示信息");
					return;
				}
				var fileIds = [];
		  		for (var i = 0; i < selectedIds.length; i++) {
        		     var rowData = $("#reportGrid").jqGrid("getRowData", selectedIds[i]);
        		     if(!rowData.fileId) continue
        			 fileIds.push(rowData.fileId);
        		}
		  		
		  		me.startUpdateCalculatingStatus();
		  		me.calculate(fileIds.join(","));
			
			});
			$('a.summary').click(function(){
				var url=__ctx + "/platform/ats/atsAttenceCalculateSet/edit.ht?type=1";
					url=url.getNewUrl();
					DialogUtil.open({
						height:400,
						width: 500,
						title : '汇总显示',
						url: url, 
						isResize: true,
						sucCall:function(rtn){
						//	me.loadGrid();
						}
					});	
			});
			
			$('a.detail').click(function(){
				var url=__ctx + "/platform/ats/atsAttenceCalculateSet/edit.ht?type=2";
				url=url.getNewUrl();
				DialogUtil.open({
					height:400,
					width: 500,
					title : '明细显示',
					url: url, 
					isResize: true,
					sucCall:function(rtn){
					//	me.loadGrid();
					}
				});	
			});
			
			$('a.export').click(function(){
				var action = $('.nav-tabs > li.active').attr("action");
				$('#action').val(action);
				$('#orgPath1').val($('#orgId').val())
				$('#fullname1').val($('#fullname').val())
				$('#attencePolicy1').val($('#attencePolicy').val());	
//				var attenceCycleOption =  $("#attenceCycle").find("option:selected");
				$('#startTime1').val($('#startTime').val());
				$('#endTime1').val($('#endTime').val());
		
				var type = $('#attenceType').val();
				if(!$.isEmpty(type))
					type =type.join(",");
				else
					type ="";
				$('#attenceType1').val(type);
				$.ligerDialog.waitting("请稍后……");
				$("#exportForm").submit();
				var isDownload = window.setInterval(function() {
					$.ajax({
						type : "POST",
						dataType:"json",
						url :__ctx+"/platform/ats/atsAttenceCalculate/isDownload.ht",
						success : function(data) {
							if(data.success){
								$.ligerDialog.closeWaitting();
								clearInterval(isDownload);
							}
						}
					});
				}, 1000);
				
			});
			this.handleSearchKeyPress();
		
		    $("#attenceType").multiselect({
		        noneSelectedText: "=请选择=",
		        checkAllText: "全选",
		        uncheckAllText: '全不选',
		        selectedText: '# 已选择'
		    });
		},
		//开启检测后台计算session的状态的定时器，更新数据
		startUpdateCalculatingStatus:function(){
			var updateCalculatingStatus=window.setInterval(function() {
				$.post(__ctx+"/platform/ats/atsAttenceCalculate/getCalculatingState.ht",{},
					function (data, textStatus){
						if(data.status){
							$.ligerDialog.closeWaitting();
							$.ligerDialog.waitting(data.msg);
						}else{
							clearInterval(updateCalculatingStatus);
						}
					}, 
				"json");
			}, 3000);
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
		initAttenceCycle:function(){
			var date = new Date();
			var ydate = date.Format("yyyy-MM");
			$('#attenceCycle').val(ydate);
		},
		searchGrid:function(){
			var flag= me.getInitVal();
			if(flag){
				return;
			} 
			$.ligerDialog.waitting("请稍后……");
			var action = $('.nav-tabs > li.active').attr("action");
			$('#attenceTypeLi').hide();
			$('#abnormityLi').hide();
		   if(action == "1"){//已计算人员
			   me.loadGrid();
		   } else if(action == "2"){//未计算人员
			  me.loadNoUserGrid();
		   } else  if(action == "3"){//结果明细
			   $('#abnormityLi').show();
				$('#attenceTypeLi').show();
			   me.loadResultDetailGrid();
		   }
		},
		/**
		 * 计算数据
		 */
		calculate:function(fileIds){
			var action = $('.nav-tabs > li.active').attr("action");
			if(action == "3"){
				$.ligerDialog.alert("该视图不进行计算！","提示信息");
				return;
			}	
	  		var flag = me.getInitVal();
			if(flag){
				return;
			} 
			$.ligerDialog.waitting("请稍后……");
			me.remoteCall(
				{method : "calculate",
				param : {
					"startTime" : _startTime,
					"endTime" : _endTime,
					"attencePolicyId" : _attencePolicyId,
					"fileIds":fileIds?fileIds:''
				},
				success : function(data) {
					$.ligerDialog.closeWaitting();
					if(data.success)
						me.searchGrid();
				}
			});
		
		},
		getInitVal:	function(){
			_attencePolicyId = $('#attencePolicy').val();
//			var attenceCycleOption =  $("#attenceCycle").find("option:selected");attenceCycleOption.
			_startTime = $('#startTime').val();
			_endTime = $('#endTime').val();
			if($.isEmpty(_attencePolicyId) ){
				$.ligerDialog.alert("请选择考勤制度");
				return true;
			}
			if($.isEmpty(_startTime) ){
				$.ligerDialog.alert("请输入开始时间");
				return true;
			}
			if($.isEmpty(_endTime) ){
				$.ligerDialog.alert("请输入结束时间");
				return true;
			}
			return false;
		},
		remoteCall:function(conf){
			var url = conf.url;
			if(conf.method)
				url = __ctx+"/platform/ats/atsAttenceCalculate/"+conf.method+".ht";
			$.ajax({
				type : "POST",
				url :url,
				data : conf.param?conf.param:{},
				success : function(data) {
					conf.success.call(this,data);
				}
			});
		},
		loadGrid:function(){
			$('#dataGrid').empty();
			$('#dataGrid')
					.append(
							'<div id="gridPager1"></div> <table id="reportGrid" style="height:1px;"></table>');
			this.renderDataGrid("getGridColModel");
		},
		renderDataGrid:function(method){
			me.getInitVal();
			me.remoteCall({
				method : method,
				param : {
					"startTime" : _startTime,
					"endTime" : _endTime,
					"attencePolicyId" : _attencePolicyId
				},
				success : function(data) {
					me.doRenderDataGrid(data);
				}
			});
		},
		doRenderDataGrid : function(data) {
			var table = $("#reportGrid");
			var	url = __ctx+"/platform/ats/atsAttenceCalculate/reportGrid.ht";
			var colNames = data.colNames;
			var	colModel = data.colModel;
			
			
			var options = {
				url : url,
				postData:{
					"orgPath":$('#orgId').val(),
					"Q_fullname_SL":$('#fullname').val(),
					"Q_beginattenceTime_DL":_startTime,
					"Q_endattenceTime_DE":_endTime,
					"Q_attencePolicy_L":_attencePolicyId,
					"Q_account_S":$('#account').val()
				},	
				datatype : "json",
				multiselect : true,
				rownumbers : false,
				colNames : colNames,
				colModel : colModel,
				rowNum : 30,
			//	pager : '#gridPager1',
				autoheight : true,
				height : 'auto',
				width:document.body.clientWidth-30,
				gridview : true,
				pginput : true,
				shrinkToFit :colModel.length > 10 ? false
						: true,
				autoScroll: true,
				viewrecords : true,
				rowNum : 10,// 每页显示记录数
				rowList : [ 10, 20, 50, 100],// 用于改变显示行数的下拉列表框的元素数组。
				jsonReader : {
					root : "results",// json中代表实际模型数据的入口
					total : "total", // json中代表总页数的数据
					page : "page", // json中代表当前页码的数据
					records : "records",// json中代表数据行总数的数据
					repeatitems :false
				// 如果设为false，则jqGrid在解析json时，会根据name来搜索对应的数据元素（即可以json中元素可以不按顺序）；而所使用的name是来自于colModel中的name设定。
				},
				prmNames : {
					page : "page", // 表示请求页码的参数名称
					rows : "pageSize" // 表示请求行数的参数名称
				},
				pager : $('#gridPager1'),
				grouping : true,
				groupingView : {
					groupField : [ 'orgName' ],
					groupColumnShow : [ false ],
					groupText : [ '<b>{0}</b>' ],
					groupCollapse : false,
					groupOrder : [ 'asc' ],
					groupSummary : [ true ],
					groupDataSorted : true,
					showSummaryOnHide : true
				},
				onCellSelect : function(rowid, index, contents,event) {
					if (index == 0) 
						return;
					var data = $("#reportGrid").jqGrid("getRowData", rowid),
						fileId = data['fileId'],colName = colModel[index - 1].name,
						userName = data['userName'];
					//
					if ( colName == 'account' ||colName == 'userName') {
						me.showCalendarDetailAction(fileId,userName,_startTime,_endTime);
					}else  if (colName.substring(0, 1) == "S") {
						if (contents == 0) {
							$.ligerDialog.alert("该汇总项没有明细！","提示信息");
							return;
						}
						me.showSummaryDetailAction(fileId,userName,colName,colModel[index - 1].label,_startTime,_endTime);
					}else  {
						me.showBillDetailAction(fileId,userName,colModel[index - 1].index);
					}
				},
				onSelectRow : function(id) {
					jQuery('#reportGrid').jqGrid('editRow', id,
							false, function() {
							});
					//_sidValue
//					sidValue.push(id);
//					lastsel2 = id;
//					$("#reportGrid")
//							.attr("sid", sidValue.join(","));
				},
				gridComplete : function() {
					$.ligerDialog.closeWaitting();
				}
			};
			$("#reportGrid").html();
			$("#reportGrid").jqGrid(options);
		//	$("#reportGrid").jqGrid('setFrozenColumns');
			
			var  h= (document.body.clientHeight)+'px';
			$('#reportGrid .ui-jqgrid-bdiv').css('height',  h).css(
					'width', '100%').css('overflow-y', 'scroll',
					'overflow-x', 'scroll');
			$('#reportGrid .ui-jqgrid .ui-jqgrid-htable th div').css('height','35px');
			},
			/**
			 * 展示日历
			 */
			showCalendarDetailAction:function(fileId,userName,startTime,endTime){
				var url=__ctx + "/platform/ats/atsAttenceCalculate/calendar.ht?fileId="
						+fileId+"&startTime="+startTime+"&endTime="+endTime;
				url=url.getNewUrl();
				DialogUtil.open({
					height:600,
					width: 800,
					title : userName+"("+startTime+"--"+endTime+")",
					url: url, 
					isResize: true
				});
			},
			/**
			 * 汇总明细
			 */
			showSummaryDetailAction:function(fileId,userName,colName,collabel,startTime,endTime){
				var url=__ctx + "/platform/ats/atsAttenceCalculate/summary.ht?fileId="
				+fileId+"&startTime="+startTime+"&endTime="+endTime+"&colName="+colName;
					url=url.getNewUrl();
					DialogUtil.open({
						height:600,
						width: 800,
						title : userName+"["+collabel+"]("+startTime+"--"+endTime+")",
						url: url, 
						isResize: true
					});
			},
			/**
			 * 详细明细
			 */
			showBillDetailAction:function(fileId,userName,colName){
				var url=__ctx + "/platform/ats/atsAttenceCalculate/bill.ht?fileId="
				+fileId+"&colName="+colName;
					url=url.getNewUrl();
				DialogUtil.open({
					height:600,
					width: 800,
					title : userName+"["+colName+"]---考勤记录",
					url: url, 
					isResize: true
				});
			},

			/**
			 * 没有计算的人员数据
			 */
			loadNoUserGrid:function(){
				var conf = {
					url:__ctx+"/platform/ats/atsAttenceCalculate/getNoneCalList.ht",
					colNames : [ 'fileId', '考勤编号', '工号', '姓名', '组织', '岗位' ],
					colModel : [ {
						name : 'fileId',
						hidden : true
					}, {
						name : 'cardNumber'
					}, {
						name : 'account',
						width : 80
					}, {
						name : 'userName'
					}, {
						name : 'orgName'
					}, {
						name : 'posName'
					} ],
					multikey:"fileId",
				};
				me.renderReportGrid(conf);
			},
			/**
			 * 结果汇总明细
			 */
			loadResultDetailGrid:function(){
				var conf = {
						url:__ctx+"/platform/ats/atsAttenceCalculate/detailList.ht",
						colNames : [ 'id', '姓名', '工号', '组织', '考勤日期',"星期","班次名称","是否异常","类型","第一段上班","第一段下班","第二段上班","第二段下班", "第三段上班","第三段下班"],
						colModel : [ {
							name : 'id',
							hidden : true
						}, {
							name : 'userName',
							width : 80
						}, {
							name : 'account',
							width : 80
						}, {
							name : 'orgName',
							width : 80
						}, {
							name : 'attenceTime',
							width : 80
						}, {
							name : 'week',
							width : 80
						},{
							name : 'shiftName',
							width : 80
						},{
							name: 'abnormity',
							width : 80
						},{
							name : 'attenceType',
							width : 80
						},{
							name : 'shiftTime11',
							width : 80
						},{
							name : 'shiftTime12',
							width : 80
						}, {
							name : 'shiftTime21',
							width : 80
						},{
							name : 'shiftTime22',
							width : 80
						},{
							name : 'shiftTime31',
							width : 80
						},{
							name : 'shiftTime32',
							width : 80
						} ],
						multikey:"id",
					};
					me.renderReportGrid(conf);
			},
			renderReportGrid:function(conf){
				$('#dataGrid').empty();
				$('#dataGrid').append('<div id="gridPager1"></div> <table id="reportGrid" style="height:1px;"></table>');
	
				var type = $('#attenceType').val();
				if(!$.isEmpty(type))
					type =type.join(",");
				else
					type ="";
				
				var options = {
					url:conf.url,
					postData:{
						"orgPath":$('#orgId').val(),
						"Q_fullname_SL":$('#fullname').val(),
						"Q_account_S":$('#account').val(),
						"Q_beginattenceTime_DL":_startTime,
						"Q_endattenceTime_DE":_endTime,
						"Q_attencePolicy_L":_attencePolicyId,
						"type":type,
						"Q_abnormity_S":$("#abnormity").val()
					},	
					datatype : "json", // 数据来源，本地数据
					mtype : "POST",// 提交方式
					height : document.body.clientHeight - 265,// 高度，表格高度。可为数值、百分比或'auto'
					width : document.body.clientWidth - 30,
					colNames :conf.colNames,
					colModel : conf.colModel,
					multikey : conf.multikey?conf.multikey:"id",
					multiselect : true,
					rownumbers : true,// 添加左侧行号
					viewrecords : true,// 是否在浏览导航栏显示记录总数
					rowNum : 10,// 每页显示记录数
					rowList : [ 10, 20, 50, 100],// 用于改变显示行数的下拉列表框的元素数组。
					jsonReader : {
						root : "results",// json中代表实际模型数据的入口
						total : "total", // json中代表总页数的数据
						page : "page", // json中代表当前页码的数据
						records : "records",// json中代表数据行总数的数据
						repeatitems :false
					// 如果设为false，则jqGrid在解析json时，会根据name来搜索对应的数据元素（即可以json中元素可以不按顺序）；而所使用的name是来自于colModel中的name设定。
					},
					prmNames : {
						page : "page", // 表示请求页码的参数名称
						rows : "pageSize" // 表示请求行数的参数名称
					},
					shrinkToFit : conf.colModel.length > 10 ? false:true,
					pager : $('#gridPager1'),
					gridComplete : function() {
						$.ligerDialog.closeWaitting();
					}
				};
				$("#reportGrid").html();
				$("#reportGrid").jqGrid(options);
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
	$('body').AtsAttenceCalculate();
});

function selectAttencePolicy(){
	AtsAttencePolicyDialog({
		isSingle:true,
		callback:function(data){
			$('#attencePolicy').val(data.id);
			$('#attencePolicyName').val(data.name);
		}
	})
}

function selectOrg(){
	OrgDialog({isSingle:true,callback:function(orgId,orgName){
		$('#orgId').val(orgId);
		$('#orgName').val(orgName);
	}});
}


function selectUser(){
	UserDialog({isSingle:true,callback:function(userId,userName){
		$('#userId').val(userId);
		$('#userName').val(userName);
	}});
}
