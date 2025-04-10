package vehcon.services.appdata;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vehcon.models.masters.Processes;
import vehcon.models.masters.VehicleParts;
import vehcon.models.vehiclecondemnations.VehicleDraft;
import vehcon.models.vehiclecondemnations.VehiclePartsConditionDraft;
import vehcon.repo.appdata.ProcessesRepository;
import vehcon.repo.appdata.VehicleDraftRepository;
import vehcon.repo.appdata.VehiclePartsConditionDraftRepository;
import vehcon.repo.masters.VehiclePartsRepository;

@Service
@RequiredArgsConstructor
public class VehicleDraftServices {

    private final VehicleDraftRepository vehicleDraftRepo;
    private final VehiclePartsConditionDraftRepository vehiclePartsConditionDraftRepo;
    private final VehiclePartsRepository vehiclePartsRepo;
    private final ProcessesRepository processesRepo;

    @Transactional
    public String addVehicleDraft(VehicleDraft vehDraft) {
        try {
            Processes initialProcess = processesRepo.findById(1)
                    .orElseThrow(() -> new RuntimeException("Initial Process with code 1 not found."));
            vehDraft.setProcesscode(initialProcess);
            
            String applicationMode = "F";
            vehDraft.setApplicationMode(applicationMode);
            
            String applicationSlno = UUID.randomUUID().toString().substring(0,10);
            vehDraft.setApplicationSlno(applicationSlno);
            
            String applicationCode = applicationMode + applicationSlno;
            vehDraft.setApplicationCode(applicationCode);

            String versionFlagCode = "2";
            vehDraft.setVersionflagcode(versionFlagCode);
            
            vehDraft.setEntrydate(LocalDateTime.now());
            
            // Create a Vehicle Draft and save in repo. return the applicationCode
            VehicleDraft savedDraft = vehicleDraftRepo.save(vehDraft);
            

            // Fetch all VehicleParts entities from the master.vehicleparts table
            List<VehicleParts> vehiclePartsList = vehiclePartsRepo.findAll();

            // Using the applicationCode, Create all the VehiclePartsDraft using this applicationCode and vehiclepartcode
            for (VehicleParts vehiclePart : vehiclePartsList) {
                VehiclePartsConditionDraft partsDraft = new VehiclePartsConditionDraft();

                // Setting the related entities for the composite primary key
                partsDraft.setApplicationcode(savedDraft);
                partsDraft.setVehiclepartcode(vehiclePart);

                vehiclePartsConditionDraftRepo.save(partsDraft);
            }

            return savedDraft.getApplicationCode(); // Return the generated applicationCode
        } catch (Exception ex) {
            throw ex;
        }
    }

}