//package vehcon.models.masters;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.IdClass;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.Table;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import vehcon.models.auth.User;
//
//@Entity
//@Table(name = "userprocesses", schema="master")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@IdClass(UsersProcessId.class)
//
//public class UsersProcesses {
//
// @Id
// @ManyToOne
// @JoinColumn(name = "fromprocess", referencedColumnName = "processcode")
// private Processes processcode;
//
// @Id
// @ManyToOne
// @JoinColumn(name = "usercode", referencedColumnName = "usercode")
// private User usercode;
// 
// private Integer slno;
//}