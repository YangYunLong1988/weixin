(function($, context) {
    var _browser = {
        versions: function() {
            var u = navigator.userAgent,
                app = navigator.appVersion;
            return { //移动终端浏览器版本信息 
                trident: u.indexOf('Trident') > -1, //IE内核 
                presto: u.indexOf('Presto') > -1, //opera内核 
                webKit: u.indexOf('AppleWebKit') > -1, //苹果、谷歌内核 
                gecko: u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1, //火狐内核 
                mobile: !!u.match(/AppleWebKit.*Mobile.*/), //是否为移动终端 
                ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios终端 
                android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, //android终端或uc浏览器 
                iPhone: u.indexOf('iPhone') > -1, //是否为iPhone或者QQHD浏览器 
                iPad: u.indexOf('iPad') > -1, //是否iPad 
                webApp: u.indexOf('Safari') == -1, //是否web应该程序，没有头部与底部 
                microMessenger: u.indexOf('MicroMessenger') > -1,
                qq: u.indexOf('QQ') > -1
            };
        }(),
        language: (navigator.browserLanguage || navigator.language).toLowerCase()
    };
    var _compare = function(v0, v1) {
        var s0 = v0.split(".");
        var s1 = v1.split(".");
        var size = s0.length < s1.length ? s0.length : s1.length;
        for (var i = 0; i < size; i++) {
            var i0 = parseInt(s0[i]);
            var i1 = parseInt(s1[i]);
            if (i0 < i1) {
                return -1;
            } else if (i0 > i1) {
                return 1;
            }
        }
        if (s0.length < s1.length) {
            return -1;
        } else if (s0.length > s1.length) {
            return 1;
        }
        return 0;
    };
    var html = $("html");
    var head = $("head");
    var body = $("body");
    var width = html.width();
    if (width < 1000) {
        var fontSize = width * 62.5 / 320;
        html.css("font-size", fontSize + "%");
    }
    var _mask = null;
    var _loading = null;
    var _alert = null;
    var _confirm = null;
    var _device = null;
    var _platform = null;


    context.common = {
        getDevice: function() {
            return _device;
        },
        getPlatform: function() {
            return _platform;
        },
        callApp: function(action, params) {
            if (_browser.versions.ios) {
                if (params["json"]) {
                    params["json"] = JSON.stringify(params["json"]);
                }
                context.jsBridge.postNotification(action, params);
            } else if (_browser.versions.android) {
                AndroidInterface.action(action, JSON.stringify(params));
            } else {
                alert(JSON.stringify(params))
            }
        },
        checkAppVersion: function(version, next) {
            context.callback = function(data) {
                var params = eval('(' + data + ')');
                if (_compare(version, params.version) > 0) {
                    common.showAlert("您的APP版本太低，请先升级至" + version + "或更高版本");
                    return;
                }
                next();
            };
            common.callApp("event", {
                event: "getVersion",
                json: {}
            });
        },
        callAppScan: function(callback) {
            common.checkAppVersion("3.1.6", function() {
                context.callback = function(data) {
                    var r = eval('(' + data + ')');
                    callback(r.result);
                };
                common.callApp("event", {
                    event: "scan",
                    json: {}
                });
            });
        },
        showLoading: function(tip) {
            if (_loading == null) {
                _loading = {
                    display: $('<div class="loading"></div>'),
                    panel: $('<div class="panel"><div class="loader_container"><div class="loader"></div></div></div>'),
                    tip: $('<div class="loading_tip"></div>')
                };
                _loading.display.append(_loading.panel);
                _loading.panel.append(_loading.tip);
            }
            _loading.tip.html(tip || "正在加载数据...");
            body.append(_loading.display);
        },
        closeLoading: function() {
            if (_loading != null) {
                _loading.display.detach();
            }
        },
        showAlert: function(message, callback, title, ok) {
            if (_mask == null) {
                _mask = {
                    display: $('<div class="mask"></div>')
                };
            }
            if (_alert == null) {
                _alert = {
                    display: $('<div class="dialog"></div>'),
                    title: $('<div class="title"></div>'),
                    content: $('<div class="content"></div>'),
                    ok: $('<a class="ok" href = "javascript:void(0)"></a>'),
                    callback: null
                };
                _alert.display.append(_alert.title);
                _alert.display.append(_alert.content);
                _alert.display.append(_alert.ok);
                _alert.ok.click(function() {
                    _mask.display.detach();
                    _alert.display.detach();
                    if (_alert.callback != null) {
                        var callback = _alert.callback;
                        _alert.callback = null;
                        callback();
                    }
                });
            }
            _alert.title.html(title || "提示");
            if('您的电影票还未进行兑换，为了保证您的权益，您可将此权益转为礼品。截止日期4月30日。'==message){
            	message = '您的电影票还未进行兑换，为了保证您的权益，您可将此权益转为礼品。截止日期2016-06-26。'
            }
            _alert.content.html(message);
            _alert.ok.html(ok || "确定");
            _alert.callback = callback;
            _mask.display.append(_alert.display);
            body.append(_mask.display);
        },
        showConfirm: function(message, callback, title, yes, no) {
            if (_mask == null) {
                _mask = {
                    display: $('<div class="mask"></div>')
                };
            }
            if (_confirm == null) {
                _confirm = {
                    display: $('<div class="dialog"></div>'),
                    title: $('<div class="title"></div>'),
                    content: $('<div class="content"></div>'),
                    yes: $('<a class="yes" href = "javascript:void(0)"></a>'),
                    no: $('<a class="no" href = "javascript:void(0)"></a>'),
                    callback: null
                };
                _confirm.display.append(_confirm.title);
                _confirm.display.append(_confirm.content);
                _confirm.display.append(_confirm.yes);
                _confirm.display.append(_confirm.no);

                _confirm.yes.click(function() {
                    _mask.display.detach();
                    _confirm.display.detach();
                    if (_confirm.callback != null) {
                        var callback = _confirm.callback;
                        _confirm.callback = null;
                        callback(true);
                    }
                });
                _confirm.no.click(function() {
                    _mask.display.detach();
                    _confirm.display.detach();
                    if (_confirm.callback != null) {
                        var callback = _confirm.callback;
                        _confirm.callback = null;
                        callback(false);
                    }
                });
            };
            _confirm.title.html(title || "提示");
            _confirm.content.html(message);
            _confirm.yes.html(yes || "是");
            _confirm.no.html(no || "否");
            _confirm.callback = callback;
            _mask.display.append(_confirm.display);
            body.append(_mask.display);
        },
        scrollTo: function(value) {
            $(document).scrollTop(value);
        },
        post: function(api, params, success, tip, fault) {
            common.showLoading(tip);
            $.ajax({
                url: api,
                type: "POST",
                async: true,
                data: params,
                dataType: "text",
                success: function(data) {
                    common.closeLoading();
                    if (data.indexOf("<html>") == -1) {
                        try {
                            var result = eval('(' + data + ')');
                            if (result.success) {
                                success(result);
                            } else {
                                if (fault != null) {
                                    fault(result);
                                } else {
                                    common.showAlert(result.firstMessage);
                                }
                            }
                        } catch (e) {
                            console.info(e);
                            alert(e);
                        }
                    } else {
                        common.showAlert("服务繁忙，请稍候再试。");
                    }
                },
                error: function(XMLHttpRequest, textStatus, errorThrown) {
                    if (XMLHttpRequest.status == 403) {
                        window.location.href = "/login";
                        return;
                    }
                    common.closeLoading();
                    common.showAlert("请求失败，请检查网络状况或者稍后再试");
                }
            });
        },
        loadJs: function(url, callback) {
            var onScriptLoad = function() {
                callback();
            };
            var node = document.createElement('script');
            node.type = 'text/javascript';
            node.charset = 'utf-8';
            node.async = true;
            node.src = url;
            if (node.attachEvent) {
                node.attachEvent('onreadystatechange', onScriptLoad);
            } else {
                node.addEventListener('load', onScriptLoad, false);
            }
            head[0].appendChild(node);
        },
        goto: function(url) {
            window.location.href = url;
        },
        refresh: function() {
            window.location.reload();
        },
        generateOrder: function(productId) {
            common.post("/order/generateOrder", {
                productId: productId
            }, function(result) {
                common.goto('/order/findOrderDetail/' + result.data.id);
            }, null, function(result) {
                if (result.type == 'WARNING') {
                    common.hasUnpayOrder(result.data.id, function() {
                        common.generateOrder(productId);
                    });
                } else {
                    common.showAlert(result.firstMessage);
                }
            });
        },
        hasUnpayOrder: function(orderId, callback) {
            common.showConfirm("您当前有尚未支付的订单，请您选择后续操作！如重新下单，则已有订单会自动取消。", function(result) {
                if (result) {
                    common.goto('/order/findOrderDetail/' + orderId);
                } else {
                    common.post("/order/cancle", {
                        id: orderId
                    }, function(data) {
                        if (callback) {
                            callback();
                        }
                    }, null, function() {
                        common.goto('/order/findOrderDetail/' + orderId);
                    });
                }
            }, '提示', '前去支付', '重新下单');
        }
    };
    (function() {
        try {
            if (NO_NAV) {
                return;
            }
        } catch (e) {}

        var html = '<div id="nav">';
        var isPage = false;
        var path = window.location.pathname;
        isPage = (path == '/channel/entrance' || path == '/product/list');
        html += '<a href="/channel/entrance" class="' + (isPage ? 'on' : 'off') + '">首页</a>';
        isPage = path == '/order/findorders';
        html += '<a id="nav_order_enter" href="/order/findorders" class="' + (isPage ? 'on' : 'off') + '">订单</a>';
        isPage = path == '/order/giftCard';
        html += '<a href="/order/giftCard" class="' + (isPage ? 'on' : 'off') + '">礼品卡</a>';
        html += '</div>';
        $("body").prepend($(html));
    })();

    common.post("/getDevice", {}, function(result) {
        _device = result.data.device;
        _platform = result.data.platform;
        if (result.pickallgiftornot == true) {
            $("#nav_order_enter").append($('<span></span>'));
        }
        try {
            if (context.callOnDevice) {
                context.callOnDevice();
            }
        } catch (e) {};
    }, "正在加载数据....");
    if (_browser.versions.microMessenger && !context.no_share) {
        common.loadJs("http://diana.static.jlfex.com/common/jweixin/v1.0.0/jweixin-1.0.0.js", function() {
            common.post("/weixin/getWeixinJSConfig", {
                timestamp: new Date().getTime(),
                url: window.location.href
            }, function(result) {
                wx.config({
                    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                    appId: result.data.appId, // 必填，公众号的唯一标识
                    timestamp: result.data.timestamp, // 必填，生成签名的时间戳
                    nonceStr: result.data.noncestr, // 必填，生成签名的随机串
                    signature: result.data.signature, // 必填，签名，见附录1
                    jsApiList: ['scanQRCode', 'showAllNonBaseMenuItem', 'hideAllNonBaseMenuItem', 'onMenuShareTimeline', 'onMenuShareAppMessage', 'onMenuShareQQ', 'onMenuShareWeibo', 'onMenuShareQZone'] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
                });
                wx.ready(function() {
                    try {
                        if (!context.share) {
                            var domain = window.location.protocol + "//" + window.location.host;
                            context.share = {
                                'title': "叶问三3月4日巅峰对决，壕礼+电影票享不停",
                                'desc': '颠覆观影价值——100元=480超值权益',
                                'link': domain + "/channel/entrance?platform=" + _platform,
                                'img': "http://static.jlfex.com/diana/share/movie_icon@2x.jpg"
                            };
                        }
                        if (context.share) {
                            wx.showAllNonBaseMenuItem();
                            wx.onMenuShareTimeline({
                                title: context.share.desc, // 分享标题
                                link: context.share.link, // 分享链接
                                imgUrl: context.share.img, // 分享图标
                                success: common.closeShareTip,
                                cancel: common.closeShareTip
                            });
                            wx.onMenuShareAppMessage({
                                title: context.share.title, // 分享标题
                                desc: context.share.desc, // 分享描述
                                link: context.share.link, // 分享链接
                                imgUrl: context.share.img, // 分享图标
                                type: 'link', // 分享类型,music、video或link，不填默认为link
                                dataUrl: '', // 如果type是music或video，则要提供数据链接，默认为空
                                success: common.closeShareTip,
                                cancel: common.closeShareTip
                            });
                            wx.onMenuShareQQ({
                                title: context.share.title, // 分享标题
                                desc: context.share.desc, // 分享描述
                                link: context.share.link, // 分享链接
                                imgUrl: context.share.img, // 分享图标
                                success: common.closeShareTip,
                                cancel: common.closeShareTip
                            });
                            wx.onMenuShareWeibo({
                                title: context.share.title, // 分享标题
                                desc: context.share.desc, // 分享描述
                                link: context.share.link, // 分享链接
                                imgUrl: context.share.img, // 分享图标
                                success: common.closeShareTip,
                                cancel: common.closeShareTip
                            });
                            wx.onMenuShareQZone({
                                title: context.share.title, // 分享标题
                                desc: context.share.desc, // 分享描述
                                link: context.share.link, // 分享链接
                                imgUrl: context.share.img, // 分享图标
                                success: common.closeShareTip,
                                cancel: common.closeShareTip
                            });
                        } else {
                            wx.hideAllNonBaseMenuItem();
                        }
                    } catch (e) {
                        wx.hideAllNonBaseMenuItem();
                    }
                });
            }, null, function(result) {
                common.showAlert(result.firstMessage);
            });
        });
    };
})(jQuery, window);