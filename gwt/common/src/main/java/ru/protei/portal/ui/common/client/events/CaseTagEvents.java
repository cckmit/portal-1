package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.UIObject;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.ui.common.client.activity.casetag.taglist.AbstractCaseTagListActivity;

import java.util.List;
import java.util.function.Consumer;

public class CaseTagEvents {

    public static class ShowList {
        public ShowList() {}

        public ShowList(HasWidgets parent, En_CaseType caseType, Long caseId, boolean isReadOnly, Consumer<AbstractCaseTagListActivity> tagListActivityConsumer) {
            this.parent = parent;
            this.caseType = caseType;
            this.caseId = caseId;
            this.caseTags = null;
            this.isReadOnly = isReadOnly;
            this.tagListActivityConsumer = tagListActivityConsumer;
        }

        public ShowList(HasWidgets parent, En_CaseType caseType, List<CaseTag> caseTags, boolean isReadOnly, Consumer<AbstractCaseTagListActivity> tagListActivityConsumer) {
            this.parent = parent;
            this.caseType = caseType;
            this.caseId = null;
            this.caseTags = caseTags;
            this.isReadOnly = isReadOnly;
            this.tagListActivityConsumer = tagListActivityConsumer;
        }

        public HasWidgets parent;
        public En_CaseType caseType;
        public Long caseId;
        public List<CaseTag> caseTags;
        public boolean isReadOnly = false;
        public Consumer<AbstractCaseTagListActivity> tagListActivityConsumer;
    }

    public static class ShowSelector {
        public ShowSelector() {}

        public ShowSelector(UIObject relative, En_CaseType caseType, boolean isEditTagEnabled, AbstractCaseTagListActivity tagListActivity) {
            this.relative = relative;
            this.caseType = caseType;
            this.isEditTagEnabled = isEditTagEnabled;
            this.tagListActivity = tagListActivity;
        }

        public UIObject relative;
        public En_CaseType caseType;
        public boolean isEditTagEnabled = false;
        public AbstractCaseTagListActivity tagListActivity;
    }

    public static class ShowEdit {
        public ShowEdit(CaseTag caseTag) {
            this.caseTag = caseTag;
        }

        public CaseTag caseTag;
    }

    public static class Created {
        public Created( CaseTag caseTag) {
            this.caseTag = caseTag;
        }

        public CaseTag caseTag;
    }

    public static class Changed {
        public Changed( CaseTag caseTag) {
            this.caseTag = caseTag;
        }

        public CaseTag caseTag;
    }

    public static class Removed {
        public Removed( CaseTag caseTag) {
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
}
