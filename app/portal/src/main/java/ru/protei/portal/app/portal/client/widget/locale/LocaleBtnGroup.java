package ru.protei.portal.app.portal.client.widget.locale;

import com.google.gwt.i18n.client.LocaleInfo;
import ru.protei.portal.test.client.DebugIdsHelper;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroup;

/**
 * Группа кнопок языков приложения
 */
public class LocaleBtnGroup extends ToggleBtnGroup<LocaleImage> {

    public LocaleBtnGroup() {
        fillOptions();
    }

    private void fillOptions() {
        String[] languages = LocaleInfo.getAvailableLocaleNames();

        for (String language : languages) {
            LocaleImage image = LocaleImage.findByLocale(language);
            if (image == null) {
                continue;
            }
            addBtnWithImage(
                    image.getImageUrl(),
                    "btn btn-default no-border",
                    null,
                    image,
                    null
            );
            setEnsureDebugId(image, DebugIdsHelper.LOCALE_BUTTON.byLocale(language));
        }
    }

}
