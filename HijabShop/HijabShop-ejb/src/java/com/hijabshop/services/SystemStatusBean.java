package com.hijabshop.services;

import javax.ejb.Stateless;

@Stateless
public class SystemStatusBean implements SystemStatusBeanLocal {

    @Override
    public String getSystemHealth() {
        return "HijabShop System is Online and Operational.";
    }
}
