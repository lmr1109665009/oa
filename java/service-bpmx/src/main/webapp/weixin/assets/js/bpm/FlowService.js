

var  flowServiceModule = angular.module('flowServiceModule',["base"]);

flowServiceModule.factory('flowService',['baseService',function(baseService){
	return {
		/**
		 * 获取发起表单界面
		 */
		getStartForm:function(defId,formKey,version,pk,runId,isSubProcess,sceneId){
			var url=__ctx +"/weixin/bpm/getStartForm.ht";
			formKey=formKey || "";
			version=version || 0;
			pk=pk || "";
			var data={defId:defId,formKey:formKey,version:version,pk:pk,runId:runId,isSubProcess:isSubProcess,sceneId:sceneId};
			var def=baseService.postForm(url,data);
			return def;
		},
		
		
		/**
		 * 添加表单规则
		 */
		addRule:function(){
			if (typeof com.hotent.form.rule.CustomRules =="undefined") return; 
			
			for(var i=0,rule;rule=com.hotent.form.rule.CustomRules[i++];){
				$.fn.addRule(rule.name,rule);
			}
		},
	
		/**
		 * 数据返回构建表单。
		 */
		handForm:function($scope,data,defId){
			if(!data.result){
				angular.element("#formHtml").html("<center style='padding:20% 10px 0 10px;'>出错了:" + data.msg+"</center>");
				return;
			}
			var isGet=data.get;
			var temp="";
			if(isGet){
				json={formKey:data.formKey,version:data.version};
				//TODO 缓存修改回来
				HtUtil.setJSON("form_" + defId,json);
				HtUtil.set("template_" + defId,data.template);
				temp=data.template;
			}
			else{
				temp=HtUtil.get("template_" + defId);
			}
			var template = Handlebars.compile(temp);
			
			var permission=data.permission;
			
			permission = JSON.parse(JSON.stringify(permission).toLowerCase());
			var html = template(permission);
			
			//处理弹出框只读子表 begin
			var $html =$(html.replace(/form/g,"form_div")); // 弹出框编辑多个form会影响到后面的子表
			var readOnlySubTable =$("[type='subTable'][readonly='readonly']",$html);
			readOnlySubTable.each(function(){ //初始化弹出框数据
				var tableName = $(this).attr("tableName");
				if(tableName){
					$scope.subTempData={};
					$scope.subTempData[tableName]={}; 
				}
			});
			if(readOnlySubTable.length>0){
				$("[permission='b'],[permission='w']",readOnlySubTable).attr("permission","r");
				html = $html[0].outerHTML;
				html = html.replace(/form_div/g,"form");// 弹出框编辑多个form会影响到后面的子表，恢复
			}
			//处理天出框只读子表 end
			
			//表单
			$scope.formHtml=html;
			//数据
			$scope.flowParam=data.flowParam;
			$scope.data=data.data;
			$scope.subject=data.subject;
			//权限
			$scope.permission=permission;
			//如果权限为必填，自动添加一行。
			this.addRequireRow($scope);
			//发布表单发布事件
			$scope.$broadcast('formInited');
			
			
		},
		/**
		 * 转换JSON。
		 * 将数据格式转换为之前的数据格式。
		 */
		convertJson:function(json){
			var toJson={};
	        toJson.main={}
	        toJson.main.fields=json.main;
		        
		    toJson.sub=[];
		        
		    var fromSub=json.sub;
		        
		    for(var key in fromSub){
	        	var subTb={};
	        	var obj=fromSub[key];
	        	subTb.tableName=key;
	        	subTb.fields=obj.rows;
	        	
	        	toJson.sub.push(subTb);
		    }
		        
		    return JSON.stringify(toJson);
		},
		checkForm:function($scope){
			if($scope.myform.$invalid){
				var invalidElements = $(".ng-invalid");
				var element = $(invalidElements[1]);
				element.popover('open');
				
				//特殊处理弹出框编辑模式
				var msg ="";
				var ngModel =element.attr("ng-model")
				if(ngModel.indexOf("subTempData")!= -1){
					var tableDiv =$("[tableName='"+ ngModel.split(".")[1]+"']");
					msg ="子表["+tableDiv.attr("tableDesc")+"]";
				}
				//end 特殊处理弹出框编辑模式
				
				Alert("提示信息",msg+"表单未校验通过！");
				return false;
			}
			return true;
		},
		/**
		 * 启动流程。
		 */
		startFlow:function(flowKey,data,runId){
			var url=__ctx +"/platform/bpm/task/startFlow.ht";
			var formJson=this.convertJson(data);
			var data={fromMobile:1,flowKey:flowKey,formData:formJson,runId:runId};
			var def=baseService.postForm(url,data);
			return def;
		},
		
		/**
		 * 保存草稿
		 */
		saveDraft:function(actDefId,data,runId,businessKey){
			var url=__ctx +"/platform/bpm/task/saveForm.ht";
			if(businessKey && runId) url =__ctx +"/platform/bpm/task/saveData.ht";
			var formJson=this.convertJson(data);
			var data={fromMobile:1,actDefId:actDefId,formData:formJson,runId:runId,businessKey:businessKey};
			var def=baseService.postForm(url,data);
			return def;
		},
		/**
		 * 删除草稿
		 */
		delDraft:function(runId){
			var url=__ctx +"/platform/bpm/processRun/delDraft.ht";
			var data={runId:runId,isMobile:true};
			var def=baseService.postForm(url,data);
			return def;
		},
		/**
		 * 在子表中添加行。
		 */
		addRow:function($scope,tableName){
			var row= $scope.data.sub[tableName].row;
			var obj=$.extend( {}, row) ;
			$scope.data.sub[tableName].rows.push(obj);
			return false;
		},
		
		editRow:function($scope,tableName,index){
			if(!$scope.subTempData)$scope.subTempData={};
			var tempTableData ;
			
			if(index===undefined){
				tempTableData =$.extend({}, $scope.data.sub[tableName].row);
				index=$scope.data.sub[tableName].rows.length;//如果没有index 默认为添加一条
			}else{
				tempTableData =$.extend({},$scope.data.sub[tableName].rows[index]);
			}
			tempTableData.$index =index;
			$scope.subTempData[tableName]=tempTableData; 
			$("#"+tableName+"_editDialog").modal();
			return false;
		},
		/**
		 * 子表必填处理。
		 */
		addRequireRow:function($scope){
			for(var tableName in $scope.permission.table){
				var require=$scope.permission.table[tableName].require;
				if(!require) continue;
				
				var rows=$scope.data.sub[tableName].rows;
				if(rows.length>0) continue;;
				
				var row= $scope.data.sub[tableName].row;
				var obj=$.extend( {}, row) ;
				rows.push(obj);
			}
		},
		/**
		 * 删除一行数据。
		 */
		removeRow:function($scope,tableName,idx){
			var rows = $scope.data.sub[tableName].rows;
			var require=$scope.permission.table[tableName].require;
			if(rows.length==1 && require){
				Alert("子表必填！");
				return ;
			}
			$scope.data.sub[tableName].rows.splice(idx,1);
			!$scope.$parent.$$phase&&scope.$parent.$digest();
		},
		/**
		 * 返回任务表单。
		 */
		getTaskForm:function(taskId,formKey,version){
			var url=__ctx +"/weixin/bpm/getTaskForm.ht";
			formKey=formKey || "";
			version=version || 0;
			var data={taskId:taskId,formKey:formKey,version:version};
			var def=baseService.postForm(url,data);
			return def;
		},
		/**
		 * 审批流程。
		 */
		approveFlow:function(taskId,voteAgree,voteContent,back,data){
			var url=__ctx +"/platform/bpm/task/complete.ht";
			var formJson=this.convertJson(data);
			var obj={taskId:taskId,voteAgree:voteAgree,voteContent:voteContent,formData:formJson,back:back,fromMobile:1};
			var def=baseService.postForm(url,obj);
			return def;
		},
		/**
		 * 获取流程实例表单。
		 */
		getInstForm:function(runId,formKey,version){
			var url=__ctx +"/weixin/bpm/getInstForm.ht";
			formKey=formKey || "";
			version=version || 0;
			var data={runId:runId,formKey:formKey,version:version};
			var def=baseService.postForm(url,data);
			return def;
		},
		/**
		 * 显示审批历史。
		 */
		showOpinion:function(runId){
			var url=__ctx +"/weixin/bpm/opinions.html?runId="+runId;
			var conf={};
			conf.title="审批历史" ;
			conf.url=url;
			createPopupDialog(conf,'flowStartDialog');
		},
		
		/**
		 * 手机流程图
		 */
		showFlowPic:function(runId){
			var url=__ctx +"/bpmImage?runId="+runId;
			var conf={};
			conf.title="流程图"
			conf.url=url;
			createPopupDialog(conf,'flowApproveDialog');
		},
		/**
		 * 获取通知类型。
		 */
		getInformType:function(){
			var url=__ctx +"/platform/system/share/getInformType.ht";
			var def=baseService.get(url);
			return def;
		},
		/**
		 * 取消代理。
		 */
		cancelAgent:function(data){
			var url=__ctx +"/platform/bpm/bpmTaskExe/cancel.ht";
			var def=baseService.postForm(url,data);
			return def;
		}

	};
}])