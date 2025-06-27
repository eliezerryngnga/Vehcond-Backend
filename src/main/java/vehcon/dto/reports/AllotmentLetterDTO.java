package vehcon.dto.reports; // Use your actual package name

import java.util.Date;

import lombok.Data;

@Data
public class AllotmentLetterDTO {
    private String letterNumberFormatted;
    private Date letterDate;
    private String mainParagraph;
    private String priceParagraph;
    private String liftingParagraph;
    private String underSecretaryLine;
    private String memoNumberFormatted;
    private String memoDateFormatted;
    private String memoRecipient1;
    private String memoRecipient2;
    private String memoRecipient3Address;
    private String allotteesname;
}