<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no"/>
    <title>云打印-扫码成功</title>
    <style>
        body{margin: 0;font-family: Microsoft YaHei;text-align: center;}
        .icon{margin-top: 3rem;width:6.4rem;}
        .status{margin-top: 1rem;font-size: 1.2rem;}
        a{text-decoration: none;}
        #confirm{position: absolute;left:0;bottom: 0;height: 3rem;width:100%;color: #fff; background: #474747;
            line-height: 3rem;}
    </style>
    <script>
        var width = window.innerWidth / 320 * 16;
        document.getElementsByTagName("html")[0].style.fontSize = width + "px";
    </script>
</head>
<body>
<div class="container">
    <img class="icon" src="<%=request.getContextPath() %>/resources/imgs/logo.png">
    <p class="status">扫码成功</p>
    <p>md5code:${param.md5code}</p>
    <p>openid:${param.openid}</p>
</div>
<a href="#" id="confirm">确认打印</a>
</body>
<script src="<%=request.getContextPath() %>/resources/js/zepto.min.js"></script>
<script>
    //openid, status, message, md5code
    $(function() {
        var url = window.location.href;
        alert(${param.md5code});
        alert('${param.openid}');
        $("#confirm").on("click", function() {
            $.get("/printfile/confirm", {
                md5code: '${param.md5code}',
                openid: '${param.openid}'
            }, function(data) {
                alert(data);
            })
        })
    })

</script>
</html>