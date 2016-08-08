package com.bluesnap.androidapi.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A Request for payment process in the SDK.
 * A new PaymentRequest should be used for each purchase.
 */
public class PaymentRequest implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public Object createFromParcel(Parcel parcel) {
            PaymentRequest pr = new PaymentRequest(parcel);
            return pr;
        }

        @Override
        public Object[] newArray(int i) {
            return new Object[0];
        }
    };
    private String currencyNameCode;
    private Double amount;
    private String customTitle;
    private String userEmail;
    private boolean rememberUser;
    private boolean shippingRequired;
    private String shopperID;
    private Double subtotalAmount;
    private Double taxAmount;
    private String baseCurrency;
    private Double baseAmount;
    private Double baseTaxAmount;
    private Double baseSubtotalAmount;
    private transient boolean allowRememberUser = true;

    public PaymentRequest(Parcel parcel) {
        currencyNameCode = parcel.readString();
        amount = parcel.readDouble();
        customTitle = parcel.readString();
        userEmail = parcel.readString();
        rememberUser = parcel.readInt() != 0;
        shippingRequired = parcel.readInt() != 0;
        shopperID = parcel.readString();
        subtotalAmount = parcel.readDouble();
        taxAmount = parcel.readDouble();

        //Set these values once to remember the base currency values
        baseCurrency = currencyNameCode;
        baseAmount = amount;
        baseTaxAmount = taxAmount;
        baseSubtotalAmount = subtotalAmount;
    }

    public PaymentRequest() {

    }

    public PaymentRequest(String currencyNameCode) {
        setCurrencyNameCode(currencyNameCode);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(currencyNameCode);
        parcel.writeDouble(amount);
        parcel.writeString(customTitle);
        parcel.writeString(userEmail);
        parcel.writeInt(rememberUser ? 1 : 0);
        parcel.writeInt(shippingRequired ? 1 : 0);
        parcel.writeString(shopperID);
        parcel.writeDouble(subtotalAmount != null ? subtotalAmount : 0D);
        parcel.writeDouble(taxAmount != null ? taxAmount : 0D);
    }


    public String getCurrencyNameCode() {
        return currencyNameCode;
    }

    public void setCurrencyNameCode(String currencyNameCode) {
        if (baseCurrency == null)
            baseCurrency = currencyNameCode;
        this.currencyNameCode = currencyNameCode;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        if (baseAmount == null)
            baseAmount = amount;
        this.amount = amount;
    }

    public String getCustomTitle() {
        return customTitle;
    }

    public void setCustomTitle(String customTitle) {
        this.customTitle = customTitle;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Boolean isRememberUser() {
        return rememberUser;
    }

    public void setRememberUser(Boolean remember) {
        rememberUser = remember;
    }

    public boolean isShippingRequired() {
        return shippingRequired;
    }

    public void setShippingRequired(boolean shippingRequired) {
        this.shippingRequired = shippingRequired;
    }

    public String getShopperID() {
        return shopperID;
    }


    public Double getSubtotalAmount() {
        return subtotalAmount;
    }

    public void setSubtotalAmount(Double subtotalAmount) {
        if (baseSubtotalAmount == null)
            baseSubtotalAmount = subtotalAmount;

        this.subtotalAmount = subtotalAmount;
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        if (baseTaxAmount == null)
            baseTaxAmount = taxAmount;

        this.taxAmount = taxAmount;
    }

    public void setAmountWithTax(Double subtotalAmount, Double taxAmount) {
        this.taxAmount = taxAmount;
        setSubtotalAmount(subtotalAmount);
        setAmount(subtotalAmount + taxAmount);
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public Double getBaseAmount() {
        return baseAmount;
    }

    public Double getBaseTaxAmount() {
        return baseTaxAmount;
    }

    public Double getBaseSubtotalAmount() {
        return baseSubtotalAmount;
    }

    public void allowRememberUser(boolean allowed) {
        allowRememberUser = allowed;
    }

    public boolean isRemembersSerIsAllowed() {
        return allowRememberUser;
    }


    public boolean verify() {
        if (amount == null)
            return false;
        if (amount <= 0)
            return false;
        return currencyNameCode != null;
    }

    public boolean isSubtotalTaxSet() {
        return (baseSubtotalAmount != 0D && baseTaxAmount != 0D);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PaymentRequest that = (PaymentRequest) o;

        if (rememberUser != that.rememberUser) return false;
        if (shippingRequired != that.shippingRequired) return false;
        if (allowRememberUser != that.allowRememberUser) return false;
        if (!currencyNameCode.equals(that.currencyNameCode)) return false;
        if (!amount.equals(that.amount)) return false;
        if (customTitle != null ? !customTitle.equals(that.customTitle) : that.customTitle != null)
            return false;
        if (userEmail != null ? !userEmail.equals(that.userEmail) : that.userEmail != null)
            return false;
        if (shopperID != null ? !shopperID.equals(that.shopperID) : that.shopperID != null)
            return false;
        if (subtotalAmount != null ? !subtotalAmount.equals(that.subtotalAmount) : that.subtotalAmount != null)
            return false;
        if (taxAmount != null ? !taxAmount.equals(that.taxAmount) : that.taxAmount != null)
            return false;
        if (baseCurrency != null ? !baseCurrency.equals(that.baseCurrency) : that.baseCurrency != null)
            return false;
        if (baseAmount != null ? !baseAmount.equals(that.baseAmount) : that.baseAmount != null)
            return false;
        return baseTaxAmount != null ? baseTaxAmount.equals(that.baseTaxAmount) : that.baseTaxAmount == null;

    }

    @Override
    public int hashCode() {
        int result = currencyNameCode != null ? currencyNameCode.hashCode() : 0;
        result = 31 * result + amount.hashCode();
        result = 31 * result + (customTitle != null ? customTitle.hashCode() : 0);
        result = 31 * result + (userEmail != null ? userEmail.hashCode() : 0);
        result = 31 * result + (rememberUser ? 1 : 0);
        result = 31 * result + (shippingRequired ? 1 : 0);
        result = 31 * result + (shopperID != null ? shopperID.hashCode() : 0);
        result = 31 * result + (subtotalAmount != null ? subtotalAmount.hashCode() : 0);
        result = 31 * result + (taxAmount != null ? taxAmount.hashCode() : 0);
        result = 31 * result + (baseCurrency != null ? baseCurrency.hashCode() : 0);
        result = 31 * result + (baseAmount != null ? baseAmount.hashCode() : 0);
        result = 31 * result + (baseTaxAmount != null ? baseTaxAmount.hashCode() : 0);
        result = 31 * result + (baseSubtotalAmount != null ? baseSubtotalAmount.hashCode() : 0);
        result = 31 * result + (allowRememberUser ? 1 : 0);
        return result;
    }
}
