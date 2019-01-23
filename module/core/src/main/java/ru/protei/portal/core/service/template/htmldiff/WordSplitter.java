package ru.protei.portal.core.service.template.htmldiff;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WordSplitter {

    /**
     * Converts Html text into a list of words
     */
    public static String[] convertHtmlToListOfWords(String text, List<Pattern> blockExpressions) {
        Mode mode = Mode.CHARACTER;
        List<Character> currentWord = new ArrayList<>();
        List<String> words = new ArrayList<>();

        Map<Integer, Integer> blockLocations = findBlocks(text, blockExpressions);

        boolean isBlockCheckRequired = !blockLocations.isEmpty();
        boolean isGrouping = false;
        int groupingUntil = -1;

        for (int index = 0; index < text.length(); index++) {
            char character = text.charAt(index);

            // Don't bother executing block checks if we don't have any blocks to check for!
            if (isBlockCheckRequired) {
                // Check if we have completed grouping a text sequence/block
                if (groupingUntil == index) {
                    groupingUntil = -1;
                    isGrouping = false;
                }

                // Check if we need to group the next text sequence/block
                int until = 0;
                if (blockLocations.containsKey(index)) {
                    until = blockLocations.get(index);
                    isGrouping = true;
                    groupingUntil = until;
                }

                // if we are grouping, then we don't care about what type of character we have, it's going to be treated as a word
                if (isGrouping) {
                    currentWord.add(character);
                    mode = Mode.CHARACTER;
                    continue;
                }
            }

            switch (mode) {
                case CHARACTER: {
                    if (Utils.isStartOfTag(character)) {
                        if (currentWord.size() != 0) {
                            words.add(charList2string(currentWord));
                        }
                        currentWord.clear();
                        currentWord.add('<');
                        mode = Mode.TAG;
                    } else if (Utils.isStartOfEntity(character)) {
                        if (currentWord.size() != 0) {
                            words.add(charList2string(currentWord));
                        }
                        currentWord.clear();
                        currentWord.add(character);
                        mode = Mode.ENTITY;
                    } else if (Utils.isWhiteSpace(character)) {
                        if (currentWord.size() != 0) {
                            words.add(charList2string(currentWord));
                        }
                        currentWord.clear();
                        currentWord.add(character);
                        mode = Mode.WHITESPACE;
                    } else if (Utils.isWord(character) &&
                            (currentWord.size() == 0 || Utils.isWord(currentWord.get(currentWord.size() - 1)))) {
                        currentWord.add(character);
                    } else {
                        if (currentWord.size() != 0) {
                            words.add(charList2string(currentWord));
                        }
                        currentWord.clear();
                        currentWord.add(character);
                    }
                    break;
                }
                case TAG: {
                    if (Utils.isEndOfTag(character)) {
                        currentWord.add(character);
                        words.add(charList2string(currentWord));
                        currentWord.clear();
                        mode = Utils.isWhiteSpace(character) ? Mode.WHITESPACE : Mode.CHARACTER;
                    } else {
                        currentWord.add(character);
                    }
                    break;
                }
                case WHITESPACE: {
                    if (Utils.isStartOfTag(character)) {
                        if (currentWord.size() != 0) {
                            words.add(charList2string(currentWord));
                        }
                        currentWord.clear();
                        currentWord.add(character);
                        mode = Mode.TAG;
                    } else if (Utils.isStartOfEntity(character)) {
                        if (currentWord.size() != 0) {
                            words.add(charList2string(currentWord));
                        }
                        currentWord.clear();
                        currentWord.add(character);
                        mode = Mode.ENTITY;
                    } else if (Utils.isWhiteSpace(character)) {
                        currentWord.add(character);
                    } else {
                        if (currentWord.size() != 0) {
                            words.add(charList2string(currentWord));
                        }
                        currentWord.clear();
                        currentWord.add(character);
                        mode = Mode.CHARACTER;
                    }
                    break;
                }
                case ENTITY: {
                    if (Utils.isStartOfTag(character)) {
                        if (currentWord.size() != 0) {
                            words.add(charList2string(currentWord));
                        }
                        currentWord.clear();
                        currentWord.add(character);
                        mode = Mode.TAG;
                    } else if (Utils.isWhiteSpace(character)) {
                        if (currentWord.size() != 0) {
                            words.add(charList2string(currentWord));
                        }
                        currentWord.clear();
                        currentWord.add(character);
                        mode = Mode.WHITESPACE;
                    } else if (Utils.isEndOfEntity(character)) {
                        boolean switchToNextMode = true;
                        if (currentWord.size() != 0) {
                            currentWord.add(character);
                            words.add(charList2string(currentWord));
                            //join &nbsp; entity with last whitespace
                            if (words.size() > 2
                                    && Utils.isWhiteSpace(words.get(words.size() - 2))
                                    && Utils.isWhiteSpace(words.get(words.size() - 1))) {
                                String w1 = words.get(words.size() - 2);
                                String w2 = words.get(words.size() - 1);
                                words.subList(words.size() - 2, 2).clear();
                                currentWord.clear();
                                currentWord.addAll(string2charList(w1));
                                currentWord.addAll(string2charList(w2));
                                mode = Mode.WHITESPACE;
                                switchToNextMode = false;
                            }
                        }
                        if (switchToNextMode) {
                            currentWord.clear();
                            mode = Mode.CHARACTER;
                        }
                    } else if (Utils.isWord(character)) {
                        currentWord.add(character);
                    } else {
                        if (currentWord.size() != 0) {
                            words.add(charList2string(currentWord));
                        }
                        currentWord.clear();
                        currentWord.add(character);
                        mode = Mode.CHARACTER;
                    }
                    break;
                }
            }
        }
        if (currentWord.size() != 0) {
            words.add(charList2string(currentWord));
        }

        return words.toArray(new String[0]);
    }

    /**
     * Finds any blocks that need to be grouped
     */
    private static Map<Integer, Integer> findBlocks(String text, List<Pattern> blockExpressions) {
        Map<Integer, Integer> blockLocations = new HashMap<>();

        if (blockExpressions == null) {
            return blockLocations;
        }

        for (Pattern exp : blockExpressions) {
            Matcher m = exp.matcher(text);
            while (m.find()) {
                if (blockLocations.containsKey(m.start())) {
                    throw new IllegalArgumentException("One or more block expressions result in a text sequence that " +
                            "overlaps. Current expression: " + exp.toString());
                }
                blockLocations.put(m.start(), m.start() + m.group().length());
            }
        }
        return blockLocations;
    }

    private static String charList2string(List<Character> chars) {
        return chars.stream()
                .map(Object::toString)
                .collect(Collectors.joining());
    }

    private static List<Character> string2charList(String text) {
        return text.chars()
                .mapToObj(e -> (char) e)
                .collect(Collectors.toList());
    }
}
