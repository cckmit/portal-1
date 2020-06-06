<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_you" value="${you}"/>
<@set name="_notification_footer" value="${notification_footer}"/>
<@set name="_reservedIpList" value="${reservedIpList}"/>

<#noparse>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <style>
        .field  {
            padding:2px 10px 2px 0;
            font-family:sans-serif;
            font-size:12px;
            color:#666666;
            text-align:left;
        }
    </style>
</head>
<body bgcolor="#FFFFFF" text="#000000">
<div>
<table style="width:70%">
    <tbody>
    <#list reservedIps as reservedIp>
        <tr>
            <td class="field">${reservedIp.ipAddress}</td>
            <td class="field">${reservedIp.ownerShortName}</td>
            <td class="field">
                ${reservedIp.reserveDate?string["dd.MM.yyyy"]} - ${(reservedIp.releaseDate?string["dd.MM.yyyy"])!reservedIpForever}
            </td>
        </tr>
    </#list>
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