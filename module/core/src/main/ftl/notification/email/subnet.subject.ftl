<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>
<@set name="_subnetActionCreated" value="${subnetActionCreated}"/>
<@set name="_subnetActionUpdated" value="${subnetActionUpdated}"/>
<@set name="_subnetActionRemoved" value="${subnetActionRemoved}"/>
<@set name="_subnetAction" value="${subnetAction}"/>
<#noparse><#if is_created == true>${_subnetActionCreated}</#if><#if is_updated == true>${_subnetActionUpdated}</#if><#if is_removed == true>${_subnetActionRemoved}</#if> ${_subnetAction} ${ipAddress} [${initiator}]</#noparse>
