// Привет. Ниже код workflow
// Прошу добавить к протеевским авто подключаемым. Срабатывать на изменение задачи.

    var entities = require('@jetbrains/youtrack-scripting-api/entities');
var http = require('@jetbrains/youtrack-scripting-api/http');

exports.rule = entities.Issue.onChange({
    title: 'On_crm_number_change',
    guard: function (ctx) {
        var isCrmField = ctx.issue.fields.isChanged("Номер обращения в CRM");
        if (!isCrmField) return false;

        var isChangedFromApi = true;
        if (ctx.currentUser) {
            isChangedFromApi = ("portal" === ctx.currentUser.login)
                || ("efremov" === ctx.currentUser.login);
        }
        return !isChangedFromApi;
    },
    action: function (ctx) {

        var issueName = ctx.issue.id;
        var crmNumber = ctx.issue.fields.CrmNumber;
        var oldCrmNumber = ctx.issue.oldValue("Номер обращения в CRM");

        if (crmNumber === oldCrmNumber) return;

        var urlParams = "";
        if (!crmNumber) {
            urlParams = "removeyoutrackidfromissue/" + issueName + "/" + oldCrmNumber;
        } else if (oldCrmNumber) {
            urlParams = "changeyoutrackidinissue/" + issueName + "/" + oldCrmNumber + "/" + crmNumber;
        } else {
            urlParams = "addyoutrackidintoissue/" + issueName + "/" + crmNumber;
        }

        var connection = new http.Connection("http://youtrack.protei.ru:8080/portal/Portal/springApi/api/");
        connection.basicAuth('portal_api', 'IOOlyzNfo22S7FpoanxYQB7Ap9FW7eG9ydGFs.cG9ydGFs');

        try {
            connection.postSync(urlParams, '');
        } catch (e) {
            //ignore exceptions
        }

    },
    requirements: {
        CrmNumber: {
            type: entities.Field.stringType,
            name: 'Номер обращения в CRM'
        }
    }
});