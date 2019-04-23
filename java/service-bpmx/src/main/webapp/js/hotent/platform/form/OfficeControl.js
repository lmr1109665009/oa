/**
 * office控件。 使用方法： var obj=new OfficeControl();
 * obj.renderTo("divContainer",{fileId:123}); divContainer： 文档容器id
 * fileId：附件id，如果指定那么根据该文件id加载word文档。
 * 
 * saveRemote:保存文档到服务器
 * 
 * @returns {OfficeControl}
 */
OfficeControl = function() {
	{
		var Sys = {};
		var ua = navigator.userAgent.toLowerCase();
		var s;
		(s = ua.match(/(msie\s|trident.*rv:)([\w.]+)/)) ? Sys.ie = s[1] : (s = ua
				.match(/(firefox)\/([\w.]+)/)) ? Sys.firefox = s[1] : (s = ua
				.match(/(chrome)\/([\w.]+)/)) ? Sys.chrome = s[1] : (s = ua
				.match(/(opera).+version\/([\w.]+)/)) ? Sys.opera = s[1] : (s = ua
				.match(/version\/([\w.]+).*(safari)/)) ? Sys.safari = s[1] : 0;
		this.isNewCleatDocByLyg = false;
		this.controlId = "";
		this.controlObj = null;
        this.height = ($(window).height()-40)+"px";
		this.width = "100%";
		var _self = this;
		this.isFileOpen = false;
		this.templatetype = 1;// 模板类型
		// 这里发布给客户时，可以修改ProductCaption，ProductKey值。
		this.params = {
			Caption : "欢迎使用",
			MakerCaption : "象翌微链科技发展有限公司",
			MakerKey : "E119EB1C2C64CBD3E9C611AE3008F0A5F505F7CE",
			ProductCaption : "象翌微链科技发展有限公司",
			ProductKey : "CAF45014E0D0978FB1FD8EB4CB9C4EC6874AABF8",
			TitlebarColor : "14402205",
			IsCheckEkey : "0",
			IsUseUTF8URL : "-1",
			IsUseUTF8Data : "-1",
			BorderStyle : "1",
			BorderColor : "14402205",
			TitlebarTextColor : "0",
			MenubarColor : "14402205",
			MenuButtonColor : "16180947",
			MenuBarStyle : "3",
			MenuButtonStyle : "1"
			//要注释掉，否则导致Chrome浏览器崩溃
			// isoptforopenspeed : "1"
		};
		this.config = {
			doctype : 'doc',
			fileId : "",
			controlId : "officeObj",
			myNum : '0',
			right : 'w',
			user : {},
			menuRight : ''
		};
	}
	;
	
	/**
	 * 获取文档类型。
	 */
	this.getDocType = function() {
		var docType = "Word.Document";
		var type = this.config.doctype;
		switch (type) {
		case "doc":
			docType = "Word.Document";
			break;
		case "xls":
			docType = "Excel.Sheet";
			break;
		case "ppt":
			docType = "PowerPoint.Show";
			break;
		}
		return docType;
	},
			/**
			 * 手写签名
			 */
			this.insertHandSign = function() {
				try {
					//_self.controlObj.FullScreenMode = true;
//					_self.IsFastSaveHandSign2=!0
					_self.controlObj.DoHandSign2(_self.config.user.name,// 手写签名用户名称
					"ntko",// signkey,DoCheckSign(检查印章函数)需要的验证密钥。
					0,// left
					0,// top
					0,// relative,设定签名位置的参照对象.0：表示按照屏幕位置插入，此时，Left,Top属性不起作用。1：光标位置；2：页边距；3：页面距离
						// 4：默认设置栏，段落（为兼容以前版本默认方式）
					100);
				} catch (err) {
					// $.ligerDialog.error("insertHandSign:" +err.name + ": " +
					// err.message);
					alert("insertHandSign:" + err.name + ": " + err.message);
				}
			},

			/**
			 * 可以设置文件用户
			 */
			this.setDocUser = function() {
				with (_self.controlObj.ActiveDocument.Application) {
					UserName = _self.config.user.name;// 设置用户信息
					UserInitials = _self.config.user.name;// 设置用户信息缩写
				}
			},
			/**
			 * 获取模版
			 */
			this.getTemplate = function(callback) {
				var url = __ctx
						+ "/platform/system/sysOfficeTemplate/dialog.ht?type="
						+ this.templatetype;
				var winArgs = "dialogWidth=600px;dialogHeight=400px;help=0;status=0;scroll=1;center=1";
				url = url.getNewUrl();
				OfficePlugin.hiddenObj(true);
				/*
				 * var rtn=window.showModalDialog(url,"",winArgs); return rtn;
				 */
				/* KILLDIALOG */
				DialogUtil.open({
					allowClose:false,
					height : 600,
					width : 700,
					title : '获取模版',
					url : url,
					isResize : false,
                    showMax: false,
                    showMin: false,
					sucCall : callback
				});
			},
			/**
			 * 模版套红。
			 */
			this.insertContentTemplate = function() {
				try {
					this.templatetype = 2;
					_self.getTemplate(function(rtn) {
						OfficePlugin.hiddenObj(false);
						this.templatetype = 1;
						if (rtn == undefined || rtn == null || rtn=="") {
							return false;
						}
						var templateUrl = __ctx
								+ "/platform/system/sysOfficeTemplate/getTemplateById.ht?templateId="
								+ rtn.templateId;

						// 选择对象当前文档的所有内容
						var curSel = _self.controlObj.ActiveDocument.Application.Selection;
						curSel.WholeStory();
						curSel.Cut();

						if (!(Sys.firefox) && !(Sys.chrome)) { // IE是同步的，它
																// 会等待模版加载成功后执行书签的插入
																// （火狐和谷歌就不可以）
							// 插入模板
							_self.controlObj
									.AddTemplateFromURL(templateUrl); // AddTemplateFromURL
							var BookMarkName = "content";
							if (!_self.controlObj.ActiveDocument.BookMarks
									.Exists(BookMarkName)) {
								alert("Word 模板中不存在名称为：\""
										+ BookMarkName + "\"的书签！");
								return false;
							}
							var bkmkObj = _self.controlObj.ActiveDocument
									.BookMarks(BookMarkName);
							var saverange = bkmkObj.Range;
							saverange.Paste();
							_self.controlObj.ActiveDocument.Bookmarks
									.Add(BookMarkName, saverange);
						} else {
							// 插入模板(火狐谷歌 异步)
							_self.controlObj
									.AddTemplateFromURL(templateUrl); // AddTemplateFromURL
						}
					});
				} catch (err) {
					// $.ligerDialog.error("insertTemplate:" +err.name + ": " +
					// err.message);
					alert("insertTemplate:" + err.name + ": " + err.message);
				}
			},
			/**
			 * 插入word模版。
			 */
			this.insertTemplate = function() {
				this.templatetype = 1;
				try {
					_self
							.getTemplate(function(rtn) {
								OfficePlugin.hiddenObj(false);
								if (rtn == undefined || rtn == null || rtn=="") {
									return;
								}
								var headFileURL = __ctx
										+ "/platform/system/sysOfficeTemplate/getTemplateById.ht?templateId="
										+ rtn.templateId;
								// _self.controlObj.ActiveDocument.Application.Selection.HomeKey(6,0);//光标移动到文档开头
								_self.controlObj.OpenFromURL(headFileURL);// 在光标位置插入红头文档
							});
				} catch (err) {
					// $.ligerDialog.error("insertTemplate:" +err.name + ": " +
					// err.message);
					alert("insertTemplate:" + err.name + ": " + err.message);
				}
			},

			/**
			 * 获取控件的html。
			 */
			this.getControlHtml = function(controlId) {
				var version="5,0,4,0";
				var classid="A64E3073-2016-4baf-A89D-FFE1FAA10EC2";
				var cabPath = __ctx
						+ "/media/office/OfficeControl.cab#version="+version;
				var str = '';
				if (Sys.ie) {
					if(window.navigator.platform=="Win64"){
						classid="916EE952-83C7-485f-8469-69D975889ED2";
						version="4,0,0,8";
                        cabPath = __ctx + "/media/office/OfficeControl64.cab#version="+version;
					}
					str = '<object  id="' + controlId + '" codeBase="'
							+ cabPath + '" height="' + this.height
							+ '" width="' + this.width + '" classid="clsid:'
							+ classid + '" style="z-index:-1;">';
					for ( var key in this.params) {
						str += '  <param name="' + key + '" value="'
								+ this.params[key] + '">  ';
					}
					str += "</object>";
				} else if (Sys.firefox || Sys.chrome) {
					if (this.config.doctype == 'pdf') {
						str = '<object id="'
								+ controlId
								+ '" codeBase="'
								+ cabPath
								+ '" height="'
								+ this.height
								+ '" width="'
								+ this.width
								+ '"  type="application/ntko-plug" ForOnSaveToURL="saveMethodOnComplete" ForOndocumentopened="documentOpenedOnComplete'
								+ this.config.myNum
								+ '" ForOnAddTemplateFromURL="addTemplateOnComplete'
								+ this.config.myNum + '" ';
					} else {
						str = '<object id="'
								+ controlId
								+ '" codeBase="'
								+ cabPath
								+ '" height="'
								+ this.height
								+ '" width="'
								+ this.width
								+ '"  type="application/ntko-plug" ForOnSaveToURL="saveMethodOnComplete" ForOnSaveAsOtherFormatToURL="saveMethodOnComplete" ForOndocumentopened="documentOpenedOnComplete'
								+ this.config.myNum
								+ '" ForOnAddTemplateFromURL="addTemplateOnComplete'
								+ this.config.myNum + '" '
								+' ForOnCustomButtonOnMenuCmd="customButtonOnMenuCmd" '
								+' ForOnCustomMenuCmd2="customMenuCmd" ';
					}
					for ( var key in this.params) {
						str += '_' + key + '="' + this.params[key] + '"	';
					}
					str += 'clsid="{' + classid + '}" >';
					str += '</object>  ';
				}

				// 是火狐和谷歌时 增加插入套红模版和只读设置的回调函数
				if (Sys.firefox || Sys.chrome) {
					str += ' <script type="text/javascript" > ';
					str += '		function addTemplateOnComplete'
							+ this.config.myNum + '(){ ';
					str += '			addTemplateOnComplete("' + this.config.myNum
							+ '"); ';
					str += '		} ';
					str += '	                 ';
					str += '		function documentOpenedOnComplete'
							+ this.config.myNum + '(){ ';
					str += '			documentOpenedOnComplete("' + this.config.myNum
							+ '"); ';
					str += '		} ';
					str += ' </script> ';
				}
				return str;
			},

			/**
			 * 将控件添加到div容器中。 第一个参数： div的容器ID 第二个参数: conf:
			 * doctype:文挡类型：可以为doc，xls，ppt fileId:服务器保存的文件ID
			 */
			this.renderTo = function(divContainerId, conf) {
				this.config = $.extend({}, this.config, conf);
				// 格式为空时默认设置为doc文件
				if (!this.config.doctype) {
					this.config.doctype = "doc";
				}
				if (!(Sys.firefox) && !(Sys.chrome)) {
					Sys.ie = true;
				}
				this.controlId = "office_div";
				var html = this.getControlHtml(this.controlId);
		
				$("#" + divContainerId).html("");
				
				$("#" + divContainerId).append(html);

				var obj = document.getElementById(this.controlId);

				this.controlObj = obj;
				this.controlObj.MenuBar = false;
				this.controlObj.Titlebar = false;
				this.controlObj.IsShowToolMenu = false;
				if(this.config.right!='w'){
                    this.controlObj.ToolBars = false;
                }else {
                    this.controlObj.ToolBars = true;
				}
				this.controlObj.FileProperties = true;
				this.controlObj.CustomToolBar = false;
                //左上角新建文档
				this.controlObj.FileNew = false;
                //左上角打开文档
				this.controlObj.FileOpen = true;
				this.controlObj.FileClose = false;
				this.controlObj.FileSave = true;
				//左上角另存为
				this.controlObj.FileSaveAs = true;
				var jqControlObj = $(this.controlObj);
				var docType = this.config.doctype;
				var menuRight = this.config.menuRight;

				if (Sys.ie || Sys.firefox || Sys.chrome) {
					/*if(window.location.pathname.indexOf("office.ht")>-1){
						return false;
					}*/
					if (docType == "pdf") {
						this.addPDFSupport();
						this.isFileOpen = true;
					} else {
						this.initDoc();
					}
				} else {
					// 32位版的浏览器!');
					OfficePlugin.hiddenObj(true);
                    $.ligerDialog.warn('office控件只支持IE、Firefox和Chrome 32位版的浏览器!',function () {
                        OfficePlugin.hiddenObj(false);
                    });
				}

			};

	/**
	 * 控件载入时，载入文档。
	 */
	this.initDoc = function() {
		// 指定了文件。
		if (this.config.fileId != "" && this.config.fileId > 0) {
            var path = __ctx + "/platform/system/sysFile/getFileById.ht?fileId=";
			if(typeof this.config.downloadPath!='undefined'&&this.config.downloadPath!=''){
                path=this.config.downloadPath;
			}
			path+= this.config.fileId;
			try {
				if(this.config.doctype=="doc"||this.config.doctype=="xls"||this.config.doctype=="ppt"){
                    //解决格式跟文件指定格式不一致的问题
                    var docType = this.getDocType();
                    this.controlObj.OpenFromURL(path, null, docType);
				}else {
                    this.controlObj.OpenFromURL(path);
				}
				this.isFileOpen = true;
			} catch (err) {
				try {
					this.addPDFSupport();
					this.isFileOpen = true;
				} catch (err) {
					this.newDoc();
				}
			}
		}
		// 新建文档。
		else {
			this.newDoc();
		}

		// IE浏览器是同步的 增加 设置文档是否只读，其它的为异步（由回调接管函数 OfficeControl.js 中有
		// documentOpenedOnComplete 处理 ）
		if (Sys.ie) {
			this.setFileReadOnly(false);
		}
	};
	/**
	 * 关闭文档。
	 */
	this.closeDoc = function() {
		if (Sys.ie || Sys.firefox || Sys.chrome) { // $.browser.msie
			this.initDoc();
		} else {
			// $.ligerDialog.warn('office控件只支持IE、Firefox和Chrome 32位版的浏览器!');
			alert('office控件只支持IE、Firefox和Chrome 32位版的浏览器!');
		}
		try {
			this.controlObj.close();
			this.isFileOpen = false;
		} catch (err) {
			// $.ligerDialog.error('closeDoc:' +err.name + ": " + err.message);
			alert('closeDoc:' + err.name + ": " + err.message);
		}
	};

	/**
	 * 新建文档。
	 */
	this.newDoc = function() {
		try {
			var docType = this.getDocType();
			this.controlObj.CreateNew(docType);
			this.isFileOpen = true;
		} catch (err) {
			try {
				this.controlObj.CreateNew("WPS.Document");
				this.isFileOpen = true;
			} catch (err) {
				// $.ligerDialog.error("newDoc:" +err.name + ": " +
				// err.message);
				alert("newDoc:" + err.name + ": " + err.message);
			}
		}
	};

	/**
	 * 保存文件到服务器。 服务器返回文件id到this.config.fileId，同时返回文件ID。
	 */
	this.saveRemote = function(inputObjNum,officeName) {
		var path = __ctx + "/platform/system/sysFile/saveFile.ht";
		var uploadName = this.controlId + "_name";
		var fileType = OfficePlugin.fileObjs.get(inputObjNum).getAttribute(
				"fileType");
		if (typeof fileType == "undefind") {
			fileType = "";
		}
		var params = "fileId=" + this.config.fileId + "&uploadName="
				+ uploadName + "&fileType=" + fileType;
		try {
			// 保存数据到服务器。
			var curDate = new Date();
			var docName = officeName;
			/*
			 * 如果人后辍名为空时,需要用对象的类型来区分是什么文件返回当前控件中的文档类型, 只读 0: 没有文档； 100 =其他文档类型；
			 * 1=word；2=Excel.Sheet或者 Excel.Chart ； 3=PowerPoint.Show； 4=
			 * Visio.Drawing； 5=MSProject.Project； 6= WPS Doc； 7:Kingsoft Sheet；
			 * 51 = PDF文档
			 */
			if (this.config.doctype == '' || this.config.doctype == null
					|| 'undefined' == typeof (this.config.doctype)) {
				var type = this.controlObj.DocType;
				if (type == 2) {
					this.config.doctype = "xls";
				} else if (type == 3) {
					this.config.doctype = "ppt";
				} else if (type == 51) {
					this.config.doctype = "pdf";
				} else {
					this.config.doctype = "doc";
				}
			}
			var result;
			if (Sys.firefox || Sys.chrome) { // 火狐谷歌浏览器控件文档保存事件（异步的）
				if (typeof (inputObjNum) != undefined && inputObjNum != null) {
					params += "&inputObjNum=" + inputObjNum; // 用于保存返回的值对象的名称
																// （异步的才会有）
				}
				// 当你用SaveToURL方法时，回调属性用：ForOnSaveToURL
				// 如果是SaveAsOtherFormatToURL的话，就用ForOnSaveAsOtherFormatToURL回调
				if (this.config.doctype == 'pdf') {
					// 直接保存文档，不用转换成指定是什么格式的文件方法
					this.controlObj.SaveToURL(path, uploadName, params, docName
							+ "." + this.config.doctype, 0);
				} else if (this.config.doctype == 'doc'){
					// 保存文档时要转换成指定兼容文档的的格式方法
					this.controlObj.SaveAsOtherFormatToURL(5, path, uploadName,
							params, docName + "." + this.config.doctype, 0);
				}else {
                    this.controlObj.SaveToURL(path, uploadName, params, docName
                        + "." + this.config.doctype, 0);
				}
				result = -11;
			} else { // IE是同步的
				// SaveToURL
				if (this.config.doctype == 'pdf') {
					// 直接保存文档，不用转换成指定是什么格式的文件方法
					result = this.controlObj.SaveToURL(path, uploadName,
							params, docName + "." + this.config.doctype, 0);
				} else if(this.config.doctype == 'doc') {
					// 保存文档时要转换成指定兼容文档的的格式方法
					result = this.controlObj.SaveAsOtherFormatToURL(5, path,
							uploadName, params, docName + "."
									+ this.config.doctype, 0);
				}else {
                    result = this.controlObj.SaveToURL(path, uploadName,
                        params, docName + "." + this.config.doctype, 0);
				}
				this.config.fileId = result;
			}
			return result;
		} catch (err) {
			// alert("saveRemote:" +err.name + ": " + err.message);
			if (Sys.firefox || Sys.chrome) {
				return -13; // 报错时表示火狐谷歌下OFFICE不正常
			}
			return -12;
		}
	};

	/**
	 * 对文档进行签单
	 */
	this.signature = function() {
		OfficePlugin.hiddenObj(true);
		var url = __ctx + "/platform/system/seal/dialog.ht";
		var winArgs = "dialogWidth=800px;dialogHeight=600px;help=0;status=0;scroll=1;center=0;resizable=1;";
		url = url.getNewUrl();
		/*
		 * var retVal = window.showModalDialog(url, "", winArgs);
		 * if(typeof(retVal)==undefined||retVal==null){ return false; }
		 * if(retVal.fileId.isEmpty()){ return false; } var sealUrl=__ctx +
		 * "/platform/system/sysFile/getFileById.ht?fileId=" + retVal.fileId;
		 * try{ // this.controlObj.AddSignFromURL(retVal.userName,sealUrl);
		 * this.controlObj.AddSecSignFromURL(_self.config.user.name,//签章的用户名
		 * sealUrl,//印章所在服务器相对url 0,//left 0,//top 1,//relative 2, //print mode
		 * 2 false,//是是否使用证书，true或者false， false //是否锁定印章 ); }catch(err){
		 * alert("signature:" +err.name + ": " + err.message); return -1; }
		 */

		var that = this;
		/* KILLDIALOG */
		DialogUtil.open({
			height : 630,
			width : 650,
			title : '对文档进行签单',
			url : url,
			allowClose:false,
			isResize : true,
            showMax: false,
            showMin: false,
			// 自定义参数
			sucCall : function(retVal) {
				OfficePlugin.hiddenObj(false);
				if (typeof (retVal) == undefined || retVal == null || retVal=="") {
					return false;
				}
				if (retVal.fileId.isEmpty()) {
					return false;
				}
				var sealUrl = __ctx
						+ "/platform/system/sysFile/getFileById.ht?fileId="
						+ retVal.fileId;
				try {
					that.controlObj.AddSecSignFromURL(_self.config.user.name,// 签章的用户名
					sealUrl,// 印章所在服务器相对url
					0,// left
					0,// top
					1,// relative
					2, // print mode 2
					false,// 是是否使用证书，true或者false，
					false // 是否锁定印章
					);
				} catch (err) {
					alert("signature:" + err.name + ": " + err.message);
					return -1;
				}
			}
		});

	};

	/**
	 * 对Office文档进行Ekey硬件签章
	 */
	this.signatureFromEkey = function() {
		if (this.controlObj != null) {
			/*
			 * if(!this.controlObj.IsEkeyConnected) //暂时不确定 {
			 * alert("没有检测到EKEY,请将EKEY插入到计算机!然后点击确定继续."); return; }
			 */
			this.controlObj.AddSecSignFromEKEY(_self.config.user.name, // username
			0, // left
			0, // top,
			1, // relative,
			2, // PrintMode,
			false, // IsUseCertificate,
			false, // IsLocked,
			true, // IsCheckDocChange,
			false, // IsShowUI
			true, // signpass,
			-1, // ekeySignIndex,
			true, // IsAddComment,
			true // IsBelowText
			);
		}
	};

	/**
	 * 对PDF文档进行Ekey硬件签章
	 */
	this.signaturePdfFromEkey = function() {

		if (this.controlObj != null) {
			/*
			 * if(!this.controlObj.IsEkeyConnected) {
			 * alert("没有检测到EKEY.请将EKEY插入到计算机!然后点击确定继续."); return; }
			 */
			alert("signaturePdfFromEkey");
			this.controlObj.ActiveDocument.AddPDFSecSignFromEKEY(
					_self.config.user.name, null, "111111", null, 1, null,
					null, null, null, true, false, true, false, null);
		}
	};

	/**
	 * 把Office文件转换成PDF文件。 服务器返回文件id到this.config.fileId，同时返回文件ID。
	 */
	this.officeToPdf = function() {
		if (!confirm("文档转换成PDF后将不可以恢复原有格式文档，确认转换吗？")) {
			return;
		}
		try {
			// 保存数据到服务器。
			var pdfUrl = __ctx + "/platform/system/sysFile/saveFilePdf.ht";
			var uploadName = this.controlId + "_pdf";
			var params = "fileId=" + this.config.fileId + "&uploadName="
					+ uploadName;
			var pdfName = this.config.fileId + ".pdf";
			this.controlObj.PublishAsPDFToURL(pdfUrl, uploadName, params,
					pdfName, 0, null, true, true, false, true, true, true);
			// window.location.href=window.location.href;
		} catch (err) {
			alert("officeToPdf:" + err.name + ": " + err.message);
		}
	};

	/**
	 * 把打开PDF文件
	 */
	this.addPDFSupport = function() {
		// this.controlObj = document.getElementById(this.controlId);
		if (document.URL.indexOf("file://") >= 0) {
			if (!confirm("如果从本地磁盘打开的URL，需要手工运行命令'regsvr32 ntkopdfdoc.dll'注册插件文件.您确认已经注册了吗？")) {
				return;
			}
		}
		var path = __ctx + "/platform/system/sysFile/getFileById.ht?fileId=";
        if(typeof this.config.downloadPath!='undefined'&&this.config.downloadPath!=''){
            path=this.config.downloadPath;
        }
        path+= this.config.fileId;
		var pdfCab="ntkooledocall.cab";
		if (Sys.ie&&window.navigator.platform=="Win64"){
			pdfCab="ntkooledocall64.cab";
		}
		this.controlObj.AddDocTypePlugin(".pdf", "PDF.NtkoDocument", "4,0,0,8",
				__ctx + "/media/office/"+pdfCab, 51, true); // 引用pdf组件
		// this.controlObj.BeginOpenFromURL(path); //打开PDF从URL
		// "media/office/bpm.pdf"
		this.controlObj.OpenFromURL(path);
	};

	/**
	 * 参数为true时把文档设置为只读，false按文档原来的权限设置
	 */
	this.setFileReadOnly = function(isRead) {
		var type = this.config.doctype;
		if (type == '' || type == null || 'undefined' == typeof (type)
				|| type == 'pdf' || type == 'PDF') {
			return;
		}

		try {
			if (isRead) {
				this.controlObj.SetReadOnly(true, '');
				this.config.right == 'r';
			} else {
				if (this.config.right != 'w' && this.config.right != 'b') {
					this.controlObj.SetReadOnly(true, '');
				}
			}
		} catch (err) {
			// alert("setFileReadOnly:" +err.name + ": " + err.message);
		}
	};

};

/**
 * 火狐谷歌浏览器控件文档保存事件（异步的，IE是同步的）回调接管函数 注意不在OfficeControl类里面 一定是单独方法
 * 是控件属性的ForOnSaveToURL对应的方法 （SaveToURL保存后的回调函数） html 为后台返回的内容
 */
var saveMethodOnCompleteNum = 0; // 有几次回调
function saveMethodOnComplete(type, code, html) {
	saveMethodOnCompleteNum = saveMethodOnCompleteNum + 1;
	var arrys = html.split("##");
	var arryNum = arrys[0]; // 保存对象的序号
	var arryValue = arrys[1]; // 要保存的内容
	if (arryNum >= 0) {
		// 返回小于1的情况要不要重新获取旧值做保存？
		/*
		 * if(arryValue<=0){ arryValue =
		 * OfficePlugin.fileObjs.get(arryNum).getAttribute("value"); //保存到对象的值; }
		 */
		if (arryValue > 0) {
			OfficePlugin.fileObjs.get(arryNum).setAttribute("value", arryValue); // 保存到对象的值
			OfficePlugin.officeObjs[arryNum].config.fileId = arryValue; // 控件中config对象的fileId
			OfficePlugin.hasSubmitOffices[arryNum] = true; // 完成标志
			OfficePlugin.submitNewNum = OfficePlugin.submitNewNum + 1; // 每回调一次就
																		// 提交数量的变量就
			if (OfficePlugin.submitNum == OfficePlugin.submitNewNum) { // 当提交问题
																		// 等于
																		// 提交数量的变量
																		// 时
																		// 表示所有文档
																		// 都提交了
																		// 然后做
																		// 业务相关的提交
				if (OfficePlugin.callBack) {
					OfficePlugin.callBack();
				} else {
					try{
						var data = CustomForm.getData();
					}catch(e){
						window.close();
					}
					// 设置表单数据
					$("#formData").val(data);
					$('#frmWorkFlow').submit();
				}
				if(!OfficePlugin.isExist&&!OfficePlugin.isEditAttach){
                    window.opener.addNewDoc(arryValue,OfficePlugin.officeName);
                    OfficePlugin.isExist=true;
                    fileId=arryValue;
				}
				if(OfficePlugin.isNeedClose){
                    window.close();
				}else {
					OfficePlugin.hiddenObj(true);
                    $.ligerDialog.success("保存成功！","提示",function () {
                        OfficePlugin.hiddenObj(false);
                    });
				}
				OfficePlugin.submitNewNum = 0; // 重置 提交数量的变量
				saveMethodOnCompleteNum = 0; // 重置 回调用提交方法次数的变量
			}
		} else {
			if (saveMethodOnCompleteNum == OfficePlugin.submitNum) {
				// $.ligerDialog.warn("提交失败,OFFICE控件没能正常使用，请重新安装 ！！！","提示");
                OfficePlugin.hiddenObj(true);
                $.ligerDialog.warn("提交失败,OFFICE控件没能正常使用，请重新安装 ！！！",function () {
					OfficePlugin.hiddenObj(false);
                });
			}
		}
	} else {
		if (saveMethodOnCompleteNum == OfficePlugin.submitNum) {
			// $.ligerDialog.warn("提交失败,OFFICE控件没能正常使用，请重新安装 ！！！","提示");
            OfficePlugin.hiddenObj(true);
			$.ligerDialog.warn("提交失败,OFFICE控件没能正常使用，请重新安装 ！！！",function () {
                OfficePlugin.hiddenObj(false);
            });
		}
	}
}

/**
 * 火狐谷歌浏览器控件文档在套红模版事件（异步的，IE是同步的）回调接管函数 注意不在OfficeControl类里面 一定是单独方法
 * 是控件属性的ForOnAddTemplateFromURL对应的方法 （AddTemplateFromURL保存后的回调函数） html 为后台返回的内容
 */
function addTemplateOnComplete(myNum) {
	// OfficePlugin.fileObjs.get(myNum).setAttribute("value",arryValue);
	// //保存到对象的值
	myObj = OfficePlugin.officeObjs[myNum]; // OfficeControl 实例对象
	var BookMarkName = "content";
	if (!myObj.controlObj.ActiveDocument.BookMarks.Exists(BookMarkName)) {
		alert("Word 模板中不存在名称为：\"" + BookMarkName + "\"的书签！");
		return false;
	}
	// var bkmkObj = myObj.controlObj.ActiveDocument.BookMarks(BookMarkName);
	// //IE的方法
	var bkmkObj = myObj.controlObj.ActiveDocument.BookMarks.Item(BookMarkName); // 火狐和谷歌特有的
	var saverange = bkmkObj.Range;
	saverange.Paste();
	myObj.controlObj.ActiveDocument.Bookmarks.Add(BookMarkName, saverange);

}

/**
 * 火狐谷歌浏览器控件文档在打开文档后的事件（异步的，IE是同步的）
 * 
 */
function documentOpenedOnComplete(myNum) {
	myObj = OfficePlugin.officeObjs[myNum]; // OfficeControl 实例对象
	myObj.controlObj.Activate(true);
	// 文档要求为只读时，通过Office 实例对象设置文档为只读
	if (myObj != null && myObj != undefined) {
		myObj.setFileReadOnly(false);
	}
	if (typeof documentOpenedOnCompleteCallback != 'undefined') {
		documentOpenedOnCompleteCallback();
	}
	if (myObj != null && myObj != undefined) {
		addCustomMenu(myNum);
	}
}

function addCustomMenu(myNum){
	myObj = OfficePlugin.officeObjs[myNum];

	if(myObj.config.fileId!=null && myObj.config.fileId!= undefined && myObj.config.fileId!=""){
		//打开已保存文档默认留痕操作,如果是新建文档则不留痕
		myObj.setDocUser();
		myObj.controlObj.ActiveDocument.TrackRevisions = true;
	}
    //打开文档时默认不显示留痕
    // myObj.controlObj.ActiveDocument.ShowRevisions = false;
	//屏蔽左侧导航栏
    myObj.controlObj.ActiveWindow.DocumentMap = false;

	if (myObj != null && myObj != undefined) {
		if(myObj.config.menuRight.showLhRight == "y"){
			myObj.controlObj.AddCustomButtonOnMenu(6," 显示留痕   ");
		}
		if(myObj.config.menuRight.hideLhRight == "y"){
			myObj.controlObj.AddCustomButtonOnMenu(7," 隐藏留痕   ");
		}

        //office菜单暂时屏蔽
        /*if(myObj.config.menuRight.wjRight == "y"){
            myObj.controlObj.AddCustomButtonOnMenu(0," 新建   ");
        }
        if(myObj.config.menuRight.lhRight == "y"){
            myObj.controlObj.AddCustomButtonOnMenu(1," 留痕   ");
        };
        if(myObj.config.menuRight.blhRight == "y"){
            myObj.controlObj.AddCustomButtonOnMenu(2," 不留痕   ");
        };
        if(myObj.config.menuRight.qchjRight == "y"){
            myObj.controlObj.AddCustomButtonOnMenu(3," 清除痕迹   ");
        };
		if(myObj.config.menuRight.mbthRight == "y"){
			myObj.controlObj.AddCustomButtonOnMenu(4," 模板套红   ");
		};
		if(myObj.config.menuRight.xzmbRight == "y"){
			myObj.controlObj.AddCustomButtonOnMenu(5," 选择模板   ");
		};
		if(myObj.config.menuRight.sxqmRight == "y"){
			myObj.controlObj.AddCustomButtonOnMenu(6," 手写签名   ");
		};
		if(myObj.config.menuRight.gzRight == "y"){
			myObj.controlObj.AddCustomButtonOnMenu(7," 盖章   ");
		};*/

		/*if(myObj.config.menuRight.originRight == "y"||myObj.config.menuRight.showLhRight == "y"||myObj.config.menuRight.hideLhRight == "y"){
			var pos=0;
			myObj.controlObj.AddCustomMenu2(1," 修订状态   ");
            if (myObj.config.menuRight.originRight == "y"){
                myObj.controlObj.AddCustomMenuItem2(1,pos,-1,false,"原始版本",false,1);
                pos++;
			}
            if (myObj.config.menuRight.showLhRight == "y"){
                myObj.controlObj.AddCustomMenuItem2(1,pos,-1,false,"显示留痕",false,2);
                pos++;
			}
            if (myObj.config.menuRight.hideLhRight == "y"){
                myObj.controlObj.AddCustomMenuItem2(1,pos,-1,false,"隐藏留痕",false,3);
                pos++;
			}
		}*/
        myObj.controlObj.AddCustomButtonOnMenu(8," 打印   ");
	}
}



function customButtonOnMenuCmd(btnPos, btnCaption, btnID){
	var myObj = OfficePlugin.officeObjs[0];
	if(btnPos==0){
		myObj.newDoc();
	}else if(btnPos==1){
		myObj.setDocUser();
		myObj.controlObj.ActiveDocument.TrackRevisions = true;
	}else if(btnPos==2){
		myObj.controlObj.ActiveDocument.TrackRevisions = false;
	}else if(btnPos==3){
		myObj.controlObj.ActiveDocument.AcceptAllRevisions()
	}else if(btnPos==4){
		myObj.insertContentTemplate();
	}else if(btnPos==5) {
        myObj.insertTemplate();
        // }else if(btnPos==6){
        // 	myObj.insertHandSign();
        // }else if(btnPos==7){
        // 	myObj.signature();
    }else if (btnPos==6){
        myObj.controlObj.ActiveDocument.ShowRevisions = true;
	}else if (btnPos==7){
        myObj.controlObj.ActiveDocument.ShowRevisions = false;
    }else if(btnPos==8){
        myObj.controlObj.printout(true);
	}

	/**/

}


function customMenuCmd(menuPos, submenuPos, subsubmenuPos, menuCaption, myMenuID){
	var myObj = OfficePlugin.officeObjs[0];
	if(myMenuID ==1){
		/** 显示原始版本	*/

		myObj.controlObj.ActiveDocument.ActiveWindow.View.RevisionsFilter.Markup = 0;
		myObj.controlObj.ActiveDocument.ActiveWindow.View.RevisionsFilter.View = 1;
	}else if(myMenuID == 2){
	
		/** 显示标记的原始状态	*/
		myObj.controlObj.ActiveDocument.ActiveWindow.View.RevisionsFilter.Markup = 2;
		myObj.controlObj.ActiveDocument.ActiveWindow.View.RevisionsFilter.View = 0;
	}else if(myMenuID == 3){
		/** 隐藏留痕	*/
        myObj.controlObj.ActiveDocument.ShowRevisions = false;
	}
}
