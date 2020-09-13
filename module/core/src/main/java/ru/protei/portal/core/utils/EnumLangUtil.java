package ru.protei.portal.core.utils;

import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dict.*;

import java.util.Locale;

public class EnumLangUtil {
    private final Lang lang;
    private Lang.LocalizedLang localizedLang;

    public EnumLangUtil(Lang lang) {
        this.lang = lang;
    }

    public String getPersonRoleType(En_DevUnitPersonRoleType type, String langCode) {
        if (localizedLang == null) {
            localizedLang = this.lang.getFor(Locale.forLanguageTag(langCode));
        }

        switch (type) {
            case HEAD_MANAGER:
                return localizedLang.get("personHeadManager");
            case DEPLOY_MANAGER:
                return localizedLang.get("personDeployManager");
            case DECISION_CENTER:
                return localizedLang.get("personDecisionCenter");
            case CHIEF_DECISION_MAKER:
                return localizedLang.get("personChiefDecisionMaker");
            case KEEPER:
                return localizedLang.get("personDecisionKeeper");
            case TECH_SPECIALIST:
                return localizedLang.get("personTechSpecialist");
            case INFLUENCE_MAKER:
                return localizedLang.get("personInfluenceMaker");
            case CHIEF_INFLUENCE_MAKER:
                return localizedLang.get("personChiefInfluenceMaker");
            case ECONOMIST:
                return localizedLang.get("personEconomist");
            case WELL_WISHER:
                return localizedLang.get("personWellWisher");
            case RECEPTIVITY_CENTER:
                return localizedLang.get("personReceptivityCenter");
            case HARDWARE_CURATOR:
                return localizedLang.get("personHardwareCurator");
            case SOFTWARE_CURATOR:
                return localizedLang.get("personSoftwareCurator");
            case INTRO_NEW_TECH_SOLUTIONS:
                return localizedLang.get("personIntroNewTechSolutions");
            case LIABLE_FOR_AUTO_TESTING:
                return localizedLang.get("personLiableForAutoTesting");
            case TECH_SUPPORT_CURATOR:
                return localizedLang.get("personTechSupportCurator");
            case PRODUCT_ASSEMBLER:
                return localizedLang.get("personProductAssembler");
            case SUPPLY_PREPARATION:
                return localizedLang.get("personSupplyPreparation");
            case ENGINEER_DOC_DEV:
                return localizedLang.get("personEngineerDocDev");
            case TECH_DOC_DEV:
                return localizedLang.get("personTechDocDev");
            case SOFTWARE_DOC_DEV:
                return localizedLang.get("personSoftwareDocDev");
            case LIABLE_FOR_CERTIFICATION:
                return localizedLang.get("personLiableForCertification");
            case OKR_ESCORT:
                return localizedLang.get("personOkrEscort");
            case QUALITY_CONTROL_SMK:
                return localizedLang.get("personQualityControlSmk");
            case CUSTOMER_INTEGRATION:
                return localizedLang.get("personCustomerIntegration");
        }

        return localizedLang.get("personRoleUnknown");
    }

    public String getRegionState(En_RegionState state, String langCode) {
        if (localizedLang == null) {
            localizedLang = this.lang.getFor(Locale.forLanguageTag(langCode));
        }

        switch (state) {
            case UNKNOWN:
                return localizedLang.get("regionStateUnknown");
            case MARKETING:
                return localizedLang.get("regionStateMarketing");
            case PRESALE:
                return localizedLang.get("regionStatePresale");
            case PROJECTING:
                return localizedLang.get("regionStateProjecting");
            case DEVELOPMENT:
                return localizedLang.get("regionStateDevelopment");
            case DEPLOYMENT:
                return localizedLang.get("regionStateDeployment");
            case SUPPORT:
                return localizedLang.get("regionStateSupport");
            case FINISHED:
                return localizedLang.get("regionStateFinished");
            case TESTING:
                return localizedLang.get("regionStateTesting");
            case CANCELED:
                return localizedLang.get("regionStateCanceled");
            case PAUSED:
                return localizedLang.get("regionStatePaused");
        }

        return localizedLang.get("regionStateUnknown");
    }

    public String getCustomerType(En_CustomerType type, String langCode) {
        if (localizedLang == null) {
            localizedLang = this.lang.getFor(Locale.forLanguageTag(langCode));
        }

        switch (type) {
            case MINISTRY_OF_DEFENCE:
                return localizedLang.get("customerTypeMinistryOfDefence");
            case STATE_BUDGET:
                return localizedLang.get("customerTypeStageBudget");
            case COMMERCIAL_RF:
                return localizedLang.get("customerTypeCommercialRf");
            case COMMERCIAL_NEAR_ABROAD:
                return localizedLang.get("customerTypeCommercialNearAbroad");
            case COMMERCIAL_FAR_ABROAD:
                return localizedLang.get("customerTypeCommercialFarAbroad");
            case COMMERCIAL_PROTEI:
                return localizedLang.get("customerTypeCommercialProtei");

        }

        return localizedLang.get("customerTypeUnknown");
    }

    public String contractTypeLang(En_ContractType contractType, String langCode) {
        if (contractType == null) {
            return "";
        }
        if (localizedLang == null) {
            localizedLang = this.lang.getFor(Locale.forLanguageTag(langCode));
        }
        switch (contractType) {
            case AFTER_SALES_SERVICE_CONTRACT: return localizedLang.get("contractTypeAfterSalesServiceContract");
            case EXPORT_OF_SERVICE_CONTRACT: return localizedLang.get("contractTypeExportOfServiceContract");
            case GOVERNMENT_CONTRACT: return localizedLang.get("contractTypeGovernmentContract");
            case LEASE_CONTRACT: return localizedLang.get("contractTypeLeaseContract");
            case LICENSE_CONTRACT: return localizedLang.get("contractTypeLicenseContract");
            case LICENSE_FRAMEWORK_CONTRACT: return localizedLang.get("contractTypeLicenseFrameworkContract");
            case MUNICIPAL_CONTRACT: return localizedLang.get("contractTypeMunicipalContract");
            case ORDER: return localizedLang.get("contractTypeOrder");
            case PURCHASE_CONTRACT: return localizedLang.get("contractTypePurchaseContract");
            case SUBCONTRACT: return localizedLang.get("contractTypeSubcontract");
            case SUPPLY_AND_WORK_CONTRACT: return localizedLang.get("contractTypeSupplyAndWorkContract");
            case SUPPLY_AND_WORK_FRAMEWORK_CONTRACT: return localizedLang.get("contractTypeSupplyAndWorkFrameworkContract");
            case SUPPLY_CONTRACT: return localizedLang.get("contractTypeSupplyContract");
            case SUPPLY_FRAMEWORK_CONTRACT: return localizedLang.get("contractTypeSupplyFrameworkContract");
            case WORK_CONTRACT: return localizedLang.get("contractTypeWorkContract");
            case HARDWARE_SOFTWARE_SERVICE: return localizedLang.get("contractTypeHardwareSoftwareService");
        }
        return "";
    }

    public String contractStateLang(En_ContractState contractState, String langCode) {
        if (contractState == null) {
            return "";
        }
        if (localizedLang == null) {
            localizedLang = this.lang.getFor(Locale.forLanguageTag(langCode));
        }
        switch (contractState) {
            case AGREEMENT: return localizedLang.get("contractStateAgreement");
            case COPIES_SEND_TO_CUSTOMER: return localizedLang.get("contractStateCopiesSendToCustomer");
            case HAVE_ORIGINAL: return localizedLang.get("contractStateHaveOriginal");
            case WAIT_ORIGINAL: return localizedLang.get("contractStateWaitOriginal");
            case WAITING_COPIES_FROM_CUSTOMER: return localizedLang.get("contractWaitingCopiesFromCustomer");
            case CANCELLED: return localizedLang.get("contractCancelled");
        }
        return "";
    }

    public String contractDatesTypeLang(En_ContractDatesType contractDatesType, String langCode) {
        if (contractDatesType == null) {
            return "";
        }
        if (localizedLang == null) {
            localizedLang = this.lang.getFor(Locale.forLanguageTag(langCode));
        }
        switch (contractDatesType) {
            case PAYMENT: return localizedLang.get("contractPayment");
            case SUPPLY: return localizedLang.get("contractSupply");
        }
        return "";
    }
}
