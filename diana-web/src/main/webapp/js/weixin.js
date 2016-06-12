(function($, context) {
    var prePayData = null;
    context.weixin = {
        clear: function() { //清空订单数据
            prePayData = null;
        },
        submitOrder: function(params) { //提交订单
            if (prePayData == null) {
                var next = function() {
                    common.post("/product/submitOrder", params, function(result) {
                        prePayData = result.data;
                        //如果是抵扣券全额支付，则直接跳转到订单详情
                        if (prePayData.payType && prePayData.payType == "CARDSTOCK_PAY_SUCCESS") {
                            context.weixin.gotoOrder(prePayData.orderId);
                            return;
                        }

                        if (common.getDevice() == "JUPITER") {
                            if (typeof context.WeixinJSBridge == "undefined") {
                                var onBridgeReady = function() {
                                    context.weixin.requestWeixinPay();
                                };
                                if (document.addEventListener) {
                                    document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
                                } else if (document.attachEvent) {
                                    document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
                                    document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
                                }
                            } else {
                                context.weixin.requestWeixinPay();
                            }
                        } else {
                            context.weixin.requestWeixinPay();
                        }
                    }, null, function(result) {
                        common.showAlert(result.firstMessage);
                    });
                };
                if (common.getDevice() == "PROMETHEUS") {
                    common.checkAppVersion("3.1.5", next);
                } else {
                    next();
                }
            } else {
                context.weixin.requestWeixinPay();
            }
        },
        requestWeixinPay: function() { //请求微信支付
            if (common.getDevice() == "PROMETHEUS") {
                context.callback = function(data) {
                    var r = eval('(' + data + ')');
                    if (r.weixinPay == "ok") {
                        context.weixin.onSuccess(r.orderId);
                    } else {
                        context.weixin.checkPayStatus(r.orderId);
                    }
                };
                common.callApp("event", {
                    event: "payWeixin",
                    json: prePayData
                });
            } else if (common.getDevice() == "JUPITER") {
                context.WeixinJSBridge.invoke(
                    'getBrandWCPayRequest', {
                        "appId": prePayData.appId, //公众号名称，由商户传入     
                        "timeStamp": prePayData.timeStamp, //时间戳，自1970年以来的秒数     
                        "nonceStr": prePayData.nonceStr, //随机串     
                        "package": prePayData.package,
                        "signType": "MD5", //微信签名方式：     
                        "paySign": prePayData.paySign //微信签名 
                    },
                    function(res) {
                        if (res.err_msg == "get_brand_wcpay_request：ok") {
                            // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。 
                            context.weixin.onSuccess(prePayData.orderId);
                        } else {
                            context.weixin.checkPayStatus(prePayData.orderId);
                        }
                    }
                );
            } else {
                common.goto("/product/ticketBuyByQr/" + prePayData.orderId + "?qrcode=" + prePayData.codeUrl);
            }
        },
        checkPayStatus: function(orderId) { //后端验证订单状态
            common.post("/weixin/checkPayStatus", {
                id: orderId
            }, function() {
                context.weixin.onSuccess(orderId); //支付成功
            }, null, function(result) {
                //支付失败
                common.showAlert(result.firstMessage, function() {
                    common.refresh();
                });
            });
        },
        onSuccess: function(orderId) { //支付成功处理
            context.weixin.gotoOrder(orderId);
        },
        gotoOrder: function(orderId) {
            common.showAlert("恭喜您成为电影的投资人", function() {
                common.goto('/order/findOrderDetail/' + orderId); //支付成功跳转到订单详情
            }, "支付成功", "查看权益");
        }
    };
})(jQuery, window);