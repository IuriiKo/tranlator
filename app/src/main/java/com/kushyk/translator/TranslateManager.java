package com.kushyk.translator;

import com.kushyk.translator.util.NetUtil;

import java.util.List;

import rx.Observable;

/**
 * Created by Iurii Kushyk on 29.12.2016.
 */

public class TranslateManager {
    private OnlineTranslatorService onlineTranslateService;
    private OfflineTranslatorService offlineTranslateService;
    private boolean isConnectedOld;

    public TranslateManager(OnlineTranslatorService onlineTranslateService, OfflineTranslatorService offlineTranslateService) {
        this.onlineTranslateService = onlineTranslateService;
        this.offlineTranslateService = offlineTranslateService;
    }

    public Observable<String[]> translate(String text,String currentLanguage, String language) {
        return getTranslatorService().map(service -> service.translate(text, currentLanguage, language).toBlocking().first());
    }

    public  Observable<List<String>> getLanguages() {
        return getTranslatorService().flatMap(TranslatorService::getLanguages);
    }

    private Observable<TranslatorService> getTranslatorService() {
        return Observable.fromCallable(()->{
            boolean isConnected = NetUtil.isNetworkConnected();
            if (isConnected != isConnectedOld) {
                isConnectedOld = isConnected;
                throw new ReconnectException();
            }
            isConnectedOld = isConnected;
            return isConnected ? onlineTranslateService : offlineTranslateService;
        });
    }
}
