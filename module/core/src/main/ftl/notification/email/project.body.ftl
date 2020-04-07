<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_createdBy" value="${created_by}"/>
<@set name="_you" value="${you}"/>
<@set name="_notification_footer" value="${notification_footer}"/>

<#noparse>
<#macro changeTo old, new>
    <span style="color:#bd1313;text-decoration:line-through;">${old}</span>
    <span style="margin:0 5px;">&rarr;</span>
    <span style="color:#11731d;background:#dff7e2;padding:2px 4px">${new}</span>
</#macro>

<#macro changeToIfDiff old, new>
    <#if old != new>
        <span style="color:#bd1313;text-decoration:line-through;">${old}</span>
        <span style="margin:0 5px;">&rarr;</span>
        <span style="color:#11731d;background:#dff7e2;padding:2px 4px">${new}</span>
    <#else>
        ${new}
    </#if>
</#macro>

<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
</head>
<body>
<div style="padding: 5px;font-size: 14px;<#if isCreated>background:#dff7e2;color:#11731d;<#else>background:#f0f0f0;color:#666666;</#if>">
    ${_createdBy} ${(TransliterationUtils.transliterate(creator, lang))!'?'}
</div>
<div style="margin-top: 12px">
    <table>
        <tbody>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                Название
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
                Описание
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
                Состояние
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                <#if stateChanged>
                    <@changeTo
                    old="${oldState!'?'}"
                    new="${newState}"
                    />
                <#else>
                    ${newState}
                </#if>
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                Регион
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
                Компания
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
                Тип заказчика
            </td>
            <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                <#if customerTypeChanged>
                    <@changeTo
                    old="${oldCustomerType!'?'}"
                    new="${newCustomerType}"
                    />
                <#else>
                    ${newCustomerType}
                </#if>
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                Продуктовое направление
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
                Продукт
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
                Срок действия технической поддержки
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

        <#assign importanceLevels=sla?keys/>
        <#assign slaDiffs=sla?values/>

        <#if importanceLevels?has_content>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    SLA
                </td>
            </tr>
            <#list importanceLevels as importanceLevel>
                <#assign importanceLevel_index=importanceLevels?seq_index_of(importanceLevel)/>
                <#assign slaDiff=slaDiffs[importanceLevel_index]/>
                <#assign newSla=slaDiff.getNewState()/>

                <tr>
                    <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                        Критичность
                    </td>
                    <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                        ${importanceLevel.getCode()}
                    </td>
                </tr>
                <tr>
                    <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                        Время реагирования
                    </td>
                    <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                        <#if slaDiff.getInitialState()??>
                            <#assign oldSla=slaDiff.getInitialState()/>
                            <@changeToIfDiff
                            old="${oldSla.getReactionTime()!'?'}"
                            new="${newSla.getReactionTime()!'?'}"
                            />
                        <#else>

                        </#if>
                    </td>
                </tr>
                <tr>
                    <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                        Временное решение
                    </td>
                    <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                        <#if slaDiff.getInitialState()??>
                            <#assign oldSla=slaDiff.getInitialState()/>
                            <@changeToIfDiff
                            old="${oldSla.getReactionTime()!'?'}"
                            new="${newSla.getReactionTime()!'?'}"
                            />
                        </#if>
                    </td>
                </tr>
                <tr>
                    <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                        Постоянное решение
                    </td>
                    <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                        <#if slaDiff.getInitialState()??>
                            <#assign oldSla=slaDiff.getInitialState()/>
                            <@changeToIfDiff
                            old="${oldSla.getReactionTime()!'?'}"
                            new="${newSla.getReactionTime()!'?'}"
                            />
                        </#if>
                    </td>
                </tr>
            </#list>
        </#if>

        <#assign roleTypes=team?keys/>
        <#assign memberDiffs=team?values/>

        <#list roleTypes as roleType>
            <#assign roleType_index=roleTypes?seq_index_of(roleType)/>
            <#assign memberDiff=memberDiffs[roleType_index]/>

            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${RoleTypeLangUtil.getName(roleType, lang)}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                    <#if memberDiff.getSameEntries()??>
                        <#list memberDiff.getSameEntries() as same>
                            <span style="display:inline-block;padding:1px 4px 1px 0px;white-space:nowrap;text-decoration:none;color:#0062ff">
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
        </tbody>
    </table>
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
