package ru.protei.portal.app.portal.client.activity.auth;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.app.portal.client.widget.locale.LocaleImage;

/**
 * Created by turik on 23.09.16.
 */
public interface AbstractAuthView extends IsWidget {

    void setActivity( AbstractAuthActivity activity );

    HasValue<String> login();

    HasValue<String> password();

    HasValue<Boolean> rememberMe();

    void setFocus();

    void showError(String msg);

    void hideError();

    void reset();

    HasValue<LocaleImage> locale();
}
