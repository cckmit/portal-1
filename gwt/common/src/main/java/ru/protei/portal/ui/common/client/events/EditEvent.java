package ru.protei.portal.ui.common.client.events;

import com.google.gwt.event.shared.GwtEvent;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.ent.CompanyDepartment;
import ru.protei.portal.core.model.ent.WorkerPosition;

public class EditEvent extends GwtEvent<EditHandler> {
    private static Type<EditHandler> TYPE;
    public CaseTag caseTag;
    public CompanyDepartment companyDepartment;
    public WorkerPosition workerPosition;
    public Long id;
    public String text;
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

    public static <T> void fire(HasEditHandlers source, WorkerPosition workerPosition) {
        if(TYPE != null) {
            source.fireEvent(new EditEvent(workerPosition));
        }
    }
    public static <T> void fire(HasEditHandlers source) {
        if(TYPE != null) {
            source.fireEvent(new EditEvent());
        }
    }

    public static <T> void fire(HasEditHandlers source, Long id, String text) {
        if(TYPE != null) {
            source.fireEvent(new EditEvent(id, text));
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

    public EditEvent(WorkerPosition workerPosition){
        this.workerPosition = workerPosition;
    }
    public EditEvent(){
    }

    public EditEvent(Long id, String text){
        this.id = id;
        this.text = text;
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
