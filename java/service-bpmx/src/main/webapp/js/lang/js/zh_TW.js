var $lang_js = {
    util : {
        copyToClipboard : {
            netscape : "被浏览器拒绝！\n请在浏览器地址栏输入'about:config'并回车\n然后将'signed.applets.codebase_principal_support'设置为'true'",
            notCopy : '该浏览器不支持自动复制功能!'
        },
        copy : {
            success : '复制成功!'
        }
    },
    customValid : {
        rules : {
            required : "必填",
            number : "请输入一个合法的数字",
            variable : "只能是字母和下划线",
            fields : "首字符为字母,最大长度18",
            minLength : "长度不少于{0}",
            maxLength : "长度不超过{0}",
            rangeLength : "长度必须在{0}之{1}间",
            email : "请输入一合法的邮箱地址",
            url : "请输入一合法的网址",
            date : "请输入一合法的日期",
            digits : "请输入整数",
            equalTo : "两次输入不等",
            range : "请输入在{0}到{1}范围之内的数字",
            maxvalue : "输入的值不能大于{0}",
            minvalue : "输入的值不能小于{0}",
            maxIntLen : "整数位最大长度为{0}",
            maxDecimalLen : "小数位最大长度为{0}",
            dateRangeStart : "开始日期必须小于或等于结束日期",
            dateRangeEnd : "开始日期必须小于或等于结束日期",
            noDigitsStart : "不能以数字开头"
        }
    },
    globalType : {
        menu : {
            addType : "增加分类",
            editType : "编辑分类",
            delType : "删除分类",
            assignRights : '分类权限管理',
            sort : '排序',
            addMy : '添加我的分类'
        }
    },
    bpm : {
        flowUtil : {
            startFlow : {
                success : "启动流程成功!",
                failure : "启动流程失败!",
                confirmRun : "需要启动流程吗?"
            },
            recover : {
                failure : "撤销失败!"
            }
        },
        nodeButton : {
            start : " 启动流程",
            image : " 流程示意图",
            print : "打印",
            saveDraft : "保存草稿",
            saveForm : "保存表单",
            complete : "同意",
            oppose : "反对",
            abstent : "弃权",
            back : "驳回",
            backToStart : "驳回到发起人",
            assignee : "转办",
            addsign : "补签",
            history : "审批历史",
            end : "终止",
            commu : "沟通意见",
            selectOptType : "请选择操作类型"
        },
        formRule : {
            confirmExport : '确认导出吗？ '
        }
    },
    form : {
        commonDialog : {
            aliasNull : "别名为空！",
            aliasError : "输入别名不正确！",
            displayfield : "没有设置显示字段！",
            resultfield : "没有设置结果字段！",
            resultData : "还没有选择数据项！"
        },
        formData : {
            sum : '求和',
            subtract : '求差',
            multiplication : '相乘',
            divide : '相除',
            average : '求平均值',
            max : '求最大值',
            min : '求最小值'
        }
    }
};