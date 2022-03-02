<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_contract_type" value="${contract_type}"/>
<@set name="_contract_number" value="${contract_number}"/>
<@set name="_contract_date_signing" value="${contract_date_signing}"/>
<@set name="_contract_organization" value="${contract_organization}"/>
<@set name="_contract_contractor" value="${contract_contractor}"/>
<@set name="_contract_description" value="${contract_description}"/>
<@set name="_contract_delivery_number" value="${contract_delivery_number}"/>
<@set name="_contract_file_location" value="${contract_file_location}"/>
<@set name="_anchor" value="${anchor}"/>
<@set name="_you" value="${you}"/>
<@set name="_notification_footer" value="${notification_footer}"/>

<#noparse>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
</head>
<body bgcolor="#FFFFFF" text="#000000">
    <div style="margin-top: 12px">
        <table>
            <tbody>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_contract_number}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;"><b><a href="${linkToContract}">${contractNumber}</a></b></td>
            </tr>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_contract_type}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">${EnumLangUtil.contractTypeLang(contractType, lang)}</td>-->
            </tr>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_contract_date_signing}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">${contractDateSigning???then(contractDateSigning?date, '?')}</td>-->
            </tr>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_contract_organization}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">${(contractOrganization)!'?'}</td>-->
            </tr>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_contract_contractor}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">${(contractContractor)!'?'}</td>-->
            </tr>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_contract_description}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">${(contractDescription)!'?'}</td>-->
            </tr>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_contract_delivery_number}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">${(contractDeliveryNumber)!'?'}</td>-->
            </tr>
            <tr>
                <td style="vertical-align:top;padding:2px 15px 2px 0;font-family: sans-serif;font-size: 14px;color: #666666;">
                    ${_contract_file_location}
                </td>
                <td style="vertical-align:top;padding:2px;font-family: sans-serif;font-size: 14px;">${(contractFileLocation)!'?'}</td>-->
            </tr>
            </tbody>
        </table>
    </div>
    <div style="padding: 4px 0 8px;">
        <div style="color: #777777; font-size: 11px; font-family:sans-serif; margin: 20px 0; padding: 8px 0; border-top: 1px solid #D4D5D6;">
            ${_you} (<b>${userName!'?'}</b>) ${_notification_footer}
            <#list recipients as recipient>
                <#if recipient??>
                    ${recipient}<#sep>, </#sep>
                </#if>
            </#list>
        </div>
    </div>
</body>
</html>
</#noparse>