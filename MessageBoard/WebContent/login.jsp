<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" type="text/css" href="css/login.css">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>登录</title>
</head>
<body>
	<form method="post" action="test">
		<div id="loginInfo">
			<div id="inner" >
				<label>用户名：</label> <input type="text" name="username" />
			</div>
			<div id="inner">
				<label>密码： </label><input type="password" name="password" />
			</div>
			<div id="inner">
				<input type="submit" value="登录" /> <input type="reset" value="重置" />
			</div>


		</div>
	</form>
</body>
</html>