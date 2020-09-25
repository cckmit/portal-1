update dev_unit SET UNIT_NAME = 'ОБЖ-112' WHERE UNIT_NAME = 'ОБЖ(112)';
update dev_unit SET UNIT_NAME = 'ОБЖ-АПК БГ' WHERE UNIT_NAME = 'БГ';

insert into dev_unit (UTYPE_ID, CREATED, UNIT_NAME, UNIT_STATE) values (3, sysdate(), 'ОБЖ-CC', 1);
insert into dev_unit (UTYPE_ID, CREATED, UNIT_NAME, UNIT_STATE) values (3, sysdate(), 'ОБЖ-02', 1);
insert into dev_unit (UTYPE_ID, CREATED, UNIT_NAME, UNIT_STATE) values (3, sysdate(), 'ОБЖ-СЦ', 1);
insert into dev_unit (UTYPE_ID, CREATED, UNIT_NAME, UNIT_STATE) values (3, sysdate(), 'ОБЖ-ВП(ВА)', 1);
insert into dev_unit (UTYPE_ID, CREATED, UNIT_NAME, UNIT_STATE) values (3, sysdate(), 'ОБЖ-ВКС (NGN)', 1);