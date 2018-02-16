/**
Добавляем поля для обратной миграции
ext_id - это будет значение из новой базы (MySQL)
 */

alter table "Resource".Tm_Company add ext_id int;
alter table "Resource".Tm_Person add ext_id int;
alter table "Resource".Tm_Product add ext_id int;

update "Resource".Tm_Company
    set ext_id=nID;
COMMIT;

update "Resource".Tm_Person
set ext_id=nID;
COMMIT;

update "Resource".Tm_Product
set ext_id=nID;
COMMIT;


create index ix_company_extid on "Resource".Tm_Company(ext_id);
create index ix_person_extid on "Resource".Tm_Person(ext_id);
create index ix_product_extid on "Resource".Tm_Product(ext_id);