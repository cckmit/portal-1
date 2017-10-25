<#macro set name value>
${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_issue_id" value="${issue_id}"/>
<@set name="_issue_name" value="${issue_name}"/>
<@set name="_issue_private" value="${issue_private}"/>
<@set name="_yes" value="${yes}"/>
<@set name="_no" value="${no}"/>
<@set name="_createdBy" value="${created_by}"/>
<@set name="_changedStateTo" value="${changed_state_to}"/>
<@set name="_you" value="${you}"/>
<@set name="_yourself" value="${yourself}"/>
<@set name="_product" value="${product}"/>
<@set name="_criticality" value="${criticality}"/>
<@set name="_state" value="${state}"/>
<@set name="_customer" value="${customer}"/>
<@set name="_manager" value="${manager}"/>
<@set name="_notification_footer" value="${notification_footer}"/>
<@set name="_attachments" value="${attachments}"/>
<@set name="_updated" value="${updated_just_now}"/>
<@set name="_description" value="${description}"/>

<#noparse>
<#macro changeTo old, new>
    <span style="color:#bd1313;text-decoration:line-through;">${old}</span>
    <span style="margin:0 5px;">&rarr;</span>
    <span style="color:#11731d;background:#dff7e2;padding:2px 4px">${new}</span>
</#macro>
<#macro diff old, new>
    ${TextUtils.diff(old, new, "color:#11731d;background:#dff7e2;text-decoration:none", "color:#bd1313;text-decoration:line-through")}
</#macro>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
</head>
<body bgcolor="#FFFFFF" text="#000000">
<div>
    <div style="padding: 5px;font-size: 13px;<#if isCreated>background:#dff7e2;color:#11731d;<#else>background:#f0f0f0;color:gray;</#if>">
        ${_createdBy} <#if createdByMe == true>${_yourself}<#else>${(case.creator.displayName)!'?'}</#if> ${(case.created)!''}
    </div>
    <div style="margin-top: 12px">
        <table>
            <tbody>
                <tr>
                    <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 13px;color: #888888;">
                        ${_issue_id}
                    </td>
                    <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 13px;"><b>${case.caseNumber}</b></td>
                </tr>
                <tr>
                    <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 13px;color: #888888;">
                        ${_issue_name}
                    </td>
                    <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 13px;">
                        <#if nameChanged>
                            <@diff new="${(case.name)!''}" old="${(oldCase.name)!''}"/>
                        <#else>
                            ${(case.name)!''}
                        </#if>
                    </td>
                </tr>
                <tr>
                    <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 13px;color: #888888;">
                        ${_description}
                    </td>
                    <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 13px;">
                        <#if infoChanged>
                            <@diff new="${(case.info)!''}" old="${(oldCase.info)!''}"/>
                        <#else>
                            ${(case.info)!''}
                        </#if>
                    </td>
                </tr>
                <tr>
                    <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 13px;color: #888888;">
                        ${_product}
                    </td>
                    <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 13px;">
                        <#if productChanged>
                            <@changeTo old="${(oldCase.product.name)!'?'}" new="${(case.product.name)!'?'}"/>
                        <#else>
                            ${(case.product.name)!'?'}
                        </#if>
                    </td>
                </tr>
                <tr>
                    <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 13px;color: #888888;">
                        ${_criticality}
                    </td>
                    <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 13px;">
                        <#if importanceChanged>
                            <@changeTo old="${oldImportanceLevel}" new="${importanceLevel}"/>
                        <#else>
                            <div style="padding:2px 4px;font-size:90%;color:white;
                                <#switch importanceLevel>
                                  <#case "critical">
                                     background: #ef5350;
                                     <#break>
                                  <#case "important">
                                     background: #ffa219;
                                     <#break>
                                  <#case "basic">
                                     background: #42a5f5;
                                     <#break>
                                  <#default>
                                     background: #607D8B;
                                </#switch>
                            ">
                                ${importanceLevel}
                            </div>
                        </#if>
                    </td>
                </tr>
                <tr>
                    <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 13px;color: #888888;">
                        ${_state}
                    </td>
                    <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 13px;">
                        <#if caseChanged>
                            <@changeTo old="${oldCaseState}" new="${caseState}"/>
                        <#else>
                            ${caseState}
                        </#if>
                    </td>
                </tr>
                <tr>
                    <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 13px;color: #888888;">
                        ${_issue_private}
                    </td>
                    <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 13px;">
                        <#if privacyChanged>
                            <@changeTo old="${oldCase.privateCase?string(_yes,_no)}" new="${case.privateCase?string(_yes,_no)}"/>
                        <#else>
                            <#if case.privateCase == true>
                                ${_yes}
                            <#else>
                                ${_no}
                            </#if>
                        </#if>
                    </td>
                </tr>
                <tr>
                    <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 13px;color: #888888;">
                        ${_customer}
                    </td>
                    <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 13px;">
                        <#if customerChanged>
                            <@changeTo old="${(oldInitiator.displayName)!''} (${(oldInitiatorCompany.cname)!''})" new="${(case.initiator.displayName)!''} (${(case.initiatorCompany.cname)!''})"/>
                        <#else>
                            ${(case.initiator.displayName)!''} (${(case.initiatorCompany.cname)!''})
                        </#if>
                    </td>
                </tr>
                <tr>
                    <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 13px;color: #888888;">
                        ${_manager}
                    </td>
                    <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 13px;">
                        <#if managerChanged>
                            <@changeTo
                                old="${(oldManager??)?then(((oldManager.displayName)!'') +' ('+ oldManager.company.cname +')', '?')}"
                                new="${(manager??)?then(((manager.displayName)!'') +' ('+ manager.company.cname +')', '?')}"
                            />
                        <#else>
                            <#if manager??>${(manager.displayName)!''} (${manager.company.cname})<#else>?</#if>
                        </#if>
                    </td>
                </tr>
                <#if attachments??>
                    <tr>
                        <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 13px;color: #888888;">
                            ${_attachments}
                        </td>
                        <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 13px;">
                            <#list attachments as attach>
                                <span style="display:inline-block;padding:1px 4px 1px 0px;white-space:nowrap;text-decoration:none;color:#0062ff">
                                    ${attach.fileName}
                                </span>
                            </#list>
                            <#if removedAttachments??>
                                <#list removedAttachments as attach>
                                    <span style="display:inline-block;padding:1px 5px;white-space:nowrap;text-decoration:line-through;color:#bd1313;">
                                        ${attach.fileName}
                                    </span>
                                </#list>
                            </#if>
                            <#if addedAttachments??>
                                <#list addedAttachments as attach>
                                    <span style="display:inline-block;padding:1px 5px;white-space:nowrap;text-decoration:none;color:#11731d;background:#dff7e2;">
                                        ${attach.fileName}
                                    </span>
                                </#list>
                            </#if>
                        </td>
                    </tr>
                </#if>
            </tbody>
        </table>
        <div style="font-size:13px;margin-top:15px">
            <#list caseComments?reverse as caseComment>
                <div style="border-radius:5px;padding:12px;margin-bottom:5px;background:<#if caseComment.changed>#dff7e2<#else>#f0f0f0</#if>;">
                    <span style="color:gray;line-height: 17px;margin-right:10px">${caseComment.created}</span>
                    <span style="color:blue;font-size:14px;margin-bottom:5px;color:#0062ff;line-height: 17px;">
                        <#if caseComment.author??>${(caseComment.author.displayName)!''}</#if>
                    </span>
                    <#if caseComment.caseState??>
                        ${_changedStateTo} ${caseComment.caseState}
                    <#else>
                        <#if caseComment.oldText??>
                            <span style="color:#11731d;line-height: 17px;margin-right:10px">${_updated}</span>
                            <div style="margin-top:4px;line-height:1.5em;white-space:pre-wrap">
                                <@diff old="${caseComment.oldText}" new="${caseComment.text}"/>
                            </div>
                        <#else>
                            <div style="margin-top:4px;line-height:1.5em;white-space:pre-wrap">
                                ${caseComment.text}
                            </div>
                        </#if>
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
                    ${recipient},
                </#if>
            </#list>
        </div>
    </div>
</div>
</body>
</html>
</#noparse>
