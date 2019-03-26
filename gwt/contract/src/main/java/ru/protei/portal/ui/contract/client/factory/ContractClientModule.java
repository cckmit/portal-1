package ru.protei.portal.ui.contract.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.contract.client.activity.edit.AbstractContractEditView;
import ru.protei.portal.ui.contract.client.activity.edit.ContractEditActivity;
import ru.protei.portal.ui.contract.client.activity.filter.AbstractContractFilterView;
import ru.protei.portal.ui.contract.client.activity.page.ContractPage;
import ru.protei.portal.ui.contract.client.activity.preview.AbstractContractPreviewView;
import ru.protei.portal.ui.contract.client.activity.preview.ContractPreviewActivity;
import ru.protei.portal.ui.contract.client.activity.table.AbstractContractTableView;
import ru.protei.portal.ui.contract.client.activity.table.ContractTableActivity;
import ru.protei.portal.ui.contract.client.view.edit.ContractEditView;
import ru.protei.portal.ui.contract.client.view.filter.ContractFilterView;
import ru.protei.portal.ui.contract.client.view.preview.ContractPreviewView;
import ru.protei.portal.ui.contract.client.view.table.ContractTableView;

public class ContractClientModule extends AbstractGinModule {
    @Override
    protected void configure()    {
        bind(ContractPage.class).asEagerSingleton();

        bind(ContractTableActivity.class).asEagerSingleton();
        bind(AbstractContractTableView.class).to(ContractTableView.class).in(Singleton.class);

        bind(ContractEditActivity.class).asEagerSingleton();
        bind(AbstractContractEditView.class).to(ContractEditView.class).in(Singleton.class);

        bind(ContractPreviewActivity.class).asEagerSingleton();
        bind(AbstractContractPreviewView.class).to(ContractPreviewView.class).in(Singleton.class);

        bind(AbstractContractFilterView.class).to(ContractFilterView.class).in(Singleton.class);
    }
}
