import model.Language;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Vladimir Livin
 */
public class LanguageTest {
    private static final File dataJson = new File("public/data.json");

    @Test
    public void loadListShouldContainProperNumberOfRecordAndSomeSample() throws IOException {
        List<Language> languages = Language.loadList(dataJson);
        assertEquals("Load should contain proper number of records", 97, languages.size());
        assertEquals("A+", languages.get(0).name);
        assertEquals("Array", languages.get(0).type);
        assertEquals("Arthur Whitney", languages.get(0).designedBy);
    }
}
