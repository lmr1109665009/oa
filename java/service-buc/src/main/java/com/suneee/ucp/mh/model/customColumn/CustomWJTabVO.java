/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: CustomWJTabVo
 * Author:   lmr
 * Date:     2018/5/22 17:01
 * Description: 文件类的返回实体类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.ucp.mh.model.customColumn;

import com.suneee.oa.model.docFile.DocFile;

import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈文件类的返回实体类〉
 *
 * @author lmr
 * @create 2018/5/22
 * @since 1.0.0
 */
public class CustomWJTabVO {

        private Long tabId;

        private Long columnId;

        private String tabName;

        //对应tab下的数据
        private List<DocFile> data;

        public Long getTabId() {
            return tabId;
        }

        public void setTabId(Long tabId) {
            this.tabId = tabId;
        }

        public Long getColumnId() {
            return columnId;
        }

        public void setColumnId(Long columnId) {
            this.columnId = columnId;
        }

        public String getTabName() {
            return tabName;
        }

        public void setTabName(String tabName) {
            this.tabName = tabName;
        }

        public List<DocFile> getData() {
            return data;
        }

        public void setData(List<DocFile> data) {
            this.data = data;
        }



}