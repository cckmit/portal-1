package ru.protei.portal.ui.common.client.events;

import ru.protei.portal.core.model.ent.CompanyDepartment;

public class CompanyDepartmentEvents {

    public static class Created {
        public Created( CompanyDepartment companyDepartment) {
            this.companyDepartment = companyDepartment;
        }

        public CompanyDepartment companyDepartment;
    }

    public static class Changed {
        public Changed( CompanyDepartment companyDepartment) {
            this.companyDepartment = companyDepartment;
        }

        public CompanyDepartment companyDepartment;
    }

    public static class Removed {
        public Removed( CompanyDepartment companyDepartment) {
            this.companyDepartment = companyDepartment;
        }

        public CompanyDepartment companyDepartment;
    }

    public static class Edit {
        public Edit( CompanyDepartment companyDepartment) {
            this.companyDepartment = companyDepartment;
        }

        public CompanyDepartment companyDepartment;
    }
}
