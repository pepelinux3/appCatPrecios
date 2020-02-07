package adr.precios.entities;

public class InventoryVo {

    private int invId;
    private String invIdBranch;
    private String invExistencia;
    private float invPrice;

    public InventoryVo(int invId, String invIdBranch, String invExistencia, float invPrice) {
        this.invId = invId;
        this.invIdBranch = invIdBranch;
        this.invExistencia = invExistencia;
        this.invPrice = invPrice;
    }

    public int getInvId() {
        return invId;
    }
    public void setInvId(int invId) {
        this.invId = invId;
    }

    public String getInvIdBranch() {
        return invIdBranch;
    }
    public void setInvIdBranch(String invIdBranch) {
        this.invIdBranch = invIdBranch;
    }

    public String getInvExistencia() {
        return invExistencia;
    }
    public void setInvExistencia(String invExistencia) {
        this.invExistencia = invExistencia;
    }

    public float getInvPrice() {
        return invPrice;
    }
    public void setInvPrice(float invId) {
        this.invPrice = invPrice;
    }
}
