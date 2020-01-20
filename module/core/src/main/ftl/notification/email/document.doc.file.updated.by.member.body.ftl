<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_document_doc_file_employee" value="${document_doc_file_employee}"/>
<@set name="_document_doc_file_updated_action" value="${document_doc_file_updated_action}"/>
<@set name="_document_doc_file_comment" value="${document_doc_file_comment}"/>

<#noparse>
    <html>
    <head>
        <meta http-equiv="content-type" content="text/html; charset=utf-8">
    </head>
    <body bgcolor="#FFFFFF" text="#000000">
    <div style="margin-top: 12px">
        <div style="padding: 8px 0 4px;">
            <div style="font-family: sans-serif;font-size: 14px;">${_document_doc_file_employee} (${initiatorName}) ${_document_doc_file_updated_action} "${documentName}"</div>
            <div style="font-family: sans-serif;font-size: 14px;">${_document_doc_file_comment}: ${comment}</div>
        </div>
    </div>
    </body>
    </html>
</#noparse>