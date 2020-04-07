package ru.protei.portal.core.utils;

import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;

import java.util.Locale;

public class RoleTypeLangUtil {
    private final Lang lang;
    private Lang.LocalizedLang localizedLang;

    public RoleTypeLangUtil(Lang localizedLang) {
        this.lang = localizedLang;
    }

    public String getName(En_DevUnitPersonRoleType type, String langCode) {
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
}
