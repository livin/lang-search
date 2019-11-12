import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Vladimir Livin
 */
public class PalendromeTest  {
    @Test
    public void testReverse() {
        assertEquals("spup", reverse("pups"));
    }

    @Test
    public void testRemoveExtraChars() {
        assertEquals("Acaramanamaraca", removeExtraChars("A car, a man, a maraca"));
    }


    @Test
    public void testPalendrome() {
        assertTrue(palendrome("level"));
        assertTrue(palendrome("A car, a man, a maraca"));
        assertFalse(palendrome("pups"));
    }

    private boolean palendrome(String string) {
        return string.equalsIgnoreCase(reverse(removeExtraChars(string)));
    }

    private String removeExtraChars(String string) {
        return string.replaceAll("[,\\s]+", "");
    }

    private String reverse(String string) {
        char reversed[] = new char[string.length()];
        for(int i = 0;  i < string.length(); i++)
            reversed[i] = string.charAt(string.length() - i - 1);
        return new String(reversed);
    }
}
