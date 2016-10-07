create table company_group (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  created datetime DEFAULT current_timestamp,
  group_name varchar(200),
  group_info varchar(1000),
  PRIMARY KEY (id),
  KEY uq_compgroup (group_name)
);


create table company_group_item (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  group_id bigint(20) not null,
  company_id bigint(20) not null,
  PRIMARY KEY (id),
  CONSTRAINT FK_CG_COMPANY FOREIGN KEY (company_id) REFERENCES company (id),
  CONSTRAINT FK_CG_GROUP FOREIGN KEY (group_id) REFERENCES company_group (id),
  CONSTRAINT UQ_COMP_GR_ITEM unique (group_id,company_id)
);


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