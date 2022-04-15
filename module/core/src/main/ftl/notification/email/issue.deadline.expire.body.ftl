<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_issue" value="${issue}"/>
<@set name="_will_be_closed" value="${will_be_closed}"/>
<@set name="_you" value="${you}"/>
<@set name="_notification_footer" value="${notification_footer_do_not_reply}"/>

<#noparse>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
</head>
<body bgcolor="#FFFFFF" text="#000000">
    <div style="margin-top: 12px">
        <div style="padding: 8px 0 4px;">
            <div style="font-family: sans-serif;font-size: 14px;">${_issue} <a href="${linkToCaseObject}">CRM-${caseNumber?c}</a> ${_will_be_closed}.</div>
        </div>
    </div>
    <div style="padding: 4px 0 8px;">
        <div style="color: #777777; font-size: 11px; font-family:sans-serif; margin: 20px 0; padding: 8px 0; border-top: 1px solid #D4D5D6;">
            ${_you} (<b>${userName!'?'}</b>) ${_notification_footer}
        </div>
    </div>
</body>
</html>
</#noparse>
