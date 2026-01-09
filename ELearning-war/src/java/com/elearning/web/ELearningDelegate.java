/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elearning.web;

import com.elearning.entities.ELearningFacadeRemote;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ELearningDelegate {
    private ELearningFacadeRemote facade;

    public ELearningDelegate() {
        try {
            Context ctx = new InitialContext();
            facade = (ELearningFacadeRemote) ctx.lookup("java:comp/env/ELearningLookup");
        } catch (NamingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public ELearningFacadeRemote getFacade() {
        return facade;
    }
}
