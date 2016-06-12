/**
 * 
 */

function QueryHelper() {
	this.getParams = function(searchFormId) {
		var params = {};
		var inputList = $(searchFormId).find("input");
		for (var i = 0; i < inputList.length; i++) {
			var input = inputList[i];
			var name = input.name;
			var type = input.type;
			var checked = input.checked;
			if (type == "checkbox" && checked == false) {
				continue;
			}
			if (type == "radio" && checked == false) {
				continue;
			}
			if (name != "") {
				var value = input.value;
				params[name] = value;
			}
		}
		var selectList = $(searchFormId).find("select");
		for (var i = 0; i < selectList.length; i++) {
			var select = selectList[i];
			var name = select.name;
			if (name != "") {
				value = select.value;
				params[name] = value;
			}
		}
		return params;
	};

	// 获取get参数
	this.getGetParams = function(searchFormId) {
		var params = this.getParams(searchFormId);
		var paramsOfGet = "";

		for ( var p in params) {
			if (typeof (params[p]) != "function") {
				paramsOfGet += p + "=" + params[p] + "&";
			}
		}

		paramsOfGet = paramsOfGet.substring(0, paramsOfGet.length - 1);

		return paramsOfGet;
	};
	
	this.hideDiv = function(searchFormId,excludeDiv) {
		var divList = $(searchFormId).children("div");
		$.each(divList,function(i,item) {
			var flag = false;
			$.each(excludeDiv,function(j,jtem){
				if(item["id"] == jtem) {
					flag = true;
					return false;
				}
			}); 
			
			if(flag) {
				$(item).show();
			} else {
				$(item).hide();
			}
		});
	}
}

var queryHelper = null;
$(function() {
	queryHelper = new QueryHelper();
	window.queryHelper = queryHelper;
});