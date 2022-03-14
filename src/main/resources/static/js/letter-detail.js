$(function () {
    var clientId = $("#clientId").val();
    var otherId = $("#otherId").val();
    var user = {headerUrl: $("#userHeaderUrl").val(), id: $("#userId").val()-0, username: $("#user-username").val()};
    var other = {headerUrl: $("#otherHeaderUrl").val(), username: $("#other-username").val()};
    var conversationId = Math.min(clientId, otherId) + "_" + Math.max(clientId, otherId);
    //1.初始化滚动条
    var bs = initialScrollBar(conversationId, user, other);
    //2.SSE服务，用于即时通信
    openSseService( conversationId,bs,user, other);


})

function initialScrollBar(conversationId, user, other) {
    var bs = BetterScroll.createBScroll('.wrapper',
        {
            pullUpLoad: true,
            /*wheel: {
                wheelWrapperClass: 'wheel-scroll',
                wheelItemClass: 'wheel-item',       wheel和mouseWheel搭配出现滚动不彻底的问题
                rotate: 0,
                adjustTime: 400,
                selectedIndex: 0,
                wheelDisabledItemClass: 'wheel-disabled-item'
            },*/ scrollY: true,
            scrollbar: {
                fade: true,
                interactive: false,
                // 以下配置项 v2.2.0 才支持
                customElements: [],
                minSize: 8,
                scrollbarTrackClickable: false,
                scrollbarTrackOffsetType: 'step',
                scrollbarTrackOffsetTime: 300,
                // 以下配置项 v2.4.0 才支持
                fadeInTime: 250,
                fadeOutTime: 500
            },
            mouseWheel: {
                speed: 20,
                invert: false,
                easeTime: 300,
                discreteTime: 400,
                throttleTime: 0,
                dampingFactor: 0.1
            }
        })
    //上拉更新实现
    var times = 1;
    bs.on("pullingUp", function () {
        var offset = $("#letter-content li").length;
        if(--times===0){
            $.get("/letter/friend/"+conversationId+"/ajax",{offset:offset},function(data,status){
                if(status==="success"){
                    data = $.parseJSON(data);
                    var list = data.data.messageList;
                    if(data.code===200&&list!=null&&list.length>0){
                        $.each(list,function(index,ele){
                            $("#letter-content").append(getLetterLi(ele,user,other));
                        })
                        times = 1;
                    }else{
                        times=10;
                    }
                }else{
                    times = 10;
                }
                //更新完要refresh一下，并且finishPullUp下次才能继续下拉更新
                bs.refresh(true);
                bs.finishPullUp();
            })
        }else{
            bs.finishPullUp();
        }

    })
    return bs;
}

function openSseService(conversationId,bs,user, other) {

    if(eventSource!==null){
        eventSource.addEventListener(conversationId,function (e) {
            if(e.data!==undefined){
                var data = $.parseJSON(e.data);
                console.log(data);
                console.log(user);
                console.log(other);
                if(data!=null&&data.code===200&&data.data!==null&&data.data.message!==null){
                    $("#letter-content").prepend(getLetterLi(data.data.message,user, other));
                    //refresh
                    bs.refresh();
                }
            }
        })
    }
}

function getLetterLi(message, user, other) {
    console.log("user",user)
    console.log("other",other)
    return $("<li class=\"media pb-3 pt-3 mb-2\" >" +
        "						<a href=\"profile.html\">" +
        "							<img src=\"" + (message.fromId === user.id ? user.headerUrl : other.headerUrl) + "\"" +
        "                                 class=\"mr-4 rounded-circle user-header\" alt=\"用户头像\" >" +
        "						</a>" +
        "						<div class=\"toast show d-lg-block\" role=\"alert\" aria-live=\"assertive\" aria-atomic=\"true\">" +
        "							<div class=\"toast-header\">" +
        "								<strong class=\"mr-auto\">" + (message.fromId === user.id ? user.username : other.username) + "</strong>" +
        "								<small>" + (new Date(message.createTime).Format("yyyy-MM-dd HH:mm:ss")) + "</small>" +
        "								<button type=\"button\" class=\"ml-2 mb-1 close\" data-dismiss=\"toast\" aria-label=\"Close\">" +
        "									<span aria-hidden=\"true\">&times;</span>" +
        "								</button>" +
        "							</div>" +
        "							<div class=\"toast-body " + (message.fromId === user.id ? 'bg-v3-success' : 'bg-v3-info') + "\" >" +
        "								" + (message.content) +
        "							</div>" +
        "						</div>" +
        "					</li>");
}