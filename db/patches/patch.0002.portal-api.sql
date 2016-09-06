ALTER TABLE company_dep ADD head_id bigint(20) DEFAULT NULL;
ALTER TABLE company_dep ADD parent_department bigint(20) DEFAULT NULL;
ALTER TABLE company_dep ADD dep_extId bigint(20) NOT NULL DEFAULT '0';
ALTER TABLE company_dep ADD INDEX ix_parent_dep(parent_department);
ALTER TABLE company_dep ADD INDEX ix_dep_head(head_id);
ALTER TABLE company_dep ADD CONSTRAINT FK_PARENT_DEP FOREIGN KEY (parent_department) REFERENCES company_dep(id) ON DELETE CASCADE;
ALTER TABLE company_dep ADD CONSTRAINT FK_DEP_HEAD FOREIGN KEY (head_id) REFERENCES person(id) ON DELETE SET NULL;

ALTER TABLE worker_position ADD company_id bigint(20) NOT NULL AFTER pos_name;
ALTER TABLE worker_position ADD INDEX ix_company_pos(company_id);
ALTER TABLE worker_position ADD CONSTRAINT FK_COMPANY_POS FOREIGN KEY (company_id) REFERENCES company(id);

ALTER TABLE worker_entry ADD worker_extId bigint(20) NOT NULL DEFAULT '0';

ALTER TABLE company_group_home ADD external_code VARCHAR(32) NOT NULL DEFAULT '0';
ALTER TABLE company_group_home ADD UNIQUE ix_external_code(external_code);

ALTER TABLE person ADD isfired int(11) NOT NULL DEFAULT '0' AFTER isdeleted;

ALTER TABLE worker_position DROP COLUMN pos_code;

ALTER TABLE worker_entry DROP FOREIGN KEY FK_WORKER_POS;
ALTER TABLE worker_position MODIFY COLUMN id bigint(20) NOT NULL AUTO_INCREMENT;
ALTER TABLE worker_entry ADD CONSTRAINT `FK_WORKER_POS` FOREIGN KEY (`positionId`) REFERENCES `worker_position` (`id`);