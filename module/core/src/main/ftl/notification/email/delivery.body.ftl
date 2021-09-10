<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_createdBy" value="${created_by}"/>
<@set name="_you" value="${you}"/>
<@set name="_notification_footer" value="${notification_footer}"/>
<@set name="_deliveryNumber" value="${deliveryNumber}"/>
<@set name="_deliveryName" value="${deliveryName}"/>
<@set name="_deliveryDescription" value="${deliveryDescription}"/>
<@set name="_deliveryState" value="${deliveryState}"/>
<@set name="_deliveryType" value="${deliveryType}"/>
<@set name="_deliveryProject" value="${deliveryProject}"/>
<@set name="_deliveryHeadManager" value="${personHeadManager}"/>
<@set name="_deliveryAttribute" value="${deliveryAttribute}"/>
<@set name="_deliveryContract" value="${deliveryContract}"/>
<@set name="_deliveryCompany" value="${deliveryCompany}"/>
<@set name="_deliveryProduct" value="${deliveryProducts}"/>
<@set name="_deliveryDepartureDate" value="${deliveryDepartureDate}"/>
<@set name="_deliveryContactPerson" value="${deliveryContactPerson}"/>
<@set name="_updated" value="${updated_just_now}"/>
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

    <#macro diff old, new>${TextUtils.diff(old, new, "color:#11731d;background:#dff7e2;text-decoration:none", "color:#bd1313;text-decoration:line-through")}</#macro>
    <#macro diffHTML old, new>${TextUtils.diffHTML(old, new, "color:#11731d;background:#dff7e2;text-decoration:none", "color:#bd1313;text-decoration:line-through")}</#macro>

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
        ${_createdBy} ${(TranslitUtils.transliterate(creator, lang))!'?'} ${(created??)?then(created?datetime, '?')}
    </div>
    <div style="margin-top: 12px">
        <table>
            <tbody>

<#--SERIAL NUMBER-->
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_deliveryNumber}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;"><b><a href="${linkToDelivery}">${serialNumber}</a></b></td>
            </tr>

<#--NAME-->
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_deliveryName}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                    <#if nameChanged>
                    <@diff
                    old="${oldName!''}"
                    new="${newName!''}"
                    />
                    <#else>
                    ${newName!''}
                </#if>
                </td>
            </tr>

<#--DESCRIPTION-->
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_deliveryDescription}
                </td>
                <td class="markdown" style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                    <#if descriptionChanged>
                    <@diffHTML
                    old="${(oldDescription)!''}"
                    new="${(newDescription)!''}"
                    />
                    <#else>
                    ${newDescription!''}
                </#if>
                </td>
            </tr>

<#--STATE-->
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_deliveryState}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                    <#if stateChanged>
                    <@changeTo
                    old="${EnumLangUtil.deliveryStateLang(oldState, lang)!'?'}"
                    new="${EnumLangUtil.deliveryStateLang(newState, lang)}"
                    />
                    <#else>
                    ${EnumLangUtil.deliveryStateLang(newState, lang)}
                </#if>
                </td>
            </tr>

<#--TYPE-->
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_deliveryType}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                    <#if typeChanged>
                    <@changeTo
                    old="${EnumLangUtil.deliveryTypeLang(oldType, lang)!'?'}"
                    new="${EnumLangUtil.deliveryTypeLang(newType, lang)}"
                    />
                    <#else>
                    ${EnumLangUtil.deliveryTypeLang(newType, lang)}
                </#if>
                </td>
            </tr>

<#--PROJECT-->
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_deliveryProject}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                    <#if projectChanged>
                    <@changeTo
                    old="${oldProject!'?'}"
                    new="${newProject!'?'}"
                    />
                    <#else>
                    ${newProject!'?'}
                </#if>
                </td>
            </tr>

<#--PRODUCT-->
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_deliveryProduct}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                    <#if productSameEntries??>
                        <#list productSameEntries as same>
                            <span style="display:inline-block;padding:1px 4px 1px 0px;white-space:nowrap;text-decoration:none"> ${same.name}</span>
                        </#list>
                    </#if>
                    <#if productAddedEntries??>
                        <#list productAddedEntries as added>
                            <span style="display:inline-block;padding:1px 5px;white-space:nowrap;text-decoration:none;color:#11731d;background:#dff7e2;"> ${added.name}</span>
                        </#list>
                    </#if>
                    <#if productRemovedEntries??>
                        <#list productRemovedEntries as removed>
                            <span style="display:inline-block;padding:1px 5px;white-space:nowrap;text-decoration:line-through;color:#bd1313;">${removed.name}</span>
                        </#list>
                    </#if>
                </td>
            </tr>

<#--MANAGER-->
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_deliveryHeadManager}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                    <#if managerChanged>
                    <@changeTo
                    old="${oldManager!'?'}"
                    new="${newManager}"
                    />
                    <#else>
                    ${newManager}
                </#if>
                </td>
            </tr>

<#--COMPANY-->
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_deliveryCompany}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                    <#if companyChanged>
                    <@changeTo
                    old="${oldCompany!'?'}"
                    new="${newCompany!'?'}"
                    />
                    <#else>
                    ${newCompany!'?'}
                </#if>
                </td>
            </tr>

<#--CONTACT PERSON-->
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_deliveryContactPerson}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                    <#if contactPersonChanged>
                    <@changeTo
                    old="${oldContactPerson!'?'}"
                    new="${newContactPerson!'?'}"
                    />
                    <#else>
                    ${newContactPerson!'?'}
                </#if>
                </td>
            </tr>

<#--DEPARTURE DATE-->
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_deliveryDepartureDate}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                    <#if departureDateChanged>
                    <@changeTo
                    old="${(oldDepartureDate??)?then(oldDepartureDate?date, '?')}"
                    new="${(newDepartureDate??)?then(newDepartureDate?date, '?')}"
                    />
                    <#else>
                    ${(newDepartureDate??)?then(newDepartureDate?date, '?')}
                </#if>
                </td>
            </tr>

<#--ATTRIBUTE-->
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_deliveryAttribute}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                    <#if attributeChanged>
                    <@changeTo
                    old="${EnumLangUtil.deliveryAttributeLang(oldAttribute, lang)!'?'}"
                    new="${EnumLangUtil.deliveryAttributeLang(newAttribute, lang)!'?'}"
                    />
                    <#else>
                    ${EnumLangUtil.deliveryAttributeLang(newAttribute, lang)!'?'}
                </#if>
                </td>
            </tr>

<#--CONTRACT-->
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_deliveryContract}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                    <#if contractChanged>
                    <@changeTo
                    old="${oldContract!'?'}"
                    new="${newContract!'?'}"
                    />
                    <#else>
                    ${newContract!'?'}
                </#if>
                </td>
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
                            ${TranslitUtils.transliterate(caseComment.author.displayName, lang)!''}
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
            ${_you} (<b>${TranslitUtils.transliterate(userName, lang)!'?'}</b>) ${_notification_footer}
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
