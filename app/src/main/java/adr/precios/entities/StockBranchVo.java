package adr.precios.entities;

public class StockBranchVo {

    private int branchId;
    private String branchName;
    private String branchExist;
    private float branchPrice;

    public StockBranchVo(int branchId, String branchName, String branchExist, float branchPrice) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.branchExist = branchExist;
        this.branchPrice = branchPrice;
    }

    public int getBranchId() {
        return branchId;
    }
    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchExist() {
        return branchExist;
    }
    public void setBranchExist(String branchExist) {
        this.branchExist = branchExist;
    }

    public float getBranchPrice() {
        return branchPrice;
    }
    public void setBranchPrice(float invId) {
        this.branchPrice = branchPrice;
    }
}
