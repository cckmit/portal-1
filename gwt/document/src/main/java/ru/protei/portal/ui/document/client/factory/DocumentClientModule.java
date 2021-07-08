package ru.protei.portal.ui.document.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.document.client.activity.create.AbstractDocumentCreateView;
import ru.protei.portal.ui.document.client.activity.create.DocumentCreateActivity;
import ru.protei.portal.ui.document.client.activity.edit.AbstractDocumentEditView;
import ru.protei.portal.ui.document.client.activity.edit.DocumentEditActivity;
import ru.protei.portal.ui.document.client.activity.filter.AbstractDocumentFilterView;
import ru.protei.portal.ui.document.client.activity.page.DocumentPage;
import ru.protei.portal.ui.document.client.activity.page.DocumentTypePage;
import ru.protei.portal.ui.document.client.activity.page.DocumentationPage;
import ru.protei.portal.ui.document.client.activity.page.EquipmentPage;
import ru.protei.portal.ui.document.client.activity.preview.AbstractDocumentPreviewView;
import ru.protei.portal.ui.document.client.activity.preview.DocumentPreviewActivity;
import ru.protei.portal.ui.document.client.activity.table.AbstractDocumentTableView;
import ru.protei.portal.ui.document.client.activity.table.DocumentTableActivity;
import ru.protei.portal.ui.document.client.view.create.DocumentCreateView;
import ru.protei.portal.ui.document.client.view.edit.DocumentEditView;
import ru.protei.portal.ui.document.client.view.filter.DocumentFilterView;
import ru.protei.portal.ui.document.client.view.preview.DocumentPreviewView;
import ru.protei.portal.ui.document.client.view.table.DocumentTableView;
import ru.protei.portal.ui.common.client.widget.document.doctype.DocumentTypeModel;

public class DocumentClientModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind(DocumentationPage.class).asEagerSingleton();
        bind(DocumentPage.class).asEagerSingleton();
        bind(DocumentTypePage.class).asEagerSingleton();
        bind(EquipmentPage.class).asEagerSingleton();

        bind(DocumentTableActivity.class).asEagerSingleton();
        bind(AbstractDocumentTableView.class).to(DocumentTableView.class).in(Singleton.class);

        bind(DocumentCreateActivity.class).asEagerSingleton();
        bind(AbstractDocumentCreateView.class).to(DocumentCreateView.class).in(Singleton.class);
        bind(DocumentEditActivity.class).asEagerSingleton();
        bind(AbstractDocumentEditView.class).to(DocumentEditView.class).in(Singleton.class);

        bind(AbstractDocumentFilterView.class).to(DocumentFilterView.class).in(Singleton.class);

        bind(DocumentPreviewActivity.class).asEagerSingleton();
        bind(AbstractDocumentPreviewView.class).to(DocumentPreviewView.class).in(Singleton.class);

        bind(DocumentTypeModel.class).asEagerSingleton();
    }
}
