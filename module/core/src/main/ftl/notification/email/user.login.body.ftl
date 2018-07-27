<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_greetings" value="${greetings}"/>
<@set name="_your_account_is_ready" value="${your_account_is_ready}"/>
<@set name="_your_login" value="${your_login}"/>
<@set name="_your_password" value="${your_password}"/>
<@set name="_you" value="${you}"/>
<@set name="_notification_footer" value="${notification_footer_do_not_reply}"/>

<#noparse>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
</head>
<body bgcolor="#FFFFFF" text="#000000">
    <div style="padding: 8px 0 4px;">
        <div style="font-family: sans-serif;font-size: 14px;">${_greetings}<#if hasDisplayName>, ${displayName!'?'}</#if>!</div>
        <div style="font-family: sans-serif;font-size: 14px;">%{_your_account_is_ready} ${url}</div>
        <div style="font-family: sans-serif;font-size: 14px;">${_your_login}: ${login}</div>
        <div style="font-family: sans-serif;font-size: 14px;">${_your_password}: ${password}</div>
    </div>
    <div style="padding: 4px 0 8px;">
        <div style="color: #777777; font-size: 11px; font-family:sans-serif; margin: 20px 0; padding: 8px 0; border-top: 1px solid #D4D5D6;">
            ${_you} (<b>${userName!'?'}</b>) ${_notification_footer}.
        </div>
    </div>
</body>
</html>
</#noparse>