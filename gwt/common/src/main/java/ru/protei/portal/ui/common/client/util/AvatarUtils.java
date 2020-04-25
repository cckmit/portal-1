package ru.protei.portal.ui.common.client.util;


import com.google.gwt.core.client.GWT;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.common.shared.model.Profile;

import java.util.Objects;

public class AvatarUtils {

    public static String getAvatarUrl(Profile profile) {
        return getAvatarUrl(profile.getId(), profile.getCompany().getCategory(), profile.getGender());
    }

    public static String getAvatarUrl(Person person) {
        return getAvatarUrl(person.getId(), person.getCompany().getCategory(), person.getGender());
    }

    public static String getAvatarUrl(Long accountId, En_CompanyCategory category, En_Gender gender) {
        if (En_CompanyCategory.HOME == category) {
            return LOAD_AVATAR_URL + gender.getCode() + "/" + accountId + ".jpg";
        }

        return getAvatarUrlByGender(gender);
    }

    public static String getPhotoUrl(Long accountId) {
        return LOAD_AVATAR_URL + accountId + ".jpg";
    }

    public static String getAvatarUrlByGender(En_Gender gender) {
        if (gender == null) {
            return NOGENDER_AVATAR_URL;
        }

        switch (gender) {
            case FEMALE:
                return FEMALE_AVATAR_URL;
            case MALE:
                return MALE_AVATAR_URL;
            default:
                return NOGENDER_AVATAR_URL;
        }
    }

    public static final String NOGENDER_AVATAR_URL = "./images/user-icon.svg";
    public static final String MALE_AVATAR_URL = "./images/user-icon-m.svg";
    public static final String FEMALE_AVATAR_URL = "./images/user-icon-f.svg";
    private static final String LOAD_AVATAR_URL = GWT.getModuleBaseURL() + "springApi/avatars/";
}
