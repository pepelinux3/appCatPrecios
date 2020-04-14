package adr.precios.entities;

public class SequenceVo {

    private int tsec_clave;
    private int tsec_codigo;
    private String tsec_tabla;
    private String tsec_fecha;
    private int tsec_final;
    private int tsec_update;

    public SequenceVo(int tsec_clave, int tsec_codigo, String tsec_tabla, String tsec_fecha, int tsec_final, int tsec_update) {
        this.tsec_clave = tsec_clave;
        this.tsec_codigo = tsec_codigo;
        this.tsec_tabla = tsec_tabla;
        this.tsec_fecha = tsec_fecha;
        this.tsec_final = tsec_final;
        this.tsec_update = tsec_update;
    }

    public int getTsec_clave() {
        return tsec_clave;
    }

    public int getTsec_codigo() {
        return tsec_codigo;
    }

    public String getTsec_tabla() {
        return tsec_tabla;
    }

    public String getTsec_fecha() {
        return tsec_fecha;
    }

    public int getTsec_final() {
        return tsec_final;
    }

    public int getTsec_update() {
        return tsec_update;
    }
}
