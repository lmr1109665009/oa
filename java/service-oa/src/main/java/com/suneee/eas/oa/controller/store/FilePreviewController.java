package com.suneee.eas.oa.controller.store;

import com.suneee.eas.common.constant.FunctionConstant;
import com.suneee.eas.common.constant.ModuleConstant;
import com.suneee.eas.common.uploader.UploaderHandler;
import com.suneee.eas.common.utils.FileUtil;
import com.suneee.eas.oa.model.fs.DocFile;
import com.suneee.eas.oa.service.fs.DocFileService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件预览
 * @user 子华
 * @created 2018/8/31
 */
@Controller
@RequestMapping(ModuleConstant.STORE_WEB_MODULE+ FunctionConstant.STORE_PREVIEW)
public class FilePreviewController {
    private static final Logger log= LogManager.getLogger(FilePreviewController.class);
    private static final String PREFIX_TEMPLATE="store/preview/";
    @Autowired
    private DocFileService fileService;
    @Autowired
    private UploaderHandler uploaderHandler;

    /**
     * 老版本预览文件
     * @param id
     * @pa
     * @return
     */
    @RequestMapping("viewDoc")
    public String viewDoc(@RequestParam Long id, HttpServletRequest request, Model model){
        DocFile file=fileService.findById(id);
        if (file==null){
            return PREFIX_TEMPLATE+"notFound";
        }
        model.addAttribute("path",request.getContextPath()+ModuleConstant.STORE_WEB_MODULE+ FunctionConstant.STORE_PREVIEW+"download/"+id);
        model.addAttribute("ext", FileUtil.getFileExt(file.getName()));
        return PREFIX_TEMPLATE+"viewFile";
    }

    /**
     * 预览文件
     * @param id
     * @return
     */
    @RequestMapping("viewFile")
    public String viewFile(@RequestParam Long id){

        return PREFIX_TEMPLATE+"viewFile";
    }

    /**
     * 文件下载处理
     * @param id
     */
    @RequestMapping(value = "download/{id}")
    public void download(@PathVariable Long id){
        DocFile docFile = fileService.findById(id);
        if (docFile==null){
            log.error("下载文件不存在，ID："+id);
            return;
        }
        String fileName = docFile.getName();
        Map<String,Object> params=new HashMap<>();
        params.put("attachName",fileName);
        String filePath = docFile.getPath();
        try {
            uploaderHandler.download(filePath,true,params);
        } catch (Exception e) {
            log.error("下载文件异常，ID："+id);
            e.printStackTrace();
        }

    }

    @RequestMapping("getOfficeList")
    public String getOfficeList(){

        return PREFIX_TEMPLATE+"getOfficeList";
    }
}
