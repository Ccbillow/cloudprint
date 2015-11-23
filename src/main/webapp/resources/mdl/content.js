define(function(require, exports, module){
    var $ = require('jquery');

    var waiting = '<div class="loading"><img src="./resources/imgs/5-121204193R7.gif" alt="loading"/></div>',
        dombread = '<div></div>';

    //var template = require('artTemplate');

    function updateDate(data, $d) {

    }

    exports.init = function(url, $d, $) {
        $d.html(waiting);
        /*$.getJSON(url, '', function(data) {
            updateDate(date, $d);
        })*/
    }
});