<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="col-md-2">
    <ul class="nav nav-pills nav-stacked" id="nav">
        <li><a href="${pageContext.request.contextPath}/student/editTeacher">我的课程<sapn class="glyphicon glyphicon-list-alt  pull-right" /></a></li>
        <%--<li><a href="${pageContext.request.contextPath}/student/selectedCourse">已选课程<span class="badge pull-right">5</span></a></li>--%>
        <%--<li><a href="${pageContext.request.contextPath}/student/overCourse">已修课程<span class="badge pull-right">8</span></a></li>--%>
        <li><a href="${pageContext.request.contextPath}/student/passwordRest">修改密码<sapn class="glyphicon glyphicon-pencil pull-right" /></a></li>
        <li><a href="${pageContext.request.contextPath}/logout">退出系统<sapn class="glyphicon glyphicon-log-out pull-right" /></a></li>

    </ul>
</div>