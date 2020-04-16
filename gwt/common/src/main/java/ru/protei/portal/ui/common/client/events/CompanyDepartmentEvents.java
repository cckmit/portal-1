package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.ent.CompanyDepartment;

public class CompanyDepartmentEvents {

    public static class Show {
        public Show() {}

        public Show( HasWidgets parent, En_CaseType caseType, boolean isEditTagEnabled ) {
            this(parent, caseType, isEditTagEnabled, null, false);
        }

        public Show( HasWidgets parent, En_CaseType caseType, boolean isEditTagEnabled, Long caseId, boolean isReadOnly ) {
            this.parent = parent;
            this.caseType = caseType;
            this.isEditTagEnabled = isEditTagEnabled;
            this.caseId = caseId;
            this.isReadOnly = isReadOnly;
        }

        public HasWidgets parent;
        public Long caseId;
        public En_CaseType caseType;
        public boolean isReadOnly = false;
        public boolean isEditTagEnabled = false;
    }

    public static class Edit {
        public Edit(CompanyDepartment companyDepartment) {
            this.companyDepartment = companyDepartment;
        }

        public CompanyDepartment companyDepartment;
    }

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

    public static class Detach {
        public Detach(Long caseId, Long id) {
            this.caseId = caseId;
            this.id = id;
        }

        public Long caseId;
        public Long id;
    }

    public static class Attach {
        public Attach(Long caseId, CaseTag tag) {
            this.caseId = caseId;
            this.tag = tag;
        }

        public Long caseId;
        public CaseTag tag;
    }

    public static class ShowTagSelector {
        public ShowTagSelector() {}
        public ShowTagSelector(IsWidget target) {
            this.target = target;
        }

        public IsWidget target;
    }
}
