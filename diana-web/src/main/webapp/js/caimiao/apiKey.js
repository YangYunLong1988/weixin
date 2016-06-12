/**
 * js加密样例代码 
 ***/
if (typeof ApiKey == 'undefined') {
	ApiKey = {}
}

/**
 * 获取秘钥
 * **/
ApiKey.getKey = function(params,data,secretKey){
	
	var paramsStr = "";

	params.sort();

	for(var i =0 ;i < params.length;i++){
		
		if(paramsStr == "")
			paramsStr += params[i]+"="+data[params[i]];
		else
			paramsStr += "&"+params[i]+"="+data[params[i]];
	}

	return faultylabs.MD5(faultylabs.MD5(secretKey+paramsStr).toLowerCase()+secretKey).toLowerCase();

}
