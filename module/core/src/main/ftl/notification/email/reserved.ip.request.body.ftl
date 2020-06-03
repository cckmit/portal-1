<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_you" value="${you}"/>
<@set name="_notification_footer" value="${notification_footer}"/>
<@set name="_reservedIpOwner" value="${reservedIpOwner}"/>
<@set name="_reservedIpReserveDate" value="${reservedIpReserveDate}"/>
<@set name="_reservedIpReleaseDate" value="${reservedIpReleaseDate}"/>
<@set name="_reservedIpComment" value="${reservedIpComment}"/>
<@set name="_reservedIpList" value="${reservedIpList}"/>

<#noparse>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
</head>
<body bgcolor="#FFFFFF" text="#000000">
<div>
    <table>
        <tbody>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">${_reservedIpOwner}</td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">${reservedIpOwner}</td>
            </tr>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">${_reservedIpReserveDate}</td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">${reservedIpReserveDate}</td>
            </tr>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">${_reservedIpReleaseDate}</td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">${reservedIpReleaseDate}</td>
            </tr>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">${_reservedIpComment}</td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">${reservedIpComment}</td>
            </tr>

            <#if hasReservedIps>
                <tr>
                    <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                       ${_reservedIpList}
                    </td>
                </tr>
                <#list reservedIpList as reservedIp>
                <tr id="createdIps">
                    <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                        ${_reservedIpList}
                    </td>
                    <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                        <div style="display:inline-block;padding:1px 4px 1px 0px;white-space:nowrap;text-decoration:none;color:#0062ff">
                            ${reservedIp.ipAddress} :: ${reservedIp.macAddress} :: ${reservedIp.checkInfo}
                        </div>
                    </td>
                </tr>
            </#list>
            </#if>

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