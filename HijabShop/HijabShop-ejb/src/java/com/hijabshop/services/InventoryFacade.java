package com.hijabshop.services;

import com.hijabshop.entities.HijabProduct;
import com.hijabshop.entities.InventoryException;
import com.hijabshop.entities.InventoryFacadeRemote;
import java.util.List;
import javax.ejb.Stateless; // Switch to Stateless for DB efficiency 
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless(name = "InventoryFacade") // Explicitly names the bean
public class InventoryFacade implements InventoryFacadeRemote {
    // ... keep the rest of the code exactly the same ...

    @PersistenceContext(unitName = "HijabShop-PU")
    private EntityManager em;

    @Override
    public HijabProduct[] getAllProducts() throws InventoryException {
        // JPA Query 
        Query query = em.createQuery("SELECT p FROM HijabProduct p");
        List<HijabProduct> list = query.getResultList();
        return list.toArray(new HijabProduct[0]);
    }

    @Override
    public HijabProduct getProduct(String sku) throws InventoryException {
        HijabProduct product = em.find(HijabProduct.class, sku);
        if (product == null) {
            throw new InventoryException("Product not found: " + sku);
        }
        return product;
    }

    @Override
    public void addProduct(HijabProduct product) throws InventoryException {
        try {
            em.persist(product);
        } catch (Exception e) {
            throw new InventoryException("Error saving product: " + e.getMessage());
        }
    }
}
