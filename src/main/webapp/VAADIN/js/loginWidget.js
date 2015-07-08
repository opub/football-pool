window.poolLoginWidget = function() {
    oneall.api.plugins.social_login.build("social_login_container", {
        'providers' :  ['google', 'facebook', 'linkedin', 'windowslive', 'yahoo'],
        'callback_uri': (("https:" == document.location.protocol) ? "https://" : "http://") + document.location.host + "/sso"
    });
};

var oneall_js_protocol = (("https:" == document.location.protocol) ? "https" : "http");
//TODO this URL much match settings in SSCallback
document.write(unescape("%3Cscript src='" + oneall_js_protocol + "://toconnor.api.oneall.com/socialize/library.js' type='text/javascript'%3E%3C/script%3E"));
