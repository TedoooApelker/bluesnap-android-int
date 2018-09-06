package com.bluesnap.android.demoapp.ShopperConfigUITests;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.matcher.ViewMatchers;

import com.bluesnap.android.demoapp.R;
import com.bluesnap.android.demoapp.TestUtils;
import com.bluesnap.android.demoapp.TestingShopperCheckoutRequirements;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.hamcrest.Matchers;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;

/**
 * Created by sivani on 27/08/2018.
 */

public class ChoosePaymentMethodTester extends ChoosePaymentMethodEspressoBasedTester {
    private static final String RETURNING_SHOPPER_ID_FULL_BILLING_WITH_SHIPPING_WITH_EMAIL = "22946805";
    private static final String RETURNING_SHOPPER_ID_FULL_BILLING_WITH_SHIPPING = "29632268";
    private static final String RETURNING_SHOPPER_ID_FULL_BILLING_WITH_EMAIL = "29632260";
    private static final String RETURNING_SHOPPER_ID_FULL_BILLING = "29632260";


    public ChoosePaymentMethodTester() {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(true, true, true);
    }

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException, JSONException {
        choosePaymentSetup(true);
    }

    @Test
    public void choose_existing_card_visibility_test() {
        //choose existing credit card
        onData(anything()).inAdapterView(withId(R.id.oneLineCCViewComponentsListView)).atPosition(0).perform(click());
        currency_hamburger_button_visibility_in_credit_card();
        submit_button_visibility_and_content_in_existing_card();

        onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(R.id.billingViewSummarizedComponent)))).perform(click());
        currency_hamburger_button_visibility_in_billing();
        Espresso.pressBack();


        onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(R.id.shippingViewSummarizedComponent)))).perform(click());
        currency_hamburger_button_visibility_in_shipping();
        Espresso.pressBack();
    }

    @Test
    public void choose_new_card_visibility_test() {
        //choose new credit card
        onView(ViewMatchers.withId(R.id.newCardButton)).perform(click());
        currency_hamburger_button_visibility_in_billing();

        TestUtils.continueToShippingOrPayInNewCard(defaultCountryKey, true, true);
        currency_hamburger_button_visibility_in_shipping();

        submit_button_visibility_and_content_in_new_card();
    }

    /**
     * This test verifies that the "Submit" button is visible and contains
     * the correct content in existing card
     */
    public void submit_button_visibility_and_content_in_existing_card() {
        ShopperConfigVisibilityTesterCommon.submit_button_visibility_and_content("submit_button_visibility_and_content", R.id.returningShppoerCCNFragmentButtonComponentView);
    }

    /**
     * This test verifies that the "Submit" button is visible and contains
     * the correct content
     */
    public void submit_button_visibility_and_content_in_new_card() {
        ShopperConfigVisibilityTesterCommon.submit_button_visibility_and_content("submit_button_visibility_and_content", R.id.shippingButtonComponentView);
    }

    /**
     * This test verifies that the hamburger button is not displayed in credit card
     */
    public void currency_hamburger_button_visibility_in_credit_card() {
        ShopperConfigVisibilityTesterCommon.currency_hamburger_button_visibility("currency_hamburger_button_visibility_in_credit_card");
    }

    /**
     * This test verifies that the hamburger button is not displayed in billing
     */
    public void currency_hamburger_button_visibility_in_billing() {
        ShopperConfigVisibilityTesterCommon.currency_hamburger_button_visibility("currency_hamburger_button_visibility_in_billing");
    }

    /**
     * This test verifies that the hamburger button is not displayed in shipping
     */
    public void currency_hamburger_button_visibility_in_shipping() {
        ShopperConfigVisibilityTesterCommon.currency_hamburger_button_visibility("currency_hamburger_button_visibility_in_shipping");
    }


}