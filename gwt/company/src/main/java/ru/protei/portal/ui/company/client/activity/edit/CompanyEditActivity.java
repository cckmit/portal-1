package ru.protei.portal.ui.company.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.struct.ContactInfo;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.CompanyEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.common.client.service.CompanyServiceAsync;

import java.util.ArrayList;
import java.util.List;

/**
 * Активность создания и редактирования компании
 */
public abstract class CompanyEditActivity implements AbstractCompanyEditActivity, Activity {

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
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
        view.tableContainer().clear();

        if(event.getCompanyId() == null) {
            fireEvent(new AppEvents.InitPanelName(lang.companyNew()));
            initialView(new Company());
        }else {
            fireEvent(new AppEvents.InitPanelName(lang.companyEdit()));
            fireEvent( new ContactEvents.ShowTable( view.tableContainer(), event.getCompanyId() ) );
            requestCompany(event.getCompanyId());
        }
    }

    @Override
    public void onSaveClicked() {
        if(!validateFieldsAndGetResult()) {
            return;
        }

        fillCompany(tempCompany);

        companyService.saveCompany(tempCompany, view.companyGroup().getValue(), new RequestCallback<Boolean>() {
            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onSuccess(Boolean aBoolean) {
                fireEvent(new CompanyEvents.Show());
                fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                fireEvent(new CompanyEvents.ChangeModel());
            }
        });
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    @Override
    public void onChangeCompanyName() {
        String value = view.companyName().getText().trim();

        //isCompanyNameExists не принимает пустые строки!
        if ( value.isEmpty() ){
            view.setCompanyNameStatus(NameStatus.NONE);
            return;
        }
        companyService.isCompanyNameExists(
                value,
                tempCompany.getId(),
                new RequestCallback<Boolean>() {
                    @Override
                    public void onError(Throwable throwable) {}

                    @Override
                    public void onSuccess(Boolean isExists) {
                        view.setCompanyNameStatus(isExists ? NameStatus.ERROR : NameStatus.SUCCESS);
                    }
                }
        );
    }

    private boolean validateFieldsAndGetResult(){
        boolean result = true;

        if(!view.companyNameValidator().isValid())
            view.companyNameValidator().setValid(result = false);

        if(!view.actualAddressValidator().isValid())
            view.actualAddressValidator().setValid(result = false);

        if(!view.legalAddressValidator().isValid())
            view.legalAddressValidator().setValid(result = false);

        return result;
    }

    private void resetValidationStatus(){
        view.setCompanyNameStatus(NameStatus.NONE);
        view.companyNameValidator().setValid(true);
        view.actualAddressValidator().setValid(true);
        view.legalAddressValidator().setValid(true);
    }

    private void initialView(Company company){
        tempCompany = company;
        fillView(tempCompany);
        resetValidationStatus();
    }

    private void requestCompany(Long id){
        companyService.getCompanyById(id, new RequestCallback<Company>() {
            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onSuccess(Company company) {
                initialView(company);
            }
        });
    }

    private void fillView(Company company){
        view.companyName().setText(company.getCname());
        view.actualAddress().setText(company.getAddressFact());
        view.legalAddress().setText(company.getAddressDejure());
        view.comment().setText(company.getInfo());
        view.companyCategory().setValue(company.getCategory());
        view.companyGroup().setValue(company.getCompanyGroup());

        ContactInfo contactInfo = company.getContactInfo();
        view.webSite().setText(contactInfo.getWebSite());
    }


    private void fillCompany(Company company){
        company.setCname(view.companyName().getText());
        company.setAddressDejure(view.legalAddress().getText());
        company.setAddressFact(view.actualAddress().getText());
        company.setInfo(view.comment().getText());
        company.setCategory(view.companyCategory().getValue());
        company.setCompanyGroup(view.companyGroup().getValue());

        ContactInfo contactInfo = company.getContactInfo();
        contactInfo.setWebSite(view.webSite().getText());
    }

    @Inject
    AbstractCompanyEditView view;

    @Inject
    Lang lang;

    @Inject
    CompanyServiceAsync companyService;

    private Company tempCompany;

    private AppEvents.InitDetails initDetails;
}
