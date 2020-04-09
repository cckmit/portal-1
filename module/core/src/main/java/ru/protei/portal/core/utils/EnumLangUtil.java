package ru.protei.portal.core.utils;

import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.core.model.dict.En_RegionState;

import java.util.Locale;

public class EnumLangUtil {
    private final Lang lang;
    private Lang.LocalizedLang localizedLang;

    public EnumLangUtil(Lang localizedLang) {
        this.lang = localizedLang;
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
                return localizedLang.get("personChielInfluenceMaker");
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
        return null;
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
        }

        return null;
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

        return null;
    }
}
