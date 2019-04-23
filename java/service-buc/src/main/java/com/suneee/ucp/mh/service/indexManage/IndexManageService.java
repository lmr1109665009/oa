package com.suneee.ucp.mh.service.indexManage;


import com.suneee.ucp.base.vo.ResultVo;
import com.suneee.ucp.mh.model.customColumn.CustomColumnVO;
import com.suneee.ucp.mh.model.customColumn.IndexCustomDTO;
import com.suneee.ucp.mh.service.customColumn.CustomColumnService;
import com.suneee.ucp.mh.service.gatewayManage.ShortCutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 首页管理service
 * @author ytw
 * date:2018.04.03
 * */
@Service
public class IndexManageService {

    @Autowired
    private CustomColumnService customColumnService;

    @Autowired
    private ShortCutService shortCutService;


    public ResultVo saveIndexCustom(IndexCustomDTO indexCustomDTO){
        //保存自定义快捷入口
        this.shortCutService.saveCustomShortCut(indexCustomDTO.getShortCutList());
        //保存已办事宜自定义栏目
        CustomColumnVO alreadyVO = new CustomColumnVO();
        alreadyVO.setCustomTabList(indexCustomDTO.getAlreadyCustomColumnList());
      //  alreadyVO.setColumnId(CustomColumnService.ALREADY);
        this.customColumnService.saveCustomTab(alreadyVO);
        //保存待办事宜自定义栏目
        CustomColumnVO penddingVO = new CustomColumnVO();
        penddingVO.setCustomTabList(indexCustomDTO.getPenddingCustomColumnList());
      //  penddingVO.setColumnId(CustomColumnService.PENDDING);
        this.customColumnService.saveCustomTab(penddingVO);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "保存成功");
    }
}
