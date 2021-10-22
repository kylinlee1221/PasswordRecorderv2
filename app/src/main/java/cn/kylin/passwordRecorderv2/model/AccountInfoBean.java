package cn.kylin.passwordRecorderv2.model;

import androidx.annotation.NonNull;

import java.util.Objects;

public class AccountInfoBean {
    private long id;
    private String username;
    private String password;
    private String security;
    private String register;
    private String otherInfo;
    private String date;

    public AccountInfoBean(long id,String username,String password,String security,String register,String otherInfo,String date){
        this.id=id;
        this.username=username;
        this.password=password;
        this.security=security;
        this.register=register;
        this.otherInfo=otherInfo;
        this.date=date;
    }

    public AccountInfoBean(String username,String password,String security,String register,String otherInfo,String date){
        //this.id=id;
        this.username=username;
        this.password=password;
        this.security=security;
        this.register=register;
        this.otherInfo=otherInfo;
        this.date=date;
    }

    public AccountInfoBean(){

    }

    public long getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getOtherInfo() {
        return otherInfo;
    }

    public String getPassword() {
        return password;
    }

    public String getRegister() {
        return register;
    }

    public String getSecurity() {
        return security;
    }

    public String getUsername() {
        return username;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRegister(String register) {
        this.register = register;
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Username: " + username + '\n' +
                "Register: " + register + '\n' +
                "Date: " + date ;
    }

    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountInfoBean)) return false;
        AccountInfoBean infoBean = (AccountInfoBean) o;
        return Objects.equals(getUsername(), infoBean.getUsername()) && Objects.equals(getPassword(), infoBean.getPassword()) && Objects.equals(getRegister(), infoBean.getRegister());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getPassword(), getSecurity(), getRegister(), getOtherInfo());
    }
}
