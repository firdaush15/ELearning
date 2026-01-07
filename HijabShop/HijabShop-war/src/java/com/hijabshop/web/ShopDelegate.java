package com.hijabshop.web;

import com.hijabshop.entities.InventoryFacadeRemote;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ShopDelegate {

    private InventoryFacadeRemote facade;

    public ShopDelegate() {
        try {
            Context ctx = new InitialContext();
            // JNDI Lookup 
            facade = (InventoryFacadeRemote) ctx.lookup("java:comp/env/InventoryLookup");
        } catch (NamingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public InventoryFacadeRemote getFacade() {
        return facade;
    }
}
