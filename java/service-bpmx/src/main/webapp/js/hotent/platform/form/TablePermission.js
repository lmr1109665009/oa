var TablePermission = {
	init : function(){
		/*TablePermission.handleMainTableRequest();*/
		if(typeof permission !="undefined"){
			TablePermission.handleSubTable(permission);
		}
		TablePermission.handlePerTag();//处理表单中的权限控制字段
	},
	/**
	  * 主表的权限补充（必埴）
	  * 
	  */
	handleMainTableRequest:function(){
		//主表的附件必埴验证问题
		$("div[name=div_attachment_container]").each(function() {
			var div_me=$(this);
			if(div_me.attr("right")!='b') return true;
			$("a.link",div_me).each(function(){
	        	var me=$(this);	
        		var validRule = me.attr("validate");
				if ( validRule != null && 'undefined' != validRule.toLowerCase() && validRule.length>2 ){ 
					var json = eval('(' + validRule + ')');		
					if(!json.required){
						var jsonStr = validRule.substring(0, validRule.lastIndexOf('}'));
						jsonStr +=",'required':true}";    //加上必填
						me.attr("validate",jsonStr);    
					}				
				}else{
					me.attr("validate","{'required':true}");     //必填
					var value = $("textarea",div_me).val();
					if(value == ""){//值为空  添加必填样式
						//虽然CustomValid.js会验证表单必填并相应的添加必填样式，但我们的页面换了一种解析方式后，附件初始化出页面HTML前，就对表单进行验证了，导致即使有数据a标签的必填样式也没去掉
						//因为a标签是在表单验证完后才解析出来的
						me.addClass("validError validYz");
					}
				}
			});
		});
	},
	/**
	 * 开始处理子表权限
	 * 这里简单说一下当前我们权限整个过程
	 * 首先在展示表单时会读取表单中保存的template，然后利用freemaker来将其跟数据结合
	 * 主表字段在这次结合时会连同权限一起解析出来，而子表字段只会解析出数据并不会处理权限，而是交给页面的js来处理
	 * （ps：俺已经试过在freemaker也处理掉子表字段的权限，但确实繁琐和艰难，有意者可以自行尝试-。-）
	 * 
	 * permission: 参数拼装可以参考BpmFormHandlerService.java中的obtainHtml
	 */
	handleSubTable : function(permission){
		var subFieldJson =permission["subFieldJson"];//子表新版本字段权限
		var subTableShow =permission["subTableShow"];
		
		$('div[type=subtable]').each(function() {
			//子表
			var table = $(this);
			var tableName = table.attr('tableName').toLowerCase();
			table.attr('tableName',tableName);
			table.attr('right','w');//子表非隐藏状态，则需要给编辑或必填权限才能显示
			
			//是否 只读  编辑  隐藏   决定子表的权限
			for(var key in subTableShow){
				var tableShow=subTableShow[key];
				if(tableName!=key.toLowerCase()) continue;
				TablePermission.handleSubTableShow(table,tableShow);
			}
			
			//处理子表字段权限
			for(var key in subFieldJson){
				var json=subFieldJson[key];
				if(tableName!=key.toLowerCase()) continue;
				for(var k in json){
					TablePermission.handleSubTableField(table,k,json[k],key);
				}
			}
		});
	},
	/**
	 * 解析一个子表show权限
	 * @param tableShow
	 * @returns
	 */
	handleSubTableShow : function(table,show){
		
		if(show['y']== 'true'){//隐藏的话下面的操作就没必要了
			table.attr('show','false');
			table.attr('right','y');  //子表编辑功能
			table.remove();
			return;
		}else{
			table.attr('show','true');  //子表表单可见
		}
		
		if(show['b']== 'true'){//必填
			table.attr('right','b');  //子表编辑功能或者必填
			
			//添加必填样式前，先判断子表是否有数据
			rtn = CustomForm.isSubTableRequest();
			if(!rtn.success){//子表暂无数据
				//添加必填样式
				table.addClass('validError validYz');
			}
		}
		
		if(show['addRow']== 'false'){//不可新增
			$("a.link",table).remove();//去除所有a.link的按钮
			$("a.extend",table).each(function(){	// 导入导出按钮的特殊处理      
				var extend=$(this);
				if(typeof extend.attr("importexcel") != "undefined"
					||typeof extend.attr("exportexcel") != "undefined"){	
					extend.remove();                
				}
			});
		}
	},
	
	/**
	 * 解析一个子表字段的权限
	 */
	handleSubTableField :function(table,fieldName,right,tableName){
	    var name = "s:"+tableName+":"+fieldName;//字段在HTML的真实NAME
	    if(right==""){//没有权限
	    	table.find("[name='"+name+"']").each(function(){
	    		$(this).closest("td").html("");
	    	});
	    }
		if(right=="r"){//只读
			//处理超链接
			$("a.link",table).each(function(){      //只读时，历遍超链接
				var link=$(this);
				if(name==link.attr('name')||name==link.attr('field')){
					link.remove();                 //只读时，删除(可能会有多个一样的，要删除所以不能用return false break)
				}                            
			});
			
			//处理自定义按钮对话框的权限问题（这里只是处理字表上的）
			$("a.extend",table).each(function(){//只读时，历遍超链接中自定义对话框的按钮
				var extend=$(this);
				var permissionName = extend.attr('permissionName');
				if(permissionName){
					var filedName = "s:"+tableName+":"+permissionName;
					if(filedName==name){
						extend.remove(); //只读时，删除(可能会有多个一样的)
					}
					return true;
				}
				var jsonStr = extend.attr('eventbtn');							
				if(jsonStr != null && 'undefined' != jsonStr.toLowerCase() && jsonStr.length>2 ){
					var jsonObjArray = eval('(' + jsonStr + ')');
					for(var j =0,jsonObj;jsonObj=jsonObjArray[j++];){
						var fileds = jsonObj.fields;
						for ( var i = 0; i < fileds.length; i++) {
							var targetArr = fileds[i].target;
							for ( var k = 0,target;target=targetArr[k++];){
								var filed =target.split("-")[1];
								var filedName = "s:"+tableName+":"+filed;
								if(filedName==name){
									extend.remove();                 //只读时，删除(可能会有多个一样的)
								    break;
								}
							}
						}
					}
				}                            
			});
			
			
			//处理其它
			$("input:visible:not([ctltype=attachment]),textarea:visible,select:visible",table).each(function(){	
				var me=$(this);
				me.attr("right",right);
				if(!me.attr('name')||name.toLowerCase()!=me.attr('name').toLowerCase()) return; //即是 continue;
				var isSelect=me.is("select");
				var isTextarea=me.is("textarea");
				var isCheckboxOrRadio = false;   //单选或者复选
				var type = me.attr("type");  //标签种类
				if(type=="checkbox"||type=="radio"){
					isCheckboxOrRadio = true;
				}
				var val ='';
				var validRule = me.attr("validate");
				if ( validRule && 'undefined' != validRule.toLowerCase() ){
					var json = eval('(' + validRule + ')');
					if(json.isWebSign){
						var lablename = me.attr("lablename");
						if(isSelect){
							val = '<input name="'+name+'"  lablename="'+lablename+'" type="hidden" validate="{\'isWebSign\':true}" value="'+me.attr("val")+'" />';
						}else if(isTextarea){
							val = '<textarea name="'+name+'" lablename="'+lablename+'" class="hidden"  validate="{\'isWebSign\':true}" >'+me.val()+'</textarea>'
						}else{
							val = '<input name="'+name+'"  lablename="'+lablename+'"  type="hidden" validate="{\'isWebSign\':true}" value="'+me.val()+'" />';
						}
					}				
				}	
				if(isSelect){
					val += CustomForm.selectValue(me);
					me.before(val);
					me.remove();
				}else if(isCheckboxOrRadio){
					me.attr("disabled","disabled");
				}else{
					val += me.val();
					var refid=me.attr("refid");
					if(typeof(refid)!=undefined && refid!=null&&refid!=''){
						var refids=me.prev("input[name='"+refid+"']").val();
						var linkType=me.attr("linktype");
						var tempval="<div>";
						var nametemp = new Array();
						var str = me.val();
						
						if(typeof(str)!=undefined && str!=null&&str!=''){
							nametemp = str.split(",");
						}
						
						if(refids){
							var id=refids.split(",");											
							for(var i=0;i<id.length; i++){
								// 先去掉浮标背景
								//tempval+="<span class='backgrounddiv'><a refid='"+id[i]+"' href='#' linktype='"+linkType+"' style='text-decoration:none'>"+nametemp[i]+"</a> </span>";
								tempval+="<span>"+nametemp[i]+"</span>";
							}
						}else{
							for(var i=0;i<nametemp.length; i++){
								//tempval+="<span class='backgrounddiv'>"+nametemp[i]+"</span>";
								tempval+="<span>"+nametemp[i]+"</span>";
							}
						}
						tempval+="</div>";
						val = tempval;
					}
					//替换 /n 为<br/>
					if(val) val = val.replace(/\n/g,"<br/>"); 
					me.before(val);
					me.remove();
				}															
			});
			
	    }
		if(right=="b"){
			$("input:visible,textarea:visible,select:visible,a:visible",table).each(function(){
        		var me=$(this);
				var objName = me.attr("name");
				
				if(!objName||name.toLowerCase()!=objName.toLowerCase()) return;
				if(me.is("a.link")&&!me.is("selectFile")){//选择器按钮因为有前面的输入框不需要必填处理，文件上传按钮需要必填处理
					return;
				}
				
				me.attr("right",right);
				var val = $.fn.getValue(me);
				if(!val){
					me.addClass("validError validYz");
				}
				
				var validRule = me.attr("validate");
				if ( validRule){ 
					var json = eval('(' + validRule + ')');
					if(!json.required){
						json["required"]=true;
						me.attr("validate",JSON.stringify(json));    
					}
				}else{
					me.attr("validate","{\'required\':true}");     //必填
				}
			});
        }
	},
	handlePerTag : function(){
		$("[permission]").each(function(){
			var expr = $(this).attr("permission");
			try{
				var b = eval(expr);
				if(!b){
					$(this).remove();
				}
			}catch(e){
				
			}
		});
		//另外处理一下子表row
		$('div[type=subtable]').each(function() {
			var row=$($(this).data("row"));
			row.find("[permission]").each(function(){
				var expr = $(this).attr("permission");
				try{
					var b = eval(expr);
					if(!b){
						$(this).remove();
					}
				}catch(e){
					
				}
			});
			if(row[0]){
				$(this).data("row",row[0].outerHTML);
			}
		});
	}
}