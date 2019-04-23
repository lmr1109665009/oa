(function($){
    var userAgent = navigator.userAgent,
        rMsie = /(msie\s|trident.*rv:)([\w.]+)/,
        rFirefox = /(firefox)\/([\w.]+)/,
        rOpera = /(opera).+version\/([\w.]+)/,
        rChrome = /(chrome)\/([\w.]+)/,
        rSafari = /version\/([\w.]+).*(safari)/;
    var browser;
    var version;
    var ua = userAgent.toLowerCase();
    function uaMatch(ua) {
        var match = rMsie.exec(ua);
        if (match != null) {
            return { browser : "IE", version : match[2] || "0" };
        }
        var match = rFirefox.exec(ua);
        if (match != null) {
            return { browser : match[1] || "", version : match[2] || "0" };
        }
        var match = rOpera.exec(ua);
        if (match != null) {
            return { browser : match[1] || "", version : match[2] || "0" };
        }
        var match = rChrome.exec(ua);
        if (match != null) {
            return { browser : match[1] || "", version : match[2] || "0" };
        }
        var match = rSafari.exec(ua);
        if (match != null) {
            return { browser : match[2] || "", version : match[1] || "0" };
        }
        if (match != null) {
            return { browser : "", version : "0" };
        }
    }
    var browserMatch = uaMatch(userAgent.toLowerCase());
    if (browserMatch.browser) {
        browser = browserMatch.browser;
        version = browserMatch.version;
    }

    /*
    谷歌浏览器事件接管
    */
    function onSaveToURL(type,code,html)
    {
        //alert(type);
        //alert(code);

    }
    function onBeginOpenFromURL(type,code,html)
    {
        //alert(type);
        //alert(code);

    }
    function onDocumentOpened(str,doc)
    {
        TANGER_OCX_OBJ.activeDocument.saved=true;//saved属性用来判断文档是否被修改过,文档打开的时候设置成ture,当文档被修改,自动被设置为false,该属性由office提供.
        //	TANGER_OCX_OBJ.SetReadOnly(true,"");
        //TANGER_OCX_OBJ.ActiveDocument.Protect(1,true,"123");
        //获取文档控件中打开的文档的文档类型
        switch (TANGER_OCX_OBJ.doctype)
        {
            case 1:
                fileType = "Word.Document";
                fileTypeSimple = "wrod";
                break;
            case 2:
                fileType = "Excel.Sheet";
                fileTypeSimple="excel";
                break;
            case 3:
                fileType = "PowerPoint.Show";
                fileTypeSimple = "ppt";
                break;
            case 4:
                fileType = "Visio.Drawing";
                break;
            case 5:
                fileType = "MSProject.Project";
                break;
            case 6:
                fileType = "WPS Doc";
                fileTypeSimple="wps";
                break;
            case 7:
                fileType = "Kingsoft Sheet";
                fileTypeSimple="et";
                break;
            default :
                fileType = "unkownfiletype";
                fileTypeSimple="unkownfiletype";
        }

        //alert("ondocumentopened成功回调");
    }
    function onPublishAsHtml(type,code,html){

    }
    function onPublishAsPdf(type,code,html){

    }
    function onSaveAsOtherUrl(type,code,html){

    }
    function onDoWebGet(type,code,html){

    }
    function onDoWebExecute(type,code,html){

    }
    function onDoWebExecute2(type,code,html){

    }
    function onFileCommand(TANGER_OCX_str,TANGER_OCX_obj){
        if (TANGER_OCX_str == 3)
        {
            TANGER_OCX_OBJ.CancelLastCommand = true;
        }
    }
    function onCustomMenuCmd(menuPos,submenuPos,subsubmenuPos,menuCaption,menuID){
//alert("第" + menuPos +","+ submenuPos +","+ subsubmenuPos +"个菜单项,menuID="+menuID+",菜单标题为\""+menuCaption+"\"的命令被执行.");
    }

    var defaultOptions={
        "id":"fileViewer",
        "classId":"A64E3073-2016-4baf-A89D-FFE1FAA10EC2",
        "classId64":"916EE952-83C7-485f-8469-69D975889ED2",
        "codeBase":"/media/office/OfficeControl.cab#version=5,0,4,0",
        "codeBase64":"/media/office/OfficeControl64.cab#version=4,0,0,8",
        "makerCaption":"象翌微链科技发展有限公司",
        "makerKey":"E119EB1C2C64CBD3E9C611AE3008F0A5F505F7CE",
        "productCaption":"象翌微链科技发展有限公司",
        "productKey":"CAF45014E0D0978FB1FD8EB4CB9C4EC6874AABF8",
        "loadFailInfo":"不能装载文档控件,请在检查浏览器的选项中检查浏览器的安全设置",
        "caption":"Office文档在线处理",
        "webUserName":"suneee",
        "file":{}
    }

    $.fn.viewFile=function(options){
        //默认office配置
        options=$.extend(defaultOptions,options);
        options.codeBase=options.contextPath+options.codeBase;
        options.codeBase64=options.contextPath+options.codeBase64;
        var content='';
        if (browser=="IE"){
            if(window.navigator.platform=="Win32"){
               content+='<!-- 用来产生编辑状态的ActiveX控件的JS脚本-->   ';
               content+='<!-- 因为微软的ActiveX新机制，需要一个外部引入的js-->   ';
               content+='<object id="'+options.id+'" classid="clsid:'+classid+'"';
               content+='codebase="'+options.codeBase+'" width="100%" height="100%">   ';
               content+='<param name="IsUseUTF8URL" value="-1">   ';
               content+='<param name="IsUseUTF8Data" value="-1">   ';
               content+='<param name="BorderStyle" value="1">   ';
               content+='<param name="BorderColor" value="14402205">   ';
               content+='<param name="TitlebarColor" value="15658734">   ';

               content+='<param name="MakerCaption" value="'+options.makerCaption+'">';
               content+='<param name="MakerKey" value="'+options.makerKey+'">';
               content+='<param name="ProductCaption" value="'+options.productCaption+'"> ';
               content+='<param name="ProductKey" value="'+options.productKey+'">';

               content+='<param name="TitlebarTextColor" value="0">   ';
               content+='<param name="MenubarColor" value="14402205">   ';
               content+='<param name="MenuButtonColor" value="16180947">   ';
               content+='<param name="MenuBarStyle" value="3">   ';
               content+='<param name="MenuButtonStyle" value="7">   ';
               content+='<param name="WebUserName" value="'+options.webUserName+'">   ';
               content+='<param name="Caption" value="'+options.caption+'">   ';
               content+='<span style="color:red">'+options.loadFailInfo+'</span>   ';
               content+='</object>';
            }
            if(window.navigator.platform=="Win64"){

               content+='<!-- 用来产生编辑状态的ActiveX控件的JS脚本-->   ';
               content+='<!-- 因为微软的ActiveX新机制，需要一个外部引入的js-->   ';
               content+='<object id="'+options.id+'" classid="clsid:'+options.classId64+'"';
               content+='codebase="'+options.codeBase64+'" width="100%" height="100%">   ';
               content+='<param name="IsUseUTF8URL" value="-1">   ';
               content+='<param name="IsUseUTF8Data" value="-1">   ';
               content+='<param name="BorderStyle" value="1">   ';
               content+='<param name="BorderColor" value="14402205">   ';
               content+='<param name="TitlebarColor" value="15658734">   ';
               content+='<param name="isoptforopenspeed" value="0">   ';
               content+='<param name="TitlebarTextColor" value="0">   ';

               content+='<param name="MakerCaption" value="'+options.makerCaption+'">';
               content+='<param name="MakerKey" value="'+options.makerKey+'">';
               content+='<param name="ProductCaption" value="'+options.productCaption+'"> ';
               content+='<param name="ProductKey" value="'+options.productKey+'">';

               content+='<param name="MenubarColor" value="14402205">   ';
               content+='<param name="MenuButtonColor" VALUE="16180947">   ';
               content+='<param name="MenuBarStyle" value="3">   ';
               content+='<param name="MenuButtonStyle" value="7">   ';
               content+='<param name="WebUserName" value="'+options.webUserName+'">   ';
               content+='<param name="Caption" value="'+options.caption+'">   ';
               content+='<span style="color:red">'+options.loadFailInfo+'</span>   ';
               content+='</object>';

            }

        }
        else if (browser=="firefox"){
           content+='<object id="'+options.id+'" type="application/ntko-plug"  codebase="'+options.codeBase+'" width="100%" height="100%" ForOnSaveToURL="onSaveToURL" ForOnBeginOpenFromURL="onBeginOpenFromURL" ForOndocumentopened="onDocumentOpened"';

           content+='ForOnpublishAshtmltourl="onPublishAsHtml"';
           content+='ForOnpublishAspdftourl="onPublishAsPdf"';
           content+='ForOnSaveAsOtherFormatToUrl="onSaveAsOtherUrl"';
           content+='ForOnDoWebGet="onDoWebGet"';
           content+='ForOnDoWebExecute="onDoWebExecute"';
           content+='ForOnDoWebExecute2="onDoWebExecute2"';
           content+='ForOnFileCommand="onFileCommand"';
           content+='ForOnCustomMenuCmd2="onCustomMenuCmd"';
           content+='_IsUseUTF8URL="-1"   ';

           content+='_MakerCaption="'+options.makerCaption+'"';
           content+='_MakerKey="'+options.makerKey+'"';
           content+='_ProductCaption="'+options.productCaption+'" ';
           content+='_ProductKey="'+options.productKey+'"';

           content+='_IsUseUTF8Data="-1"   ';
           content+='_BorderStyle="1"   ';
           content+='_BorderColor="14402205"   ';
           content+='_MenubarColor="14402205"   ';
           content+='_MenuButtonColor="16180947"   ';
           content+='_MenuBarStyle="3"  ';
           content+='_MenuButtonStyle="7"   ';
           content+='_WebUserName="'+options.webUserName+'"   ';
           content+='clsid="{'+options.classId+'}" >';
           content+='<span style="color:red">'+options.loadFailInfo+'</span>   ';
           content+='</object>   ';
        }else if(browser=="chrome"){
           content+='<object id="'+options.id+'" clsid="{'+options.classId+'}"  ForOnSaveToURL="onSaveToURL" ForOnBeginOpenFromURL="onBeginOpenFromURL" ForOndocumentopened="onDocumentOpened"';
           content+='ForOnpublishAshtmltourl="onPublishAsHtml"';
           content+='ForOnpublishAspdftourl="onPublishAsPdf"';
           content+='ForOnSaveAsOtherFormatToUrl="onSaveAsOtherUrl"';
           content+='ForOnDoWebGet="onDoWebGet"';
           content+='ForOnDoWebExecute="onDoWebExecute"';
           content+='ForOnDoWebExecute2="onDoWebExecute2"';
           content+='ForOnFileCommand="onFileCommand"';

           content+='_MakerCaption="'+options.makerCaption+'"';
           content+='_MakerKey="'+options.makerKey+'"';
           content+='_ProductCaption="'+options.productCaption+'" ';
           content+='_ProductKey="'+options.productKey+'"';

           content+='ForOnCustomMenuCmd2="onCustomMenuCmd"';
           content+='codebase="'+options.codeBase+'" width="100%" height="100%" type="application/ntko-plug" ';
           content+='_IsUseUTF8URL="-1"   ';
           content+='_IsUseUTF8Data="-1"   ';
           content+='_BorderStyle="1"   ';
           content+='_BorderColor="14402205"   ';
           content+='_MenubarColor="14402205"   ';
           content+='_MenuButtonColor="16180947"   ';
           content+='_MenuBarStyle="3"  ';
           content+='_MenuButtonStyle="7"   ';
           content+='_WebUserName="'+options.webUserName+'"   ';
           content+='_Caption="'+options.caption+'">    ';
           content+='<span style="color:red">'+options.loadFailInfo+'</span>   ';
           content+='</object>';
        }else if (Sys.opera){
            alert("sorry,ntko web印章暂时不支持opera!");
        }else if (Sys.safari){
            alert("sorry,ntko web印章暂时不支持safari!");
        }
        $(this).html(content);
        var viewer=$("#"+options.id).get(0);
        viewer.MenuBar = false;
        viewer.Titlebar = false;
        viewer.CustomToolBar = false;
        viewer.ToolBars = false;

        if(options.file.ext=="doc"||options.file.ext=="xls"||options.file.ext=="ppt"){
            //解决格式跟文件指定格式不一致的问题
            viewer.OpenFromURL(options.file.path, null, options.file.ext);
        }else if (options.file.ext=="docx"||options.file.ext=="xlsx"||options.file.ext=="pptx") {
            viewer.OpenFromURL(options.file.path);
        }else if (options.file.ext=="pdf"){
            var pdfCab="ntkooledocall.cab";
            if (browser=="IE"&&window.navigator.platform=="Win64"){
                pdfCab="ntkooledocall64.cab";
            }
            viewer.AddDocTypePlugin(".pdf", "PDF.NtkoDocument", "4,0,0,8",
                options.contextPath + "/media/office/"+pdfCab, 51, true); // 引用pdf组件
            viewer.OpenFromURL(options.file.path);
        }else {
            alert("当前文件不支持预览");
        }
    };
    $.fn.hideFile=function(options){
        //默认office配置
        options=$.extend(defaultOptions,options);
        options.codeBase=options.contextPath+options.codeBase;
        options.codeBase64=options.contextPath+options.codeBase64;
        var viewer=$("#"+options.id);
        viewer.css({
            width:1,
            height:1,
            position:"absolute",
            top:-1000
        })
    }
    $.fn.showFile=function(options){
        //默认office配置
        options=$.extend(defaultOptions,options);
        options.codeBase=options.contextPath+options.codeBase;
        options.codeBase64=options.contextPath+options.codeBase64;
        var viewer=$("#"+options.id);
        viewer.css({
            width:"100%",
            height:"100%",
            position:"inherit",
            top:0
        })
    }
})(jQuery)