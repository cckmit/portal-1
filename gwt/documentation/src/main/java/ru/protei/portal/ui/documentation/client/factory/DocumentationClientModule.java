package ru.protei.portal.ui.documentation.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.documentation.client.activity.page.DocumentationPage;
import ru.protei.portal.ui.documentation.client.activity.preview.AbstractDocumentationPreviewView;
import ru.protei.portal.ui.documentation.client.activity.preview.DocumentationPreviewActivity;
import ru.protei.portal.ui.documentation.client.activity.table.AbstractDocumentationTableView;
import ru.protei.portal.ui.documentation.client.activity.table.DocumentationTableActivity;
import ru.protei.portal.ui.documentation.client.common.DocumentationUtils;
import ru.protei.portal.ui.documentation.client.view.preview.DocumentationPreviewView;
import ru.protei.portal.ui.documentation.client.view.table.DocumentationTableView;

public class DocumentationClientModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind(DocumentationPage.class).asEagerSingleton();

        bind(DocumentationTableActivity.class).asEagerSingleton();
        bind(AbstractDocumentationTableView.class).to(DocumentationTableView.class);

        bind(DocumentationPreviewActivity.class).asEagerSingleton();
        bind(AbstractDocumentationPreviewView.class).to(DocumentationPreviewView.class).in(Singleton.class);

        requestStaticInjection(DocumentationUtils.class);
    }
}
