<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_you" value="${you}"/>
<@set name="_notification_footer" value="${notification_footer}"/>
<@set name="_reservedIpActionCreated" value="${reservedIpActionCreated}"/>
<@set name="_reservedIpActionUpdated" value="${reservedIpActionUpdated}"/>
<@set name="_reservedIpActionRemoved" value="${reservedIpActionRemoved}"/>
<@set name="_reservedIpOwner" value="${reservedIpOwner}"/>
<@set name="_reservedIpIpAddress" value="${reservedIpIpAddress}"/>
<@set name="_reservedIpMacAddress" value="${reservedIpMacAddress}"/>
<@set name="_reservedIpReserveDate" value="${reservedIpReserveDate}"/>
<@set name="_reservedIpReleaseDate" value="${reservedIpReleaseDate}"/>
<@set name="_reservedIpComment" value="${reservedIpComment}"/>
<#--<@set name="_reservedIpCheckInfo" value="${reservedIpCheckInfo}"/>-->

<#noparse>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
</head>
<body bgcolor="#FFFFFF" text="#000000">
<div>
    <div style="padding: 5px;font-size: 14px;background:#f0f0f0;color:#666666;">
        <#if is_created == true>${_reservedIpActionCreated}</#if>
        <#if is_updated == true>${_reservedIpActionUpdated}</#if>
        <#if is_removed == true>${_reservedIpActionRemoved}</#if>
        ${_reservedIpAction}
    </div>
    <table>
        <tbody>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">${_reservedIpOwner}</td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">${ipOwner}</td>
            </tr>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">${_reservedIpIpAddress}</td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">${ipAddress}</td>
            </tr>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">${_reservedIpMacAddress}</td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">${macAddress}</td>
            </tr>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">${_reservedIpReserveDate}</td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">${reserveDate}</td>
            </tr>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">${_reservedIpReleaseDate}</td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">${releaseDate}</td>
            </tr>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">${_reservedIpComment}</td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">${comment}</td>
            </tr>
<#--            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">${_reservedIpCheckInfo}</td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">${reservedIpCheckInfo}</td>
            </tr>-->
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