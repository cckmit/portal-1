<#macro set name value>
${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_createdBy" value="${createdBy}"/>
<@set name="_you" value="${you}"/>
<@set name="_yourself" value="${yourself}"/>
<@set name="_issueIsPrivate" value="${issueIsPrivate}"/>
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
<div class="">
    <table style="border-collapse: collapse; width: 100%;table-layout: fixed">
        <tbody>
            <tr>
                <td style="padding:12px 15px;background:<#if isCreated>#dff7e2;<#else>#f0f0f0;</#if>border-radius: 5px;">
                    <table style="border-collapse: collapse; border: 0; width: 100%;">
                        <tbody>
                            <tr>
                                <td style="vertical-align: top;font-family:sans-serif; font-size: 13px;">
                                    <a title="newproject" style="float:left; margin-right:6px; font-size:15px; color: #1466c6; text-decoration: none; " href="${linkToIssue}">
                                        CRM-${case.caseNumber} — ${case.name}
                                    </a>
                                </td>
                                <td style="padding-left: 5px; font-size: 11px; font-family:sans-serif; text-align: right;<#if isCreated>color:#11731d;<#else>color:#888888;</#if>">
                                    ${_createdBy} <#if createdByMe == true>${_yourself}<#else>${case.creator.displayShortName}</#if> ${case.created}
                                </td>
                            </tr>
                        </tbody>
                    </table>
                    <div style="margin:14px 0 8px 0;font-size:13px;line-height:18.5px">
                        <div>${case.info}</div>
                        <div style="margin-top: 14px;">
                            <#if case.privateCase == true>
                                <span style="color:#777777;font-style:italic;font-size:13px">
                                    <img style="vertical-align:text-bottom;opacity: 0.3;margin-left: -2px;" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAApUlEQVQ4jd3RPYpCQRAE4M+fTPYZiAu6F/B8G3gPr6B4H0FzXUFETMR0hWdgBzIO62PVxIaimZquqhmaF9UnRpgHRuhWFX/hByV2gRJL9KoYTEPwjXpgGNykisERs4SrYYFDOlzPGHxgk3BlcO10uJmc+9FbGCR3reg9bDPB+jhF2l/4ddnSzRcKNHLOmVcXOYN/1RsYXK9xhTE6dzR7rB8Nfl6dAU0MImYlDT68AAAAAElFTkSuQmCC">
                                    ${_issueIsPrivate}
                                </span>
                            </#if>
                        </div>
                    </div>
                </td>
            </tr>
        </tbody>
    </table>
    <div style="margin-top: 12px">
        <table>
            <tbody>
                <#if infoChanged>
                    <tr>
                        <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 13px;color: #888888;">
                            ${_description}
                        </td>
                        <td colspan="3" style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 13px;">
                            <@diff new="${case.info}" old="${oldCase.info}"/>
                        </td>
                    </tr>
                </#if>
                <tr>
                    <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 13px;color: #888888;">
                        ${_product}
                    </td>
                    <td colspan="3" style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 13px;">
                        <#if productChanged>
                            <@changeTo old="${oldProductName}" new="${case.product.name}"/>
                        <#else>
                            ${case.product.name}
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
                        ${_customer}
                    </td>
                    <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 13px;">
                        <#if customerChanged>
                            <@changeTo old="${oldInitiator.displayShortName} (${oldInitiatorCompany.cname})" new="${case.initiator.displayShortName} (${case.initiatorCompany.cname})"/>
                        <#else>
                            ${case.initiator.displayShortName} (${case.initiatorCompany.cname})
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
                                old="${(oldManager??)?then(oldManager.displayShortName +' ('+ oldManager.company.cname +')', '?')}"
                                new="${(manager??)?then(manager.displayShortName +' ('+ manager.company.cname +')', '?')}"
                            />
                        <#else>
                            <#if manager??>${manager.displayShortName} (${manager.company.cname})<#else>?</#if>
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
                            <#if oldCase??>
                                <#list removedAttachments as attach>
                                    <span style="display:inline-block;padding:1px 5px;white-space:nowrap;text-decoration:line-through;color:#bd1313;">
                                        ${attach.fileName}
                                    </span>
                                </#list>
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
                        <#if caseComment.author??>${caseComment.author.displayShortName}</#if>
                    </span>
                    <#if caseComment.oldText??>
                        <span style="color:#11731d;line-height: 17px;margin-right:10px">${_updated}</span>
                        <div style="margin-top:4px;line-height:1.5em">
                            <@diff old="${caseComment.oldText}" new="${caseComment.text}"/>
                        </div>
                    <#else>
                        <div style="margin-top:4px;line-height:1.5em">
                            ${caseComment.text}
                        </div>
                    </#if>
                </div>
            </#list>
        </div>
    </div>
    <div style="padding: 4px 0 8px;">
        <div style="color: #777777; font-size: 11px; font-family:sans-serif; margin: 20px 0; padding: 8px 0; border-top: 1px solid #D4D5D6;">
            ${_you} (<b>${userName}</b>) ${_notification_footer}
            <#list recipients as recipient>
                ${recipient},
            </#list>
        </div>
    </div>
</div>
</body>
</html>
</#noparse>
