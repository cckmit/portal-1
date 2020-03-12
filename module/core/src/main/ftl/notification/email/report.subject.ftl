<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>
<@set name="_typeDaily" value="${notificationReportDailySubject}"/>
<@set name="_typeWeekly" value="${notificationReportWeeklySubject}"/>
<#noparse>
<#if scheduledType.name() == "DAILY">
${_typeDaily}
</#if>
<#if scheduledType.name() == "WEEKLY">
${_typeWeekly}
</#if>
 "${reportTitle}"
</#noparse>