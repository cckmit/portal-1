<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_document_member_added_to" value="${document_member_added_to}"/>
<@set name="_document_member_added_link" value="${document_member_added_link}"/>

<#noparse>
    <html>
    <head>
        <meta http-equiv="content-type" content="text/html; charset=utf-8">
    </head>
    <body bgcolor="#FFFFFF" text="#000000">
    <div style="margin-top: 12px">
        <div style="padding: 8px 0 4px;">
            <div style="font-family: sans-serif;font-size: 14px;">${_document_member_added_to} "${documentName}"</div>
            <div style="font-family: sans-serif;font-size: 14px;">${_document_member_added_link}: <a href="${url}">${url}</a></div>
        </div>
    </div>
    </body>
    </html>
</#noparse>