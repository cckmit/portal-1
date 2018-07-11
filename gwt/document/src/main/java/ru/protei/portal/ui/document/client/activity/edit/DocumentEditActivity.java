package ru.protei.portal.ui.document.client.activity.edit;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.common.ConsumeTimer;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.DocumentEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DocumentControllerAsync;
import ru.protei.portal.ui.common.client.service.EquipmentControllerAsync;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.document.client.widget.uploader.UploadHandler;


public abstract class DocumentEditActivity
        implements Activity, AbstractDocumentEditActivity {

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        this.authorizedProfile = event.profile;
    }

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        view.decimalNumber().addValueChangeHandler(valueChangeEvent -> onDecimalNumberChanged(valueChangeEvent.getValue()));
        view.documentUploader().setUploadHandler(new UploadHandler() {
            @Override
            public void onError() {
                fireErrorMessage(lang.errSaveDocumentFile());
            }

            @Override
            public void onSuccess() {
                saveUploadedDocument();
            }
        });
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow(DocumentEvents.Edit event) {
        if (event.id == null) {
            fillView(new Document());
            return;
        }

        documentService.getDocument(event.id, new RequestCallback<Document>() {
            @Override
            public void onError(Throwable throwable) {
                fireErrorMessage(lang.errGetObject());
            }

            @Override
            public void onSuccess(Document result) {
                fillView(result);
            }
        });
    }

    private void fireErrorMessage(String msg) {
        fireEvent(new NotifyEvents.Show(msg, NotifyEvents.NotifyType.ERROR));
    }

    @Override
    public void onSaveClicked() {
        Document newDocument = getDocument();
        DecimalNumber newDecimalNumber = newDocument.getDecimalNumber();
        boolean decimalNumberEmpty = newDecimalNumber == null || newDecimalNumber.isCompletelyEmpty();

        if (newDocument.getId() == null && HelperFunc.isEmpty(view.documentUploader().getFilename())) {
            fireErrorMessage(lang.uploadingDocumentNotSet());
            return;
        }
        if (!newDocument.isValid()) {
            fireErrorMessage(getValidationErrorMessage(newDocument));
            return;
        } else if (!decimalNumberEmpty && !newDecimalNumber.isValid()) {
            view.decimalNumberValidator().setValid(false);
            return;
        }

        boolean decimalNumberWasSet = newDecimalNumber != null && newDecimalNumber.getId() != null;
        if (decimalNumberWasSet && decimalNumberEmpty) {
            fireErrorMessage(lang.decimalNumberNotSet());
            return;
        }

        saveDocument(newDocument);
    }

    private void saveDocument(Document document) {
        this.document = document;
        if (document.getId() == null)
            view.documentUploader().uploadBindToDocument(document);
        else
            saveUploadedDocument();
    }

    private void saveUploadedDocument() {
        documentService.saveDocument(this.document, new RequestCallback<Document>() {
            @Override
            public void onError(Throwable throwable) {
                fireErrorMessage(lang.errDocumentNotSaved());
            }

            @Override
            public void onSuccess(Document result) {
                fireEvent(new DocumentEvents.ChangeModel());
                fireEvent(new Back());
            }
        });
    }

    private void onDecimalNumberChanged(DecimalNumber number) {
        view.setSaveEnabled(false);
        view.decimalNumberValidator().setValid(true);

        if (number == null || number.isEmpty()) {
            document.setDecimalNumber(number);
            view.setSaveEnabled(true);
            view.decimalNumberValidator().setValid(true);
            return;
        }
        timer.cancel();
        timer.setObject(number);
        timer.schedule(300);
    }

    private void findDecimalNumber(DecimalNumber number) {
        equipmentService.findDecimalNumber(number, new AsyncCallback<DecimalNumber>() {
            @Override
            public void onFailure(Throwable caught) {
                view.setDecimalNumberExists(false);
            }

            @Override
            public void onSuccess(DecimalNumber decimalNumber) {
                if (decimalNumber == null) {
                    onFailure(null);
                    return;
                }
                document.setDecimalNumber(decimalNumber);
                view.setSaveEnabled(true);
                view.decimalNumberValidator().setValid(true);
                view.setDecimalNumberExists(true);
            }
        });
    }

    private String getValidationErrorMessage(Document doc) {
        if (doc.getType() == null) {
            return lang.documentTypeIsEmpty();
        }
        if (doc.getProjectId() == null) {
            return lang.documentProjectIsEmpty();
        }
        if (doc.getInventoryNumber() != null && doc.getInventoryNumber() < 0) {
            return lang.negativeInventoryNumber();
        }
        if (HelperFunc.isEmpty(doc.getName())) {
            return lang.documentNameIsNotSet();
        }
        return null;
    }


    private Document getDocument() {
        Document d = new Document();
        d.setId(document.getId());
        d.setName(view.name().getValue());
        d.setAnnotation(view.annotation().getValue());
        d.setDecimalNumber(view.decimalNumber().getValue());
        d.setType(view.documentType().getValue());
        d.setInventoryNumber(view.inventoryNumber().getValue());
        d.setKeywords(view.keywords().getValue());
        d.setContractor(Person.fromPersonShortView(view.contractor().getValue()));
        d.setRegistrar(Person.fromPersonShortView(view.registrar().getValue()));
        d.setVersion(view.version().getValue());
        d.setProjectId(view.project().getValue().getId());
        d.setEquipment(view.equipment().getValue() == null ? null : new Equipment(view.equipment().getValue().getId()));
        return d;
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    private void fillView(Document document) {
        this.document = document;

        boolean isNew = document.getId() == null;

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        view.name().setValue(document.getName());
        view.annotation().setValue(document.getAnnotation());
        view.created().setValue(DateFormatter.formatDateTime(document.getCreated()));
        view.decimalNumber().setValue(document.getDecimalNumber());
        view.documentCategory().setValue(document.getType() == null ? null : document.getType().getDocumentCategory(), true);
        view.documentType().setValue(document.getType(), true);
        view.inventoryNumber().setValue(document.getInventoryNumber());
        view.keywords().setValue(document.getKeywords());
        view.project().setValue(document.getProjectInfo());
        view.version().setValue(document.getVersion());
        view.equipment().setValue(EquipmentShortView.fromEquipment(document.getEquipment()));

        if (isNew) {
            PersonShortView currentPerson = new PersonShortView(authorizedProfile.getShortName(), authorizedProfile.getId(), authorizedProfile.isFired());
            view.registrar().setValue(currentPerson);
            view.contractor().setValue(currentPerson);
        } else {
            view.registrar().setValue(document.getRegistrar() == null ? null : document.getRegistrar().toShortNameShortView());
            view.contractor().setValue(document.getContractor() == null ? null : document.getContractor().toShortNameShortView());
        }

        view.setEnabledProject(document.getId() == null);
        view.setVisibleUploader(document.getId() == null);

        view.nameValidator().setValid(true);
        view.decimalNumberValidator().setValid(true);

        view.resetFilename();
        view.documentUploader().resetAction();
        view.setSaveEnabled(true);

        decimalNumberIsSet = StringUtils.isEmpty(document.getDecimalNumberStr());
    }

    private final ConsumeTimer<DecimalNumber> timer = new ConsumeTimer<DecimalNumber>() {
        @Override
        public void accept(DecimalNumber decimalNumber) {
            findDecimalNumber(decimalNumber);
        }
    };

    @Inject
    AbstractDocumentEditView view;

    @Inject
    Lang lang;

    private Document document;

    boolean decimalNumberIsSet = false;

    @Inject
    DocumentControllerAsync documentService;

    @Inject
    EquipmentControllerAsync equipmentService;

    private AppEvents.InitDetails initDetails;
    private Profile authorizedProfile;
}
