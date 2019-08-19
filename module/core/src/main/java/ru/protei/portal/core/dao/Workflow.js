/**
 * This is a template for an on-change rule. This rule defines what
 * happens when a change is applied to an issue.
 *
 * For details, read the Quick Start Guide:
 * https://www.jetbrains.com/help/youtrack/standalone/2019.2/Quick-Start-Guide-Workflows-JS.html
 */

var entities = require('@jetbrains/youtrack-scripting-api/entities');
var http = require('@jetbrains/youtrack-scripting-api/http');

exports.rule = entities.Issue.onChange({
    title: 'On_crm_number_change',
    guard: function (ctx) {
        return ctx.issue.fields.isChanged("Номер обращения в CRM");
    },
    action: function (ctx) {

        var issueName = ctx.issue.id;
        var crmNumber = ctx.issue.fields.CrmNumber;
        var oldCrmNumber = ctx.issue.oldValue("Номер обращения в CRM");

        var urlParams = "";
        if (!crmNumber) {
            urlParams = "removeyoutrackidfromissue/" + issueName + "/" + oldCrmNumber;
        } else if (oldCrmNumber) {
            urlParams = "changeyoutrackidinissue/" + issueName + "/" + oldCrmNumber+ "/" + crmNumber;
        } else {
            urlParams = "addyoutrackidintoissue/" + issueName + "/" + crmNumber;
        }

        var connection = new http.Connection("http://192.168.100.69:9007/Portal/springApi/api/");
        // connection.basicAuth('youtrackapiuser', 'pswrd');

        var response = connection.postSync(urlParams, '');
        var text =
            // "oldCrmNumber="+oldCrmNumber+" "+
            "oldCrmNumber2=" + oldCrmNumber + " " + urlParams;
        if (response && response.code === 200) {
            text = "Success. Add youtrack issue  " + issueName + " into portal issue with number " + crmNumber + "\n urlParams: " + text;
        } else {
            text = "Failure.  Can't add youtrack issue  " + issueName + " into portal issue with number " + crmNumber + "\n urlParams: " + text;
            if (response) {
                response.headers.forEach(function (header) {
                    text += header.name + ': ' + header.value + '\n';
                });
                text += '\n' + response.response;
            } else {
                text = text + " response: " + response;
            }
        }
        ctx.issue.addComment(text);
    },
    requirements: {
        CrmNumber: {
            type: entities.Field.stringType,
            name: 'Номер обращения в CRM'
        }
    }
});