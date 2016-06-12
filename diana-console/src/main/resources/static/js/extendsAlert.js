(function($, context) {
	/**
	 * 扩张 window.alert
	 * 
	 * @param info(default:"")
	 *            提示信息支持html
	 * @param title(default:"提示")
	 *            框标题
	 * @param handler(default:function(){})
	 *            回调函数
	 * @param ok(default:"确认")
	 *            按钮说明
	 */
	context.alert = function(info, title, handler, ok) {
		var info = info || "";
		var title = title || "提示";
		var handler = handler || function(r) {
		};
		var ok = ok || "确认";
		var headHtml = $("<div class='modal-header bg-primary'><button type='button' class='close' data-dismiss='modal' aria-label='Close'><span aria-hidden='true'>&times;</span></button><h4 class='modal-title' id='myModalLabel'>" + title + "</h4></div>");
		var contentHtml = $("<div class='modal-body' style='font-size:14px;'>" + info + "</div>");
		var footerHtml = $("<div class='modal-footer' style='text-align:center'><button type='button' class='btn btn-primary'}'>" + ok + "</button></div>");
		$(footerHtml).find("button").unbind().bind('click', function() {
			$(dialog).modal("hide");
		});
		var dialog = $("<div class='modal fade bs-example-modal-sm' tabindex='-1' role='dialog'><div class='modal-dialog modal-sm' role='document' style='margin-top:10%;'><div class='modal-content'></div></div></div>");
		$(dialog).find(".modal-content").append(headHtml).append(contentHtml).append(footerHtml);
		$("html").append(dialog);
		$(dialog).modal("show");
		$(dialog).on('hidden.bs.modal', function(e) {
			handler(true);
			$(dialog).remove();
		});
	};
	/**
	 * 扩张 window.confirm
	 * 
	 * @param info(default:"")
	 *            提示信息支持html
	 * @param title(default:"提示")
	 *            框标题
	 * @param handler(default:function(){})
	 *            回调函数
	 * @param yes(default:确认)
	 *            按钮说明
	 * @param no(default:取消)
	 *            按钮说明
	 */
	context.confirm = function(info, title, handler, yes, no) {
		var info = info || "";
		var title = title || "提示";
		var yesOrno = false;
		var handler = handler || function(r) {
		};
		var yes = yes || "确认";
		var no = no || "取消";
		var headHtml = $("<div class='modal-header bg-primary'><button type='button' class='close' data-dismiss='modal' aria-label='Close'><span aria-hidden='true'>&times;</span></button><h4 class='modal-title' id='myModalLabel'>" + title + "</h4></div>");
		var contentHtml = $("<div class='modal-body' style='font-size:14px;'>" + info + "</div>");
		var footerHtml = $("<div class='modal-footer'><button type='button' class='btn btn-default' data-dismiss='modal'>" + no + "</button>&nbsp;&nbsp;&nbsp;&nbsp;<button type='button' class='btn btn-primary'}'>" + yes + "</button></div>");
		$(footerHtml).find("button:eq(1)").unbind().bind('click', function() {
			$(dialog).modal("hide");
			yesOrno = true;
		});
		var dialog = $("<div class='modal fade bs-example-modal-sm' tabindex='-1' role='dialog'><div class='modal-dialog modal-sm' role='document' style='margin-top:10%;'><div class='modal-content'></div></div></div>");
		$(dialog).find(".modal-content").append(headHtml).append(contentHtml).append(footerHtml);
		$("html").append(dialog);
		$(dialog).modal("show");
		$(dialog).on('hidden.bs.modal', function(e) {
			handler(yesOrno);
			$(dialog).remove();
		});
	};
	/**
	 * 扩张 信息提示
	 * 
	 * @param info(default:"")
	 *            提示信息支持html
	 * @param title(default:"提示")
	 *            框标题
	 * @param time(default:2S)
	 *            显示时间 *
	 * @param handler(default:function(){})
	 *            回调函数
	 */
	context.info = function(info, title, time, handler) {// info 提示信息,
		var info = info || "";
		var title = title || "提示";
		var time = (time || 2) * 1000;
		var handler = handler || function(r) {
		};
		var headHtml = $("<div class='modal-header bg-primary'><button type='button' class='close' data-dismiss='modal' aria-label='Close'><span aria-hidden='true'>&times;</span></button><h4 class='modal-title' id='myModalLabel'>" + title + "</h4></div>");
		var contentHtml = $("<div class='modal-body' style='font-size:14px;'>" + info + "</div>");
		var dialog = $("<div class='modal fade bs-example-modal-sm' tabindex='-1' role='dialog'><div class='modal-dialog modal-sm' role='document' style='margin-top:10%;'><div class='modal-content'></div></div></div>");
		$(dialog).find(".modal-content").append(headHtml).append(contentHtml);
		$("html").append(dialog);
		$(dialog).modal("show");
		$(dialog).on('hidden.bs.modal', function(e) {
			$(dialog).remove();
		});
		setTimeout(function() {
			handler(true);
			$(dialog).modal("hide");
		}, time);
	};
})(jQuery, window);

/**
 * @author dmz @ requires jQuery
 * 
 * 将form表单元素的值序列化成对象
 * 
 * @returns object
 */
var serializeObject = function(form) {
	var o = {};
	$.each(form.serializeArray(), function(index) {
		if (o[this['name']]) {
			o[this['name']] = o[this['name']] + "," + this['value'];
		} else {
			o[this['name']] = this['value'];
		}
	});
	return o;
};
