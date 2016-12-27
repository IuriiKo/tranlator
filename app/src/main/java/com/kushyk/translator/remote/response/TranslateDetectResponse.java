package com.kushyk.translator.remote.response;

import com.kushyk.translator.entity.Data;

/**
 * Created by Iurii Kushyk on 26.12.2016.
 */
public class TranslateDetectResponse {
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
