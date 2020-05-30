package adr.precios.entities;

public class PriceVo {
    int tpri_id;
    float tpri_precio;
    String tpri_fecha;
    int tpri_lista;
    int tpri_articulo;
    int tpri_ultimo;

    public PriceVo() {
    }

    public PriceVo(int tpri_id, float tpri_precio, String tpri_fecha, int tpri_lista, int tpri_articulo, int tpri_ultimo) {
        this.tpri_id = tpri_id;
        this.tpri_precio = tpri_precio;
        this.tpri_fecha = tpri_fecha;
        this.tpri_lista = tpri_lista;
        this.tpri_articulo = tpri_articulo;
        this.tpri_ultimo = tpri_ultimo;
    }

    public int getTpri_id() {
        return tpri_id;
    }
    public void setTpri_id(int tpri_id) {
        this.tpri_id = tpri_id;
    }

    public float getTpri_precio() {
        return tpri_precio;
    }
    public void setTpri_precio(float tpri_precio) {
        this.tpri_precio = tpri_precio;
    }

    public String getTpri_fecha() {
        return tpri_fecha;
    }
    public void setTpri_fecha(String tpri_fecha) {
        this.tpri_fecha = tpri_fecha;
    }

    public int getTpri_lista() {
        return tpri_lista;
    }
    public void setTpri_lista(int tpri_lista) {
        this.tpri_lista = tpri_lista;
    }

    public int getTpri_articulo() {
        return tpri_articulo;
    }
    public void setTpri_articulo(int tpri_articulo) {
        this.tpri_articulo = tpri_articulo;
    }

    public int getTpri_ultimo() {
        return tpri_ultimo;
    }
    public void setTpri_ultimo(int tpri_ultimo) {
        this.tpri_ultimo = tpri_ultimo;
    }

}
