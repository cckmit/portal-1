package ru.protei.portal.ui.equipment.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.equipment.client.activity.copy.AbstractEquipmentCopyView;
import ru.protei.portal.ui.equipment.client.activity.copy.EquipmentCopyActivity;
import ru.protei.portal.ui.equipment.client.activity.edit.AbstractEquipmentEditView;
import ru.protei.portal.ui.equipment.client.activity.edit.EquipmentEditActivity;
import ru.protei.portal.ui.equipment.client.activity.filter.AbstractEquipmentFilterView;
import ru.protei.portal.ui.equipment.client.activity.preview.AbstractEquipmentPreviewView;
import ru.protei.portal.ui.equipment.client.activity.preview.EquipmentPreviewActivity;
import ru.protei.portal.ui.equipment.client.activity.table.AbstractEquipmentTableView;
import ru.protei.portal.ui.equipment.client.activity.table.EquipmentTableActivity;
import ru.protei.portal.ui.equipment.client.view.copy.EquipmentCopyView;
import ru.protei.portal.ui.equipment.client.view.document.list.EquipmentDocumentsListView;
import ru.protei.portal.ui.equipment.client.view.document.list.item.EquipmentDocumentsListItemView;
import ru.protei.portal.ui.equipment.client.view.edit.EquipmentEditView;
import ru.protei.portal.ui.equipment.client.view.filter.EquipmentFilterView;
import ru.protei.portal.ui.equipment.client.view.preview.EquipmentPreviewView;
import ru.protei.portal.ui.equipment.client.view.table.EquipmentTableView;
import ru.protei.portal.ui.equipment.client.activity.document.list.AbstractEquipmentDocumentsListView;
import ru.protei.portal.ui.equipment.client.activity.document.list.EquipmentDocumentsListActivity;
import ru.protei.portal.ui.equipment.client.activity.document.list.item.AbstractEquipmentDocumentsListItemView;

/**
 * Описание классов фабрики
 */
public class EquipmentClientModule extends AbstractGinModule {

    @Override
    protected void configure() {

        bind ( EquipmentTableActivity.class ).asEagerSingleton();
        bind ( AbstractEquipmentTableView.class ).to( EquipmentTableView.class ).in( Singleton.class );

        bind ( EquipmentPreviewActivity.class ).asEagerSingleton();
        bind ( AbstractEquipmentPreviewView.class ).to( EquipmentPreviewView.class ).in( Singleton.class );

        bind ( EquipmentEditActivity.class ).asEagerSingleton();
        bind ( AbstractEquipmentEditView.class ).to(EquipmentEditView.class).in(Singleton.class);

        bind ( AbstractEquipmentFilterView.class ).to( EquipmentFilterView.class ).in( Singleton.class );

        bind( EquipmentCopyActivity.class ).asEagerSingleton();
        bind( AbstractEquipmentCopyView.class ).to( EquipmentCopyView.class ).in( Singleton.class );

        bind(EquipmentDocumentsListActivity.class).asEagerSingleton();
        bind(AbstractEquipmentDocumentsListView.class).to(EquipmentDocumentsListView.class);
        bind(AbstractEquipmentDocumentsListItemView.class).to(EquipmentDocumentsListItemView.class);
    }
}

