<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_you" value="${you}"/>
<@set name="_notification_footer" value="${notification_footer}"/>
<@set name="_reservedIpIpAddress" value="${reservedIpIpAddress}"/>
<@set name="_reservedIpOwner" value="${reservedIpOwner}"/>
<@set name="_reservedIpUsePeriod" value="${reservedIpUsePeriod}"/>
<@set name="_reservedIpForever" value="${reservedIpForever}"/>
<@set name="_reservedIpComment" value="${reservedIpComment}"/>
<@set name="_reservedIpInstructionStart" value="${reservedIpInstructionStart}"
<@set name="_reservedIpInstructionEnding" value="${reservedIpInstructionEnding}"
<@set name="_reservedIpInstructionPortal" value="${reservedIpInstructionPortal}"
<@set name="linkTo" value="${reservedIpInstructionPortal}"
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
<table style="width:100%">
    <tbody>
    <tr>
        <td class="caption">${_reservedIpIpAddress}</td>
        <td class="caption">${_reservedIpOwner}</td>
        <td class="caption">${_reservedIpUsePeriod}</td>
        <td class="caption">${_reservedIpComment}</td>
    </tr>
    <#list reservedIps as reservedIp>
    <tr>
        <td class="field" rowspan="2">${reservedIp.ipAddress}
            <#if reservedIp.macAddress??><br>[${reservedIp.macAddress}]</#if>
        </td>
    </tr>
    <tr>
        <td class="field">${reservedIp.ownerName!'?'}</td>
        <td class="field">
            ${(reservedIp.reserveDate?string["dd.MM.yyyy"])!'?'}
            -
            ${(reservedIp.releaseDate?string["dd.MM.yyyy"])!_reservedIpForever}
        </td>
        <td class="field">${HtmlUtils.htmlEscape(reservedIp.comment!'')}</td>
    </tr>
    </#list>
    </tbody>
</table>
    <div style="padding: 8px 0 0;">${_reservedIpUsePeriod}
        <a href="${linkToPortal}/#reserved_ips" rel="link">${_reservedIpInstructionEnding}</a>.
        ${_reservedIpInstructionPortal}
    </div>

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