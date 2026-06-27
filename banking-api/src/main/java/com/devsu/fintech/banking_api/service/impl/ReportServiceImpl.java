package com.devsu.fintech.banking_api.service.impl;

import com.devsu.fintech.banking_api.dto.ReportTransactionsDTO;
import com.devsu.fintech.banking_api.excepcion.ResourceNotFoundException;
import com.devsu.fintech.banking_api.model.Client;
import com.devsu.fintech.banking_api.model.Transaction;
import com.devsu.fintech.banking_api.model.TransactionType;
import com.devsu.fintech.banking_api.repository.ClientRepository;
import com.devsu.fintech.banking_api.repository.TransactionRepository;
import com.devsu.fintech.banking_api.service.ReportService;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl  implements ReportService {

    private final ClientRepository clientRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public ReportTransactionsDTO.StatusAcountResponse generate(String clientId, LocalDate from, LocalDate to) {
        ReportTransactionsDTO.Resume resume = buildResume(clientId,from,to);
        byte[] pdf = buildPdf(resume);
        return new ReportTransactionsDTO.StatusAcountResponse(resume, Base64.getEncoder().encodeToString(pdf));
    }

    @Override
    public byte[] generatePdf(String clientId, LocalDate from, LocalDate to) {
        ReportTransactionsDTO.Resume resume = buildResume(clientId,from,to);
        return buildPdf(resume);
    }

    private ReportTransactionsDTO.Resume buildResume(String clientId, LocalDate from, LocalDate to) {
        Client client = clientRepository.findByClientId(clientId)
                .orElseThrow(()-> new ResourceNotFoundException("Client not found : " + clientId));

        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.plusDays(1).atStartOfDay();

        List<Transaction> transactions = transactionRepository.reportAccountStatement(clientId,start,end);

        Predicate<Transaction> isCredit = transaction-> transaction.getTransactionType() == TransactionType.DEPOSITO;
        Predicate<Transaction> isDebit = transaction-> transaction.getTransactionType() == TransactionType.RETIRO;

        BigDecimal totalCredits = transactions.stream()
                .filter(isCredit)
                .map(Transaction::getAmount)
                .map(BigDecimal::abs)
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        BigDecimal totalDebits = transactions.stream()
                .filter(isDebit)
                .map(Transaction::getAmount)
                .map(BigDecimal::abs)
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        List<ReportTransactionsDTO.TransactionDetail> details = transactions.stream()
                .map(m->new ReportTransactionsDTO.TransactionDetail(
                        m.getDate(),
                        m.getAccount().getClient().getName(),
                        m.getAccount().getAccountNumber(),
                        m.getAccount().getAccountType(),
                        m.getAccount().getInitialBalance(),
                        m.getAccount().getStatus(),
                        m.getTransactionType(),
                        m.getAmount(),
                        m.getBalance()))
                .toList();
        return new ReportTransactionsDTO.Resume(client.getName(),from,to,totalCredits,totalDebits,details);
    }

    private byte[] buildPdf(ReportTransactionsDTO.Resume resume) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4.rotate(), 24, 24, 24, 24);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            Color titleColor = new Color(33, 33, 33);
            Color headColor = new Color(255, 255, 255);
            Color bodyColor = new Color(0, 0, 0);

            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD, titleColor);
            Font headFont = new Font(Font.HELVETICA, 10, Font.BOLD, headColor);
            Font bodyFont = new Font(Font.HELVETICA, 9, Font.NORMAL, bodyColor);

            Paragraph t = new Paragraph("Estado de Cuenta", titleFont);
            t.setAlignment(Element.ALIGN_CENTER);
            doc.add(t);
            doc.add(new Paragraph(" "));

            doc.add(new Paragraph("Cliente: " + resume.client()));
            doc.add(new Paragraph("Periodo: " + resume.from() + " a " + resume.to()));
            doc.add(new Paragraph("Total creditos: " + resume.totalCredits()));
            doc.add(new Paragraph("Total debitos: " + resume.totalDebits()));
            doc.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(9);
            table.setWidthPercentage(100);
            String[] headers = {"Fecha", "Cliente", "Numero Cuenta", "Tipo", "Saldo Inicial",
                    "Estado", "Tipo Mov.", "Movimiento", "Saldo Disp."};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headFont));
                cell.setBackgroundColor(new Color(60, 60, 60));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (ReportTransactionsDTO.TransactionDetail detail : resume.transactions()) {
                table.addCell(new Phrase(detail.date().format(formatter), bodyFont));
                table.addCell(new Phrase(detail.client(), bodyFont));
                table.addCell(new Phrase(detail.accountNumber(), bodyFont));
                table.addCell(new Phrase(detail.accountType().name(), bodyFont));
                table.addCell(new Phrase(String.valueOf(detail.initialBalance()), bodyFont));
                table.addCell(new Phrase(detail.status() ? "True" : "False", bodyFont));
                table.addCell(new Phrase(detail.transactionType().name(), bodyFont));
                table.addCell(new Phrase(String.valueOf(detail.amount()), bodyFont));
                table.addCell(new Phrase(String.valueOf(detail.balance()), bodyFont));
            }
            doc.add(table);

            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Error generando PDF: " + e.getMessage(), e);
        }
    }
}
