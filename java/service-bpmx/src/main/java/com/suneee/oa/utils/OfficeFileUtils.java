package com.suneee.oa.utils;

import com.suneee.oa.dto.OfficeFileDto;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * office文件操作类
 * 开发公司:深圳象翌
 * 开发人员:子华
 * 创建时间:2018-02-05 14:09:00
 */
public class OfficeFileUtils {
    public static final XStream X_STREAM=new XStream();
    static {
        X_STREAM.alias("office",OfficeFileDto.class);
        X_STREAM.alias("list",ArrayList.class);
    }

    /**
     * 加载office下载文件数据
     * @param filepath
     * @return
     */
    public static List<OfficeFileDto> loadOfficeFile(String filepath){
        List<OfficeFileDto> officeList=null;
        File file=new File(filepath);
        if (file.exists()){
            officeList= (List<OfficeFileDto>) X_STREAM.fromXML(file);
        }
        if (officeList!=null){
            long id=1;
            for (OfficeFileDto office:officeList){
                office.setId(id);
                id++;
            }
        }
        if (officeList==null){
            officeList=new ArrayList<OfficeFileDto>();
        }
        return officeList;
    }

    /**
     * 持久化office下载文件数据
     */
    public static void persistenceData(List<OfficeFileDto> officeList,String filepath) throws IOException {
        File file=new File(filepath);
        if (file.getParentFile()!=null&&!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        String content="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        content+=X_STREAM.toXML(officeList);
        FileUtils.writeByteArrayToFile(file,content.getBytes("utf-8"));
    }
}
