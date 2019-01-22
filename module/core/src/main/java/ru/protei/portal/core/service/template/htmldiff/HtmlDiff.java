package ru.protei.portal.core.service.template.htmldiff;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Java port of https://github.com/Rohland/htmldiff.net
 */
public class HtmlDiff {

    /**
     * This value defines balance between speed and memory utilization.
     * The higher it is the faster it works and more memory consumes.
     */
    private final static int MatchGranularityMaximum = 4;

    private final StringBuilder _content;
    private String _newText;
    private String _oldText;
    private String[] _newWords;
    private String[] _oldWords;
    private int _matchGranularity;
    private List<Pattern> _blockExpressions;

    /**
     * Defines how to compare repeating words. Valid values are from 0 to 1.
     * This value allows to exclude some words from comparison that eventually
     * reduces the total time of the diff algorithm.
     * 0 means that all words are excluded so the diff will not find any matching words at all.
     * 1 (default value) means that all words participate in comparison so this is the most accurate case.
     * 0.5 means that any word that occurs more than 50% times may be excluded from comparison. This doesn't
     * mean that such words will definitely be excluded but only gives a permission to exclude them if necessary.
     */
    private double repeatingWordsAccuracy;

    /**
     * If true all whitespaces are considered as equal
     */
    private boolean ignoreWhitespaceDifferences;

    /**
     * If some match is too small and located far from its neighbors then it is considered as orphan
     * and removed. For example:
     * <code>
     * aaaaa bb ccccccccc dddddd ee
     * 11111 bb 222222222 dddddd ee
     * </code>
     * will find two matches <code>bb</code> and <code>dddddd ee</code> but the first will be considered
     * as orphan and ignored, as result it will consider texts <code>aaaaa bb ccccccccc</code> and
     * <code>11111 bb 222222222</code> as single replacement:
     * <code>
     * &lt;del&gt;aaaaa bb ccccccccc&lt;/del&gt;&lt;ins&gt;11111 bb 222222222&lt;/ins&gt; dddddd ee
     * </code>
     * This property defines relative size of the match to be considered as orphan, from 0 to 1.
     * 1 means that all matches will be considered as orphans.
     * 0 (default) means that no match will be considered as orphan.
     * 0.2 means that if match length is less than 20% of distance between its neighbors it is considered as orphan.
     */
    private double orphanMatchThreshold;

    public double getRepeatingWordsAccuracy() {
        return repeatingWordsAccuracy;
    }

    public void setRepeatingWordsAccuracy(double repeatingWordsAccuracy) {
        this.repeatingWordsAccuracy = repeatingWordsAccuracy;
    }

    public boolean isIgnoreWhitespaceDifferences() {
        return ignoreWhitespaceDifferences;
    }

    public void setIgnoreWhitespaceDifferences(boolean ignoreWhitespaceDifferences) {
        this.ignoreWhitespaceDifferences = ignoreWhitespaceDifferences;
    }

    public double getOrphanMatchThreshold() {
        return orphanMatchThreshold;
    }

    public void setOrphanMatchThreshold(double orphanMatchThreshold) {
        this.orphanMatchThreshold = orphanMatchThreshold;
    }

    /**
     * Initializes a new instance of the class
     * @param oldText The old text
     * @param newText The new text
     */
    public HtmlDiff(String oldText, String newText) {
        repeatingWordsAccuracy = 1d; // by default all repeating words should be compared
        _oldText = oldText;
        _newText = newText;
        _content = new StringBuilder();
        _blockExpressions = new ArrayList<>();
    }

    public static String execute(String oldText, String newText, String insStyle, String delStyle) {
        return new HtmlDiff(oldText, newText).build(insStyle, delStyle);
    }

    /**
     * Builds the HTML diff output
     * @return HTML diff markup
     */
    public String build(String insStyle, String delStyle) {
        // If there is no difference, don't bother checking for differences
        if (Objects.equals(_oldText, _newText)) {
            return _newText;
        }

        splitInputsToWords();

        _matchGranularity = Math.min(MatchGranularityMaximum, Math.min(_oldWords.length, _newWords.length));

        List<Operation> op = operations();

        for (Operation item : op) {
            performOperation(item, insStyle, delStyle);
        }

        return _content.toString();
    }

    /**
     * Uses expression to group text together so that any change detected within the group is treated as a single block
     * @param expression
     */
    public void addBlockExpression(Pattern expression) {
        _blockExpressions.add(expression);
    }

    private void splitInputsToWords() {
        _oldWords = WordSplitter.convertHtmlToListOfWords(_oldText, _blockExpressions);

        //free memory, allow it for GC
        _oldText = null;

        _newWords = WordSplitter.convertHtmlToListOfWords(_newText, _blockExpressions);

        //free memory, allow it for GC
        _newText = null;
    }

    private void performOperation(Operation operation, String insStyle, String delStyle) {
        switch (operation.getAction()) {
            case EQUAL:
                processEqualOperation(operation);
                break;
            case DELETE:
                processDeleteOperation(operation, delStyle);
                break;
            case INSERT:
                processInsertOperation(operation, insStyle);
                break;
            case REPLACE:
                processReplaceOperation(operation, insStyle, delStyle);
                break;
            case NONE:
                break;
        }
    }

    private void processReplaceOperation(Operation operation, String insStyle, String delStyle) {
        processDeleteOperation(operation, delStyle);
        processInsertOperation(operation, insStyle);
    }

    private void processInsertOperation(Operation operation, String style) {
        AtomicInteger index = new AtomicInteger();
        List<String> text = Stream.of(_newWords)
                .filter(word -> {
                    int pos = index.getAndIncrement();
                    return pos >= operation.getStartInNew() && pos < operation.getEndInNew();
                })
                .collect(Collectors.toList());
        insertTag("ins", style, text);
    }

    private void processDeleteOperation(Operation operation, String style) {
        AtomicInteger index = new AtomicInteger();
        List<String> text = Stream.of(_oldWords)
                .filter(word -> {
                    int pos = index.getAndIncrement();
                    return pos >= operation.getStartInOld() && pos < operation.getEndInOld();
                })
                .collect(Collectors.toList());
        insertTag("del", style, text);
    }

    private void processEqualOperation(Operation operation) {
        AtomicInteger index = new AtomicInteger();
        String result = Stream.of(_newWords)
                .filter(word -> {
                    int pos = index.getAndIncrement();
                    return pos >= operation.getStartInNew() && pos < operation.getEndInNew();
                })
                .map(Object::toString)
                .collect(Collectors.joining(""));
        _content.append(result);
    }

    /**
     * This method encloses words within a specified tag (ins or del), and adds this into "content",
     * with a twist: if there are words contain tags, it actually creates multiple ins or del,
     * so that they don't include any ins or del. This handles cases like
     * old: '<p>a</p>'
     * new: '<p>ab</p>
     *     <p>
     *     c</b>'
     * diff result: '<p>a<ins>b</ins></p>
     *     <p>
     *         <ins>c</ins>
     *     </p>
     *     '
     * this still doesn't guarantee valid HTML (hint: think about diffing a text containing ins or
     * del tags), but handles correctly more cases than the earlier version.
     * P.S.: Spare a thought for people who write HTML browsers. They live in this ... every day.
     */
    private void insertTag(String tag, String style, List<String> words) {
        while (true) {
            if (words.size() == 0) {
                break;
            }
            String[] nonTags = extractConsecutiveWords(words, x -> !Utils.isTag(x));
            if (nonTags.length != 0) {
                String plainText = String.join("", nonTags);
                String wrappedPlainText = Utils.wrapText(plainText, tag, style);
                _content.append(wrappedPlainText);
            }
            if (words.size() == 0) {
                break;
            }
            _content.append(String.join("", extractConsecutiveWords(words, Utils::isTag)));
        }
    }

    private String[] extractConsecutiveWords(List<String> words, Function<String, Boolean> condition) {
        Integer indexOfFirstTag = null;

        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);

            if (i == 0 && " ".equals(word)) {
                words.set(i, "&nbsp;");
            }

            if (!condition.apply(word)) {
                indexOfFirstTag = i;
                break;
            }
        }

        if (indexOfFirstTag != null) {
            AtomicInteger index = new AtomicInteger();
            Integer finalIndexOfFirstTag = indexOfFirstTag;
            String[] items = words.stream()
                    .filter(word -> {
                        int pos = index.getAndIncrement();
                        return pos >= 0 && pos < finalIndexOfFirstTag;
                    })
                    .toArray(String[]::new);

            if (indexOfFirstTag > 0) {
                words.subList(0, indexOfFirstTag).clear();
            }
            return items;
        } else {
            AtomicInteger index = new AtomicInteger();
            String[] items = words.stream()
                    .filter(word -> {
                        int pos = index.getAndIncrement();
                        return pos >= 0 && pos <= words.size();
                    })
                    .toArray(String[]::new);
            words.subList(0, words.size()).clear();
            return items;
        }
    }

    private List<Operation> operations() {
        int positionInOld = 0, positionInNew = 0;
        List<Operation> operations = new ArrayList<>();

        List<Match> matches = matchingBlocks();

        matches.add(new Match(_oldWords.length, _newWords.length, 0));

        //Remove orphans from matches.
        //If distance between left and right matches is 4 times longer than length of current match then it is considered as orphan
        List<Match> matchesWithoutOrphans = removeOrphans(matches);

        for (Match match : matchesWithoutOrphans) {
            boolean matchStartsAtCurrentPositionInOld = (positionInOld == match.getStartInOld());
            boolean matchStartsAtCurrentPositionInNew = (positionInNew == match.getStartInNew());

            Action action;

            if (!matchStartsAtCurrentPositionInOld && !matchStartsAtCurrentPositionInNew) {
                action = Action.REPLACE;
            } else if (matchStartsAtCurrentPositionInOld && !matchStartsAtCurrentPositionInNew) {
                action = Action.INSERT;
            } else if (!matchStartsAtCurrentPositionInOld) {
                action = Action.DELETE;
            } else {
                // This occurs if the first few words are the same in both versions
                action = Action.NONE;
            }

            if (action != Action.NONE) {
                operations.add(
                        new Operation(action,
                                positionInOld,
                                match.getStartInOld(),
                                positionInNew,
                                match.getStartInNew()));
            }

            if (match.getSize() != 0) {
                operations.add(new Operation(
                        Action.EQUAL,
                        match.getStartInOld(),
                        match.getEndInOld(),
                        match.getStartInNew(),
                        match.getEndInNew()));
            }

            positionInOld = match.getEndInOld();
            positionInNew = match.getEndInNew();
        }

        return operations;
    }

    private List<Match> removeOrphans(List<Match> matches) {
        List<Match> result = new ArrayList<>();
        Match prev = null;
        Match curr = null;
        for (Match next : matches) {
            if (curr == null) {
                prev = new Match(0, 0, 0);
                curr = next;
                continue;
            }

            if (prev.getEndInOld() == curr.getStartInOld() && prev.getEndInNew() == curr.getStartInNew()
                    || curr.getEndInOld() == next.getStartInOld() && curr.getEndInNew() == next.getStartInNew()) {
                //if match has no diff on the left or on the right
                result.add(curr);
                prev = curr;
                curr = next;
                continue;
            }

            int oldDistanceInChars = IntStream.range(prev.getEndInOld(), next.getStartInOld() - prev.getEndInOld())
                    .map(i -> _oldWords[i].length())
                    .sum();
            int newDistanceInChars = IntStream.range(prev.getEndInNew(), next.getStartInNew() - prev.getEndInNew())
                    .map(i -> _newWords[i].length())
                    .sum();
            int currMatchLengthInChars = IntStream.range(curr.getStartInNew(), curr.getEndInNew() - curr.getStartInNew())
                    .map(i -> _newWords[i].length())
                    .sum();

            if (currMatchLengthInChars > Math.max(oldDistanceInChars, newDistanceInChars) * orphanMatchThreshold) {
                result.add(curr);
            }

            prev = curr;
            curr = next;
        }

        result.add(curr); //assume that the last match is always vital
        return result;
    }

    private List<Match> matchingBlocks() {
        List<Match> matchingBlocks = new ArrayList<>();
        findMatchingBlocks(0, _oldWords.length, 0, _newWords.length, matchingBlocks);
        return matchingBlocks;
    }

    private void findMatchingBlocks(int startInOld, int endInOld, int startInNew, int endInNew, List<Match> matchingBlocks) {
        Match match = findMatch(startInOld, endInOld, startInNew, endInNew);

        if (match != null) {
            if (startInOld < match.getStartInOld() && startInNew < match.getStartInNew()) {
                findMatchingBlocks(startInOld, match.getStartInOld(), startInNew, match.getStartInNew(), matchingBlocks);
            }

            matchingBlocks.add(match);

            if (match.getEndInOld() < endInOld && match.getEndInNew() < endInNew) {
                findMatchingBlocks(match.getEndInOld(), endInOld, match.getEndInNew(), endInNew, matchingBlocks);
            }
        }
    }

    private Match findMatch(int startInOld, int endInOld, int startInNew, int endInNew) {
        // For large texts it is more likely that there is a Match of size bigger than maximum granularity.
        // If not then go down and try to find it with smaller granularity.
        for (int i = _matchGranularity; i > 0; i--) {
            MatchOptions options = new MatchOptions(i, repeatingWordsAccuracy, ignoreWhitespaceDifferences);
            MatchFinder finder = new MatchFinder(_oldWords, _newWords, startInOld, endInOld, startInNew, endInNew, options);
            Match match = finder.findMatch();
            if (match != null) {
                return match;
            }
        }
        return null;
    }
}
