package com.kushyk.translator;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.services.translate.Translate;
import com.google.api.services.translate.model.DetectionsListResponse;
import com.google.api.services.translate.model.LanguagesListResponse;
import com.google.api.services.translate.model.TranslationsListResponse;
import com.kushyk.translator.util.RestQuery;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by Iurii Kushyk on 28.12.2016.
 */

public class TranslatorService {
    private String apiKey = RestQuery.API_KEY;
    private Translate translate;

    public static Translate initTranslate() {
        return  new Translate.Builder(
                AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory() , null)
                .build();
    }

    public TranslatorService(Translate translate) {
        this.translate = translate;
    }

    public TranslatorService() {
        this(initTranslate());
    }

    public TranslationsListResponse translate(String text, String targetLanguage) throws IOException, NoSuchAlgorithmException{


        Translate.Translations.List list = translate.translations().list(
                Arrays.asList(text),
                targetLanguage);
        list.setKey(apiKey);
        return list.execute();
    }

    public LanguagesListResponse languages() throws IOException{
        Translate.Languages.List list = translate.languages().list();
        list.setKey(apiKey);
        return list.execute();
    }

    public DetectionsListResponse detections(String text) throws IOException{
        Translate.Detections.List list = translate.detections().list(Arrays.asList(text));
        list.setKey(apiKey);
        return list.execute();
    }

    public void setAPI_KEY(String apiKey) {
        this.apiKey = apiKey;
    }
}
