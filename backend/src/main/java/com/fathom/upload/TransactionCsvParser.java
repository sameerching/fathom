package com.fathom.upload;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class TransactionCsvParser {
    private static final Set<String> REQUIRED = Set.of("transactionDate", "direction", "amount", "rawDescription", "transactionType");

    public List<org.apache.commons.csv.CSVRecord> parse(MultipartFile file) {
        try {
            CSVParser parser = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setIgnoreEmptyLines(true).setTrim(true).build()
                    .parse(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
            if (!parser.getHeaderMap().keySet().containsAll(REQUIRED)) throw new IllegalArgumentException("Missing required CSV headers");
            return parser.getRecords();
        } catch (IOException e) {
            throw new IllegalArgumentException("Unreadable CSV file");
        }
    }
}
