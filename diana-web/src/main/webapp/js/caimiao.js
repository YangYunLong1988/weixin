(function($, context) {
    context.caimiao = {
        login: function(orderId, codes) {
            $.ajax({
                type: "post",
                url: "/movie/getLoginTokenNew",
                data: {
                    orderId: orderId,
                    ticketsIds: codes
                },
                dataType: "json",
                success: function(result) {
                	console.info(result);
                	if(result.success == "false"){
                		common.showAlert(result.message);
                	}else{
                		var action      = result.caimiao_url;
						var company     = result.company;
						var phone       = result.phone;
						var num         = result.num;
						var activityid  = result.activityid;
						var client      = result.client;
						var sign        = result.sign;
						
						//填充表单
						$('[name="company"]').val(company);
						$('[name="phone"]').val(phone);
						$('[name="num"]').val(num);
						$('[name="activityid"]').val(activityid);
						$('[name="client"]').val(client);
						$('[name="sign"]').val(sign);
						//提交到菜苗
						$('#caimiaoForm').attr("action", action);
						$('#caimiaoForm').submit();
                	}
                },
                error: function() {
                    common.showAlert("系统繁忙，请稍后再试。");
                }
            });
        }
    };
})(jQuery, window);