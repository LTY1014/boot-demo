<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${title}</title>
    <!-- 引入LayUI -->
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/layui/2.8.18/css/layui.css">
</head>
<body class="layui-container" style="margin-top: 30px;">
    <div class="layui-card">
        <div class="layui-card-header">${name}</div>
        <div class="layui-card-body">
            <!-- 普通变量 -->
            <p>状态：<span class="layui-badge layui-bg-green">运行成功</span></p>
            <hr>
            <!-- 用户信息（LayUI表格） -->
            <table class="layui-table" lay-width="300">
                <tr><td>ID</td><td>${user.id}</td></tr>
                <tr><td>姓名</td><td>${user.username}</td></tr>
                <tr><td>年龄</td><td>${user.age}</td></tr>
            </table>
            <hr>
            <!-- 兴趣列表（LayUI列表） -->
            <ul class="layui-list">
                <#list hobbyList as hobby>
                    <li class="layui-list-item">${hobby_index+1}. ${hobby}</li>
                </#list>
            </ul>
            <!-- 条件判断（LayUI按钮） -->
            <#if user.age gte 18>
                <button class="layui-btn layui-btn-primary">已成年</button>
            <#else>
                <button class="layui-btn layui-btn-danger">未成年</button>
            </#if>
        </div>
    </div>
    <!-- LayUI JS（放body末尾） -->
    <script src="https://cdn.bootcdn.net/ajax/libs/layui/2.8.18/layui.js"></script>
</body>
</html>