package ru.protei.portal.ui.common.client.util;

import com.google.gwt.i18n.client.LocaleInfo;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.util.TransliterationUtils;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientTransliterationUtils {
    static public Set<PersonShortView> transliterateNotifiers(Collection<Person> notifiers) {
        return notifiers == null ? new HashSet<>() :
                notifiers
                        .stream()
                        .map(notifier -> {
                            PersonShortView personShortView = new PersonShortView(notifier);
                            personShortView.setName(transliteration(personShortView.getDisplayShortName()));
                            return personShortView;
                        })
                        .collect(Collectors.toSet());
    }

    static public String transliteration(String input) {
        return TransliterationUtils.transliterate(input, LocaleInfo.getCurrentLocale().getLocaleName());
    }
}
