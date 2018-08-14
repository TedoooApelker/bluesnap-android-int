package com.bluesnap.android.demoapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.*;

/**
 * Created by sivani on 17/07/2018.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class AllowCurrencyChangeTest extends EspressoBasedTest {
    protected boolean isAllowed = true;

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(purchaseAmount, checkoutCurrency);
        sdkRequest.setBillingRequired(true);
        sdkRequest.setShippingRequired(true);
        sdkRequest.setAllowCurrencyChange(isAllowed);
        setupAndLaunch(sdkRequest);
        onView(withId(R.id.newCardButton)).perform(click());
    }

    /**
     * This test verifies the visibility of the currency hamburger button to the shopper,
     * according to whether we allowed currency change or not.
     * It covers visibility in billing, shipping and after changing activities
     */
    @Test
    public void currency_change_hamburger_view_validation() {
        //check hamburger button visibility in billing
        checkCurrencyHamburgerButtonVisibility();

        //check hamburger button visibility after opening country activity
        onView(withId(R.id.countryImageButton)).perform(click());
        onData(hasToString(containsString("Spain"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        checkCurrencyHamburgerButtonVisibility();

        CreditCardLineTesterCommon.fillInCCLineWithValidCard();
        ContactInfoTesterCommon.fillInContactInfo(R.id.billingViewComponent, "SP", true, false);

        //check hamburger button visibility in shipping
        onView(withId(R.id.buyNowButton)).perform(click());
        checkCurrencyHamburgerButtonVisibility();

        //check hamburger button visibility after opening country activity
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());
        onData(hasToString(containsString("Spain"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        checkCurrencyHamburgerButtonVisibility();

        //check hamburger button visibility back in billing
        Espresso.closeSoftKeyboard();
        Espresso.pressBack();
        checkCurrencyHamburgerButtonVisibility();
    }

    private void checkCurrencyHamburgerButtonVisibility() {
        if (isAllowed)
            onView(withId(R.id.hamburger_button)).check(matches(ViewMatchers.isDisplayed()));
        else
            onView(withId(R.id.hamburger_button)).check(matches(not(ViewMatchers.isDisplayed())));

    }
}