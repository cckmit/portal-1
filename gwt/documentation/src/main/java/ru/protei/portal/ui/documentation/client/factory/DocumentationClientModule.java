package ru.protei.portal.ui.documentation.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import ru.protei.portal.ui.documentation.client.activity.edit.AbstractDocumentationEditView;
import ru.protei.portal.ui.documentation.client.activity.edit.DocumentationEditActivity;
import ru.protei.portal.ui.documentation.client.activity.page.DocumentationPage;
import ru.protei.portal.ui.documentation.client.activity.table.AbstractDocumentationTableView;
import ru.protei.portal.ui.documentation.client.activity.table.DocumentationTableActivity;
import ru.protei.portal.ui.documentation.client.view.edit.DocumentationEditView;
import ru.protei.portal.ui.documentation.client.view.table.DocumentationTableView;

public class DocumentationClientModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind(DocumentationPage.class).asEagerSingleton();

        bind(DocumentationTableActivity.class).asEagerSingleton();
        bind(AbstractDocumentationTableView.class).to(DocumentationTableView.class);

        bind(DocumentationEditActivity.class).asEagerSingleton();
        bind(AbstractDocumentationEditView.class).to(DocumentationEditView.class);
    }
}
