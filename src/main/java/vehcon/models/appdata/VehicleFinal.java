package vehcon.models.appdata;

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
@Data
@Table(name = "vehicles", schema = "vehiclecondemnations")
@NoArgsConstructor
@AllArgsConstructor
public class VehicleFinal {
	
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

    @Column(name = "premises")
    private String premises;

    @Column(name = "locations")
    private String locations;
    
    @Column(name = "directorateletternodate")
    private String directorateLetterNodate; 
    
    
    @Column(name = "govtletternodate")
    private String govtLetterNoDate;
    

    @ManyToOne
    @JoinColumn(name = "vehicletypecode", referencedColumnName="vehicletypecode")
    private VehicleType vehicletypecode;

    @Column(name="vehicledescription",length = 1000)
    private String vehicledescription;

    @ManyToOne
    @JoinColumn(name = "vehiclemanufacturercode", referencedColumnName="vehiclemanufacturercode")
    private VehicleManufacturer vehiclemanufacturercode;

    @ManyToOne
    @JoinColumn(name = "registereddistrict", referencedColumnName="districtcode")
    private Districts registeredDistrict;
    

    @Column(name="registrationno")
    private String registrationNo;
    
    @Column(name="engineno")
    private String engineno;

    @Column(name="chassisno")
    private String chassisno;

    @Column(name="manufactureyear")
    private Integer manufactureyear;

    @Column(name="purchasedate")
    private LocalDate purchasedate;

    @Column(name="vehicleprice")
    private Integer vehicleprice;

    @Column(name="totalkms")
    private Integer totalkms;

    @Column(name="depreciatedamount")
    private Integer depreciatedamount;

    @Column(name="improvements")
    private String improvements;

    @Column(name="expenses")
    private Integer expenses;

    @Column(name="repairexpenses")
    private Integer repairexpenses;

    @Column(name = "repairslastsixmonths")
    private Integer repairslastsixmonths;

    @Column(name="whetheraccident")
    private String whetheraccident;

    @Column(name="accidentcaseresolved")
    private String accidentcaseresolved;

    @Column(name="comments", length = 1000)
    private String comments;

    @Column(name="mvireportavailable")
    private String mvireportavailable;

    @Column(name="battery")
    private String battery;

    @Column(name="tyres")
    private String tyres;

    @Column(name="accidentdamage",length = 1000)
    private String accidentdamage;

    @Column(name="mviprice")
    private Integer mviprice;

    @Column(length = 1000)
    private String mviremarks;

    @ManyToOne
    @JoinColumn(name = "processcode", referencedColumnName ="processcode")
    private Processes processcode;

    @Column(name="entrydate")
    private LocalDateTime entrydate;
    
    @ManyToOne
    @JoinColumn(name = "financialyearcode", referencedColumnName = "financialyearcode")
    private FinancialYear financialYearCode;
    

    @Column(name = "remarks")
    private String remarks;

    @Column(name="versionflagcode")
    private String versionflagcode;
}
