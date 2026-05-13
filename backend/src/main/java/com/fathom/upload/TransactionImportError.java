package com.fathom.upload;

import com.fathom.common.BaseEntity;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "transaction_import_errors")
public class TransactionImportError extends BaseEntity {
    @Id private UUID id;
    @Column(name = "import_id", nullable = false) private UUID importId;
    @Column(name = "row_number", nullable = false) private int rowNumber;
    @Column(nullable = false, columnDefinition = "TEXT") private String message;
    @Column(name = "raw_row", columnDefinition = "TEXT") private String rawRow;
    @PrePersist void init(){ if (id == null) id = UUID.randomUUID(); }
    public UUID getId(){return id;} public UUID getImportId(){return importId;} public int getRowNumber(){return rowNumber;} public String getMessage(){return message;} public String getRawRow(){return rawRow;}
    public void setImportId(UUID v){importId=v;} public void setRowNumber(int v){rowNumber=v;} public void setMessage(String v){message=v;} public void setRawRow(String v){rawRow=v;}
}
