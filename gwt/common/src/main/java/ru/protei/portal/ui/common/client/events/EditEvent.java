package ru.protei.portal.ui.common.client.events;

import com.google.gwt.event.shared.GwtEvent;
import ru.protei.portal.core.model.ent.CaseTag;

public class EditEvent extends GwtEvent<EditHandler> {
    private static Type<EditHandler> TYPE;
    public CaseTag caseTag;

    public static <T> void fire(HasEditHandlers source, CaseTag caseTag) {
        if(TYPE != null) {
            source.fireEvent(new EditEvent(caseTag));
        }
    }

    public static Type<EditHandler> getType() {
        if(TYPE == null) {
            TYPE = new Type();
        }

        return TYPE;
    }

    protected EditEvent(CaseTag caseTag) {
        this.caseTag = caseTag;
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
