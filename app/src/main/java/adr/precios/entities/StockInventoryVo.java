package adr.precios.entities;

public class StockInventoryVo {

    private int invId;
    private int invIdItem;
    private int invIdBranch;
    private int invExist;
    private int invUltimo;

    public StockInventoryVo(int invId, int invIdItem, int invIdBranch, int invExist, int invUltimo) {
        this.invId = invId;
        this.invIdItem = invIdItem;
        this.invIdBranch = invIdBranch;
        this.invExist = invExist;
        this.invUltimo = invUltimo;
    }

    public int getInvId() {
        return invId;
    }
    public void setInvId(int invId) {
        this.invId = invId;
    }

    public int getInvIdItem() {
        return invIdItem;
    }
    public void setInvIdItem(int invIdItem) {
        this.invIdItem = invIdItem;
    }

    public int getInvIdBranch() {
        return invIdBranch;
    }
    public void setInvIdBranch(int invIdBranch) {
        this.invIdBranch = invIdBranch;
    }

    public int getInvExist() {
        return invExist;
    }
    public void setInvExist(int invExist) {
        this.invExist = invExist;
    }

    public int getInvUltimo() {
        return invUltimo;
    }
    public void setInvUltimo(int invUltimo) {
        this.invUltimo = invUltimo;
    }
}
