import com.google.common.collect.ImmutableList;
import model.Language;
import model.LanguageSearchIndex;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class LanguageSearchIndexTest {
    private final static Language aPlus = new Language("A+", "Array", "Arthur Whitney");
    private final static Language actionScript = new Language("ActionScript", "Compiled, Curly-bracket, Procedural, Reflective, Scripting, Object-oriented class-based", "Gary Grossman");

    private final static List<Language> fewLangs = ImmutableList.of(aPlus, actionScript);
    public static final File JSON_FILE = new File("public/data.json");
    private static LanguageSearchIndex fullSampleIndex = new LanguageSearchIndex();

    static  {
        try {
            fullSampleIndex.add(Language.loadList(JSON_FILE));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private LanguageSearchIndex index;

    @Before
    public void setUp() throws IOException {
        index = new LanguageSearchIndex();
    }

    @Test
    public void searchShouldGiveLanguageWithGivenName() {
        index.add(aPlus);
        List<Language> results = index.search("A+");
        assertEquals("One result should be found", 1, results.size());
        assertSame("Result should be A+", aPlus, results.get(0));
    }

    @Test
    public void searchByKeywordForScriptingShouldGiveActionScript() {
        index.add(fewLangs);
        List<Language> results = index.search("Scripting");
        assertEquals(1, results.size());
        assertSame("Scripting should match ActionScript", actionScript, results.get(0));
    }

    @Test
    public void searchByPartOfKeywordClassBasedShouldGiveActionScript() {
        index.add(fewLangs);
        List<Language> results = index.search("class-based");
        assertEquals(1, results.size());
        assertSame("Class-based should match ActionScript", actionScript, results.get(0));
    }

    @Test
    public void searchByAuthorSurnameGrossmanShouldGiveActionScript() {
        index.add(fewLangs);
        List<Language> results = index.search("Grossman");
        assertEquals(1, results.size());
        assertSame("Grossman should match ActionScript", actionScript, results.get(0));
    }

    @Test
    public void searchLispCommonShouldGiveCommonLisp() {
        List<Language> results = fullSampleIndex.search("Lisp Common");
        assertEquals("Common Lisp", results.get(0).name);
    }

    @Test
    public void searchThomasEugeneExactShouldNotMatchEugeneThomas() {
        Language thomasEugene = new Language("A", "Compiled", "Thomas Eugene");
        Language eugeneThomas = new Language("B", "Compiled", "Eugene Thomas");
        index.add(thomasEugene);
        index.add(eugeneThomas);
        List<Language> results = index.search("\"Thomas Eugene\"");
        assertEquals(1, results.size());
        assertSame(thomasEugene, results.get(0));
    }

    @Test
    public void searchJohnMinusArrayShouldIncludeJohnButNotArray() {
        Language c = new Language("C", "Compiled, Scripting", "John D");
        Language d = new Language("D", "Compiled, Array", "John H");
        index.add(c);
        index.add(d);
        List<Language> results = index.search("john -array");
        assertEquals(Arrays.asList(c), results);
    }

    @Test
    public void testParseQuery() {
        LanguageSearchIndex.SearchQuery sq = LanguageSearchIndex.parseQuery("Interpreted -Array \"Thomas Eugene\"");
        assertEquals(Arrays.asList("Interpreted", "Thomas", "Eugene"), Arrays.asList(sq.includes));
        assertEquals(Arrays.asList("Array"), Arrays.asList(sq.excludes));
        assertEquals(Arrays.asList("Thomas Eugene"), Arrays.asList(sq.exactMatches));
    }

    @Test
    public void testDocumentHavingMoreMatchesShouldBeMoreRelevant() {
        Language x1 = new Language("X1", "Compiled, Scripting", "John D");
        Language x2 = new Language("X2", "Compiled, Array", "John H");
        index.add(x1);
        index.add(x2);
        List<Language> results = index.search("Compiled Array");
        assertEquals(Arrays.asList(x2, x1), results);
    }
}
