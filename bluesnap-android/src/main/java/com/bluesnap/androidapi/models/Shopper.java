package com.bluesnap.androidapi.models;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * A representation of server exchange rate.
 */
public class Shopper extends ContactInfo{

    @SerializedName("vaultedShopperId")
    private int vaultedShopperId;
    @Nullable
    @SerializedName("email")
    private String email;
    @Nullable
    @SerializedName("address")
    private String address;
    @Nullable
    @SerializedName("phone")
    private String phone;
    @SerializedName("shopperCurrency")
    private String shopperCurrency;
    @Nullable
    @SerializedName("paymentSources")
    private PaymentSources paymentSources;
    @Nullable
    @SerializedName("shippingContactInfo")
    private ShippingInfo shippingContactInfo;
    @Nullable
    @SerializedName("lastPaymentInfo")
    private LastPaymentInfo lastPaymentInfo;

    public int getVaultedShopperId() {
        return vaultedShopperId;
    }

    public void setVaultedShopperId(int vaultedShopperId) {
        this.vaultedShopperId = vaultedShopperId;
    }


    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
    }

    @Nullable
    public String getAddress() {
        return address;
    }

    public void setAddress(@Nullable String address) {
        this.address = address;
    }

    @Nullable
    public String getPhone() {
        return phone;
    }

    public void setPhone(@Nullable String phone) {
        this.phone = phone;
    }

    public String getShopperCurrency() {
        return shopperCurrency;
    }

    public void setShopperCurrency(String shopperCurrency) {
        this.shopperCurrency = shopperCurrency;
    }

    @Nullable
    public PaymentSources getPaymentSources() {
        return paymentSources;
    }

    public void setPaymentSources(@Nullable PaymentSources paymentSources) {
        this.paymentSources = paymentSources;
    }

    @Nullable
    public ShippingInfo getShippingContactInfo() {
        return shippingContactInfo;
    }

    public void setShippingContactInfo(@Nullable ShippingInfo shippingContactInfo) {
        this.shippingContactInfo = shippingContactInfo;
    }

    @Nullable
    public LastPaymentInfo getLastPaymentInfo() {
        return lastPaymentInfo;
    }

    public void setLastPaymentInfo(@Nullable LastPaymentInfo lastPaymentInfo) {
        this.lastPaymentInfo = lastPaymentInfo;
    }
}