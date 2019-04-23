/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: DocumentServcieImpl
 * Author:   lmr
 * Date:     2018/9/30 17:00
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.eas.oa.service.fs.impl;


import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.service.impl.BaseServiceImpl;
import com.suneee.eas.common.utils.BeanUtils;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.IdGeneratorUtil;
import com.suneee.eas.oa.dao.fs.DocumentDao;
import com.suneee.eas.oa.model.fs.Authorization;
import com.suneee.eas.oa.model.fs.Document;
import com.suneee.eas.oa.service.fs.AuthService;
import com.suneee.eas.oa.service.fs.DocumentService;
import com.suneee.eas.oa.service.user.SysOrgService;
import com.suneee.eas.oa.service.user.SysRoleService;
import com.suneee.platform.model.system.SysOrg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author lmr
 * @create 2018/9/30
 * @since 1.0.0
 */
@Service
public class DocumentServcieImpl extends BaseServiceImpl<Document> implements DocumentService {
    private static final Logger LOGGER = LogManager.getLogger(DocumentServcieImpl.class);
    public static final String OWNER_TYPE_USER = "user";
    public static final String OWNER_TYPE_ROLE = "role";
    public static final String OWNER_TYPE_ORG = "org";
    private DocumentDao documentDao;
    @Autowired
    public void setDocumentDao(DocumentDao documentDao) {
        this.documentDao = documentDao;
        setBaseDao(documentDao);
    }
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysOrgService sysOrgService;
    @Autowired
    private AuthService authService;

    /**
     * 获取所有有权限的文件
     * @param type
     * @return
     * @throws UnsupportedEncodingException
     */
    @Override
    public List<Document> getDocumentByAuth(String type) throws UnsupportedEncodingException {
      Set<Authorization> auths = getAllAuths(null);
        //通过权限对象获取所有的文件夹，通过Set去重
        Set<Long> docSet = new HashSet<>();
        //如果不是访问权限，则需要匹配，拥有该权限的则添加
        if(!OWNER_TYPE_ACCESS.equals(type)){
            for (Authorization authTemp:auths) {
                if(authTemp.getType().contains(type)){
                    docSet.add(authTemp.getDocId());
                }
             }
             //不管拥有什么权限都会有默认拥有访问权限。所以全部添加
        }else{
            for (Authorization authTemp:auths) {
                    docSet.add(authTemp.getDocId());
            }
        }
        //转为数组作为参数查询所有的文件夹。
        Long[] docIds = new Long[docSet.toArray().length];
        for(int i=0;i<docSet.toArray().length;i++){
            docIds[i]=Long.parseLong(docSet.toArray()[i].toString());
        }
        Map<String,Object> params = new HashMap<>();
        params.put("ids",docIds);
        params.put("enterpriseCode",ContextSupportUtil.getCurrentEnterpriseCode());
        List<Document> documentList = documentDao.findByIds(params);
        return documentList;
    }

    /**
     * 获取左侧树结构
     * @return
     * @throws UnsupportedEncodingException
     * @param type
     */
    public Set<Document> getTreeData(String type) throws UnsupportedEncodingException {
        //获取该用户拥有权限的所有文件夹；
        List<Document> documentList = getDocumentByAuth(type);
        Set<Document> documentSet = new HashSet<>();
        //获取该用户拥有权限的所有文件夹以及父节点的所有有的文件夹构建左侧边栏的树结构
        for(Document document:documentList){
            String[] parentIds = document.getDirPath().split("\\.");
            Long[] ids = new Long[parentIds.length];
            for (int i =0;i<parentIds.length;i++) {
                ids[i]=Long.parseLong(parentIds[i]);
            }
            List<Document> documentsTemp = new ArrayList<>();
            if(ids.length!=0){
                Map<String,Object> params = new HashMap<>();
                params.put("ids",ids);
                params.put("enterpriseCode",ContextSupportUtil.getCurrentEnterprise());
                documentsTemp = documentDao.findByIds(params);
            }
            //用Set去重
            if(BeanUtils.isNotEmpty(documentsTemp)){
                documentSet.addAll(documentsTemp);
            }
        }
        return documentSet;
    }

    /**
     * 获取父文件夹的所有权限值
     * @param docId
     * @return
     */
    @Override
    public Set<String> getAllAuthNum(Long docId) throws UnsupportedEncodingException {
        Set<String> authNumSet = new HashSet<>();
        Set<Authorization> authorizationSet = getAllAuths(docId);
        for (Authorization auth:authorizationSet) {
            String authStr = auth.getType();
            String[] authNums = authStr.split(",");
            for(int i = 0;i<authNums.length;i++){
                authNumSet.add(authNums[i]);
            }
        }
        return authNumSet;
    }

    /**
     * 获取父文件夹的所有权限（权限对象）
     * @param docId
     * @return
     * @throws UnsupportedEncodingException
     */
    @Override
    public Set<Authorization> getAllAuths(Long docId) throws UnsupportedEncodingException {
        Long userId = ContextSupportUtil.getCurrentUserId();
        String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
        List<Long> roleIds = sysRoleService.getRoleIdsByUserId(userId, enterpriseCode);
        List<SysOrg> sysOrgList = sysOrgService.getSysOrgList(userId, enterpriseCode);
        Set<Authorization> auths =  new HashSet<>();
        //获取该用户的用户权限
        List<Authorization> authByUser = authService.getByOwnerId(userId,OWNER_TYPE_USER,docId);
        if(BeanUtils.isNotEmpty(authByUser)){
            auths.addAll(authByUser);
        }
        //获取该用户的角色权限
        List<Authorization> authByRole = authService.getByOwnerIds(roleIds,OWNER_TYPE_ROLE);
        if(BeanUtils.isNotEmpty(authByRole)){
            auths.addAll(authByRole);
        }

        //获取该用户的组织权限
        List<Long> OrgIds = new ArrayList<>();
        if(BeanUtils.isNotEmpty(sysOrgList)){
            for (SysOrg sysOrg:sysOrgList) {
                String [] idStr = sysOrg.getPath().split(".");
                for(int i =0;i<idStr.length;i++){
                    OrgIds.add(Long.parseLong(idStr[i]));
                }
            }
            List<Authorization> authByOrg = authService.getByOwnerIds(OrgIds,OWNER_TYPE_ORG);
            if(BeanUtils.isNotEmpty(authByOrg)){
                auths.addAll(authByOrg);
            }
        }
        return auths;
    }

    @Override
    public void saveList(List<Document> documentList) {
        String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
        Long userId= ContextSupportUtil.getCurrentUserId();
        String userName = ContextSupportUtil.getCurrentUsername();
        Long parentId = documentList.get(0).getParentId();
        for (int i = 0; i < documentList.size(); i++) {
            Document document = documentList.get(i);
            //判断是否重名并且如果重名则重新生成新的文件名
            document.setEnterpriseCode(enterpriseCode);
            document.setId(IdGeneratorUtil.getNextId());
            //如果是新建的文件夹。则用户本人拥有该文件夹的所有权限
            if(document.getIsDir()==1){
                Authorization auth = new Authorization();
                auth.setDocId(document.getId());
                auth.setOwnerId(userId);
                auth.setOwnerName(userName);
                auth.setOwnerType(OWNER_TYPE_USER);
                auth.setType("1,2,3,4,5");
                authService.save(auth);
            }
            //parentId==1说明是根目录。
            if(parentId==PUBLIC_DOCUMENT_ROOT_ID){
                document.setDirPath(PUBLIC_DOCUMENT_ROOT_ID+"."+document.getId()+".");
                document.setDirPathName(DOCUMENT_ROOT_NAME+"/"+document.getName());
            }else {
                Document parentDoc = documentDao.findById(parentId);
                document.setDirPath(parentDoc.getDirPath() + document.getId() + ".");
                document.setDirPathName(parentDoc.getDirPathName() + "/" + document.getName());
            }
            document.setReadNum(0);
            document.setCreateBy(userId);
            document.setCreateByName(userName);
            Date date = new Date();
            document.setCreateTime(date);
            document.setUpdateBy(userId);
            document.setCreateByName(userName);
            document.setUpdateTime(date);
            setNoRepeatName(document,document.getName(),1);
        }
        documentDao.saveList(documentList);
    }

    /**
     * 判断该文件夹是否拥有所传参数值的权限
     * @param document
     * @param type
     * @return
     * @throws UnsupportedEncodingException
     */
    public boolean hasAuth(Document document,String type) throws UnsupportedEncodingException {
        Long id = null;
        //如果是文件夹，并且是更改则用本身的id查权限，如果是新增则用父文件夹的id查权限
        if(document.getIsDir()==1&&document.getId()!=null){
            id = document.getId();
        }else{
            //如果是文件则用父文件夹的id查权限
            id = document.getParentId();
        }
        Set<String> allAuthNum = getAllAuthNum(id);
        if(allAuthNum.contains(OWNER_TYPE_CREATE)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 保存
     * @param document
     */
    @Override
    public int save(Document document) {
        Long parentId = document.getParentId();
        Long userId = ContextSupportUtil.getCurrentUserId();
        String userName = ContextSupportUtil.getCurrentUsername();
        String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
        document.setId(IdGeneratorUtil.getNextId());
        //parentId==1说明是根目录。
        if(parentId==PUBLIC_DOCUMENT_ROOT_ID){
            document.setDirPath(PUBLIC_DOCUMENT_ROOT_ID+"."+document.getId()+".");
            document.setDirPathName(DOCUMENT_ROOT_NAME+"/"+document.getName());
        }else {
            Document parentDoc = findById(parentId);
            document.setDirPath(parentDoc.getDirPath() + document.getId() + ".");
            document.setDirPathName(parentDoc.getDirPathName() + "/" + document.getName());
        }
        document.setReadNum(0);
        document.setEnterpriseCode(enterpriseCode);
        document.setCreateBy(userId);
        document.setCreateByName(userName);
        document.setUpdateBy(userId);
        document.setUpdateByName(userName);
        //获取当前时间
        Date date = new Date();
        document.setCreateTime(date);
        document.setUpdateTime(date);
        setNoRepeatName(document,document.getName(),1);
        return documentDao.save(document);
    }

    /**
     * 删除子文件
     * @param dirPath
     */
    @Override
    public void delByPath(String dirPath) {
        Map<String,Object> params = new HashMap<>();
        params.put("updateBy",ContextSupportUtil.getCurrentUserId());
        params.put("updateByName",ContextSupportUtil.getCurrentUsername());
        params.put("enterpriseCode",ContextSupportUtil.getCurrentEnterpriseCode());
        Date date = new Date();
        params.put("updateTime",date);
        params.put("dirPath",dirPath);
        documentDao.delByPath(params);
    }

    /**
     * 通过id软删除
     * @param id
     */
    public void delById(Long id) {
        Map<String,Object> params = new HashMap<>();
        params.put("updateBy",ContextSupportUtil.getCurrentUserId());
        params.put("updateByName",ContextSupportUtil.getCurrentUsername());
        Date date = new Date();
        params.put("updateTime",date);
        params.put("id",id);
        documentDao.delById(params);
    }

    /**
     * 通过路径获取所有的文件
     * @param dirPath
     * @param isDir
     * @param userId
     * @return
     */
    public List<Document> getByDirPath(String dirPath, Integer isDir, Long userId) {
        Map<String,Object> params = new HashMap<>();
        params.put("dirPath",dirPath);
        params.put("enterpriseCode",ContextSupportUtil.getCurrentEnterpriseCode());
        if(isDir!=null){
            params.put("isDir",isDir);
        }
        if(userId!=null){
            params.put("createBy",userId);
        }
        return documentDao.getByDirPath(params);
    }

    /**
     * 更新
     * @param document
     * @return
     */
    @Override
    public int update(Document document) {
        Long userId = ContextSupportUtil.getCurrentUserId();
        String userName = ContextSupportUtil.getCurrentUsername();
        Document oldDocument = findById(document.getId());
        Date date = new Date();
        document.setEnterpriseCode(oldDocument.getEnterpriseCode());
        document.setUpdateTime(date);
        document.setUpdateByName(userName);
        document.setUpdateBy(userId);
        setNoRepeatName(document,document.getName(),1);
        //如果是名字更改后。其子文件的路径名的名字也要改
        if(!document.getName().equals(oldDocument.getName())){
            List<Document> documentList = getByDirPath(oldDocument.getDirPath(),null, null);
            for(Document docTemp : documentList){
                docTemp.getDirPathName().replace(oldDocument.getName(),document.getName());
                documentDao.update(docTemp);
            }
        }
        return documentDao.update(document);
    }


    /**
     * 通过id删除
     * @param id
     * @return
     */
    @Override
    public int deleteById(Long id) {
       Document document = findById(id);
        document.setIsDelete((byte) 1);
        return update(document);
    }

    /**
     * 移动或复制
     * @param newDocument
     * @param document
     * @param isMove
     */
    public void moveOrCopy(Document newDocument, Document document,int isMove,boolean needAuth){
        Long id = document.getId();
        document.setDirPath(newDocument.getDirPath()+id+".");
        document.setDirPathName(newDocument.getDirPathName()+"/"+document.getName());
        document.setReadNum(0);
        document.setParentId(newDocument.getId());
        if(isMove==1){
            update(document);
        }else{
            if(document.getIsDir()==1&&needAuth){
                //如果需要权限，权限也需要复制
                List<Authorization> authorizationList = authService.getByDocId(document.getId());
                if(BeanUtils.isNotEmpty(authorizationList)){
                    for(Authorization auth : authorizationList){
                        auth.setDocId(document.getId());
                        authService.save(auth);
                    }
                }
            }
            save(document);

        }
        if(document.getIsDir()==1){
            List<Document> documentList = getByParentId(id);
            if(documentList.size()==0){
                return ;
            }
            for(Document docTemp:documentList){
                this.moveOrCopy(document,docTemp,isMove,needAuth);
            }
        }

    }

    /**
     * 通过parentId获取子文件
     * @param id
     * @return
     */
    public List<Document> getByParentId(Long id) {
        Map<String,Object> params = new HashMap<>();
        params.put("id",id);
        params.put("enterpriseCode",ContextSupportUtil.getCurrentEnterprise());
        return documentDao.getByParentId(params);
    }

    /**
     * 获取文件夹的size
     * @param document
     * @return
     * @throws UnsupportedEncodingException
     */
    public String getDirSize(Document document) throws UnsupportedEncodingException {
        QueryFilter queryFilter = new QueryFilter("listAll");
        //获取所有有权限的文件夹，通过条件筛选（文件必须在有权限的文件夹下，并且在document该文件夹下的所有文件），将这些文件的size相加
        List<Document> documentList = getDocumentByAuth(DocumentService.OWNER_TYPE_ACCESS);
        Long[] docIds = new Long[documentList.size()];
        for (int i =0;i<documentList.size();i++) {
            docIds[i]=documentList.get(i).getId();
        }
        queryFilter.addFilter("parentIds",docIds);
        queryFilter.addFilter("isDir",0);
        queryFilter.addFilter("dirPath",document.getDirPath());
        List<Document> documents = getListBySqlKey(queryFilter);
        Double size=0.0;
        for(Document doc:documents){
            String s = doc.getSize().substring(0,doc.getSize().indexOf("M"));
            Double sizeTemp = Double.parseDouble(s);
            size+=sizeTemp;
        }
        return size+"M";
    }

    /**
     * 重命名文件，有重复的文件名自动添加1，没有重复直接返回
     * @param document
     */

    private void setNoRepeatName(Document document, String oldName, int pos) {

        int count = this.countNameRepetition(document);
        String newName = "新文件";
        if (count == 0) {
            document.getDirPathName().replace(oldName,document.getName());
            return;
        }
        int extPos = document.getName().lastIndexOf(".");
        if (extPos == -1) {
            newName = oldName + "(" + pos + ")";
        } else {
            newName = oldName.substring(0, extPos) + "(" + pos + ")" + oldName.substring(extPos);
        }
        document.setName(newName);
        pos++;
       setNoRepeatName(document,oldName, pos);

    }

    /**
     * 计算重复文件名的个数
     * @param document
     * @return
     */
    private int countNameRepetition(Document document) {
        return documentDao.countNameRepetition(document);
    }
}