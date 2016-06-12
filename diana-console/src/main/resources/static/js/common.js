function load(api) {
	//做一个过滤器处理服务端超时后302的情况
	$.ajaxSetup({
		dataFilter:function(data, type){
			if(data.indexOf('loginform') !== -1){
				//如果返回的文本包含"登陆页面"，就跳转到登陆页面
				window.location.href = '/login';
				//一定要返回一个字符串不能不返回或者不给返回值，否则会进入success方法
				return "";
			}else{
				//如果没有超时直接返回
				return data;
			}
		}
	});
	
	$.ajax({
		url : api,
		success : function(result) {
			$('#content').html(result);
		}
	});

}