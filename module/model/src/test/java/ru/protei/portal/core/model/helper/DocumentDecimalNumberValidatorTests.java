package ru.protei.portal.core.model.helper;

import org.junit.Test;
import ru.protei.portal.core.model.util.documentvalidators.DocumentDecimalNumberValidator;

import static org.junit.Assert.*;
import static ru.protei.portal.core.model.dict.En_DocumentCategory.*;

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

    @Test
    public void successfulKDTest() {
        assertTrue( DocumentDecimalNumberValidator.isValid("ПАМР.123456.123-01СБ", KD) );
        assertTrue( DocumentDecimalNumberValidator.isValid("ПДРА.000001.001-01СБ", KD) );
        assertTrue( DocumentDecimalNumberValidator.isValid("ПДРА.999999.999-99СБ", KD) );
    }

    @Test
    public void KDOrganizationTest() {
        assertFalse( DocumentDecimalNumberValidator.isValid("ГУГЛ.123456.123-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("42.123456.123-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("PAMR.123456.123-01СБ", KD) );;
        assertFalse( DocumentDecimalNumberValidator.isValid("*.123456.123-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("#$%^.123456.123-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("----.123456.123-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("    .123456.123-01СБ", KD) );
    }

    @Test
    public void KDNumberTest() {
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.123-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР..123-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР. .123-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.*.123-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.777.123-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.-23456.123-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.&23456.123-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.I23456.123-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.*12345.123-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.123-56.123-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.12+456.123-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.десять.123-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.одиндватричетырепять.123-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.one.123-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.0x1234.123-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.0x123h.123-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.0xCAFE.123-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.0b1010.123-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.12.345.123-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.12345f.123-01СБ", KD) );

        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001. -01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.1-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.*-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.-23-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.&23-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.I23-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.*12-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.1-5-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.1+4-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.десять-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.одиндватричетырепять-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.one-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.0x1-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.0xCAFE-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.0b1-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.1.3-01СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.12f-01СБ", KD) );

        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.123СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.123-СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.123- СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.123-1СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.123-*СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.123--23СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.123-&3СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.123-I2СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.123-4-1СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.123-1+4СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.123-десятьСБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.123-одиндватричетырепятьСБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.123-oneСБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.123-0x1СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.123-0xCAFEСБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.123-0b1СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.123-1.3СБ", KD) );
        assertFalse( DocumentDecimalNumberValidator.isValid("ПАМР.000001.123-1fСБ", KD) );
    }

    @Test
    public void KDPosTest() {
        assertFalse(DocumentDecimalNumberValidator.isValid("ПАМР.123456.123-01", KD));
        assertFalse(DocumentDecimalNumberValidator.isValid("ПАМР.123456.123-01 СБ", KD));
        assertFalse(DocumentDecimalNumberValidator.isValid("ПАМР.123456.123-01С", KD));
        assertFalse(DocumentDecimalNumberValidator.isValid("ПАМР.123456.123-01WJ", KD));
        assertFalse(DocumentDecimalNumberValidator.isValid("ПАМР.123456.123-01*", KD));
        assertFalse(DocumentDecimalNumberValidator.isValid("ПАМР.123456.123-01--", KD));
        assertFalse(DocumentDecimalNumberValidator.isValid("ПАМР.123456.123-01  ", KD));
    }

    @Test
    public void PDSuccessfulTest() {
        assertTrue(DocumentDecimalNumberValidator.isValid("ПАМР.12345-01 90", PD));
        assertTrue(DocumentDecimalNumberValidator.isValid("ПАМР.12345-01 90 12", PD));
        assertTrue(DocumentDecimalNumberValidator.isValid("ПАМР.12345-01 90 12-3", PD));
        assertFalse(DocumentDecimalNumberValidator.isValid("ПАМР.12345-01 90aaa", PD));
    }
}

