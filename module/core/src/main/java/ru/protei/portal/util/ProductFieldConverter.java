package ru.protei.portal.util;

import org.springframework.core.convert.converter.Converter;
import ru.protei.portal.core.model.ent.DevUnit;

public class ProductFieldConverter implements Converter<String, DevUnit.ProductField> {
    @Override
    public DevUnit.ProductField convert( String source ) {
        if(source==null) return null;
        try {
            return DevUnit.ProductField.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
