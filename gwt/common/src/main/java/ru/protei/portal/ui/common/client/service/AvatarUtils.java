package ru.protei.portal.ui.common.client.service;


import com.google.gwt.core.client.GWT;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.common.shared.model.Profile;

import java.util.Objects;

public class AvatarUtils {

    public static String getAvatarUrl(Profile profile) {
        return getAvatarUrl(profile.getId(), profile.getCompany().getCategoryId(), profile.getGender());
    }

    public static String getAvatarUrl(Person person) {
        return getAvatarUrl(person.getId(), person.getCompany().getCategoryId(), person.getGender());
    }

    public static String getAvatarUrl(Long accountId, Long categoryId, En_Gender gender) {
        if (Objects.equals(categoryId, En_CompanyCategory.HOME.getId() )) {
            return LOAD_AVATAR_URL + accountId + ".jpg";
        }

        return getAvatarUrlByGender(gender);
    }

    public static String getAvatarUrlByGender(En_Gender gender ) {
        if ( gender == null ) {
            return NO_AVATAR_PATH;
        }

        switch ( gender ) {
            case FEMALE:
                return FEMALE_AVATAR_URL;
            case MALE:
                return MALE_AVATAR_URL;
            default:
                return NO_AVATAR_PATH;
        }
    }

    private static final String NO_AVATAR_PATH = "./images/nophoto.png";
    private static final String MALE_AVATAR_URL = "./images/user-icon-m.svg";
    private static final String FEMALE_AVATAR_URL = "./images/user-icon-f.svg";
    private static final String LOAD_AVATAR_URL = GWT.getModuleBaseURL() + "springApi/avatars/";

}
