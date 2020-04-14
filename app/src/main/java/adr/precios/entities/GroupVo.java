package adr.precios.entities;

public class GroupVo {

    private int gruVineta;
    private int gruId;
    private String gruNombre;
    private int gruEmpresa;

    public GroupVo(int gruVineta, int gruId, String gruNombre, int gruEmpresa) {
        this.gruVineta = gruVineta;
        this.gruId = gruId;
        this.gruNombre = gruNombre;
        this.gruEmpresa = gruEmpresa;
    }

    public int getGruVineta() {
        return gruVineta;
    }
    public void setGruVineta(int gruVineta) {
        this.gruVineta = gruVineta;
    }

    //*****************************************************************

    public int getGruId() {
        return gruId;
    }
    public void setGruId(int gruId) {
        this.gruId = gruId;
    }

    //*****************************************************************

    public String getGruNombre() {
        return gruNombre;
    }
    public void setGruNombre(String gruNombre) {
        this.gruNombre = gruNombre;
    }

    //*****************************************************************

    public int getGruEmpresa() {
        return gruEmpresa;
    }
    public void setGruEmpresa(int gruNombre) {
        this.gruEmpresa = gruEmpresa;
    }

    //*****************************************************************

}

