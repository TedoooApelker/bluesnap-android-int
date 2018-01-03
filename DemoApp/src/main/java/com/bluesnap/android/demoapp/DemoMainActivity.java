package com.bluesnap.android.demoapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bluesnap.androidapi.BluesnapCheckoutActivity;
import com.bluesnap.androidapi.models.BillingInfo;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.models.SdkResult;
import com.bluesnap.androidapi.models.ShippingInfo;
import com.bluesnap.androidapi.services.AndroidUtil;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.BluesnapAlertDialog;
import com.bluesnap.androidapi.services.BluesnapServiceCallback;
import com.bluesnap.androidapi.services.TokenProvider;
import com.bluesnap.androidapi.services.TokenServiceCallback;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.Currency;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;

import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_PASS;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_TOKEN_CREATION;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_URL;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_USER;

public class DemoMainActivity extends Activity {

    private static final String TAG = "DemoMainActivity";
    private static final int HTTP_MAX_RETRIES = 2;
    private static final int HTTP_RETRY_SLEEP_TIME_MILLIS = 3750;
    private static Context context;
    protected BlueSnapService bluesnapService;
    protected TokenProvider tokenProvider;
    private Spinner ratesSpinner;
    private Spinner merchantStoreCurrencySpinner;
    private EditText returningShopperEditText;
    private String returningOrNewShopper = "";
    private EditText productPriceEditText;
    private Currency currency;
    private TextView currencySym;
    private String currencySymbol;
    private String initialPrice;
    private String displayedCurrency;
    private String currencyName;
    private SdkRequest sdkRequest;
    private String merchantToken;
    private Currency currencyByLocale;
    private ProgressBar progressBar;
    private LinearLayout linearLayoutForProgressBar;
    private Switch shippingSwitch;
    private Switch billingSwitch;
    private Switch emailSwitch;
    private EditText taxAmountEditText;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        linearLayoutForProgressBar = (LinearLayout) findViewById(R.id.mainLinearLayout);
        linearLayoutForProgressBar.setVisibility(View.INVISIBLE);
        progressBar = (ProgressBar) findViewById(R.id.progressBarMerchant);
        shippingSwitch = (Switch) findViewById(R.id.shippingSwitch);
        shippingSwitch.setChecked(false);
        billingSwitch = (Switch) findViewById(R.id.billingSwitch);
        billingSwitch.setChecked(false);
        emailSwitch = (Switch) findViewById(R.id.emailSwitch);
        emailSwitch.setChecked(false);
        progressBar.setVisibility(View.VISIBLE);
        productPriceEditText = (EditText) findViewById(R.id.productPriceEditText);
        taxAmountEditText = (EditText) findViewById(R.id.demoTaxEditText);
        currencySym = (TextView) findViewById(R.id.currencySym);
        ratesSpinner = (Spinner) findViewById(R.id.rateSpinner);
        merchantStoreCurrencySpinner = (Spinner) findViewById(R.id.merchantStoreCurrencySpinner);
        showDemoAppVersion();
        try {
            Locale current = getResources().getConfiguration().locale;
            currencyByLocale = Currency.getInstance(current);
        } catch (Exception e) {
            currencyByLocale = Currency.getInstance("USD");
        }

        context = getBaseContext();
        bluesnapService = BlueSnapService.getInstance();

        generateMerchantToken();
        returningShopperEditText = (EditText) findViewById(R.id.returningShopperEditText);
        returningShopperEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 8) {
                    returningOrNewShopper = "?shopperId=" + s;
                    generateMerchantToken(); // 22232799
                }
            }
        });
    }

    private void showDemoAppVersion() {
        TextView demoVersionTextView = (TextView) findViewById(R.id.demoVersionTextView);
        try {
            int versionCode = BuildConfig.VERSION_CODE;
            String versionName = BuildConfig.VERSION_NAME;
            demoVersionTextView.setText(String.format(Locale.ENGLISH, "V:%s[%d]", versionName, versionCode));
        } catch (Exception e) {
            Log.e(TAG, "cannot extract version");
        }
    }

    private void ratesAdapterSelectionListener() {
        merchantStoreCurrencySpinner.post(new Runnable() {
            @Override
            public void run() {
                merchantStoreCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        progressBar.setVisibility(View.VISIBLE);
                        if (initialPrice == null && position != 0) {
                            initialPrice = productPriceEditText.getText().toString();
                        }
                        String selectedRateName = merchantStoreCurrencySpinner.getSelectedItem().toString();
                        String convertedPrice = readCurencyFromSpinner(selectedRateName);
                        if (convertedPrice == null) return;
                        //Avoid Rotation renew
                        if (selectedRateName.equals(displayedCurrency)) {
                            return;
                        }
                        displayedCurrency = currency.getCurrencyCode();
                        if (convertedPrice.equals("0")) {
                            productPriceEditText.setHint("0");
                        } else {
                            if (currency != null) {
                                currencySymbol = currency.getSymbol();
                                currencySym.setText(currencySymbol);
                                currencyName = currency.getCurrencyCode();
                            }
                            productPriceEditText.setText(convertedPrice);
                        }
                        initControlsAfterToken();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        });
    }

    private String readCurencyFromSpinner(String selectedRateName) {
        currency = Currency.getInstance(selectedRateName);
        String convertedPrice = "0";
        currencySymbol = currency.getSymbol();
        currencyName = currency.getCurrencyCode();
        if (initialPrice == null) {
            initialPrice = productPriceEditText.getText().toString().trim();
        }
        convertedPrice = bluesnapService.convertUSD(initialPrice, selectedRateName).trim();
        return convertedPrice;
    }

    private void showDialog(String message) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(message);
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            try {
                dialog.show();
            } catch (Exception e) {
                Log.d(TAG, "Dialog cannot be shown", e);
            }
        } catch (Exception e) {
            Log.d(TAG, "Dialog cannot be shown", e);
        }
    }

    private void updateSpinnerAdapterFromRates(final Set<String> supportedRates) {
        String[] quotesArray = new String[supportedRates.size()];
        supportedRates.toArray(quotesArray);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_view, quotesArray);
        ratesSpinner.setAdapter(adapter);
        merchantStoreCurrencySpinner.setAdapter(adapter);
        int currentposition = 0;
        for (String rate : quotesArray) {
            if (rate.equals(currencyByLocale.getCurrencyCode())) {
                break;
            }
            currentposition++;
        }
        ratesSpinner.setSelection(currentposition);
        merchantStoreCurrencySpinner.setSelection(currentposition);
        ratesAdapterSelectionListener();
    }

    public void onPaySubmit(View view) {

        String productPriceStr = AndroidUtil.stringify(productPriceEditText.getText());
        if (TextUtils.isEmpty(productPriceStr)) {
            Toast.makeText(getApplicationContext(), "null payment", Toast.LENGTH_LONG).show();
            return;
        }

        Double productPrice = Double.valueOf(productPriceStr);
        if (productPrice <= 0D) {
            Toast.makeText(getApplicationContext(), "0 payment", Toast.LENGTH_LONG).show();
            return;
        }

        readCurencyFromSpinner(ratesSpinner.getSelectedItem().toString());
        String taxString = taxAmountEditText.getText().toString().trim();
        Double taxAmountPrecentage = 0D;
        if (!taxString.isEmpty()) {
            taxAmountPrecentage = Double.valueOf(taxAmountEditText.getText().toString().trim());
        }
        // You can set the Amout solely
        sdkRequest = new SdkRequest(productPrice, ratesSpinner.getSelectedItem().toString());

        // Or you can set the Amount with tax, this will override setAmount()
        // The total purchase amount will be the sum of both numbers
        if (taxAmountPrecentage > 0D) {
            sdkRequest.setAmountWithTax(productPrice, productPrice * (taxAmountPrecentage / 100));
        } else {
            sdkRequest.setAmountNoTax(productPrice);
        }


        sdkRequest.setCustomTitle("Demo Merchant");

        if (shippingSwitch.isChecked()) {
            sdkRequest.setShippingRequired(true);
        }
        if (billingSwitch.isChecked()) {
            sdkRequest.setBillingRequired(true);
        }
        if (emailSwitch.isChecked()) {
            sdkRequest.setEmailRequired(true);
        }
        try {
            sdkRequest.verify();
        } catch (BSPaymentRequestException e) {
            showDialog("SdkRequest error:" + e.getMessage());
            Log.d(TAG, sdkRequest.toString());
            finish();
        }

        try {
            bluesnapService.setSdkRequest(sdkRequest);
            Intent intent = new Intent(getApplicationContext(), BluesnapCheckoutActivity.class);
            startActivityForResult(intent, BluesnapCheckoutActivity.REQUEST_CODE_DEFAULT);
        } catch (BSPaymentRequestException e) {
            Log.e(TAG, "payment request not validated: ", e);
            finish();
        }
    }

    private void merchantTokenService(final TokenServiceInterface tokenServiceInterface) {
        final AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.setMaxRetriesAndTimeout(HTTP_MAX_RETRIES, HTTP_RETRY_SLEEP_TIME_MILLIS);
        httpClient.setBasicAuth(SANDBOX_USER, SANDBOX_PASS);
        httpClient.post(SANDBOX_URL + SANDBOX_TOKEN_CREATION + returningOrNewShopper, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG, responseString, throwable);
                tokenServiceInterface.onServiceFailure();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                merchantToken = DemoTransactions.extractTokenFromHeaders(headers);
                tokenServiceInterface.onServiceSuccess();
            }

        });
    }

    //TODO: Find a mock merchant service t¡o provide this
    private void generateMerchantToken() {

        // create the interface for activating the token creation from server
        tokenProvider = new TokenProvider() {
            @Override
            public void getNewToken(final TokenServiceCallback tokenServiceCallback) {

                merchantTokenService(new TokenServiceInterface() {
                    @Override
                    public void onServiceSuccess() {
                        //change the expired token
                        tokenServiceCallback.complete(merchantToken);
                    }

                    @Override
                    public void onServiceFailure() {

                    }
                });
            }
        };

        progressBar.setVisibility(View.VISIBLE);

        merchantTokenService(new TokenServiceInterface() {
            @Override
            public void onServiceSuccess() {
                initControlsAfterToken();
            }

            @Override
            public void onServiceFailure() {
                BluesnapAlertDialog.setDialog(DemoMainActivity.this, "Cannot obtain token from merchant server", "Service error", new BluesnapAlertDialog.BluesnapDialogCallback() {
                    @Override
                    public void setPositiveDialog() {
                        finish();
                    }

                    @Override
                    public void setNegativeDialog() {
                        generateMerchantToken();
                    }
                }, "Close", "Retry");
            }
        });
    }

    private void initControlsAfterToken() {
        final String merchantStoreCurrency = (null == currency || null == currency.getCurrencyCode()) ? "USD" : currency.getCurrencyCode();
        bluesnapService.setup(merchantToken, tokenProvider, merchantStoreCurrency, getApplicationContext(), new BluesnapServiceCallback() {
            @Override
            public void onSuccess() {
                Set<String> supportedRates = bluesnapService.getSupportedRates();
                if (null == currency || null == currency.getCurrencyCode())
                    updateSpinnerAdapterFromRates(demoSupportedRates(supportedRates));
                progressBar.setVisibility(View.INVISIBLE);
                linearLayoutForProgressBar.setVisibility(View.VISIBLE);
                productPriceEditText.setVisibility(View.VISIBLE);
                productPriceEditText.requestFocus();
            }

            @Override
            public void onFailure() {
                showDialog("unable to get rates quote from service");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            if (data != null) {
                String sdkErrorMsg = "SDK Failed to process the request:";
                sdkErrorMsg += data.getStringExtra(BluesnapCheckoutActivity.SDK_ERROR_MSG);
                showDialog(sdkErrorMsg);
            } else {
                showDialog("Purchase canceled");
            }
            return;
        }

        // Here we can access the payment result
        Bundle extras = data.getExtras();
        SdkResult sdkResult = data.getParcelableExtra(BluesnapCheckoutActivity.EXTRA_PAYMENT_RESULT); //TODO: why??? the change????? why???

        //Start a demo activity that shows purchase summary.
        Intent intent = new Intent(getApplicationContext(), PostPaymentActivity.class);
        intent.putExtra("MERCHANT_TOKEN", merchantToken);
        intent.putExtra(BluesnapCheckoutActivity.EXTRA_PAYMENT_RESULT, sdkResult);

        // If shipping information is available show it, Here we simply log the shipping info.
        ShippingInfo shippingInfo = (ShippingInfo) extras.get(BluesnapCheckoutActivity.EXTRA_SHIPPING_DETAILS);
        if (shippingInfo != null) {
            Log.d(TAG, "ShippingInfo " + shippingInfo.toString());
            intent.putExtra(BluesnapCheckoutActivity.EXTRA_SHIPPING_DETAILS, shippingInfo);
        }

        // If billing information is available show it, Here we simply log the billing info.
        BillingInfo billingInfo = (BillingInfo) extras.get(BluesnapCheckoutActivity.EXTRA_BILLING_DETAILS);
        if (billingInfo != null) {
            Log.d(TAG, "BillingInfo " + billingInfo.toString());
            intent.putExtra(BluesnapCheckoutActivity.EXTRA_BILLING_DETAILS, billingInfo);
        }

        startActivity(intent);

        //Recreate the demo activity
        merchantToken = null;
        recreate();
    }

    public String getMerchantToken() {
        if (merchantToken == null) {

            generateMerchantToken();
        }
        return merchantToken;
    }

    /**
     * We only show a subset of all available rates in our demo app.
     *
     * @param supportedRates
     * @return
     */

    private TreeSet<String> demoSupportedRates(Set<String> supportedRates) {
        TreeSet<String> treeSet = new TreeSet();
        if (supportedRates.contains("USD")) {
            treeSet.add("USD");
        }
        if (supportedRates.contains("CAD")) {
            treeSet.add("CAD");
        }
        if (supportedRates.contains("EUR")) {
            treeSet.add("EUR");
        }
        if (supportedRates.contains("GBP")) {
            treeSet.add("GBP");
        }
        if (supportedRates.contains("ILS")) {
            treeSet.add("ILS");
        }
        return treeSet;
    }

    public BlueSnapService getBluesnapService() {
        return bluesnapService;
    }
}
