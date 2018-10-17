package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.WebViewUITests;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CurrencyChangeTesterCommon;
import com.bluesnap.android.demoapp.CustomFailureHandler;
import com.bluesnap.android.demoapp.R;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by sivani on 27/08/2018.
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class PayPalBasicTests extends PayPalWebViewTests {

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException, JSONException {
        payPalCheckoutSetup();
    }

    @Test
    public void pay_pal_back_button_test() throws InterruptedException {
        loadPayPalWebView();

        Espresso.pressBack();

        //verify we are back in choose payment methods
        onView(withId(R.id.newCardButton))
                .withFailureHandler(new CustomFailureHandler("pay_pal_back_button_test: New Card button is not displayed after pressing back"))
                .check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.payPalButton))
                .withFailureHandler(new CustomFailureHandler("pay_pal_back_button_test: PayPal button is not displayed after pressing back"))
                .check(matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void pay_pal_entering_twice_test() throws InterruptedException {
        loadPayPalWebView();

        Espresso.pressBack();

        loadPayPalWebView();
//
//        loginToPayPal();
//        submitPayPalPayment();
    }

    @Test
    public void pay_pal_basic_transaction_test() throws InterruptedException {
        payPalBasicTransaction();
    }

    @Test
    public void pay_pal_transaction_after_changing_currency_test() throws InterruptedException {
        onView(withId(R.id.newCardButton)).perform(click());
        CurrencyChangeTesterCommon.changeCurrency("GBP");
        updateCurrencyAndAmountAfterConversion("USD", "GBP");

        Espresso.pressBack();

        payPalBasicTransaction();
    }

    //TODO: check why the f*ck this thing doesn't work
//    @Test
//    public void pay_pal_used_token_test() throws InterruptedException {
//        //press PayPal to create PayPal token
//        loadPayPalWebView();
//
//        //go back and change currency
//        Espresso.pressBack();
//        onView(withId(R.id.newCardButton)).perform(click());
//        CurrencyChangeTesterCommon.changeCurrency("GBP");
//        updateCurrencyAndAmountAfterConversion("USD", "GBP");
//
//        Espresso.pressBack();
//
//        //press PayPal to create PayPal token
//        payPalBasicTransaction();
//    }
}
