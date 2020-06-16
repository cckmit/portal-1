package ru.protei.portal.core.model.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class AlternativeKeyboardLayoutTextServiceTest {

   static final String latinUpperCase = "~QWERTYUIOP{}ASDFGHJKL:\"ZXCVBNM<>?";
   static final String latinLowerCase = "`qwertyuiop[]asdfghjkl;'zxcvbnm,./";
   static final String cyrilUpperCase = "ЁЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ,";
   static final String cyrilLowerCase = "ёйцукенгшщзхъфывапролджэячсмитьбю.";

    @Test
    public void latinToCyrillic(  ) {
        String alternativeString = AlternativeKeyboardLayoutTextService.latinToCyrillic( latinLowerCase );

        assertEquals( "Преобразование строчных символов в кирилицу",
                cyrilLowerCase, alternativeString );
    }

    @Test
    public void latinToCyrillicUppercase(  ) {
        String alternativeString = AlternativeKeyboardLayoutTextService.latinToCyrillic( latinUpperCase );

        assertEquals( "Преобразование символов верхнего регистра в кирилицу",
                cyrilUpperCase, alternativeString );
    }

    @Test
    public void cyrillicToLatin(  ) {
        String alternativeString = AlternativeKeyboardLayoutTextService.cyrillicToLatin( cyrilLowerCase );

        assertEquals( "Преобразование строчных символов в латиницу",
                latinLowerCase, alternativeString );
    }

    @Test
    public void cyrillicToLatinUppercase(  ) {
        String alternativeString = AlternativeKeyboardLayoutTextService.cyrillicToLatin( cyrilUpperCase );

        assertEquals( "Преобразование символов верхнего регистра в латиницу",
                latinUpperCase, alternativeString );
    }

    @Test
    public void latinToLatin(  ) {
        String expected = new String( latinLowerCase ).replace('.', '/' ).replace( ',', '?' );
        String alternativeString = AlternativeKeyboardLayoutTextService.cyrillicToLatin( latinLowerCase );

        assertEquals( "Латиница буквы не изменяется, точка как слэш, запятая как вопрос",
                expected, alternativeString );
    }

    @Test
    public void cyrillicToCyrillic(  ) {
        String expected = new String( cyrilLowerCase ).replace('.', 'ю' );
        String alternativeString = AlternativeKeyboardLayoutTextService.latinToCyrillic( cyrilLowerCase );

        assertEquals( "Кириллица буквы не изменяется, точка как ю",
                expected, alternativeString );
    }

    @Test
    public void latinToLatinUppercase(  ) {
        String alternativeString = AlternativeKeyboardLayoutTextService.cyrillicToLatin( latinUpperCase );

        assertEquals( "Латиница не изменяется",
                latinUpperCase, alternativeString );
    }

}