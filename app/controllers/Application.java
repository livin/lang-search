package controllers;

import model.Language;
import model.LanguageSearchIndex;
import play.Logger;
import play.api.Play;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import java.io.IOException;
import java.util.List;

import static play.libs.Json.toJson;

public class Application extends Controller {
    public static LanguageSearchIndex languageSearchIndex = new LanguageSearchIndex();

    public Application() {
        reindex();
    }

    public Result index() {
        return ok(index.render());
    }

    public Result search(String q) {
        return ok(toJson(languageSearchIndex.search(q)));
    }

    public void reindex() {
        languageSearchIndex.clear();

        try {
            List<Language> data = Language.loadList(Play.getFile("public/data.json", Play.current()));
            Application.languageSearchIndex.add(data);
        } catch (IOException e) {
            Logger.error(e.getMessage());
        }
    }
}
