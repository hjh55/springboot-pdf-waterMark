package org.example.pdf.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdf.model.vo.ResponseVo;
import org.example.pdf.model.dto.Pdf;
import org.example.pdf.service.PdfService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@Slf4j
@RequestMapping("/pdf/order")
@RequiredArgsConstructor
public class PdfController {

    final PdfService pdfservice;
    @PostMapping(value = "/fill")
    public ResponseVo<?> fillContent(@Valid @RequestBody Pdf pdf ){
        try {
            pdfservice.fillContent(pdf);
            return new ResponseVo<>(200, "成功", true, null);
        } catch (Exception e) {
            // 使用日志库记录异常信息
            log.error("Failed to fill content", e);
            return new ResponseVo<>(500, "失败", false, null);
        }
    }



}
