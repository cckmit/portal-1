package ru.protei.portal.ui.document.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.document.client.activity.edit.AbstractDocumentEditView;
import ru.protei.portal.ui.document.client.activity.edit.DocumentEditActivity;
import ru.protei.portal.ui.document.client.activity.filter.AbstractDocumentFilterView;
import ru.protei.portal.ui.document.client.activity.page.DocumentPage;
import ru.protei.portal.ui.document.client.activity.table.AbstractDocumentTableView;
import ru.protei.portal.ui.document.client.activity.table.DocumentTableActivity;
import ru.protei.portal.ui.document.client.view.edit.DocumentEditView;
import ru.protei.portal.ui.document.client.view.filter.DocumentFilterView;
import ru.protei.portal.ui.document.client.view.table.DocumentTableView;
import ru.protei.portal.ui.document.client.widget.doctype.DocumentTypeModel;
import ru.protei.portal.ui.document.client.widget.select.item.AbstractSelectItemView;
import ru.protei.portal.ui.document.client.widget.select.item.SelectItemView;

public class DocumentClientModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind(DocumentPage.class).asEagerSingleton();

        bind(DocumentTableActivity.class).asEagerSingleton();
        bind(AbstractDocumentTableView.class).to(DocumentTableView.class).in(Singleton.class);

        bind(DocumentEditActivity.class).asEagerSingleton();
        bind(AbstractDocumentEditView.class).to(DocumentEditView.class).in(Singleton.class);

        bind(AbstractDocumentFilterView.class).to(DocumentFilterView.class).in(Singleton.class);

        bind(DocumentTypeModel.class).asEagerSingleton();

        bind(AbstractSelectItemView.class).to(SelectItemView.class);
    }
}
