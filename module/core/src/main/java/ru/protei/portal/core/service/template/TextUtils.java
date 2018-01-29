package ru.protei.portal.core.service.template;


import java.util.*;
import java.util.regex.Pattern;

/**
 * Строковые утилиты для шаблонов Freemarker
 */
public class TextUtils {

    public String diff(String oldText, String newText, String insertStyle, String deleteStyle) {
        oldText = oldText != null?oldText:"";
        newText = newText != null?newText:"";
        LinkedList<Diff> diffs = diffMain(oldText, newText);
        return toHTML(diffs, insertStyle, deleteStyle);
    }

    private String toHTML(LinkedList<Diff> diffs, String insertStyle, String deleteStyle) {
        StringBuilder wiki = new StringBuilder();
        String insertTag = "<ins style=\""+ insertStyle +"\">";
        String deleteTag = "<del style=\""+ deleteStyle +"\">";

        for(Diff diff: diffs){
            switch(diff.operation) {
                case INSERT:
                    wiki.append(insertTag).append(diff.text).append("</ins>");
                    break;
                case DELETE:
                    wiki.append(deleteTag).append(diff.text).append("</del>");
                    break;
                case EQUAL:
                    wiki.append(diff.text);
            }
        }
        return wiki.toString();
    }


    private final float Diff_Timeout = 10.0F;
    private final Pattern BLANKLINEEND = Pattern.compile("\\n\\r?\\n\\Z", 32);
    private final Pattern BLANKLINESTART = Pattern.compile("\\A\\r?\\n\\r?\\n", 32);


    private LinkedList<Diff> diffMain(String text1, String text2) {
        return this.diff_main(text1, text2, true);
    }

    private LinkedList<Diff> diff_main(String text1, String text2, boolean checklines) {
        long deadline;
        if(this.Diff_Timeout <= 0.0F) {
            deadline = 9223372036854775807L;
        } else {
            deadline = System.currentTimeMillis() + (long)(this.Diff_Timeout * 1000.0F);
        }

        return this.diff_main(text1, text2, checklines, deadline);
    }

    private LinkedList<Diff> diff_main(String text1, String text2, boolean checklines, long deadline) {
        if(text1 != null && text2 != null) {
            LinkedList diffs;
            if(text1.equals(text2)) {
                diffs = new LinkedList();
                if(text1.length() != 0) {
                    diffs.add(new Diff(Operation.EQUAL, text1));
                }

                return diffs;
            } else {
                int commonlength = this.diff_commonPrefix(text1, text2);
                String commonprefix = text1.substring(0, commonlength);
                text1 = text1.substring(commonlength);
                text2 = text2.substring(commonlength);
                commonlength = this.diff_commonSuffix(text1, text2);
                String commonsuffix = text1.substring(text1.length() - commonlength);
                text1 = text1.substring(0, text1.length() - commonlength);
                text2 = text2.substring(0, text2.length() - commonlength);
                diffs = this.diff_compute(text1, text2, checklines, deadline);
                if(commonprefix.length() != 0) {
                    diffs.addFirst(new Diff(Operation.EQUAL, commonprefix));
                }

                if(commonsuffix.length() != 0) {
                    diffs.addLast(new Diff(Operation.EQUAL, commonsuffix));
                }

                this.diff_cleanupMerge(diffs);
                return diffs;
            }
        } else {
            throw new IllegalArgumentException("Null inputs. (diff_main)");
        }
    }

    private LinkedList<Diff> diff_compute(String text1, String text2, boolean checklines, long deadline) {
        LinkedList diffs = new LinkedList();
        if(text1.length() == 0) {
            diffs.add(new Diff(Operation.INSERT, text2));
            return diffs;
        } else if(text2.length() == 0) {
            diffs.add(new Diff(Operation.DELETE, text1));
            return diffs;
        } else {
            String longtext = text1.length() > text2.length()?text1:text2;
            String shorttext = text1.length() > text2.length()?text2:text1;
            int i = longtext.indexOf(shorttext);
            if(i != -1) {
                Operation hm1 = text1.length() > text2.length()? Operation.DELETE: Operation.INSERT;
                diffs.add(new Diff(hm1, longtext.substring(0, i)));
                diffs.add(new Diff(Operation.EQUAL, shorttext));
                diffs.add(new Diff(hm1, longtext.substring(i + shorttext.length())));
                return diffs;
            } else if(shorttext.length() == 1) {
                diffs.add(new Diff(Operation.DELETE, text1));
                diffs.add(new Diff(Operation.INSERT, text2));
                return diffs;
            } else {
                String[] hm = this.diff_halfMatch(text1, text2);
                if(hm != null) {
                    String text1_a = hm[0];
                    String text1_b = hm[1];
                    String text2_a = hm[2];
                    String text2_b = hm[3];
                    String mid_common = hm[4];
                    LinkedList diffs_a = this.diff_main(text1_a, text2_a, checklines, deadline);
                    LinkedList diffs_b = this.diff_main(text1_b, text2_b, checklines, deadline);
                    diffs_a.add(new Diff(Operation.EQUAL, mid_common));
                    diffs_a.addAll(diffs_b);
                    return diffs_a;
                } else {
                    return checklines && text1.length() > 100 && text2.length() > 100?this.diff_lineMode(text1, text2, deadline):this.diff_bisect(text1, text2, deadline);
                }
            }
        }
    }

    private LinkedList<Diff> diff_lineMode(String text1, String text2, long deadline) {
        LinesToCharsResult b = this.diff_linesToChars(text1, text2);
        text1 = b.chars1;
        text2 = b.chars2;
        List linearray = b.lineArray;
        LinkedList diffs = this.diff_main(text1, text2, false, deadline);
        this.diff_charsToLines(diffs, linearray);
        this.diffCleanupSemantic(diffs);
        diffs.add(new Diff(Operation.EQUAL, ""));
        int count_delete = 0;
        int count_insert = 0;
        String text_delete = "";
        String text_insert = "";
        ListIterator pointer = diffs.listIterator();

        for(Diff thisDiff = (Diff)pointer.next(); thisDiff != null; thisDiff = pointer.hasNext()?(Diff)pointer.next():null) {
            switch(thisDiff.operation.ordinal()) {
                case 1:
                    ++count_insert;
                    text_insert = text_insert + thisDiff.text;
                    break;
                case 2:
                    ++count_delete;
                    text_delete = text_delete + thisDiff.text;
                    break;
                case 3:
                    if(count_delete >= 1 && count_insert >= 1) {
                        pointer.previous();

                        for(int i$ = 0; i$ < count_delete + count_insert; ++i$) {
                            pointer.previous();
                            pointer.remove();
                        }

                        Iterator var16 = this.diff_main(text_delete, text_insert, false, deadline).iterator();

                        while(var16.hasNext()) {
                            Diff newDiff = (Diff)var16.next();
                            pointer.add(newDiff);
                        }
                    }

                    count_insert = 0;
                    count_delete = 0;
                    text_delete = "";
                    text_insert = "";
            }
        }

        diffs.removeLast();
        return diffs;
    }

    private LinkedList<Diff> diff_bisect(String text1, String text2, long deadline) {
        int text1_length = text1.length();
        int text2_length = text2.length();
        int max_d = (text1_length + text2_length + 1) / 2;
        int v_offset = max_d;
        int v_length = 2 * max_d;
        int[] v1 = new int[v_length];
        int[] v2 = new int[v_length];

        int delta;
        for(delta = 0; delta < v_length; ++delta) {
            v1[delta] = -1;
            v2[delta] = -1;
        }

        v1[max_d + 1] = 0;
        v2[max_d + 1] = 0;
        delta = text1_length - text2_length;
        boolean front = delta % 2 != 0;
        int k1start = 0;
        int k1end = 0;
        int k2start = 0;
        int k2end = 0;

        for(int diffs = 0; diffs < max_d && System.currentTimeMillis() <= deadline; ++diffs) {
            int k2;
            int k2_offset;
            int x2;
            int y2;
            int k1_offset;
            int x1;
            for(k2 = -diffs + k1start; k2 <= diffs - k1end; k2 += 2) {
                k2_offset = v_offset + k2;
                if(k2 != -diffs && (k2 == diffs || v1[k2_offset - 1] >= v1[k2_offset + 1])) {
                    x2 = v1[k2_offset - 1] + 1;
                } else {
                    x2 = v1[k2_offset + 1];
                }

                for(y2 = x2 - k2; x2 < text1_length && y2 < text2_length && text1.charAt(x2) == text2.charAt(y2); ++y2) {
                    ++x2;
                }

                v1[k2_offset] = x2;
                if(x2 > text1_length) {
                    k1end += 2;
                } else if(y2 > text2_length) {
                    k1start += 2;
                } else if(front) {
                    k1_offset = v_offset + delta - k2;
                    if(k1_offset >= 0 && k1_offset < v_length && v2[k1_offset] != -1) {
                        x1 = text1_length - v2[k1_offset];
                        if(x2 >= x1) {
                            return this.diff_bisectSplit(text1, text2, x2, y2, deadline);
                        }
                    }
                }
            }

            for(k2 = -diffs + k2start; k2 <= diffs - k2end; k2 += 2) {
                k2_offset = v_offset + k2;
                if(k2 != -diffs && (k2 == diffs || v2[k2_offset - 1] >= v2[k2_offset + 1])) {
                    x2 = v2[k2_offset - 1] + 1;
                } else {
                    x2 = v2[k2_offset + 1];
                }

                for(y2 = x2 - k2; x2 < text1_length && y2 < text2_length && text1.charAt(text1_length - x2 - 1) == text2.charAt(text2_length - y2 - 1); ++y2) {
                    ++x2;
                }

                v2[k2_offset] = x2;
                if(x2 > text1_length) {
                    k2end += 2;
                } else if(y2 > text2_length) {
                    k2start += 2;
                } else if(!front) {
                    k1_offset = v_offset + delta - k2;
                    if(k1_offset >= 0 && k1_offset < v_length && v1[k1_offset] != -1) {
                        x1 = v1[k1_offset];
                        int y1 = v_offset + x1 - k1_offset;
                        x2 = text1_length - x2;
                        if(x1 >= x2) {
                            return this.diff_bisectSplit(text1, text2, x1, y1, deadline);
                        }
                    }
                }
            }
        }

        LinkedList var26 = new LinkedList();
        var26.add(new Diff(Operation.DELETE, text1));
        var26.add(new Diff(Operation.INSERT, text2));
        return var26;
    }

    private LinkedList<Diff> diff_bisectSplit(String text1, String text2, int x, int y, long deadline) {
        String text1a = text1.substring(0, x);
        String text2a = text2.substring(0, y);
        String text1b = text1.substring(x);
        String text2b = text2.substring(y);
        LinkedList diffs = this.diff_main(text1a, text2a, false, deadline);
        LinkedList diffsb = this.diff_main(text1b, text2b, false, deadline);
        diffs.addAll(diffsb);
        return diffs;
    }

    private LinesToCharsResult diff_linesToChars(String text1, String text2) {
        ArrayList lineArray = new ArrayList();
        HashMap lineHash = new HashMap();
        lineArray.add("");
        String chars1 = this.diff_linesToCharsMunge(text1, lineArray, lineHash);
        String chars2 = this.diff_linesToCharsMunge(text2, lineArray, lineHash);
        return new LinesToCharsResult(chars1, chars2, lineArray);
    }

    private String diff_linesToCharsMunge(String text, List<String> lineArray, Map<String, Integer> lineHash) {
        int lineStart = 0;
        int lineEnd = -1;
        StringBuilder chars = new StringBuilder();

        while(lineEnd < text.length() - 1) {
            lineEnd = text.indexOf(10, lineStart);
            if(lineEnd == -1) {
                lineEnd = text.length() - 1;
            }

            String line = text.substring(lineStart, lineEnd + 1);
            lineStart = lineEnd + 1;
            if(lineHash.containsKey(line)) {
                chars.append(String.valueOf((char) lineHash.get(line).intValue()));
            } else {
                lineArray.add(line);
                lineHash.put(line, Integer.valueOf(lineArray.size() - 1));
                chars.append(String.valueOf((char)(lineArray.size() - 1)));
            }
        }

        return chars.toString();
    }

    private void diff_charsToLines(LinkedList<Diff> diffs, List<String> lineArray) {
        StringBuilder text;
        Diff diff;
        for(Iterator i$ = diffs.iterator(); i$.hasNext(); diff.text = text.toString()) {
            diff = (Diff)i$.next();
            text = new StringBuilder();

            for(int y = 0; y < diff.text.length(); ++y) {
                text.append(lineArray.get(diff.text.charAt(y)));
            }
        }

    }

    private int diff_commonPrefix(String text1, String text2) {
        int n = Math.min(text1.length(), text2.length());

        for(int i = 0; i < n; ++i) {
            if(text1.charAt(i) != text2.charAt(i)) {
                return i;
            }
        }

        return n;
    }

    private int diff_commonSuffix(String text1, String text2) {
        int text1_length = text1.length();
        int text2_length = text2.length();
        int n = Math.min(text1_length, text2_length);

        for(int i = 1; i <= n; ++i) {
            if(text1.charAt(text1_length - i) != text2.charAt(text2_length - i)) {
                return i - 1;
            }
        }

        return n;
    }

    private int diff_commonOverlap(String text1, String text2) {
        int text1_length = text1.length();
        int text2_length = text2.length();
        if(text1_length != 0 && text2_length != 0) {
            if(text1_length > text2_length) {
                text1 = text1.substring(text1_length - text2_length);
            } else if(text1_length < text2_length) {
                text2 = text2.substring(0, text1_length);
            }

            int text_length = Math.min(text1_length, text2_length);
            if(text1.equals(text2)) {
                return text_length;
            } else {
                int best = 0;
                int length = 1;

                while(true) {
                    int found;
                    do {
                        String pattern = text1.substring(text_length - length);
                        found = text2.indexOf(pattern);
                        if(found == -1) {
                            return best;
                        }

                        length += found;
                    } while(found != 0 && !text1.substring(text_length - length).equals(text2.substring(0, length)));

                    best = length++;
                }
            }
        } else {
            return 0;
        }
    }

    private String[] diff_halfMatch(String text1, String text2) {
        if(this.Diff_Timeout <= 0.0F) {
            return null;
        } else {
            String longtext = text1.length() > text2.length()?text1:text2;
            String shorttext = text1.length() > text2.length()?text2:text1;
            if(longtext.length() >= 4 && shorttext.length() * 2 >= longtext.length()) {
                String[] hm1 = this.diff_halfMatchI(longtext, shorttext, (longtext.length() + 3) / 4);
                String[] hm2 = this.diff_halfMatchI(longtext, shorttext, (longtext.length() + 1) / 2);
                if(hm1 == null && hm2 == null) {
                    return null;
                } else {
                    String[] hm;
                    if(hm2 == null) {
                        hm = hm1;
                    } else if(hm1 == null) {
                        hm = hm2;
                    } else {
                        hm = hm1[4].length() > hm2[4].length()?hm1:hm2;
                    }

                    return text1.length() > text2.length()?hm:new String[]{hm[2], hm[3], hm[0], hm[1], hm[4]};
                }
            } else {
                return null;
            }
        }
    }

    private String[] diff_halfMatchI(String longtext, String shorttext, int i) {
        String seed = longtext.substring(i, i + longtext.length() / 4);
        int j = -1;
        String best_common = "";
        String best_longtext_a = "";
        String best_longtext_b = "";
        String best_shorttext_a = "";
        String best_shorttext_b = "";

        while((j = shorttext.indexOf(seed, j + 1)) != -1) {
            int prefixLength = this.diff_commonPrefix(longtext.substring(i), shorttext.substring(j));
            int suffixLength = this.diff_commonSuffix(longtext.substring(0, i), shorttext.substring(0, j));
            if(best_common.length() < suffixLength + prefixLength) {
                best_common = shorttext.substring(j - suffixLength, j) + shorttext.substring(j, j + prefixLength);
                best_longtext_a = longtext.substring(0, i - suffixLength);
                best_longtext_b = longtext.substring(i + prefixLength);
                best_shorttext_a = shorttext.substring(0, j - suffixLength);
                best_shorttext_b = shorttext.substring(j + prefixLength);
            }
        }

        if(best_common.length() * 2 >= longtext.length()) {
            return new String[]{best_longtext_a, best_longtext_b, best_shorttext_a, best_shorttext_b, best_common};
        } else {
            return null;
        }
    }

    private void diffCleanupSemantic(LinkedList<Diff> diffs) {
        if(!diffs.isEmpty()) {
            boolean changes = false;
            Stack equalities = new Stack();
            String lastequality = null;
            ListIterator pointer = diffs.listIterator();
            int length_insertions1 = 0;
            int length_deletions1 = 0;
            int length_insertions2 = 0;
            int length_deletions2 = 0;

            Diff thisDiff;
            for(thisDiff = (Diff)pointer.next(); thisDiff != null; thisDiff = pointer.hasNext()?(Diff)pointer.next():null) {
                if(thisDiff.operation == Operation.EQUAL) {
                    equalities.push(thisDiff);
                    length_insertions1 = length_insertions2;
                    length_deletions1 = length_deletions2;
                    length_insertions2 = 0;
                    length_deletions2 = 0;
                    lastequality = thisDiff.text;
                } else {
                    if(thisDiff.operation == Operation.INSERT) {
                        length_insertions2 += thisDiff.text.length();
                    } else {
                        length_deletions2 += thisDiff.text.length();
                    }

                    if(lastequality != null && lastequality.length() <= Math.max(length_insertions1, length_deletions1) && lastequality.length() <= Math.max(length_insertions2, length_deletions2)) {
                        while(thisDiff != equalities.lastElement()) {
                            thisDiff = (Diff)pointer.previous();
                        }

                        pointer.next();
                        pointer.set(new Diff(Operation.DELETE, lastequality));
                        pointer.add(new Diff(Operation.INSERT, lastequality));
                        equalities.pop();
                        if(!equalities.empty()) {
                            equalities.pop();
                        }

                        if(equalities.empty()) {
                            while(pointer.hasPrevious()) {
                                pointer.previous();
                            }
                        }

                        length_insertions1 = 0;
                        length_insertions2 = 0;
                        length_deletions1 = 0;
                        length_deletions2 = 0;
                        lastequality = null;
                        changes = true;
                    }
                }
            }

            if(changes) {
                this.diff_cleanupMerge(diffs);
            }

            this.diff_cleanupSemanticLossless(diffs);
            pointer = diffs.listIterator();
            Diff prevDiff = null;
            thisDiff = null;
            if(pointer.hasNext()) {
                prevDiff = (Diff)pointer.next();
                if(pointer.hasNext()) {
                    thisDiff = (Diff)pointer.next();
                }
            }

            while(thisDiff != null) {
                if(prevDiff.operation == Operation.DELETE && thisDiff.operation == Operation.INSERT) {
                    String deletion = prevDiff.text;
                    String insertion = thisDiff.text;
                    int overlap_length1 = this.diff_commonOverlap(deletion, insertion);
                    int overlap_length2 = this.diff_commonOverlap(insertion, deletion);
                    if(overlap_length1 >= overlap_length2) {
                        if((double)overlap_length1 >= (double)deletion.length() / 2.0D || (double)overlap_length1 >= (double)insertion.length() / 2.0D) {
                            pointer.previous();
                            pointer.add(new Diff(Operation.EQUAL, insertion.substring(0, overlap_length1)));
                            prevDiff.text = deletion.substring(0, deletion.length() - overlap_length1);
                            thisDiff.text = insertion.substring(overlap_length1);
                        }
                    } else if((double)overlap_length2 >= (double)deletion.length() / 2.0D || (double)overlap_length2 >= (double)insertion.length() / 2.0D) {
                        pointer.previous();
                        pointer.add(new Diff(Operation.EQUAL, deletion.substring(0, overlap_length2)));
                        prevDiff.operation = Operation.INSERT;
                        prevDiff.text = insertion.substring(0, insertion.length() - overlap_length2);
                        thisDiff.operation = Operation.DELETE;
                        thisDiff.text = deletion.substring(overlap_length2);
                    }

                    thisDiff = pointer.hasNext()?(Diff)pointer.next():null;
                }

                prevDiff = thisDiff;
                thisDiff = pointer.hasNext()?(Diff)pointer.next():null;
            }

        }
    }

    private void diff_cleanupSemanticLossless(LinkedList<Diff> diffs) {
        ListIterator pointer = diffs.listIterator();
        Diff prevDiff = pointer.hasNext()?(Diff)pointer.next():null;
        Diff thisDiff = pointer.hasNext()?(Diff)pointer.next():null;

        for(Diff nextDiff = pointer.hasNext()?(Diff)pointer.next():null; nextDiff != null; nextDiff = pointer.hasNext()?(Diff)pointer.next():null) {
            if(prevDiff.operation == Operation.EQUAL && nextDiff.operation == Operation.EQUAL) {
                String equality1 = prevDiff.text;
                String edit = thisDiff.text;
                String equality2 = nextDiff.text;
                int commonOffset = this.diff_commonSuffix(equality1, edit);
                if(commonOffset != 0) {
                    String commonString = edit.substring(edit.length() - commonOffset);
                    equality1 = equality1.substring(0, equality1.length() - commonOffset);
                    edit = commonString + edit.substring(0, edit.length() - commonOffset);
                    equality2 = commonString + equality2;
                }

                String bestEquality1 = equality1;
                String bestEdit = edit;
                String bestEquality2 = equality2;
                int bestScore = this.diff_cleanupSemanticScore(equality1, edit) + this.diff_cleanupSemanticScore(edit, equality2);

                while(edit.length() != 0 && equality2.length() != 0 && edit.charAt(0) == equality2.charAt(0)) {
                    equality1 = equality1 + edit.charAt(0);
                    edit = edit.substring(1) + equality2.charAt(0);
                    equality2 = equality2.substring(1);
                    int score = this.diff_cleanupSemanticScore(equality1, edit) + this.diff_cleanupSemanticScore(edit, equality2);
                    if(score >= bestScore) {
                        bestScore = score;
                        bestEquality1 = equality1;
                        bestEdit = edit;
                        bestEquality2 = equality2;
                    }
                }

                if(!prevDiff.text.equals(bestEquality1)) {
                    if(bestEquality1.length() != 0) {
                        prevDiff.text = bestEquality1;
                    } else {
                        pointer.previous();
                        pointer.previous();
                        pointer.previous();
                        pointer.remove();
                        pointer.next();
                        pointer.next();
                    }

                    thisDiff.text = bestEdit;
                    if(bestEquality2.length() != 0) {
                        nextDiff.text = bestEquality2;
                    } else {
                        pointer.remove();
                        nextDiff = thisDiff;
                        thisDiff = prevDiff;
                    }
                }
            }

            prevDiff = thisDiff;
            thisDiff = nextDiff;
        }

    }

    private int diff_cleanupSemanticScore(String one, String two) {
        if(one.length() != 0 && two.length() != 0) {
            char char1 = one.charAt(one.length() - 1);
            char char2 = two.charAt(0);
            boolean nonAlphaNumeric1 = !Character.isLetterOrDigit(char1);
            boolean nonAlphaNumeric2 = !Character.isLetterOrDigit(char2);
            boolean whitespace1 = nonAlphaNumeric1 && Character.isWhitespace(char1);
            boolean whitespace2 = nonAlphaNumeric2 && Character.isWhitespace(char2);
            boolean lineBreak1 = whitespace1 && Character.getType(char1) == 15;
            boolean lineBreak2 = whitespace2 && Character.getType(char2) == 15;
            boolean blankLine1 = lineBreak1 && this.BLANKLINEEND.matcher(one).find();
            boolean blankLine2 = lineBreak2 && this.BLANKLINESTART.matcher(two).find();
            return !blankLine1 && !blankLine2?(!lineBreak1 && !lineBreak2?(nonAlphaNumeric1 && !whitespace1 && whitespace2?3:(!whitespace1 && !whitespace2?(!nonAlphaNumeric1 && !nonAlphaNumeric2?0:1):2)):4):5;
        } else {
            return 6;
        }
    }

    private void diff_cleanupMerge(LinkedList<Diff> diffs) {
        diffs.add(new Diff(Operation.EQUAL, ""));
        ListIterator pointer = diffs.listIterator();
        int count_delete = 0;
        int count_insert = 0;
        String text_delete = "";
        String text_insert = "";
        Diff thisDiff = (Diff)pointer.next();

        boolean changes;
        for(Diff prevEqual = null; thisDiff != null; thisDiff = pointer.hasNext()?(Diff)pointer.next():null) {
            switch(thisDiff.operation.ordinal()) {
                case 1:
                    ++count_insert;
                    text_insert = text_insert + thisDiff.text;
                    prevEqual = null;
                    break;
                case 2:
                    ++count_delete;
                    text_delete = text_delete + thisDiff.text;
                    prevEqual = null;
                    break;
                case 3:
                    if(count_delete + count_insert > 1) {
                        changes = count_delete != 0 && count_insert != 0;
                        pointer.previous();

                        while(count_delete-- > 0) {
                            pointer.previous();
                            pointer.remove();
                        }

                        while(count_insert-- > 0) {
                            pointer.previous();
                            pointer.remove();
                        }

                        if(changes) {
                            int commonlength = this.diff_commonPrefix(text_insert, text_delete);
                            if(commonlength != 0) {
                                if(pointer.hasPrevious()) {
                                    thisDiff = (Diff)pointer.previous();

                                    assert thisDiff.operation == Operation.EQUAL : "Previous diff should have been an equality.";

                                    thisDiff.text = thisDiff.text + text_insert.substring(0, commonlength);
                                    pointer.next();
                                } else {
                                    pointer.add(new Diff(Operation.EQUAL, text_insert.substring(0, commonlength)));
                                }

                                text_insert = text_insert.substring(commonlength);
                                text_delete = text_delete.substring(commonlength);
                            }

                            commonlength = this.diff_commonSuffix(text_insert, text_delete);
                            if(commonlength != 0) {
                                thisDiff = (Diff)pointer.next();
                                thisDiff.text = text_insert.substring(text_insert.length() - commonlength) + thisDiff.text;
                                text_insert = text_insert.substring(0, text_insert.length() - commonlength);
                                text_delete = text_delete.substring(0, text_delete.length() - commonlength);
                                pointer.previous();
                            }
                        }

                        if(text_delete.length() != 0) {
                            pointer.add(new Diff(Operation.DELETE, text_delete));
                        }

                        if(text_insert.length() != 0) {
                            pointer.add(new Diff(Operation.INSERT, text_insert));
                        }

                        thisDiff = pointer.hasNext()?(Diff)pointer.next():null;
                    } else if(prevEqual != null) {
                        prevEqual.text = prevEqual.text + thisDiff.text;
                        pointer.remove();
                        thisDiff = (Diff)pointer.previous();
                        pointer.next();
                    }

                    count_insert = 0;
                    count_delete = 0;
                    text_delete = "";
                    text_insert = "";
                    prevEqual = thisDiff;
            }
        }

        if(diffs.getLast().text.length() == 0) {
            diffs.removeLast();
        }

        changes = false;
        pointer = diffs.listIterator();
        Diff prevDiff = pointer.hasNext()?(Diff)pointer.next():null;
        thisDiff = pointer.hasNext()?(Diff)pointer.next():null;

        for(Diff nextDiff = pointer.hasNext()?(Diff)pointer.next():null; nextDiff != null; nextDiff = pointer.hasNext()?(Diff)pointer.next():null) {
            if(prevDiff.operation == Operation.EQUAL && nextDiff.operation == Operation.EQUAL) {
                if(thisDiff.text.endsWith(prevDiff.text)) {
                    thisDiff.text = prevDiff.text + thisDiff.text.substring(0, thisDiff.text.length() - prevDiff.text.length());
                    nextDiff.text = prevDiff.text + nextDiff.text;
                    pointer.previous();
                    pointer.previous();
                    pointer.previous();
                    pointer.remove();
                    pointer.next();
                    thisDiff = (Diff)pointer.next();
                    nextDiff = pointer.hasNext()?(Diff)pointer.next():null;
                    changes = true;
                } else if(thisDiff.text.startsWith(nextDiff.text)) {
                    prevDiff.text = prevDiff.text + nextDiff.text;
                    thisDiff.text = thisDiff.text.substring(nextDiff.text.length()) + nextDiff.text;
                    pointer.remove();
                    nextDiff = pointer.hasNext()?(Diff)pointer.next():null;
                    changes = true;
                }
            }

            prevDiff = thisDiff;
            thisDiff = nextDiff;
        }

        if(changes) {
            this.diff_cleanupMerge(diffs);
        }

    }

    public static class Diff {
        public Operation operation;
        public String text;

        public Diff(Operation operation, String text) {
            this.operation = operation;
            this.text = text;
        }

        public String toString() {
            String prettyText = this.text.replace('\n', '¶');
            return "Diff(" + this.operation + ",\"" + prettyText + "\")";
        }

        public int hashCode() {
            int result = this.operation == null?0:this.operation.hashCode();
            result += 31 * (this.text == null?0:this.text.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if(this == obj) {
                return true;
            } else if(obj == null) {
                return false;
            } else if(this.getClass() != obj.getClass()) {
                return false;
            } else {
                Diff other = (Diff)obj;
                if(this.operation != other.operation) {
                    return false;
                } else {
                    if(this.text == null) {
                        if(other.text != null) {
                            return false;
                        }
                    } else if(!this.text.equals(other.text)) {
                        return false;
                    }

                    return true;
                }
            }
        }
    }

    public enum Operation {
        DELETE,
        INSERT,
        EQUAL
    }

    private class LinesToCharsResult {
        final String chars1;
        final String chars2;
        final List<String> lineArray;

        LinesToCharsResult(String chars1, String chars2, List<String> lineArray) {
            this.chars1 = chars1;
            this.chars2 = chars2;
            this.lineArray = lineArray;
        }
    }
}
