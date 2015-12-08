<!DOCTYPE html>
 <html lang="en">
 <head>
 	<meta charset="UTF-8">
 	<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no"/>
 	<title>Document</title>
 
 	<style type="text/css">
   
   
   			body{
   				padding: 0;
   				margin: 0;
   				font-family: Microsoft YaHei;
   				font-size: 62.5%;
   				text-align: center;
   			}
   
   			.icon{
                				margin-top: 3rem;
                				width:6.4rem;
                				height:4.6rem;
                			}
   
   			p.status{
                				margin-top: 1rem;
                				font-size: 1.2rem;
                
                			}
   
   	</style>
 	<script type="text/javascript" src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
 	<script type="text/javascript" src="<%=request.getContextPath() %>/resources/js/zepto.min.js"></script>
 </head>
 <body ontouchstart="">
 
 
 	<div class="container">
   	<img class="icon" src="<%=request.getContextPath() %>/resources/imgs/banding_fail.png">
   	<p class="status">登陆失败</p>
   	</div>
 
 
 </body>
 </html> 
