package ru.protei.portal.ui.documenttype.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.documenttype.client.activity.filter.AbstractDocumentTypeFilterView;
import ru.protei.portal.ui.documenttype.client.activity.preview.AbstractDocumentTypePreviewView;
import ru.protei.portal.ui.documenttype.client.activity.preview.DocumentTypePreviewActivity;
import ru.protei.portal.ui.documenttype.client.activity.table.AbstractDocumentTypeTableView;
import ru.protei.portal.ui.documenttype.client.activity.table.DocumentTypeTableActivity;
import ru.protei.portal.ui.documenttype.client.view.filter.DocumentTypeFilterView;
import ru.protei.portal.ui.documenttype.client.view.preview.DocumentTypePreviewView;
import ru.protei.portal.ui.documenttype.client.view.table.DocumentTypeTableView;

public class DocumentTypeClientModule extends AbstractGinModule {

    @Override
    protected void configure() {

        bind(DocumentTypeTableActivity.class).asEagerSingleton();
        bind(AbstractDocumentTypeTableView.class).to(DocumentTypeTableView.class).in(Singleton.class);

        bind(DocumentTypePreviewActivity.class).asEagerSingleton();
        bind(AbstractDocumentTypePreviewView.class).to(DocumentTypePreviewView.class).in(Singleton.class);

        bind(AbstractDocumentTypeFilterView.class).to(DocumentTypeFilterView.class).in(Singleton.class);
    }
}
