package ru.protei.portal.ui.company.client.activity.edit;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.CompanyEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.company.client.service.CompanyServiceAsync;
import ru.protei.portal.ui.company.client.view.edit.CompanyView;

import java.util.List;


/**
 * Активность списка компаний
 */
public abstract class CompanyActivity implements AbstractCompanyActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow( CompanyEvents.Edit event ) {

        this.fireEvent( new AppEvents.InitPanelName( lang.newCompany() ) );

        initDetails.parent.clear();
        resetFields();
        initDetails.parent.add( view.asWidget() );


    }

    private void resetFields(){
        view.setCompanyNameStatus(CompanyView.CompanyNameStatus.UNDEFINED);
        view.companyName().setText("");
        view.setActualAddress("");
        view.setLegalAddress("");
        view.setWebSite("");
        view.setComment("");
    }

    @Override
    public void onSaveClicked() {
        if(view.companyName().getText().isEmpty() || view.getActualAddress().isEmpty() || view.getLegalAddress().isEmpty()){
            fireEvent( new NotifyEvents.Show(lang.asteriskRequired(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        Company company = new Company();
        company.setCname(view.companyName().getText());
        company.setInfo(view.getComment());
        company.setWebsite(view.getWebSite());
        company.setAddressDejure(view.getLegalAddress());
        company.setAddressFact(view.getActualAddress());

        companyService.setCompany(company, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable throwable) {
                fireEvent( new NotifyEvents.Show(lang.companyNotSaved(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Void aVoid) {
                fireEvent( new CompanyEvents.Show());
                fireEvent(new NotifyEvents.Show(lang.companySaved(), NotifyEvents.NotifyType.SUCCESS));
            }
        });

    }

    @Override
    public void onCancelClicked() {
        fireEvent(new CompanyEvents.Show());
    }

    @Override
    public void onChangeCompanyName() {
        if(view.companyName().getText().isEmpty()){
            view.setCompanyNameStatus(CompanyView.CompanyNameStatus.UNDEFINED);
            return;
        }

        companyService.getCompanies(
                view.companyName().getText(),
                null,
                null,
                En_SortField.comp_name,
                false,
                new AsyncCallback<List<Company>>() {

            @Override
            public void onFailure(Throwable throwable) {
                fireEvent( new NotifyEvents.Show(lang.errorGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(List<Company> companies) {
                if(companies.size() == 0){
                    view.setCompanyNameStatus(CompanyView.CompanyNameStatus.SUCCESS);
                }else{
                    view.setCompanyNameStatus(CompanyView.CompanyNameStatus.ERROR);
                }
            }
        });
    }


    //    native void consoleLog( String message) /*-{
//        console.log( "me:" + message );
//    }-*/;

    @Inject
    AbstractCompanyView view;
    @Inject
    Lang lang;

    @Inject
    CompanyServiceAsync companyService;
    private AppEvents.InitDetails initDetails;
}
