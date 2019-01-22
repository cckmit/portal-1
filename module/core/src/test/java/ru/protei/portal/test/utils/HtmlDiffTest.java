package ru.protei.portal.test.utils;

import org.junit.Assert;
import org.junit.Test;
import ru.protei.portal.core.service.template.htmldiff.HtmlDiff;

public class HtmlDiffTest {

    @Test
    public void testPlainTextNoChanges() {
        String input1 = "Один два три четыре";
        String input2 = "Один два три четыре";
        String expected = "Один два три четыре";
        doTest(input1, input2, expected);
    }

    @Test
    public void testTextWithTagNoChanges() {
        String input1 = "Один <b>два</b> три четыре";
        String input2 = "Один <b>два</b> три четыре";
        String expected = "Один <b>два</b> три четыре";
        doTest(input1, input2, expected);
    }

    @Test
    public void testPlainTextOneLine() {
        String input1 = "Один два три четыре";
        String input2 = "Один пять три четыре";
        String expected = "Один <del>два</del><ins>пять</ins> три четыре";
        doTest(input1, input2, expected);
    }

    @Test
    public void testPlainTextOneLineChangeWord() {
        String input1 = "Один два три четыре";
        String input2 = "Один две три четыре";
        String expected = "Один дв<del>а</del><ins>е</ins> три четыре";
        doTest(input1, input2, expected);
    }

    @Test
    public void testPlainTextTwoLines() {
        String input1 = "Первая строка.\nСтрока.";
        String input2 = "Первая строка.\nВторая строка.";
        String expected = "Первая строка.\n<del>С</del><ins>Вторая с</ins>трока.";
        doTest(input1, input2, expected);
    }

    @Test
    public void testPlainTextTwoLinesFullReplace() {
        String input1 = "Первая строка\nВторая строка";
        String input2 = "Ехал грека\nЧерез реку";
        String expected = "<del>Первая строка\nВторая строка</del><ins>Ехал грека\nЧерез реку</ins>";
        doTest(input1, input2, expected);
    }

    @Test
    public void testPlainTextWithBlockquote() {
        String input1 = "Первая строка.\n<blockquote>Цитата\nЦитата еще.</blockquote>";
        String input2 = "Первая строка.\n<blockquote>Референс\nРеференс еще.</blockquote>";
        String expected = "Первая строка.\n<blockquote><del>Цитата\nЦитата</del><ins>Референс\nРеференс</ins> еще.</blockquote>";
        doTest(input1, input2, expected);
    }

    @Test
    public void testTagAppended() {
        String input1 = "В начале было слово";
        String input2 = "В начале <strong>было</strong> слово";
        String expected = "В начале <del>было</del><strong><ins>было</ins></strong> слово";
        doTest(input1, input2, expected);
    }

    @Test
    public void testTagRemoved() {
        String input1 = "В начале <strong>было</strong> слово";
        String input2 = "В начале было слово";
        String expected = "В начале <strong><del>было</del></strong><ins>было</ins> слово";
        doTest(input1, input2, expected);
    }

    @Test
    public void testTagReplaced() {
        String input1 = "В начале <strong>было</strong> слово";
        String input2 = "В начале <i>было</i> слово";
        String expected = "В начале <strong><del>было</del></strong><i><ins>было</ins></i> слово";
        doTest(input1, input2, expected);
    }

    @Test
    public void testTableCellReplacement() {
        String input1 = "<table><tr><td>Первая ячейка</td><td>Вторая ячейка</td></tr></table>";
        String input2 = "<table><tr><td>Первая ячейка</td><td>Вторая столбец</td></tr></table>";
        String expected = "<table><tr><td>Первая ячейка</td><td>Вторая <del>ячейка</del><ins>столбец</ins></td></tr></table>";
        doTest(input1, input2, expected);
    }

    @Test
    public void testTableAddRow() {
        String input1 = "<table><tr><td>Первая ячейка</td><td>Вторая ячейка</td></tr></table>";
        String input2 = "<table><tr><td>Первая ячейка</td><td>Вторая ячейка</td></tr><tr><td>Третья</td><td>Четвертая</td></tr></table>";
        String expected = "<table><tr><td>Первая ячейка</td><td>Вторая ячейка</td></tr><tr><td><ins>Третья</ins></td><td><ins>Четвертая</ins></td></tr></table>";
        doTest(input1, input2, expected);
    }

    @Test
    public void testTableRemoveRow() {
        String input1 = "<table><tr><td>Первая ячейка</td><td>Вторая ячейка</td></tr><tr><td>Третья</td><td>Четвертая</td></tr></table>";
        String input2 = "<table><tr><td>Первая ячейка</td><td>Вторая ячейка</td></tr></table>";
        String expected = "<table><tr><td>Первая ячейка</td><td>Вторая ячейка</td></tr><tr><td><del>Третья</del></td><td><del>Четвертая</del></td></tr></table>";
        doTest(input1, input2, expected);
    }

    @Test
    public void testReplaceTagInsideTag() {
        String input1 = "Сквозь волнистые туманы <b>пробирается луна <i>на печальные</i> поляны</b> льет печально свет она";
        String input2 = "Сквозь волнистые туманы <b>пробирается луна <em>на печальные</em> поляны</b> льет печально свет она";
        String expected = "Сквозь волнистые туманы <b>пробирается луна <i><del>на печальные</del></i><em><ins>на печальные</ins></em> поляны</b> льет печально свет она";
        doTest(input1, input2, expected);
    }

    @Test
    public void testReplaceTagWithInsideTag() {
        String input1 = "Сквозь волнистые туманы <b>пробирается луна <i>на печальные</i> поляны</b> льет печально свет она";
        String input2 = "Сквозь волнистые туманы <strong>пробирается луна <em>на печальные</em> поляны</strong> льет печально свет она";
        String expected = "Сквозь волнистые туманы " +
                "<b><del>пробирается луна </del><i><del>на печальные</del></i><del>&nbsp;поляны</del></b>" +
                "<strong><ins>пробирается луна </ins><em><ins>на печальные</ins></em><ins>&nbsp;поляны</ins></strong>" +
                " льет печально свет она";
        doTest(input1, input2, expected);
    }

    @Test
    public void testAddLiElement() {
        String input1 = "<ul><li>Первый элемент</li></ul>";
        String input2 = "<ul><li>Первый элемент</li><li>Второй элемент</li></ul>";
        String expected = "<ul><li>Первый элемент</li><li><ins>Второй элемент</ins></li></ul>";
        doTest(input1, input2, expected);
    }

    @Test
    public void testStyleAttribute() {
        String input1 = "а";
        String input2 = "аб";
        String style = "color:black;";
        String expected = "а<ins style=\"" + style + "\">б</ins>";
        doTest(input1, input2, expected, style);
    }

    private void doTest(String input1, String input2, String expected) {
        doTest(input1, input2, expected, "");
    }

    private void doTest(String input1, String input2, String expected, String style) {
        input1 = input1 != null ? input1 : "";
        input2 = input2 != null ? input2 : "";
        String actual = HtmlDiff.execute(input1, input2, style, style);
        Assert.assertEquals("Not matched", expected, actual);
    }
}
