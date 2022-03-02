<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_you" value="${you}"/>
<@set name="_notification_footer" value="${notification_footer}"/>
<@set name="_absenceActionCreated" value="${absenceActionCreated}"/>
<@set name="_absenceActionUpdated" value="${absenceActionUpdated}"/>
<@set name="_absenceActionRemoved" value="${absenceActionRemoved}"/>
<@set name="_absenceAction" value="${absenceAction}"/>
<@set name="_absenceEmployee" value="${absenceEmployee}"/>
<@set name="_absenceDateRange" value="${absenceDateRange}"/>
<@set name="_absenceReason" value="${absenceReason}"/>
<@set name="_absenceComment" value="${absenceComment}"/>
<@set name="_absenceReasonValue1" value="${absenceReasonValue1}"/>
<@set name="_absenceReasonValue2" value="${absenceReasonValue2}"/>
<@set name="_absenceReasonValue3" value="${absenceReasonValue3}"/>
<@set name="_absenceReasonValue4" value="${absenceReasonValue4}"/>
<@set name="_absenceReasonValue5" value="${absenceReasonValue5}"/>
<@set name="_absenceReasonValue6" value="${absenceReasonValue6}"/>
<@set name="_absenceReasonValue7" value="${absenceReasonValue7}"/>
<@set name="_absenceReasonValue8" value="${absenceReasonValue8}"/>
<@set name="_absenceReasonValue9" value="${absenceReasonValue9}"/>
<@set name="_absenceReasonValue10" value="${absenceReasonValue10}"/>
<@set name="_absenceReasonValue11" value="${absenceReasonValue11}"/>
<@set name="_absenceReasonValue12" value="${absenceReasonValue12}"/>


<#noparse>
    <#macro changeTo old, new>
        <span style="color:#bd1313;text-decoration:line-through;">${old}</span>
        <span style="margin:0 5px;">&rarr;</span>
        <span style="color:#11731d;background:#dff7e2;padding:2px 4px">${new}</span>
    </#macro>
    <#macro diff old, new>${TextUtils.diff(old, new, "color:#11731d;background:#dff7e2;text-decoration:none", "color:#bd1313;text-decoration:line-through")}</#macro>
    <#macro diffHTML old, new>${TextUtils.diffHTML(old, new, "color:#11731d;background:#dff7e2;text-decoration:none", "color:#bd1313;text-decoration:line-through")}</#macro>

<html>
    <head>
        <meta http-equiv="content-type" content="text/html; charset=utf-8">
    </head>
    <body bgcolor="#FFFFFF" text="#000000">
        <div>
            <div style="padding: 5px;font-size: 14px;<#if is_created>background:#dff7e2;color:#11731d;<#elseif is_removed>background:#f7dede;color:#f55753;<#else>background:#f0f0f0;color:#666666;</#if>">
                <#if is_created>${_absenceActionCreated}</#if>
                <#if is_updated>${_absenceActionUpdated}</#if>
                <#if is_removed>${_absenceActionRemoved}</#if>
                ${_absenceAction}
            </div>
            <table>
                <tbody>
                    <tr>
                        <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">${_absenceEmployee}</td>
                        <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">${absentEmployee}</td>
                    </tr>
                    <tr>
                        <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">${_absenceDateRange}</td>
                        <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                            <#if multiAddAbsenceList??>
                                <#list multiAddAbsenceList as absence>
                                    ${absence.getFromTime()?string("dd.MM.yyyy HH:mm")} - ${absence.getTillTime()?string("dd.MM.yyyy HH:mm")} <br>
                                </#list>
                            <#else>
                                <#if fromTimeChanged>
                                    <@changeTo old="${(oldFromTime)!'?'}" new="${(fromTime)!'?'}"/>
                                <#else>
                                    ${(fromTime)!'?'}
                                </#if> -
                                <#if tillTimeChanged>
                                    <@changeTo old="${(oldTillTime)!'?'}" new="${(tillTime)!'?'}"/>
                                <#else>
                                    ${(tillTime)!'?'}
                                </#if>
                            </#if>
                        </td>
                    </tr>
                    <tr>
                        <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">${_absenceReason}</td>
                        <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                            <#if     reason == 1>${_absenceReasonValue1}
                            <#elseif reason == 2>${_absenceReasonValue2}
                            <#elseif reason == 3>${_absenceReasonValue3}
                            <#elseif reason == 4>${_absenceReasonValue4}
                            <#elseif reason == 5>${_absenceReasonValue5}
                            <#elseif reason == 6>${_absenceReasonValue6}
                            <#elseif reason == 7>${_absenceReasonValue7}
                            <#elseif reason == 8>${_absenceReasonValue8}
                            <#elseif reason == 9>${_absenceReasonValue9}
                            <#elseif reason == 10>${_absenceReasonValue10}
                            <#elseif reason == 11>${_absenceReasonValue11}
                            <#elseif reason == 12>${_absenceReasonValue12}
                            <#else>${reason}</#if>
                        </td>
                    </tr>
                    <tr>
                        <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">${_absenceComment}</td>
                        <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                            <#if commentChanged>
                                <@changeTo old="${(oldComment)!'?'}" new="${(comment)!'?'}"/>
                            <#else>
                                ${(comment)!'?'}
                            </#if>
                        </td>
                    </tr>
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