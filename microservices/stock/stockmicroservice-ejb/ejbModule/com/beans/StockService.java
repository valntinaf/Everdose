package com.beans;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.json.JSONObject;

import com.entities.Drugstore;
import com.entities.Product;
import com.entities.ProductFromDrugstore;
import com.google.gson.Gson;
import com.utils.DeliveryProduct;
import com.utils.ProductAdapter;
import com.utils.Utils;

/**
 * Session Bean implementation class StockService
 */
@Stateless
@LocalBean
public class StockService implements StockServiceRemote, StockServiceLocal {

    /**
     * Default constructor.
     */
	private static String DELIVERY_URL="http://127.0.0.1:8080/deliverymicroservice-web-0.0.1-SNAPSHOT/DeliverOrder";
	
    public StockService() {

    }

	@Override
	public List<Product> getProducts() {
		return Product.getProducts();
	}

	@Override
	public boolean addProduct( String name, String description, String location, String image, float price, int threshold, int amount ,String keyword) {

		boolean succesfulltransaction = false;

		try {

			Product product = new Product();
			product.setName(name);
			product.setDescription(description);
			product.setAmount(amount);
			product.setPrice(price);
			product.setImage(image);
			product.setThreshold(threshold);
			product.setLocation(location);
			product.setKeywords(keyword);
			product.save();

			succesfulltransaction = true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return succesfulltransaction;
	}

	@Override
	public boolean removeProduct( int id ) {
		boolean succesfulltransaction = false;
		try {
			Product product = Product.getProduct(id);
			product.removeProduct();
			succesfulltransaction=true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return succesfulltransaction;
	}

	@Override
	public List<Product> checkRunningOut() {
		// TODO Auto-generated method stub
		ArrayList<Product> productsRunningOut = new ArrayList<Product>();
		try {
			List<Product> products = Product.getProducts();
			for (Product product : products) {
				if (product.getAmount() < product.getThreshold()) {
					productsRunningOut.add(product);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return productsRunningOut;
	}

	@Override
	public boolean addDrugstore(String address, String email, String name, String phone, String uri) {
		Drugstore drugstore=new Drugstore();
		drugstore.setAddress(address);
		drugstore.setEmail(email);
		drugstore.setName(name);
		drugstore.setPhone(phone);
		drugstore.setUri(uri);
		return drugstore.save();
	}

	@Override
	public List<Drugstore> getDrugstores() {
		return Drugstore.getDrugstores();
	}

	@Override
	public boolean modifyDrugstore(int id, String address, String email, String name, String phone, String uri) {
		return Drugstore.UpdateDrugstoreById(id, address, email, name, phone, uri);
		
	}

	@Override
	public boolean deleteDrugstore(int id) {
		return Drugstore.deleteById(id);
	}

	@Override
	public List<ProductAdapter> getCatalog(String keywords) {
		List<ProductAdapter> products=new ArrayList<ProductAdapter>();
		List<Product> prds=this.getProducts();
		List<ProductFromDrugstore> prdsfd=this.getProductsFromDrugstore();
		for(Product p:prds) {
			if(keywords==null||(p.getKeywords()!=null&&p.getKeywords().contains(keywords))) {
				products.add(new ProductAdapter(p));
			}
		}
		for(ProductFromDrugstore p:prdsfd) {
			if(keywords==null||(p.getKeywords()!=null&&p.getKeywords().contains(keywords))) {
				products.add(new ProductAdapter(p));
			}
		}
		return products;
	}

	@Override
	public List<ProductFromDrugstore> getProductsFromDrugstore() {
		return ProductFromDrugstore.getProducts();
	}

	@Override
	public boolean addProductFromDrugstore(String name, String description, String keywords, float price,int drugstore_id) {
		boolean succesfulltransaction = false;

		try {

			ProductFromDrugstore product = new ProductFromDrugstore();
			Drugstore drugstore=Drugstore.getDrugstore(drugstore_id);
			product.setDrugstore(drugstore);
			product.setName(name);
			product.setDescription(description);
			product.setPrice(price);
			product.setKeywords(keywords);
			

			succesfulltransaction = product.save();;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return succesfulltransaction;
	}

	@Override
	public boolean modifyProduct(int id, String name, String description, String location, String image, Float price,
			Integer threshold, Integer amount, String keyword) {
		return Product.UpdateProductById(id, name, description, location, image, price,threshold, amount,keyword);
	}

	@Override
	public boolean removeProductFromDrugstore(int id) {
		boolean succesfulltransaction = false;
		try {
			ProductFromDrugstore product = ProductFromDrugstore.getProduct(id);
			succesfulltransaction=product.removeProduct();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return succesfulltransaction;
	}

	@Override
	public boolean modifyProductFromDrugstore(int id, String name, String description, Float price, String keywords) {
		return ProductFromDrugstore.UpdateProductById(id, name, description,price,keywords);
	}

	@Override
	public boolean consumeProducts(List<ProductAdapter> products,String destiny_address) {
		List<DeliveryProduct> deliveryOrder=new ArrayList<DeliveryProduct>();
		ProductAdapter pa;
		for(ProductAdapter p:products) {
			if(p.getType().equals("drugstore")) {
				ProductFromDrugstore product = ProductFromDrugstore.getProduct(p.getId());
				if(product==null) {
					System.out.println("no existe PD:"+p.getId());
					return false;
				}
				pa=new ProductAdapter(product);
				pa.setAmount(p.getAmount());
				deliveryOrder.add(new DeliveryProduct(pa));
			}else {
				Product product=Product.getProduct(p.getId());
				if(product==null) {
					System.out.println("no existe P:"+p.getId());
					return false;
				}
				int amount=product.getAmount()-p.getAmount();
				if(amount<0) {
					System.out.println("no suficiente P:"+p.getId());
					return false;
				}
				pa=new ProductAdapter(product);
				pa.setAmount(p.getAmount());
				deliveryOrder.add(new DeliveryProduct(pa));
			}
			
		}
		String order_json=new Gson().toJson(deliveryOrder);
		JSONObject json=new JSONObject();
		json.put("destiny_address", destiny_address);
		json.put("products", order_json);
		System.out.println("Json:");
		System.out.println(json.toString());
		if(!Utils.sendJson(DELIVERY_URL, json.toString())) {
			System.out.println("fallo el envio del delivery");
			return false;
		}
		for(ProductAdapter p:products) {
			if(p.getType().equals(ProductAdapter.INVENTARY)) {
				Product product=Product.getProduct(p.getId());
				int amount=product.getAmount()-p.getAmount();
				product.setAmount((amount>=0)?amount:0);
				product.save();
			}
		}
		return true;
	}

}
