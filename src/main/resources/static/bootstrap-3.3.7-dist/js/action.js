$(document).ready(function () {
    loadHeader();
    loadContent();

    $("#reloadApi").click(function () {
        reloadApi();
    });

});

/**
 * 点击事件
 */
$(document).on("click", ".btn-success", function () {
    var data = $(this).parents(".panel");

    /* ***********************标题头********************/
    var head = data.children(".panel-heading");
    var way = head.children(".label-success").text();
    var url_path = head.children(".url-path").text();

    //基础的地址
    var baseUrll = $(".header-content span").text();

    //请求地址
    var requestUrl = baseUrll + "/" + url_path;


    /* **********************参数********************************/
    var params = {};
    var bodys = data.children(".collapse");
    var trList = bodys.find("tbody").children("tr");
    for (let i = 0; i < trList.length; i++) {
        const tdArr = trList.eq(i).find("td");

        const paramName = tdArr.eq(0).text();//参数名
        if ("无" !== paramName) {
            params[paramName] = tdArr.eq(1).find('input').val();//输入值
        }
    }

    /* **********************发送请求********************************/
    sendRequest(requestUrl, way, params, bodys);

});

/**
 * 发送请求
 * @param pathUrl
 * @param way
 * @param params
 * @param body
 */
function sendRequest(pathUrl, way, params, bodys) {
    $.ajax({
        url: pathUrl,
        type: way,
        data: params,
        dataType: "json",
        success: function (data) {
            var textarea = $(bodys).children(".panel-body").find(".form-group #textareaDiv");
            // textarea.val(JSON.stringify(data));
            textarea.html(parse2(data));
        },
        error: function (data) {
            alert("失败")
        }
    })
    ;
}

function parse2(str) {
    // 设置缩进为2个空格
    str = JSON.stringify(str, null, 2);
    str = str
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;');
    return str.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
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
    });
};


/**
 * 加载接口信息
 */


function reloadApi() {
    $.ajax({
        url: "refresh",
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

function loadContent() {
    $.ajax({
        url: "apis",
        type: "GET",
        data: "",
        dataType: "json",
        success: function (data) {
            putContentDate(data)
        },
        error: function (data) {
            // alert("失败")
        }
    })
    ;
}

/**
 * guid
 * @returns {string}
 */
function uuid() {
    var s = [];
    var hexDigits = "0123456789abcdef";
    for (var i = 0; i < 36; i++) {
        s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
    }
    s[14] = "4";  // bits 12-15 of the time_hi_and_version field to 0010
    s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1);  // bits 6-7 of the clock_seq_hi_and_reserved to 01
    s[8] = s[13] = s[18] = s[23] = "-";
    var uuid = s.join("");
    return uuid;
}

function randomStyle(i) {
    var style = ["panel panel-success", "panel panel-info", "panel panel-warning", "panel panel-danger"];
    return style[i % 5];
}

function random(lower, upper) {
    return Math.floor(Math.random() * (upper - lower + 1)) + lower;
}

function putContentDate(data) {
    var data = data.data;

    var html = "";
    for (let i = 0; i < data.length; i++) {
        var next = data[i];
        html += "<div class=\"panel panel-primary\">";
        html += parseTag(next); //每一個标签tag

        /*每一个接口*/
        for (let j = 0; j < next.values.length; j++) {
            var node = next.values[j];
            html += parseEachApi(node, j);
        }
        html += "</div>";
    }


    $(".container").html(html);

}


/**
 * 每一個接口
 * @param data
 */
function parseEachApi(data, j) {
    var randomID = uuid();
    var html = " <div class=\"" + randomStyle(j) + "\">";
    html += parsePanelHeading(data, randomID);
    html += " <div id=\"" + randomID + "\" class=\"panel-collapse collapse\"> <div class=\"panel-body\"> <table class=\"table table-striped\">";
    html += " <thead> <tr><th>参数名</th><th>值</th><th>参数说明</th><th>是否必传</th><th>参数类型</th></tr></thead><tbody>";
    /*各个参数*/
    if (data.params.length > 0) {
        for (let i = 0; i < data.params.length; i++) {
            var params = data.params[i];
            html += parseParamValue(params);
        }
    } else {
        html += parseParamNoValue();
    }


    html += "  </tbody></table><button  type=\"button\" class=\"btn btn-success\">执行</button>";
    html += " <div class=\"form-group\"><label>结果:</label>  <div id=\"textareaDiv\"></div>  </div> </div></div></div>";
    /* html += " <div class=\"form-group\"><label>结果:</label><textarea class=\"form-control\" rows=\"5\"></textarea></div> </div></div></div>";*/
    return html;
}


/**
 * 每一个参数值
 * @param data
 * @returns {string}
 */
function parseParamValue(data) {
    var html = "<tr> <td>" + data.name + "</td><td> <input type=\"text\" class=\"form-control\" placeholder=\"输入值\" id=\"" + data.name + "\" >" +
        "</td><td>" + data.value + "</td><td>" + data.required + "</td><td>" + data.type + "</td></tr>";
    return html;
}

/**
 * 无参数
 * @param data
 * @returns {string}
 */
function parseParamNoValue() {
    return "<tr> <td>无</td><td> <input type=\"text\"  placeholder=\"输入值\" id=\"无\"</td><td>无</td><td>无</td><td>无</td></tr>";
}

/**
 * 每個接口的头
 * @param data
 * @returns {string}
 */
function parsePanelHeading(data, guid) {
    return " <div class=\"panel-heading\" data-toggle=\"collapse\" data-parent=\"#accordion\" href=\"#" + guid + "\">\n" +
        "                <span class=\"label label-success\">" + data.way + "</span>\n" +
        "                <span class=\"url-path\">" + data.path + "</span>\n" +
        "                <span class=\"path-description\">" + data.descrition + "</span>\n" +
        "            </div>";
}

/**
 * 每一个标签值
 */
function parseTag(data) {
    return " <div class=\"panel-heading\">\n" +
        "            <span class=\"tags\">" + data.name + "</span>\n" +
        "            <span class=\"path-description\">" + data.descrition + "</span>\n" +
        "        </div>";
}


/**
 * 加载头信息
 */
function loadHeader() {
    $.ajax({
        url: "base/info",
        type: "GET",
        data: "",
        dataType: "json",
        success: function (data) {
            putDate(data)
        },
        error: function (data) {
            // alert("失败")
        }

    })
    ;
}

/**
 * 讲数据加载到头部
 * @param data
 */
function putDate(data) {
    var data = data.data;
    $(".header-content h2").text(data.title);

    var net = data.net;
    var port = net.port;
    var url = net.url;
    var name = net.project;

    var path = "";
    path += "http://" + url;
    if (port !== '') {
        path += ":" + port;
    }
    if (name !== null && name !== '') {
        path += "/" + name;
    }

    $(".header-content span").text(path);

    $(".header-content h3").text(data.decribetion);
}








