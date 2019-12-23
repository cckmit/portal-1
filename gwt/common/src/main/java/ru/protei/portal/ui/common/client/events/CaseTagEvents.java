package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseTag;

public class CaseTagEvents {

    public static class Show {
        public Show() {}

        public Show(HasWidgets parent) {
            this.parent = parent;
        }

        public Show withCaseType(En_CaseType caseType) {
            this.caseType = caseType;
            return this;
        }

        public Show withCaseId(Long caseId) {
            this.caseId = caseId;
            return this;
        }

        public Show withEditEnabled(boolean isEditEnabled) {
            this.isEditTagEnabled = isEditEnabled;
            return this;
        }

        public Show withAddEnabled(boolean isAddEnabled) {
            this.isAddNewTagEnabled = isAddEnabled;
            return this;
        }

        public Show withReadOnly(boolean isReadOnly) {
            this.isReadOnly = isReadOnly;
            return this;
        }

        public Show readOnly() {
            return withReadOnly(true);
        }

        public HasWidgets parent;
        public Long caseId;
        public En_CaseType caseType;
        public boolean isReadOnly = false;
        public boolean isEditTagEnabled = false;
        public boolean isAddNewTagEnabled = false;
    }

    public static class Edit {
        public Edit(CaseTag caseTag) {
            this.caseTag = caseTag;
        }

        public CaseTag caseTag;
    }

    public static class ChangeModel {}

    public static class Remove {
        public Remove(CaseTag caseTag) {
            this.caseTag = caseTag;
        }

        public CaseTag caseTag;
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
        public ShowTagSelector(IsWidget anchor) {
            this.anchor = anchor;
        }

        public IsWidget anchor;
    }
}
