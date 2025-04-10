package vehcon.models.vehiclecondemnations;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vehcon.models.masters.Departments;
import vehcon.models.masters.Districts;
import vehcon.models.masters.FinancialYear;
import vehcon.models.masters.Processes;
import vehcon.models.masters.VehicleManufacturer;
import vehcon.models.masters.VehicleType;

@Entity
@Table(name = "vehicles_draft", schema = "vehiclecondemnations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDraft {

	@Column(name = "applicationmode")
    private String applicationMode;

    @Column(name = "applicationslno")
    private String applicationSlno;

    @Id
    @Column(name = "applicationcode")
    private String applicationCode;

    @ManyToOne
    @JoinColumn(name = "departmentcode", referencedColumnName= "departmentcode")
    private Departments departmentCode;

    @Column(name = "officename")
    private String officeName;

    @Column(name = "officerdesignation")
    private String officerDesignation;

    private String premises;

    private String locations;
    
    @Column(name = "directorateletternodate")
    private String directorateLetterNodate; 
    
    
    @Column(name = "govtletternodate")
    private String govtLetterNoDate;
    

    @ManyToOne
    @JoinColumn(name = "vehicletypecode", referencedColumnName="vehicletypecode")
    private VehicleType vehicletypecode;

    private String vehicledescription;

    @ManyToOne
    @JoinColumn(name = "vehiclemanufacturercode", referencedColumnName="vehiclemanufacturercode")
    private VehicleManufacturer vehiclemanufacturercode;

    @ManyToOne
    @JoinColumn(name = "registereddistrict", referencedColumnName="districtcode")
    private Districts registeredDistrict;
    

    @Column(name="registrationno")
    private String registrationNo;
    
    private String engineno;

    private String chassisno;

    private Integer manufactureyear;

    private LocalDate purchasedate;

    private Integer vehicleprice;

    private Integer totalkms;

    private Integer depreciatedamount;

    private String improvements;

    private Integer expenses;

    private Integer repairexpenses;

    private Integer repairslastsixmonths;

    private String whetheraccident;

    private String accidentcaseresolved;

    private String comments;

    private String mvireportavailable;

    private String battery;

    private String tyres;

    private String accidentdamage;

    private Integer mviprice;

    private String mviremarks;

    @ManyToOne
    @JoinColumn(name = "processcode", referencedColumnName ="processcode")
    private Processes processcode;

    private LocalDateTime entrydate;
    
    @ManyToOne
    @JoinColumn(name = "financialyearcode", referencedColumnName = "financialyearcode")
    private FinancialYear financialYearCode;
    

    private String remarks;

    private String versionflagcode;
}