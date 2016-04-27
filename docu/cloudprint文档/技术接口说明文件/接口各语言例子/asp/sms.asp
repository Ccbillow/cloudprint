<%@LANGUAGE="VBSCRIPT" %>

<% 
On error resume next

Dim SmsUrl,SmsPost,https
SmsUrl="http://web.cr6868.com/asmx/smsservice.aspx"
SmsPost="name=sdss&pwd=1aksjdflkjsdlkfjs23456&content=Server.URLEncode(通知：2013-7-10开会)&mobile=13956085463&extno=&stime=&sign=Server.URLEncode(社科联)&type=pt"

Set https = Server.CreateObject("MSXML2.XMLHTTP") 
With https 
	.Open "Post", SmsUrl, False
	.setRequestHeader "Content-Type","application/x-www-form-urlencoded"
	.setRequestHeader "Content-Length","length"
	.Send SmsPost
End With 
Set https = Nothing 
	
	
Response.Write(SmsUrl)
	
	
 %>