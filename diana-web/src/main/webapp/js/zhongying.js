(function($, context) {
    context.zhongying = {
        login: function(orderId, codes) {
            $.ajax({
                type: "post",
                url: "/movie/getLoginUrl",
                data: {
                    orderId: orderId,
                    ticketsIds: codes,
                    agency : 'ZHONGYING'
                },
                dataType: "json",
                success: function(result) {
                    console.info(result);
                    if(result.success == "false"){
                        common.showAlert(result.message);
                    }else{
                        common.goto(result.url);
                    }
                },
                error: function() {
                    common.showAlert("系统繁忙，请稍后再试。");
                }
            });
        }
    };
})(jQuery, window);