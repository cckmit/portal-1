package ru.protei.portal.test.renderer;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.config.MainTestsConfiguration;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.renderer.HTMLRenderer;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class, MainTestsConfiguration.class})
public class JiraWikiMarkupRendererTest {

    @Test
    public void testNoMarkup() {
        String input = "There is no markup";
        String expected = "<p>There is no markup</p>";
        doTest(input, expected);
    }

    @Test
    public void testNoMarkupWithSoftBreak() {
        String input = "There is no markup\nSecond line";
        String expected = "<p>There is no markup<br/>\nSecond line</p>";
        doTest(input, expected);
    }

    @Test
    public void testNoMarkupWithHardBreak() {
        String input = "There is no markup\\\\\nSecond line";
        String expected = "<p>There is no markup<br class=\"atl-forced-newline\" />\nSecond line</p>";
        doTest(input, expected);
    }

    @Test
    public void testStrong() {
        String input = "*strong*";
        String expected = "<p><b>strong</b></p>";
        doTest(input, expected);
    }

    @Test
    public void testEmphasis() {
        String input = "_emphasis_";
        String expected = "<p><em>emphasis</em></p>";
        doTest(input, expected);
    }

    @Test
    public void testCitation() {
        String input = "??citation??";
        String expected = "<p><cite>citation</cite></p>";
        doTest(input, expected);
    }

    @Test
    public void testDeleted() {
        String input = "-deleted-";
        String expected = "<p><del>deleted</del></p>";
        doTest(input, expected);
    }

    @Test
    public void testInserted() {
        String input = "+inserted+";
        String expected = "<p><ins>inserted</ins></p>";
        doTest(input, expected);
    }

    @Test
    public void testSuperscript() {
        String input = "^superscript^";
        String expected = "<p><sup>superscript</sup></p>";
        doTest(input, expected);
    }

    @Test
    public void testSubscript() {
        String input = "~subscript~";
        String expected = "<p><sub>subscript</sub></p>";
        doTest(input, expected);
    }

    @Test
    public void testMonospaced() {
        String input = "{{monospaced}}";
        String expected = "<p><tt>monospaced</tt></p>";
        doTest(input, expected);
    }

    @Test
    public void testBlockquoteSingle() {
        String input = "bq. Some block quoted text";
        String expected = "<blockquote><p>Some block quoted text</p></blockquote>";
        doTest(input, expected);
    }

    @Test
    public void testBlockquoteMulti() {
        String input =
                "{quote}\n" +
                " here is quotable\n" +
                " content to be quoted\n" +
                "{quote}";
        String expected =
                "<blockquote>\n" +
                " here is quotable\n" +
                " content to be quoted\n" +
                "</blockquote>";
        doTest(input, expected);
    }

    @Test
    public void testColored() {
        String input = "{color:red}look ma, red text!{color}";
        String expected = "<p><font color=\"red\">look ma, red text!</font></p>";
        doTest(input, expected);
    }

    @Test
    public void testHorizontalLine() {
        String input = "----";
        String expected = "<hr />";
        doTest(input, expected);
    }

    @Test
    public void testDashLong() {
        String input = "---";
        String expected = "<p>&#8212;</p>";
        doTest(input, expected);
    }

    @Test
    public void testDashMedium() {
        String input = "--";
        String expected = "<p>&#8211;</p>";
        doTest(input, expected);
    }

    @Test
    public void testDash() {
        String input = "-";
        String expected = "<p>-</p>";
        doTest(input, expected);
    }

    @Test
    public void testHeader1() {
        String input = "h1. Biggest heading";
        String expected = "<h1><a name=\"Biggestheading\"></a>Biggest heading</h1>";
        doTest(input, expected);
    }

    @Test
    public void testHeader2() {
        String input = "h2. Bigger heading";
        String expected = "<h2><a name=\"Biggerheading\"></a>Bigger heading</h2>";
        doTest(input, expected);
    }

    @Test
    public void testHeader3() {
        String input = "h3. Big heading";
        String expected = "<h3><a name=\"Bigheading\"></a>Big heading</h3>";
        doTest(input, expected);
    }

    @Test
    public void testTable() {
        String input =
                "||heading 1||heading 2||heading 3||\n" +
                "|col A1|col A2|col A3|\n" +
                "|col B1|col B2|col B3|";
        String expected =
                "<div class='table-wrap'>\n" +
                "<table class='confluenceTable'><tbody>\n" +
                "<tr>\n" +
                "<th class='confluenceTh'>heading 1</th>\n" +
                "<th class='confluenceTh'>heading 2</th>\n" +
                "<th class='confluenceTh'>heading 3</th>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td class='confluenceTd'>col A1</td>\n" +
                "<td class='confluenceTd'>col A2</td>\n" +
                "<td class='confluenceTd'>col A3</td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td class='confluenceTd'>col B1</td>\n" +
                "<td class='confluenceTd'>col B2</td>\n" +
                "<td class='confluenceTd'>col B3</td>\n" +
                "</tr>\n" +
                "</tbody></table>\n" +
                "</div>";
        doTest(input, expected);
    }

    @Test
    public void testList() {
        String input =
                "* some\n" +
                "* bullet\n" +
                "** indented\n" +
                "** bullets\n" +
                "* points";
        String expected =
                "<ul>\n" +
                "\t<li>some<br/></li>\n" +
                "\t<li>bullet<br/>\n" +
                "\t<ul>\n" +
                "\t\t<li>indented<br/></li>\n" +
                "\t\t<li>bullets<br/></li>\n" +
                "\t</ul>\n" +
                "\t</li>\n" +
                "\t<li>points</li>\n" +
                "</ul>";
        doTest(input, expected);
    }

    private void doTest(String input, String expected) {
        String actual = htmlRenderer.plain2html(input, En_TextMarkup.JIRA_WIKI_MARKUP).trim();
        Assert.assertEquals("Not matched", expected, actual);
    }

    @Autowired
    HTMLRenderer htmlRenderer;
}
