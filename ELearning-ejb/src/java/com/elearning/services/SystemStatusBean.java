package com.elearning.services;

import java.util.Date;
import javax.ejb.Stateless;

@Stateless
public class SystemStatusBean implements SystemStatusBeanLocal {

    @Override
    public String getSystemHealth() {
        // Logic to check system status (Simulated)
        Date now = new Date();
        return "System is Online. Server Time: " + now.toString();
    }
}