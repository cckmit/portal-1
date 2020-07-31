package ru.protei.portal.ui.role.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.helper.HTMLHelper;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;
import ru.protei.portal.ui.common.client.lang.En_ScopeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.role.client.activity.table.AbstractRoleTableActivity;
import ru.protei.portal.ui.role.client.activity.table.AbstractRoleTableView;

import java.util.ArrayList;
import java.util.List;

/**
 * Представление таблицы роли
 */
public class RoleTableView extends Composite implements AbstractRoleTableView {

    @Inject
    public void onInit( EditClickColumn<UserRole> editClickColumn, RemoveClickColumn< UserRole > removeClickColumn
    ) {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        this.editClickColumn = editClickColumn;
        this.removeClickColumn = removeClickColumn;
        initTable();
    }

    @Override
    public void setActivity( AbstractRoleTableActivity activity ) {
        this.activity = activity;

        editClickColumn.setHandler( activity );
        editClickColumn.setEditHandler( activity );
        editClickColumn.setColumnProvider( columnProvider );
        removeClickColumn.setColumnProvider( columnProvider );
        removeClickColumn.setRemoveHandler( activity );
        columns.forEach( clickColumn -> {
            clickColumn.setHandler( activity );
            clickColumn.setColumnProvider( columnProvider );
        });
    }

    @Override
    public void setAnimation ( TableAnimation animation ) {
        animation.setContainers( tableContainer, previewContainer, filterContainer );
    }

    @Override
    public HasWidgets getPreviewContainer () { return previewContainer; }

    @Override
    public HasWidgets getFilterContainer () { return filterContainer; }

    @Override
    public void clearRecords() {
        table.clearRows();
    }

    @Override
    public void clearSelection() {
        columnProvider.removeSelection();
    }

    @Override
    public void setData( List<UserRole> roles ) {
        for ( UserRole role : roles ) {
            table.addRow( role );
        }
    }

    private void initTable () {
        editClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.ROLE_EDIT) );
        removeClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.ROLE_REMOVE) );

        ClickColumn< UserRole > advanceInfo = new ClickColumn< UserRole >() {
            @Override
            protected void fillColumnHeader( Element element ) {}

            @Override
            public void fillColumnValue ( Element cell, UserRole value ) {
                String htmlValue = "";
                String scopeStyleName = "warning";
                if ( value.getScope().equals( En_Scope.SYSTEM )) {
                    scopeStyleName = "inverse";
                } else if ( value.getScope().equals( En_Scope.COMPANY )) {
                    scopeStyleName = "success";
                }
                htmlValue += "<p class=\"label label-" + scopeStyleName + "\">" + scopeLang.getName(value.getScope()) + "</p>";
                if ( value.isDefaultForContact() ) {
                    htmlValue += "<i class=\"inline fa fa-lg fa-flag text-danger m-l-10\"></i>";
                }
                cell.setInnerHTML( HTMLHelper.wrapDiv( htmlValue ));
            }
        };
        columns.add( advanceInfo );

        ClickColumn< UserRole > name = new ClickColumn< UserRole >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.roleName() );
            }

            @Override
            public void fillColumnValue ( Element cell, UserRole value ) {
                cell.setInnerText( value.getCode() == null ? "" : value.getCode() );
            }
        };
        columns.add( name );

        ClickColumn< UserRole > description = new ClickColumn< UserRole >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.roleDescription() );
            }

            @Override
            public void fillColumnValue ( Element cell, UserRole value ) {
                cell.setInnerText( value.getInfo() == null ? "" : value.getInfo() );
            }
        };
        columns.add( description );

        table.addColumn( advanceInfo.header, advanceInfo.values );
        table.addColumn( name.header, name.values );
        table.addColumn( description.header, description.values );
        table.addColumn( editClickColumn.header, editClickColumn.values );
        table.addColumn( removeClickColumn.header, removeClickColumn.values );
    }

    @UiField
    TableWidget<UserRole> table;

    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;

    @UiField
    Lang lang;

    @Inject
    private En_ScopeLang scopeLang;
    @Inject
    private PolicyService policyService;

    private AbstractRoleTableActivity activity;

    private ClickColumnProvider<UserRole> columnProvider = new ClickColumnProvider<>();
    private EditClickColumn<UserRole > editClickColumn;
    private RemoveClickColumn<UserRole> removeClickColumn;
    private List<ClickColumn > columns = new ArrayList<>();

    private static ContactTableViewUiBinder ourUiBinder = GWT.create( ContactTableViewUiBinder.class );
    interface ContactTableViewUiBinder extends UiBinder< HTMLPanel, RoleTableView > {}
}
