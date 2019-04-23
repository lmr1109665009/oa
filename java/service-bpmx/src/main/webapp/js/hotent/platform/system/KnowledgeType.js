/**
 * 分类管理
 * @param catKey
 * @param divId
 * @param conf
 * @returns {GlobalType}
 */
KnowledgeType=function(catKey,divId,conf){
	{
		this.glTypeTree=null;
		this.currentNode=null;
		this.conf=conf;
		this.catKey=catKey;
		this.divId=divId;
		var _self=this
	};
	
	this.loadGlobalTree=function(){
		var setting = {
			data: {
				key : { name: "typeName"},
				simpleData: {enable: true,idKey: "typeId",pIdKey: "parentId"}
			},
			
			callback:{onClick: this.clickHandler,onRightClick: this.rightClickHandler}
		};
		var url=__ctx + "/platform/system/globalType/getByCatKey.ht";
		if(conf.url){
			url=conf.url;
		}
    	
        var params={catKey:this.catKey};
        $.post(url,params,function(result){
        	knowPerData = result;
        	var typeList = result.userTypeList
        	for(var i=0;i<typeList.length;i++){
                var node=typeList[i];
                if(node.parentId==0){
                    node.icon=__ctx +"/styles/default/images/icon/root.png";
                    node.isRoot=1;
                }
            }
            
            _self.glTypeTree=$.fn.zTree.init($("#" + _self.divId), setting,typeList);
            
            var depth = _self.conf.expandByDepth;
            if(depth!=null && depth>=0)
            {
                var nodes = _self.glTypeTree.getNodesByFilter(function(node){
                    return (node.level==depth);
                });
                if(nodes.length>0){
                    for(var idx=0;idx<nodes.length;idx++){
                        _self.glTypeTree.expandNode(nodes[idx],false,false);
                    }
                }
            }
            else
            {
                _self.glTypeTree.expandAll(true);
            }
            // 设置左边树的高度
           	var toolbarHeight=$(".tree-toolbar").height();
        	var allHeight=$("#defLayout").height();
        	var treeHeight=(parseInt(allHeight)-parseInt(toolbarHeight)*3)+"px";
        	$("#glTypeTree").css({"height":treeHeight});
        });
        
	};
	
	this.rightClickHandler=function(event, treeId, treeNode){
		if( _self.conf.onRightClick){
			_self.conf.onRightClick(event, treeId, treeNode);
		}
	};
	//点击事件处理
	this.clickHandler=function(event, treeId, treeNode){
		_self.currentNode=treeNode;
		if(_self.conf.onClick){
			_self.conf.onClick(treeNode);
		}
	};
	
	//展开收起
	this.treeExpandAll=function(type){
		_self.glTypeTree.expandAll(type);
	};
	
	this.delNode=function (){
		var typeId=_self.currentNode.typeId;;
		if(typeId!=0){
			var params={typeId:typeId};
			var url=__ctx +'/platform/system/globalType/del.ht';
			$.post(url,params,function(data){
    			_self.loadGlobalTree();
    			var result = $.parseJSON(data);
    			if(result.result == 1){
    				$.ligerDialog.confirm("成功删除,是否删除该分类下的文章？",function (r){
    					if(r){
    						var delKnowUrl = __ctx +'/platform/system/sysKnowledge/delByType.ht';
    						$.post(delKnowUrl,params,function(rtn){
    							$("#defFrame").attr("src","");
    						});
    					}
    				});
    			}
    			else{
    				$.ligerDialog.warn(result.message)
    			}
    		});
		}
	};
	
	this.openGlobalTypeDlg=function(isAdd,isPrivate){
		var typeId=_self.currentNode.typeId;
		var isRoot=_self.currentNode.isRoot;
		var url=__ctx +'/platform/system/globalType/dialog.ht';
		if(isAdd){
			if(isRoot){
				url+="?parentId="+typeId +"&isRoot=1";
			}else{
				url+="?parentId="+typeId +"&isRoot=0";
			}
		}else{
			url+="?typeId="+typeId;
		}
		if(isPrivate){
			url+="&isPrivate=1";
		}
	 	var winArgs="dialogWidth:500px;dialogHeight:250px";
	 	url=url.getNewUrl();
	 	/*var rtn=window.showModalDialog(url,"",winArgs);
	 	//重新加载树。
	 	_self.loadGlobalTree();*/
	 	/*KILLDIALOG*/
	 	DialogUtil.open({
	 		height:300,
	 		width: 500,
	 		title : '增加',
	 		url: url, 
	 		isResize: true,
	 		//自定义参数
	 		sucCall:function(rtn){
	 			_self.loadGlobalTree();
	 		}
	 	});
	};
	this.sortNode=function(){
		var typeId=_self.currentNode.typeId;
		var url=__ctx +'/platform/system/globalType/sortList.ht?parentId='+typeId;
		var winArgs="dialogWidth:600px;dialogHeight:300px";
	 	url=url.getNewUrl();
	 	/*var rtn=window.showModalDialog(url,"",winArgs);
	 	//重新加载树。
	 	_self.loadGlobalTree();*/
	 	
	 	/*KILLDIALOG*/
	 	DialogUtil.open({
	 		height:400,
	 		width: 600,
	 		title : '节点排序',
	 		url: url, 
	 		isResize: true,
	 		//自定义参数
	 		sucCall:function(rtn){
	 			_self.loadGlobalTree();
	 		}
	 	});
	}
}