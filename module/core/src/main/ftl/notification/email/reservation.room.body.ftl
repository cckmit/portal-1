<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_you" value="${you}"/>
<@set name="_notification_footer" value="${notification_footer}"/>
<@set name="_reservationRoomActionCreated" value="${reservationRoomActionCreated}"/>
<@set name="_reservationRoomActionUpdated" value="${reservationRoomActionUpdated}"/>
<@set name="_reservationRoomActionRemoved" value="${reservationRoomActionRemoved}"/>
<@set name="_reservationRoomAction" value="${reservationRoomAction}"/>
<@set name="_reservationRoomPersonResponsible" value="${reservationRoomPersonResponsible}"/>
<@set name="_reservationRoomRoom" value="${reservationRoomRoom}"/>
<@set name="_reservationRoomDate" value="${reservationRoomDate}"/>
<@set name="_reservationRoomTime" value="${reservationRoomTime}"/>
<@set name="_reservationRoomReason" value="${reservationRoomReason}"/>
<@set name="_reservationRoomCoffeeBreakCount" value="${reservationRoomCoffeeBreakCount}"/>
<@set name="_reservationRoomComment" value="${reservationRoomComment}"/>
<@set name="_reservationRoomReasonValue0" value="${reservationRoomReasonValue0}"/>
<@set name="_reservationRoomReasonValue1" value="${reservationRoomReasonValue1}"/>
<@set name="_reservationRoomReasonValue2" value="${reservationRoomReasonValue2}"/>
<@set name="_reservationRoomReasonValue3" value="${reservationRoomReasonValue3}"/>
<@set name="_reservationRoomReasonValue4" value="${reservationRoomReasonValue4}"/>

<#noparse>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
</head>
<body bgcolor="#FFFFFF" text="#000000">
<div>
    <div style="padding: 5px;font-size: 14px;background:#f0f0f0;color:#666666;">
        <#if is_created == true>${_reservationRoomActionCreated}</#if>
        <#if is_updated == true>${_reservationRoomActionUpdated}</#if>
        <#if is_removed == true>${_reservationRoomActionRemoved}</#if>
        ${_reservationRoomAction}
    </div>
    <table>
        <tbody>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">${_reservationRoomPersonResponsible}</td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">${person_responsible}</td>
            </tr>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">${_reservationRoomRoom}</td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">${room}</td>
            </tr>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">${_reservationRoomDate}</td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">${date}</td>
            </tr>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">${_reservationRoomTime}</td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">${time}</td>
            </tr>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">${_reservationRoomReason}</td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                    <#if     reason == 0>${_reservationRoomReasonValue0}
                    <#elseif reason == 1>${_reservationRoomReasonValue1}
                    <#elseif reason == 2>${_reservationRoomReasonValue2}
                    <#elseif reason == 3>${_reservationRoomReasonValue3}
                    <#elseif reason == 4>${_reservationRoomReasonValue4}
                    <#else>${reason}</#if>
                </td>
            </tr>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">${_reservationRoomCoffeeBreakCount}</td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">${coffee_break_count}</td>
            </tr>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">${_reservationRoomComment}</td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">${comment}</td>
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