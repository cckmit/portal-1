<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>
<@set name="_reservedIpActionCreated" value="${reservedIpActionCreated}"/>
<@set name="_reservedIpActionUpdated" value="${reservedIpActionUpdated}"/>
<@set name="_reservedIpActionRemoved" value="${reservedIpActionRemoved}"/>
<#noparse><#if is_created == true>${_reservedIpActionCreated}</#if><#if is_updated == true>${_reservedIpActionUpdated}</#if><#if is_removed == true>${_reservedIpActionRemoved}</#if> "${ipAddress}" [${initiator}]</#noparse>