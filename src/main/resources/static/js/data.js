$(function(){
    $("#uvCaculate").click(_=>caculate("uv"));
    $("#dauCaculate").click(_=> caculate("dau"));
});
function caculate(type){
    const start = $("#"+type+"Start").val();
    const end = $("#"+type+"End").val();
    if(start==null||end==null||start===""||end===""){
        alert("时间起始不能为空");
    }
    $.get(CONTEXT_PATH+"/data/segment"+type.toUpperCase(),{start,end},function (data) {
        console.log(data);
        if(data.code===200){
            console.log(data);
            const resId = type+"Result";
            $("#"+resId).text(data.data[resId]);
        }else{
            data = JSON.parse(data);
            alert(data.msg);
        }
    })
}