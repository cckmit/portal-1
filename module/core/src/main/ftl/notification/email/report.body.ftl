<#macro set name value>
${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_reportId" value="${notificationReportId}"/>
<@set name="_name" value="${notificationReportName}"/>
<@set name="_created" value="${notificationReportCreated}"/>
<@set name="_creator" value="${notificationReportCreator}"/>
<@set name="_type" value="${notificationReportType}"/>
<@set name="_status" value="${notificationReportStatus}"/>
<@set name="_filter" value="${notificationReportFilter}"/>

<@set name="_typeCaseObjects" value="${notificationReportTypeCaseObjects}"/>
<@set name="_typeCaseTimeElapsed" value="${notificationReportTypeCaseTimeElapsed}"/>
<@set name="_typeCaseResolutionTime" value="${notificationReportTypeCaseResolutionTime}"/>

<@set name="_statusCreated" value="${notificationReportStatusCreated}"/>
<@set name="_statusProcess" value="${notificationReportStatusProcess}"/>
<@set name="_statusReady" value="${notificationReportStatusReady}"/>
<@set name="_statusError" value="${notificationReportStatusError}"/>

<@set name="_filterSearch" value="${notificationReportFilterSearch}"/>
<@set name="_filterCreated" value="${notificationReportFilterCreated}"/>
<@set name="_filterUpdated" value="${notificationReportFilterUpdated}"/>
<@set name="_filterFrom" value="${notificationReportFilterFrom}"/>
<@set name="_filterTo" value="${notificationReportFilterTo}"/>
<@set name="_filterSelected" value="${notificationReportFilterSelected}"/>
<@set name="_filterCompany" value="${notificationReportFilterCompany}"/>
<@set name="_filterProduct" value="${notificationReportFilterProduct}"/>
<@set name="_filterCommentAuthor" value="${notificationReportFilterCommentAuthor}"/>

<#noparse>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <style>
        .markdown p,
        .markdown blockquote,
        .markdown ul,
        .markdown ol,
        .markdown dl,
        .markdown table,
        .markdown pre {
            font-size: 14px !important;
        }
        .markdown p {
            margin-bottom: 0;
            margin-top: 0;
        }
        <#include "/ru/protei/portal/skin/classic/public/css/markdown.css" parse=false>
    </style>
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
                        ${_filter}
                    </td>
                    <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">
                        <#if filter.searchString??>
                            ${_filterSearch}: ${filter.searchString}
                            <br>
                        </#if>
                        <#if filter.createdFrom?? || filter.createdTo??>
                            ${_filterCreated} :
                            <#if filter.createdFrom??>
                                ${_filterFrom} ${filter.createdFrom?datetime}
                            </#if>
                            <#if filter.createdTo??>
                                ${_filterTo} ${filter.createdTo?datetime}
                            </#if>
                            <br>
                        </#if>
                        <#if filter.modifiedFrom?? || filter.modifiedTo??>
                            ${_filterUpdated} :
                            <#if filter.modifiedFrom??>
                                ${_filterFrom} ${filter.modifiedFrom?datetime}
                            </#if>
                            <#if filter.modifiedTo??>
                                ${_filterTo} ${filter.modifiedTo?datetime}
                            </#if>
                            <br>
                        </#if>
                        <#if filter.companyIds??>
                            ${_filterCompany}: ${filter.companyIds?size} ${_filterSelected?lower_case}
                            <br>
                        </#if>
                        <#if filter.productIds??>
                            ${_filterProduct}: ${filter.productIds?size} ${_filterSelected?lower_case}
                            <br>
                        </#if>
                        <#if filter.commentAuthorIds??>
                            ${_filterCommentAuthor}: ${filter.commentAuthorIds?size} ${_filterSelected?lower_case}
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
