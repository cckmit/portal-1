package ru.protei.portal.ui.project.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import ru.protei.portal.ui.project.client.activity.filter.AbstractProjectFilterView;
import ru.protei.portal.ui.project.client.activity.list.AbstractProjectDocumentsListView;
import ru.protei.portal.ui.project.client.activity.list.ProjectDocumentsListActivity;
import ru.protei.portal.ui.project.client.activity.list.item.AbstractProjectDocumentsListItemView;
import ru.protei.portal.ui.project.client.activity.page.ProjectPage;
import ru.protei.portal.ui.project.client.activity.preview.AbstractProjectPreviewView;
import ru.protei.portal.ui.project.client.activity.preview.ProjectPreviewActivity;
import ru.protei.portal.ui.project.client.activity.table.AbstractProjectTableView;
import ru.protei.portal.ui.project.client.activity.table.ProjectTableActivity;
import ru.protei.portal.ui.project.client.view.filter.ProjectFilterView;
import ru.protei.portal.ui.project.client.view.list.ProjectDocumentsListView;
import ru.protei.portal.ui.project.client.view.list.item.ProjectDocumentsListItemView;
import ru.protei.portal.ui.project.client.view.preview.ProjectPreviewView;
import ru.protei.portal.ui.project.client.view.table.ProjectTableView;
import ru.protei.portal.ui.project.client.view.widget.team.AbstractTeamSelector;
import ru.protei.portal.ui.project.client.view.widget.team.TeamSelector;
import ru.protei.portal.ui.project.client.view.widget.team.item.AbstractTeamSelectorItem;
import ru.protei.portal.ui.project.client.view.widget.team.item.TeamSelectorItem;


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

        bind( AbstractTeamSelector.class ).to( TeamSelector.class );
        bind( AbstractTeamSelectorItem.class ).to( TeamSelectorItem.class );

        bind( ProjectDocumentsListActivity.class ).asEagerSingleton();
        bind( AbstractProjectDocumentsListView.class ).to( ProjectDocumentsListView.class );
        bind( AbstractProjectDocumentsListItemView.class ).to( ProjectDocumentsListItemView.class );

    }
}

