<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_you" value="${you}"/>
<@set name="_notification_footer" value="${notification_footer}"/>
<@set name="_reservedIpSubnet" value="${subnet}"/>
<@set name="_reservedIpOwner" value="${reservedIpOwner}"/>
<@set name="_reservedIpIpAddress" value="${reservedIpIpAddress}"/>
<@set name="_reservedIpMacAddress" value="${reservedIpMacAddress}"/>
<@set name="_reservedIpComment" value="${reservedIpComment}"/>
<@set name="_contract_open" value="${contract_open}"/>
<@set name="_anchor" value="${anchor}"/>

<#noparse>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
</head>
<body bgcolor="#FFFFFF" text="#000000">

    <div style="padding: 4px 0 8px;">
        <div style="color: #777777; font-size: 11px; font-family:sans-serif; margin: 20px 0; padding: 8px 0; border-top: 1px solid #D4D5D6;">
            ${_you} (<b>${userName!'?'}</b>) ${_notification_footer}
            <#list recipients as recipient>
                <#if recipient??>
                    ${recipient}<#sep>, </#sep>
                </#if>
            </#list>
        </div>
    </div>
</body>
</html>
</#noparse>