package com.suneee.oa.component;

/**
 * 流程导入holder
 */
public class BpmImportHolder {
    /**
     * 流程定义默认分类holder
     */
    private final static ThreadLocal<GlobalTypeHolder> definitionDefaultTypeHolder=new ThreadLocal<GlobalTypeHolder>();
    /**
     * 定义字段默认分类holder
     */
    private final static ThreadLocal<GlobalTypeHolder> tableDefaultTypeHolder=new ThreadLocal<GlobalTypeHolder>();
    /**
     * 自定义表单默认分类holder
     */
    private final static ThreadLocal<GlobalTypeHolder> formDefaultTypeHolder=new ThreadLocal<GlobalTypeHolder>();

    private final static ThreadLocal<Long> tableIdHolder=new ThreadLocal<Long>();

    public static ThreadLocal<GlobalTypeHolder> getDefinitionDefaultTypeHolder() {
        return definitionDefaultTypeHolder;
    }

    public static ThreadLocal<GlobalTypeHolder> getTableDefaultTypeHolder() {
        return tableDefaultTypeHolder;
    }

    public static ThreadLocal<GlobalTypeHolder> getFormDefaultTypeHolder() {
        return formDefaultTypeHolder;
    }

    public static ThreadLocal<Long> getTableIdHolder() {
        return tableIdHolder;
    }
}
