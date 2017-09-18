insert into company_category (id, category_name) VALUES (1,'Заказчик');
insert into company_category (id, category_name) VALUES (2,'Партнер');
insert into company_category (id, category_name) VALUES (3,'Субподрядчик');

create table person_company_list (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  person_id bigint(20) not null,
  company_id bigint(20) not null,
  favorite int not null default 1,
  subscription int not null default 0,

  PRIMARY KEY (id),

  CONSTRAINT FK_PCLIST_COMPANY FOREIGN KEY (company_id) REFERENCES company (id),
  CONSTRAINT FK_PCLIST FOREIGN KEY (person_id) REFERENCES person (id),
  CONSTRAINT UQ_PCLIST unique (person_id, company_id)
);

alter table company add (
  category_id bigint(20),
  constraint fk_company_category FOREIGN KEY (category_id) REFERENCES company_category (id) on delete set null
);
