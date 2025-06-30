package com.lunasapiens.repository;

public interface DatabaseMaintenanceRepository {


    void deleteOldOroscopoRecords();
    void vacuumFullOroscopoGiornaliero();
    void vacuum();
    void vacuumFull();


}
