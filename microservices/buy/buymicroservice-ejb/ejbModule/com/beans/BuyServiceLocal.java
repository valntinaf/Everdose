package com.beans;

import java.util.List;

import javax.ejb.Local;

import com.classes.DeliveryInfo;
import com.classes.FinancialInfo;
import com.classes.StripeToken;
import model.ProductAdapter;

@Local
public interface BuyServiceLocal {

	boolean buy(List<ProductAdapter> products, String token, StripeToken stripeToken, DeliveryInfo deliveryInfo,
			FinancialInfo financialInfo, String productsString);
}
