UE.commands['taskopinion'] = {
    execCommand:function (cmd) {
    	var me = this;
    	var html='<div name="editable-input" parser="taskopinionhistory" class="taskopinion" instanceId="#instanceId#"><input type="text" /></div>';
        me.execCommand('insertHtml',html);
    },
    queryCommandState:function () {
        return this.highlight ? -1 : 0;
    }
};

