<%@page import="java.sql.*" import="java.util.*" import="java.io.*"
	contentType="text/html; charset=gb2312"%>
<html>
<head>
<title>MySql</title>
<meta name="GENERATOR" content="Microsoft FrontPage 4.0">
<meta name="ProgId" content="FrontPage.Editor.Document">
</head>
<body>
	<p align="center">
		<u><font size="5" face="华文新魏">JSP</font></u>
	</p>
	<%!String host = "localhost"; //数据库主机 
	String database = "sgs"; //数据库名 
	String user = "root"; //用户名 
	String pass = ""; //口令%>
	<%
		java.sql.Connection sqlConn; //数据库连接对象 
		java.sql.Statement sqlStmt; //语句对象 
		java.sql.ResultSet sqlRst; //结果集对象 
		//登记JDBC驱动对象 
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		//连接数据库 
		sqlConn = java.sql.DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, user, pass);
		//创建语句对象 
		sqlStmt = sqlConn.createStatement(java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
		//执行Sql语句 
		String sqlQuery = "select * from test";
		sqlRst = sqlStmt.executeQuery(sqlQuery);
		//sqlRst.next();
		int count = 1; //取得总的留言记录数
	%>
	Total<%=count%>comments
	<table border="1" width="100%" bordercolorlight="#CC99FF"
		cellpadding="2" bordercolordark="#FFFFFF" cellspacing="0">
		<tr>
			<td align="center">ID</td>
			<td align="center">Name</td>
			<td align="center">Sex</td>
			<td align="center">Age</td>
		</tr>
		<%
			while (sqlRst.next())
			{ //取得下一条记录
		%>
		<tr>
			<!--显示记录-->
			<td align="center"><%=sqlRst.getString("id")%></td>
			<td align="center"><%=sqlRst.getString("name")%></td>
			<td align="center"><%=sqlRst.getString("sex")%></td>
			<td align="center"><%=sqlRst.getInt("age")%></td>
		</tr>
		<%
			}
		%>
	</table>
</body>
<%
	//关闭结果集对象 
	sqlRst.close();
	//关闭语句对象 
	sqlStmt.close();
	//关闭数据库连接 
	sqlConn.close();
%>