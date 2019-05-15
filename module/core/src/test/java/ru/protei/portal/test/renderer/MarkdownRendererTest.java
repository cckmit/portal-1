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
public class MarkdownRendererTest {

    @Test
    public void testNoMarkdown() {
        String input = "There is no markdown";
        String expected = "<p>There is no markdown</p>";
        doTest(input, expected);
    }

    @Test
    public void testNoMarkdownWithSoftBreak() {
        String input = "There is no markdown\nSecond line";
        String expected = "<p>There is no markdown<br/>Second line</p>";
        doTest(input, expected);
    }

    @Test
    public void testNoMarkdownWithHardBreak() {
        String input = "There is no markdown  \nSecond line";
        String expected = "<p>There is no markdown<br />\nSecond line</p>";
        doTest(input, expected);
    }

    @Test
    public void testStrikethrough() {
        String input = "~~strikethrough~~";
        String expected = "<p><del>strikethrough</del></p>";
        doTest(input, expected);
    }

    @Test
    public void testItalic1() {
        String input = "*italic*";
        String expected = "<p><em>italic</em></p>";
        doTest(input, expected);
    }

    @Test
    public void testItalic2() {
        String input = "_italic_";
        String expected = "<p><em>italic</em></p>";
        doTest(input, expected);
    }

    @Test
    public void testBold1() {
        String input = "**bold**";
        String expected = "<p><strong>bold</strong></p>";
        doTest(input, expected);
    }

    @Test
    public void testBold2() {
        String input = "__bold__";
        String expected = "<p><strong>bold</strong></p>";
        doTest(input, expected);
    }

    @Test
    public void testItalicBold1() {
        String input = "***italic-bold***";
        String expected = "<p><em><strong>italic-bold</strong></em></p>";
        doTest(input, expected);
    }

    @Test
    public void testItalicBold2() {
        String input = "__*bold-italic*__";
        String expected = "<p><strong><em>bold-italic</em></strong></p>";
        doTest(input, expected);
    }

    @Test
    public void testHeader1() {
        String input = "# header1";
        String expected = "<h1>header1</h1>";
        doTest(input, expected);
    }

    @Test
    public void testHeader2() {
        String input = "## header2";
        String expected = "<h2>header2</h2>";
        doTest(input, expected);
    }

    @Test
    public void testHeader3() {
        String input = "### header3";
        String expected = "<h3>header3</h3>";
        doTest(input, expected);
    }

    @Test
    public void testQuoteSingleLine() {
        String input = "> Single line quote";
        String expected =
                "<blockquote>\n" +
                "<p>Single line quote</p>\n" +
                "</blockquote>";
        doTest(input, expected);
    }

    @Test
    public void testQuoteDoubleLine() {
        String input = "> Double line \n> quote";
        String expected =
                "<blockquote>\n" +
                "<p>Double line<br/>quote</p>\n" +
                "</blockquote>";
        doTest(input, expected);
    }

    @Test
    public void testQuoteOldStyle() {
        String input = "[quote]Single line quote[/quote]";
        String expected = "<p><blockquote>Single line quote</blockquote></p>";
        doTest(input, expected);
    }

    @Test
    public void testList() {
        String input =
                "- first\n" +
                "  - second 1\n" +
                "  - second 2\n" +
                "    - third";
        String expected =
                "<ul>\n" +
                    "<li>first\n" +
                    "<ul>\n" +
                        "<li>second 1</li>\n" +
                        "<li>second 2\n" +
                            "<ul>\n" +
                                "<li>third</li>\n" +
                            "</ul>\n" +
                        "</li>\n" +
                    "</ul>\n" +
                    "</li>\n" +
                "</ul>";
        doTest(input, expected);
    }

    @Test
    public void testTable() {
        String input =
                "| Tables        | Are           | Cool  |\n" +
                "| ------------- |:-------------:| -----:|\n" +
                "| **col 3 is**  | *right aligned* | $1500 |\n" +
                "| _col 2 is_    | *centered*    |   $13 |\n" +
                "| zebra stripes | are neat  |    $1 |";
        String expected =
                "<table>\n" +
                "<thead>\n" +
                    "<tr><th>Tables</th><th align=\"center\">Are</th><th align=\"right\">Cool</th></tr>\n" +
                "</thead>\n" +
                "<tbody>\n" +
                    "<tr><td><strong>col 3 is</strong></td><td align=\"center\"><em>right aligned</em></td><td align=\"right\">$1500</td></tr>\n" +
                    "<tr><td><em>col 2 is</em></td><td align=\"center\"><em>centered</em></td><td align=\"right\">$13</td></tr>\n" +
                    "<tr><td>zebra stripes</td><td align=\"center\">are neat</td><td align=\"right\">$1</td></tr>\n" +
                "</tbody>\n" +
                "</table>";
        doTest(input, expected);
    }

    @Test
    public void testBlock1() {
        String input = "`block`";
        String expected = "<p><code>block</code></p>";
        doTest(input, expected);
    }

    @Test
    public void testBlock2() {
        String input = "``block``";
        String expected = "<p><code>block</code></p>";
        doTest(input, expected);
    }

    @Test
    public void testBlock3() {
        String input = "```block```";
        String expected = "<p><code>block</code></p>";
        doTest(input, expected);
    }

    @Test
    public void testBlockMultiLine() {
        String input = "```\n" +
                "block\n" +
                "  asdf\n" +
                "f dsf   dsf\n" +
                "    er we e\n" +
                "```";
        String expected =
                "<pre><code>" +
                "block\n" +
                "  asdf\n" +
                "f dsf   dsf\n" +
                "    er we e\n" +
                "</code></pre>";
        doTest(input, expected);
    }

    @Test
    public void testLink() {
        String input = "[Link](http://localhost)";
        String expected = "<p><a href=\"http://localhost\">Link</a></p>";
        doTest(input, expected);
    }

    @Test
    public void testImage() {
        String input = "![Alt](http://localhost/image.png)";
        String expected = "<p><img src=\"http://localhost/image.png\" alt=\"Alt\" /></p>";
        doTest(input, expected);
    }

    private void doTest(String input, String expected) {
        String actual = htmlRenderer.plain2html(input, En_TextMarkup.MARKDOWN).trim();
        Assert.assertEquals("Not matched", expected, actual);
    }

    @Autowired
    HTMLRenderer htmlRenderer;
}
