package com.fathom.upload;

import com.fathom.account.FinancialAccount;
import com.fathom.account.FinancialAccountService;
import com.fathom.category.Category;
import com.fathom.category.CategoryRepository;
import com.fathom.common.ResourceNotFoundException;
import com.fathom.transaction.*;
import com.fathom.user.UserService;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TransactionImportService {
    private final UserService userService; private final FinancialAccountService accountService; private final TransactionImportRepository importRepository;
    private final TransactionImportErrorRepository errorRepository; private final TransactionRepository transactionRepository; private final CategoryRepository categoryRepository; private final TransactionCsvParser parser;
    @Value("${fathom.import.max-file-size-bytes:5242880}") private long maxFileSize;
    public TransactionImportService(UserService userService, FinancialAccountService accountService, TransactionImportRepository importRepository, TransactionImportErrorRepository errorRepository, TransactionRepository transactionRepository, CategoryRepository categoryRepository, TransactionCsvParser parser){this.userService=userService;this.accountService=accountService;this.importRepository=importRepository;this.errorRepository=errorRepository;this.transactionRepository=transactionRepository;this.categoryRepository=categoryRepository;this.parser=parser;}

    @Transactional
    public TransactionImportDtos.ImportSummaryResponse importCsv(UUID userId, UUID accountId, MultipartFile file, TransactionSource source){
        userService.getEntity(userId); FinancialAccount account=accountService.getEntity(accountId); if(!account.getUserId().equals(userId)) throw new ResourceNotFoundException("Account not found");
        validateFile(file);
        TransactionImport imp = new TransactionImport(); imp.setUserId(userId); imp.setAccountId(accountId); imp.setSource(source); imp.setOriginalFilename(file.getOriginalFilename()==null?"upload.csv":file.getOriginalFilename()); imp.setStatus(ImportStatus.PROCESSING); imp=importRepository.save(imp);
        List<CSVRecord> rows=parser.parse(file);
        int created=0, dup=0, failed=0;
        List<TransactionImportError> errors = new ArrayList<>();
        for(CSVRecord r: rows){
            try{
                LocalDate date = LocalDate.parse(req(r,"transactionDate"), DateTimeFormatter.ISO_LOCAL_DATE);
                Direction direction = Direction.valueOf(req(r,"direction").toUpperCase(Locale.ROOT));
                BigDecimal amount = new BigDecimal(req(r,"amount")); if(amount.signum()<=0) throw new IllegalArgumentException("Invalid amount");
                TransactionType type = TransactionType.valueOf(req(r,"transactionType").toUpperCase(Locale.ROOT));
                String raw = req(r,"rawDescription");
                String hash = hash(userId+"|"+accountId+"|"+date+"|"+direction+"|"+amount+"|"+normalize(raw));
                if(transactionRepository.existsByUserIdAndImportHash(userId, hash)){dup++; continue;}
                Transaction t = new Transaction(); t.setUserId(userId); t.setAccountId(accountId); t.setTransactionDate(date); t.setDirection(direction); t.setAmount(amount); t.setTransactionType(type); t.setSource(source); t.setRawDescription(raw);
                t.setMerchant(optional(r, "merchant")); t.setNotes(optional(r, "notes")); t.setCategoryId(resolveCategoryId(userId, optional(r, "categoryName"))); t.setImportHash(hash);
                transactionRepository.save(t); created++;
            } catch(Exception ex){
                failed++; TransactionImportError e = new TransactionImportError(); e.setImportId(imp.getId()); e.setRowNumber((int)r.getRecordNumber()+1); e.setMessage(ex.getMessage()==null?"Invalid row":ex.getMessage()); e.setRawRow(r.toString()); errors.add(e);
            }
        }
        if(!errors.isEmpty()) errorRepository.saveAll(errors);
        imp.setTotalRows(rows.size()); imp.setCreatedCount(created); imp.setSkippedDuplicateCount(dup); imp.setFailedCount(failed);
        imp.setStatus(failed==0?ImportStatus.COMPLETED:(created+dup)>0?ImportStatus.COMPLETED_WITH_ERRORS:ImportStatus.FAILED);
        imp=importRepository.save(imp);
        return toSummary(imp, errors.stream().map(this::toError).toList());
    }

    public List<TransactionImportDtos.ImportSummaryResponse> listByUser(UUID userId){ userService.getEntity(userId); return importRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(i->toSummary(i,List.of())).toList(); }
    public TransactionImportDtos.ImportSummaryResponse get(UUID importId){ TransactionImport imp=importRepository.findById(importId).orElseThrow(()->new ResourceNotFoundException("Import not found")); List<TransactionImportDtos.ImportErrorResponse> errs=errorRepository.findByImportIdOrderByRowNumberAsc(importId).stream().map(this::toError).toList(); return toSummary(imp,errs); }

    private void validateFile(MultipartFile file){ if(file==null||file.isEmpty()) throw new IllegalArgumentException("File must not be empty"); if(file.getSize()>maxFileSize) throw new IllegalArgumentException("File exceeds max size"); String name=file.getOriginalFilename()==null?"":file.getOriginalFilename().toLowerCase(Locale.ROOT); if(!name.endsWith(".csv")) throw new IllegalArgumentException("File must be CSV"); }
    private String req(CSVRecord r,String k){ String v=blank(r.get(k)); if(v==null) throw new IllegalArgumentException("Missing value for "+k); return v; }
    private String optional(CSVRecord r, String k){ if(!r.isMapped(k)) return null; return blank(r.get(k)); }
    private String blank(String s){ return s==null||s.isBlank()?null:s.trim(); }
    private String normalize(String s){ return s.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+"," "); }
    private String hash(String input){ try{MessageDigest md=MessageDigest.getInstance("SHA-256"); byte[] b=md.digest(input.getBytes(StandardCharsets.UTF_8)); StringBuilder sb=new StringBuilder(); for(byte x:b) sb.append(String.format("%02x",x)); return sb.toString();}catch(Exception e){ throw new RuntimeException(e);} }
    private UUID resolveCategoryId(UUID userId, String name){ if(name==null) return null; return categoryRepository.findByUserIdAndNameIgnoreCaseAndActiveTrue(userId,name).or(()->categoryRepository.findBySystemDefaultTrueAndNameIgnoreCaseAndActiveTrue(name)).map(Category::getId).orElse(null); }
    private TransactionImportDtos.ImportSummaryResponse toSummary(TransactionImport i,List<TransactionImportDtos.ImportErrorResponse> errs){ return new TransactionImportDtos.ImportSummaryResponse(i.getId(),i.getStatus(),i.getTotalRows(),i.getCreatedCount(),i.getSkippedDuplicateCount(),i.getFailedCount(),errs,i.getCreatedAt(),i.getUpdatedAt()); }
    private TransactionImportDtos.ImportErrorResponse toError(TransactionImportError e){ return new TransactionImportDtos.ImportErrorResponse(e.getRowNumber(), e.getMessage()); }
}
