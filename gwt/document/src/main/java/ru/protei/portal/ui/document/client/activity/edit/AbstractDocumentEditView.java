package ru.protei.portal.ui.document.client.activity.edit;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.List;

public interface AbstractDocumentEditView extends IsWidget {

    void setActivity(AbstractDocumentEditActivity activity);

    void setEnabledProject(boolean isEnabled);

    void setVisibleUploader(boolean isVisible);

    HasValue<String> name();

    HasValue<DocumentType> documentType();

    HasValue<ProjectInfo> project();

    HasValue<PersonShortView> manager();

    HasValue<String> annotation();

    HasValue<String> created();

    HasValue<Long> inventoryNumber();

    HasValue<List<String>> keywords();

    HasValue<DecimalNumber> decimalNumber();

    boolean isDecimalNumbersCorrect();
}
