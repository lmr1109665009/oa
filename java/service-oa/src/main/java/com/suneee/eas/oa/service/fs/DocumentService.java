package com.suneee.eas.oa.service.fs;


import com.suneee.eas.common.service.BaseService;
import com.suneee.eas.oa.model.fs.Authorization;
import com.suneee.eas.oa.model.fs.Document;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

public interface DocumentService extends BaseService<Document> {
    String OWNER_TYPE_CREATE="1";
    String OWNER_TYPE_DEL="2";
    String OWNER_TYPE_EDIT="3";
    String OWNER_TYPE_ACCESS="4";
    String OWNER_TYPE_DOWNLOAD="5";
    Long PUBLIC_DOCUMENT_ROOT_ID=1l;
    Long PERSONAL_DOCUMENT_ROOT_ID=2L;
    String DOCUMENT_ROOT_NAME="文档管理";
    List<Document> getDocumentByAuth(String type) throws UnsupportedEncodingException;
    Set<Document> getTreeData(String type) throws UnsupportedEncodingException;

    Set<String> getAllAuthNum(Long docId) throws UnsupportedEncodingException;
    Set<Authorization> getAllAuths(Long docId) throws UnsupportedEncodingException;

    void saveList(List<Document> documentList);

    boolean hasAuth(Document parentId, String ownerTypeCreate) throws UnsupportedEncodingException;

    void delByPath(String dirPath);

    void moveOrCopy(Document newDocument, Document document,int isMove,boolean needAuth);

    String getDirSize(Document document) throws UnsupportedEncodingException;

    List<Document> getByDirPath(String dirPath, Integer isDir, Long UserId);
}