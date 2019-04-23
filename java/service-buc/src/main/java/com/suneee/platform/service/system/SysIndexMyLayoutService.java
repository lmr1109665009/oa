package com.suneee.platform.service.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.system.SysIndexMyLayoutDao;
import com.suneee.platform.model.system.SysIndexColumn;
import com.suneee.platform.model.system.SysIndexMyLayout;
import com.suneee.platform.service.util.ServiceUtil;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.system.SysIndexMyLayoutDao;
import com.suneee.platform.model.system.SysIndexColumn;
import com.suneee.platform.model.system.SysIndexMyLayout;
import com.suneee.platform.service.util.ServiceUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

/**
 * <pre>
 * 对象功能:我的布局 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:hugh
 * 创建时间:2015-03-18 15:39:48
 * </pre>
 */
@Service
public class SysIndexMyLayoutService extends BaseService<SysIndexMyLayout> {
	@Resource
	private SysIndexMyLayoutDao dao;
	@Resource
	private SysIndexLayoutManageService sysIndexLayoutManageService;
	@Resource
	private SysIndexColumnService sysIndexColumnService;
	public SysIndexMyLayoutService() {
	}

	@Override
	protected IEntityDao<SysIndexMyLayout, Long> getEntityDao() {
		return dao;
	}

	public String obtainMyIndexData(Long userId) {
		// 1、首先先找到自己定义的布局；
		SysIndexMyLayout sysIndexMyLayout = dao.getByUserId(userId);
		if (BeanUtils.isNotEmpty(sysIndexMyLayout))
			return sysIndexMyLayout.getTemplateHtml();
		//2.找自己我有权限的管理布局 ，按是否默认，排序排序
		String html = sysIndexLayoutManageService.getMyHasRightsLayout();
		if (BeanUtils.isNotEmpty(html))
			return html;
		//3、找自己所属子组织或公司的没权限但设置默认布局；
		html = sysIndexLayoutManageService.getHasRightsLayout();
		if (BeanUtils.isNotEmpty(html))
			return html;
		//4、如果找不到找系统管理员的设置默认布局;
		html = sysIndexLayoutManageService.getManagerLayout();
		if (BeanUtils.isNotEmpty(html))
			return html;
		//5、再找不到则使用系统默认布局。
		if (BeanUtils.isEmpty(html))
			html = defaultIndexLayout();
		return html;
	}

	/**
	 * 默认首页布局
	 * 
	 * @return
	 */
	private String defaultIndexLayout() {
		String templateHtml= FileUtil.readFile(ServiceUtil.getIndexTemplatePath()+"templates"+File.separator+"defaultIndexPages.ftl");
		return templateHtml;
	}

	public SysIndexMyLayout getLayoutList(Long userId,
			List<SysIndexColumn> columnList) {
		SysIndexMyLayout sysIndexMyLayout = dao.getByUserId(userId);
		if (BeanUtils.isEmpty(sysIndexMyLayout))
			return getDefaultIndexLayout();
		sysIndexMyLayout.setDesignHtml(sysIndexColumnService.parserDesignHtml(
				sysIndexMyLayout.getDesignHtml(), columnList));
		return sysIndexMyLayout;
	}


	private SysIndexMyLayout getDefaultIndexLayout() {
		SysIndexMyLayout sysIndexMyLayout = new SysIndexMyLayout();
		sysIndexMyLayout.setDesignHtml(sysIndexLayoutManageService.getDefaultDesignHtml());
		return sysIndexMyLayout;
	}
	public void save(String html, String designHtml) {
		Long userId = ContextUtil.getCurrentUserId();
		SysIndexMyLayout sysIndexMyLayout = dao.getByUserId(userId);
		if (BeanUtils.isEmpty(sysIndexMyLayout)) {
			sysIndexMyLayout = new SysIndexMyLayout();
			sysIndexMyLayout.setDesignHtml(designHtml);
			sysIndexMyLayout.setTemplateHtml(html);
			sysIndexMyLayout.setId(UniqueIdUtil.genId());
			sysIndexMyLayout.setUserId(userId);
			dao.add(sysIndexMyLayout);
		} else {
			sysIndexMyLayout.setDesignHtml(designHtml);
			sysIndexMyLayout.setTemplateHtml(html);
			dao.update(sysIndexMyLayout);
		}
	}
}
