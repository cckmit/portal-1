package ru.protei.portal.ui.common.client.lang;

import com.google.gwt.i18n.client.Messages;
import ru.protei.portal.core.model.dict.lang.ContactItemLang;

/**
 * Интерфейс со строковыми константами
 */
public interface Lang extends Messages, ContactItemLang {
    String buttonLogout();

    String buttonLogin();

    String companies();

    String name();

    String created();

    String updated();

    String sortBy();

    String products();

    String productNew();

    String productShowDeprecated();

    String buttonSave();

    String buttonApply();

    String buttonCancel();

    String buttonCreate();

    String errNoMatchesFound();

    String errGetList();

    String errGetItem();

    String msgHello();

    String unknownField();

    String companyGroup();

    String search();

    String description();

    String productToArchive();

    String productName();

    String msgObjectSaved();

    String productFromArchive();

    String errEmptyName();

    String error();

    String companyName();

    String companyActualAddress();

    String companyLegalAddress();

    String companyWebSite();

    String phone();

    String comment();

    String contacts();

    String companyNew();

    String errAsteriskRequired();

    String errLoginOrPwd();

    String msgOK();

    String errFieldsRequired();

    String errConnectionError();

    String errInvalidSessionID();

    String errGetDataError();

    String errNotFound();

    String errNotCreated();

    String errNotUpdated();

    String errNotSaved();

    String errUndefinedObject();

    String errAlreadyExist();

    String errValidationError();

    String errIncorrectParams();

    String errInternalError();

    String errDatabaseError();

    String errDatabaseTempError();

    String errUnknownResult();

    String companyInfoHeader();

    String companyCommonHeader();

    String company();

    String contactShowFired();

    String contactFullName();

    String contactPosition();

    String fullScreen();

    String contactLastName();

    String contactFirstName();

    String contactSecondName();

    String contactDepartment();

    String address();

    String productDescription();

    String edit();

    String companyAdditionalInfoHeader();

    String newContact();

    String editContactHeader(String contactName);

    String firstName();

    String lastName();

    String secondName();

    String displayName();

    String displayShortName();

    String birthday();

    String gender();

    String genderMale();

    String genderFemale();

    String genderUndefined();

    String personalData();

    String contactInfo();

    String advPersonalData();

    String workPhone();

    String workEmail();

    String personalEmail();

    String primaryFax();

    String secondaryFax();

    String contactFax();

    String contactMobilePhone();

    String contactPersonalPhone();

    String contactEmail();

    String contactActualAddress();

    String contactLegalAddress();

    String contactIcq();

    String contactJabber();

    String contactSkype();

    String contactWebSite();

    String contactSocialNet();

    String workAddress();

    String homeAddress();

    String department();

    String errorCompanyRequired();

    String errorFirstNameRequired();

    String errorLastNameRequired();

    String companyCategory();

    String appNavHeader();

    String companyEdit();

    String noCompanyGroup();

    String companyGroupLabel();

    String issues();

    String issueNumber();

    String issueProduct();

    String issueContacts();

    String issueInfo();

    String issueCreationDate();

    String issueManager();

    String issueName();

    String issueState();

    String issueImportance();

    String issueCompany();

    String issueInitiator();

    String issuePrivate();

    String issueHeader(String issueNumber);

    String issueCurrentStateHeader();

    String issueContactHeader();

    String issueInfoHeader();

    String issueCriticality();

    String newIssue();

    String issueEdit();

    String selectIssueState();

    String selectIssueImportance();

    String selectIssueCompany();

    String selectIssueInitiator();

    String selectIssueProduct();

    String selectIssueManager();

    String issueAttachments();

    String createdCaseState();

    String openedCaseState();

    String closedCaseState();

    String pausedCaseState();

    String verifiedCaseState();

    String reopenedCaseState();

    String ignoredCaseState();

    String assignedCaseState();

    String estimatedCaseState();

    String discussCaseState();

    String plannedCaseState();

    String activeCaseState();

    String doneCaseState();

    String testCaseState();

    String testLocalCaseState();

    String testCustCaseState();

    String designCaseState();

    String solvedNoapCaseState();

    String solvedFixCaseState();

    String solvedDupCaseState();

    String workaroundCaseState();

    String requestInfoCaseState();

    String criticalImportance();

    String importantImportance();

    String basicImportance();

    String cosmeticImportance();

    String comments();

    String product();

    String buttonReset();

    String selectContactCompany();

    String selectDate();

    String email();

    String personalPhone();

    String separatorText(int page, int pageCount);

    String dataPageNumber(int page);

    String pagerLabel(int currentPage, int totalPages, long totalCount);

    String commentSendMessage();

    String commentAddMessagePlaceholder();

    String errEditIssueCommentNotAllowed();

    String errRemoveIssueComment();

    String errEditIssueComment();

    String selectCompanyGroup();

    String activeRecords();

    String newRecords();

    String inactiveRecords();

    String dashboard();

    String issueCommentChangeStatusTo();

    String issueCommentChangeImportanceTo();

    String errNotRemoved();

    String errEditIssueCommentEmpty();

    String classifier();

    String equipmentName();

    String equipmentOrganizationCodePAMR();

    String equipmentOrganizationCodePDRA();

    String equipmentDecimalNumber();

    String equipmentComment();

    String equipmentNameBySpecification();

    String equipmentNameBySldWrks();

    String equipmentOrganizationProtei();

    String equipmentOrganizationProteiST();

    String equipmentOrganization();

    String equipmentSearchNameOrProject();

    String equipmentProduct();

    String regions();

    String regionStateUnknown();

    String regionStateRival();

    String regionStateTalk();

    String regionStateProjecting();

    String regionStateDevelopment();

    String regionStateDeployment();

    String regionStateSupport();

    String regionStateSupportFinished();

    String regionState();

    String regionDistrict();

    String productDirection();

    String projectDirection();

    String projectInfo();

    String projects();

    String projectManagers();

    String projectHeader(String s);

    String projectCreationDate();

    String projectHeadManager();

    String projectDeployManagers();

    String projectState();

    String projectRegion();

    String equipmentAttachment();

    String equipmentDescription();

    String equipmentTypeAssemblyUnit();

    String equipmentTypeComplex();

    String equipmentTypeDetail();

    String equipmentTypeProduct();

    String equipmentType();

    String equipmentStageDraftProject();

    String equipmentStageRKD_Product();

    String equipmentStageRKD_Prototype();

    String equipmentStageTechnicalProject();

    String equipmentStage();

    String equipmentPrimaryUse();

    String equipmentPrimaryUseNotDefinied();

    String equipmentDecimalNumberEmpty();

    String equipmentDecimalNumberBusy();

    String buttonImport();

    String equipmentClassifierCode();

    String equipmentRegisterNumber();

    String equipmentCommonHeader();

    String equipmentReserve();

    String buttonAdd();

    String equipmentRemove();

    String equipmentDecimalNumberNotDefinied();

    String equipmentRegisterNumberModification();

    String equipmentErrorGetNextAvailableNumber();

    String equipmentErrorCheckNumber();

    String equipmentNextAvailableNumber();

    String equipmentOrChange();

    String equipmentModification();

    String equipmentManager();

    String equipmentProject();

    String selectorAny();

    String buttonYes();

    String buttonNo();

    String buttonCopy();

    String buttonRemove();

    String equipmentRemoveConfirmMessage();

    String equipmentRemoveSuccessed();

    String equipmentNewName();

    String equipmentCopyNotFilledNewName();

    String equpmentCopySuccess();

    String equipmentCopyHeader();

    String equipmentCopyMessage();

    String copyPostfix();

    String attachment();

    String addAttachmentBtn();

    String uploadFileError();

    String uploadFileSuccess();

    String removeFileError();

    String attachmentsNotLoaded();

    String attachmentRemoveConfirmMessage();

    String attachmentAuthor();

    String accountPassword();

    String accountPasswordNotDefinied();

    String errEditContactLogin();

    String accountLoginInfo();

    String accountConfirmPassword();

    String accountPasswordsNotMatch();

    String companySubscription();

    String accounts();

    String accountType();

    String accountLDAP();

    String accountLocal();

    String accountLogin();

    String accountPerson();

    String accountAuthType();

    String accountNew();

    String accountRoles();

    String accountCompany();

    String selectAccountCompany();

    String selectAccountPerson();

    String accountRoleNotDefinied();

    String accountRemoveConfirmMessage();

    String remove();

    String accountRemoveSuccessed();

    String account();

    String accountLastName();

    String accountFirstName();

    String accountSecondName();

    String roleName();

    String roleDescription();

    String roles();

    String role(Long id);

    String privilegeCategoryCommon();

    String privilegeCategoryIssue();

    String privilegeCategoryRegion();

    String privilegeCategoryProject();

    String privilegeCategoryCompany();

    String privilegeCategoryProduct();

    String privilegeCategoryContact();

    String privilegeCategoryAccount();

    String privilegeCategoryEquipment();

    String privilegeLogin();

    String privilegeView();

    String privilegeEdit();

    String privilegeCreate();

    String privilegeReport();

    String privilegeExport();

    String rolePrivileges();

    String errPermissionDenied();

    String errSessionNotFound();

    String privilegeCategoryRole();

    String privilegeRemove();

    String buttonLocale();

    String privilegeCategoryOfficial();

    String matrixSolutions();

    String officialTableProduct();

    String officialTableInfo();

    String officialTableNumberEmployees();

    String selectOfficialRegion();

    String officialRegion();

    String officialGeneralInfo();

    String officialFaces();

    String officialPreviewSearch();

    String officialOrganization();

    String officialRelations();

    String officialAmplua();

    String selectOfficialCompany();

    String errOfficialRemove();

    String errOfficialMemberRemove();

    String officialComment();

    String selectOfficialAmplua();

    String personHeadManager();

    String personDeployManager();

    String officialInRelationsWith();

    String selectOfficialProduct();

    String personDecisionCenter();

    String personChiefDecisionMaker();

    String personDecisionKeeper();

    String personTechSpecialist();

    String personInfluenceMaker();

    String personChielInfluenceMaker();

    String personEconomist();

    String personWellWisher();

    String personReceptivityCenter();

    String equipmentGetNextModification();

    String formNextMod();

    String buttonAddPdra();

    String buttonAddPamr();

    String equipmentManagerNotDefined();

    String equipmentNumberAlreadyInList();

    String privilegeDashboard();

    String companySubscriptionUpdatedSuccessful();

    String scopeSystem();

    String scopeCompany();

    String scopeRole();

    String roleScope();

    String messageServerConnectionLost();

    String roleRemoveSuccessed();

    String roleAdd();

    String view();

    String list();

    String table();

    String issueNameHeader();

    String noChanges();

    String roleRemoveConfirmMessage();

    String issueCompanySubscriptionNotDefined();

    String issueCompanySubscriptionNeedSelectCompany();

    String caseSubscription();

    String errCreatedStateSelected();

    String document();

    String annotation();

    String designation();

    String privilegeDocument();

    String documentCommonHeader();

    String documentName();

    String documentType();

    String documentSearchNameOrProject();

    String documentKeywords();

    String documentContent();

    String addKeyword();

    String keywordInputPlaceholder();

    String inventoryNumber();

    String customer();

    String documentTypeIsEmpty();

    String documentProjectIsEmpty();

    String inventoryNumberIsEmpty();

    String negativeInventoryNumber();

    String customerNotSet();

    String decimalNumberNotSet();

    String documentNameIsNotSet();

    String documentTypeNotDefined();

    String errGetObject();

    String uploadDocuments();

    String documentDescription();

    String documentProject();

    String documentManager();

    String documentDecimalNumber();

    String documentIdentification();

    String documentUploadPlaceholder();

    String uploadingDocumentNotSet();

    String errSaveDocumentFile();

    String errSaveIssueFilter();

    String issueFilterName();

    String issueFilter();

    String errDocumentNotSaved();

    String errFilterNameRequired();

    String issueFilterRemoveSuccessed();

    String errTooMuchCompanies();

    String errTooMuchManagers();

    String errTooMuchProducts();

    String issueFilterNotDefined();

    String buttonModify();

    String documentCategory();

    String tpDocumentCategory();

    String kdDocumentCategory();

    String pdDocumentCategory();

    String edDocumentCategory();

    String tdDocumentCategory();

    String documentTypes();

    String documentTypeName();

    String documentTypeShortName();

    String documentTypeGost();

    String documentTypeRemoveSuccessed();

    String documentTypeRemoveConfirmMessage();

    String documentNameNew();

    String privilegeDocumentType();

    String productWithout();

    String personCreateNew();

    String roleDefaultForContact();

    String mobilePhone();

    String download();

    String accountFilterCompany();
    
    String documentTypeCode();

    String errNotAvailable();
    
    String decimalNumberNotFound();

    String from();

    String to();

    String asc();

    String desc();

    String selected();

    String buttonReport();

    String buttonDownload();

    String issueReports();

    String issueReportsNumber();

    String issueReportsInfo();

    String issueReportsFilter();

    String issueReportsNotDeleted();

    String issueReportsDeleted();

    String issueReportsCreated();

    String issueReportsProcess();

    String issueReportsReady();

    String issueReportsError();

    String issueReportsTitle();

    String issueReportsNew();

    String issueReportsView();

    String reportRequested();

    String errProductNotSelected();

    String contactFired();

    String contactDeleted();

    String contactFiredShort();

    String contactDeletedShort();

    String contactFire();

    String contactFireConfirmMessage();

    String contactRemoveConfirmMessage();

    String decimalNumberFound();

    String searchByComments();

    String searchByCommentsUnavailable(int threshold);

    String caseStates();

    String privilegeCaseStates();

    String caseStatesPreviewHeader();

    String caseStateUsagesInCompaniesNone();

    String caseStateUsagesInCompaniesAll();

    String caseStateUsagesInCompaniesSelected();

    String caseStatesPreviewCompanies();

    String caseStatesColumnUsageInCompanies();

    String caseStatesColumnInfo();

    String caseStatesColumnName();

    String caseLinkCrm();

    String caseLinkCrmShort();

    String caseLinkOldCrm();

    String caseLinkOldCrmShort();

    String caseLinkYouTrack();

    String caseLinkYouTrackShort();

    String linkedWith();

    String id();

    String withoutContact();

    String filters();

    String sendEmail();

    String locale();

    String privilegeSiteFolder();
}
