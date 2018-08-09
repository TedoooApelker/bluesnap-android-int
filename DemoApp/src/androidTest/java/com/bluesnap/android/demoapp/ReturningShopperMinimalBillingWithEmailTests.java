package com.bluesnap.android.demoapp;

import android.support.test.espresso.Espresso;

import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;

/**
 * Created by sivani on 02/08/2018.
 */

public class ReturningShopperMinimalBillingWithEmailTests extends EspressoBasedTest {
    public ReturningShopperMinimalBillingWithEmailTests() {
        //super("?shopperId=" + RETURNING_SHOPPER_ID_MIN_BILLING_WITH_EMAIL);
        super(true, "");
    }

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(purchaseAmount, checkoutCurrency);
        sdkRequest.setEmailRequired(true);
        setupAndLaunch(sdkRequest);
        int cardPosition = randomTestValuesGenerator.randomReturningShopperCardPosition();
        if (!returningShopper.isWithEmail())
            returningShopperBillingContactInfo.setEmail("");
    }

    public void returning_shopper_minimal_billing_with_email_common_tester() throws IOException {
        credit_card_in_list_visibility_validation();
        onData(anything()).inAdapterView(withId(R.id.oneLineCCViewComponentsListView)).atPosition(0).perform(click());
        credit_card_view_visibility_validation();
        billing_summarized_contact_info_visibility_validation();
        pay_button_in_billing_validation();

        onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(R.id.billingViewSummarizedComponent)))).perform(click());
        billing_contact_info_content_validation();
        Espresso.pressBack();

        //Pre-condition: current info is billingInfo
        returning_shopper_edit_billing_contact_info_using_back_button_validation();
        Espresso.pressBack();
        returning_shopper_edit_billing_contact_info_using_done_button_validation();
    }

    @Test
    public void returning_shopper_minimal_billing_test() throws IOException {
        returning_shopper_minimal_billing_with_email_common_tester();
    }

    @Test
    public void returning_shopper_minimal_billing_with_shipping_test() throws IOException {
        returning_shopper_minimal_billing_with_email_common_tester();
    }

    @Test
    public void returning_shopper_minimal_billing_with_email_test() throws IOException {
        returning_shopper_minimal_billing_with_email_common_tester();
    }

    @Test
    public void returning_shopper_minimal_billing_with_shipping_with_email_test() throws IOException {
        returning_shopper_minimal_billing_with_email_common_tester();
    }

    /**
     * This test verifies that the all credit card fields are displayed as they should
     * in the returning shopper cards list.
     */
    public void credit_card_in_list_visibility_validation() {
        ReturningShopperVisibilityTesterCommon.credit_card_in_list_visibility_validation("credit_card_in_list_visibility_validation", "5288", "12/26");
    }

    /**
     * This test verifies that the all credit card fields are displayed as they should
     * when choosing an existing credit card in returning shopper.
     */
    public void credit_card_view_visibility_validation() {
        ReturningShopperVisibilityTesterCommon.credit_card_view_visibility_validation("credit_card_view_visibility_validation", "5288", "12/26");
    }

    /**
     * This test verifies that the summarized billing contact info is displayed
     * according to minimal billing with shipping when choosing an existing credit card in returning shopper.
     */
    public void billing_summarized_contact_info_visibility_validation() {
        boolean isEmailVisible = returningShopper.isWithEmail();
        ReturningShopperVisibilityTesterCommon.summarized_contact_info_visibility_validation("billing_summarized_contact_info_visibility_validation in " + returningShopper.getShopperDescription(),
                R.id.billingViewSummarizedComponent, returningShopper.getBillingCountry(), false, isEmailVisible);
    }

    /**
     * This test verifies that the "Pay" button is visible and contains
     * the correct currency symbol and amount
     */
    public void pay_button_in_billing_validation() {
        CreditCardVisibilityTesterCommon.pay_button_visibility_and_content_validation("pay_button_in_shipping_validation", R.id.returningShppoerCCNFragmentButtonComponentView, checkoutCurrency, purchaseAmount, 0.0);
    }

    /**
     * This test verifies that the billing contact info presents the correct
     * content when pressing the billing edit button in returning shopper.
     */
    public void billing_contact_info_content_validation() throws IOException {
        //verify info has been saved
        ContactInfoTesterCommon.contact_info_content_validation("billing_contact_info_content_validation in " + returningShopper.getShopperDescription(),
                applicationContext, R.id.billingViewComponent, returningShopper.getBillingCountry(), false, true, returningShopperBillingContactInfo);
    }

    /**
     * This test verifies that the summarized billing contact info and the
     * billing contact info presents the new content after editing the info.
     * It uses the "Done" button to go back to credit card fragment.
     */
    public void returning_shopper_edit_billing_contact_info_using_done_button_validation() throws IOException {
        ContactInfoTesterCommon.returning_shopper_edit_contact_info_validation("returning_shopper_edit_billing_contact_info_using_done_button_validation", applicationContext, R.id.billingViewSummarizedComponent, false, true, true, returningShopper.getBillingCountry(), null);
    }

    /**
     * This test verifies that the summarized billing contact info and the
     * billing contact info presents the old content after editing the info,
     * since it uses the "Back" button to go back to credit card fragment.
     */
    public void returning_shopper_edit_billing_contact_info_using_back_button_validation() throws IOException {
        ContactInfoTesterCommon.returning_shopper_edit_contact_info_validation("returning_shopper_edit_billing_contact_info_using_back_button_validation", applicationContext, R.id.billingViewSummarizedComponent, false, true, false, returningShopper.getBillingCountry(), returningShopperBillingContactInfo);
    }
}
