package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

/**
 * Created by michael on 06.07.16.
 */
@JdbcEntity(table = "company_group_home")
public class CompanyHomeGroupItem {

        @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
        private Long id;

        @JdbcColumn(name = "companyId")
        private Long companyId;


        public CompanyHomeGroupItem() {
        }


        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public Long getCompanyId() {
                return companyId;
        }

        public void setCompanyId(Long companyId) {
                this.companyId = companyId;
        }
}
