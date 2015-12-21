<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>重邮云打印</title>
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
            <img src="/user/getqrcode" alt="dimension" width="220" class="wechat">
            <a href="instruction.html" id="wechat-status" target="_blank">第一次玩？点我2分钟搞懂</a>
        </div>
    </div>
    <div class="close"><a href="#"></a></div>
</div>
<div class="upload boundIn" id="upload">

    <form action="/printfile/upload" target="upframe" method="post" enctype="multipart/form-data" id="upload-box">
        <input type="hidden" name="id" value=""/>
        <div class="upload-content">
            <div class="upload-content-title">
                <span class="title">上传待打印文件</span>
                <span class="sub-title">单个文件请小于20M</span>
                <span id="closeX" class="close">X</span>
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
                    <p><label>仅上传，不立即打印:</label><input type="checkbox" name="status"/><span style="font-size: 13px;">(勾选后文件会分类到已上传,可重新下载和打印)</span></p>
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
                <li class="active" data-status="0" data-know="#tips-ready"><a href="#" class="content-tab-btn">待打印</a></li>
                <li data-status="1" data-know="#tips-already"><a href="#" class="content-tab-btn">已上传</a></li>
                <li data-status="2"><a href="#" class="content-tab-btn">已打印</a></li>
            </ul>
            <a class="btn uploadbtn pull-right" href="#" id="uploadfile">上传打印文件</a>
        </div>
        <div class="content-main">
            <div class="tips-ready" id="tips-ready">
                <span class="tri-up"></span>
                <span>待打印中的文件，在打印店电脑上微信扫描之后会自动下载，并可在手机端控制其打印。</span>
                <div class="tips-oper">
                    <a href="instruction.html" target="_blank">查看详情</a>
                    <a href="#" id="iknow-ready">我知道了</a>
                </div>
            </div>
            <div class="tips-ready" style="left: 87px" id="tips-already">
                <span class="tri-up"></span>
                <span>已上传的文件，不会立即打印，但可以点击功能按钮手动下载或者移入待打印</span>
                <div class="tips-oper">
                    <a href="instruction.html" target="_blank">查看详情</a>
                    <a href="#" id="iknow-already">我知道了</a>
                </div>
            </div>
            <div class="inner-main">
                <div class="content-text nofile">
                    <i class="fa fa-commenting-o"></i>暂无文件
                </div>
            </div>
        </div>
        <div class="pages" id="pages"></div>
    </div>
    <div class="bottom wrap" id="bottom">
        <div class="bottom-title">
            <div class="pull-left">
                <span class="title">文件上传列表</span>
                <span class="sub-title">仅显示本次上传内容</span>
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