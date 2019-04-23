package com.suneee.ucp.mh.model.shortcut;


import java.util.List;

/**
 * 快捷入口自定义编辑页面展示信息VO
 * @author ytw
 * */
public class ShortCutCustomInfoVO {

    //已选快捷入口list
    private List<ShortCut> selectedList;

    //未选快捷入口list
    private List<ShortCut> unSelectedList;

    public ShortCutCustomInfoVO() {
    }

    public ShortCutCustomInfoVO(List<ShortCut> selectedList, List<ShortCut> unSelectedList) {
        this.selectedList = selectedList;
        this.unSelectedList = unSelectedList;
    }

    public List<ShortCut> getSelectedList() {
        return selectedList;
    }

    public void setSelectedList(List<ShortCut> selectedList) {
        this.selectedList = selectedList;
    }

    public List<ShortCut> getUnSelectedList() {
        return unSelectedList;
    }

    public void setUnSelectedList(List<ShortCut> unSelectedList) {
        this.unSelectedList = unSelectedList;
    }
}
