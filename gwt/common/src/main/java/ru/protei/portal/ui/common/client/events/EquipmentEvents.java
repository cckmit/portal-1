package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Name;
import ru.brainworm.factory.context.client.annotation.Omit;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.Equipment;

import java.util.List;

/**
 * События модуля - классификтор оборудования
 */
public class EquipmentEvents {

    /**
     * Показать
     */
    @Url( value = "equipments", primary = true )
    public static class Show {
        @Omit
        public Boolean preScroll = false;
        public Show () {}
        public Show (Boolean preScroll) {
            this.preScroll = preScroll;
        }
    }

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
        public DocumentEdit(Long equipmentId, Long projectId, List<String> decimalNumbers) {
            this.equipmentId = equipmentId;
            this.projectId = projectId;
            this.decimalNumbers = decimalNumbers;
        }

        @Name("document")
        public Long documentId;
        @Name("equipment")
        public Long equipmentId;
        @Name("project")
        public Long projectId;
        @Omit
        public List<String> decimalNumbers;
    }

    public static class ShowDocumentList {
        public ShowDocumentList(HasWidgets parent, Long equipmentId) {
            this.parent = parent;
            this.equipmentId = equipmentId;
        }
        public HasWidgets parent;
        public Long equipmentId;
    }

    public static class ChangeModel {}

    public static class ShowCopyDialog {
        public ShowCopyDialog( Equipment equipment ) {
            this.equipment = equipment;
        }

        public Equipment equipment;
    }
}
