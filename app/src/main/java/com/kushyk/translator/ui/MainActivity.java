package com.kushyk.translator.ui;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kushyk.translator.OfflineTranslatorService;
import com.kushyk.translator.R;
import com.kushyk.translator.ReconnectException;
import com.kushyk.translator.TranslateManager;
import com.kushyk.translator.OnlineTranslatorService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.Unbinder;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = MainActivity.class.getName();
    @BindView(R.id.inLanguageView)
    Spinner inLanguageView;
    @BindView(R.id.outLanguageView)
    Spinner outLanguageView;
    private Unbinder unbinder;
    private TranslateManager translateManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        translateManager = new TranslateManager(new OnlineTranslatorService(),new OfflineTranslatorService());
        getLanguages();
    }

    @OnEditorAction(R.id.translateEditView)
    public boolean onEditorAction(TextView view, int actionId, KeyEvent event){
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            translate(view.getText().toString(),getCurrentLanguageCode(), getTargetLanguageCode());
            return true;
        }
        return false;
    }

    private String getTargetLanguageCode() {
        return outLanguageView.getSelectedItem().toString();
    }

    private String getCurrentLanguageCode() {
        return inLanguageView.getSelectedItem().toString();
    }

    private void translate(String text, String currentLanguage, String targetLanguage) {
        if (text.isEmpty()) {
            showToast(R.string.put_some_text);
            return;
        }
        translateManager.translate(text, currentLanguage, targetLanguage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuccessTranslate, this::onErrorTranslate);
    }

    private void onSuccessTranslate(String[] translateTexts) {
        Log.d(LOG_TAG, translateTexts.toString());
        if (translateTexts.length == 0) {
            showToast(getString(R.string.no_translation));
            return;
        }
        showToast(translateTexts[0]);
    }

    private void onErrorTranslate(Throwable throwable) {
        Log.e(LOG_TAG, "onErrorTranslate()",throwable);
        showToast(R.string.cant_translate);
        checkReconection(throwable);
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private void getLanguages() {
        translateManager.getLanguages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuccessGetLanguage, this::onErrorGetLanguage);
    }

    private void onSuccessGetLanguage(List<String> languages) {
        fillSpinners(languages);
    }

    private void onErrorGetLanguage(Throwable throwable) {
        Log.e(LOG_TAG, "onErrorTranslate()",throwable);
        if (!(throwable instanceof ReconnectException)) {
            showToast(R.string.server_error);
        }
        checkReconection(throwable);
    }

    @OnClick(R.id.translateIconView)
    public void changeLanguage() {
        int outPosition = outLanguageView.getSelectedItemPosition();
        int inPosition = inLanguageView.getSelectedItemPosition();

        outLanguageView.setSelection(inPosition);
        inLanguageView.setSelection(outPosition);
    }

    private void fillSpinners(List<String> languages) {

        ArrayAdapter<String> adapterIn = new ArrayAdapter(this, android.R.layout.simple_spinner_item, languages);
        ArrayAdapter<String> adapterOut = new ArrayAdapter(this, android.R.layout.simple_spinner_item, languages);

        inLanguageView.setAdapter(adapterIn);
        inLanguageView.setSelection(0);
        outLanguageView.setAdapter(adapterOut);
        outLanguageView.setSelection(1);
    }

    private void showToast(@StringRes int textResId) {
        Snackbar.make(outLanguageView, textResId, Snackbar.LENGTH_LONG).show();
    }

    private void checkReconection(Throwable throwable) {
        if (throwable instanceof ReconnectException) {
            getLanguages();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
