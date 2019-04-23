<#-- Tab 例子   参数设置必须包含当前tab  “curTab” -->
<div class="widget-box border " >
	<div class="widget-header">
		<h4 class="widget-title"><i class="ht-icon fa fa-bars"></i>${model.title}</h4>
		<div class="widget-toolbar">
          <a href="javascript:void(0);" data-action="prev"   data-placement="bottom" title="上一页">
				<i class="ht-icon fa fa-arrow-left"></i>
			</a>
               <button class="btn btn-xs bigger btn-yellow dropdown-toggle" data-toggle="dropdown">
             每页
            <i class="ace-icon fa fa-chevron-down icon-on-right"></i>
          </button>
          
          <ul class="dropdown-menu dropdown-yellow dropdown-menu-right dropdown-caret dropdown-close">
            <li>
              <a href="javascript:void(0);" data-action="pageSize">5</a>
              <a href="javascript:void(0);" data-action="pageSize">10</a>
               <a href="javascript:void(0);" data-action="pageSize">20</a>
              <a href="javascript:void(0);" data-action="pageSize">50</a>
              <a href="javascript:void(0);" data-action="pageSize">100</a>
            </li>
            </li>

          </ul>
           <a href="javascript:void(0);" data-action="next" 
              data-placement="bottom" title="下一页">
				<i class="ht-icon fa fa-arrow-right"></i>
			</a>
     
														
			<a href="javascript:void(0);" data-action="more" data-url="${model.url}" data-title="${model.title}"  data-placement="bottom" title="更多">
				<i class="ht-icon fa fa-list-alt"></i>
			</a>
			<a href="javascript:void(0);" data-action="reload" data-placement="bottom" title="刷新">
				<i class="ht-icon fa fa-refresh"></i>
			</a>
			<a href="javascript:void(0);" data-action="collapse"  data-placement="bottom" title="折叠">
				<i class="ht-icon fa fa-chevron-up"></i>
			</a>
    
		</div>
	</div>
	<div class="widget-body">
      	<#if data?exists> 
	  	<ul class="nav nav-tabs" >
		  <#list data.indexTabList as d> 
		   	 <li <#if d.active> class="active"</#if> >
           <a data-toggle="tab" href="#${d.key}" data-alias="${d.key}" data-param="curTab">${d.title} <span class="badge badge-danger">${d.badge}</span></a>
          </li>
		  </#list>
      </ul>
      <div class="tab-content">
       	<#list data.indexTabList as d> 	
       		<div class="tab-pane <#if d.active>active</#if>" id="${d.key}">
       			
       			
       			<div  class="widget-scroller" data-height="${model.height}px">
								<#if d.list?exists> 
								<ul class="widget-list list-unstyled"  >
										<#list d.list as list>
												<li class="clearfix" onclick="javascript:jQuery.openFullWindow('${ctx}/platform/bpm/task/toStart.ht?taskId=${list.id}')">
													<div class="pull-left">
														<p>
															</p><h5><strong> ${list.subject}</strong></h5>
													<p></p>
					                                  <p><i class="fa fa-clock-o"></i> <abbr class="timeago" title="">
					                                  <#if d.key == 'pendingMatter'>
					                                     ${list.createTime?string("yyyy-MM-dd HH:mm:ss")}</abbr></p>
					                                  <#else>
					                                  	   ${list.createtime?string("yyyy-MM-dd HH:mm:ss")}</abbr></p>
					                                  </#if>
					                               
																													
												</div>
												<div class="text-right pull-right">
													<h4 class="cost"> ${list.creator}</h4>
													<p>
														<span class="label label-success arrow-in-right"><i class="fa fa-check"></i> 待办</span>
													</p>
												</div>
											</li>
									</#list>
								</ul>
								<#else>
									<div class="alert alert-info">当前没有记录。</div>
								</#if>
							</div>
       		
      		 </div>
      	</#list>
			</div>
      
			<#else>
				<div class="alert alert-info">数据异常</div>
			</#if>
		</div>
	</div>
</div>
