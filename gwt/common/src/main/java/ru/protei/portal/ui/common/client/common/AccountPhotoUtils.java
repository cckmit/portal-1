package ru.protei.portal.ui.common.client.common;


import com.google.gwt.core.client.GWT;
import ru.protei.portal.core.model.dict.En_Gender;

public class AccountPhotoUtils {
    private static final String LOAD_AVATAR_URL = GWT.getModuleBaseURL() + "springApi/avatars/";

    public static String getPhotoUrl(Long accountId) {
        return LOAD_AVATAR_URL + accountId + ".jpg";
    }

    public static String getGenderIcon( En_Gender gender ) {
        if ( gender == null ) {
            return UiConstants.UserIcon.MALE;
        }

        switch ( gender ) {
            case FEMALE:
                return UiConstants.UserIcon.FEMALE;
            case MALE:
            default:
                return UiConstants.UserIcon.MALE;
        }
    }
}
