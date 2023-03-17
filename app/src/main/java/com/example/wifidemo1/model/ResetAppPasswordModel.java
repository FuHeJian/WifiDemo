package com.example.wifidemo1.model;

public class ResetAppPasswordModel {
    public String step = null;
    public String ret = null;
    public String password = null;
    public String securityQ = null;
    public String securityA = null;
    public boolean changePassword = false;


    @Override
    public String toString() {
        return "step= " + step + ", ret= " + ret + ", password= " + password + ", securityQ= " + securityQ + ", securityA= " + securityA + ",changePassword =" + changePassword;
    }
}
