package com.bluesnap.androidapi;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.models.BillingContactInfo;
import com.bluesnap.androidapi.models.CardinalJWT;
import com.bluesnap.androidapi.models.CreditCard;
import com.bluesnap.androidapi.models.PurchaseDetails;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.BlueSnapValidator;
import com.bluesnap.androidapi.services.CardinalManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static java.net.HttpURLConnection.HTTP_OK;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by oz on 10/30/17.
 */

public class CardinalAPITest extends BSAndroidTestsBase {

    @Test
    public void cardinal_token_tests() throws Exception {

        CardinalManager cardinalManager = CardinalManager.getInstance();
        Double amount = 30.5D;
        SdkRequest sdkRequest = new SdkRequest(amount, "USD");
        blueSnapService.setSdkRequest(sdkRequest);
        cardinalManager.configureCardinal(getTestContext());
        CardinalJWT cardinalJWT = cardinalManager.getCardinalJWT();
        assertTrue(cardinalJWT.getJWT().length() > 10);

//        //assertTrue("this should be a valid luhn", BlueSnapValidator.creditCardNumberValidation(CARD_NUMBER_VALID_LUHN_UNKNOWN_TYPE));
//        assertTrue(BlueSnapValidator.creditCardNumberValidation(CARD_NUMBER_VALID_LUHN_UNKNOWN_TYPE));
//        assertTrue(BlueSnapValidator.creditCardFullValidation(card));
//        assertNotNull(card.getCardType());
//        assertFalse(card.getCardType().isEmpty());
//
//        try {
//            BlueSnapHTTPResponse blueSnapHTTPResponse = blueSnapService.submitTokenizedDetails(purchaseDetails);
//            assertEquals(HTTP_OK, blueSnapHTTPResponse.getResponseCode());
//            JSONObject jsonObject = new JSONObject(blueSnapHTTPResponse.getResponseString());
//            String Last4 = jsonObject.getString("last4Digits");
//            String ccType = jsonObject.getString("ccType");
//            assertEquals("MASTERCARD", ccType);
//            assertEquals("1116", Last4);
//
//        } catch (NullPointerException | JSONException e) {
//            e.printStackTrace();
//            fail("Exceptions while parsing response");
//        }
    }
}


