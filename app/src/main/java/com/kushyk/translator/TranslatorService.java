package com.kushyk.translator;

import java.util.List;

import rx.Observable;

/**
 * Created by Iurii Kushyk on 29.12.2016.
 */
public interface TranslatorService {
     Observable<String[]> translate(String text,String currentLanguage, String targetLanguage);
     Observable<List<String>> getLanguages();
}
