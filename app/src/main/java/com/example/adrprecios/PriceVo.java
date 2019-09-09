package com.example.adrprecios;

public class PriceVo {

    private int idItem;
    private String noItem;
    private String desItem;
    private String dateItem;
    private String gruItem;
    private String subgItem;
    private float priItem;

    public PriceVo(int idItem, String gruItem, String subgItem, String noItem, String desItem, String dateItem, float priItem) {
        this.idItem = idItem;
        this.gruItem = gruItem;
        this.subgItem = subgItem;
        this.noItem = noItem;
        this.desItem = desItem;
        this.dateItem = dateItem;
        this.priItem = priItem;
    }

    public int getIdItem() { return idItem; }
    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }

    public String getGruItem() {
        return gruItem;
    }
    public void setGruItem(String gruItem) { this.gruItem = gruItem; }

    public String getSubgItem() {
        return subgItem;
    }
    public void setSubgItem(String subgItem) {
        this.subgItem = subgItem;
    }

    public String getNoItem() {
        return noItem;
    }
    public void setNoItem(String noItem) {
        this.noItem = noItem;
    }

    public String getDesItem() {
        return desItem;
    }
    public void setDesItem(String desItem) {
        this.desItem = desItem;
    }

    public String getDateItem() {
        return dateItem;
    }
    public void setDateItem(String dateItem) {
        this.dateItem = dateItem;
    }

    public float getPriItem() {
        return priItem;
    }
    public void setPriItem(float priItem) {
        this.priItem = priItem;
    }
}
