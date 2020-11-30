<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>
<@set name="_leave" value="${leave}"/>
<@set name="_absent" value="${absent}"/>
<#noparse><#if is_leave>${_leave}<#else>${_absent}</#if> ${absentEmployee} [${initiator}]</#noparse>