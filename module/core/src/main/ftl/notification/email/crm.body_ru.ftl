<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=windows-1251">
    <style>

    </style>
</head>
<body bgcolor="#FFFFFF" text="#000000">
<div class="">
    <table style="border-collapse: collapse; width: 100%;
        table-layout: fixed">
        <tbody>
        <tr>
            <td style="padding:12px 15px; background:#f0f0f0;border-radius: 5px;">
                <table style="border-collapse: collapse; border: 0; width: 100%;">
                    <tbody>
                    <tr>
                        <td style="vertical-align: top;font-family:sans-serif; font-size: 13px;">
                            <a title="newproject" style="float:left; margin-right:6px; font-size:15px; color: #1466c6; text-decoration: none; " href="${linkToIssue}">
                                ${case.caseNumber}
                            </a>
                        </td>
                        <td style="padding-left: 5px; font-size: 11px; font-family:sans-serif; text-align: right; color: #888888">
                            Создана <#if createdByMe == true>вами <#else>${case.creator.displayShortName}</#if> ${case.created}
                        </td>
                    </tr>
                    </tbody>
                </table>
                <div style="margin:14px 0 8px 29px;font-size:13px;line-height:18.5px">
                    <div class="wiki text">${case.info}</div>
                    <div style="margin-top: 14px;">
                        <#if case.private == true>
                            <span style="color:#777777;font-style:italic;font-size:13px">
                                <img style="vertical-align:text-bottom;opacity: 0.3;margin-left: -2px;" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAApUlEQVQ4jd3RPYpCQRAE4M+fTPYZiAu6F/B8G3gPr6B4H0FzXUFETMR0hWdgBzIO62PVxIaimZquqhmaF9UnRpgHRuhWFX/hByV2gRJL9KoYTEPwjXpgGNykisERs4SrYYFDOlzPGHxgk3BlcO10uJmc+9FbGCR3reg9bDPB+jhF2l/4ddnSzRcKNHLOmVcXOYN/1RsYXK9xhTE6dzR7rB8Nfl6dAU0MImYlDT68AAAAAElFTkSuQmCC">
                                Обращение является приватным
                            </span>
                        </#if>
                    </div>
                </div>
            </td>
        </tr>
        </tbody>
    </table>
    <div style="margin-top: 12px">
        <table>
            <tbody>
            <tr>
                <td style="vertical-align: top;padding: 5px 5px 5px 0;padding-right: 15px;font-family: sans-serif;font-size: 13px;color: #888888;">
                    Продукт
                </td>
                <td colspan="3" style="vertical-align: top;padding:5px;font-family: sans-serif;font-size: 13px;">
                    <div>${case.product.name}</div>
                </td>
            </tr>
            <tr>
                <td style=" padding: 5px 5px 5px 0; vertical-align: top;padding-right: 15px; font-family: sans-serif; font-size:13px; color: #888888;">
                    Критичность
                </td>
                <td style="vertical-align: top; padding: 5px; font-family:sans-serif; font-size: 13px;">
                    <div style=" background-color: #ffee9c !important;padding:2px 4px;font-size:90%; ">
                        <div style="word-wrap: break-word; overflow: hidden; color: #b45f06 !important; ">
                            ${importanceLevel}
                        </div>
                    </div>
                </td>
                <td style=" padding: 5px 5px 5px 20px; vertical-align: top; padding-right: 15px; font-family: sans-serif;font-size: 13px; color: #888888;">
                    Статус
                </td>
                <td style="vertical-align: top; padding: 5px; font-family:sans-serif; font-size: 13px;">
                    <div style="">
                        <div style="word-wrap: break-word; overflow: hidden;">
                            ${caseState}
                        </div>
                    </div>
                </td>
            </tr>
            <tr>
                <td style=" padding: 5px 5px 5px 0; vertical-align: top;padding-right: 15px; font-family: sans-serif; font-size:13px; color: #888888;">
                    Заказчик
                </td>
                <td style="vertical-align: top; padding: 5px; font-family:sans-serif; font-size: 13px;">
                    <div style="">
                        <div style="word-wrap: break-word; overflow: hidden;">
                            ${case.initiator.displayName} из ${case.initiatorCompany.cname}
                        </div>
                    </div>
                </td>
                <td style=" padding: 5px 5px 5px 20px; vertical-align:top; padding-right: 15px; font-family: sans-serif;font-size: 13px; color: #888888;">
                    Менеджер
                </td>
                <td style="vertical-align: top; padding: 5px; font-family:sans-serif; font-size: 13px;">
                    <div style="">
                        <div style="word-wrap: break-word; overflow: hidden;">
                            ${case.manager.displayName} из ${case.manager.company.cname}
                        </div>
                    </div>
                </td>
            </tr>
            </tbody>
        </table>
        <table style="border-spacing:0 3px;width:100%;font-size:13px;margin-top:15px">
            <tbody>
            <#list caseComments as caseComment>
                <#if caseComment.caseState??>
                    <tr>
                        <td style="background: #dff7e2;;border-radius:5px 0 0 5px;padding:12px;white-space:nowrap;color:gray;vertical-align:top;font-size:11px">
                            <div style="color:blue;font-size:14px;margin-bottom:5px;color:#0062ff">
                                <#if caseComment.author??>
                                    ${caseComment.author.displayName}
                                </#if>
                            </div>
                        ${caseComment.created}
                        </td>
                        <td style="background: #dff7e2;;border-radius:0 5px 5px 0;padding:12px;width:100%;vertical-align:top;line-height:18.5px">
                            <div class="wiki text">
                                Изменил статус на ${caseComment.caseState}
                            </div>
                        </td>
                    </tr>
                <#else>
                    <tr>
                        <td style="background: #dff7e2;;border-radius:5px 0 0 5px;padding:12px;white-space:nowrap;color:gray;vertical-align:top;font-size:11px">
                            <div style="color:blue;font-size:14px;margin-bottom:5px;color:#0062ff">
                                <#if caseComment.author??>
                                    ${caseComment.author.displayName}
                                </#if>
                            </div>
                        ${caseComment.created}
                        </td>
                        <td style="background: #dff7e2;;border-radius:0 5px 5px 0;padding:12px;width:100%;vertical-align:top;line-height:18.5px">
                            <div class="wiki text">
                            ${caseComment.text}
                            </div>
                        </td>
                    </tr>
                </#if>
            </#list>
            </tbody>
        </table>
    </div>
    <div style="padding: 4px 0 8px;">
        <div style="color: #777777; font-size: 11px; font-family:sans-serif; margin: 20px 0; padding: 8px 0; border-top: 1px solid #D4D5D6;">
            Вы (<b>${userName}</b>) получили это сообщение,
            потому что включены в список рассылки
        </div>
    </div>
</div>
</body>
</html>
