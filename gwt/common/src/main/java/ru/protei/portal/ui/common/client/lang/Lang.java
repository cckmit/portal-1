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

    String buttonChangePassword();

    String errNoMatchesFound();

    String errGetList();

    String errGetItem();

    String msgHello();

    String unknownField();

    String companyGroup();

    String search();

    String description();

    String buttonToArchive();

    String productName();

    String msgObjectSaved();

    String msgStatusChanged();

    String buttonFromArchive();

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

    String goToContacts();

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

    String companyParentLabel();

    String companyChildrenLabel();

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

    String custPendingCaseState();

    String canceledCaseState();

    String requestNXCaseState();

    String requestCustomerCaseState();

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

    String errEditTimeElapsedType();

    String selectCompanyGroup();

    String activeRecords();

    String newRecords();

    String inactiveRecords();

    String dashboard();

    String dashboardSelectCompany();

    String issueCommentChangeStatusTo();

    String issueCommentChangeImportanceTo();

    String issueCommentChangeManagerTo();

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

    String regionStateMarketing();

    String regionStatePresale();

    String regionStateProjecting();

    String regionStateDevelopment();

    String regionStateDeployment();

    String regionStateSupport();

    String regionStateFinished();

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

    String buttonNotDefined();

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

    String uploadFileSizeExceed();

    String uploadFileSuccess();

    String removeFileError();

    String attachmentsNotLoaded();

    String attachmentRemoveConfirmMessage();

    String attachmentAuthor();

    String accountPassword();

    String accountCurrentPassword();

    String accountNewPassword();

    String accountPasswordNotDefinied();

    String errEditContactLogin();

    String errEditProfile();

    String accountLoginInfo();

    String accountConfirmPassword();

    String accountPasswordChange();

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

    String issuePlatformHeader();

    String noChanges();

    String roleRemoveConfirmMessage();

    String issueCompanySubscriptionNotDefined();

    String issueCompanySubscriptionBasedOnPrivacyNotDefined();

    String issueCompanySubscriptionNeedSelectCompany();

    String caseSubscription();

    String errSaveIssueNeedSelectManager();

    String document();

    String annotation();

    String designation();

    String privilegeDocument();

    String documentCommonHeader();

    String documentProjectHeader();

    String documentCreateHeader();

    String documentName();

    String documentType();

    String documentSearchNameOrProject();

    String documentKeywords();

    String documentContent();

    String documentChangeStateConfirmMessage();

    String documentShowDeprecated();

    String addKeyword();

    String keywordInputPlaceholder();

    String aliasInputPlaceholder();

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

    String errTooMuchInitiators();

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

    String timeDayLiteral();

    String timeHourLiteral();

    String accountFilterCompany();

    String documentTypeCode();

    String timeMinuteLiteral();

    String timeEstimated();

    String errNotAvailable();

    String decimalNumberNotFound();

    String timeElapsed();

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

    String caseLinkYouTrack();

    String caseLinkYouTrackShort();

    String linkedWith();

    String id();

    String withoutContact();

    String filters();

    String sendEmail();

    String locale();

    String documentContractor();

    String documentRegistrar();

    String documentVersion();

    String documentManagers();

    String documentOrganizationCode();

    String documentEquipment();

    String projectCustomerCompany();

    String projectDescription();

    String projectProducts();

    String projectCustomerType();

    String customerTypeMinistryOfDefence();

    String customerTypeStageBudget();

    String customerTypeCommercialRf();

    String customerTypeCommercialNearAbroad();

    String customerTypeCommercialFarAbroad();

    String customerTypeCommercialProtei();

    String workTime();

    String privilegeSiteFolder();

    String amountShort();

    String siteFolder();

    String siteFolderName();

    String siteFolderCompany();

    String siteFolderParams();

    String siteFolderParamsLocal();

    String siteFolderProject();

    String siteFolderComment();

    String siteFolderIP();

    String siteFolderPlatform();

    String siteFolderPlatforms();

    String siteFolderPlatformNew();

    String siteFolderPlatformEdit();

    String siteFolderPlatformCreate();

    String siteFolderPlatformConfirmRemove();

    String siteFolderPlatformRemoved();

    String siteFolderPlatformNotRemoved();

    String siteFolderPlatformNotSaved();

    String siteFolderPlatformSaved();

    String siteFolderPlatformName();

    String siteFolderServer();

    String siteFolderServers();

    String siteFolderServerNew();

    String siteFolderServerEdit();

    String siteFolderServerCreate();

    String siteFolderServerConfirmRemove();

    String siteFolderServerRemoved();

    String siteFolderServerNotRemoved();

    String siteFolderServerNotSaved();

    String siteFolderServerName();

    String siteFolderApp();

    String siteFolderApps();

    String siteFolderAppNew();

    String siteFolderAppEdit();

    String siteFolderAppCreate();

    String siteFolderAppConfirmRemove();

    String siteFolderAppRemoved();

    String siteFolderAppNotRemoved();

    String siteFolderAppNotSaved();

    String siteFolderAppName();

    String siteFolderPath();

    String siteFolderPaths();

    String openTableView();

    String buttonClear();

    String regionStateTesting();

    String regionStateCanceled();

    String issueLinkIncorrectCrmNumberFormat();

    String issueLinkIncorrectCrmCaseNotFound(Long num);

    String documentCreated(String dateTime);

    String documentIdColumnHeader();

    String devUnitDirection();

    String devUnitProduct();

    String devUnitComponent();

    String productType();

    String searchPrivate();

    String equipmentProjectNotDefined();

    String siteFolderCompanyContacts();

    String selectProduct();

    String componentName();

    String components();

    String errSaveIssueNeedSelectCompany();

    String errSaveIssueFieldsInvalid();

    String personHeadManager();

    String personHardwareCurator();

    String personSoftwareCurator();

    String personIntroNewTechSolutions();

    String personLiableForAutoTesting();

    String personTechSupportCurator();

    String personProductAssembler();

    String personSupplyPreparation();

    String personEngineerDocDev();

    String personTechDocDev();

    String personSoftwareDocDev();

    String personLiableForCertification();

    String personOkrEscort();

    String personQualityControlSmk();

    String personCustomerIntegration();

    String projectTeam();

    String projectSelectRole();

    String membersCount();

    String version();

    String siteFolderClone();

    String issueFilterRemoveConfirmMessage();

    String accessory();

    String projectOnlyMine();

    String selectComponent();

    String belongsTo();

    String siteFolderManager();

    String selectManager();

    String selectPlatform();

    String employees();

    String privilegeCategoryEmployee();

    String employeeAdditionalInfoHeader();

    String employeeID();

    String employeeIPAddress();

    String employeePositionsHeader();

    String employeeCompany();

    String employeeMainPosition();

    String employeeFired();

    String employeeShowFired();

    String initiatorSelectACompany();
    String serverAccessParamsColumn();

    String contactPhone();

    String equipmentDecimalNumberNotCorrect();

    String equipmentDecimalNumbeOccupied();

    String documentApproveType();

    String documentApproved();

    String documentNotApproved();

    String employeeRegistrations();

    String employeeRegistrationEmployeeFullNameColumnHeader();

    String employeeRegistrationHeadOfDepartmentColumnHeader();

    String employeeRegistrationEmploymentDateColumnHeader();

    String privilegeCategoryEmployeeRegistration();

    String employmentTypeFullTime();

    String employmentTypePartTime();

    String employmentTypeRemote();

    String employmentTypeContract();

    String employeeEquipmentTable();

    String employeeEquipmentChair();

    String employeeEquipmentComputer();

    String employeeEquipmentMonitor();

    String internalResourceYoutrack();

    String internalResourceCvs();

    String internalResourceStoreDelivery();

    String internalResourceSvn();

    String internalResourceMercurial();

    String internalResourceGit();

    String internalResourceCrm();

    String employeeRegistrationCommonHeader();

    String employeeRegistrationEmployeeFullName();

    String employeeRegistrationHeadOfDepartment();

    String employeeRegistrationEmploymentDate();

    String employeeRegistrationEmploymentType();

    String employeeRegistrationWithRegistration();

    String employeeRegistrationPost();

    String employeeRegistrationWorkplace();

    String employeeRegistrationComment();

    String employeeRegistrationEquipmentList();

    String employeeRegistrationResourceList();

    String employeeRegistrationSearchFullNameOrPost();

    String employeeRegistrationState();

    String employeeRegistrationCreated();

    String employeeRegistrationEquipmentListPreview();

    String employeeRegistrationEmployeeWithRegistrationTrue();

    String employeeRegistrationEmployeeWithRegistrationFalse();

    String employeeRegistrationValidationEmployeeFullName();

    String employeeRegistrationValidationPosition();

    String employeeRegistrationValidationEmploymentDate();

    String employeeRegistrationValidationHeadOfDepartment();

    String employeeRegistrationLastYoutrackSynchronization();
    
    String internalResourceEmail();

    String sendOnCtrlEnter();

    String abroadDocumentCategory();

    String errAlreadyExistRelated();

    String errInventoryNumberAlreadyExist();

    String documentExecutionTypeElectronic();

    String documentExecutionTypePaper();

    String documentExecutionTypeTypographic();

    String documentExecutionType();

    String errDecimalNumberAlreadyExist();

    String equipmentDocuments();

    String equipmentDocumentCreate();

    String documentEdit();

    String documentCreate();

    String documentSaving();

    String documentSaved();

    String equipmentDocumentAlreadyExists();

    String reUploadDocuments();

    String projectRequired();

    String decimalNumbersRequired();

    String proteiTitleShort();

    String authDescription();

    String proteiTitleFull();

    String authTitle();

    String companyMainInfoTitle();

    String companyContactInfoTitle();

    String companyContactsHeader();

    String contactMainInfoTitle();

    String contactContactInfoTitle();

    String contactAccountTitle();

    String contactAccountHint();

    String projectCreateBy();

    String accountFor();

    String accountRolesNotFound();

    String projectRemoveConfirmMessage(String name);

    String projectRemoveSucceeded();

    String employeeEquipmentPhone();

    String phoneOfficeTypeLongDistance();

    String phoneOfficeTypeInternational();

    String employeeRegistrationPhoneOfficeTypeList();

    String employeeRegistrationResourceOtherComment();

    String employeeRegistrationOS();

    String employeeRegistrationProbationPeriod();

    String employeeRegistrationWithoutProbationPeriod();

    String employeeRegistrationProbationPeriodPlaceholder();

    String employeeRegistrationOSPlaceholder();

    String employeeRegistrationAdditionalSoft();

    String employeeRegistrationAdditionalSoftPlaceholder();

    String employeeRegistrationInternalResourceOtherCommentPlaceholder();

    String issueReportNew();

    String reportTypeCaseObjects();

    String reportTypeCaseTimeElapsed();

    String issueReportsType();

    String issueCommentAuthor();

    String type();

    String goToIssues();

    String goToPlatforms();

    String employeeWithoutManager();

    String commentPreview();

    String companyParentCompany();

    String productSubscription();

    String productWikiLink();

    String productCDRDescription();

    String productHistoryVersion();

    String productConfiguration();

    String markdownPreview();

    String contractTypeAfterSalesServiceContract();

    String contractTypeExportOfServiceContract();

    String contractTypeGovermentContract();

    String contractTypeLeaseContract();

    String contractTypeLicenseContract();

    String contractTypeLicenseFrameworkContract();

    String contractTypeMunicipalContract();

    String contractTypeOrder();

    String contractTypePurchaseContract();

    String contractTypeSubcontract();

    String contractTypeSupplyAndWorkContract();

    String contractTypeSupplyAndWorkFrameworkContract();

    String contractTypeSupplyContract();

    String contractTypeSupplyFrameworkContract();

    String contractTypeWorkContract();

    String contractStateAgreement();

    String contractStateCopiesSendToCustomer();

    String contractStateHaveOriginal();

    String contractStateWaitOriginal();

    String contractWaitingCopiesFromCustomer();

    String contractDescription();

    String contractContragent();

    String contractManager();

    String contractCost();

    String contractDirection();

    String contractCurator();

    String contractState();

    String contractType();

    String contractNumber();

    String contractCommonHeader();

    String contracts();

    String contractNum(String number);

    String contractWorkGroup();

    String contractCostNotDefined();

    String contractSearchPlaceholder();

    String contractPeriodHeader();

    String contractDeliveryAndPaymentsPeriodHeader();

    String contractDateSigning();

    String contractDateValid();

    String contractPayment();

    String contractSupply();

    String contractPaymentCommentPlaceholder();

    String contractSelectDirection();

    String contractSelectType();

    String contractDateNotDefined();

    String contractValidationEmptyNumber();

    String contractValidationEmptyDescription();

    String contractValidationEmptyType();

    String contractValidationEmptyDirection();

    String contractValidationEmptyState();

    String contractValidationEmptyDateSigning();

    String contractValidationEmptyDateValid();

    String period();

    String reportMissingProduct();

    String reportMissingPeriod();

    String reportMissingState();

    String reportTypeCaseResolutionTime();

    String reportTypeCaseTimeElapsedWorkAutor();

    String reportTypeCaseTimeElapsedPeriod();

    String employeeRegistrationNumberColumn();

    String employeeRegistrationCurators();

    String employeeRegistrationValidationCurators();

    String employeeRegistrationValidationHeadOfDepartmentAsCurator();

    String employeeRegistrationPositionExceed(int num);

    String employeeRegistrationWorkplaceExceed(int num);

    String employeeRegistrationOperatingSystemExceed(int num);

    String employeeRegistrationAdditionalSoftLengthExceed(int num);

    String employeeRegistrationResourceCommentLengthExceed(int num);

    String employeeRegistrationValidationProbationPeriod();

    String timeElapsedTypeNone();

    String timeElapsedTypeWatch();

    String timeElapsedTypeNightWork();

    String timeElapsedType();

    String issueCommentElapsedTimeTypeLabel();

    String issueCommentElapsedTimeTypeChange();

    String selectValue();

    String privilegeContract();

    String contractOrganization();

    String contractParent();

    String contractChild();

    String contractParentWithout();

    String contractDatesNotify();

    String tags();

    String colorHex();

    String tagCreate();

    String tagEdit();

    String tagName();

    String tagColor();

    String tagCompany();

    String commentEmpty();

    String siteFolderAttachments();

    String numberUncompletedCases();

    String errIssueCommentProhibitedPrivate();

    String rememberMe();

    String textMarkdownSupport();

    String textJiraWikiMarkupSupport();

    String tagNotSpecified();

    String employeeRegistrationMainInfoTitle();

    String employeeRegistrationWorkplaceTitle();

    String employeeRegistrationResourceTitle();

    String employeeRegistrationResourceHint();

    String employeeRegistrationWorkplaceHint();

    String createBy(String author, String date);

    String companyIsAPartOfCompany(String name);

    String companyIsAHeadOfCompany(String names);

    String in();

    String roleMainTitle();

    String rolePrivilegesHint();

    String caseStateDescription();

    String documentWorkGroupHeader();

    String tagAuthor();

    String tagInfo();

    String employeeEmployeeFullNameColumnHeader();

    String employeeWorkPhoneColumnHeader();

    String employeeMobilePhoneColumnHeader();

    String employeeIPAddressColumnHeader();

    String employeeEmailColumnHeader();

    String employeeDepartmentColumnHeader();

    String privateComment();

    String commentPreviewIsDisplay();

    String timeElapsedTypeSoftInstall();

    String timeElapsedTypeSoftUpdate();

    String timeElapsedTypeSoftConfig();

    String timeElapsedTypeTesting();

    String timeElapsedTypeConsultation();

    String timeElapsedTypeMeeting();

    String timeElapsedTypeDiscussionOfImprovements();

    String timeElapsedTypeLogAnalysis();

    String timeElapsedTypeSolveProblems();

    String newProject();

    String projectEdit();

    String goToProjects();

    String errInvalidCaseUpdateCaseIsClosed();

    String buttonSettings();

    String commentSetPrivate();

    String employeeRegistrationCuratorsHint();

    String logoutHotKey();

    String documentSearchProject();

    String buttonSearch();

    String buttonClose();

    String projectName();

    String buttonCreateProject();
    String buttonCreateProduct();

    String inputProjectName();
    String selectCustomerType();

    String errFilterParameterRequired();

    String firstHundredRecords();

    String issueCopyNumber();

    String errCopyToClipboard();

    String issueCopiedToClipboard();

    String crmPrefix();

    String errInvalidCurrentPassword();

    String errSaveProjectNeedSelectRegion();

    String errSaveProjectNeedSelectDirection();

    String errSaveProjectNeedSelectCustomerType();

    String errSaveProjectNeedSelectCompany();

    String passwordUpdatedSuccessful();

    String issueLinkIncorrectYouTrackCaseNotFound( String p0 );

    String dropFilesHere();

    String productChangeStateConfirmMessage();

    String complexName();

    String devUnitComplex();

    String complexesAndProducts();

    String componentDescription();

    String complexDescription();

    String jiraIssueType();

    String jiraSeverity();

    String jiraTimeOfReaction();

    String jiraTimeOfDecision();

    String contractProject();

    String contractProjectNotDefined();

    String errSaveIssueNeedSelectPlatform();

    String contractProjectHint();

    String goToProducts();

    String goToContracts();

    String contract();

    String buttonBack();

    String buttonForward();

    String projectSearch();

    String projectCreate();

    String projectSiteFolder();

    String issueInitiatorInfo();

    String errNotAllowedChangeIssueNameOrDescription();

    String timeElapsedInfo();

    String phoneOfficeTypeOffice();

    String productAliases();

    String productSearchNameOrAlias();

    String documentSearchNameOrDesignation();

    String errSavePlatformConnectedIssuesExist();

    String errSaveProjectCannotChangeCompany();

    String sendEmailWarning();

    String errAccessDenied();

    String caseLinkSuccessfulCreated();

    String caseLinkSuccessfulRemoved();

    String issueCopyNumberAndName();

    String documentConfirmRemove();

    String documentRemoved();

    String documentProjectCustomerType();

    String documentProjectProductDirection();

    String documentProjectRegion();

    String documentSectionProject();

    String documentSectionEquipment();

    String documentSectionInfo();

    String fileDropzoneLabel();

    String documentFileDoc();

    String documentFilePdf();

    String backToIssues();

    String editNameAndDescription();

    String openFullScreen();

    String tagAdd();

    String linkAdd();

    String contactFieldLengthExceed(String fieldName, int symbolsCount);

    String errorFieldHasInvalidValue(String fieldName);

    String searchNoMatchesFound();

    String emptySelectorList();
}
