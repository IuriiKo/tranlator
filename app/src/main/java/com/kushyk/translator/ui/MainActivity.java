package com.kushyk.translator.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.translate.Translate;
import com.google.api.services.translate.model.TranslationsListResponse;
import com.google.api.services.translate.model.TranslationsResource;
import com.kushyk.translator.App;
import com.kushyk.translator.R;
import com.kushyk.translator.remote.response.TranslateDetectResponse;
import com.kushyk.translator.util.RestQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.Unbinder;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.ActionN;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = MainActivity.class.getName();
    @BindView(R.id.inLanguageView)
    Spinner inLanguageView;
    @BindView(R.id.outLanguageView)
    Spinner outLanguageView;
    @BindView(R.id.translateIconView)
    ImageView translateIconView;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
    }


    @OnEditorAction(R.id.translateEditView)
    public boolean onEditorAction(TextView view, int actionId, KeyEvent event){
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            translate(view.getText().toString());
            return true;
        }
        return false;
    }

    private void translate(String text) {

        Observable.fromCallable(()->{
            TranslationsListResponse response = null;
            try {
                Translate t = new Translate.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory() , null)
                        //Need to update this to your App-Name
                        .setApplicationName("youtube")

                        .build();
                Translate.Translations.List list = t.new Translations().list(
                        Arrays.asList(
                                "Hello World",
                                "How to use Google Translate from Java"),
                        "UA");
                list.setKey(RestQuery.API_KEY);

                response = list.execute();
                for(TranslationsResource tr : response.getTranslations()) {
                    Log.d(LOG_TAG, tr.getTranslatedText());
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "translate", e);
            }
            return response;
        })
//        App.service.getTranslateService().detect(text)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuccessTranslate, this::onErrorTranslate);
    }

    private void onSuccessTranslate(TranslationsListResponse translateDetectResponse) {
        Log.d(LOG_TAG, translateDetectResponse.toString());
    }

    private void onErrorTranslate(Throwable throwable) {
        Log.e(LOG_TAG, "onErrorTranslate()",throwable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }


}
