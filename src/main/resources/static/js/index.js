$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");//关闭编辑框
	var title = $("#recipient-name").val();
	var content =$("#message-text").val();
	var data = {title:title,content:content};
	$("#hintBody").text("正在发布中......");
	$("#hintModal").modal("show");
	if(title.trim().length===0||content.trim().length===0){
		var msg = "发布失败，标题和内容都不能为空！！！";
		afterCompletePublish(false,msg);
	}else{
		$.post(CONTEXT_PATH+"/discuss/post",data,function (data) {
			var success = false;
			if(data!=null){
				data = $.parseJSON(data);
				if(data&&data.code===200){
					success = true;
				}
				var msg = success?data.msg:"【"+data.code+"】:"+data.msg;
				afterCompletePublish(success,msg);
			}else{
				afterCompletePublish(success,"ERROR:服务器异常！！！");
			}
		})
	}
}
function afterCompletePublish(success,msg){
	if(success){
		window.location.reload()//刷新页面
	}
	$("#hintBody").text(msg);
	setTimeout(function(){
		$("#hintModal").modal("hide");
	}, 2000);
}