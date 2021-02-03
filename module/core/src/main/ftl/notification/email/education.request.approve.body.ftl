<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_you" value="${you}"/>
<@set name="_notification_footer" value="${notification_footer}"/>
<@set name="_educationRequest" value="${educationRequest}"/>

<#noparse>
    <html>
    <head>
        <meta http-equiv="content-type" content="text/html; charset=utf-8">
    </head>
    <body bgcolor="#FFFFFF" text="#000000">
    <div>
        ${_educationRequest}
    </div>

    <div>
        <div style="padding: 4px 0 8px;">
            <div style="color: #777777; font-size: 11px; font-family:sans-serif; margin: 20px 0; padding: 8px 0; border-top: 1px solid #D4D5D6;">
                ${_you} (<b>${userName}</b>) ${_notification_footer}
                <#list recipients as recipient>
                <#if recipient??>
                ${recipient}<#sep>, </#sep>
        </#if>
    </#list>
    </div>
    </body>
    </html>
</#noparse>