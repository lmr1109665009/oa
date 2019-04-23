$(function() {
	var multiRowsCounter=0;
	CustomForm = {
		onEditCallBack:null,
		tables: {},
		init : function() {
			var self=this;

			//初始化子表数据及其相关操作的js
			self.initSubTable();
			self.initUI();

			self.initComdify();//初始化子表编辑和菜单
			self.validate();
            if(TablePermission){//如果引入了子表权限js，则开始解析
				setTimeout(function () {
                    TablePermission.init();
                },200);
            }
		},

		initSubTable : function(){
			$('div[type=subtable]').each(function() {
				var table = $(this);
				var tableName = table.attr('tableName').toLowerCase();
				table.attr('tableName',tableName);

				//没有form类型的属于表内编辑模式
				var row = table.find('[formType=form]');
				//表单
				var form;
				if(row.length > 0) {//窗口编辑模式
					form = table.find('[formType=window]');
				}else {//页内编辑模式
					row = table.find('[formType=edit]');
				}

				table.data('row', $('<div></div>').append(row.clone()).html());
				row.css('display', 'none').html('');
				//窗口编辑模式
				if(form) {
					//修复窗口模式附件展示
					$("div[name='div_attachment_container']",form).each(function(){
						var atta  =$("textarea[controltype='attachment']",$(this));
						var attaName =  atta.attr("name");
						if($.isEmpty(attaName)) return true;
						$("td[fieldname='"+attaName+"']",table).each(function() {
								AttachMent.insertHtml($(this),$(this).text());
						});
					});

					table.data('form', '<form>' + $('<div></div>').append(form.clone()).html() + '</form>');
					table.data("formProperty",{width:form.width() ,height: form.height()});
					form.remove();
				}
				CustomForm.tables[tableName] = table;

				var canWrite = !permission||permission.subTableShow[tableName].addRow!="false";
				//子表的权限(可以编辑或必填或只读)
				$(this).find('[formType="newrow"]').each(function() {
					if(!canWrite) return true;
					CustomForm.addBind($(this), table);
				});
				if(canWrite){
					//子表是否可以编辑
					var menu = $.ligerMenu({top : 100,left : 100,width : 130,
						items : [{
								text : '添加',
								click : function() {
									CustomForm.add($(menu.target).closest('div[type=subtable]'));
								}
							}]
					});
					table.find('[formType]:first').prev().bind("contextmenu", function(e) {
						menu.target = e.target;
						menu.show({top : e.pageY,left : e.pageX});
						return false;
					});
				}
				CustomForm.handButton(table,canWrite);
			});
		},

		//处理添加和删除按钮。
		handButton:function(table,canWrite){
			var self=this;
			if(!canWrite) {
				$(".add",table).addClass("disabled");
				$(".del",table).addClass("disabled");
				return;
			};

			$(".add",table).click(function(){
				self.add(table);
				self.initSubQuery();
				self.subSelectorInit();
			});

			table.delegate(".del", "click", function(){
				var t = $(this).closest('[formType]');

				$("input",t).each(function(){//设置被删除的数据的Input全为0
					var me = $(this);
					me.val("");
				});
				FormUtil.InitMathfunction(t);//触发其计算函数
				t.remove();//之后删除属性

			});
		},


		//处理块模式和表内编译模式下千分位
		initComdify:function(){
			$("div[type='custform']").delegate('[showtype]', 'blur',function (){
					CustomForm.delaComdify($(this));
				});
		},

		//处理弹出框模式下千分位
		initComdifyForWindow:function(){
			$("[formtype='window']").delegate('[showtype]', 'blur',function(){
					CustomForm.delaComdify($(this));
				});
		},

		//处理千分位
		delaComdify:function(varObj){
			var me=$(varObj);
			var value = me.val();
			var json=null;
			try{
				var jsonStr=me.attr("showtype");
				json=eval('('+jsonStr+')');
			}
			catch(err){}
			if(json!=null){
				var coinvalue=json.coinValue;
				var iscomdify=json.isShowComdify;
				var decimallen=json.decimalValue;
				//去除货币标签
				if (coinvalue && value.split(coinvalue) != -1) {
					var ary = value.split(coinvalue);
					value = ary.join("");
				}

				if (value.indexOf(',') != -1) {
					value = $.toNumber(value);
				}
				if(iscomdify && iscomdify=='1'){
					var value = $.comdify(value);
				}

					// 小数处理
				if (decimallen > 0 && value) {
					var zeroLen = '';
					if (value.indexOf('.') != -1) {
						var ary = value.split('.');
						var temp = ary[ary.length - 1];
						if (temp.length > 0 && temp.length < decimallen) {
							for (var i = 0; i < decimallen- temp.length; i++) {
								zeroLen = zeroLen + '0';
							}
							value = value + zeroLen;
						}
					} else {
						for (var i = 0; i < decimallen; i++) {
							zeroLen = zeroLen + '0';
						}
						value = value + '.' + zeroLen;
					}
				}
				//添加货币标签
				if (coinvalue && value) {
					value = coinvalue + value;
				}
			}
			me.val(value);
		},


		/**
		 * 初始化表单界面。
		 *
		 * @param parent
		 */
		initUI : function(parent) {
			$('input[type="checkbox"]').each(function() {
				var value=$(this).val();
				var data=$(this).attr('data');
				if(!data||data==''||data=='null')return;
				var ary=data.split(",");
				for(var i=0;i<ary.length;i++){
					if(value!=ary[i]) continue;
					$(this).attr("checked","checked");
				}
			});

			var filter='input,textarea,.dicComboTree,.dicComboBox,.dicCombo';
			if (parent==undefined) {
				parent = $('body div[type=custform]');
				if(!parent||parent.length==0)parent = $("body");
			}else{
				//处理初始化附件的bug
				AttachMent.init('w', $("div[name='div_attachment_container']",parent));
			}
			//下拉框默认选中,在下拉框定义一个val属性,使用脚本选中。
			$("select[name][val]",parent).each(function(){
				var obj=$(this),val= obj.attr("val");
				if($.isEmpty(val))
					val= obj.val();
				obj.val(val);
			});

			parent.find(filter).each(function() {
				if ($(this).is('.dicComboTree:not([name^="m:"]),.dicComboBox:not([name^="m:"]),.dicCombo:not([name^="m:"])')) {
					if($(this).closest('[type=append]').length<=0){//是模板的也不用初始化
						var rowCounter=parseInt(parent.attr("rowCounter"));
						if (!isNaN(rowCounter)){
							$(this).attr("rowCounter",rowCounter);
						}
						$(this).htDicCombo();
					}
				}
				//处理默认日期
				if ($(this).is('.Wdate[displayDate=1]')){
					var me = $(this);
					if($.isEmpty(me.val())){
						var datefmt = me.attr("datefmt");
						var nowDate=new Date().Format(datefmt);
						me.val(nowDate);
					}
				}
			});
			if (parent.is('form')) {

				parent.data('validate', this.getValidate(parent));
			} else {
				var v = parent.closest('form');
				this.valid = this.getValidate(v);
			}
			$('div[type=subtable]').each(function() {
				var subTable=$(this);
				CustomForm.handRow('edit',subTable);
			});
		},
		//添加一行数据
		//参数，子表区域，在那个地方插入。
		add : function(table, beforeElement) {
			var self=this;
			//判断是否是使用窗口方式编辑数据。
			var frm=table.data('form');
			if (frm) {
				self.openWin('添加', $(frm), table, beforeElement, function(form, table, beforeElement) {
					//校验数据
					var v = form.data('validate');
					if (!v.valid()) {
						return false;
					}
					var row = $(table.data('row'));

					//开始处理序号问题
					var rows=table.find(".listRow:visible");
					//编号
					row.find(".tdNo").text(rows.length+1);
					//添加行数据
					self.addRow(row, form);
					self._add(table, row, beforeElement);
					self.handRow('add',table);

					return true;
				});
				//弹窗模式下初始化千分位货币处理函数
				self.initComdifyForWindow();
			}
			//使用的行内编辑。
			else {
				var row = $(table.data('row'));
                //开始处理序号问题
                var rows=table.find(".listRow:visible");
                //编号
                row.find(".tdNo").text(rows.length+1);
				this._add(table, row, beforeElement);
			}
			self.handRow('add',table);
		},

		handRow:function(type,table){
			if(typeof(handRowEvent)=="function"){
				handRowEvent(type,table);
			}
		},
		//添加一行数据往行数据中添加隐藏域，同时设置显示的数据。
		addRow:function(row,form){
			form.find('input:text[name],input:hidden[name], textarea[name],select[name]').each(function() {
				var name=$(this).attr('name');
				var val=$(this).val();
				//添加隐藏表单。
				if($(this).is(".dicComboBox,.dicComboTree,.dicCombo")){//数据字典取值要特殊处理
					var tval = $(this).ligerComboBox("getValue");
					row.append($('<input type="hidden"/>').attr('name', name).val(tval));
				}else{
					row.append($('<input type="hidden"/>').attr('name', name).val(val));
				}

				//修改表格的文本显示。
				var filter="[fieldName='"+name+"']";
				var objTd=$(filter,row);
				if(objTd.length>0){
					var controltype = $(this).attr('controltype');
					if(!$.isEmpty(controltype) && controltype=='attachment'){
						AttachMent.insertHtml(objTd,val);
					}else if($(this).is("select")){
						var text = $(this).find("option:selected").text();
						objTd.text(text);
					}else{
						objTd.text(val);
					}
				}
				$(this).val('');
			});

			//回填checkbox的值。
			form.find('input:checkbox,input:radio').each(function() {
				var name=$(this).attr('name');
				var value=$(this).val();//key
				var text=$(this).parent().text();//text

				var filterHidden="input[name='"+ name +"']";
				var isChecked=($(this).attr("checked")!=undefined);
				var obj=$(filterHidden,row);

				var filter="[fieldName='"+name+"']";
				var objTd=$(filter,row);

				var val=(isChecked)?value:"";
				var txt=isChecked?text:"";
				if(obj.length==0){
					row.append($('<input type="hidden"/>').attr('name', name).val(val));
				}else{
					var existTxt = objTd.text();
					var existVal = obj.val();
					if(existVal!=""&&val){
						existVal+= ",";
						existTxt+=",";
					}
					obj.val(existVal+val);
					txt=existTxt+txt;
				}
				if(objTd.length>0){
					objTd.text(txt);
				}
			});
		},
		_add : function(table, newRow, beforeElement) {
			if (beforeElement) {
				$(beforeElement).before(newRow);
			}
			//在最后面加入
			else {
				table.find('[formType]:last').after(newRow);
			}
			multiRowsCounter++;
			newRow.attr("rowCounter",multiRowsCounter);
			//初始化界面
			this.initUI(newRow);
			//添加右键绑定
			this.addBind(newRow, table);
			FormUtil.triggerChoice(newRow);
			FormUtil.initEventBtn(newRow);
			//删除必填样式
			if(table.hasClass('validError')){
				table.removeClass('validError');
			}
			// me_zlklzgzAddRowEvent(); 你需要写如下方法
			var addRowAfterEvent = eval('window.'+table.attr("tablename")+"AddRowAfterEvent")
			if(addRowAfterEvent) addRowAfterEvent(newRow);
			//添加后置事件
			table.trigger("addRowAfterEvent",newRow);

		},

		/**
		 * 添加行数据的右键事件绑定。
		 * @param row
		 * @param table
		 */
		addBind: function(row, table) {
			//是否需要编辑菜单项目
			//如果该表弹出窗口定义，那么就不需要编辑菜单项目。
			var menu = this.getMenu(table);

			row.bind("contextmenu", function(e) {
				menu.target = e.target;
				menu.show({top : e.pageY,left : e.pageX});
				return false;
			});
		},
		/**
		 * 编辑行数据。
		 * @param table
		 * @param row
		 */
		edit : function(table, row) {
			var self=this;
			var tableName = table.attr('tableName');
			//获取表单
			var form = $(this.tables[tableName].data('form'));


			//对表单数据进行初始化
			self.initFormData(form,row);
			form.data('row', row);
			this.openWin('编辑', form, table, null, function(form, table) {
				var v = form.data('validate');
				var rtn=v.valid();
				if(!rtn) return false;

				var row = form.data('row');
				//对表单进行遍历
				self.setRowData(form,row);
				return true;

			});
		},
		initFormData:function(form,row){
			form.find('input:text,textarea,select,input[type="hidden"]').each(function() {
				var name= $(this).attr('name');
				var value = row.find('[name="' + name+ '"]').val();
				$(this).val(value);
			});
			form.find('select').each(function() {
				var name= $(this).attr('name');
				var value = row.find('[name="' + name+ '"]').val();
				$(this).attr("val",value);
			});
			form.find('input:checkbox,input:radio').each(function(){
				var name=$(this).attr("name");
				var chkValue=$(this).val();
				var value=row.find('[name="' + name+ '"]').val();

				if(value.indexOf(chkValue)!=-1){
					$(this).attr("checked","checked");
				}
			});
		},
		setRowData:function(form,row){
			var self=this;
			//对表单进行遍历
			form.find('input:text, textarea,input[type="hidden"],select').each(function() {
				var name=$(this).attr('name');
				var val=$(this).val();
				//修改隐藏域的数据值
				var objHidden=$("input[name='"+name+"']",row);

				objHidden.val(val);

				if($(this).hasClass("dicComboTree")){
					var dicNameId = name.replaceAll(":","")  + "_id";
					objHidden.val(form.find("input[name='"+dicNameId+"']").val());
				}

				//修改表格的文本显示。
				var filter="[fieldName='"+name+"']";
				var objTd=$(filter,row);
				if(objTd.length<=0) return true;
				//处理附件
				var controltype = $(this).attr('controltype');
				if(!$.isEmpty(controltype) && controltype=='attachment'){
					AttachMent.insertHtml(objTd,val);
				}else if($(this).is("select")){
					var text=$(this).find("option[value="+val+"]").text();
					objTd.text(text);
				}else{
					objTd.text(val);
				}
			});

			form.find('input:checkbox,input:radio').each(function(){
				var name=$(this).attr('name');
				var objHidden=$("input[name='"+name+"']",row);
				objHidden.val("");
				//修改表格的文本显示。
				var filter="[fieldName='"+name+"']";
				var objTd=$(filter,row);
				if(objTd.length>0){
					objTd.text("");
				}
			});

			form.find('input:checkbox:checked,input:radio:checked').each(function(){
				var name=$(this).attr('name');
				var value=$(this).val();
				var text = $(this).parent().text();
				var objHidden=$("input[name='"+name+"']",row);
				var filter="[fieldName='"+name+"']";
				var objTd=$(filter,row);

				var hidValue=objHidden.val();
				var existText=objTd.text();
				if(hidValue){
					objHidden.val(hidValue +"," +value);
					objTd.text(existText +"," +text);
				} else{
					objHidden.val(value);
					objTd.text(text);
				}
			});
		},

		/**
		 * 获取验证器。
		 * @param target 为一个表单。
		 * @returns
		 */
		getValidate : function(target) {
			return target.form({
				/**
				 * 错误消息处理
				 */
				errorPlacement : function(el, msg) {
					var element=$(el),corners =['right center','left center'],flipIt= element.parents('span.right').length > 0;
					element.addClass('validError validYz');

                    //添加必填样式
                    var parentTd = element.parent();
                    if (element.is("[type='radio'],[type='checkbox']")){
                        parentTd=element.closest("td");
                    }
                    if(parentTd){
                        parentTd.find("span.red").remove();
                        parentTd.append("<span class='red'>!</span>");
                        var span=parentTd.find("span.red");
                        if (span.prev().is("label")){
                            span.css({
                                "margin-left":"10px"
                            });
                        }
                    }

                    //原来的逻辑，在前面名称上加红色*
					/*var parentTd = element.closest("td");
					if(parentTd){
						var formTitle = parentTd.prev("td.formTitle");
						if(formTitle){
							var span = $("span.red", formTitle);
							if(!span || span.length==0){
								formTitle.append($("<span class='red'>*</span>"));
							}
						}
					}*/

					if(element.hasClass("myeditor")){
						setTimeout(function(){
							element = element.next();
							element.css("border","1px solid red");
						},1000);
					}else if(element.hasClass("Wdate")||element.is('textarea')){
						element.css("border","1px solid red");
					}else if(element.is("select")||element.attr('type')&&(element.attr('type')=='checkbox'||element.attr('type')=='radio')){
						var name = element.attr('name');
						if(!name)return;
						var priElement = $("*[name='"+name+"']",$("span.select_contain_span"));
						if(priElement.length>0)return;
						element.removeClass('validError');
						var errorSpan = $('<span></span>').css({}).addClass("select_contain_span");
						element.before(errorSpan);
						errorSpan.append(element);
					}

					if(!$(msg).is(':empty')){
						element.qtip({
							overwrite:false,
							content : msg,
							position:{
								my:corners[flipIt?0:1],
								at:corners[flipIt?1:0],
								viewport:$(window)
							},
					        show:{
			   			     	effect: function(offset) {
			   						$(this).slideDown(100);
			   					}
		   			        },
		   			        hide:{
		   			        	event:'click mouseleave',
		   			        	leave: false,
		   			        	fixed:true,
		   			        	delay:200
		   			        },
		   			        style:{
								classes:'ui-tooltip-red'
							}
						});
					}else{
						element.qtip("destroy");
					}
				},
				/**
				 * 成错误消息
				 */
				success : function(el) {
					var element=$(el);
					if(element.hasClass("myeditor")){
						element = element.next();
						element.css("border","");
					}else if(element.hasClass("Wdate")||element.is('textarea')){
						//element.css("border","1px solid #E8EEF0");
					}else if(element.is("select")||element.attr('type')&&(element.attr('type')=='checkbox'||element.attr('type')=='radio')){
						var selectSpan = element.parents("span.select_contain_span");
						if(!selectSpan||selectSpan.length==0){
							var name = element.attr('name');
							if(!name)return;
							var priElement = $("*[name='"+name+"']",$("span.select_contain_span"));
							if(!priElement||priElement.length==0)return;
							var tipSpan = priElement.parents("span.select_contain_span");
							var formtype = priElement.parents("[formtype]");
							if(formtype&&formtype.length>0){
								$("[name='"+name+"']",formtype).each(function(){
									$(this).removeClass('validError');
									$(this).qtip("destroy");
									$(this).unbind('mouseover');
								});
							}
							else{
								$("[name='"+name+"']").each(function(){
									$(this).removeClass('validError');
									$(this).qtip("destroy");
									$(this).unbind('mouseover');
								});
							}
							tipSpan.before(priElement);
							tipSpan.remove();
						}
						else{
							selectSpan.before(element);
							selectSpan.remove();
						}
					}else if(element.attr('type')=='subtable'){

					}
					element.removeClass('validError');
					element.qtip("destroy");
					element.unbind('mouseover');
				}
				,rules:com.hotent.form.rule.CustomRules
			});
		},
		validate:function(conf){
			return this.valid.valid(conf);
		},
		//处理有数据格式定义的数据。
		handNumberData:function(obj){
			var value=$(obj).val();

			if ($(obj).is(".dicComboTree")) {// 是数据字典，要进行特殊处理
				value = $(obj).parent().next().val();
				return value;// 拿到ligerui自动生成的id项然后返回
			}

			var showType=$(obj).attr("showtype");
			if(!showType) return value;
			try{
				showType=showType.replaceAll("'","\"");
				var json=jQuery.parseJSON(showType);
				var coinvalue = json.coinValue;
				var isShowComdify = json.isShowComdify;
				if (coinvalue != null && coinvalue != '' && value.split(coinvalue) != -1) {
					var ary = value.split(coinvalue);
					value = ary.join("");
				}
				if (isShowComdify && value.split(",") != -1) {
					var temp = value.split(",");
					value = temp.join("");
				}
			}
			catch(err){}
			return value;
		},
		/**
		 * 获取数据。
		 * 返回json数据。
		 */
		getData: function() {
			var self=this;
			// 主表数据
			var main = {
				fields:{}
			};
			//取主表的字段。
			$("input:text[name^='m:'],input[type='hidden'][name^='m:'],textarea[name^='m:'],select[name^='m:']").each(function() {
				var name = $(this).attr('name');
				var value=self.handNumberData(this);
				main.fields[name.replace(/.*:/, '')] = value;
			});

			$("textarea[name^='m:'].myeditor").each(function(num) {
				var name = $(this).attr('name');
				var data=$(this).val();
				main.fields[name.replace(/.*:/, '')] =data;
				$(this).val(data);
			});
			//设置单选按钮。
			self.setMainRadioData(main.fields);
			//设置复选框。
			self.setMainCheckBoxData(main.fields);

			//子表数据
			var sub = [];
			$('div[type=subtable][right=w || right=b][show=true||undefined]').each(function() {
				var table = {
					tableName: $(this).attr('tableName'),
					fields: []
				};
				$(this).find('[formtype]').not(":first").each(function() {	/*修复需要提交隐藏的行数据的bug*/
					var row = {};
					var objRow=$(this);
					$("input:text[name^='s:'],input[type='hidden'][name^='s:'],textarea[name^='s:'],select[name^='s:']",objRow).each(function() {
						var name = $(this).attr('name').replace(/.*:/, '');
						var value=self.handNumberData(this);
						row[name] = value;
					});
					//设置复选框按钮的数据。
					self.setSubCheckBoxData(objRow,row);

					//设置单选按钮的数据
					self.setSubRadioData(objRow, row);

					table.fields.push(row);
				});
				sub.push(table);

				JSON2.stringify(sub);
			});

			//意见
			var opinion = [];
			$('textarea[opinionname]').each(function() {
				opinion.push({
					name: $(this).attr('name'),
					value: $(this).val()
				});
			});

			var data = {main: main, sub: sub, opinion: opinion};
			return JSON2.stringify(data);
		},
		/**
		 * 设置子表的radio单选按钮字段。
		 * @param dataObj
		 */
		setSubRadioData:function(objParent,dataObj){
			var operatorObj = $('input:radio', objParent);
			this.setRadioData(dataObj, operatorObj);
		},
		/**
		 * 设置主表的radio单选按钮字段。
		 * @param dataObj
		 */
		setMainRadioData:function(dataObj){
			var operatorObj = $('input[name^=m]:radio');
			this.setRadioData(dataObj, operatorObj);
		},
		setRadioData:function(dataObj, operatorObj){
			$(operatorObj).each(function() {
				var name = $(this).attr('name').replace(/.*:/, '');
				var value= $(this).val();

				if($(this).attr("checked")!=undefined){
					dataObj[name]=value;
				}
			});
		},
		setCheckBoxData:function(dataObj, operatorObj, checkedObj){
			//将所有复选框选址清空。
			$(operatorObj).each(function() {
				var name = $(this).attr('name').replace(/.*:/, '');
				dataObj[name]="";
			});

			//复选框取值。
			$(checkedObj).each(function() {
				var name = $(this).attr('name').replace(/.*:/, '');
				var value= $(this).val();
				if(dataObj[name]==""){
					dataObj[name]=value;
				} else{
					dataObj[name]+="," + value;
				}
			});
		},
		/**
		 * 设置子表复选框的数据
		 * @param objParent
		 * @param dataObj
		 */
		setSubCheckBoxData:function(objParent,dataObj){
			var operatorObj = $('input:checkbox',objParent);
			var checkedObj = $('input:checkbox:checked',objParent);
			this.setCheckBoxData(dataObj, operatorObj, checkedObj);
		},
		setMainCheckBoxData:function(dataObj){
			var operatorObj = $('input[name^=m]:checkbox');
			var checkedObj = $('input[name^=m]:checkbox:checked');
			this.setCheckBoxData(dataObj, operatorObj, checkedObj);
		},

		/**
		 * 打开操作数据窗口。
		 * @param title 窗口标题
		 * @param form	窗口对象。
		 * @param table 表
		 * @param beforeElement
		 * @param callback 回调函数
		 */
		openWin : function(title, form, table, beforeElement, callback) {
			var formProperty=table.data("formProperty");
			var width=formProperty.width+20;
			var height=formProperty.height+100;
			var self=this;
			form.data('beforeElement', beforeElement);
			var win = $.ligerDialog({target:form,
					width:width,
					height:height,
					title : title,
					showMax : true,
					onClose : true,
					showButton : true,
					buttons : [ {
						text : "确定",
						onclick: function (item, dialog) {
							var result = callback(form, table, form.data('beforeElement'));
							if(!result) return;
						    if(self.onEditCallBack!=null){
								self.onEditCallBack(title);
							}
							dialog.close();
						}
					} ]
				});
			//初始化表单界面
			this.initUI(form);
			FormUtil.initEventBtn(form);//自定义按钮
			win.show();

		},
		/**
		 * 获取右键菜单。
		 * @param needEdit
		 * @returns
		 */
		getMenu : function(table) {

			var self=this;
			if(table.menu){
				return table.menu;
			}
			else{
				var needEdit = table.data('form') != null;
				var del=table.del;
				var menu;
				var items = [ {
					text : '在前面插入记录',
					click : function() {
						var row = $(menu.target).closest('[formType]');
						var table = row.closest('div[type=subtable]');
						self.add(table, row);
						//TODO
						self.subSelectorInit();
						self.initSubQuery();
						self.handRow('add',table);
					}
				}, {
					text : '在后面插入记录',
					click : function() {
						var row = $(menu.target).closest('[formType]');
						var table = row.closest('div[type=subtable]');
						row = row.next('[formType]:visible');
						if (row.length == 0) {
							row = null;
						}
						self.add(table, row);
						//TODO
						self.subSelectorInit();
						self.initSubQuery();
						self.handRow('add',table);
					}
				}, {
					line : true
				},{
					line : true
				}, {
					text : '编辑',
					alias:"edit",
					click : function() {
						var row = $(menu.target).closest('[formType]');
						var table = row.closest('div[type=subtable]');
						self.edit(table, row);
					}
				}, {
					text : '删除此记录',
					alias:"del",
					click : function() {
						var t = $(menu.target).closest('[formType]');
						var	table = t.closest('div[type=subtable]');

						//console.log(table,"ddjiaopgf[fp;ff")
						table.trigger("delRowBeforeEvent",t);

						/*删除前置事件   【如果返回了 false 则取消删除】*/
						var delRowBeforeEvent = eval('window.'+table.attr("tablename")+"DelRowBeforeEvent")
						if(delRowBeforeEvent) {
							if(delRowBeforeEvent(t) == false) return;
						}

						$("input",t).each(function(){//设置被删除的数据的Input全为0
							var me = $(this);
							me.val("");
						});
						FormUtil.InitMathfunction(t);//触发其计算函数
						t.remove();//之后删除属性

						self.handRow('del',table);//页面删除

						//删除数据后判断子表必填样式问题
						var b = !permission||permission.subTableShow[tableName].b!="false";
						if(b){//必填

							var blocktableLen = $("table.blocktable",table).length;
							var listRows = $('tr.listRow', table);
							if(listRows.length <= 1 && blocktableLen ==0 ){//子表中不存在数据 且子表不是快模式  为子表添加必填样式
								table.addClass('validError validYz');
							}

							if(blocktableLen != 0 || (blocktableLen==0 && listRows.length==0 )){// 子表为块模式情况
								var blocktable = $("table.blocktable",table);
								if($("tr",blocktable).length <1){
									table.addClass('validError validYz');
								}
							}
						}

					}
				}, {
					text : '向上移动',
					alias:"up",
					click : function() {
						var t = $(menu.target).closest('[formType]');
						var prev = t.prev('[formType]:visible');
						if (prev.length > 0) {
							prev.before(t);
						}
						var table = t.closest('div[type=subtable]');
						self.handRow('add',table);
					}
				}, {
					text : '向下移动',
					alias:"down",
					click : function() {
						var t = $(menu.target).closest('[formType]');
						var next = t.next('[formType]:visible');
						if (next.length > 0) {
							next.after(t);
						}
						var table = t.closest('div[type=subtable]');
						self.handRow('add',table);
					}
				} ];

				//如果不允许删除，删除“删除此记录”菜单
				var tableName = table.attr('tableName').toLowerCase();
				var allowDel = !permission||permission.subTableShow[tableName].del!="false";
				if(!allowDel){
					items.splice(4,1);
				}

				//如果不需要编辑，删除编辑菜单。
				if (!needEdit) {
					items.splice(3, 1);
				}
				menu = $.ligerMenu({top : 100,left : 100,width : 130,items : items});
				if (needEdit) {
					this.menuWithEdit = menu;
				}
				else{
					this.menu = menu;
				}
				return menu;
			}

		},

		/**
		 * 从数据库加载子表权限。
		 */
	  loadSubTablePermission:function(){
			var defId = $(':hidden[name=defId]').val();
			var actDefId = $(':hidden[name=actDefId]').val();
			var formKey = $(':hidden[name=formKey]').val();
			var nodeId = $(':hidden[name=nodeId]').val();
			var params={defId:defId,actDefId:actDefId,formKey:formKey,nodeId:nodeId};
			var url = "getSubTablePermission.ht";
			var map = null;
			$.ajaxSetup({async:false});  //修改成同步
			$.post(url, params,function(data){
				map = data;
	        });
			$.ajaxSetup({async:true}); //改为原来的异步
			return map;
	  },

	  selectValue:function(select){
		    var html='';
			var query=select.attr("selectquery")
			var queryJson;
			if(query!=undefined && query!=null ){
				query=query.replaceAll("'","\"");
				queryJson = JSON2.parse(query);
			}else{
				queryJson = "";
				query = "";
			}
			var sValue=select.attr("val");    //select中有一个属性是保存选中的值的（初始化时用这个）
			if(sValue==undefined || sValue==null && sValue.length<=0){
				sValue = "";
			}
			if(query){
				html="<span selectvalue="+sValue+" selectquery="+query+"><lable></lable></span>"
			}else{
				if(sValue==""){
					html=select.find("option:selected").text();    //获取Select选择的Text   选中（不可编辑时或者没有值时，是默认的）
				}else{
					html += select.find("option[value='"+sValue+"']").text();    //和值相对应的显示
				}
			}
			return html;
	  },

	   //初始化子表级联
	  initSubQuery:function(){

	  	$("select[selectquery]",$("[type=subtable],[formtype='window']")).each(function(){
		  	var me = $(this);
		  	var selected=me.attr("selectqueryed");
		  	//selectqueryed标识这个子表是否已经执行过了change绑定，存在就是已经绑定了，不存在就是没用被绑定过
		  	if(selected)return true;
			var selectquery = me.attr("selectquery");
			if (!selectquery)
				return true;
			selectquery = selectquery.replaceAll("'", "\"");
			var queryJson = JSON2.parse(selectquery);
			var query = queryJson.query;
			for (var i = 0; i < query.length; i++) {
				var field;
				// isMain是true 就是绑定主表的字段
				if (query[i].isMain=="true") {
					field = $(".formTable [name$=':" + query[i].trigger + "']");
				} else {
					field = me.parents('table.listTable,[formtype="window"]').find("[name='" + query[i].trigger + "']");
					//添加新一个子表时，执行了多次绑定change事件，把之前的change事件都解绑
					field.unbind("change");
				}
				if (field != "")
					QueryUI.change(field, me, queryJson);
			}
			//添加一个属性，标识已经被绑定过，
			me.attr("selectqueryed","selectqueryed");
			QueryUI.getvalue(me, queryJson);
	  	});
	  },
	  //初始化子表选择器
		 subSelectorInit:function(){
	  		$('[ctlType="selector"]','.listTable,[formtype="window"]').each(function(){
	  			var me =$(this);
	  			var nameID=me.attr('name')+'ID';
	  			var isHidden=me.siblings("input[name='"+nameID+"']");
	  			//如果存在隐藏域，则是已经添加了
	  			if(isHidden.length!=0) return;
					var type = me.attr('class');
					//方法在SelectotInit.js
					buildSelector(me, type);
				});
		},
		// 验证子表是否必填（至少要有一条记录）
		isSubTableRequest:function(){
			var result = {success : true}
			$('div[right="b"][type="subtable"]').each(function(){
				var subtable = $(this);

				var listRows = $('tr.listRow', subtable);//其他模式
				if(listRows.length > 1) return true;


				var blocktable = $('div[formtype="edit"]', subtable);//块模式验证子表必填
				if(blocktable.length >1) return true;

				var blocktable = $('div[formtype="newrow"]', subtable);//块模式验证子表必填
				if(blocktable.length >=1) return true;

				var tableDesc = subtable.attr('tabledesc');
				result.success = false;
				result.errorMsg = tableDesc;
				return false;
			});
			return result;
		}
	};
	//对表单初始化。
	CustomForm.init();
});