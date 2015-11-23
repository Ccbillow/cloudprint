define(function(require, exports, module){
    // 通过 require 引入依赖
    var jQuery = $ = require('jquery'),
        mdlContent = require("content"),
        mdlUser = require('user'),
        mdlFile = require('file');

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
    })(jQuery);


    // 页面初始化入口
    function init() {
        var $uname = $("#user-name").on('click', showLogin).html('登陆');

        function tryGetInfo() {
            mdlUser.getInfo().done(function(data) {
                if(data && data.islogin == 0) {
                    var ws = $("#wechat-status");
                    ws.html('正在登陆..')
                    setTimeout(function() {
                        hideLogin();
                        $uname.html(data.username);
                        ws.html('');
                    }, 100)

                }else{
                    showLogin();
                }
            });
            setTimeout(tryGetInfo, 3000)
        }

        tryGetInfo()
        events();
        //showReady();
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
                number = parseInt($upBox.find("[name='number']").val());console.log($upBox),
                filename = '';

            if((filename = $upBox.find('[name="file"]').val()) == "") return alert('请选择上传的文件');
            if(!/^.*\.pdf$/.test(filename) && !/^.*\.doc$/.test(filename) && !/^.*\.docx$/.test(filename) && !/^.*\.xls$/.test(filename)) {
                alert('上传的文件不是标准的word/excel文件，请确认');
            }
            $upBox.submit()
            //.submit()
        })



        /*$("!body").on("click", function() {
            $("#login_frame").show();
            $mask.show();
        });*/


        $("#login_frame").on("click", ".close", function() {
            $login_frame.hide();
            $mask.hide();
            return false
        });


        /*$("#to-login,#to-login2").on("click", function() {
            $login_frame.find(".login").show();
            $login_frame.find(".reset").hide()
            $login_frame.find(".sign-up").hide();
        })

        $("#to-reg").on("click", function() {
            $login_frame.find(".login").hide();
            $login_frame.find(".reset").hide()
            $login_frame.find(".sign-up").show();
        })

        $("#to-password").on("click", function() {
            $login_frame.find(".login").hide();
            $login_frame.find(".reset").show()
            $login_frame.find(".sign-up").hide();
        })*/

        //userBind();
        fileBind();
    }


    /*function userBind() {
        /!**
         * 用户注册按钮
         *!/
        $("#register").on("click", function() {
            var reg,
                message;

            var regForm = $("#register-form");

            regForm.find("input").forEach(function() {
                var _this = $(this);
                if(reg = _this.data('reg') && (message = _this.data('info'))) {
                    if(!new RegExp(reg).test(_this.val())) {
                        alert(_this.data('info'));
                        return false;
                    }
                }
            });

            if(regForm.find("[name='re-password']").val() != regForm.find("[name='password']").val()) {
                return alert('两次密码输入一致');
            }

            mdlUser.register(
                regForm.find('[name="mobile"]'),
                regForm.find('[name="password"]'),
                regForm.find('[name="VCode"]')
            ).done(function(data) {
                if(!data) return;
                if(data.status == 0) {
                    window.location.reload();
                }else{
                    alert(data.message)
                }
            })
        });

        $("#getCode-find, #getCode-register").on('click', function() {
            var $this = $(this);
            $this.addClass('btn-light');

            var curr = total = 60, code = -1;
            function last($dom, time) {
                $dom.html(curr + '秒后再试');
                if(curr-- > 0) {
                    setTimeout(function(){
                        last($dom, time);
                    }, time)
                }else{
                    curr = total;
                    $dom.removeClass('btn-light').data('stop', '0').html('获取验证码')
                }
            }

            code = $this.siblings("[name='code']").val();

            if($this.data('stop') != 1 && !!code) {
                $this = $(this).data('stop', '1');
                mdlUser.resetPw().getVcode(code).done(function() {
                    setTimeout(function(){
                        last($this, 1000);
                    }, 1000)
                })
            }
        })

        /!**
         * 使用手机号码登陆
         *!/
        $("#login").on("click", function() {
            var logForm = $("#login-form");

            mdlUser.login(
                logForm.find("mobile"),
                logForm.find("password")
            ).done(function(data) {
                    if(!data) return;
                    if(data.status == 0) {
                        window.location.reload()
                    }else{
                        alert(data.message)
                    }
                })

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


        /!**
         * 找回密码
         *!/
         $("#reset-get-code").on("click", function() {
             var $this = $(this),
                 time = 60,
                 indexTime = 60;

             function last() {
                 $this.find('.text').html(indexTime + 's后再次获取')
                 setTimeout(function() {
                     if(--indexTime >= 0) {last()}
                     else {indexTime = time; $(this).data('click', 'true')}
                 }, 1000)
             }

             if($this.data('click') === 'true') {
                 mdlUser.resetPw().getVcode().done(function() {
                     $(this).data('canclick', 'false')
                 })
             }

         })
    }*/
    function fileBind() {
        var waiting = '<div class="loading"><img src="./resources/imgs/5-121204193R7.gif" alt="loading"/></div>',
            nofiles = '<div class="content-text nofile"><i class="fa fa-commenting-o"></i>暂无文件</div>';

        /**
         * 得到文件列表的html代码
         */
        function getContentFiles(files, status) {
            if(!files || status === undefined) return;
            var html = '', index = 0;

            $.each(files, function(key, value) {
                var str = index ++ % 2 == 0 ? "" : "even";
                html += '<div class="content-text ' + str + '">\
                            <i class="fa fa-file-o"></i>\
                            <span>'+value.filename+'</span>\
                            <div class="icons">'+getIcons(status, this.id)+'</div>\
                        </div>';
            });

            function getIcons(status, id) {
                // todo
                var icons = [
                    '<a href="#" class="oper-del" data-oid="'+id+'"><i class="fa fa-trash"></i></a> \
                    <!--<a href="#"><i class="fa fa-share-square-o"></i></a>-->',
                    '<a href="#" class="oper-to-ready" data-oid="'+id+'"><i class="fa fa-print"></i></a> \
                    <a href="#"><i class="fa fa-trash"></i></a> \
                    <!--<a href="#"><i class="fa fa-share-square-o"></i></a>-->',
                    '<a href="#" class="oper-to-ready" data-oid="'+id+'"><i class="fa fa-print"></i></a> \
                    <a href="#"><i class="fa fa-trash"></i></a> \
                    '
                ];

                // todo
                return icons[status]
            }

            return html == '' ? nofiles : html
        }

        /**
         * 得到正准备打印的列表的html代码
         *
         */
        function getReadyFiles() {
            var status = 1, page = 0;
            load()
        }

        /**
         * 请求数据，显示ui
         * status 加载文件的状态
         * page 加载页数
         * loadTo 加载到指定的dom节点
         * loadMore 如何分页
         * compiler 数据加工函数
         * fail 加载失败，或者没有文件的毁掉函数
         * done 完成之后的回掉
         */
        function load(params) {

            mdlFile.load(params.status, params.page).done(function(data) {
                if(!$.isPlainObject(data) || !data.files || !data.totalPage) return;
                if(data.status == 1) {
                    /*if(data.message == '请登录后操作'){
                        showLogin()
                    }*/
                    params.fail(data, params);
                    //params.$loadTo.html(nofiles)
                }else{
                    params.$loadTo.html(params.compilder(data.files, params.status));
                    //todo
                    !!params.loadMore && params.loadMore(data.totalPage, status);
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



            function loader(status, page, more) {
                $content.html(waiting);
                load({
                    status: status,
                    page: page,
                    $loadTo: $content,
                    loadMore: !!more?more:undefined,
                    compilder: getContentFiles,
                    fail: function(data, params) {
                        if(data.message == '请登录后操作'){
                            showLogin()
                        }
                        $.content.html(nofiles)
                    }
                });
            }

            loader(status, 1, function(data) {
                $("#pages").createPage({
                    pageCount: data.totalPage,
                    current: 2,
                    backFn:function(page){
                        loader(status, page);
                    }
                });
            })


        });

        $(".content-tab .content-tab-btn").eq(0).trigger("click");

        $("#page-content").on('click', '.oper-del', function() {
            var $this = $(this);
            mdlFile.del($this.data('oid')).done(function(data) {
                if(data.status == 1) {
                    alert(data.message)
                }else if(data.status == 0){
                    $this.parents(".content-text").fadeOut()
                }
            })
        }).on('click', '.oper-to-ready', function() {
            var $this = $(this);
            mdlFile.update($this.data('oid'), 0).done(function(data) {
                if(data.status == 1) {
                    alert(data.message)
                }else if(data.status == 0){
                    $this.parents(".content-text").fadeOut()
                }
            })
        })




    }

    module.exports = {
        bindEvent: events,
        init: init
    }
});