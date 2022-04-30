$(function(){
	$(".follow-btn").click(follow);
	$(".page-link").click(pageClick);
});
var timer = null;
function follow() {
	var btn = this;
	clearTimeout(timer);
	timer = setTimeout(function () {
		if($(btn).hasClass("btn-info")) {
			// 关注TA
			var userId = Number.parseInt($("#curPageUserId").val());
			$.post(CONTEXT_PATH+"/follow/follow",{entityType:3,entityId: userId,fromUserId:userId},function (data, status) {
				data = 	$.parseJSON(data);
				if(data.code===200){
					$(btn).text("已关注").removeClass("btn-info").addClass("btn-secondary");
					$("#fansCount").text(Number.parseInt($("#fansCount").text())+1);
				}
			})
		} else {
			// 取消关注
			$.post(CONTEXT_PATH+"/follow/unFollow",{entityType:3,entityId:Number.parseInt($("#curPageUserId").val())},function (data, status) {
				data = 	$.parseJSON(data);
				if(data.code===200){
					$(btn).text("关注TA").removeClass("btn-secondary").addClass("btn-info");
					$("#fansCount").text(Number.parseInt($("#fansCount").text())-1);
				}
			})
		}
	},300);
}
function pageClick(e) {
	var userId = $("#userId").val();
	let oldHref = $(e.target).attr("href");
	let href = oldHref+"&userId="+userId;
	$(e.target).attr("href",href);
	return true;
}