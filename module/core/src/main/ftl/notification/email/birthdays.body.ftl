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
/*        .date  {
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
        .bordered  {
            border-bottom: 1px solid #D4D5D6;
        }*/
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
<div>
<#--<div style="width:600px;">
    <#assign birthdays = employees?keys/>
    <#assign names_values = employees?values/>
    <#list birthdays as birthday>
        <div class="date bordered">${(birthday?string["dd MMMMM"])!_empty}</div>
        <div class="name">
            <#assign seq_index = birthdays?seq_index_of(birthday) />
            <#assign names = names_values[seq_index]/>
            <#list names as name>
                ${name.displayName}<#sep><br> </#sep>
            </#list>
        </div>
    </#list>
</div>-->
<table style="width:100%">
    <tbody>
    <tr>
        <td class="caption">${_fio}</td>
        <td class="caption">${_birthday}</td>
    </tr>
    <#list employees as employee>
    <tr>
        <td class="field">${employee.displayName}</td>
        <td class="field">${(employee.birthday?string["dd MMMMM"])!_empty}</td>
    </tr>
    </#list>
    </tbody>
</table>
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