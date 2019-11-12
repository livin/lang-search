package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Language {
    @JsonProperty("Name")
    public String name;

    @JsonProperty("Type")
    public String type;

    @JsonProperty("Designed by")
    public String designedBy;

    public Language(String name, String type, String designedBy) {
        this.name = name;
        this.type = type;
        this.designedBy = designedBy;
    }

    public Language() {
    }

    public static List<Language> loadList(File jsonFile) throws IOException {
        return new ObjectMapper().readValue(jsonFile, new TypeReference<List<Language>>(){});
    }

    @Override
    public String toString() {
        return String.format("%s by %s (%s)", name, designedBy, type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Language language = (Language) o;

        if (!designedBy.equals(language.designedBy)) return false;
        if (!name.equals(language.name)) return false;
        if (!type.equals(language.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public boolean matches(String string) {
        return (name.contains(string)) || (type.contains(string)) || (designedBy.contains(string));
    }
}
