package com.bluesnap.android.demoapp.ShopperConfigUITests;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.lifecycle.ActivityLifecycleCallback;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.support.test.uiautomator.UiDevice;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.ContactInfoTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CreditCardLineTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutEspressoBasedTester;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutReturningShopperTests.ReturningShoppersFactory;
import com.bluesnap.android.demoapp.CreateVaultedShopperInterface;
import com.bluesnap.android.demoapp.GetShopperServiceInterface;
import com.bluesnap.android.demoapp.R;
import com.bluesnap.android.demoapp.RandomTestValuesGenerator;
import com.bluesnap.android.demoapp.TestUtils;
import com.bluesnap.android.demoapp.TestingShopperContactInfo;
import com.bluesnap.android.demoapp.TestingShopperCreditCard;
import com.bluesnap.android.demoapp.TokenServiceInterface;
import com.bluesnap.androidapi.Constants;
import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.http.CustomHTTPParams;
import com.bluesnap.androidapi.http.HTTPOperationController;
import com.bluesnap.androidapi.models.PriceDetails;
import com.bluesnap.androidapi.models.SDKConfiguration;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.models.SdkRequestBase;
import com.bluesnap.androidapi.models.SdkRequestShopperRequirements;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.BluesnapServiceCallback;
import com.bluesnap.androidapi.services.TaxCalculator;
import com.bluesnap.androidapi.services.TokenProvider;
import com.bluesnap.androidapi.services.TokenServiceCallback;
import com.bluesnap.androidapi.views.activities.BluesnapCheckoutActivity;
import com.bluesnap.androidapi.views.activities.BluesnapChoosePaymentMethodActivity;

import junit.framework.Assert;

import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_PASS;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_TOKEN_CREATION;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_URL;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_USER;
import static com.bluesnap.androidapi.utils.JsonParser.getOptionalString;
import static java.lang.Thread.sleep;
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.Matchers.containsString;

/**
 * Created by sivani on 30/08/2018.
 */

public class ChoosePaymentMethodEspressoBasedTester {
    protected static final String TAG = CheckoutEspressoBasedTester.class.getSimpleName();
    protected BlueSnapService blueSnapService = BlueSnapService.getInstance();
    private SDKConfiguration sDKConfiguration = null;
    //    protected NumberFormat df;
    protected RandomTestValuesGenerator randomTestValuesGenerator = new RandomTestValuesGenerator();

    protected String defaultCountryKey;
    protected String defaultCountryValue;
    protected String checkoutCurrency = "USD";
    protected double purchaseAmount = randomTestValuesGenerator.randomDemoAppPrice();
    //    protected double roundedPurchaseAmount = TestUtils.round_amount(purchaseAmount);
    private double taxPercent = randomTestValuesGenerator.randomTaxPercentage() / 100;
    protected double taxAmount = purchaseAmount * taxPercent;

    protected ReturningShoppersFactory.TestingShopper returningShopper;

    private boolean isSdkRequestNull = false;

    private static final String SANDBOX_VAULTED_SHOPPER = "vaulted-shoppers";
    protected String shopperId;
    private String getShopperResponse;
    private String emailFromServer;

    private HttpURLConnection myURLConnection;
    private String merchantToken;

    public Context applicationContext;

    private List<CustomHTTPParams> sahdboxHttpHeaders = getHttpParamsForSandboxTests();

    private boolean createShopperSucceed = false;
    private String createVaultedShopperResponse;
    protected String vaultedShopperId;

    ChoosePaymentMethodEspressoBasedTester() {
        setUrlConnection("");
    }

    ChoosePaymentMethodEspressoBasedTester(String returningShopperId) {
        setUrlConnection("?shopperId=" + returningShopperId);
    }

    private void setUrlConnection(String returningOrNewShopper) {
        try {
            URL myURL = new URL(SANDBOX_URL + SANDBOX_TOKEN_CREATION + returningOrNewShopper);
            myURLConnection = (HttpURLConnection) myURL.openConnection();
        } catch (IOException e) {
            fail("Network error open server connection:" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Rule
    public ActivityTestRule<BluesnapChoosePaymentMethodActivity> mActivityRule = new ActivityTestRule<>(
            BluesnapChoosePaymentMethodActivity.class, false, false);

    private BluesnapCheckoutActivity mActivity;


    protected void preSetup(boolean openURL, final boolean withFullBilling, final boolean withEmail, final boolean withShipping) throws JSONException, BSPaymentRequestException, InterruptedException {
        SdkRequestShopperRequirements sdkRequest = new SdkRequestShopperRequirements(withFullBilling, withEmail, withShipping);
        if (withFullBilling)
            sdkRequest.getShopperCheckoutRequirements().setBillingRequired(true);

        if (withEmail)
            sdkRequest.getShopperCheckoutRequirements().setEmailRequired(true);

        if (withShipping)
            sdkRequest.getShopperCheckoutRequirements().setShippingRequired(true);

        if (openURL) {
            createVaultedShopper();
            setupAndLaunch(sdkRequest, openURL, vaultedShopperId);
        } else
            setupAndLaunch(sdkRequest);

    }

    //    @Before
    private void doSetup() {
        try {
            wakeUpDeviceScreen();
        } catch (RemoteException e) {
            fail("Could not wake up device");
            e.printStackTrace();
        }
        //randomTestValuesGenerator = new RandomTestValuesGenerator();
        IdlingPolicies.setMasterPolicyTimeout(60, TimeUnit.SECONDS);
        IdlingPolicies.setIdlingResourceTimeout(60, TimeUnit.SECONDS);

        //Wake up device again in case token fetch took to much time
        try {
            wakeUpDeviceScreen();
        } catch (RemoteException e) {
            fail("Could not wake up device");
            e.printStackTrace();
        }
    }


    public void setupAndLaunch(SdkRequestBase sdkRequest) throws InterruptedException, BSPaymentRequestException {
        setupAndLaunch(sdkRequest, "USD", false, "");
    }

    public void setupAndLaunch(SdkRequestBase sdkRequest, boolean openURL, String returningShopperId) throws InterruptedException, BSPaymentRequestException {
        setupAndLaunch(sdkRequest, "USD", openURL, returningShopperId);
    }

    public void setupAndLaunch(SdkRequestBase sdkRequest, String merchantStoreCurrency, boolean openURL, String returningShopperId) throws InterruptedException, BSPaymentRequestException {
        if (openURL)
            setUrlConnection("?shopperId=" + returningShopperId);

        doSetup();
        sdkRequest.setTaxCalculator(new TaxCalculator() {
            @Override
            public void updateTax(String shippingCountry, String shippingState, PriceDetails priceDetails) {
                if ("us".equalsIgnoreCase(shippingCountry)) {
                    Double taxRate = taxPercent;
                    if ("ma".equalsIgnoreCase(shippingState)) {
                        taxRate = 0.1;
                    }
                    priceDetails.setTaxAmount(priceDetails.getSubtotalAmount() * taxRate);
                } else {
                    priceDetails.setTaxAmount(0D);
                }
            }
        });

        setSDKToken(merchantStoreCurrency);
        Intent intent = new Intent();
        blueSnapService.setSdkRequest(sdkRequest);
        mActivityRule.launchActivity(intent);
        mActivity = mActivityRule.getActivity();
        applicationContext = mActivity.getApplicationContext();
        defaultCountryKey = BlueSnapService.getInstance().getUserCountry(this.mActivity.getApplicationContext());
        String[] countryKeyArray = applicationContext.getResources().getStringArray(com.bluesnap.androidapi.R.array.country_key_array);
        String[] countryValueArray = applicationContext.getResources().getStringArray(com.bluesnap.androidapi.R.array.country_value_array);

        defaultCountryValue = countryValueArray[Arrays.asList(countryKeyArray).indexOf(defaultCountryKey)];
    }

    private void setSDKToken(String merchantStoreCurrency) throws InterruptedException {
        try {
            String userCredentials = SANDBOX_USER + ":" + SANDBOX_PASS;
            String basicAuth = "Basic " + new String(Base64.encode(userCredentials.getBytes(), 0));
            myURLConnection.setRequestProperty("Authorization", basicAuth);
            myURLConnection.setRequestMethod("POST");
            myURLConnection.connect();
            int responseCode = myURLConnection.getResponseCode();
            String locationHeader = myURLConnection.getHeaderField("Location");
            merchantToken = locationHeader.substring(locationHeader.lastIndexOf('/') + 1);
        } catch (IOException e) {
            fail("Network error obtaining token:" + e.getMessage());
            e.printStackTrace();
        }

        new Handler(Looper.getMainLooper())
                .post(new Runnable() {
                    @Override
                    public void run() {
                        final TokenProvider tokenProvider = new TokenProvider() {
                            @Override
                            public void getNewToken(final TokenServiceCallback tokenServiceCallback) {
                                new TokenServiceInterface() {
                                    @Override
                                    public void onServiceSuccess() {
                                        //change the expired token
                                        tokenServiceCallback.complete(merchantToken);
                                    }

                                    @Override
                                    public void onServiceFailure() {
                                    }
                                };
                            }
                        };
                        BlueSnapService.getInstance().setup(merchantToken, tokenProvider, merchantStoreCurrency, null, new BluesnapServiceCallback() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "Service finish setup");
                                isSdkRequestNull = true;
                            }

                            @Override
                            public void onFailure() {
                                fail("Service could not finish setup");
                                isSdkRequestNull = true;
                            }
                        });

                    }
                });
        while (BlueSnapService.getInstance().getBlueSnapToken() == null) {
            Log.d(TAG, "Waiting for token setup");
            sleep(200);

        }

        while (BlueSnapService.getInstance().getsDKConfiguration() == null || BlueSnapService.getInstance().getsDKConfiguration().equals(sDKConfiguration)) {
            Log.d(TAG, "Waiting for SDK configuration to finish");
            sleep(200);

        }

        sDKConfiguration = BlueSnapService.getInstance().getsDKConfiguration();

        while (!isSdkRequestNull) {
            Log.d(TAG, "Waiting for SDK request to finish");
            sleep(500);
        }

        isSdkRequestNull = false;
    }


    @NonNull
    List<CustomHTTPParams> getHttpParamsForSandboxTests() {
        String basicAuth = "Basic " + Base64.encodeToString((SANDBOX_USER + ":" + SANDBOX_PASS).getBytes(StandardCharsets.UTF_8), 0);
        List<CustomHTTPParams> headerParams = new ArrayList<>();
        headerParams.add(new CustomHTTPParams("Authorization", basicAuth));
        return headerParams;
    }

    public void checkToken() {
        try {
            onView(withText(containsString("Cannot obtain token"))).check(matches(isDisplayed()));
            fail("No token from server");
        } catch (NoMatchingViewException e) {
            //view not displayed logic
        }
    }

    //@After
    public void detectIfNoToken() {
        IdlingPolicies.setMasterPolicyTimeout(60, TimeUnit.SECONDS);
        IdlingPolicies.setIdlingResourceTimeout(60, TimeUnit.SECONDS);
        checkToken();

    }

    public void resetBeforeSetUp(boolean withFullBilling, boolean withEmail, boolean withShipping) throws BSPaymentRequestException, InterruptedException {
        String returningShopperId = "?shopperId=" + shopperId; //get the shopper id from last transaction
        //isReturningShopper = true;
        setUrlConnection(returningShopperId);
        purchaseAmount = randomTestValuesGenerator.randomDemoAppPrice();
        SdkRequest sdkRequest = new SdkRequest(purchaseAmount, checkoutCurrency);
        sdkRequest.getShopperCheckoutRequirements().setBillingRequired(withFullBilling);
        sdkRequest.getShopperCheckoutRequirements().setEmailRequired(withEmail);
        sdkRequest.getShopperCheckoutRequirements().setShippingRequired(withShipping);

        setupAndLaunch(sdkRequest);
    }

    private void wakeUpDeviceScreen() throws RemoteException {
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.wakeUp();
        ActivityLifecycleMonitorRegistry.getInstance().addLifecycleCallback(new ActivityLifecycleCallback() {
            @Override
            public void onActivityLifecycleChanged(Activity activity, Stage stage) {
                //if (stage == Stage.PRE_ON_CREATE) {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                // }
            }
        });
    }


    private void createVaultedShopper() throws JSONException {
        createVaultedShopperService(new CreateVaultedShopperInterface() {
            @Override
            public void onServiceSuccess() throws JSONException {
                JSONObject jsonObject = new JSONObject(createVaultedShopperResponse);
                vaultedShopperId = getOptionalString(jsonObject, "vaultedShopperId");
                createShopperSucceed = true;
            }

            @Override
            public void onServiceFailure() {
                fail("Cannot create shopper from merchant server");
            }
        });
    }

    void chooseNewCardPaymentMethod(final boolean withFullBilling, final boolean withEmail, final boolean withShipping,
                                    TestingShopperCreditCard creditCard, int cardIndex) throws InterruptedException {
        //choose new card
        onView(ViewMatchers.withId(R.id.newCardButton)).perform(click());
        ContactInfoTesterCommon.changeCountry(R.id.billingViewComponent, ContactInfoTesterCommon.billingContactInfo.getCountryValue());
        CreditCardLineTesterCommon.fillInCCLineWithValidCard(TestingShopperCreditCard.MASTERCARD_CREDIT_CARD);
        ContactInfoTesterCommon.fillInContactInfo(R.id.billingViewComponent, ContactInfoTesterCommon.billingContactInfo.getCountryKey(), withFullBilling, withEmail);
        //submit the choice
        onView(withId(R.id.buyNowButton)).perform(click());
        chosenPaymentMethodValidationInServer(withFullBilling, withEmail, withShipping, creditCard, cardIndex);

    }

    void chooseExistingCardPaymentMethod(final boolean withFullBilling, final boolean withEmail, final boolean withShipping,
                                         TestingShopperCreditCard creditCard, int cardIndex) throws InterruptedException {
        //choose existing credit card
        onData(anything()).inAdapterView(withId(R.id.oneLineCCViewComponentsListView)).atPosition(cardIndex).perform(click());

        onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(R.id.billingViewSummarizedComponent)))).perform(click());
        ContactInfoTesterCommon.changeCountry(R.id.billingViewComponent, ContactInfoTesterCommon.editBillingContactInfo.getCountryValue());
        ContactInfoTesterCommon.fillInContactInfo(R.id.billingViewComponent, ContactInfoTesterCommon.editBillingContactInfo.getCountryKey(), withFullBilling, withEmail);
        TestUtils.goBackToCreditCardInReturningShopper(true, R.id.returningShopperBillingFragmentButtonComponentView);
        //submit the choice
        onView(withId(R.id.buyNowButton)).perform(click());
        chosenPaymentMethodValidationInServer(withFullBilling, withEmail, withShipping, creditCard, cardIndex);

    }

    private void createVaultedShopperService(final CreateVaultedShopperInterface createVaultedShopper) throws JSONException {
        JSONObject body = createDataObject();
        BlueSnapHTTPResponse response = HTTPOperationController.post(SANDBOX_URL + SANDBOX_VAULTED_SHOPPER, body.toString(), "application/json", "application/json", sahdboxHttpHeaders);
        if (response.getResponseCode() >= 200 && response.getResponseCode() < 300) {
            createVaultedShopperResponse = response.getResponseString();
            createVaultedShopper.onServiceSuccess();
        } else {
            Log.e(TAG, response.getResponseCode() + " " + response.getErrorResponseString());
            createVaultedShopper.onServiceFailure();
        }
    }

    public JSONObject createDataObject() throws JSONException {
        JSONObject postData = new JSONObject();
        putJSON(postData, "firstName", "Fanny");
        putJSON(postData, "lastName", "Brice");

        return postData;
    }

    public void putJSON(JSONObject jsonObject, String key, String stringValue) {
        try {
            jsonObject.put(key, stringValue);
        } catch (JSONException e) {
            Log.e(TAG, "Error on putJSON " + e.getMessage());
        }
    }

    public void chosenPaymentMethodValidationInServer(final boolean withFullBilling, final boolean withEmail, final boolean withShipping,
                                                      TestingShopperCreditCard creditCard, int cardIndex) throws InterruptedException {
        while (!mActivity.isDestroyed()) {
            Log.d(TAG, "Waiting for tokenized credit card service to finish");
            sleep(1000);
        }

        get_shopper_from_server(withFullBilling, withShipping, withEmail, false, true, creditCard, cardIndex);
    }

    private void get_shopper_from_server(boolean isShopperConfig, TestingShopperCreditCard creditCard) {
        get_shopper_from_server(false, false, false, false, isShopperConfig, creditCard, 0);
    }

    private void get_shopper_from_server(final boolean withFullBilling, final boolean withEmail, final boolean withShipping, final boolean shippingSameAsBilling) {
        get_shopper_from_server(withFullBilling, withEmail, withShipping, shippingSameAsBilling, false, null, 0);
    }

    private void get_shopper_from_server(final boolean withFullBilling, final boolean withEmail, final boolean withShipping, final boolean shippingSameAsBilling,
                                         boolean isShopperConfig, TestingShopperCreditCard creditCard, int cardIndex) {
        get_shopper_service(new GetShopperServiceInterface() {
            @Override
            public void onServiceSuccess() {
                if (isShopperConfig)
                    shopper_chosen_payment_method_validation(creditCard);
                shopper_info_saved_validation(withFullBilling, withEmail, withShipping, shippingSameAsBilling, cardIndex);
            }

            @Override
            public void onServiceFailure() {
                fail("Cannot obtain shopper info from merchant server");
            }
        });
    }


    private void get_shopper_service(final GetShopperServiceInterface getShopperServiceInterface) {
        BlueSnapHTTPResponse response = HTTPOperationController.get(SANDBOX_URL + SANDBOX_VAULTED_SHOPPER + "/" + shopperId, "application/json", "application/json", sahdboxHttpHeaders);
        if (response.getResponseCode() >= 200 && response.getResponseCode() < 300) {
            getShopperResponse = response.getResponseString();
            getShopperServiceInterface.onServiceSuccess();
        } else {
            Log.e(TAG, response.getResponseCode() + " " + response.getErrorResponseString());
            getShopperServiceInterface.onServiceFailure();
        }
    }

    private void shopper_chosen_payment_method_validation(TestingShopperCreditCard creditCard) {
        try {
            JSONObject jsonObject = new JSONObject(getShopperResponse);

            JSONObject jsonObjectChosenPaymentMethod = jsonObject.getJSONObject("chosenPaymentMethod");
            check_if_field_identify("chosenPaymentMethodType", "CC", jsonObjectChosenPaymentMethod);

            JSONObject jsonObjectCreditCard = jsonObjectChosenPaymentMethod.getJSONObject("creditCard");
            check_if_field_identify("cardLastFourDigits", creditCard.getCardLastFourDigits(), jsonObjectCreditCard);
            check_if_field_identify("cardType", creditCard.getCardType(), jsonObjectCreditCard);
//            check_if_field_identify("cardSubType", creditCard.getCardSubType(), jsonObjectCreditCard);
            check_if_field_identify("expirationMonth", Integer.toString(creditCard.getExpirationMonth()), jsonObjectCreditCard);
            check_if_field_identify("expirationYear", Integer.toString(creditCard.getExpirationYear()), jsonObjectCreditCard);

        } catch (JSONException e) {
            e.printStackTrace();
            fail("Error on parse shopper info");
        } catch (Exception e) {
            e.printStackTrace();
            fail("missing field in server response:\n Expected fieldName: email" + "\n" + getShopperResponse);
        }
    }

    private void shopper_info_saved_validation(boolean withFullBilling, boolean withEmail, boolean withShipping, boolean shippingSameAsBilling, int cardIndex) {
        try {
            JSONObject jsonObject = new JSONObject(getShopperResponse);
            if (withEmail)
                emailFromServer = getOptionalString(jsonObject, "email");

            JSONObject jsonObjectPaymentSources = jsonObject.getJSONObject("paymentSources");
            JSONArray creditCardInfoJsonArray = jsonObjectPaymentSources.getJSONArray("creditCardInfo");
            JSONObject jsonObjectBillingContactInfo = creditCardInfoJsonArray.getJSONObject(cardIndex).getJSONObject("billingContactInfo");

            new_shopper_component_info_saved_validation(withFullBilling, withEmail, shippingSameAsBilling, true, jsonObjectBillingContactInfo);
            if (withShipping)
                new_shopper_component_info_saved_validation(true, withEmail, shippingSameAsBilling, false, jsonObject.getJSONObject("shippingContactInfo"));

        } catch (JSONException e) {
            e.printStackTrace();
            fail("Error on parse shopper info");
        } catch (Exception e) {
            e.printStackTrace();
            fail("missing field in server response:\n Expected fieldName: email" + "\n" + getShopperResponse);
        }
    }

    private void new_shopper_component_info_saved_validation(boolean fullInfo, boolean withEmail, boolean shippingSameAsBilling, boolean isBillingInfo, JSONObject jsonObject) {
        String countryKey;
        TestingShopperContactInfo contactInfo;

        contactInfo = (!isBillingInfo && !shippingSameAsBilling) ? ContactInfoTesterCommon.shippingContactInfo : ContactInfoTesterCommon.billingContactInfo;
        countryKey = contactInfo.getCountryKey();

        check_if_field_identify("country", countryKey.toLowerCase(), jsonObject);

        check_if_field_identify("firstName", contactInfo.getFirstName(), jsonObject);
        check_if_field_identify("lastName", contactInfo.getLastName(), jsonObject);

        if (isBillingInfo && withEmail)
            check_if_field_identify("email", contactInfo.getEmail(), jsonObject);

        if (!Arrays.asList(Constants.COUNTRIES_WITHOUT_ZIP).contains(countryKey))
            check_if_field_identify("zip", contactInfo.getZip(), jsonObject);

        if (fullInfo || !isBillingInfo) { //full info or shipping
            if (countryKey.equals("US") || countryKey.equals("CA") || countryKey.equals("BR")) {
                if (countryKey.equals("US"))
                    check_if_field_identify("state", "NY", jsonObject);
                else if (countryKey.equals("CA"))
                    check_if_field_identify("state", "QC", jsonObject);
                else
                    check_if_field_identify("state", "RJ", jsonObject);
            }
            check_if_field_identify("city", contactInfo.getCity(), jsonObject);
            check_if_field_identify("address1", contactInfo.getAddress(), jsonObject);
        }
    }

    private void check_if_field_identify(String fieldName, String expectedResult, JSONObject shopperInfoJsonObject) {
        String fieldContent = null;
        try {
            if (fieldName.equals("email"))
                fieldContent = emailFromServer.substring(0, emailFromServer.indexOf("&")) + "@" + emailFromServer.substring(emailFromServer.indexOf(";") + 1);
            else
                fieldContent = getOptionalString(shopperInfoJsonObject, fieldName);
        } catch (Exception e) {
            e.printStackTrace();
            fail("missing field in server response:\n Expected fieldName: " + fieldName + " Expected Value:" + expectedResult + "\n" + getShopperResponse);
        }

        Assert.assertEquals(fieldName + " was not saved correctly in DataBase", expectedResult, fieldContent);
    }

//    private void check_if_field_identify(String fieldName, String expectedResult, JSONObject shopperInfoJsonObject) {
//        String fieldContent = null;
//        try {
//            fieldContent = getOptionalString(shopperInfoJsonObject, fieldName);
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail("missing field in server response:\n Expected fieldName: " + fieldName + " Expected Value:" + expectedResult + "\n" + getShopperResponse);
//        }
//
//        Assert.assertEquals(fieldName + " was not saved correctly in DataBase", expectedResult, fieldContent);
//    }

}
