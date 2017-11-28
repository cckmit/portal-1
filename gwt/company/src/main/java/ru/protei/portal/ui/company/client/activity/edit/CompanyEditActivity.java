package ru.protei.portal.ui.company.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CompanyServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Активность создания и редактирования компании
 */
public abstract class CompanyEditActivity implements AbstractCompanyEditActivity, Activity {

    public CompanyEditActivity(){
        ALLOWED_PHONE_TYPES = new ArrayList<>(3);
        ALLOWED_PHONE_TYPES.add(En_ContactItemType.MOBILE_PHONE);
        ALLOWED_PHONE_TYPES.add(En_ContactItemType.GENERAL_PHONE);
        ALLOWED_PHONE_TYPES.add(En_ContactItemType.FAX);

        ALLOWED_EMAIL_TYPES = new ArrayList<>(1);
        ALLOWED_EMAIL_TYPES.add(En_ContactItemType.EMAIL);
    }

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

        fillDto(tempCompany);

        companyService.saveCompany(tempCompany, new RequestCallback<Boolean>() {
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
        String value = view.companyName().getValue().trim();

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
        return view.companyNameValidator().isValid() /*&&
                view.actualAddressValidator().isValid() &&
                view.legalAddressValidator().isValid()*/;
    }

    private void resetValidationStatus(){
        view.setCompanyNameStatus(NameStatus.NONE);
    }

    private void initialView(Company company){
        tempCompany = company;
        fillView(tempCompany);
        resetValidationStatus();
    }

    private void requestCompany(Long id){
        companyService.getCompany( id, new RequestCallback<Company>() {
            @Override
            public void onError( Throwable throwable ) {
            }

            @Override
            public void onSuccess( Company company ) {
                initialView( company );
            }
        } );
    }

    private void fillView(Company company){
        PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(company.getContactInfo());
        view.companyName().setValue(company.getCname());
        view.actualAddress().setValue(infoFacade.getFactAddress());
        view.legalAddress().setValue(infoFacade.getLegalAddress());

        view.comment().setText(company.getInfo());
        view.companyCategory().setValue(EntityOption.fromCompanyCategory(company.getCategory()));
        view.companyGroup().setValue(EntityOption.fromCompanyGroup(company.getCompanyGroup()));
        view.companySubscriptions().setValue(company.getSubscriptions());

        view.webSite().setText(infoFacade.getWebSite());

        fireEvent(new ContactItemEvents.ShowList(view.phonesContainer(), company.getContactInfo().getItems(), ALLOWED_PHONE_TYPES));
        fireEvent(new ContactItemEvents.ShowList(view.emailsContainer(), company.getContactInfo().getItems(), ALLOWED_EMAIL_TYPES));
    }

    private void fillDto(Company company){
        company.setCname(view.companyName().getValue());

        PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(company.getContactInfo());

        infoFacade.setLegalAddress(view.legalAddress().getValue());
        infoFacade.setFactAddress(view.actualAddress().getValue());
        company.setInfo(view.comment().getText());
        company.setCategory(CompanyCategory.fromEntityOption(view.companyCategory().getValue()));
        if(view.companyGroup().getValue() != null) {
            company.setGroupId(view.companyGroup().getValue().getId());
        }
        else {
            company.setGroupId(null);
        }
        company.setSubscriptions(view.companySubscriptions().getValue());
        infoFacade.setWebSite(view.webSite().getText());
    }

    @Inject
    AbstractCompanyEditView view;

    @Inject
    Lang lang;

    @Inject
    CompanyServiceAsync companyService;

    private Company tempCompany;

    private AppEvents.InitDetails initDetails;

    private final List<En_ContactItemType> ALLOWED_PHONE_TYPES;
    private final List<En_ContactItemType> ALLOWED_EMAIL_TYPES;
}
