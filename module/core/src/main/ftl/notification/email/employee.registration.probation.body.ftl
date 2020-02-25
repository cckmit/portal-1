<#macro set name value>
    ${"<#assign "+ name +"=\""+ value +"\"/>"}
</#macro>

<@set name="_anchor" value="${anchor}"/>
<@set name="_you" value="${you}"/>
<@set name="_notification_footer" value="${notification_footer_do_not_reply}"/>

<@set name="_response_head_of_department" value="${response_head_of_department}"/>
<@set name="_probation_period_is_end" value="${probation_period_is_end}"/>

<#noparse>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
</head>
<body bgcolor="#FFFFFF" text="#000000">
    <div style="margin-top: 12px">


        <div style="padding: 8px 0 4px;">
            <div style="font-family: sans-serif;font-size: 14px;">${_probation_period_is_end} <a href="${linkToEmployeeRegistration}">${employee_registration_name}</a>.</div>
            <div style="font-family: sans-serif;font-size: 14px;">${_response_head_of_department} <a href="https://forms.yandex.ru/u/5e450660fe156801a92ce758/">${_anchor}</a></div>
        </div>

    </div>
    <div style="padding: 4px 0 8px;">
        <div style="color: #777777; font-size: 11px; font-family:sans-serif; margin: 20px 0; padding: 8px 0; border-top: 1px solid #D4D5D6;">
            ${_you} (<b>${userName!'?'}</b>) ${_notification_footer}
        </div>
    </div>
</body>
</html>
</#noparse>
