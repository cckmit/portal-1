/*
MYSQL
 */


UPDATE companysubscription
set email_addr='emergency.crm@protei.ru'
where email_addr='emergency@protei.ru';


UPDATE companysubscription
set email_addr='musson.crm@protei.ru'
where email_addr='musson@protei.ru';


UPDATE companysubscription
set email_addr='support.eacd.crm@protei.ru'
where email_addr='support.eacd@protei.ru';


UPDATE companysubscription
set email_addr='support.callcenter.crm@protei.ru'
where email_addr='support.callcenter@protei.ru';



/*
SYBASE
 */
UPDATE "Resource".Tm_Emails
set strEmail='emergency.crm@protei.ru'
where strEmail='emergency@protei.ru';


UPDATE "Resource".Tm_Emails
set strEmail='musson.crm@protei.ru'
where strEmail='musson@protei.ru';


UPDATE "Resource".Tm_Emails
set strEmail='support.eacd.crm@protei.ru'
where strEmail='support.eacd@protei.ru';


UPDATE "Resource".Tm_Emails
set strEmail='support.callcenter.crm@protei.ru'
where strEmail='support.callcenter@protei.ru';