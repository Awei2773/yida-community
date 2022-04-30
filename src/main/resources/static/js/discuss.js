/*$.ajax({
        url: CONTEXT_PATH + "/discuss/image/uploadPolicy",
        data: data,
        type:
        success: data => {

        },
        error(){

        }
    });*/
$(function(){
    //点击之后去掉btn-danger改成btn-secondary
    $("#stick").click(stick);
    $("#digest").click(digest);
    $("#delete").click(deletePost);
});
function stick(){
    const postId = $("#postId").val();
    //当前是否已置顶
    const isStick = $("#stick").hasClass("btn-secondary");
    //发起请求进行置顶
    $.ajax({
        url: CONTEXT_PATH + "/discuss/"+postId+"/changePostType",
        data: {type:isStick?0:1},
        type: "PUT",
        success: data => {
            console.log(data);
            toggleBtnClass($("#stick"));
        },
        error(){
            alert("操作失败，网络问题~~~");
        }
    });
}
function digest(){
    const postId = $("#postId").val();
    //当前是否已加精
    const isDigest = $("#digest").hasClass("btn-secondary");
    $.ajax({
        url: CONTEXT_PATH + "/discuss/"+postId+"/toDigest",
        data: {status:isDigest?0:1},
        type: "PUT",
        success: data => {
            console.log(data);
            toggleBtnClass($("#digest"));
        },
        error(){
            alert("操作失败，网络问题~~~");
        }
    });
}
function deletePost() {
    const postId = $("#postId").val();
    $.ajax({
        url: CONTEXT_PATH + "/discuss/"+postId,
        type: "DELETE",
        success: data => {
            if(data.code===200){
                alert(data.msg);
                setTimeout(()=>{
                    location.href = CONTEXT_PATH+"/index";
                },500);
            }
        },
        error(){
            alert("操作失败，网络问题~~~");
        }
    });
}
function toggleBtnClass(btn){
    btn.toggleClass("btn-danger");
    btn.toggleClass("btn-secondary");
    let text = btn.text();
    if(text.indexOf("已")===0){
        //例如：已置顶
        btn.text(text.substring(1,text.length));
    }else{
        btn.text("已"+text);
    }
}