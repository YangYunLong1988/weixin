//
//  TGJSBridge.js
//  TGJSBridge
//
//  Created by Chao Shen on 12-3-1.
//  Copyright (c) 2012年 Hangzhou Jiuyan Technology Co., Ltd. All rights reserved.
//

(function(context) {
    function bridgeCall(src, callback) {
        iframe = document.createElement("iframe");
        iframe.style.display = "none";
        iframe.src = src;
        var cleanFn = function(state) {
            console.log(state)
            try {
                iframe.parentNode.removeChild(iframe);
            } catch (error) {}
            if (callback) callback();
        };
        iframe.onload = cleanFn;
        document.documentElement.appendChild(iframe);
    }

    function JSBridge() {
        this.notificationIdCount = 0;
        this.notificationDict = {};
    }

    JSBridge.prototype = {
        constructor: JSBridge,
        //send notification to WebView
        postNotification: function(name, userInfo) {
            this.notificationIdCount++;

            this.notificationDict[this.notificationIdCount] = {
                name: name,
                userInfo: userInfo
            };

            bridgeCall('jsbridge://PostNotificationWithId-' + this.notificationIdCount);
        },
        //pop the notification in the cache
        popNotificationObject: function(notificationId) {
            var result = JSON.stringify(this.notificationDict[notificationId]);
            delete this.notificationDict[notificationId];
            return result;
        }
    };

    context.jsBridge = new JSBridge();

})(window);