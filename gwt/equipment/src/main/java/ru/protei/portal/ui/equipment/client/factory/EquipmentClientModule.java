package ru.protei.portal.ui.equipment.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.equipment.client.activity.copy.AbstractEquipmentCopyView;
import ru.protei.portal.ui.equipment.client.activity.copy.EquipmentCopyActivity;
import ru.protei.portal.ui.equipment.client.activity.edit.AbstractEquipmentEditView;
import ru.protei.portal.ui.equipment.client.activity.edit.EquipmentEditActivity;
import ru.protei.portal.ui.equipment.client.activity.filter.AbstractEquipmentFilterView;
import ru.protei.portal.ui.equipment.client.activity.page.EquipmentPage;
import ru.protei.portal.ui.equipment.client.activity.preview.AbstractEquipmentPreviewView;
import ru.protei.portal.ui.equipment.client.activity.preview.EquipmentPreviewActivity;
import ru.protei.portal.ui.equipment.client.activity.table.AbstractEquipmentTableView;
import ru.protei.portal.ui.equipment.client.activity.table.EquipmentTableActivity;
import ru.protei.portal.ui.equipment.client.provider.AbstractDecimalNumberDataProvider;
import ru.protei.portal.ui.equipment.client.provider.DecimalNumberDataProvider;
import ru.protei.portal.ui.equipment.client.view.copy.EquipmentCopyView;
import ru.protei.portal.ui.equipment.client.view.edit.EquipmentEditView;
import ru.protei.portal.ui.equipment.client.view.filter.EquipmentFilterView;
import ru.protei.portal.ui.equipment.client.view.preview.EquipmentPreviewView;
import ru.protei.portal.ui.equipment.client.view.table.EquipmentTableView;

/**
 * Описание классов фабрики
 */
public class EquipmentClientModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind ( EquipmentPage.class ).asEagerSingleton();

        bind ( EquipmentTableActivity.class ).asEagerSingleton();
        bind ( AbstractEquipmentTableView.class ).to( EquipmentTableView.class ).in( Singleton.class );

        bind ( EquipmentPreviewActivity.class ).asEagerSingleton();
        bind ( AbstractEquipmentPreviewView.class ).to( EquipmentPreviewView.class ).in( Singleton.class );

        bind ( EquipmentEditActivity.class ).asEagerSingleton();
        bind ( AbstractEquipmentEditView.class ).to(EquipmentEditView.class).in(Singleton.class);

        bind ( AbstractEquipmentFilterView.class ).to( EquipmentFilterView.class ).in( Singleton.class );
        bind ( AbstractDecimalNumberDataProvider.class ).to( DecimalNumberDataProvider.class ).in( Singleton.class );

        bind( EquipmentCopyActivity.class ).asEagerSingleton();
        bind( AbstractEquipmentCopyView.class ).to( EquipmentCopyView.class ).in( Singleton.class );
    }
}

