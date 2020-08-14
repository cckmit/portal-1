package ru.protei.portal.core.model.converter;

import ru.protei.portal.core.model.struct.Money;
import ru.protei.winter.jdbc.converter.AbstractConverter;
import ru.protei.winter.jdbc.converter.Converter;

public class MoneyJdbcConverter extends AbstractConverter<Long, Money> implements Converter<Long, Money> {

    @Override
    public Money sqlToJava(Long value) throws Exception {
        return new Money(value);
    }

    @Override
    public Long javaToSql(Money value) throws Exception {
        return value.getFull();
    }
}
