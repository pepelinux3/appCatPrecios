package adr.precios.entities;

public class ItemVo {

    private int idClave;
    private String noParte;
    private String nomCorto;
    private String nomLargo;
    private int idEmpresa;
    private int idSubgrupo;
    private int status;
    private int visible;
    private int modifica;

    public ItemVo(int idClave, String noParte, String nomCorto, String nomLargo, int idEmpresa, int idSubgrupo, int status, int visible, int modifica) {
        this.idClave = idClave;
        this.noParte = noParte;
        this.nomCorto = nomCorto;
        this.nomLargo = nomLargo;
        this.idEmpresa = idEmpresa;
        this.idSubgrupo = idSubgrupo;
        this.status = status;
        this.visible = visible;
        this.modifica = modifica;
    }

    public int getIdClave() {
        return idClave;
    }

    public String getNoParte() {
        return noParte;
    }

    public String getNomCorto() {
        return nomCorto;
    }

    public String getNomLargo() {
        return nomLargo;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public int getIdSubgrupo() {
        return idSubgrupo;
    }

    public int getStatus() {
        return status;
    }

    public int getVisible() {
        return visible;
    }

    public int getModifica() {
        return modifica;
    }
}
