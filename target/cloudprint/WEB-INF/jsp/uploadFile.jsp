<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <style type="text/css">
        div{
            height: 100px;
            width: 100%;
            background: red
        }
    </style>
    <title></title>
</head>
<body>
<form action="printFile/upload" method="post" enctype="multipart/form-data" accept-charset="UTF-8">
    <input type="file" name="file"/><br>
    类型：<input type="text" name="type"/><br>
    打印数量：<input type="text" name="number"/><br>
    文件状态：<input type="text" name="status"/><br>
    是否彩印：<input type="text" name="isColorful"/><br>
    打印完成后是否删除：<input type="text" name="isDelete"/><br>
    <input type="submit" value="submit"/><br>
</form>
</body>
</html>