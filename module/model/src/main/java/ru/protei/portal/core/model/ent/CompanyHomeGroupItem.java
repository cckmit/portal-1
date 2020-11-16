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

        @JdbcColumn(name = Columns.COMPANY_ID)
        private Long companyId;

        @JdbcColumn(name = "external_code")
        private String externalCode;

        @JdbcColumn(name = "mainId")
        private Long mainId;

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

        public String getExternalCode() { return externalCode; }

        public void setExternalCode(String externalCode) { this.externalCode = externalCode; }

        public Long getMainId() {
                return mainId;
        }

        public void setMainId(Long mainId) {
                this.mainId = mainId;
        }

        public interface Columns{
                String COMPANY_ID = "companyId";
        }
}
