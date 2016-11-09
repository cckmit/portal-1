package ru.protei.portal.ui.company.client.activity.edit;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.view.ValueComment;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.CompanyEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ValueCommentEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.NameStatus;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.common.client.service.CompanyServiceAsync;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


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

        if(event.getCompanyId() == null) {
            this.fireEvent(new AppEvents.InitPanelName(lang.companyNew()));
            tempCompany = createCompany();
            fillView(tempCompany);
            resetValidationStatus();
        }else {
            this.fireEvent(new AppEvents.InitPanelName(lang.companyEdit()));
            companyService.getCompanyById(event.getCompanyId(), new RequestCallback<Company>() {
                @Override
                public void onError(Throwable throwable) {}

                @Override
                public void onSuccess(Company company) {
                    tempCompany = company;
                    fillView(tempCompany);
                    resetValidationStatus();
                }
            });

        }
    }


    private <T> List<T> removeNullableValues(List<T> dataList){
        dataList.removeIf(Objects::isNull); //remove all null values
        return dataList;

    }

    private void setValueCommentList(HasWidgets parent, List<ValueComment> dataList){
        fireEvent(
                new ValueCommentEvents.ShowList(
                        parent,
                        removeNullableValues(dataList)
                )
        );
    }

    private void fillView(Company company){
        if(company.getId() != null){
            view.companyName().setText(company.getCname());
            view.setCompanyNameStatus(NameStatus.SUCCESS);
        }

        if(!company.getGroups().isEmpty())
            view.companyGroup().setValue(company.getGroups().get(0));
        else
            view.companyGroup().setValue(null);

        view.actualAddress().setText(company.getAddressFact());
        view.legalAddress().setText(company.getAddressDejure());

        view.webSite().setText(company.getWebsite());
        view.comment().setText(company.getInfo());

//        setValueCommentList(view.phonesContainer(), company.getPhone());
//        setValueCommentList(view.emailsContainer(), company.getEmail());
    }


    private void fillCompany(Company company){
        company.setCname(view.companyName().getText());
        company.setInfo(view.comment().getText());
        company.setWebsite(view.webSite().getText());
        company.setAddressDejure(view.legalAddress().getText());
        company.setAddressFact(view.actualAddress().getText());

        if(view.companyGroup() != null){
            company.getGroups().add(view.companyGroup().getValue());
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
            public void onError(Throwable throwable) {
                //fireEvent(new NotifyEvents.Show(lang.errNotSaved(), NotifyEvents.NotifyType.ERROR));
            }

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
                    public void onError(Throwable throwable) {
                        //fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                    }

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

    private Company createCompany(){
        Company company = new Company();
//        company.setPhone(new ArrayList<ValueComment>());
//        company.setEmail(new ArrayList<ValueComment>());
        return company;
    }

    private void resetValidationStatus(){
        view.setCompanyNameStatus(NameStatus.NONE);
        view.companyNameValidator().setValid(true);
        view.actualAddressValidator().setValid(true);
        view.legalAddressValidator().setValid(true);
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
