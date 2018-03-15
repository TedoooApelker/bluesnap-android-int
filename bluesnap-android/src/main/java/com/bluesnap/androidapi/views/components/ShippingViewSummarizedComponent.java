package com.bluesnap.androidapi.views.components;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.ShippingInfo;
import com.bluesnap.androidapi.services.BlueSnapLocalBroadcastManager;

/**
 * Created by roy.biber on 20/02/2018.
 */

public class ShippingViewSummarizedComponent extends ContactInfoViewSummarizedComponent {
    public Button editButton;

    public ShippingViewSummarizedComponent(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ShippingViewSummarizedComponent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ShippingViewSummarizedComponent(Context context) {
        super(context);
    }

    public void updateResource(ShippingInfo shippingInfo) {
        super.updateResource(shippingInfo);
        setEmailVisibility(GONE);
    }

    @Override
    void initControl(final Context context) {
        super.initControl(context);

        editButton = (Button) findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlueSnapLocalBroadcastManager.sendMessage(context, BlueSnapLocalBroadcastManager.SUMMARIZED_SHIPPING_EDIT, TAG);
            }
        });
    }
}