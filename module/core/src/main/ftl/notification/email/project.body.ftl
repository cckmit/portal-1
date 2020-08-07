<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_createdBy" value="${created_by}"/>
<@set name="_you" value="${you}"/>
<@set name="_notification_footer" value="${notification_footer}"/>
<@set name="_timeDayLiteral" value="${timeDayLiteral}"/>
<@set name="_timeHourLiteral" value="${timeHourLiteral}"/>
<@set name="_timeMinuteLiteral" value="${timeMinuteLiteral}"/>
<@set name="_projectNumber" value="${projectNumber}"/>
<@set name="_projectName" value="${projectName}"/>
<@set name="_projectDescription" value="${projectDescription}"/>
<@set name="_projectState" value="${projectState}"/>
<@set name="_projectRegion" value="${projectRegion}"/>
<@set name="_projectCompany" value="${projectCompany}"/>
<@set name="_projectCustomerType" value="${projectCustomerType}"/>
<@set name="_projectProductDirection" value="${projectProductDirection}"/>
<@set name="_projectProduct" value="${projectProduct}"/>
<@set name="_projectTechnicalSupportValidity" value="${projectTechnicalSupportValidity}"/>
<@set name="_projectImportance" value="${projectImportance}"/>
<@set name="_projectSlaReactionTime" value="${projectSlaReactionTime}"/>
<@set name="_projectSlaTemporaryTime" value="${projectSlaTemporaryTime}"/>
<@set name="_projectSlaFullTime" value="${projectSlaFullTime}"/>
<@set name="_projectSla" value="${projectSla}"/>
<@set name="_updated" value="${updated_just_now}"/>
<@set name="_linkedTasks" value="${linkedTasks}"/>
<@set name="_attachments" value="${attachments}"/>

<#noparse>
<#macro changeTo old, new>
    <span style="color:#bd1313;text-decoration:line-through;">${old}</span>
    <span style="margin:0 5px;">&rarr;</span>
    <span style="color:#11731d;background:#dff7e2;padding:2px 4px">${new}</span>
</#macro>

<#macro changeToIfDiff old, new>
    <#if old == new>
        ${new}
    <#else>
        <span style="color:#bd1313;text-decoration:line-through;">${old}</span>
        <span style="margin:0 5px;">&rarr;</span>
        <span style="color:#11731d;background:#dff7e2;padding:2px 4px">${new}</span>
    </#if>
</#macro>

<#macro diffHTML old, new>
    ${TextUtils.diffHTML(old, new, "color:#11731d;background:#dff7e2;text-decoration:none", "color:#bd1313;text-decoration:line-through")}
</#macro>

<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <style>
        .markdown * {
            box-sizing: border-box;
        }

        .markdown p,
        .markdown blockquote,
        .markdown ul,
        .markdown ol,
        .markdown dl,
        .markdown pre {
            font-size: 14px !important;
        }

        .markdown p {
            margin-bottom: 0;
            margin-top: 0;
        }

        .markdown table {
            display: block;
            width: 100%;
            overflow: auto;
            border-spacing: 0;
            border-collapse: collapse;
            padding: 2px;
            font-family: sans-serif;
            font-size: 14px;
        }

        .markdown table tr {
            background-color: #fff;
            border-top: 1px solid #c6cbd1;
        }

        .markdown table th {
            font-weight: normal;
        }

        .markdown table th, .markdown table td {
            padding: 6px 13px;
            border: 1px solid #dfe2e5;
        }
    </style>
</head>
<body>
<div style="padding: 5px;font-size: 14px;<#if isCreated>background:#dff7e2;color:#11731d;<#else>background:#f0f0f0;color:#666666;</#if>">
    ${_createdBy} ${(TransliterationUtils.transliterate(creator, lang))!'?'} ${(created??)?then(created?datetime, '?')}
</div>
<div style="margin-top: 12px">
    <table>
        <tbody>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_projectNumber}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;"><b><a href="${linkToProject}">${projectNumber}</a></b></td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_projectName}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                <#if nameChanged>
                    <@changeTo
                    old="${oldName!'?'}"
                    new="${newName}"
                    />
                <#else>
                    ${newName}
                </#if>
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_projectDescription}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                <#if descriptionChanged>
                    <@changeTo
                    old="${oldDescription!'?'}"
                    new="${newDescription}"
                    />
                <#else>
                    ${newDescription}
                </#if>
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_projectState}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                <#if stateChanged>
                    <@changeTo
                    old="${EnumLangUtil.getRegionState(oldState, lang)!'?'}"
                    new="${EnumLangUtil.getRegionState(newState, lang)}"
                    />
                <#else>
                    ${EnumLangUtil.getRegionState(newState, lang)}
                </#if>
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_projectRegion}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                <#if regionChanged>
                    <@changeTo
                    old="${oldRegion!'?'}"
                    new="${newRegion!'?'}"
                    />
                <#else>
                    ${newRegion!'?'}
                </#if>
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_projectCompany}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                <#if companyChanged>
                    <@changeTo
                    old="${oldCompany!'?'}"
                    new="${newCompany}"
                    />
                <#else>
                    ${newCompany}
                </#if>
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_projectCustomerType}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                <#if customerTypeChanged>
                    <@changeTo
                    old="${EnumLangUtil.getCustomerType(oldCustomerType, lang)!'?'}"
                    new="${EnumLangUtil.getCustomerType(newCustomerType, lang)}"
                    />
                <#else>
                    ${EnumLangUtil.getCustomerType(newCustomerType, lang)}
                </#if>
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_projectProductDirection}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                <#if productDirectionChanged>
                    <@changeTo
                    old="${oldProductDirection!'?'}"
                    new="${newProductDirection}"
                    />
                <#else>
                    ${newProductDirection}
                </#if>
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_projectProduct}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                <#if productChanged>
                    <@changeTo
                    old="${oldProduct!'?'}"
                    new="${newProduct!'?'}"
                    />
                <#else>
                    ${newProduct!'?'}
                </#if>
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                ${_projectTechnicalSupportValidity}
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                <#if supportValidityChanged>
                    <@changeTo
                    old="${(oldSupportValidity??)?then(oldSupportValidity?date, '?')}"
                    new="${(newSupportValidity??)?then(newSupportValidity?date, '?')}"
                    />
                <#else>
                    ${(newSupportValidity??)?then(newSupportValidity?date, '?')}
                </#if>
            </td>
        </tr>

        <#if hasLinks>
            <tr id="test-linkedTasks">
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_linkedTasks}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                    <#if existingLinks??>
                        <#list existingLinks as link>
                            <span style="display:inline-block;padding:1px 4px 1px 0px;white-space:nowrap;text-decoration:none;color:#0062ff">
                                    <a href="${link.url}">${link.linkName}</a>
                                </span>
                        </#list>
                    </#if>
                    <#if addedLinks??>
                        <#list addedLinks as link>
                            <span style="display:inline-block;padding:1px 5px;white-space:nowrap;text-decoration:none;color:#11731d;background:#dff7e2;">
                                    <a href="${link.url}" >${link.linkName}</a>
                                </span>
                        </#list>
                    </#if>
                    <#if removedLinks??>
                        <#list removedLinks as link>
                            <span style="display:inline-block;padding:1px 5px;white-space:nowrap;text-decoration:line-through">
                                    <a href="${link.url}" style="color:#bd1313;">${link.linkName}</a>
                                </span>
                        </#list>
                    </#if>
                </td>
            </tr>
        </#if>

        <#assign roleTypes=team?keys/>
        <#assign memberDiffs=team?values/>

        <#list roleTypes as roleType>
            <#assign roleType_index=roleTypes?seq_index_of(roleType)/>
            <#assign memberDiff=memberDiffs[roleType_index]/>

            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${EnumLangUtil.getPersonRoleType(roleType, lang)}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                    <#if memberDiff.getSameEntries()??>
                        <#list memberDiff.getSameEntries() as same>
                            <span style="display:inline-block;padding:1px 4px 1px 0px;white-space:nowrap;text-decoration:none">
                                ${TransliterationUtils.transliterate(same.name, lang)}
                            </span>
                        </#list>
                    </#if>
                    <#if memberDiff.getAddedEntries()??>
                        <#list memberDiff.getAddedEntries() as added>
                            <span style="display:inline-block;padding:1px 5px;white-space:nowrap;text-decoration:none;color:#11731d;background:#dff7e2;">
                                ${TransliterationUtils.transliterate(added.name, lang)}
                            </span>
                        </#list>
                    </#if>
                    <#if memberDiff.getRemovedEntries()??>
                        <#list memberDiff.getRemovedEntries() as removed>
                            <span style="display:inline-block;padding:1px 5px;white-space:nowrap;text-decoration:line-through;color:#bd1313;">
                                ${TransliterationUtils.transliterate(removed.name, lang)}
                            </span>
                        </#list>
                    </#if>
                </td>
            </tr>
        </#list>

        <#assign importanceLevels=sla?keys/>
        <#assign slaDiffs=sla?values/>

        <#if importanceLevels?has_content>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_projectSla}
                </td>
                <td class="markdown">
                    <table>
                        <thead>
                        <tr>
                            <th>${_projectImportance}</th>
                            <th>${_projectSlaReactionTime}</th>
                            <th>${_projectSlaTemporaryTime}</th>
                            <th>${_projectSlaFullTime}</th>
                        </tr>
                        </thead>
                        <tbody>
                        <#list importanceLevels as importanceLevel>
                            <#assign importanceLevel_index=importanceLevels?seq_index_of(importanceLevel)/>
                            <#assign slaDiff=slaDiffs[importanceLevel_index]/>

                            <#assign oldSla=slaDiff.getInitialState()/>
                            <#assign newSla=slaDiff.getNewState()/>

                            <tr>
                                <td>
                                    ${importanceLevel.getCode()}
                                </td>
                                <td>
                                    <@changeToIfDiff
                                    old="${(oldSla.getReactionTime()??)?then(TimeFormatter.format(oldSla.getReactionTime(),_timeDayLiteral,_timeHourLiteral,_timeMinuteLiteral), '?')}"
                                    new="${(newSla.getReactionTime()??)?then(TimeFormatter.format(newSla.getReactionTime(),_timeDayLiteral,_timeHourLiteral,_timeMinuteLiteral), '?')}"
                                    />
                                </td>
                                <td>
                                    <@changeToIfDiff
                                    old="${(oldSla.getTemporarySolutionTime()??)?then(TimeFormatter.format(oldSla.getTemporarySolutionTime(),_timeDayLiteral,_timeHourLiteral,_timeMinuteLiteral), '?')}"
                                    new="${(newSla.getTemporarySolutionTime()??)?then(TimeFormatter.format(newSla.getTemporarySolutionTime(),_timeDayLiteral,_timeHourLiteral,_timeMinuteLiteral), '?')}"
                                    />
                                </td>
                                <td>
                                    <@changeToIfDiff
                                    old="${(oldSla.getFullSolutionTime()??)?then(TimeFormatter.format(oldSla.getFullSolutionTime(),_timeDayLiteral,_timeHourLiteral,_timeMinuteLiteral), '?')}"
                                    new="${(newSla.getFullSolutionTime()??)?then(TimeFormatter.format(newSla.getFullSolutionTime(),_timeDayLiteral,_timeHourLiteral,_timeMinuteLiteral), '?')}"
                                    />
                                </td>
                            </tr>
                        </#list>
                        </tbody>
                    </table>
                </td>
            </tr>
        </#if>
        </tbody>
    </table>
    <div id="test-case-comments" style="font-size:14px;margin-top:15px">
        <#list caseComments?reverse as caseComment>
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
                    <div class="markdown"
                         style="margin-top:4px;line-height:1.5em;"><@diffHTML old="${caseComment.oldText}" new="${caseComment.text}"/></div>
                <#else>
                    <div class="markdown" style="margin-top:4px;line-height:1.5em;">${caseComment.text}</div>
                </#if>
                <#if caseComment.hasAttachments>
                    <div class="markdown" style="margin-top:12px;line-height:1.5em;">
                        <hr/>
                        <i>${_attachments}: </i>
                        <#if caseComment.sameAttachments??>
                            <#list caseComment.sameAttachments as attach>
                                <span style="display:inline-block;padding:1px 4px 1px 0px;white-space:nowrap;text-decoration:none;color:#0062ff">
                                    ${attach.fileName}
                                </span>
                            </#list>
                        </#if>
                        <#if caseComment.removedAttachments??>
                            <#list caseComment.removedAttachments as attach>
                                <span style="display:inline-block;padding:1px 5px;white-space:nowrap;text-decoration:line-through;color:#bd1313;">
                                    ${attach.fileName}
                                </span>
                            </#list>
                        </#if>
                        <#if caseComment.addedAttachments??>
                            <#list caseComment.addedAttachments as attach>
                                <span style="display:inline-block;padding:1px 5px;white-space:nowrap;text-decoration:none;color:#11731d;background:#dff7e2;">
                                    ${attach.fileName}
                                </span>
                            </#list>
                        </#if>
                    </div>
                </#if>
            </div>
        </#list>
    </div>
    <div style="padding: 4px 0 8px;">
        <div style="color: #777777; font-size: 11px; font-family:sans-serif; margin: 20px 0; padding: 8px 0; border-top: 1px solid #D4D5D6;">
            ${_you} (<b>${TransliterationUtils.transliterate(userName, lang)!'?'}</b>) ${_notification_footer}
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
