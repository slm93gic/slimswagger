var ajaxUrl = null;
var apis = null;
var title = null;
var tempDatas = null;
var filedDatas = null;

$(document).ready(function () {
	
	setBaseRequestUrl();
	
	setTitle();
	
	// 加载接口列表
     loadApi();
     
      //
     $("#reloadApi").click(function () {
    	 reloadApi();
     });
     
     $("#tipApi").click(function () {
    	 window.location.href="api.html";
     });
     
     
     // 加载注释
     $("#loadExplain").click(function () {
    	 loadExplain();
     });
     
    $(document).click(function(e) { // 在页面任意位置点击而触发此事件
    	var v_id = $(e.target).attr('id');
    	if (v_id != null && v_id.indexOf("/") != -1 ){
    		
    		$("#textareaDiv").html("");
    		$("#request_url").html("");
    		$("#cost_time").html("");

    		tempDatas = null;// 初始化缓存数据
    		    
    		// 加载接口的详细信息
    		var data = apis.data;
    		  for (let i = 0; i < data.length; i++) {
    			  var nexts = data[i];
    			  var nodes = nexts.values;
    			  for (let j = 0; j < nodes.length; j++) {
    				  var node = nodes[j];
    				  if (node.path == v_id){
    					  $("#api_name").html(node.descrition);
    					  $("#api_meth").html('</h4>'+node.way+"</h4>");
    					  $("#api_path").html(node.path);
    					  eachParams(node);
    				  }
    			  }
    		  }
    	}
    	
    })
    
});


/**
 * 解释字段的意思
 * 
 * @returns
 */
function loadExplain() {
    if(tempDatas){
    	$("#textareaDiv").html("");
    	 
    	$.ajax({
            url: ajaxUrl+"api/rest/base/table/field.do",
            type: "POST",
            dataType: "json",
            success: function (data) {  
            	filedDatas = data.data;
            	matchEach(tempDatas);
            	
            	// 将字符串转换成json对象
            	var result = JSON.stringify(tempDatas, null, 2);
            	$("#textareaDiv").html("<h4>"+syntaxHighlight(result)+"</h4>");    
            }             
        });  	
    }
}


/**
 * 生成空格数
 * 
 * @returns
 */
function buildBlack(key,max_length){
	var len = key.length;
	var space = "";
	if (len < max_length){
		for (let i = 0; i < max_length - len; i++) {
			space += " ";	
		}	
	}
	return key + space;
}


/**
 * 匹配数据
 * 
 * @param tempData
 * @param temp
 * @returns
 */
function match(temp){ 
	for (var key in temp){// 遍历键值对
		var value = temp[key];
		if (value && value.constructor === Object){
			if(checkIsMap(value)){
				match(value);		
			}else {
				matchEach(value);
			}
		} else {
			
			for (let i = 0; i < filedDatas.length; i++) {		
				var filedData = filedDatas[i];
				if (filedData.field == key ){
					delete temp[key];
					var desc = filedData.field_desc.replace(/\s+/i,"");
					var new_key = buildBlack(key,25) +"  {<span style='color:#FF00FF'>"+desc+"</span>}";
					temp[new_key] = value;
					break;
				}	 
			}		
			
		}
		
		
	}	
}

/**
 * 判断是不是map
 * 
 * @param datas
 * @returns
 */
function checkIsMap(data){ 
	if (data.code && data.msg){
		return false;
	}
	
	for(key in data){
		var value = data[key];
		return !(value && value.constructor == Object);
 	 
	}

	
}


/**
 * 遍历所有的字段
 * 
 * @param filedDatas
 * @param tempDatas
 * @returns
 */
function matchEach(tempData){ 
	for(key in tempData){
		if ("msg" != key && "totalCount" != key && "pageSize" != key && "pageNo" != key && "code" != key){
			var value = tempData[key];
			 
			if (value && value.length >= 1){ 
				var temp_value = value[0];
				match(temp_value);
				// tempData[key] = temp_value;
				
			}else if(value && value.constructor === Object){
				
				if(checkIsMap(value)){
					match(value);
					
				}else {
					matchEach(value);
				}
	
			} else if(value) {
				match(value);
			}
					
			
		}
	
	}
}
 








/**
 * 获取当前服务器的请求地址和ip
 */
function setBaseRequestUrl(){ 
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
 * 
 * @param json
 */
function syntaxHighlight(json) {
    if (typeof json != 'string') {
        json = JSON.stringify(json, undefined, 2);
    }
    json = json.replace(/&/g, '&').replace(/</g, '<').replace(/>/g, '>');
    return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g,
        function (match) {
            var cls = 'number';
            if (/^"/.test(match)) {
                if (/:$/.test(match)) {
                    cls = 'key';
                } else {
                    cls = 'string';
                }
            } else if (/true|false/.test(match)) {
                cls = 'boolean';
            } else if (/null/.test(match)) {
                cls = 'null';
            }
            return '<span class="' + cls + '">' + match + '</span>';
        }
    );
}

/**
 * 
 */
function reloadApi() {
    $.ajax({
        url: ajaxUrl+"slimgger/refresh",
        type: "GET",
        data: "",
        dataType: "json",
        success: function (data) {
            location.reload();
        },
        error: function (data) {
            // alert("失败")
        }
    })
    ;
}

/**
 * 点击事件
 */
$(document).on("click", ".btn-primary", function () {	 
 var data = $(this).parents(".card");

$("#textareaDiv").html("");
$("#request_url").html("");
$("#cost_time").html("");
       
var url_path =  data.find(".card-header .url-path").text();
    
if(url_path == '无'){
    return;
}
    
var way =    data.find(".card-header #api_meth").text();
var title =  data.find(".card-header #api_name").text();
    
var requestUrl = ajaxUrl + "api/rest" + url_path;// 请求地址
 
 
 /* **********************参数******************************* */
  var params = {};
  var bodys  = data.find(".card-body .table-striped");
  var trList = bodys.find("tbody").children("tr");
   
 for (let i = 0; i < trList.length; i++) {
	 const tdArr = trList.eq(i).find("td");
	 const paramName = tdArr.eq(0).text();// 参数名
	 
	 var flag = tdArr.eq(3).text().indexOf("必填") != -1 ;
	 if ( paramName ) {
		var value =  tdArr.eq(1).find('input').val();// 输入值
		
		if (flag && !value){	 
			$("#"+paramName+"").css("color","red");
			$("#"+paramName+"").css("font-size","25px");

			
			return; 	
		}else {
			$("#"+paramName+"").css("color","black");
			$("#"+paramName+"").css("font-size","18px");
			params[paramName] = tdArr.eq(1).find('input').val();// 输入值
		}
 	}
 }
 
// /* **********************发送请求******************************* */
 sendRequest(requestUrl, way, params, data,url_path, title);

});


/**
 * 发送请求
 * 
 * @param pathUrl
 * @param way
 * @param params
 * @param body
 */
function sendRequest(pathUrl, way, params, bodys, url_path, title) { 
	// 查询的接口链接
$(bodys).find("#request_url").html("<h4>"+getUrl(pathUrl,params)+"</h4>");

var textarea = $(bodys).find("#textareaDiv");
    
var startTime = null;
var endTime = null;
     
    $.ajax({
        url: pathUrl,
        type: "POST",
        data: params,
        dataType: "json",
        beforeSend: function () {
        	 startTime = new Date().getTime();
        	 $("#loadingDiv").show();
        },
        
        success: function (data) {  
        	var tempData = data.data;
        	if(tempData != null && tempData.length > 150){
        		data.data = tempData.slice(0,149)
        	}
        	
        	// 将查询数缓存起来
        	tempDatas = null;// 初始化缓存数据
        	tempDatas = data;
        	
        	// 将字符串转换成json对象
        	 var result = JSON.stringify(data, null, 2);
        	 textarea.html("<h4>"+syntaxHighlight(result)+"</h4>");   	
        	 
        	 if (pathUrl.indexOf("user/login.do") != -1 && data.code != 1){
        		 localStorage.setItem("token",data.data.token); 
        		 
        	 } else if(pathUrl.indexOf("/user/logout.do") != -1 && data.code != 1){
        		 localStorage.removeItem("token"); 
        		  ajaxUrl = null;
        		  apis = null;
        		  title = null;
        		  tempDatas = null;
        		  filedDatas = null;
        	 }
        	 
        },
        complete: function () {
        	 endTime = new Date().getTime();
        	 $("#loadingDiv").hide();
        	 $("#cost_time").html(formatDuring(endTime - startTime));	 
        	 sendToContainer( url_path, title, endTime - startTime);
        },
        error: function (data) {
        	 textarea.html("无权限，请重新登录再试");
        }
       
    })
    ;
}

/**
 * ms 转换为时间
 * 
 * @param mss
 * @returns {String}
 */
function formatDuring(mss) {
	 var desc = "";
	 var seconds = (mss % (1000 * 60)) / 1000;
	 var minutes = parseInt((mss % (1000 * 60 * 60)) / (1000 * 60));
	 if (minutes == 0){
		 return  seconds + " 秒 ";
	 }
    return  minutes + " 分钟 " + seconds + " 秒 ";
}


function getUrl(url, data){
	var param = getParam(data);
	if (param != ''){
		return url += (url.indexOf('?') < 0 ? '?' : '') + param;
	}else {
		return url;
	}
} 


function getParam(data){
    let url = '';
    for(var k in data){
        let value = data[k] !==undefined ? data[k] : '';
        url += `&${k}=${encodeURIComponent(value)}`
    }
    return url ? url.substring(1) : ''
}


/**
 * 参数
 * 
 * @param data
 */
function eachParams(data) {
	var data = data.params;
	var html = "";
	 for (let i = 0; i < data.length; i++) {
		 var next = data[i];
		 var typeName="";
		 if(next.type=='Double' || next.type == 'Float'){
			 typeName = '多精度数字';
		 }else  if(next.type=='Integer' || next.type == 'int'){
			 typeName = '数字';
		 }else if(next.type=='String'){
			 typeName = '字符';
		 } else if(next.type=='Date') {
			 typeName = '时间';
		 }else {
			 typeName = next.type;
		 }
		 
		 var nextValue = next.value
		 if (next.name == 'token'){
			 nextValue = localStorage.getItem("token");
			 if(!nextValue){
				 nextValue = '未登录';
			 }
		 }
		 	 
		 html = html + '<tr>'
			+'<td><h5 id="'+next.name+'" style="width:158px">'+next.name+'</sapn></td>'
			+'<td><input type="text" style="width:250px" class="form-control" placeholder="输入值" id="mail" name="email" value = "'+nextValue+'"></td>'
			+'<td><h5>'+next.note +'</h5></td>'
			+'<td><h5 style="width:80px">'+(next.required ? '必填 <apn style="color:red"> *</apn>':'选填') +'</h5></td>'
			+'<td><h5 style="width:80px">'+typeName+'</h5></td>'
			+'</tr>'
		+'<tr>';
	 }
	 $("#api_params").html(html);
	 
}



// 加载接口列表
function loadApi() {
    $.ajax({
        url: ajaxUrl+"slimgger/apis",
        type: "GET",
        data: "",
        dataType: "json",
        success: function (data) {
        	apis = null;
        	apis = data;
            putContentDate(data)
 
        },
        error: function (data) {
           
        }
    })    
}


function sendToContainer(url_path, meth_desc, time){
	var dataParam = {
			"meth_desc":meth_desc,
			"path":url_path,
			"time":time
	};
	
	 $.ajax({
	        url: ajaxUrl+"slimgger/insert/times",
	        type: "GET",
	        data: dataParam,
	        dataType: "json",
	        success: function (data) {
	        	
	        },
	        error: function (data) {
	           
	        }
	    })    
	
}





// 加载接口列表
function setTitle() {
    $.ajax({
        url: ajaxUrl+"slimgger/base/info",
        type: "GET",
        data: "",
        dataType: "json",
        success: function (data) {
        	var data = data.data;
        	$("#page-title").html(data.title);
        	title = data.description;
        },
        error: function (data) {
           
        }
    })    
}

var index = 1;

/**
 * 加载每一个方法
 * 
 * @param data
 * @param html
 * @returns
 */
 function eachAPi(data,html) {
     var data = data.values;
     for (let i = 0; i < data.length; i++) {
    	 var next = data[i];
    	 html = html + '<li><a href= "#" id= "'+next.path+'">'+index +"、"+next.descrition+'</a></li>';  	  
    	 index ++;
     }
     return html;
 }

 

// 拼接格式
function putContentDate(data) {
    var data = data.data;

    var html =  getTitle();
    
    for (let i = 0; i < data.length; i++) {
        var next = data[i]; 
        html = html + ' <li class="">'
        			+ '<a href="#title'+i+'" class="accordion-toggle wave-effect" data-toggle="collapse" '
        			+ 'aria-expanded="false"><i class="fa fa-paper-plane-o"></i>&nbsp;&nbsp;&nbsp;[ '+(i+1) +" ]&nbsp;&nbsp;&nbsp;"+ next.name +'</a>'
        			+' <ul class="collapse list-unstyled" id="title'+i+'" data-parent="#accordion">';
        
        html = eachAPi(next,html); 
        html = html  +'</ul></li>';

    }
    
    $("#accordion").html(html);

}



 /**
	 * 标题
	 * 
	 * @param data
	 * @param html
	 * @returns
	 */
 function getTitle() {
	return  '<div class="user-profile">'
			+'<div class="dropdown user-pro-body">'
			+'<div class="mb-2"><a href="#" class="" data-toggle="" aria-haspopup="true" aria-expanded="false">'
			+' <span class="font-weight-semibold"><h4>'+title+'</h4></span>  </a>'
			+'</div>'
			+'</div>'
			+'</div>';
 }

                     
                  
 


  