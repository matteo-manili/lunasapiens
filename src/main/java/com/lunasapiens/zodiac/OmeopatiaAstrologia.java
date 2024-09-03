package com.lunasapiens.zodiac;

public class OmeopatiaAstrologia {

    private SegnoZodiacale ascendente;
    private SegnoZodiacale pianetaSole;
    private SegnoZodiacale pianetaLuna;
    private SegnoZodiacale pianetaMercurio;
    private SegnoZodiacale pianetaVenere;
    private SegnoZodiacale pianetaMarte;
    private SegnoZodiacale pianetaGiove;
    private SegnoZodiacale pianetaSaturno;
    private SegnoZodiacale pianetaUrano;
    private SegnoZodiacale pianetaNettuno;
    private SegnoZodiacale pianetaPlutone;

    private int totElementoFuoco;
    private int totElementoAcqua;
    private int totElementoTerra;
    private int totElementoAria;

    private int totElementoFuocoTipoPianeta = 0;
    private int totElementoAcquaTipoPianeta = 0;
    private int totElementoTerraTipoPianeta = 0;
    private int totElementoAriaTipoPianeta = 0;


    public OmeopatiaAstrologia() { }

    // get...

    public SegnoZodiacale getAscendente() {
        return ascendente;
    }

    public SegnoZodiacale getPianetaSole() { return pianetaSole; }

    public SegnoZodiacale getPianetaLuna() {
        return pianetaLuna;
    }

    public SegnoZodiacale getPianetaMercurio() {
        return pianetaMercurio;
    }

    public SegnoZodiacale getPianetaVenere() {
        return pianetaVenere;
    }

    public SegnoZodiacale getPianetaMarte() {
        return pianetaMarte;
    }

    public SegnoZodiacale getPianetaGiove() {
        return pianetaGiove;
    }

    public SegnoZodiacale getPianetaSaturno() {
        return pianetaSaturno;
    }

    public SegnoZodiacale getPianetaUrano() {
        return pianetaUrano;
    }

    public SegnoZodiacale getPianetaNettuno() {
        return pianetaNettuno;
    }

    public SegnoZodiacale getPianetaPlutone() { return pianetaPlutone; }

    public int getTotElementoFuoco() { return totElementoFuoco; }

    public int getTotElementoAcqua() { return totElementoAcqua; }

    public int getTotElementoTerra() {
        return totElementoTerra;
    }

    public int getTotElementoAria() {
        return totElementoAria;
    }

    public int getTotElementoFuocoTipoPianeta() { return totElementoFuocoTipoPianeta; }

    public int getTotElementoAcquaTipoPianeta() { return totElementoAcquaTipoPianeta; }

    public int getTotElementoTerraTipoPianeta() { return totElementoTerraTipoPianeta; }

    public int getTotElementoAriaTipoPianeta() { return totElementoAriaTipoPianeta; }


    // set...

    public void setAscendente(SegnoZodiacale ascendente) {
        this.ascendente = ascendente;
    }

    public void setPianetaSole(SegnoZodiacale pianetaSole) {
        this.pianetaSole = pianetaSole;
    }

    public void setPianetaLuna(SegnoZodiacale pianetaLuna) {
        this.pianetaLuna = pianetaLuna;
    }

    public void setPianetaMercurio(SegnoZodiacale pianetaMercurio) {
        this.pianetaMercurio = pianetaMercurio;
    }

    public void setPianetaVenere(SegnoZodiacale pianetaVenere) {
        this.pianetaVenere = pianetaVenere;
    }

    public void setPianetaMarte(SegnoZodiacale pianetaMarte) {
        this.pianetaMarte = pianetaMarte;
    }

    public void setPianetaGiove(SegnoZodiacale pianetaGiove) {
        this.pianetaGiove = pianetaGiove;
    }

    public void setPianetaSaturno(SegnoZodiacale pianetaSaturno) {
        this.pianetaSaturno = pianetaSaturno;
    }

    public void setPianetaUrano(SegnoZodiacale pianetaUrano) {
        this.pianetaUrano = pianetaUrano;
    }

    public void setPianetaNettuno(SegnoZodiacale pianetaNettuno) {
        this.pianetaNettuno = pianetaNettuno;
    }

    public void setPianetaPlutone(SegnoZodiacale pianetaPlutone) {
        this.pianetaPlutone = pianetaPlutone;
    }

    public void setTotElementoFuoco(int totElementoFuoco) {
        this.totElementoFuoco = totElementoFuoco;
    }

    public void setTotElementoAcqua(int totElementoAcqua) {
        this.totElementoAcqua = totElementoAcqua;
    }

    public void setTotElementoTerra(int totElementoTerra) {
        this.totElementoTerra = totElementoTerra;
    }

    public void setTotElementoAria(int totElementoAria) {
        this.totElementoAria = totElementoAria;
    }

    public void setTotElementoFuocoTipoPianeta(int totElementoFuocoTipoPianeta) { this.totElementoFuocoTipoPianeta = totElementoFuocoTipoPianeta; }

    public void setTotElementoAcquaTipoPianeta(int totElementoAcquaTipoPianeta) { this.totElementoAcquaTipoPianeta = totElementoAcquaTipoPianeta; }

    public void setTotElementoTerraTipoPianeta(int totElementoTerraTipoPianeta) { this.totElementoTerraTipoPianeta = totElementoTerraTipoPianeta; }

    public void setTotElementoAriaTipoPianeta(int totElementoAriaTipoPianeta) { this.totElementoAriaTipoPianeta = totElementoAriaTipoPianeta; }

}
