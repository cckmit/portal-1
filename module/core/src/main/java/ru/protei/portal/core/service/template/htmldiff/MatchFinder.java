package ru.protei.portal.core.service.template.htmldiff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchFinder {

    private final String[] _oldWords;
    private final String[] _newWords;
    private final int _startInOld;
    private final int _endInOld;
    private final int _startInNew;
    private final int _endInNew;
    private Map<String, List<Integer>> _wordIndices;
    private final MatchOptions _options;

    public MatchFinder(String[] oldWords, String[] newWords, int startInOld, int endInOld, int startInNew, int endInNew, MatchOptions options) {
        _oldWords = oldWords;
        _newWords = newWords;
        _startInOld = startInOld;
        _endInOld = endInOld;
        _startInNew = startInNew;
        _endInNew = endInNew;
        _options = options;
    }

    public Match findMatch() {
        indexNewWords();
        removeRepeatingWords();

        if (_wordIndices.size() == 0) {
            return null;
        }

        int bestMatchInOld = _startInOld;
        int bestMatchInNew = _startInNew;
        int bestMatchSize = 0;

        Map<Integer, Integer> matchLengthAt = new HashMap<>();
        List<String> block = new ArrayList<>(_options.getBlockSize());

        for (int indexInOld = _startInOld; indexInOld < _endInOld; indexInOld++) {
            String word = normalizeForIndex(_oldWords[indexInOld]);
            String index = putNewWord(block, word, _options.getBlockSize());

            if (index == null) {
                continue;
            }

            Map<Integer, Integer> newMatchLengthAt = new HashMap<>();

            if (!_wordIndices.containsKey(index)) {
                matchLengthAt = newMatchLengthAt;
                continue;
            }

            for (int indexInNew : _wordIndices.get(index)) {
                int newMatchLength = matchLengthAt.getOrDefault(indexInNew - 1, 0) + 1;
                newMatchLengthAt.put(indexInNew, newMatchLength);
                if (newMatchLength > bestMatchSize) {
                    bestMatchInOld = indexInOld - newMatchLength + 1 - _options.getBlockSize() + 1;
                    bestMatchInNew = indexInNew - newMatchLength + 1 - _options.getBlockSize() + 1;
                    bestMatchSize = newMatchLength;
                }
            }

            matchLengthAt = newMatchLengthAt;
        }

        return bestMatchSize != 0 ? new Match(bestMatchInOld, bestMatchInNew, bestMatchSize + _options.getBlockSize() - 1) : null;
    }

    private void indexNewWords() {
        _wordIndices = new HashMap<>();
        List<String> block = new ArrayList<>(_options.getBlockSize());
        for (int i = _startInNew; i < _endInNew; i++) {
            // if word is a tag, we should ignore attributes as attribute changes are not supported (yet)
            String word = normalizeForIndex(_newWords[i]);
            String key = putNewWord(block, word, _options.getBlockSize());

            if (key == null) {
                continue;
            }

            if (_wordIndices.containsKey(key)) {
                _wordIndices.get(key).add(i);
            } else {
                List<Integer> list = new ArrayList<>();
                list.add(i);
                _wordIndices.put(key, list);
            }
        }
    }

    private String putNewWord(List<String> block, String word, int blockSize) {
        block.add(word);

        if (block.size() > blockSize) {
            block.remove(0);
        }

        if (block.size() != blockSize) {
            return null;
        }

        StringBuilder result = new StringBuilder(blockSize);

        for (String s : block) {
            result.append(s);
        }

        return result.toString();
    }

    /**
     * Converts the word to index-friendly value so it can be compared with other similar words
     */
    private String normalizeForIndex(String word) {
        word = Utils.stripAnyAttributes(word);
        if (_options.isIgnoreWhitespaceDifferences() && Utils.isWhiteSpace(word)) {
            return " ";
        }
        return word;
    }

    /**
     * This method removes words that occur too many times. This way it reduces total count of comparison operations
     * and as result the diff algoritm takes less time. But the side effect is that it may detect false differences of
     * the repeating words.
     */
    private void removeRepeatingWords() {
        double threshold = ((double) _newWords.length) * _options.getRepeatingWordsAccuracy();
        _wordIndices.entrySet().stream()
                .filter(entry -> entry.getValue().size() > threshold)
                .map(Map.Entry::getKey)
                .forEach(word -> _wordIndices.remove(word));
    }
}
