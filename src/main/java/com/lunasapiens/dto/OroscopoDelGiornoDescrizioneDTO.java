package com.lunasapiens.dto;



public class OroscopoDelGiornoDescrizioneDTO {

    private String descrizioneOroscopoDelGiorno;
    private GiornoOraPosizioneDTO giornoOraPosizioneDTO;



    public OroscopoDelGiornoDescrizioneDTO(String descrizioneOroscopoDelGiorno, GiornoOraPosizioneDTO giornoOraPosizioneDTO) {
        this.descrizioneOroscopoDelGiorno = descrizioneOroscopoDelGiorno;
        this.giornoOraPosizioneDTO = giornoOraPosizioneDTO;
    }




    // get
    public String getDescrizioneOroscopoDelGiorno() { return descrizioneOroscopoDelGiorno; }
    public GiornoOraPosizioneDTO getGiornoOraPosizioneDTO() {
        return giornoOraPosizioneDTO;
    }

}

