package com.suneee.ucp.mh.model.gatewayManage;


import com.suneee.ucp.mh.model.customColumn.CustomColumnVO;
import com.suneee.ucp.mh.model.rowTemp.SysRowTemp;
import com.suneee.ucp.mh.model.shortcut.ShortCut;

import java.util.List;

/**
 * 门户管理编辑信息VO
 *
 * @author ytw
 */
public class GatewaySettingMenuVO {

    //所属公司
    private String companyName;

    //门户类型list
    private List gatewayType;

    //门户模板list
    private List<SysRowTemp> templateList;

    //快捷入口list
    private List<ShortCut> shortCutList;

    //门户内容list
    private List<CustomColumnVO> customColumnVOList;

    //常用模板list
    private List<SysRowTemp> hotTemplateList;

    //职务级别
    private List jobGrade;

    public GatewaySettingMenuVO() {
    }

    public GatewaySettingMenuVO(String companyName, List gatewayType, List<SysRowTemp> templateList, List<ShortCut> shortCutList, List<CustomColumnVO> customColumnVOList, List<SysRowTemp> hotTemplateList, List jobGrade) {
        this.companyName = companyName;
        this.gatewayType = gatewayType;
        this.templateList = templateList;
        this.shortCutList = shortCutList;
        this.customColumnVOList = customColumnVOList;
        this.hotTemplateList = hotTemplateList;
        this.jobGrade = jobGrade;
    }

    public List<CustomColumnVO> getCustomColumnVOList() {
        return customColumnVOList;
    }

    public void setCustomColumnVOList(List<CustomColumnVO> customColumnVOList) {
        this.customColumnVOList = customColumnVOList;
    }


    public List getJobGrade() {
        return jobGrade;
    }

    public void setJobGrade(List jobGrade) {
        this.jobGrade = jobGrade;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public List getGatewayType() {
        return gatewayType;
    }

    public void setGatewayType(List gatewayType) {
        this.gatewayType = gatewayType;
    }

    public List<SysRowTemp> getTemplateList() {
        return templateList;
    }

    public void setTemplateList(List<SysRowTemp> templateList) {
        this.templateList = templateList;
    }

    public List<ShortCut> getShortCutList() {
        return shortCutList;
    }

    public void setShortCutList(List<ShortCut> shortCutList) {
        this.shortCutList = shortCutList;
    }

    public List<SysRowTemp> getHotTemplateList() {
        return hotTemplateList;
    }

    public void setHotTemplateList(List<SysRowTemp> hotTemplateList) {
        this.hotTemplateList = hotTemplateList;
    }


}
