<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
    <@block name="seo">
        <meta name="description" content="OA服务">
        <meta name="author" content="子华">
        <title>欢迎使用OA服务</title>
    </@block>

    <link href="${request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
    <@block name="customCss"></@block>
    <!--[if lt IE 9]>
    <script src="${request.contextPath}/js/html5shiv.min.js"></script>
    <script src="${request.contextPath}/js/respond.min.js"></script>
    <![endif]-->
</head>

<body>
<@block name="content">
    <h1>默认内容。。。</h1>
</@block>
<script src="${request.contextPath}/js/jquery-3.3.1.min.js"></script>
<script src="${request.contextPath}/js/bootstrap.min.js"></script>
<@block name="customJs"></@block>
</body>
</html>