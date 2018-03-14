package ru.protei.portal.ui.documentation.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.documentation.client.activity.edit.AbstractDocumentationEditView;
import ru.protei.portal.ui.documentation.client.activity.edit.DocumentationEditActivity;
import ru.protei.portal.ui.documentation.client.activity.filter.AbstractDocumentationFilterView;
import ru.protei.portal.ui.documentation.client.activity.page.DocumentationPage;
import ru.protei.portal.ui.documentation.client.activity.table.AbstractDocumentationTableView;
import ru.protei.portal.ui.documentation.client.activity.table.DocumentationTableActivity;
import ru.protei.portal.ui.documentation.client.view.edit.DocumentationEditView;
import ru.protei.portal.ui.documentation.client.view.filter.DocumentationFilterView;
import ru.protei.portal.ui.documentation.client.view.table.DocumentationTableView;
import ru.protei.portal.ui.documentation.client.widget.doctype.DocumentTypeModel;
import ru.protei.portal.ui.documentation.client.widget.select.item.AbstractSelectItemView;
import ru.protei.portal.ui.documentation.client.widget.select.item.SelectItemView;

public class DocumentationClientModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind(DocumentationPage.class).asEagerSingleton();

        bind(DocumentationTableActivity.class).asEagerSingleton();
        bind(AbstractDocumentationTableView.class).to(DocumentationTableView.class).in(Singleton.class);

        bind(DocumentationEditActivity.class).asEagerSingleton();
        bind(AbstractDocumentationEditView.class).to(DocumentationEditView.class).in(Singleton.class);

        bind(AbstractDocumentationFilterView.class).to(DocumentationFilterView.class).in(Singleton.class);

        bind(DocumentTypeModel.class).asEagerSingleton();

        bind(AbstractSelectItemView.class).to(SelectItemView.class);
    }
}
