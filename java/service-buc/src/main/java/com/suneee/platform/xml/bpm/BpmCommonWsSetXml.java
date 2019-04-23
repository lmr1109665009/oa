package com.suneee.platform.xml.bpm;

import com.suneee.platform.model.bpm.BpmCommonWsParams;
import com.suneee.platform.model.bpm.BpmCommonWsSet;
import com.suneee.platform.xml.constant.XmlConstant;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "commonWsSets")
@XmlAccessorType(XmlAccessType.FIELD)

public class BpmCommonWsSetXml
{	
	/**
	 * Web服务调用配置
	 */
	@XmlElement(name = XmlConstant.BPM_COMMON_WS_SET, type = BpmCommonWsSet.class)
	private BpmCommonWsSet bpmCommonWsSet;
	
	/**
	 * 通用webservice调用参数列表 
	 */
	@XmlElementWrapper(name = XmlConstant.BPM_COMMON_WS_PARAMS_LIST)
	@XmlElement(name = XmlConstant.BPM_COMMON_WS_PARAMS, type =BpmCommonWsParams.class)
	private List<BpmCommonWsParams> bpmCommonWsParamsList;

	public BpmCommonWsSet getBpmCommonWsSet() {
		return bpmCommonWsSet;
	}

	public void setBpmCommonWsSet(BpmCommonWsSet bpmCommonWsSet) {
		this.bpmCommonWsSet = bpmCommonWsSet;
	}

	public List<BpmCommonWsParams> getBpmCommonWsParamsList() {
		return bpmCommonWsParamsList;
	}

	public void setBpmCommonWsParamsList(
			List<BpmCommonWsParams> bpmCommonWsParamsList) {
		this.bpmCommonWsParamsList = bpmCommonWsParamsList;
	}
	
	
	
}
