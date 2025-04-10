package vehcon.models.masters;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "processflow", schema="master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessFlow {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 @Column(name = "slno")
 private Integer slno;

 
 @ManyToOne
 @JoinColumn(name = "fromprocess", referencedColumnName = "processcode")
 private Processes fromProcess;

 @ManyToOne
 @JoinColumn(name = "toprocess", referencedColumnName = "processcode")
 private Processes toProcess;
}