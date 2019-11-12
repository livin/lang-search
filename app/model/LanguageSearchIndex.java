package model;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import play.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Search Index. Provides addition to index and search.
 */
public class LanguageSearchIndex {
    public static final String DOUBLE_QUOTED_OR_WORD = "\"([^\"]*)\"|([-]?\\S+)";

    private ListMultimap<String, Language> invertedIndex = ArrayListMultimap.create();

    /** SearchUQuery represents parsed query */
    public static class SearchQuery {
        public String[] includes;
        public String[] excludes;
        public String[] exactMatches;
    }

    /**
     * Adds document to index
     * @param document
     */
    public void add(Language document) {
        List<String> words = new ArrayList<>();
        words.addAll(Arrays.asList(getWords(document.name)));
        words.addAll(Arrays.asList(getWords(getKeywords(document.type))));
        words.addAll(Arrays.asList(getWords(getKeywords(document.designedBy))));
        for (String word: words) {
            invertedIndex.put(word.toLowerCase(), document);
        }
    }

    /**
     * Adds a list of documents to index
     * @param list
     */
    public void add(List<Language> list) {
        Logger.info("Indexing " + list.size() + " languages");
        for (Language each: list)
            add(each);
    }

    private static String[] getKeywords(String keywords) {
        return keywords.split(",\\s+");
    }

    private static String[] getWords(String phrase) {
        return phrase.split("\\s+");
    }

    private String[] getWords(String[] keyWords) {
        ArrayList<String> words = new ArrayList<String>();
        for(String keyword: keyWords)
            words.addAll(Arrays.asList(getWords(keyword)));
        return words.toArray(new String[0]);
    }

    /**
     * Parses string query to more index-friendly query representation
     */
    public static SearchQuery parseQuery(String query) {
        SearchQuery sq = new SearchQuery();
        List<String> includes = new ArrayList<>();
        List<String> excludes = new ArrayList<>();
        List<String> exactMatches = new ArrayList<>();

        Matcher m = Pattern.compile(DOUBLE_QUOTED_OR_WORD).matcher(query);
        while (m.find()) {
            if (m.group(1) != null) {
                String content = m.group(1);
                exactMatches.add(content);
                includes.addAll(Arrays.asList(getWords(content)));
            } else {
                String word = m.group(2);
                boolean exclude = false;
                if (word.startsWith("-")) {
                    exclude = true;
                    word = word.substring(1);
                }
                if (exclude)
                    excludes.add(word);
                else
                    includes.add(word);
            }
        }
        sq.includes = includes.toArray(new String[0]);
        sq.excludes = excludes.toArray(new String[0]);
        sq.exactMatches = exactMatches.toArray(new String[0]);
        return sq;
    }

    /**
     * Searches languages in the index by a string query which can contain words
     * double-quoted phrases or excludes starting with minus (-).
     *
     * @param query a string representation of the query
     * @return a list of languages found by given query according to relevance
     */
    public List<Language> search(String query) {
        if (query.trim().isEmpty())
            return new ArrayList<Language>(Sets.newLinkedHashSet(invertedIndex.values()));

        SearchQuery searchQuery = parseQuery(query);

        LinkedHashMap<Language, Integer> matchScore = Maps.newLinkedHashMap();

        String firstWord = searchQuery.includes[0];
        // Start search
        HashSet<Language> results = Sets.newLinkedHashSet(documentsMatching(firstWord));
        updateMatchScore(matchScore, results);
        // Narrow down our search with subsequent words
        for (int i = 1; i < searchQuery.includes.length && results.size() > 0; i++) {
            List<Language> subsequentResults = documentsMatching(searchQuery.includes[i]);
            updateMatchScore(matchScore, subsequentResults);
        }

        // Get rid of excludes
        for (String exclude: searchQuery.excludes) {
            results.removeAll(documentsMatching(exclude));
        }

        // Keep only documents containing exact matches
        for (int exactMatchIndex = 0; exactMatchIndex < searchQuery.exactMatches.length && !results.isEmpty(); exactMatchIndex++) {
            final String exactMatchString = searchQuery.exactMatches[exactMatchIndex];
            Set<Language> exactMatches = results.stream().filter(l -> l.matches(exactMatchString)).collect(Collectors.toSet());
            results.retainAll(exactMatches);
        }

        // Order by relevance using matching score
        return results.stream().sorted((l1, l2) -> Integer.compare(matchScore.getOrDefault(l2, 0), matchScore.getOrDefault(l1, 0))).collect(Collectors.toList());
    }

    private void updateMatchScore(LinkedHashMap<Language, Integer> matchScore, Collection<Language> results) {
        for (Language lang: results)
            matchScore.put(lang, matchScore.getOrDefault(lang, 0) + 1);
    }

    /**
     * Returns all documents matching a single keyword.
     */
    private List<Language> documentsMatching(String keyword) {
        if (invertedIndex.containsKey(keyword.toLowerCase()))
            return invertedIndex.get(keyword.toLowerCase());
        else
            return Collections.emptyList();
    }

    public void clear() {
        invertedIndex.clear();
    }
}
