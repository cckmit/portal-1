<#macro set name value>
${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_personCaseFilterMailNoIssues" value="${personCaseFilterMailNoIssues}"/>

<#noparse>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
</head>
<body bgcolor="#FFFFFF" text="#000000">
<div>
    <div style="margin-top: 12px">
        <#if stateToIssues?has_content>
            <#list stateOrder as state>
                <h3 style="font-family: sans-serif;font-size: 14px">${state}</h3>
                <#list stateToIssues[state] as issue>
                    <#assign number=issue.caseNumber?c href=urlTemplate?replace("%d", number) name=issue.name/>
                    <div style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                        <b><a href=${href}>CRM-${number}</a></b> - <span>${name}</span> </div>
                </#list>
            </#list>
        <#else>
            <div style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                ${_personCaseFilterMailNoIssues}</div>
        </#if>
    </div>
</div>
</body>
</html>
</#noparse>
