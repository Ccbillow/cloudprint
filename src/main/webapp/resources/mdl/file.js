define(function(require, exports, module) {
    var $ = require('jquery');

    var File = module.exports = {};

    var upUrl = '/printfile/upload',
        delUrl = '/printfile/delete/{}',
        udUrl = '/printfile/update/{}',
        loadUrl = '/printfile/findbystatus';

    function getRealUrl(url, pid) {
        return url.replace('{}', pid)
    }

    File.upload = function() {}
    File.del = function(pid) {
        return $.get(getRealUrl(delUrl, pid), '')
    }
    File.update = function(pid, status, number, isColorful) {
        if(status === undefined) {
            return $.get(getRealUrl(udUrl, pid), {
                number: number,
                isColorful: isColorful
            })
        }else{
            return $.get(getRealUrl(udUrl, pid), {
                status: status
            })
        }

    }
    File.load = function(status, page) {
        return $.get(loadUrl, {
            status: status,
            pageNow: page - 1
        })
    }
})