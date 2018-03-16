package ru.protei.portal.ui.documentation.client.activity.edit;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.view.PersonShortView;

public interface AbstractDocumentationEditView extends IsWidget {

    void setActivity(AbstractDocumentationEditActivity activity);

    HasValue<String> name();

    HasValue<DocumentType> documentType();

    HasValue<String> project();

    HasValue<PersonShortView> manager();

    HasValue<String> annotation();

    HasValue<String> created();
}
