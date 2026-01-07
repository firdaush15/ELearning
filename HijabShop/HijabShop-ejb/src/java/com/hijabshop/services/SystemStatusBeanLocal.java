package com.hijabshop.services;

import javax.ejb.Local;

@Local
public interface SystemStatusBeanLocal {

    String getSystemHealth();
}
