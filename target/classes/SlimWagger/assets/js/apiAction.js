var ajaxUrl = null;
 
$(document).ready(function () {
	
	 setBaseRequestUrl();
	    
     setApiCostTime();
     
     
     $("#api_list").click(function () {
    	 window.location.href="index.html";
     });
     
    
});


/**
 * 显示api花费时间
 * 
 * @returns
 */
function setApiCostTime() {
    $("#api_table").html("");
    	 
    $.ajax({
         url: ajaxUrl+"slimgger/times",
         type: "GET",
         dataType: "json",
         success: function (data) {  
            	eachParams(data.data); 
            }             
        });  	
    }
 
/**
 * 获取当前服务器的请求地址和ip
 */
function setBaseRequestUrl(){ 
	if (ajaxUrl != null){
		return;
	}
	
  　　// 获取当前网址，如：
    var curWwwPath=window.document.location.href;

    // 获取主机地址之后的目录如：/Tmall/index.jsp
    var pathName=window.document.location.pathname;
    var pos=curWwwPath.indexOf(pathName);

    // 获取主机地址，如：//localhost:8080
    var localhostPaht=curWwwPath.substring(0,pos);

    // 获取带"/"的项目名，如：/Tmall
    var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1); 
    
     ajaxUrl = localhostPaht + projectName +"/";
}



/**
 * 参数
 * 
 * @param data
 */
function eachParams(data) {
	var html = "";
	for (let i = 0; i < data.length; i++) {
		 var next = data[i];
		 var api = next.api;
		 var className = next.className;
		 var times = next.times;
		 var cost_time = next.cost_time;
		 var desc = next.desc;
		 
		 if ( !(className == "" || className == undefined || className == null)){
			 html += "<tr><td><h4>"+isNull(api)+"</h4></td><td><h4>"+isNull(desc)+"</h4></td>" +
			 		"<td><h4>"+isNull(className)+"</h4></td><td><h4>"+isNull(times)+"</h4></td><td><h4>"+isNull(cost_time)+"</h4></td></tr>";
		 }
		 
		 
	 }
	
	 $("#api_table").html(html); 
}


function isNull(data){ 
	return (data == "" || data == undefined || data == null) ? "无" : data; 
}

 
                
 


  