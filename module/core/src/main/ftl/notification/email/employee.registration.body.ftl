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
<@set name="_curators" value="${curators}"/>
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
<@set name="_employee_equipment_telephone" value="${employee_equipment_telephone}"/>

<@set name="_employee_resource_youtrack" value="${employee_resource_youtrack}"/>
<@set name="_employee_resource_cvs" value="${employee_resource_cvs}"/>
<@set name="_employee_resource_svn" value="${employee_resource_svn}"/>
<@set name="_employee_resource_mercurial" value="${employee_resource_mercurial}"/>
<@set name="_employee_resource_git" value="${employee_resource_git}"/>
<@set name="_employee_resource_crm" value="${employee_resource_crm}"/>
<@set name="_employee_resource_store_delivery" value="${employee_resource_store_delivery}"/>
<@set name="_employee_resource_email" value="${employee_resource_email}"/>
<@set name="_employee_resource_vpn" value="${employee_resource_vpn}"/>

<@set name="_phone_type_text" value="${phone_type_text}"/>
<@set name="_phone_type_international" value="${phone_type_international}"/>
<@set name="_phone_type_long_distance" value="${phone_type_long_distance}"/>
<@set name="_phone_type_office" value="${phone_type_office}"/>

<@set name="_additional_resource" value="${additional_resource}"/>
<@set name="_operating_system" value="${operating_system}"/>
<@set name="_additional_soft" value="${additional_soft}"/>

<#noparse>
<#macro changeTo old, new>
    <span style="color:#bd1313;text-decoration:line-through;">${old}</span>
    <span style="margin:0 5px;">&rarr;</span>
    <span style="color:#11731d;background:#dff7e2;padding:2px 4px">${new}</span>
</#macro>

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
                ${(employeeFullName)!''}
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_head_of_department}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                ${(headOfDepartmentShortName)!''}
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_employment_type}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                <#switch (employmentType)!>
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
                <#if withRegistration>
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
                ${(position)!''}
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_state}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                ${(state)!}
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_employment_date}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                <#if employmentDateChanged>
                    <@changeTo
                    old="${(oldEmploymentDate??)?then(oldEmploymentDate?date, '?')}"
                    new="${(newEmploymentDate??)?then(newEmploymentDate?date, '?')}"
                    />
                <#else>
                    ${(newEmploymentDate??)?then(newEmploymentDate?date, '?')}
                </#if>
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_curators}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                <#if curatorsDiff.getSameEntries()??>
                    <#list curatorsDiff.getSameEntries() as same>
                        <span style="display:inline-block;padding:1px 4px 1px 0px;white-space:nowrap;text-decoration:none">
                                ${TransliterationUtils.transliterate(same.displayName, lang)}
                            </span>
                    </#list>
                </#if>
                <#if curatorsDiff.getAddedEntries()??>
                    <#list curatorsDiff.getAddedEntries() as added>
                        <span style="display:inline-block;padding:1px 5px;white-space:nowrap;text-decoration:none;color:#11731d;background:#dff7e2;">
                                ${TransliterationUtils.transliterate(added.displayName, lang)}
                            </span>
                    </#list>
                </#if>
                <#if curatorsDiff.getRemovedEntries()??>
                    <#list curatorsDiff.getRemovedEntries() as removed>
                        <span style="display:inline-block;padding:1px 5px;white-space:nowrap;text-decoration:line-through;color:#bd1313;">
                                ${TransliterationUtils.transliterate(removed.displayName, lang)}
                            </span>
                    </#list>
                </#if>
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_created}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                ${(created)!}
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_workplace}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;white-space:pre-wrap;">${(workplace)!}</td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_equipment_list}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;white-space:pre-wrap;"><#rt>
                <#if (equipmentList)??><#rt>
                    <#list equipmentList as eq><#rt>
                        <#switch (eq.name())!''>
                            <#case "TABLE">${_employee_equipment_table}<#break>
                            <#case "CHAIR">${_employee_equipment_chair}<#break>
                            <#case "COMPUTER">${_employee_equipment_computer}<#break>
                            <#case "MONITOR">${_employee_equipment_monitor}<#break>
                            <#case "TELEPHONE">${_employee_equipment_telephone}<#break>
                        </#switch><#sep>, </#sep><#rt>
                    </#list><#rt>
                </#if><#rt>
            </td>
        </tr>

        <#if (operatingSystem)??><#rt>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">${_operating_system}</td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;white-space:pre-wrap;">${(operatingSystem)!}</td>
            </tr>
        </#if><#rt>

        <#if (additionalSoft)??><#rt>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">${_additional_soft}</td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;white-space:pre-wrap;">${(additionalSoft)!}</td>
            </tr>
        </#if><#rt>

        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_resources_list}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;white-space:pre-wrap;"><#rt>
                <#if (resourceList)??><#rt>
                    <#list resourceList as eq><#rt>
                        <#switch (eq.name())!''>
                            <#case "YOUTRACK">${_employee_resource_youtrack}<#break>
                            <#case "CVS">${_employee_resource_cvs}<#break>
                            <#case "SVN">${_employee_resource_svn}<#break>
                            <#case "MERCURIAL">${_employee_resource_mercurial}<#break>
                            <#case "GIT">${_employee_resource_git}<#break>
                            <#case "CRM">${_employee_resource_crm}<#break>
                            <#case "STORE_DELIVERY">${_employee_resource_store_delivery}<#break>
                            <#case "EMAIL">${_employee_resource_email}<#break>
                            <#case "VPN">${_employee_resource_vpn}<#break>
                        </#switch><#sep>, </#sep><#rt>
                    </#list><#rt>
                </#if><#rt>
            </td>
        </tr>

        <#if (resourceComment)??><#rt>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">${_additional_resource}</td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;white-space:pre-wrap;">${(resourceComment)!}</td>
            </tr>
        </#if><#rt>

        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_phone_type_text}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;white-space:pre-wrap;"><#rt>
                <#if (phoneOfficeTypeList)??><#rt>
                    <#list phoneOfficeTypeList as eq><#rt>
                        <#switch (eq.name())!''>
                            <#case "LONG_DISTANCE">${_phone_type_long_distance}<#break>
                            <#case "INTERNATIONAL">${_phone_type_international}<#break>
                            <#case "OFFICE">${_phone_type_office}<#break>
                        </#switch><#sep>, </#sep><#rt>
                    </#list><#rt>
                </#if><#rt>
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_description}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;white-space:pre-wrap;">${(comment)!''}</td>
        </tr>
        </tbody>
    </table>

    <#--COMMENTS-->
    <div id="test-case-comments" style="font-size:14px;margin-top:15px">
        <#list caseComment?reverse as caseComment>
            <div style="border-radius:5px;padding:12px;margin-bottom:5px;background:<#if caseComment.removed>#f7dede<#else><#if caseComment.added>#dff7e2<#else>#f0f0f0</#if></#if>;">
                <span style="color:#666666;line-height: 17px;margin-right:5px">${caseComment.created?datetime}</span>
                <span style="font-size:14px;margin-bottom:5px;color:#0062ff;line-height: 17px;">
                            <#if caseComment.author??>
                                ${TransliterationUtils.transliterate(caseComment.author.displayName, lang)!''}
                            </#if>
                </span>
                <#if caseComment.isUpdated>
                    <span style="color:#11731d;line-height: 17px;margin-right:10px">${_updated}</span>
                </#if>
                <#if caseComment.oldText??>
                    <div class="markdown" style="margin-top:4px;line-height:1.5em;"><@diffHTML old="${caseComment.oldText}" new="${caseComment.text}"/></div>
                <#else>
                    <div class="markdown" style="margin-top:4px;line-height:1.5em;">${caseComment.text}</div>
                </#if>
            </div>
        </#list>
    </div>

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
