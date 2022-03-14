$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");
	//1.取到数据
	var to = $("#recipient-name").val();
	var content = $("#message-text").val();
	$("#hintBody").text("正在发送....");
	$("#hintModal").modal("show");
	$.post(CONTEXT_PATH+"/letter/post",{to:to,content:content},function (data,status) {
		if("success"===status){
			data = $.parseJSON(data);
			if(data.code===200){
				$("#hintBody").text("发送成功！！！");
			}else{
				$("#hintBody").text("发送失败:"+data.msg+",错误码:["+data.code+"]");
			}
		}else{
			$("#hintBody").text("发送失败，请重试！！！");
		}
		setTimeout(function(){
			$("#hintModal").modal("hide");
		}, 2000);

	})

}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}