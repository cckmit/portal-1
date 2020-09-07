<#macro set name value>
${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_reportId" value="${notificationReportId}"/>
<@set name="_name" value="${notificationReportName}"/>
<@set name="_created" value="${notificationReportCreated}"/>
<@set name="_creator" value="${notificationReportCreator}"/>
<@set name="_type" value="${notificationReportType}"/>
<@set name="_status" value="${notificationReportStatus}"/>
<@set name="_period" value="${notificationReportPeriod}"/>

<@set name="_typeCaseObjects" value="${notificationReportTypeCaseObjects}"/>
<@set name="_typeCaseTimeElapsed" value="${notificationReportTypeCaseTimeElapsed}"/>
<@set name="_typeCaseResolutionTime" value="${notificationReportTypeCaseResolutionTime}"/>

<@set name="_statusCreated" value="${notificationReportStatusCreated}"/>
<@set name="_statusProcess" value="${notificationReportStatusProcess}"/>
<@set name="_statusReady" value="${notificationReportStatusReady}"/>
<@set name="_statusError" value="${notificationReportStatusError}"/>

<@set name="_filterSearch" value="${notificationReportFilterSearch}"/>

<#noparse>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
</head>
<body bgcolor="#FFFFFF" text="#000000">
<div>
    <div style="margin-top: 12px">
        <table>
            <tbody>
                <tr>
                    <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                        ${_reportId}
                    </td>
                    <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                        ${reportId}
                    </td>
                </tr>
                <tr>
                    <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                        ${_name}
                    </td>
                    <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                        ${name}
                    </td>
                </tr>
                <tr>
                    <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                        ${_creator}
                    </td>
                    <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                        ${creator}
                    </td>
                </tr>
                <tr>
                    <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                        ${_created}
                    </td>
                    <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                        ${created}
                    </td>
                </tr>
                <tr>
                    <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                        ${_type}
                    </td>
                    <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                        <#if type.name() == "CASE_OBJECTS">
                            ${_typeCaseObjects}
                        </#if>
                        <#if type.name() == "CASE_TIME_ELAPSED">
                            ${_typeCaseTimeElapsed}
                        </#if>
                        <#if type.name() == "CASE_RESOLUTION_TIME">
                            ${_typeCaseResolutionTime}
                        </#if>
                    </td>
                </tr>
                <tr>
                    <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                        ${_status}
                    </td>
                    <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                        <#if status.name() == "CREATED">
                            ${_statusCreated}
                        </#if>
                        <#if status.name() == "PROCESS">
                            ${_statusProcess}
                        </#if>
                        <#if status.name() == "READY">
                            ${_statusReady}
                        </#if>
                        <#if status.name() == "ERROR">
                            ${_statusError}
                        </#if>
                    </td>
                </tr>
                <tr>
                    <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                        ${_period}
                    </td>
                    <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                        <#if filter.createdRange??>
                            <#if filter.createdRange.from??>
                                ${filter.createdRange.from?datetime}
                            </#if> -
                            <#if filter.createdRange.to??>
                                ${filter.createdRange.to?datetime}
                            </#if>
                            <br>
                        </#if>
                        <#if filter.modifiedRange??>
                            <#if filter.modifiedRange.from??>
                                ${filter.modifiedRange.from?datetime}
                            </#if> -
                            <#if filter.modifiedRange.to??>
                                ${filter.modifiedRange.to?datetime}
                            </#if>
                            <br>
                        </#if>
                    </td>
                </tr>
            </tbody>
        </table>
        </div>
    </div>
</div>
</body>
</html>
</#noparse>
