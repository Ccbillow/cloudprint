define(function(require, exports, module){
    // 通过 require 引入依赖
    var jQuery = $ = require('jquery'),
        mdlContent = require("content"),
        mdlUser = require('user'),
        mdlFile = require('file'),
        upbox = 0;

    var $mask = $("#mask"),
        $upload = $("#upload"),
        $bottom = $("#bottom"),
        $content = $(".content-main"),
        $login_frame = $("#login_frame"),
        $printControl = $("#print-up");

    (function($){
        var ms = {
            init:function(obj,args){
                return (function(){
                    ms.fillHtml(obj,args);
                    ms.bindEvent(obj,args);
                })();
            },
            //填充html
            fillHtml:function(obj,args){
                return (function(){
                    obj.empty();
                    //上一页
                    if(args.current > 1){
                        obj.append('<a href="javascript:;" class="prevPage">上一页</a>');
                    }else{
                        obj.remove('.prevPage');
                        obj.append('<span class="disabled">上一页</span>');
                    }
                    //中间页码
                    if(args.current != 1 && args.current >= 4 && args.pageCount != 4){
                        obj.append('<a href="javascript:;" class="tcdNumber">'+1+'</a>');
                    }
                    if(args.current-2 > 2 && args.current <= args.pageCount && args.pageCount > 5){
                        obj.append('<span>...</span>');
                    }
                    var start = args.current -2,end = args.current+2;
                    if((start > 1 && args.current < 4)||args.current == 1){
                        end++;
                    }
                    if(args.current > args.pageCount-4 && args.current >= args.pageCount){
                        start--;
                    }
                    for (;start <= end; start++) {
                        if(start <= args.pageCount && start >= 1){
                            if(start != args.current){
                                obj.append('<a href="javascript:;" class="tcdNumber">'+ start +'</a>');
                            }else{
                                obj.append('<span class="current">'+ start +'</span>');
                            }
                        }
                    }
                    if(args.current + 2 < args.pageCount - 1 && args.current >= 1 && args.pageCount > 5){
                        obj.append('<span>...</span>');
                    }
                    if(args.current != args.pageCount && args.current < args.pageCount -2  && args.pageCount != 4){
                        obj.append('<a href="javascript:;" class="tcdNumber">'+args.pageCount+'</a>');
                    }
                    //下一页
                    if(args.current < args.pageCount){
                        obj.append('<a href="javascript:;" class="nextPage">下一页</a>');
                    }else{
                        obj.remove('.nextPage');
                        obj.append('<span class="disabled">下一页</span>');
                    }
                })();
            },
            //绑定事件
            bindEvent:function(obj,args){
                return (function(){
                    obj.on("click","a.tcdNumber",function(){
                        var current = parseInt($(this).text());
                        ms.fillHtml(obj,{"current":current,"pageCount":args.pageCount});
                        if(typeof(args.backFn)=="function"){
                            args.backFn(current);
                        }
                    });
                    //上一页
                    obj.on("click","a.prevPage",function(){
                        var current = parseInt(obj.children("span.current").text());
                        ms.fillHtml(obj,{"current":current-1,"pageCount":args.pageCount});
                        if(typeof(args.backFn)=="function"){
                            args.backFn(current-1);
                        }
                    });
                    //下一页
                    obj.on("click","a.nextPage",function(){
                        var current = parseInt(obj.children("span.current").text());
                        ms.fillHtml(obj,{"current":current+1,"pageCount":args.pageCount});
                        if(typeof(args.backFn)=="function"){
                            args.backFn(current+1);
                        }
                    });
                })();
            }
        }
        $.fn.createPage = function(options){
            var args = $.extend({
                pageCount : 10,
                current : 1,
                backFn : function(){}
            },options);
            ms.init(this,args);
        }

        $.fn.extend({
            alternate: function() {
                var args = Array.prototype.slice.call(arguments, 0),
                    len = args.length,
                    index = 0;
                if(len < 1) return;
                $(this).on('click', function() {
                    var fc = args[index++%len];
                    $.isFunction(fc) && fc.call(this);
                })
            }
        });
        $.log = function() {
            if(console) {
                console.log(arguments)
            }
        }
    })(jQuery);


    // 页面初始化入口
    function init() {
        $.ajaxSetup({cache: false});
        var $uname = $("#user-name");//.on('click', showLogin).html('登陆');

        /**
         * @param {id, status, message}
         * @type {Function}
         */
        var addReady = window.addReady = function(data) {
            if(!data.id) {
                $.log("服务器异常");
            }
            if(data.status == 0) {
                // 上传文件成功
                var $this = $("#" + data.id),
                    filename = $this.find('span').html();

                $(".content-tab .content-tab-btn").eq(0).trigger("click");
                $this.find(".right span").html("上传成功");
                /*$this.find(".right span").html("上传成功");
                $content.append(getContentFiles([{
                    filename: filename,
                    id: data.id
                }], 0))*/
            }else{
                // 文件上传失败
                $("#" + data.id + " .right span").addClass('warn').html("上传失败:" + data.message);
            }
        }

        function tryGetInfo() {
            mdlUser.getInfo().done(function(data) {
                if(data && data.status == 0) {
                    var ws = $("#wechat-status");
                    ws.html('正在登陆..')
                    setTimeout(function() {
                        hideLogin();
                        $uname.html(data.loginUser.nickname);
                        ws.html('');
                    }, 100)

                    $("#user").find('img').attr('src', data.loginUser.headimgurl);
                    events();
                    fileBind();
                }else{
                    showLogin();
                    setTimeout(tryGetInfo, 3000)
                }
            });

        }

        tryGetInfo()
    }

    // 显示待打印窗口
    function showReady() {
        $bottom.css({
            'height': '334px'
        });
        $printControl.addClass('fa-angle-down').removeClass('fa-angle-up');
    }

    // 隐藏待打印窗口
    function hideReady() {
        $bottom.css({
            'height': '55px'
        });
        $printControl.addClass('fa-angle-up').removeClass('fa-angle-down');
    }

    function showLogin() {
        $login_frame.show();
        $mask.show();
        $(".login", "#login_frame").show();
        $(".sign-up", "#login_frame").hide();
    }

    function hideLogin() {
        $login_frame.hide();
        $mask.hide();
        $("#login_frame").hide();
    }

    function events() {
        $bottom.on('click', '.del', function() {
            var $this = $(this),
                id = $this.data('uid');
            //console.log(id)

            $this.parents("li").remove();
        });

        $(".menu-list", ".top").hover(function() {
            $(this).find('.menu').addClass('show');
        },function() {
            $(this).find('.menu').removeClass('show');
        });

        $("#uploadfile").on("click", function() {
            $mask.show();
            $upload.show();
        });

        $("#closeBtn, #closeX").on('click', function() {
            $mask.hide();
            $upload.hide();
        });

        $("#print-up").alternate(showReady, hideReady);


        $(".content-main").on("mouseenter", "i", function() {
            $(this).addClass("shake")
        });
        $(".content-main").on("mouseleave", "i", function() {
            $(this).removeClass("shake")
        });

        $("#upload-btn-ok").on('click', function() {
            var $upBox = $("#upload-box"),
                number = parseInt($upBox.find("[name='number']").val());
            filename = '',
                uid = 'upbox' + upbox++;

            if((filename = $upBox.find('[name="file"]').val()) == "") return alert('请选择上传的文件');
            if(!/^.*\.pdf$/.test(filename) && !/^.*\.doc$/.test(filename) && !/^.*\.docx$/.test(filename) && !/^.*\.xls$/.test(filename) && !/^.*\.xlsx$/.test(filename)) {
                alert('上传的文件不是标准的word/excel/pdf文件，请重试');
            }else{
                $upBox.find('[type="hidden"]').val(uid).end().submit();

                showReady();
                $mask.hide();
                $upload.hide();
                // 正在准备打印列表
                $bottom.find("ul").append(filsReady({
                    filename: filename.substring(filename.lastIndexOf("\\") + 1),
                    uid: uid
                }));

                $upBox.find("form").reset();
            }
        })


        $("#login_frame").on("click", ".close", function() {
            $login_frame.hide();
            $mask.hide();
            return false
        });

        $("#logout").on("click", function() {
            mdlUser
                .logout()
                .done(function(data) {
                    if(!$.isPlainObject(data)) return;
                    if(data.status == 0) {
                        window.location.reload()
                    }else{
                        alert(data.message)
                    }
                })

        });
    }
    function filsReady (data) {
        function template(data) {
            return '<li id="'+data.uid+'"> \
                <div class="pull-left">\
                    <i class="fa fa-file-o"></i>\
                    <span>'+data.filename+'</span>\
                </div>\
                <div class="pull-right right">\
                    <span>上传中<img src="./resources/imgs/5-121204193R7.gif" alt="loading"/></span>\
                    <a href="#" class="del" data-uid="'+data.uid+'">\
                    <i class="fa fa-times" ></i>\
                    </a>\
                </div>\
                </li>';
        };
        var html = '';
        if($.isArray(data)) {
            $.each(data, function(key, val) {
                html += template(val)
            })
        }else{
            html += template(data)
        }

        return html;
    }

    // 抽象不够 todo
    function filsReadyDone (data, html) {
        function template(data) {
            return '<li data-pid="'+data.id+'" id="'+data.uid+'"> \
                <div class="pull-left">\
                    <i class="fa fa-file-o"></i>\
                    <span>'+data.filename+'</span>\
                </div>\
                <div class="pull-right right">\
                    <span>上传完成</span>\
                    <a href="#" class="del" data-uid="'+data.id+'">\
                    <i class="fa fa-times" ></i>\
                    </a>\
                </div>\
                </li>';
        };
        var html = '';
        if($.isArray(data)) {
            $.each(data, function(key, val) {
                html += template(val)
            })
        }else{
            html += template(data)
        }

        return html;
    }
    var waiting = '<div class="loading"><img src="./resources/imgs/5-121204193R7.gif" alt="loading"/></div>',
        nofiles = '<div class="content-text nofile"><i class="fa fa-commenting-o"></i>暂无文件</div>';
    function getContentFiles(files, status) {
        if(!files || status === undefined || files.length == 0) return nofiles;
        var html = '', index = 0;

        $.each(files, function(key, value) {
            var str = index ++ % 2 == 0 ? "" : "even";
            html += '<div data-pid="'+value.id+'" class="content-text ' + str + '">\
                            <i class="fa fa-file-o"></i>\
                            <span>'+value.filename+'</span>\
                            <div class="icons">'+getIcons(status, this.id, this.path)+'</div>\
                        </div>';
        });

        function getIcons(status, id, path) {
            // todo
            var icons = [
                '<a href="'+ dlPath(path) +'" target="_blank"><i class="fa fa-download"></i></a><a href="#" class="oper-del" data-oid="'+id+'"><i class="fa fa-trash"></i></a> \
                    <!--<a href="#"><i class="fa fa-share-square-o"></i></a>-->',
                '<a href="'+ dlPath(path) +'" target="_blank"><i class="fa fa-download"></i></a><a href="#" class="oper-to-ready" data-oid="'+id+'"><i class="fa fa-print"></i></a> \
                    <a href="#"><i class="fa fa-trash"></i></a> \
                    <!--<a href="#"><i class="fa fa-share-square-o"></i></a>-->',
                '<a href="#" class="oper-del" data-oid="'+id+'><i class="fa fa-trash"></i></a>'
            ];

            // todo
            return icons[status]
        }

        return html == '' ? nofiles : html
    }

    function dlPath(path) {
        return path.replace("%3A", ":").replace(/%2F/gim, "/");
    }


    function fileBind() {
        function getReadyFiles() {
            var status = 1, page = 0;
            load()
        }

        /**
         * 请求数据，显示ui
         * status 加载文件的状态
         * page 加载页数
         * $loadTo 加载到指定的dom节点
         * loadMore 如何分页
         * compiler 数据加工函数
         * fail 加载失败，或者没有文件的毁掉函数
         * done 完成之后的回掉  可以处理分页
         */
        function load(params) {
            mdlFile.load(params.status, params.page).done(function(data) {
                console.log(data)
                if(!$.isPlainObject(data)) return;
                if(data.status == 1) {
                    /*if(data.message == '请登录后操作'){
                     showLogin()
                     }*/
                    params.fail(data, params);
                    //params.$loadTo.html(nofiles)
                }else{
                    $.each(params.$loadTo, function(key, val) {
                        params.condition[key] && val.html(params.compilder[key](data.files, params.status));
                    })
                    //todo
                    !!params.done && params.done(data, status);
                }
            });
        }

        $(".content-tab").on("click", ".content-tab-btn", function() {
            var status = $(this).parents("ul")
                .find('.active')
                .removeClass("active")
                .end()
                .end()
                .parent()
                .addClass('active')
                .data('status');


            // 显示等待界面
            $content.html(waiting);

            function forPages(page, callback) {
                page = page || 1;
                load({
                    status: status,
                    page: page,
                    condition: [true],
                    $loadTo: [$content],
                    done: function(data) {
                        if(!!callback) {
                            data.totalPage && $("#pages").createPage({
                                pageCount: data.totalPage,
                                current: data.nextPageNum,
                                backFn: callback
                            });
                        }
                    },
                    compilder: [getContentFiles],
                    fail: function(data, params) {
                        if(data.message == '请登录后操作'){
                            alert('请登陆后操作');
                            window.location.reload();
                        }
                        $content.html(nofiles)
                    }
                })
            }


            forPages(1, function(page){
                forPages(page);
            });


        });

        $(".content-tab .content-tab-btn").eq(0).trigger("click");

        $content.on('click', '.oper-del', function() {
            if(confirm("确定删除该文件吗？")) {
                var $this = $(this);
                mdlFile.del($this.data('oid')).done(function(data) {
                    if(data.status == 1) {
                        alert(data.message)
                    }else if(data.status == 0){
                        //var dataid =
                        $this.parents(".content-text").fadeOut()//.data("pid");
                        //$bottom.find('li[data-pid="'+dataid+'"]').fadeOut();
                    }
                })
            }

        }).on('click', '.oper-to-ready', function() {
            var $this = $(this);
            mdlFile.update($this.data('oid'), '').done(function(data) {
                if(data.status == 1) {
                    alert(data.message)
                }else if(data.status == 0){
                    $this.parents(".content-text").fadeOut()
                }
            })
        })



        // 加载bottom栏目
        // 默认加载两次 todo...
    }

    module.exports = {
        bindEvent: events,
        init: init
    }
});