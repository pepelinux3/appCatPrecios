package adr.precios.entities;

public class SubgroupVo {

    private int subClave;
    private String subNombre;
    private String subDescripcion;
    private int subGroup;
    private float subDesc;
    private float subPorcen;

    public SubgroupVo(int subClave, String subNombre, String subDescripcion, int subGroup, float subDesc, float subPorcen) {
        this.subClave = subClave;
        this.subNombre = subNombre;
        this.subDescripcion = subDescripcion;
        this.subGroup = subGroup;
        this.subDesc = subDesc;
        this.subPorcen = subPorcen;
    }

    public int getSubClave() {
        return subClave;
    }

    public String getSubNombre() {
        return subNombre;
    }

    public String getSubDescripcion() {
        return subDescripcion;
    }

    public int getSubGroup() {
        return subGroup;
    }

    public float getSubDesc() {
        return subDesc;
    }

    public float getSubPorcen() {
        return subPorcen;
    }
}
