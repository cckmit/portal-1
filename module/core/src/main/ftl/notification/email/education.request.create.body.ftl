<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_you" value="${you}"/>
<@set name="_notification_footer" value="${notification_footer}"/>
<@set name="_educationRequest" value="${educationRequest}"/>

<@set name="_educationEntryTitle" value="${educationEntryTitle}"/>
<@set name="_educationEntryType" value="${educationEntryType}"/>
<@set name="_educationEntryCoins" value="${educationEntryCoins}"/>
<@set name="_educationEntryLink" value="${educationEntryLink}"/>
<@set name="_educationEntryLocation" value="${educationEntryLocation}"/>
<@set name="_educationEntryDates" value="${educationEntryDates}"/>
<@set name="_educationEntryDescription" value="${educationEntryDescription}"/>
<@set name="_educationEntryParticipants" value="${educationEntryParticipants}"/>


<#noparse>
    <html>
    <head>
        <meta http-equiv="content-type" content="text/html; charset=utf-8">
    </head>

    <body>
    <div style="margin-top: 12px">
        <table>
            <tbody>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_educationEntryTitle}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                    ${title}
                </td>
            </tr>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_educationEntryType}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                    ${EnumLangUtil.educationTypeLang(type, lang)}
                </td>
            </tr>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_educationEntryCoins}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                    ${coins}
                </td>
            </tr>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_educationEntryLink}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                    ${link}
                </td>
            </tr>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_educationEntryLocation}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                    ${location}
                </td>
            </tr>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_educationEntryDates}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                    ${(dateStart??)?then(dateStart?date, '?')}
                    ${(dateStart??)?then(' - ', '')}
                    ${(dateEnd??)?then(dateEnd?date, '')}
                </td>
            </tr>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_educationEntryDescription}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                    ${description}
                </td>
            </tr>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_educationEntryParticipants}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                    ${participants}
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
