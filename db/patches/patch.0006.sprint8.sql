alter table case_object add (product_id bigint);
alter table case_object add constraint fk_case_product foreign key (product_id) references Dev_Unit(id);
alter table case_object add (deleted int default 0 not null);
alter table case_object add (private_flag int default 0 not null);