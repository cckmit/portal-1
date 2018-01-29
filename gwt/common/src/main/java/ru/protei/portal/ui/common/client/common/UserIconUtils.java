package ru.protei.portal.ui.common.client.common;

import ru.protei.portal.core.model.dict.En_Gender;

public class UserIconUtils {

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
