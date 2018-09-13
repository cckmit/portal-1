<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_employee_full_name" value="${employee_full_name}"/>
<@set name="_head_of_department" value="${head_of_department}"/>
<@set name="_employment_type" value="${employment_type}"/>
<@set name="_employment_type_full_time" value="${employment_type_full_time}"/>
<@set name="_employment_type_part_time" value="${employment_type_part_time}"/>
<@set name="_employment_type_remote" value="${employment_type_remote}"/>
<@set name="_employment_type_contract" value="${employment_type_contract}"/>
<@set name="_with_registration" value="${with_registration}"/>
<@set name="_yes" value="${yes}"/>
<@set name="_you" value="${you}"/>
<@set name="_no" value="${no}"/>
<@set name="_position" value="${position}"/>
<@set name="_state" value="${state}"/>
<@set name="_employment_date" value="${employment_date}"/>
<@set name="_created" value="${created}"/>
<@set name="_workplace" value="${workplace}"/>
<@set name="_equipment_list" value="${equipment_list}"/>
<@set name="_resources_list" value="${resources_list}"/>
<@set name="_description" value="${description}"/>
<@set name="_notification_footer" value="${notification_footer}"/>
<@set name="_employee_registration_link" value="${employee_registration_link}"/>

<@set name="_employee_equipment_table" value="${employee_equipment_table}"/>
<@set name="_employee_equipment_chair" value="${employee_equipment_chair}"/>
<@set name="_employee_equipment_computer" value="${employee_equipment_computer}"/>
<@set name="_employee_equipment_monitor" value="${employee_equipment_monitor}"/>

<@set name="_employee_resource_youtrack" value="${employee_resource_youtrack}"/>
<@set name="_employee_resource_cvs" value="${employee_resource_cvs}"/>
<@set name="_employee_resource_svn" value="${employee_resource_svn}"/>
<@set name="_employee_resource_mercurial" value="${employee_resource_mercurial}"/>
<@set name="_employee_resource_git" value="${employee_resource_git}"/>
<@set name="_employee_resource_crm" value="${employee_resource_crm}"/>
<@set name="_employee_resource_store_delivery" value="${employee_resource_store_delivery}"/>
<@set name="_employee_resource_email" value="${employee_resource_email}"/>

<#noparse>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
</head>
<body bgcolor="#FFFFFF" text="#000000">
<div style="margin-top: 12px">

    <div style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;"><b><a href="${linkToEmployeeRegistration}">${_employee_registration_link}</a></b>
    </div>

    <table>
        <tbody>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_employee_full_name}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                ${(er.employeeFullName)!''}
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_head_of_department}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                ${(er.headOfDepartmentShortName)!''}
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_employment_type}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                <#switch (er.employmentType.name())!>
                    <#case "FULL_TIME">
                        ${_employment_type_full_time}
                        <#break>
                    <#case "PART_TIME">
                        ${_employment_type_part_time}
                        <#break>
                    <#case "REMOTE">
                        ${_employment_type_remote}
                        <#break>
                    <#case "CONTRACT">
                        ${_employment_type_contract}
                        <#break>
                </#switch>
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_with_registration}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                <#if er.withRegistration>
                    ${_yes}
                <#else>
                    ${_no}
                </#if>
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_position}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                ${(er.position)!''}
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_state}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                ${(er.state.getName())!}
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_employment_date}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                ${(er.employmentDate?date)!}
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_created}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                ${(er.created)!}
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_workplace}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;white-space:pre-wrap;">${(er.workplace)!}</td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_equipment_list}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;white-space:pre-wrap;"><#rt>
                <#if (er.equipmentList)??><#rt>
                    <#list er.equipmentList as eq><#rt>
                        <#switch (eq.name())!''><#rt>
                            <#case "TABLE">${_employee_equipment_table}<#break>
                            <#case "CHAIR">${_employee_equipment_chair}<#break>
                            <#case "COMPUTER">${_employee_equipment_computer}<#break>
                            <#case "MONITOR">${_employee_equipment_monitor}<#break>
                        </#switch><#sep>, </#sep><#rt>
                    </#list><#rt>
                </#if><#rt>
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_resources_list}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;white-space:pre-wrap;"><#rt>
                <#if (er.resourceList)??><#rt>
                    <#list er.resourceList as eq><#rt>
                        <#switch (eq.name())!''><#rt>
                            <#case "YOUTRACK">${_employee_resource_youtrack}<#break>
                            <#case "CVS">${_employee_resource_cvs}<#break>
                            <#case "SVN"> ${_employee_resource_svn}<#break>
                            <#case "MERCURIAL"> ${_employee_resource_mercurial}<#break>
                            <#case "GIT"> ${_employee_resource_git}<#break>
                            <#case "CRM"> ${_employee_resource_crm}<#break>
                            <#case "STORE_DELIVERY"> ${_employee_resource_store_delivery}<#break>
                            <#case "EMAIL"> ${_employee_resource_email}<#break>
                        </#switch><#sep>, </#sep><#rt>
                    </#list><#rt>
                </#if><#rt>
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_description}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;white-space:pre-wrap;">${(er.comment)!''}</td>
        </tr>
        </tbody>
    </table>
</div>
<div style="padding: 4px 0 8px;">
    <div style="color: #777777; font-size: 11px; font-family:sans-serif; margin: 20px 0; padding: 8px 0; border-top: 1px solid #D4D5D6;">
        ${_you} (<b>${userName!'?'}</b>) ${_notification_footer}
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
