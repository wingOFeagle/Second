<%@page import="java.sql.*" import="java.util.*" import="java.io.*"
	contentType="text/html; charset=gb2312"%>
<html>
<head>
<title>MySqlTest</title>
<meta name="GENERATOR" content="Microsoft FrontPage 4.0">
<meta name="ProgId" content="FrontPage.Editor.Document">
</head>
<body>
	<p align="center">
		<u><font size="5">JSP</font></u>
	</p>
	<%!String host = "localhost";
	String database = "sgs";
	String user = "root"; 
	String pass = "";%>
	<%
		java.sql.Connection sqlConn; 
		java.sql.Statement sqlStmt;  
		java.sql.ResultSet sqlRst;  
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		sqlConn = java.sql.DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, user, pass);
		sqlStmt = sqlConn.createStatement();
		String sqlQuery = "select num from test2 where type=0";
		sqlRst = sqlStmt.executeQuery(sqlQuery);
		sqlRst.next();
		int count = sqlRst.getInt(1);
		String sqlUpdate =  String.format("update test2 set num=%d where type=0", count+1);
		sqlStmt.execute(sqlUpdate);
	%>
	There is  <%=count%>  comments!
</body>
<%
	sqlRst.close();
	sqlStmt.close();
	sqlConn.close();
%>