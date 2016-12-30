package com.kushyk.translator;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;

/**
 * Created by Iurii Kushyk on 29.12.2016.
 */

public class OfflineTranslatorService implements TranslatorService{
    private static final String LOG_TAG = OfflineTranslatorService.class.getName();
    public static final String SENSE_QUERY = "sense";
    public static final String ORTH_QUERY = "orth";
    private Map<String, String> pathMap = new HashMap<>();
    private Map<String, Elements> dictionaryCash = new HashMap<>();

    public OfflineTranslatorService() {
        pathMap.put("enru", "eng-rus.tei");
    }

    public Elements parseDocument(String path) {
        InputStream stream = null;
        try {
            stream = App.getContext().getAssets().open(path);
            Document document = Jsoup.parse(stream, null, "", Parser.xmlParser());
            if (document != null) {
                return document.select("body entry");
            } else {
                throw new IllegalArgumentException("parseDocument() document = null");
            }
        } catch (IOException e) {
            Log.e(LOG_TAG,"parseDocument()", e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG,"parseDocument()", e);
                }
            }
        }
        return null;
    }

      @Override
    public Observable<String[]> translate(String text,String currentLanguage, String targetLanguage) {
        return Observable.fromCallable(()->{
            List<String> translate = new ArrayList<>();
            boolean isReverseDictionary = isReverseDictionary(currentLanguage, targetLanguage);
            String query = isReverseDictionary ? ORTH_QUERY : SENSE_QUERY;
            for (Element entry : getDictionary(currentLanguage, targetLanguage)) {
                if (!entry.select(":containsOwn(" + text + ")").isEmpty()) {
                    if (!isTargetTextEquals(text, entry, isReverseDictionary)) {
                        break;
                    }
                    Elements sense = entry.select(query);

                    for (Element element : sense) {
                        translate.add(element.text());
                    }
                    break;
                }
            }
            return translate;
        }).map(list -> list.toArray(new String[0]));
    }

    private boolean isTargetTextEquals(String text, Element entry, boolean isReverseDictionary) {
        return  text.equals(entry.select(isReverseDictionary ? SENSE_QUERY : ORTH_QUERY).text());
    }

    private boolean isReverseDictionary(String currentLanguage, String targetLanguage) {
        for (String pathKey : pathMap.keySet()) {
            if (pathKey.equals(currentLanguage + targetLanguage)) {
                return false;
            } else if (pathKey.equals(targetLanguage + currentLanguage)) {
                return true;
            }
        }
        return false;
    }

    private Elements getDictionary(String currentLanguage, String targetLanguage) {
        boolean isDictionaryValid = false;

        String key = currentLanguage + targetLanguage;
        String path = null;
        for (String pathKey : pathMap.keySet()) {
            if (pathKey.equals(key)) {
                isDictionaryValid =  true;
                path = pathMap.get(pathKey);
            } else if (pathKey.equals(targetLanguage + currentLanguage)) {
                isDictionaryValid =  true;
                path = pathMap.get(pathKey);
            }
        }

        if (!isDictionaryValid) {
            throw new IllegalArgumentException("invalid dictionary: " + currentLanguage + " : " + targetLanguage);
        }

        boolean isDictionaryInCash = dictionaryCash.containsKey(key);

        if (!isDictionaryInCash) {
            dictionaryCash.put(key, parseDocument(path));
        }
        return dictionaryCash.get(key);
    }

    @Override
    public Observable<List<String>> getLanguages() {
        return Observable.fromCallable(()->{
            List<String> list = new ArrayList<>();
            InputStream inputStream = null;

            try {
                inputStream = App.getContext().getAssets().open("language.xml");
                Document document = Jsoup.parse(inputStream, null, "", Parser.xmlParser());
                for (Element element : document.select("item")) {
                    list.add(element.text());
                }
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return list;
        });
    }
}
