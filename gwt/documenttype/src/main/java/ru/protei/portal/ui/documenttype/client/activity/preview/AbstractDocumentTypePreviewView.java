package ru.protei.portal.ui.documenttype.client.activity.preview;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

public interface AbstractDocumentTypePreviewView extends IsWidget {

    void setActivity(AbstractDocumentTypePreviewActivity activity);

    HasValue<String> name();

    HasValue<String> shortName();

    HasValue<En_DocumentCategory> category();

    HasValue<String> gost();

    HasValidable nameValidation();

    HasValidable shortNameValidation();

    HasValidable gostValidation();
}
