define(function(require, exports, module){
    // 通过 require 引入依赖
    var jQuery = $ = require("jquery"),
        mdlContent = require("content"),
        mdlUser = require("user"),
        mdlFile = require("file"),
        upbox = 0;
    var $mask = $("#mask"),
        $upload = $("#upload"),
        $bottom = $("#bottom"),
        $content = $(".inner-main"),
        $content_outer = $(".content-main"),
        $login_frame = $("#login_frame"),
        $tabs = $(".content-tab"),
        $printControl = $("#print-up");
    (function($) {
        var ms = {
            init: function(obj, args) {
                return (function() {
                    ms.fillHtml(obj, args);
                    ms.bindEvent(obj, args)
                })()
            },
            fillHtml: function(obj, args) {
                return (function() {
                    obj.empty();
                    if (args.current > 1) {
                        obj.append('<a href="javascript:;" class="prevPage">上一页</a>')
                    } else {
                        obj.remove(".prevPage");
                        obj.append('<span class="disabled">上一页</span>')
                    }
                    if (args.current != 1 && args.current >= 4 && args.pageCount != 4) {
                        obj.append('<a href="javascript:;" class="tcdNumber">' + 1 + "</a>")
                    }
                    if (args.current - 2 > 2 && args.current <= args.pageCount && args.pageCount > 5) {
                        obj.append("<span>...</span>")
                    }
                    var start = args.current - 2,
                        end = args.current + 2;
                    if ((start > 1 && args.current < 4) || args.current == 1) {
                        end++
                    }
                    if (args.current > args.pageCount - 4 && args.current >= args.pageCount) {
                        start--
                    }
                    for (; start <= end; start++) {
                        if (start <= args.pageCount && start >= 1) {
                            if (start != args.current) {
                                obj.append('<a href="javascript:;" class="tcdNumber">' + start + "</a>")
                            } else {
                                obj.append('<span class="current">' + start + "</span>")
                            }
                        }
                    }
                    if (args.current + 2 < args.pageCount - 1 && args.current >= 1 && args.pageCount > 5) {
                        obj.append("<span>...</span>")
                    }
                    if (args.current != args.pageCount && args.current < args.pageCount - 2 && args.pageCount != 4) {
                        obj.append('<a href="javascript:;" class="tcdNumber">' + args.pageCount + "</a>")
                    }
                    if (args.current < args.pageCount) {
                        obj.append('<a href="javascript:;" class="nextPage">下一页</a>')
                    } else {
                        obj.remove(".nextPage");
                        obj.append('<span class="disabled">下一页</span>')
                    }
                })()
            },
            bindEvent: function(obj, args) {
                return (function() {
                    obj.on("click", "a.tcdNumber", function() {
                        var current = parseInt($(this).text());
                        ms.fillHtml(obj, {
                            "current": current,
                            "pageCount": args.pageCount
                        });
                        if (typeof(args.backFn) == "function") {
                            args.backFn(current)
                        }
                    });
                    obj.on("click", "a.prevPage", function() {
                        var current = parseInt(obj.children("span.current").text());
                        ms.fillHtml(obj, {
                            "current": current - 1,
                            "pageCount": args.pageCount
                        });
                        if (typeof(args.backFn) == "function") {
                            args.backFn(current - 1)
                        }
                    });
                    obj.on("click", "a.nextPage", function() {
                        var current = parseInt(obj.children("span.current").text());
                        ms.fillHtml(obj, {
                            "current": current + 1,
                            "pageCount": args.pageCount
                        });
                        if (typeof(args.backFn) == "function") {
                            args.backFn(current + 1)
                        }
                    })
                })()
            }
        };
        $.fn.createPage = function(options) {
            var args = $.extend({
                pageCount: 10,
                current: 1,
                backFn: function() {}
            }, options);
            ms.init(this, args)
        };
        $.fn.extend({
            alternate: function() {
                var args = Array.prototype.slice.call(arguments, 0),
                    len = args.length,
                    index = 0;
                if (len < 1) {
                    return
                }
                $(this).on("click", function() {
                    var fc = args[index++ % len];
                    $.isFunction(fc) && fc.call(this)
                })
            }
        })
    })(jQuery);

    function init() {
        $.ajaxSetup({
            cache: false
        });
        var $uname = $("#user-name");
        var addReady = window.addReady = function(data) {
            if (!data.id) {
                alert("服务器异常")
            }
            if (data.status == 0) {
                var $this = $("#" + data.id),
                    filename = $this.find("span").html();
                $(".content-tab .content-tab-btn").eq(0).trigger("click");
                $this.find(".right span").html("上传成功")
            } else {
                $("#" + data.id + " .right span").addClass("warn").html("上传失败：" + data.message)
            }
        };

        function tryGetInfo() {
            mdlUser.getInfo().done(function(data) {
                if (data && data.status == 0) {
                    var ws = $("#wechat-status");
                    ws.html("正在登陆..");
                    setTimeout(function() {
                        hideLogin();
                        $uname.html(data.loginUser.nickname);
                        ws.html("")
                    }, 100);
                    $("#user").find("img").attr("src", data.loginUser.headimgurl);
                    events();
                    fileBind()
                } else {
                    showLogin();
                    setTimeout(tryGetInfo, 3000)
                }
            })
        }
        tryGetInfo()
    }
    function showReady() {
        $bottom.css({
            "height": "334px"
        });
        $printControl.addClass("fa-angle-down").removeClass("fa-angle-up")
    }
    function hideReady() {
        $bottom.css({
            "height": "55px"
        });
        $printControl.addClass("fa-angle-up").removeClass("fa-angle-down")
    }
    function showLogin() {
        $login_frame.show();
        $mask.show();
        $(".login", "#login_frame").show();
        $(".sign-up", "#login_frame").hide()
    }
    function hideLogin() {
        $login_frame.hide();
        $mask.hide();
        $("#login_frame").hide()
    }
    function events() {
        $bottom.on("click", ".del", function() {
            var $this = $(this),
                id = $this.data("uid");
            $this.parents("li").remove()
        });
        $(".menu-list", ".top").hover(function() {
            $(this).find(".menu").addClass("show")
        }, function() {
            $(this).find(".menu").removeClass("show")
        });
        $("#uploadfile").on("click", function() {
            $mask.show();
            $upload.show()
        });
        $("#closeBtn, #closeX").on("click", function() {
            $mask.hide();
            $upload.hide()
        });
        $("#print-up").alternate(showReady, hideReady);
        $content.on("mouseenter", "i", function() {
            $(this).addClass("shake")
        });
        $content.on("mouseleave", "i", function() {
            $(this).removeClass("shake")
        });
        $("#upload-btn-ok").on("click", function() {
            var $upBox = $("#upload-box"),
                number = parseInt($upBox.find("[name='number']").val()),
                filename = "",
                uid = "upbox" + upbox++;
            if ((filename = $upBox.find('[name="file"]').val()) == "") {
                return alert("请选择上传的文件")
            }
            if (!/^.*\.pdf$/.test(filename) && !/^.*\.doc$/.test(filename) && !/^.*\.docx$/.test(filename) && !/^.*\.xls$/.test(filename) && !/^.*\.xlsx$/.test(filename)) {
                alert("上传的文件不是标准的word/excel/pdf文件，请重试")
            } else {
                $upBox.find('[type="hidden"]').val(uid).end().submit();
                showReady();
                $mask.hide();
                $upload.hide();
                $bottom.find("ul").append(filsReady({
                    filename: filename.substring(filename.lastIndexOf("\\") + 1),
                    uid: uid
                }));
                $upBox[0].reset()
            }
        });
        $("#login_frame").on("click", ".close", function() {
            $login_frame.hide();
            $mask.hide();
            return false
        });
        $("#logout").on("click", function() {
            mdlUser.logout().done(function(data) {
                if (!$.isPlainObject(data)) {
                    return
                }
                if (data.status == 0) {
                    window.location.reload()
                } else {
                    alert(data.message)
                }
            })
        })
    }
    function filsReady(data) {
        function template(data) {
            return '<li id="' + data.uid + '">                 <div class="pull-left">                    <i class="fa fa-file-o"></i>                    <span>' + data.filename + '</span>                </div>                <div class="pull-right right">                    <span>上传中<img src="./resources/imgs/5-121204193R7.gif" alt="loading"/></span>                    <a href="#" class="del" data-uid="' + data.uid + '">                    <i class="fa fa-times" ></i>                    </a>                </div>                </li>'
        }
        var html = "";
        if ($.isArray(data)) {
            $.each(data, function(key, val) {
                html += template(val)
            })
        } else {
            html += template(data)
        }
        return html
    }
    function filsReadyDone(data, html) {
        function template(data) {
            return '<li data-pid="' + data.id + '" id="' + data.uid + '">                 <div class="pull-left">                    <i class="fa fa-file-o"></i>                    <span>' + data.filename + '</span>                </div>                <div class="pull-right right">                    <span>上传完成</span>                    <a href="#" class="del" data-uid="' + data.id + '">                    <i class="fa fa-times" ></i>                    </a>                </div>                </li>'
        }
        var html = "";
        if ($.isArray(data)) {
            $.each(data, function(key, val) {
                html += template(val)
            })
        } else {
            html += template(data)
        }
        return html
    }
    var waiting = '<div class="loading"><img src="./resources/imgs/5-121204193R7.gif" alt="loading"/></div>',
        nofiles = '<div class="content-text nofile"><i class="fa fa-commenting-o"></i>暂无文件</div>';

    function getContentFiles(files, status) {
        if (!files || status === undefined || files.length == 0) {
            return nofiles
        }
        var html = "",
            index = 0;
        $.each(files, function(key, value) {
            var str = index++ % 2 == 0 ? "" : "even";
            html += '<div data-pid="' + value.id + '" class="content-text ' + str + '">                            <i class="fa fa-file-o"></i>                            <span>' + value.filename + '</span>                            <div class="icons">' + getIcons(status, this.id, this.path) + "</div>                        </div>"
        });

        function getIcons(status, id, path) {
            var icons = ['<a href="' + dlPath(path) + '" target="_blank" title="下载"><i class="fa fa-download"></i></a><a href="#" class="oper-del" title="删除" data-oid="' + id + '"><i class="fa fa-trash"></i></a>                     <!--<a href="#"><i class="fa fa-share-square-o"></i></a>-->', '<a href="' + dlPath(path) + '" target="_blank" title="下载"><i class="fa fa-download"></i></a><a href="#" class="oper-to-ready" title="移到待打印列表" data-oid="' + id + '"><i class="fa fa-print"></i></a>                     <a href="#" title="删除"><i class="fa fa-trash"></i></a>                    <!--<a href="#"><i class="fa fa-share-square-o"></i></a>-->', '<a href="' + dlPath(path) + '" target="_blank" title="下载"><i class="fa fa-download"></i></a><a href="#" class="oper-to-ready" title="移到待打印列表" data-oid="' + id + '"><i class="fa fa-print"></i></a><a href="#" class="oper-del" title="删除" data-oid="' + id + '"><i class="fa fa-trash"></i></a>'];
            return icons[status]
        }
        return html == "" ? nofiles : html
    }
    function dlPath(path) {
        return path.replace("%3A", ":").replace(/%2F/gim, "/")
    }
    function fileBind() {
        function getReadyFiles() {
            var status = 1,
                page = 0;
            load()
        }
        function load(params) {
            mdlFile.load(params.status, params.page).done(function(data) {
                if (!$.isPlainObject(data)) {
                    return
                }
                if (data.status == 1) {
                    params.fail(data, params)
                } else {
                    $.each(params.$loadTo, function(key, val) {
                        params.condition[key] && val.html(params.compilder[key](data.files, params.status))
                    }); !! params.done && params.done(data, status)
                }
            })
        }
        $tabs.on("mouseenter", ".content-tab-btn", function() {
            var $li = $(this).parents("li");
            var know = $li.data("know");
            if (know) {
                var $know = $(know).stop().fadeIn();
                var knowHandler = null
            }
        }).on("mouseleave", ".content-tab-btn", function() {
            var $li = $(this).parents("li");
            var know = $li.data("know");
            if (know) {
                var $know = $(know).stop().fadeOut();
                var knowHandler = null
            }
        });
        $content_outer.on("mouseenter", ".tips-ready", function() {
            $(this).stop().show()
        }).on("mouseleave", ".tips-ready", function() {
            $(this).fadeOut()
        });
        $tabs.on("click", ".content-tab-btn", function() {
            var $li = $(this).parents("ul").find(".active").removeClass("active").end().end().parent().addClass("active");
            var status = $li.data("status");
            $("#pages").hide();
            $content.html(waiting);

            function forPages(page, callback) {
                page = page || 1;
                load({
                    status: status,
                    page: page,
                    condition: [true],
                    $loadTo: [$content],
                    done: function(data) {
                        if ( !! callback) {
                            if (data.totalPage) {
                                $("#pages").show().createPage({
                                    pageCount: data.totalPage,
                                    current: data.nextPageNum,
                                    backFn: callback
                                })
                            }
                        }
                    },
                    compilder: [getContentFiles],
                    fail: function(data, params) {
                        if (data.message == "请登录后操作") {
                            alert("请登陆后操作");
                            window.location.reload()
                        }
                        $content.html(nofiles)
                    }
                })
            }
            forPages(1, function(page) {
                forPages(page)
            })
        });
        $(".content-tab .content-tab-btn").eq(0).trigger("click");
        $content.on("click", ".oper-del", function() {
            if (confirm("确定删除该文件吗？")) {
                var $this = $(this);
                mdlFile.del($this.data("oid")).done(function(data) {
                    if (data.status == 1) {
                        alert(data.message)
                    } else {
                        if (data.status == 0) {
                            $this.parents(".content-text").fadeOut()
                        }
                    }
                })
            }
        }).on("click", ".oper-to-ready", function() {
            var $this = $(this);
            mdlFile.update($this.data("oid"), "").done(function(data) {
                if (data.status == 1) {
                    alert(data.message)
                } else {
                    if (data.status == 0) {
                        $this.parents(".content-text").fadeOut()
                    }
                }
            })
        })
    };
    module.exports = {
        bindEvent: events,
        init: init
    }
});