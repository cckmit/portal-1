package ru.protei.portal.tools;

import org.junit.Assert;
import org.junit.Test;

public class HtmlUtilsTest {
    
    @Test
    public void htmlEscapeTestQuotes() {
        String string = "Protei";
        String escapedString = HtmlUtils.htmlEscapeCharacters(string);
        Assert.assertEquals("String are equals after escape", string, escapedString);
    }

    @Test
    public void htmlEscapeTestDoubleQuotes() {
        String stringWithQuotes = "\"Protei";
        String escapedString = HtmlUtils.htmlEscapeCharacters(stringWithQuotes);
        Assert.assertTrue("Expected quote replaced on &quot;" ,escapedString.contains("&quot;"));
    }

    @Test
    public void htmlEscapeTestLeadingQuotesAmpersand() {
        String stringWithQuotes = "\"Protei";
        String escapedString = HtmlUtils.htmlEscapeCharacters(stringWithQuotes);
        Assert.assertTrue("Expected quote replaced on &quot;",escapedString.contains("&quot;"));
        Assert.assertFalse("Expected ampersand not replaced on &quot;\"",escapedString.contains("&amp;"));
    }

    @Test
    public void htmlEscapeTestDoubleQuotesBothSide() {
        String stringWithDoubleQuotesOnBothSide = "\"Protei\"";
        String escapedString = HtmlUtils.htmlEscapeCharacters(stringWithDoubleQuotesOnBothSide);
        String[] splittedEscapedString =  escapedString.split("Protei");
        Assert.assertEquals("Expected left quote replaced on &quot;",splittedEscapedString[0],"&quot;");
        Assert.assertEquals("Expected right quote replaced on &quot;", splittedEscapedString[1],"&quot;");
    }

    @Test
    public void htmlEscapeTestAmpersand() {
        String stringWithAmpersand = "&Protei";
        String escapedString = HtmlUtils.htmlEscapeCharacters(stringWithAmpersand);
        Assert.assertTrue("Expected ampersand replaced on &amp;" ,escapedString.contains("&amp;"));
    }

    @Test
    public void htmlEscapeTestLessThanSign() {
        String stringWithLessThanSign = "<Protei";
        String escapedString = HtmlUtils.htmlEscapeCharacters(stringWithLessThanSign);
        Assert.assertTrue("Expected replacing < on &lt; ", escapedString.contains("&lt;"));
    }

    @Test
    public void htmlEscapeTestSingleQuote() {
        String stringWithLessThanSign = "'Protei";
        String escapedString = HtmlUtils.htmlEscapeCharacters(stringWithLessThanSign);
        Assert.assertTrue("Expected single quote replaced on &#39;" ,escapedString.contains("&#39;"));
    }

    @Test
    public void htmlEscapeTestSingleQuoteBothSide() {
        String stringWithLessThanSign = "'Protei'";
        String escapedString = HtmlUtils.htmlEscapeCharacters(stringWithLessThanSign);
        String[] splittedEscapedString =  escapedString.split("Protei");
        Assert.assertEquals("Expected left single quote replaced on &#39;" ,splittedEscapedString[0], "&#39;");
        Assert.assertEquals("Expected right single quote replaced on &#39;", splittedEscapedString[1], "&#39;");
    }
}