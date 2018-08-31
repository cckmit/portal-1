package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Name;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.view.EntityOption;

/**
 * События модуля - классификтор оборудования
 */
public class EquipmentEvents {

    /**
     * Показать
     */
    @Url( value = "equipments", primary = true )
    public static class Show {}

    /**
     * Показать превью оборудования
     */
    public static class ShowPreview {

        public ShowPreview ( HasWidgets parent, Equipment equipment )
        {
            this.parent = parent;
            this.equipment = equipment;
        }

        public Equipment equipment;
        public HasWidgets parent;

    }

    @Url( value = "equipment_preview", primary = true )
    public static class ShowFullScreen {

        public ShowFullScreen() {}

        public ShowFullScreen(Long id) {
            this.equipmentId = id;
        }

        @Name("id")
        public Long equipmentId;
    }

    @Url( value = "equipment", primary = false )
    public static class Edit {

        public Long id;

        public Edit() { this.id = null; }
        public Edit (Long id) {
            this.id = id;
        }
        public static Edit byId (Long id) {
            return new Edit(id);
        }
    }

    @Url(value = "eq_document")
    public static class DocumentEdit {

        public DocumentEdit() {}
        public DocumentEdit(Long documentId) {
            this.documentId = documentId;
        }
        public DocumentEdit(Long projectId, String decimalNumber) {
            this.projectId = projectId;
            this.decimalNumber = decimalNumber;
        }

        @Name("id")
        public Long documentId;
        @Name("project")
        public Long projectId;
        @Name("dn")
        public String decimalNumber;
    }

    public static class ShowDocumentList {
        public ShowDocumentList(HasWidgets parent, String decimalNumber) {
            this.parent = parent;
            this.decimalNumber = decimalNumber;
        }
        public HasWidgets parent;
        public String decimalNumber;
    }

    public static class ChangeModel {}

    public static class ShowCopyDialog {
        public ShowCopyDialog( Equipment equipment ) {
            this.equipment = equipment;
        }

        public Equipment equipment;
    }
}