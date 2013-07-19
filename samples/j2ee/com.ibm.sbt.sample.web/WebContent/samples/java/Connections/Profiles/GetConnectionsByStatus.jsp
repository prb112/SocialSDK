<!-- 
/*
 * � Copyright IBM Corp. 2012
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */ -->
 
 <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="com.ibm.commons.runtime.Application"%>
<%@page import="com.ibm.commons.runtime.Context"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.ibm.sbt.services.client.connections.profiles.ProfileService"%>
<%@page import="com.ibm.sbt.services.client.connections.profiles.ConnectionEntry"%>
<%@page import="com.ibm.sbt.services.client.connections.profiles.ConnectionEntryList"%>
<%@page language="java" contentType="text/html; charset=ISO-8859-1"	pageEncoding="ISO-8859-1"%>
<html>
<head>
	<title>SBT JAVA Sample - Get Connections By Status</title>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<body>
	<div id="content">
	<%
		try {
			String userId = Context.get().getProperty("sample.id1");
			ProfileService connProfSvc = new ProfileService();
			Map<String, String> params = new HashMap<String, String>();
			params.put("inclMessage", "true");
	
			ConnectionEntryList entries = connProfSvc.getConnectionsColleagueEntriesByStatus(userId, "pending", params);
		
			if (entries != null && !entries.isEmpty()) {
				out.println("<b> User's colleagues </b>");
				out.println("<br>");
				for (Iterator iterator = entries.iterator(); iterator.hasNext();) {
					ConnectionEntry entry = (ConnectionEntry) iterator.next();
					out.println("<b>Invite : </b> " + entry.getTitle() +entry.getContent());
					out.println("<br>");
				}
			} else {
				out.println("No result");
			}
		} catch (Throwable e) {
			out.println("<pre>");
			out.println(e.getMessage());
			out.println("</pre>");
		}
	%>
	</div>
</body>
</html>
