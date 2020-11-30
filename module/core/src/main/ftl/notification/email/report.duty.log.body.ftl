<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_you" value="${you}"/>
<@set name="_notification_footer" value="${notification_footer}"/>
<@set name="_name" value="${notificationReportName}"/>
<@set name="_created" value="${notificationReportCreated}"/>
<@set name="_creator" value="${notificationReportCreator}"/>

<#noparse>
<html>
    <head>
        <meta http-equiv="content-type" content="text/html; charset=utf-8">
    </head>
    <body bgcolor="#FFFFFF" text="#000000">
        <div style="margin-top: 12px">
            <table>
                <tbody>
                    <tr>
                        <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                            ${_name}
                        </td>
                        <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                            ${name}
                        </td>
                    </tr>
                    <tr>
                        <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                            ${_creator}
                        </td>
                        <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                            ${creator}
                        </td>
                    </tr>
                    <tr>
                        <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                            ${_created}
                        </td>
                        <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                            ${created?datetime}
                        </td>
                    </tr>
                </tbody>
            </table>
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
