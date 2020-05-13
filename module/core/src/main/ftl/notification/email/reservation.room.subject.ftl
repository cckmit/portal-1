<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>
<@set name="_reservationRoomActionCreated" value="${reservationRoomActionCreated}"/>
<@set name="_reservationRoomActionUpdated" value="${reservationRoomActionUpdated}"/>
<@set name="_reservationRoomActionRemoved" value="${reservationRoomActionRemoved}"/>
<@set name="_reservationRoomAction" value="${reservationRoomAction}"/>
<#noparse><#if is_created == true>${_reservationRoomActionCreated}</#if><#if is_updated == true>${_reservationRoomActionUpdated}</#if><#if is_removed == true>${_reservationRoomActionRemoved}</#if> ${_reservationRoomAction} "${room}"</#noparse>