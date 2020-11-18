package vn.easyca.signserver.webapp.web.rest.controller;

import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;
import vn.easyca.signserver.webapp.service.TransactionService;
import vn.easyca.signserver.webapp.service.dto.TransactionReportDTO;
import vn.easyca.signserver.webapp.web.rest.errors.BadRequestAlertException;
import vn.easyca.signserver.webapp.service.dto.TransactionDTO;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static vn.easyca.signserver.webapp.utils.DateTimeUtils.convertToInstant;

/**
 * REST controller for managing {@link vn.easyca.signserver.webapp.domain.Transaction}.
 */
@RestController
@RequestMapping("/api")
public class TransactionResource {

    private final Logger log = LoggerFactory.getLogger(TransactionResource.class);

    private final String ENTITY_NAME = "transaction";
    private static final String OUTPUT_FILE = "test.pdf";
    private static final String UTF_8 = "UTF-8";
    @Autowired
    SpringTemplateEngine templateEngine;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;
    private final TransactionService transactionService;

    public TransactionResource( TransactionService transactionService) {
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
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/transactions")
    public ResponseEntity<TransactionDTO> updateTransaction(@RequestBody TransactionDTO transactionDTO) throws URISyntaxException {
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
    public ResponseEntity<List<TransactionDTO>> getAllTransactionsByFilter(Pageable pageable, @RequestParam(required = false) String api, @RequestParam(required = false) String triggerTime, @RequestParam(required = false) String code, @RequestParam(required = false) String message, @RequestParam(required = false) String data, @RequestParam(required = false) String type ) {
        log.debug("REST request to get a page of Transactions");
        Page<TransactionDTO> page = transactionService.getByFilter(pageable , api,triggerTime,code,message,data,type);
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
     * @param startdate, enddate, type from transaction
     * @return the total request success and totals request fail .
     */


    @GetMapping("/transactions/report/{startDate}/{endDate}/{type}")
    public ResponseEntity<TransactionReportDTO> getAllTransactionBetweenDate(@PathVariable("startDate") String startdate,
                                                             @PathVariable("endDate") String enddate,
                                                             @PathVariable("type") String type) throws ParseException {
        log.debug("REST request to get all Transactions beween date and type ");
        TransactionReportDTO transactionReportDTO = new TransactionReportDTO();
        int totalsuccess = 0;
        int totalfalse = 0;
        List<TransactionDTO> transactionDTOList = new ArrayList<>();
        transactionDTOList = transactionService.findTransactionType(startdate, enddate, type);
        for (TransactionDTO item : transactionDTOList) {
            if (item.getCode().equals("200")) {
                totalsuccess += 1;
            } else {
                totalfalse += 1;
            }
        }
        if (totalfalse != 0 || totalsuccess != 0) {
            transactionReportDTO.setTotalfail(totalfalse);
            transactionReportDTO.setTotalsuccess(totalsuccess);

        }
        return ResponseEntity.ok().body(transactionReportDTO);
    }


    @GetMapping("/transactions/exportPDF")
    public void  savePDF() throws IOException, DocumentException {
        log.debug("REST request to export  PDF Transactions ");

        List<TransactionDTO> listTranscation=new ArrayList<>(3000);
        for (int i = 0; i < 3000; i++) {
            TransactionDTO transactionDTO=new TransactionDTO();
            transactionDTO.setApi("api"+i);
            transactionDTO.setCode("code"+i);
            transactionDTO.setData(" components "+i);
            transactionDTO.setMessage("message  "+i);
            transactionDTO.setTriggerTime(null);
            transactionDTO.setType("SYSTEM");
            listTranscation.add(transactionDTO);
        }

        Context context=new Context();
        context.setVariable("listReport",listTranscation);

        String renderdHtmlContext=templateEngine.process("template",context);
        String xHtml=convertToXhtml(renderdHtmlContext);
        ITextRenderer renderer=new ITextRenderer();

        String baseUrl= FileSystems.getDefault()
            .getPath("src","main","resources","templates")
            .toUri()
            .toURL()
            .toString();
        renderer.setDocumentFromString(xHtml,baseUrl);
        renderer.layout();

        OutputStream outputStream=new FileOutputStream("src//test17.pdf");
        renderer.createPDF(outputStream);
        outputStream.close();

    }
    private String convertToXhtml(String html) throws UnsupportedEncodingException {
        Tidy tidy = new Tidy();
        tidy.setInputEncoding(UTF_8);
        tidy.setOutputEncoding(UTF_8);
        tidy.setXHTML(true);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(html.getBytes(UTF_8));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        tidy.parseDOM(inputStream, outputStream);
        return outputStream.toString(UTF_8);
    }

}
