/**
 * jquery-datatables工具
 */

/**dataTables错误处理*/
jQuery.fn.dataTableExt.oApi.fnProcessingIndicator = function ( oSettings, onoff )
{
	if ( onoff === undefined ) {
		onoff = true;
	}
	this.oApi._fnProcessingDisplay( oSettings, onoff );
};

//dataTables错误消息提示
function handleAjaxError(xhr, textStatus, error) {
	var str = error.message;
	//session过期
	if (str == "Unexpected token <") {
		location.href = "logout";
		return;
	}

	if (textStatus === 'timeout') {
		alert('服务器请求超时');
	} else {
		alert('服务器请求出现异常!');
	}

	//开启或关闭Datatables的正在处理提醒的消息框
	$('.userTab').dataTable().fnProcessingIndicator(false);
	$('.movie').dataTable().fnProcessingIndicator(false);
	$('.vocal').dataTable().fnProcessingIndicator(false);
	$('.productTab').dataTable().fnProcessingIndicator(false);
}


var MyDataTable = {
		createNew:function() {
			var myDataTable = {};
			var i = 0;//序号
			var temp = 1;//记录次数
			//序号
			myDataTable.getRow = function() {
				return function(){
					var begin = start.settings._iDisplayStart;//开始记录数
					var len = start.settings._iDisplayLength;//每页显示条数
					var total = start.settings._iRecordsTotal;//总记录数
					//console.info(start.settings._iRecordsTotal);
					if (i == 0) {
						i = 1 + begin;
						temp++;
					} else if (temp == len) {
						var idx = i + 1;
						i = 0;
						temp = 1;
						return idx;
					} else {
						i++;
						temp++;
					}

					if (i == total) {//最后一条数据
						i = 0;
						temp = 1;
						return total;
					}

					return i;
				}
			},
			// 国际化配置
			myDataTable.getOLanguage = function() {
				return {
					"sLengthMenu" : "每页显示 _MENU_ 条记录 ",
					"sZeroRecords" : "抱歉， 没有找到",
					"sInfo" : "从 _START_ 到 _END_ /共 _TOTAL_ 条数据",
					"sInfoEmpty" : "",
					"sInfoFiltered" : "(从 _MAX_ 条数据中检索)",
					"sProcessing" : "正在加载中......",
					"sSearch" : "搜索:",
					"oPaginate" : {
						"sFirst" : "首页",
						"sPrevious" : "前一页",
						"sNext" : "后一页",
						"sLast" : "尾页",
					},
					"sZeroRecords" : "没有检索到数据",
					"sProcessing" : "<img src='img/loading.gif' />"//加载图片 
				};
			},
			
			myDataTable.getRender = function() {
				return function(data, type, row) {
					if (data != null) {
						return data;
					} else {
						return "";
					}
				};
			},
			
			// 获取yyyy-MM-dd HH:mm:ss格式日期
			myDataTable.getRenderOfDateYMDHMS = function() {
				return function(data, type, row) {
					if (data != null) {
						return getLocalTime(data);
					} else {
						return "";
					}
				};
			},

			myDataTable.getRenderOfDateYMD = function() {
				return function(data, type, row) {
					if (data != null) {
						return getLocalTimeDate(data);
					} else {
						return "";
					}
				};
			}
			
			return myDataTable;
		}
}
$(function() {
	var myDataTable  = MyDataTable.createNew();
	window.myDataTable = myDataTable;
});