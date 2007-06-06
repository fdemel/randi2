<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page import="de.randi2.model.fachklassen.beans.*"
	import="de.randi2.controller.DispatcherServlet"
	import="java.util.GregorianCalendar"
	import="java.text.SimpleDateFormat" import="java.util.*"%>

<%
			Iterator listeZentren = ((Vector) request
			.getAttribute("listeZentren")).iterator();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
       "http://www.w3.org/TR/html4/strict.dtd">
<html >
<head>
<link rel="stylesheet" type="text/css" href="css/style.css">
<script language="Javascript" src="js/motionpack.js"> </script>
<title>Randi2 :: Benutzer anlegen</title>
<script type="text/javascript">
<!--
	function hideFilter(){
		document.getElementById('filterdiv').style.display = 'none';
	}
//-->
</script>
<link rel="stylesheet" type="text/css" href="js/ext/resources/css/ext-all.css" />
<!-- GC --> <link rel="stylesheet" type="text/css" href="../../resources/css/xtheme-gray.css" /><!-- LIBS -->     <script type="text/javascript" src="js/ext/adapter/yui/yui-utilities.js"></script>     <script type="text/javascript" src="js/ext/adapter/yui/ext-yui-adapter.js"></script>     <!-- ENDLIBS -->
<script type="text/javascript" src="js/ext/ext-all.js"></script>
<script type="text/javascript" src="js/benutzer_anlegen_zwei.js"></script>
</head>
<body onload="hideFilter();">
<%@include file="include/inc_header_clean.jsp"%>

<div id="content">

<h1>Benutzer anlegen</h1>
<%@include file="include/inc_nachricht.jsp"%>

<fieldset style="width: 90%;"><legend><b>Zentrum
suchen </b></legend>
<form action="DispatcherServlet" method="POST"><input
	type="hidden" name="anfrage_id"
	value="<%=DispatcherServlet.anfrage_id.JSP_BENUTZER_ANLEGEN_ZWEI_BENUTZER_REGISTRIEREN_DREI.name() %>">
	
<img alt="Filter anzeigen" src="images/find.png" onmousedown="toggleSlide('filterdiv');" title="Filter anzeigen" style="cursor:pointer"/>
Filter
<div id="filterdiv" style="overflow:hidden; height: 75px;">
<table width="90%">
	<tr>
		<td>
			Name&nbsp;der&nbsp;Institution:
		</td>
		<td>
			Name&nbsp;der&nbsp;Abteilung:
		</td>
		<td>
			&nbsp;
		</td>
	</tr>
	<tr>
		<td>
			<input type="Text" name="name_institution" value="" size="30"
	maxlength="50"/>
		</td>
		<td>
			<input type="Text"
	name="name_abteilung" value="" size="30" maxlength="50"/>
		</td>
		<td>
			<input
	type="submit" name="Filtern" value="Filtern"/>
		</td>
	</tr>
</table>
</div>
	
	
<table width="90%" id="zentren">
	<thead align="left" >
		<tr style="background:#eeeeee;">
			<th width="40%">Name der Institution</th>
			<th width="30%">Abteilung</th>
			<th width="20%">Passwort</th>
			<th width="20%">Aktion</th>
		</tr>
	</thead>

	<%
		String reihe = "tblrow1";
		int tabindex = 1;
		while (listeZentren.hasNext()) {
			ZentrumBean aktuellesZentrum = (ZentrumBean) listeZentren
			.next();
	%>

	<tr class="<%=reihe %>">
		<td><%=aktuellesZentrum.getInstitution()%></td>
		<td><%=aktuellesZentrum.getAbteilung()%></td>
		<td><input type="password"
			name="zentrum_passwort<%=aktuellesZentrum.getId() %>"
			tabindex="<%=tabindex %>" z:required="true"
			z:message="Passwort erforderlich"></input></td>
		<td><input type="submit"
			name="bestaetigen<%=aktuellesZentrum.getId() %>" value="weiter"></input>
		</td>
	</tr>
	<%
			tabindex++;
		if (reihe.equals("tblrow1"))
				reihe = "tblrow2";
			else
				reihe = "tblrow1";
		}
	%>

</table>
</form>
</fieldset>

<form>
<table>
	<tr>
		<td><input type="button" name="abbrechen" value="Zur&uuml;ck"
			tabindex="2" onClick="location.href='benutzer_anlegen_eins.jsp'"></td>
	</tr>
</table>
</form>
<%@include file="include/inc_footer_clean.jsp"%>
</div>

</body>
</html>
