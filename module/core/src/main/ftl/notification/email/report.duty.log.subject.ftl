<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>
<@set name="_dutyLogReport" value="${dutyLogReport}"/>
<#noparse>${_dutyLogReport} "${reportTitle}"</#noparse>