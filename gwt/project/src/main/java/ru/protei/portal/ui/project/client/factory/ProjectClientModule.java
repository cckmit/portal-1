package ru.protei.portal.ui.project.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.project.client.activity.quickcreate.AbstractProjectCreateView;
import ru.protei.portal.ui.project.client.activity.quickcreate.ProjectCreateActivity;
import ru.protei.portal.ui.project.client.activity.edit.AbstractProjectEditView;
import ru.protei.portal.ui.project.client.activity.edit.ProjectEditActivity;
import ru.protei.portal.ui.project.client.activity.filter.AbstractProjectFilterView;
import ru.protei.portal.ui.project.client.activity.list.AbstractProjectDocumentsListView;
import ru.protei.portal.ui.project.client.activity.list.ProjectDocumentsListActivity;
import ru.protei.portal.ui.project.client.activity.list.item.AbstractProjectDocumentsListItemView;
import ru.protei.portal.ui.project.client.activity.page.ProjectPage;
import ru.protei.portal.ui.project.client.activity.preview.AbstractProjectPreviewView;
import ru.protei.portal.ui.project.client.activity.preview.ProjectPreviewActivity;
import ru.protei.portal.ui.project.client.activity.search.AbstractProjectSearchView;
import ru.protei.portal.ui.project.client.activity.search.ProjectSearchActivity;
import ru.protei.portal.ui.project.client.activity.table.AbstractProjectTableView;
import ru.protei.portal.ui.project.client.activity.table.ProjectTableActivity;
import ru.protei.portal.ui.project.client.view.quickcreate.ProjectCreateView;
import ru.protei.portal.ui.project.client.view.edit.ProjectEditView;
import ru.protei.portal.ui.project.client.view.filter.ProjectFilterView;
import ru.protei.portal.ui.project.client.view.list.ProjectDocumentsListView;
import ru.protei.portal.ui.project.client.view.list.item.ProjectDocumentsListItemView;
import ru.protei.portal.ui.project.client.view.preview.ProjectPreviewView;
import ru.protei.portal.ui.project.client.view.search.ProjectSearchView;
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
        bind( AbstractProjectTableView.class ).to(ProjectTableView.class );
        bind( AbstractProjectFilterView.class ).to(ProjectFilterView.class );

        bind( ProjectPreviewActivity.class ).asEagerSingleton();
        bind( AbstractProjectPreviewView.class ).to( ProjectPreviewView.class );

        bind( AbstractTeamSelector.class ).to( TeamSelector.class );
        bind( AbstractTeamSelectorItem.class ).to( TeamSelectorItem.class );

        bind( ProjectDocumentsListActivity.class ).asEagerSingleton();
        bind( AbstractProjectDocumentsListView.class ).to( ProjectDocumentsListView.class );
        bind( AbstractProjectDocumentsListItemView.class ).to( ProjectDocumentsListItemView.class );

        bind( ProjectCreateActivity.class ).asEagerSingleton();
        bind( AbstractProjectCreateView.class ).to( ProjectCreateView.class );

        bind( ProjectSearchActivity.class ).asEagerSingleton();
        bind( AbstractProjectSearchView.class ).to( ProjectSearchView.class ).in( Singleton.class );

        bind( ProjectEditActivity.class ).asEagerSingleton();
        bind( AbstractProjectEditView.class ).to( ProjectEditView.class );
    }
}

