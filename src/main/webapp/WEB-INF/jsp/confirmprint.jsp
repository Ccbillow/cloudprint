<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no" />
    <title>重邮云打印</title>
    <style type="text/css">
        body{text-align:center;font-size:62.5%;font-family:"Microsoft YaHei"}h1{font-size:1.8rem}.demension{margin-top:30px;width:260px;height:260px}.panel{position:absolute;top:0;left:0;z-index:1;width:100%;height:100%}body,h1,h2,h3,img,li,p,ul{margin:0;padding:0;border:0}a{color:#000;text-decoration:none}li{list-style:none}.fl{float:left}.fr{float:right}.clear{clear:both;overflow:hidden;zoom:1}.wrapper{width:100%}.head{width:100%;height:2.935rem;background:#00adde;color:#fff;font-size:1.125rem;line-height:2.935rem}.head-icon{width:1.65625rem;vertical-align:middle}.head-icon-title{margin-left:.75rem}.head-title{line-height:2.935rem}.head-detail{overflow:hidden;width:100%;height:3.75rem;background:#2fbae1;font-size:.9375rem}.head-detail .content{margin:1.125rem 0 0 .75rem}.head-detail .content ul{margin:0;padding:0}.head-detail .content li{overflow:hidden;margin-top:.325rem}.head-detail .content span{color:#fff}.head-detail .content img{position:relative;margin-right:.75rem;width:1.4375rem}.main{width:100%;height:260px}.main .file-container{position:fixed;top:4.6875rem;bottom:2.375rem;overflow:auto;width:100%}.main-title{height:1.75rem;background:#ececec}.main-title-text{margin-left:.75rem;color:#969696;font-size:.9375rem;line-height:1.75rem}.main li{position:relative;overflow:hidden;height:3rem;zoom:1}.main .file-type{margin:.57rem .4rem 0 .875rem;width:1.51875rem;height:1.83125rem}.main .file-name{color:#373737;font-size:1.6rem}.main .file-detail{position:relative;color:#878787;font-size:.65rem}.main .file-detail span:last-child{margin-left:.3rem}.file-message{overflow:hidden;margin-top:.4rem;width:10rem}.file-message .file-name{overflow:hidden;width:100%;font-size:.8625rem}.main .file-starus a{color:#847f7f}.main .file-starus{margin-right:.5rem;line-height:5.6rem}.footer{position:absolute;bottom:0;left:0;width:100%;height:3.2rem;background:#2fbae1;box-shadow:0 0 9px #777;text-align:center;font-size:.935rem}.footer .waiting{position:relative;top:50%;margin-top:-5px}.footer a{display:block;height:100%;color:#fff;font-size:1.2rem;line-height:3.2rem}.margin-50{margin-left:2.25rem}.even{background:#f3f3f3}.warning{background:#ff1919}.main .warning a,.main .warning p{color:#fff}.main .warning .file-starus{position:absolute;top:0;right:0;text-align:right;line-height:100%}.main .warning .tips{color:#ffaeae;font-size:1.2rem}.fs-inner{margin-top:1.3rem}#detail{text-align:left}#open-download-file{display:block;height:100%}.head img{position:relative;top:-.125rem}#close{float:right;width:2.5625rem;height:100%;text-align:center;line-height:2.935rem;transition:background .2s}#close img{width:2.3rem;height:2.3rem;vertical-align:middle}#close:hover{background:#009dca}#connState{margin-bottom:3rem;color:#777;font-size:1.2rem}
    </style>
    <script>
        var width = window.innerWidth / 320 * 16;
        document.getElementsByTagName("html")[0].style.fontSize = width + "px";
    </script>
</head>

<body>
<div class="panel" id="detail">
    <div class="wrapper">
        <div class="head">
            <div class="fl head-icon-title">
                <img class="head-icon" src="<%=request.getContextPath() %>/resources/imgs/computrue.png"/>
                <span class="head-title">云打印-文件传输完成</span>
            </div>
        </div>
        <div class="main">
            <div class="main-title">
                <p class="main-title-text ">文件列表</p>
            </div>
            <ul class="file-container">
            </ul>
        </div>
        <div class="footer">
            <a id="start-print" href="###">点击开始自动打印</a>
        </div>
    </div>
</div>
</body>
<script src="<%=request.getContextPath() %>/resources/js/jquery.1.9.1.min.js"></script>
<script>
    $(function() {
        $.ajaxSetup({cache: false});
        var $home = $("#detail");
        var html = "";

        function getFileKN(filename) {
            if (/.doc$/.test(filename) || /.docx$/.test(filename)) return 'doc';
            if (/.pdf$/.test(filename)) return 'pdf';
            if (/.xls$/.test(filename) || /.xlsx$/.test(filename)) return 'xls';
            return 'file';
        }

        function isColor(isColorful) {
            return isColorful == 0 ? '黑白' : '彩色';
        }

        function readerFiles(file, index, status) {
            var isEven = index % 2 == 0 ? "" : "even";
            return '<li class="' + isEven + '">\
                        <img class="file-type fl" src="<%=request.getContextPath() %>/resources/imgs/' + getFileKN(file.filename) + '.png">\
                        <div class="fl file-message">\
                            <p class="file-name">' + file.filename + '</p>\
                            <p class="file-detail"><span>' + isColor(file.isColorful) + '</span><span>' + file.number + '份</span></p>\
                        </div>\
                    </li>';
        }

        $("#start-print").on('click', function() {
            var $this = $(this).html("请求中..");
            $.getJSON("/printfile/confirm", {
                md5code: '${hashMap.md5code}',
                openid: '${hashMap.openid}'
            }).done(function(data) {
                if(data.status === 0)
                    $this.html("已确认,请到打印机接收文件");
                else{
                    alert(data.message)
                }
            });
        })

        if('${hashMap.md5code}' == '') {
            alert("电脑客户端查找失败,请在电脑客户端点击手动连接或重启客户端");
        }else{
            var files = ${hashMap.files};
            if(!!files) {
                $.each(files, function(key, val) {
                    html += readerFiles(val, key);
                });
                $home.find(".file-container").html(html);
            }
        }
    })
</script>

</html>