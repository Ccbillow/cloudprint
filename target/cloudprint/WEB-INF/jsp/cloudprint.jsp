<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title></title>
    <link type="text/css" rel="stylesheet" href="<%=request.getContextPath() %>/resources/css/main.css">
    <link type="text/css" rel="stylesheet" href="<%=request.getContextPath() %>/resources/css/Font-Awesome/font-awesome.css">
    <link rel="stylesheet" href="<%=request.getContextPath() %>/resources/css/Font-Awesome/font-awesome-ie7.min.css">
</head>
<body>
<iframe src="#" frameborder="0" name="upframe" style="display: none;"></iframe>
<div class="mask" id="mask"></div>
<div id="login_frame" style="margin-top: -215px;" class="boundIn">
    <img src="<%=request.getContextPath() %>/resources/imgs/logo.png" height="46" class="logo">
    <div style="display: none;" class="login">
        <div class="holder">
            <div class="with-line">使用微信扫描登陆</div>
            <img src="/user/getQRCode" alt="dimension" width="220" class="wechat">
            <p id="wechat-status">微信扫描登陆</p>
        </div>
    </div>
    <div class="close"><a href="#">x</a></div>
</div>
<!--<div id="login_frame" style="margin-top: -215px;" class="boundIn">
    <img src="./imgs/logo.png" height="46" class="logo">
    <div class="sign-up">
        <div class="holder">
            <div class="with-line">云打印注册</div>
            <form action="/auth/" method="post" class="mail-login" id="register-form">
                <input type="text" name="email" placeholder="手机号码" class="clear-input" data-reg="\d{11}" data-info="手机号码输入有误"/>
                <input name="password" type="password" placeholder="密码" class="clear-input" data-reg="[0-9a-zA-Z]{6,14}" data-info="密码是6-14位数字或者密码"/>
                <span class="sep"></span>
                <div class="clearfix">
                    <input type="text" placeholder="验证码" name="code" class="pull-left clear-input" style="width: 156px;margin-right: 10px"/>
                    <a href="#" onclick="return false;" style="width:85px" class="btn btn18 rbtn pull-left btn-normal" id="getCode-register">
                        <span class="text">获取验证码</span>
                    </a>
                </div>

                <a href="#" onclick="return false;" class="btn btn18 rbtn" id="">
                    <span class="text">注册</span>
                </a>
            </form>
            <div class="switch">已有帐号？
                <a class="brown-link" href="#" id="to-login">登录</a>
            </div>
        </div>
    </div>
    <div style="display: none;" class="login">
        <div class="holder">

            <div class="with-line">使用手机登录</div>
            <form action="/auth/" method="post" class="mail-login" id="login-form">
                <input type="hidden" name="_ref" value="frame">
                <input type="text" name="email" placeholder="手机号码" class="clear-input">
                <input name="password" type="password" placeholder="密码" class="clear-input">
                <a href="#" onclick="return false;" class="btn btn18 rbtn" id="login">
                    <span class="text"> 登录</span>
                </a>
            </form>
            <a class="reset-password red-link" id="to-password" href="#">忘记密码»</a>
            <div class="switch-back">还没有云打印帐号？<a class="red-link" href="#" id="to-reg">点击注册»</a></div>
        </div>
    </div>
    <div style="display: none" class="reset mail-login">
        <div class="holder">
            <div class="with-line">找回密码</div>
            <form class="reset-form">
                <input type="text" name="mobile" placeholder="输入注册手机号码" class="clear-input">
                <input type="text" name="mobile" placeholder="新密码" class="clear-input">
                <span class="sep"></span>
                <div class="clearfix">
                    <input type="text" placeholder="验证码" name="code" class="pull-left clear-input" style="width: 156px;margin-right: 10px"/>
                    <a href="#"style="width: 85px"  onclick="return false;" class="btn btn18 rbtn pull-left btn-normal" id="getCode-find">
                        <span class="text">获取验证码</span>
                    </a>
                </div>
                <a href="#" onclick="return false;" class="btn btn18 rbtn" id="reset-get-code" data-click="true">
                    <span class="text">确定</span>
                </a>
            </form>
            <a class="back red-link switch-back" id="to-login2" href="#">又想起来了»</a>
        </div>
    </div>
    &lt;!&ndash;<div class="email-signup">
        <div style="display: none" class="signup-success">
            <div class="with-line">注册成功</div>
            <div class="text">验证邮件已经发送到<span class="email">email</span>，请<a href="" target="_blank" class="check-mail red-link">点击查收邮件</a>激活账号。
                <br>没有收到激活邮件？请耐心等待，或者<a class="resend red-link disabled">重新发送<span>30</span></a></div><a class="login-link red-link">« 返回登录页</a></div>
        <div style="display: none" class="signup-form">
            <div class="holder">
                <div class="with-line">使用邮箱注册</div>
                <form action="" method="post">
                    <input type="text" name="email" placeholder="邮箱" class="clear-input">
                    <input type="text" name="captcha" value="" placeholder="验证码" class="clear-input input-captcha">
                    <input type="hidden" name="challenge" value="">
                    <a title="换一个" class="captcha"><img></a><a href="#" onclick="return false;" class="btn btn18 rbtn"><span class="text"> 注册</span></a></form><a class="email-signup-back brown-link">« 返回第三方帐号登录</a></div>
        </div>
    </div>&ndash;&gt;
    <div class="close"><a href="#">x</a></div>
</div>-->


<div class="upload boundIn" id="upload">
    <form action="/printfile/upload" target="upframe" method="post" enctype="multipart/form-data" id="upload-box">
        <input type="hidden" name="id" value=""/>
        <div class="upload-content">
            <div class="upload-content-title">
                上传待打印文件<span id="closeX">X</span>
            </div>
            <div class="upload-content-sets">
                <div class="upload-content-set">
                    <h1 class="title"><label>文件:</label></h1>
                    <p><label class="left">选择文件:</label><input type="file" name="file"></p>
                </div>
                <div class="upload-content-set">
                    <h1 class="title"><label>页面设置:</label></h1>
                    <p><label class="left">纸张:</label>
                        <select>
                            <option>A4</option>
                        </select>
                    </p>
                    <p>
                        <label class="left">份数:</label>
                        <select name="number">
                            <option value="1">1</option>
                            <option value="2">2</option>
                            <option value="3">3</option>
                            <option value="4">4</option>
                            <option value="5">5</option>
                            <option value="6">6</option>
                            <option value="7">7</option>
                            <option value="8">8</option>
                            <option value="9">9</option>
                            <option value="10">10</option>
                            <option value="11">11</option>
                            <option value="12">12</option>
                            <option value="13">13</option>
                            <option value="14">14</option>
                            <option value="15">15</option>
                            <option value="16">16</option>
                            <option value="17">17</option>
                            <option value="18">18</option>
                            <option value="19">19</option>
                            <option value="20">20</option>
                            <option value="21">21</option>
                            <option value="22">22</option>
                            <option value="23">23</option>
                            <option value="24">24</option>
                            <option value="25">25</option>
                            <option value="26">26</option>
                            <option value="27">27</option>
                            <option value="28">28</option>
                            <option value="29">29</option>
                            <option value="30">30</option>
                        </select>
                        <span class="set">份</span>
                    </p>
                    <!--<p><label>页码范围:</label></p>-->
                </div>
                <div class="upload-content-set">
                    <h1 class="title"><label>打印设置:</label></h1>
                    <p><label>打印完成后自动删除:</label><input type="checkbox" checked="checked" name="isDelete"/></p>
                    <p><label>彩色打印:</label><input type="checkbox" name="isColorful"/></p>
                    <p><label>仅上传，不立即打印:</label><input type="checkbox" name="status"/></p>
                </div>
            </div>
        </div>
    </form>
    <div class="upload-buttons">
        <p>
            <a class="btn ok" href="#" id="upload-btn-ok">确定</a>
            <a class="btn cancel" href="#" id="closeBtn">取消</a>
        </p>
    </div>
</div>

<div class="container">
    <div class="two-dimension">
        <div class="two-dimension-img">
            <img src="<%=request.getContextPath() %>/resources/imgs/code.png" alt="dimension"/>
        </div>
        <div class="two-dimension-text">扫描下载云打印app</div>
    </div>
    <div class="top">
        <div class="title menu-list">
            <span class="btn">重邮云打印<i class="ml5 fa fa-angle-down"></i></span>
             <ul id="menu-main" class="menu animate fadeIn">
                 <li><a href="###">使用说明</a></li>
                 <li class="sep"></li>
                 <li><a href="###">版本介绍</a></li>
             </ul>
        </div>
        <div class="user menu-list" id="user">
            <img src="<%=request.getContextPath() %>/resources/imgs/user.png" title="img" class="pull-right">
            <span class="pull-right btn"><span id="user-name">登陆</span><i class="ml5 fa fa-angle-down"></i></span>

            <ul class="menu animate fadeIn" id="menu-user">
                <li><a href="###" id="logout">注销</a></li>
            </ul>
        </div>
    </div>
    <div class="content wrap" id="page-content">
        <div class="content-tab clearfix">
            <ul class="pull-left clearfix">
                <li class="active" data-status="0"><a href="#" class="content-tab-btn">待打印</a></li>
                <li data-status="1"><a href="#" class="content-tab-btn">已上传</a></li>
                <li data-status="2"><a href="#" class="content-tab-btn">已打印</a></li>
            </ul>
            <a class="btn uploadbtn pull-right" href="#" id="uploadfile">上传打印文件</a>
        </div>
        <div class="content-main">
            <div class="content-text nofile">
                <i class="fa fa-commenting-o"></i>暂无文件
            </div>
            <!--<div class="content-text">
                <i class="fa fa-file-o"></i>
                <span>2012214890谢鹏程.doc</span>
                <div class="icons">
                    <a href="#"><i class="fa fa-print"></i></a>
                    <a href="#"><i class="fa fa-trash"></i></a>
                    <a href="#"><i class="fa fa-share-square-o"></i></a>
                </div>
            </div>-->
        </div>
        <div class="pages" id="pages"></div>
    </div>
    <div class="bottom wrap" id="bottom">
        <div class="bottom-title">
            <div class="pull-left">
                <span class="title">文件上传列表</span>
                <span class="sub-title"></span>
            </div>
            <div class="pull-right">
                <i class="fa fa-angle-up fa-2x" id="print-up"></i>
            </div>

        </div>
        <ul>
        </ul>
    </div>
</div>
<script>
    if(!('boxShadow' in document.body)) {
        document.getElementsByTagName('html')[0].className = 'no-boxshadow';
    }
</script>
<script src="<%=request.getContextPath() %>/resources/js/sea-debug.js"></script>
<script>
    seajs.config({
        base: "./resources/mdl/",
        debug: true,
        alias: {
            "jquery": "../js/jquery"
        }
    });
    seajs.use("index");
</script>
</body>
</html>