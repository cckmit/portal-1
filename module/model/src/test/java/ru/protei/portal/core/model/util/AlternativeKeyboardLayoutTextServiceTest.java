package ru.protei.portal.core.model.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AlternativeKeyboardLayoutTextServiceTest {

    static final String latinUpperCase = "~QWERTYUIOP{}ASDFGHJKL:\"ZXCVBNM<>?";
    static final String latinLowerCase = "`qwertyuiop[]asdfghjkl;'zxcvbnm,./";
    static final String cyrilUpperCase = "ЁЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ,";
    static final String cyrilLowerCase = "ёйцукенгшщзхъфывапролджэячсмитьбю.";

    @Test
    public void latinToCyrillic() {
        String alternativeString = AlternativeKeyboardLayoutTextService.latinToCyrillic( latinLowerCase );

        assertEquals( "Преобразование строчных латинских символов в кирилицу",
                cyrilLowerCase, alternativeString );
    }

    @Test
    public void latinToCyrillicUppercase() {
        String alternativeString = AlternativeKeyboardLayoutTextService.latinToCyrillic( latinUpperCase );

        assertEquals( "Преобразование символов латинских верхнего регистра в кирилицу",
                cyrilUpperCase, alternativeString );
    }

    @Test
    public void cyrillicToLatin() {
        String alternativeString = AlternativeKeyboardLayoutTextService.cyrillicToLatin( cyrilLowerCase );

        assertEquals( "Преобразование строчных кирилических символов в латиницу",
                latinLowerCase, alternativeString );
    }

    @Test
    public void cyrillicToLatinUppercase() {
        String alternativeString = AlternativeKeyboardLayoutTextService.cyrillicToLatin( cyrilUpperCase );

        assertEquals( "Преобразование символов кирилических верхнего регистра в латиницу",
                latinUpperCase, alternativeString );
    }

    @Test
    public void latinToLatin() {
        String expected = new String( latinLowerCase ).replace( '.', '/' ).replace( ',', '?' );
        String alternativeString = AlternativeKeyboardLayoutTextService.cyrillicToLatin( latinLowerCase );

        assertEquals( "Латиница буквы не изменятся, точка в слэш, запятая в вопрос",
                expected, alternativeString );
    }

    @Test
    public void cyrillicToCyrillic() {
        String expected = new String( cyrilLowerCase ).replace( '.', 'ю' );
        String alternativeString = AlternativeKeyboardLayoutTextService.latinToCyrillic( cyrilLowerCase );

        assertEquals( "Кириллица буквы не изменятся, точка в ю",
                expected, alternativeString );
    }

    @Test
    public void latinToLatinUppercase() {
        String alternativeString = AlternativeKeyboardLayoutTextService.cyrillicToLatin( latinUpperCase );

        assertEquals( "Латиница не изменятся",
                latinUpperCase, alternativeString );
    }

    @Test
    public void latinToAlternativeString() {
        String latinString = latinLowerCase + latinUpperCase;
        String expected = cyrilLowerCase.replace( 'б', ',' ).replace( 'ю', '.' ) + cyrilUpperCase;
        String alternativeString = AlternativeKeyboardLayoutTextService.makeAlternativeString( latinString );

        assertEquals( "Латиница буквы преобразуются, точка и запятая не изменяются",
                expected, alternativeString );
    }

    @Test
    public void cyrillicToAlternativeString() {
        String cyrillicString = cyrilLowerCase + cyrilUpperCase;
        String expected = latinLowerCase.replace( '/', '.' ) + latinUpperCase.replace( '?', ',' );
        String alternativeString = AlternativeKeyboardLayoutTextService.makeAlternativeString( cyrillicString );

        assertEquals( "Кирилица буквы преобразуются, точка и запятая не изменяются",
                expected, alternativeString );
    }

}