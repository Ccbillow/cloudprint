define(function(require, exports, module){
    var $ = require('jquery');

    var User = module.exports = {};

    /**
     * �û���¼�ӿ�
     */
    var loginUrl = '/cloudprint/user/login',
        regUrl = '/cloudprint/user/register',
        logoutUrl = '/cloudprint/user/logout',
        infoUrl = '/cloudprint/user/getUserMessage',
        codeUrl = '/cloudprint/user/getValidateCode';

    User.login = function(mobile, password) {
        return $.get(loginUrl)
    }
    User.logout = function() {
        return $.get(logoutUrl)
    }
    /**
     *  mobile=18580741650
        password=123123
        nicknmae=С��
        validateCode=5234����̨����ǰ�˵���֤�룩
        VCode=5234
     */
    User.register = function(mobile, password, VCode) {
        return $.get(regUrl, {
            mobile: mobile,
            password: password,
            VCode: VCode
        })
    }

    User.getInfo = function() {
        return $.get(infoUrl);
    }

    User.resetPw = function() {
        function c(mobile) {
            return $.get(codeUrl, {
                mobile: mobile
            })
        }
        function s() {

        }
        return {
            getVcode: c,
            sendRest: s
        }
    }
});