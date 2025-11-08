package br.com.ifpe.olx_pp1_api.transactional;

public class CreateCheckoutRequest {

    private String productName;
    private Long unitAmountCents;
    private Long quantity;
    private String currency = "brl";
    private String successUrl;
    private String cancelUrl;

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Long getUnitAmountCents() { return unitAmountCents; }
    public void setUnitAmountCents(Long unitAmountCents) { this.unitAmountCents = unitAmountCents; }

    public Long getQuantity() { return quantity; }
    public void setQuantity(Long quantity) { this.quantity = quantity; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getSuccessUrl() { return successUrl; }
    public void setSuccessUrl(String successUrl) { this.successUrl = successUrl; }

    public String getCancelUrl() { return cancelUrl; }
    public void setCancelUrl(String cancelUrl) { this.cancelUrl = cancelUrl; }
}
