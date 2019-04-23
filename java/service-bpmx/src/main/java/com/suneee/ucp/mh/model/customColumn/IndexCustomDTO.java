package com.suneee.ucp.mh.model.customColumn;

import com.suneee.ucp.mh.model.shortcut.ShortCut;

import java.util.List;

/**
 * 首页自定义保存DTO
 * @author ytw
 * */
public class IndexCustomDTO {

    /**
     * 已办事宜栏目自定义tab信息
     * */
   //private CustomColumnVO alreadyCustomColumnList;

    private List<CustomColumn> alreadyCustomColumnList;

    /**
     * 待办事宜栏目自定义tab信息
     * */
    private List<CustomColumn> penddingCustomColumnList;

    // private CustomColumnVO penddingCustomColumnList;

    /**
     * 快捷入口自定义信息
     * */
   private List<ShortCut> shortCutList;

    public List<CustomColumn> getAlreadyCustomColumnList() {
        return alreadyCustomColumnList;
    }

    public void setAlreadyCustomColumnList(List<CustomColumn> alreadyCustomColumnList) {
        this.alreadyCustomColumnList = alreadyCustomColumnList;
    }

    public List<CustomColumn> getPenddingCustomColumnList() {
        return penddingCustomColumnList;
    }

    public void setPenddingCustomColumnList(List<CustomColumn> penddingCustomColumnList) {
        this.penddingCustomColumnList = penddingCustomColumnList;
    }

    public List<ShortCut> getShortCutList() {
        return shortCutList;
    }

    public void setShortCutList(List<ShortCut> shortCutList) {
        this.shortCutList = shortCutList;
    }

    @Override
    public String toString() {
        return "IndexCustomDTO{" +
                "alreadyCustomColumnList=" + alreadyCustomColumnList +
                ", penddingCustomColumnList=" + penddingCustomColumnList +
                ", shortCutList=" + shortCutList +
                '}';
    }
}
