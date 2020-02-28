package ru.protei.portal.core.model.helper;

import org.junit.Test;
import ru.protei.portal.core.model.util.documentvalidators.DocumentDecimalNumberValidator;

import static org.junit.Assert.*;
import static ru.protei.portal.core.model.dict.En_DocumentCategory.TD;

public class DocumentDecimalNumberValidatorTests {

    @Test
    public void successfulTDTest() {
        assertTrue( DocumentDecimalNumberValidator.isValid("ПАМР.60110.01234", TD) );
        assertTrue( DocumentDecimalNumberValidator.isValid("ПАМР.60110.00001", TD) );
        assertTrue( DocumentDecimalNumberValidator.isValid("ПАМР.60110.99999", TD) );
        assertTrue( DocumentDecimalNumberValidator.isValid("ПДРА.77291.98765", TD) );
        assertTrue( DocumentDecimalNumberValidator.isValid("ПДРА.77291.01234Р", TD) );
    }

    @Test
    public void TDWrongOrganizationTest() {
        assertFalse( DocumentDecimalNumberValidator.isValid("ГУГЛ.60110.01234", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("42.60110.01234", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("PAMR.60110.01234", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("*.60110.01234", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("#$%^.60110.01234", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("----.60110.01234", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("    .60110.01234", TD) );
    }

    @Test
    public void TDWrongTypeDocTest() {
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.03110.01234", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.AA110.01234", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.WW110.01234", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.**110.01234", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.--110.01234", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР. 2110.01234", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.1 110.01234", TD) );
    }

    @Test
    public void TDWrongTypeProcessTest() {
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.60910.01234", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.60W10.01234", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.60А10.01234", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.60 10.01234", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.60-10.01234", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.60*10.01234", TD) );
    }

    @Test
    public void TDWrongTypeProcessWorkTest() {
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.60105.01234", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.601WW.01234", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.601АА.01234", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.601--.01234", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.601**.01234", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.601 5.01234", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.6015 .01234", TD) );
    }

    @Test
    public void TDWrongNumberTest() {
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.60110", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.60110.", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.60110.1234", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.60110.-2345", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.60110.I2345", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.60110.&2345", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.60110.*2345", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.60110.1234*", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.60110.12-45", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.60110.2+2", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.60110.один", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.60110.одиндватричетырепять", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.60110.one", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.60110.0x123", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.60110.0x12h", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.60110.0xCAFE", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.60110.0b101", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.60110.12.13", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.60110.1234f", TD) );
    }

    @Test
    public void TDWSpaceTest() {
        assertFalse( DocumentDecimalNumberValidator.isValid(" ПАМР.60110.01234", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.60110.01234 ", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid(" ПАМР.60110.01234 ", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР 60110 01234", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР .60110.01234", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР. 60110.01234", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.60110 .01234", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.60110. 01234", TD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.60 11 0.01234", TD) );
    }

//    "ПАМР.60110.01234"
}
