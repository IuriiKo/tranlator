package com.kushyk.translator;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.services.translate.Translate;
import com.google.api.services.translate.model.DetectionsListResponse;
import com.google.api.services.translate.model.LanguagesResource;
import com.google.api.services.translate.model.TranslationsResource;
import com.google.common.collect.Lists;
import com.kushyk.translator.util.RestQuery;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import rx.Observable;

/**
 * Created by Iurii Kushyk on 28.12.2016.
 */

public class OnlineTranslatorService implements TranslatorService {
    private String apiKey = RestQuery.API_KEY;
    private Translate translate;

    public static Translate initTranslate() {
        return  new Translate.Builder(
                AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory() , null)
                .build();
    }

    public OnlineTranslatorService(Translate translate) {
        this.translate = translate;
    }

    public OnlineTranslatorService() {
        this(initTranslate());
    }

    @Override
    public Observable<String[]> translate(String text,String currentLanguage, String targetLanguage){
        return Observable.fromCallable(()->{
            Translate.Translations.List list = translate.translations().list(
                    Arrays.asList(text),
                    targetLanguage);
            list.setKey(apiKey);
            return list.execute();
        })
                .map(response -> Lists.transform(response.getTranslations(), TranslationsResource::getTranslatedText).toArray(new String[0]));
    }

    @Override
    public Observable<List<String>> getLanguages() {
        return Observable.fromCallable(()->{
            Translate.Languages.List list = translate.languages().list();
            list.setKey(apiKey);
            return list.execute();
        }).map(list -> Lists.transform(list.getLanguages(), LanguagesResource::getLanguage));
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
