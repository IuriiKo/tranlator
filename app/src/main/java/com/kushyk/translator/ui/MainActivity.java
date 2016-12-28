package com.kushyk.translator.ui;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.services.translate.Translate;
import com.google.api.services.translate.model.LanguagesListResponse;
import com.google.api.services.translate.model.LanguagesResource;
import com.google.api.services.translate.model.TranslationsListResponse;
import com.google.common.collect.Lists;
import com.kushyk.translator.R;
import com.kushyk.translator.util.RestQuery;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.Unbinder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = MainActivity.class.getName();
    @BindView(R.id.inLanguageView)
    Spinner inLanguageView;
    @BindView(R.id.outLanguageView)
    Spinner outLanguageView;
    private Unbinder unbinder;
    private Translate translate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        initTranslate();
        getLanguages();
    }

    private void initTranslate() {
        translate = new Translate.Builder(
                AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory() , null)
                .build();
    }

    @OnEditorAction(R.id.translateEditView)
    public boolean onEditorAction(TextView view, int actionId, KeyEvent event){
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            translate(view.getText().toString(), getTargetLanguageCode());
            return true;
        }
        return false;
    }

    private String getTargetLanguageCode() {
        return outLanguageView.getSelectedItem().toString();
    }

    private void translate(String text, String targetLanguage) {
        if (text.isEmpty()) {
            showToast(R.string.put_some_text);
            return;
        }
        Observable.fromCallable(()->{
                Translate.Translations.List list = translate.translations().list(
                        Arrays.asList(text),
                        targetLanguage);
                list.setKey(RestQuery.API_KEY);

            return list.execute();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuccessTranslate, this::onErrorTranslate);
    }

    private void onSuccessTranslate(TranslationsListResponse translationsListResponse) {
        Log.d(LOG_TAG, translationsListResponse.toString());
        new AlertDialog.Builder(this).setMessage(translationsListResponse.getTranslations().get(0).getTranslatedText()).create().show();
    }

    private void onErrorTranslate(Throwable throwable) {
        Log.e(LOG_TAG, "onErrorTranslate()",throwable);
        showToast(R.string.cant_translate);
    }

    private void getLanguages() {
        Observable.fromCallable(()->{
            Translate.Languages.List list = translate.languages().list();
            list.setKey(RestQuery.API_KEY);
            return list.execute();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuccessGetLanguage, this::onErrorGetLanguage);
    }

    private void onSuccessGetLanguage(LanguagesListResponse languagesListResponse) {
        fillSpinners(languagesListResponse);
    }

    private void onErrorGetLanguage(Throwable throwable) {
        Log.e(LOG_TAG, "onErrorTranslate()",throwable);
        showToast(R.string.server_error);
    }

    @OnClick(R.id.translateIconView)
    public void changeLanguage() {
        int outPosition = outLanguageView.getSelectedItemPosition();
        int inPosition = inLanguageView.getSelectedItemPosition();

        outLanguageView.setSelection(inPosition);
        inLanguageView.setSelection(outPosition);
    }

    private void fillSpinners(LanguagesListResponse languagesListResponse) {
        List<String> languages = Lists.transform(languagesListResponse.getLanguages(), LanguagesResource::getLanguage);

        ArrayAdapter<String> adapterIn = new ArrayAdapter(this, android.R.layout.simple_spinner_item, languages);
        ArrayAdapter<String> adapterOut = new ArrayAdapter(this, android.R.layout.simple_spinner_item, languages);

        inLanguageView.setAdapter(adapterIn);
        inLanguageView.setSelection(0);
        outLanguageView.setAdapter(adapterOut);
        outLanguageView.setSelection(0);
    }

    private void showToast(@StringRes int textResId) {
        Snackbar.make(outLanguageView, textResId, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
