package com.elearning.services;

import javax.ejb.Local;

@Local
public interface SystemStatusBeanLocal {
    String getSystemHealth();
}