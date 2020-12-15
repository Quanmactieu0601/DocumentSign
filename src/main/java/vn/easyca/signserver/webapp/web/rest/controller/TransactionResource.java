package vn.easyca.signserver.webapp.web.rest.controller;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vn.easyca.signserver.webapp.service.TransactionService;
import vn.easyca.signserver.webapp.service.dto.TransactionDTO;
import vn.easyca.signserver.webapp.web.rest.errors.BadRequestAlertException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * REST controller for managing {@link vn.easyca.signserver.webapp.domain.Transaction}.
 */
@RestController
@RequestMapping("/api")
public class TransactionResource {

    private final Logger log = LoggerFactory.getLogger(TransactionResource.class);
    private final String ENTITY_NAME = "transaction";
    static final String fileName = "src/main/resources/templates/transactionReport/TransactionReport.jrxml";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;
    private final TransactionService transactionService;

    public TransactionResource(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * {@code POST  /transactions} : Create a new transaction.
     *
     * @param transactionDTO the transactionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new transactionDTO, or with status {@code 400 (Bad Request)} if the transaction has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/transactions")
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO transactionDTO) throws URISyntaxException {
        log.debug("REST request to save Transaction : {}", transactionDTO);
        if (transactionDTO.getId() != null) {
            throw new BadRequestAlertException("A new transaction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TransactionDTO result = transactionService.save(transactionDTO);
        return ResponseEntity.created(new URI("/api/transactions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /transactions} : Updates an existing transaction.
     *
     * @param transactionDTO the transactionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transactionDTO,
     * or with status {@code 400 (Bad Request)} if the transactionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the transactionDTO couldn't be updated.
     */
    @PutMapping("/transactions")
    public ResponseEntity<TransactionDTO> updateTransaction(@RequestBody TransactionDTO transactionDTO) {
        log.debug("REST request to update Transaction : {}", transactionDTO);
        if (transactionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        TransactionDTO result = transactionService.save(transactionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, transactionDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /transactions} : get all the transactions.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of transactions in body.
     */
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDTO>> getAllTransactions(Pageable pageable) {
        log.debug("REST request to get a page of Transactions");
        Page<TransactionDTO> page = transactionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/transactions/search")
    public ResponseEntity<List<TransactionDTO>> getAllTransactionsByFilter(Pageable pageable, @RequestParam(required = false) String api, @RequestParam(required = false) String triggerTime, @RequestParam(required = false) String status, @RequestParam(required = false) String message, @RequestParam(required = false) String data, @RequestParam(required = false) String type, @RequestParam(required = false)  String host, @RequestParam(required = false) String method, @RequestParam(required = false) String createdBy, @RequestParam(required = false) String fullName, @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate, @RequestParam(required = false) String action, @RequestParam(required = false) String extension) throws ParseException {
        log.debug("REST request to get a page of Transactions");
        Page<TransactionDTO> page = transactionService.getByFilter(pageable, api, triggerTime, status, message, data, type, host, method, createdBy, fullName, startDate, endDate, action, extension);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /transactions/:id} : get the "id" transaction.
     *
     * @param id the id of the transactionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the transactionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/transactions/{id}")
    public ResponseEntity<TransactionDTO> getTransaction(@PathVariable Long id) {
        log.debug("REST request to get Transaction : {}", id);
        Optional<TransactionDTO> transactionDTO = transactionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(transactionDTO);
    }

    /**
     * {@code DELETE  /transactions/:id} : delete the "id" transaction.
     *
     * @param id the id of the transactionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        log.debug("REST request to delete Transaction : {}", id);
        transactionService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code get all transaction   /transaction/report}
     *
     * @param startDate, enddate, type from transaction
     * @return the total request success and totals request fail .
     */
    @GetMapping("/transactions/report")
    public ResponseEntity<Map<String, BigInteger>> getAllTransactionBetweenDate(@RequestParam("startDate") String startDate,
                                                                                @RequestParam("endDate") String endDate,
                                                                                @RequestParam("type") String type) {
        log.debug("REST request to get all Transactions beween date and type ");
        Map<String, BigInteger> transactionDTOList = transactionService.findTransactionType(startDate, endDate, type);
        return ResponseEntity.ok().body(transactionDTOList);
    }

    /*
     *@code export file pdf transaction report
     * * @param startdate, enddate, type from transaction
     * @return the file pdf transaction report
     */
    @GetMapping("/transactions/exportPDFJasper")
    public void exportPDF(@RequestParam("startDate") String startDate,
                          @RequestParam("endDate") String endDate,
                          @RequestParam("type") String type, HttpServletResponse response) throws JRException, IOException {

        log.debug("REST request to export  PDF Transactions ");
        Map<String, Object> parameter = new HashMap<>();
        List<TransactionDTO> listTranscation = transactionService.findTransaction(startDate, endDate, type);
        JRBeanCollectionDataSource TransactionCollectionDataSource = new JRBeanCollectionDataSource(listTranscation);
        parameter.put("transactionDataSource", TransactionCollectionDataSource);
        parameter.put("title", "Transaction Report");
        JasperReport jasperDesign = JasperCompileManager.compileReport(fileName);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperDesign, parameter, new JREmptyDataSource());
        OutputStream out = response.getOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, out);
        response.setContentType("application/pdf");
        response.addHeader("Content-Disposition", "inline; filename=TransactionReport.pdf;");
        log.debug("Export file transaction report.pdf success !");
    }
}
