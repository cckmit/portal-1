<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_case_object_deadline_expire" value="${case_object_deadline_expire}"/>

<#noparse>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
</head>
<body bgcolor="#FFFFFF" text="#000000">
    <div style="margin-top: 12px">
        <div style="padding: 8px 0 4px;">
            <div style="font-family: sans-serif;font-size: 14px;">${_case_object_deadline_expire} <a href="${linkToCaseObject}">CRM-${caseNumber?c}</a>.</div>
        </div>
    </div>
</body>
</html>
</#noparse>
