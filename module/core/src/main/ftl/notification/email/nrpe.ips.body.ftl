<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_you" value="${you}"/>
<@set name="_notification_footer" value="${notification_footer}"/>
<@set name="_nrpeNonAvailableIpsMailBodyMessage" value="${nrpeNonAvailableIpsMailBodyMessage}"/>
<#noparse>
<html>
    <head>
        <meta http-equiv="content-type" content="text/html; charset=utf-8">
        <style>
            .caption  {
                padding:2px 10px 2px 0;
                font-family:sans-serif;
                font-size:14px;
                text-align:left;
                color:#666666;
                border-bottom: 1px solid #D4D5D6;
            }

            .field  {
                padding:2px 10px 2px 0;
                font-family:sans-serif;
                font-size:14px;
                text-align:left;
            }
        </style>
    </head>
    <body bgcolor="#FFFFFF" text="#000000">
        <div>
            <div class="field">${_nrpeNonAvailableIpsMailBodyMessage}</div>
            <#list nonAvailableIps as nonAvailableIp>
            <div class="field">${nonAvailableIp}</div>
            </#list>
            <div style="padding: 4px 0 8px;">
                <div style="color: #777777; font-size: 11px; font-family:sans-serif; margin: 20px 0; padding: 8px 0; border-top: 1px solid #D4D5D6;">
                    ${_you} (<b>${userName}</b>) ${_notification_footer}
                    <#list recipients as recipient>
                        <#if recipient??>
                            ${recipient}<#sep>, </#sep>
                        </#if>
                    </#list>
                </div>
            </div>
        </div>
    </body>
</html>
</#noparse>