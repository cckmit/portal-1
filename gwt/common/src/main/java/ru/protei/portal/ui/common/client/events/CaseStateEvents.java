package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Omit;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.view.EntityOption;

/**
 * События "Статусы обращений"
 */
public class CaseStateEvents {


    @Url( value = "casestates", primary = true )
    public static class Show {

        public Show () {}

    }

    /**
     * Показать таблицу ролей
     */
    public static class ShowTable {

        public ShowTable ( HasWidgets parent, Long companyId) {
            this.parent = parent;
            this.companyId = companyId;
        }

        public HasWidgets parent;
        public Long companyId;
    }

    /**
     * Показать превью статуса обращения
     */
    public static class ShowPreview {

        public ShowPreview ( HasWidgets parent, CaseState caseState )
        {
            this.parent = parent;
            this.caseState = caseState;
        }

        public CaseState caseState;
        public HasWidgets parent;
    }


    @Url( value = "casestate" )
    public static class Edit {

        public Long id;

        public Edit() {}

        public Edit( Long id ) { this.id = id; }
    }

    /**
     * Добавление / изменение / удаление ролей
     */
    public static class ChangeModel {}

    public static class UpdateItem {
        public CaseState caseState;

        public UpdateItem(CaseState caseState) {
            this.caseState = caseState;
        }
    }

    public static class UpdateSelectorOptions {
    }
}