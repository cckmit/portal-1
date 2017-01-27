package ru.protei.portal.ui.project.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import ru.protei.portal.ui.project.client.activity.filter.AbstractProjectFilterView;
import ru.protei.portal.ui.project.client.activity.page.ProjectPage;
import ru.protei.portal.ui.project.client.activity.preview.AbstractProjectPreviewView;
import ru.protei.portal.ui.project.client.activity.preview.ProjectPreviewActivity;
import ru.protei.portal.ui.project.client.activity.table.AbstractProjectTableView;
import ru.protei.portal.ui.project.client.activity.table.ProjectTableActivity;
import ru.protei.portal.ui.project.client.view.filter.ProjectFilterView;
import ru.protei.portal.ui.project.client.view.preview.ProjectPreviewView;
import ru.protei.portal.ui.project.client.view.table.ProjectTableView;


/**
 * Описание классов фабрики
 */
public class ProjectClientModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind( ProjectPage.class ).asEagerSingleton();

        bind( ProjectTableActivity.class ).asEagerSingleton();
        bind( AbstractProjectTableView.class ).to(ProjectTableView.class);
        bind( AbstractProjectFilterView.class ).to(ProjectFilterView.class);

        bind( ProjectPreviewActivity.class).asEagerSingleton();
        bind( AbstractProjectPreviewView.class ).to( ProjectPreviewView.class );

//        bind( IssueEditActivity.class ).asEagerSingleton();
//        bind(AbstractIssueEditView.class).to(IssueEditView.class);

    }
}

