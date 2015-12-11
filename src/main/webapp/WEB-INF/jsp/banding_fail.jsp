<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no"/>
	<title>云打印-登陆失败</title>
	<style>
		body{margin: 0;font-family: Microsoft YaHei;text-align: center;}
		.icon{margin-top: 3rem;width:6.4rem;}
		.status{margin-top: 1rem;font-size: 1.2rem;}
	</style>
	<script>
		var width = window.innerWidth / 320 * 16;
		document.getElementsByTagName("html")[0].style.fontSize = width + "px";
	</script>
</head>
<body>
<div class="container">
	<img class="icon" src="<%=request.getContextPath() %>/resources/imgs/logo.png"/>
	<p class="status">系统出了一点小差，请稍微重试..</p>
</div>
</body>
</html>