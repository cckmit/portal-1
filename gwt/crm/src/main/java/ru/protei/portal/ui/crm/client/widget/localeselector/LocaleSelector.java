package ru.protei.portal.ui.crm.client.widget.localeselector;

import ru.protei.portal.ui.common.client.widget.selector.image.ImageSelector;

import java.util.List;

/**
 * Селектор языков приложения
 */
public class LocaleSelector extends ImageSelector<LocaleImagesHelper.ImageModel > {

    public void fillOptions( List< LocaleImagesHelper.ImageModel > list ) {

        for ( LocaleImagesHelper.ImageModel model : list ) {
            addOption( model.name, model, model.img );
        }
    }
}
