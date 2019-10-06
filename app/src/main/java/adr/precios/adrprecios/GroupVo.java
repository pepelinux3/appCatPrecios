package adr.precios.adrprecios;

public class GroupVo {

    private int gruVineta;
    private int gruId;
    private String gruNombre;

    public GroupVo(int vineta, int id, String nombre ) {
        this.gruNombre = nombre;
        this.gruId = id;
        this.gruVineta = vineta;
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

}

