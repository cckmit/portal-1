<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_you" value="${you}"/>
<@set name="_notification_footer" value="${notification_footer}"/>
<@set name="_fio" value="${fio}"/>
<@set name="_birthday" value="${birthday}"/>
<@set name="_empty" value="${empty}"/>
<#noparse>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <style>
        .date  {
            padding:5px 20px;
            font-family:sans-serif;
            font-size:14px;
            text-align:left;
            color:blue;
            color:#0062ff;
        }
        .name  {
            padding:2px 50px;
            font-family:sans-serif;
            font-size:14px;
            text-align:left;
        }
        .border  {
            border-bottom: 1px solid #D4D5D6;
        }
    </style>
</head>
<body bgcolor="#FFFFFF" text="#000000">
<div>
<div style="width:600px;">
    <#list employees as date, names>
        <div class="date border">${(date?string["dd MMMMM"])!_empty}</div>
        <#list names as name>
            <div class="name">${name.displayName}</div>
        </#list>
    </#list>
</div>
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