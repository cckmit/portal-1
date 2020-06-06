<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_createdBy" value="${created_by}"/>
<@set name="_you" value="${you}"/>
<@set name="_notification_footer" value="${notification_footer}"/>
<@set name="_subnetActionCreated" value="${subnetActionCreated}"/>
<@set name="_subnetActionUpdated" value="${subnetActionUpdated}"/>
<@set name="_subnetActionRemoved" value="${subnetActionRemoved}"/>
<@set name="_subnetAction" value="${subnetAction}"/>
<@set name="_subnetIpAddress" value="${subnetIpAddress}"/>
<@set name="_subnetComment" value="${subnetComment}"/>

<#noparse>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
</head>
<body bgcolor="#FFFFFF" text="#000000">
<div>
    <#if is_removed == false>
    <div style="padding: 5px;font-size: 14px;background:#f0f0f0;color:#666666;">
        <#if is_created == true>${_subnetActionCreated}</#if>
        <#if is_updated == true>${_subnetActionUpdated}</#if>
        ${_subnetAction}
    </div>
    <table>
        <tbody>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">${_subnetIpAddress}</td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">${ipAddress}${mask}</td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">${_subnetComment}</td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">${comment!''}</td>
        </tr>
        </tbody>
    </table>
    </#if>
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