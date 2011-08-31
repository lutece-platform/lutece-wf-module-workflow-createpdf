<jsp:useBean id="doDownloadPDF" scope="session" class="fr.paris.lutece.plugins.workflow.modules.createpdf.utils.DoDownloadPDF" /><%
	 doDownloadPDF.doDownloadFile(request,response);
%>
