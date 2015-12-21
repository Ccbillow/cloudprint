define(function(require, exports, module) {
    var $ = require('jquery');

    var File = module.exports = {};

    var upUrl = '/printfile/upload',
        delUrl = '/printfile/delete/{}',
        modiUrl = '/printfile/update/{}',
        udStaUrl = '/printfile/updatestatus/{}',
        loadUrl = '/printfile/findbystatus';

    function getRealUrl(url, pid) {
        return url.replace('{}', pid)
    }

    File.upload = function() {}
    File.del = function(pid) {
        return $.get(getRealUrl(delUrl, pid), '')
    }
    //修改文件状态
    File.update = function(pid, status) {
        return $.get(getRealUrl(udStaUrl, pid), {
            status: status
        })
    }
    //修改文件信息
    File.modify = function(pid, number, isColorful) {
        return $.get(getRealUrl(modiUrl, pid), {
            number: number,
            isColorful: isColorful
        })
    }
    File.load = function(status, page) {
        return $.get(loadUrl, {
            status: status,
            pageNow: page - 1
        })
    }
})