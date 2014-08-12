<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core" xmlns:tags="urn:jsptagdir:/WEB-INF/tags/project" xmlns:display="urn:jsptld:http://displaytag.sf.net">
  <jsp:directive.page contentType="text/html; charset=UTF-8"/>
  <jsp:directive.page import="org.displaytag.sample.*"/>
  <jsp:scriptlet> request.setAttribute( "test", new TestList(10, false) );</jsp:scriptlet>
  <tags:page>
    <h2>Data exporting</h2>
    <display:table name="test" export="true" id="currentRowObject">
      <display:setProperty name="export.rtf.filename" value="example.rtf"/>
      <display:column property="id" title="ID"/>
      <display:column property="email"/>
      <display:column property="status"/>
      <display:column property="longDescription" media="csv excel xml pdf" title="Not On HTML"/>
      <display:column property="date"/>
      <display:column media="html" title="URL">
        <a href="${currentRowObject.url}">${currentRowObject.url}</a>
      </display:column>
      <display:column media="csv excel" title="URL" property="url"/>
      <display:setProperty name="export.pdf" value="true"/>
    </display:table>
    <p>
      When you set the Table Tag's
      <strong>export</strong>
      attribute to "true", a footer will appear below the table which will allow you to export the data being shown in various formats, just click on the format.
    </p>
    <p>
      If you need to change what you output based on the destination, use the
      <strong>media</strong>
      attribute of the Column Tag. In this example, we are making the url a hyperlink in html, we are just outputting it plain in csv and excel, and are skipping the column altogether in xml. Valid values for the media
      tag are 'html', 'xml', 'csv', and 'excel'.
    </p>
    <p>
      Please note that the basic export functionality will
      <strong>not</strong>
      work when the JSP page is included in another page via a jsp:include or the RequestDispatcher. Front end frameworks such as Struts and Tiles will do this behind the scenes. If you want to use export functionality
      in any of these scenarios, you must configure an export filter (configuration is explained in the displaytag documentation). Also, make sure you check the FAQ on displaytag website.
    </p>
  </tags:page>
</jsp:root>