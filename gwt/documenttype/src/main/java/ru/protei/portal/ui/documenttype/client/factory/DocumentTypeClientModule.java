package ru.protei.portal.ui.documenttype.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import ru.protei.portal.ui.documenttype.client.activity.page.DocumentTypePage;

public class DocumentTypeClientModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(DocumentTypePage.class).asEagerSingleton();

        /*
        bind(DocumentTableActivity.class).asEagerSingleton();
        bind(AbstractDocumentTableView.class).to(DocumentTableView.class).in(Singleton.class);

        bind(DocumentEditActivity.class).asEagerSingleton();
        bind(AbstractDocumentEditView.class).to(DocumentEditView.class).in(Singleton.class);

        bind(AbstractDocumentFilterView.class).to(DocumentFilterView.class).in(Singleton.class);

        bind(DocumentPreviewActivity.class).asEagerSingleton();
        bind(AbstractDocumentPreviewView.class).to(DocumentPreviewView.class).in(Singleton.class);

        bind(DocumentTypeModel.class).asEagerSingleton();
         */
    }
}
