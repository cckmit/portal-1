<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_contract_for_contract" value="${contract_for_contract}"/>
<@set name="_contract_will_be" value="${contract_will_be}"/>
<@set name="_contract_comment" value="${contract_comment}"/>
<@set name="_contract_open" value="${contract_open}"/>
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
        <div style="padding: 8px 0 4px;">
            <div style="font-family: sans-serif;font-size: 14px;">
            ${EnumLangUtil.contractDatesTypeLang(contractDateType, lang)} ${_contract_for_contract} ${EnumLangUtil.contractTypeLang(contractType, lang)} ${contractNumber} ${_contract_will_be} ${contractDateDate}</div>
            <#if contractDateCommentExists>
                <div style="font-family: sans-serif;font-size: 14px;">${_contract_comment}: ${contractDateComment}</div>
            </#if>
            <div style="font-family: sans-serif;font-size: 14px;">${_contract_open}: <a href="${linkToContract}">${_anchor}</a></div>
        </div>
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