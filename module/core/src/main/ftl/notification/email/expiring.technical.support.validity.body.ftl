<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_you" value="${you}"/>
<@set name="_notification_footer" value="${notification_footer}"/>
<@set name="_expiringTechnicalSupportValidityBodyMessage" value="${expiringTechnicalSupportValidityBodyMessage}"/>
<@set name="_expiringTechnicalSupportValidity7Day" value="${expiringTechnicalSupportValidity7Day}"/>
<@set name="_expiringTechnicalSupportValidity14Day" value="${expiringTechnicalSupportValidity14Day}"/>
<@set name="_expiringTechnicalSupportValidity30Day" value="${expiringTechnicalSupportValidity30Day}"/>
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
        <div class="field">${_expiringTechnicalSupportValidityBodyMessage}</div>
        <#if expiringIn7Days??>
            <div class="caption">${_expiringTechnicalSupportValidity7Day}:</div>
            <#list expiringIn7Days as info>
                <#assign id=info.getId()?c href=linkToProject?replace("%d", id)/>
                <div class="field">${(info.getTechnicalSupportValidity())?date!'?'} - <a href=${href}>№${info.getId()?c!'?'}</a>
                    ${(info.getCustomerName())!'?'}, ${(info.getName())!'?'}</div>
            </#list>
        </#if>
        <#if expiringIn14Days??>
            <div class="caption">${_expiringTechnicalSupportValidity14Day}:</div>
            <#list expiringIn14Days as info>
                <#assign id=info.getId()?c href=linkToProject?replace("%d", id)/>
                <div class="field">${(info.getTechnicalSupportValidity())?date!'?'} - <a href=${href}>№${info.getId()?c!'?'}</a>
                    ${(info.getCustomerName())!'?'}, ${(info.getName())!'?'}</div>
            </#list>
        </#if>
        <#if expiringIn30Days??>
            <div class="caption">${_expiringTechnicalSupportValidity30Day}:</div>
            <#list expiringIn30Days as info>
                <#assign id=info.getId()?c href=linkToProject?replace("%d", id)/>
                <div class="field">${(info.getTechnicalSupportValidity())?date!'?'} - <a href=${href}>№${info.getId()?c!'?'}</a>
                    ${(info.getCustomerName())!'?'}, ${(info.getName())!'?'}</div>
            </#list>
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
    </body>
</html>
</#noparse>