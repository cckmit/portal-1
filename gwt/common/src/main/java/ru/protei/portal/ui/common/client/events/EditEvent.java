package ru.protei.portal.ui.common.client.events;

import com.google.gwt.event.shared.GwtEvent;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.ent.CompanyDepartment;

public class EditEvent extends GwtEvent<EditHandler> {
    private static Type<EditHandler> TYPE;
    public CaseTag caseTag;
    public CompanyDepartment companyDepartment;
    public boolean isReadOnly;

    public static <T> void fire(HasEditHandlers source, CaseTag caseTag, boolean isReadOnly) {
        if(TYPE != null) {
            source.fireEvent(new EditEvent(caseTag, isReadOnly));
        }
    }

    public static <T> void fire(HasEditHandlers source, CompanyDepartment companyDepartment) {
        if(TYPE != null) {
            source.fireEvent(new EditEvent(companyDepartment));
        }
    }

    public static Type<EditHandler> getType() {
        if(TYPE == null) {
            TYPE = new Type();
        }

        return TYPE;
    }

    protected EditEvent(CaseTag caseTag, boolean isReadOnly) {
        this.caseTag = caseTag;
        this.isReadOnly = isReadOnly;
    }

    public EditEvent(CompanyDepartment companyDepartment){
        this.companyDepartment = companyDepartment;
    }

    @Override
    public Type<EditHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(EditHandler handler) {
        handler.onEdit(this);
    }
}
