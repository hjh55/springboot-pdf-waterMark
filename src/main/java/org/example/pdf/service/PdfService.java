package org.example.pdf.service;


import org.example.pdf.model.dto.Pdf;


public interface PdfService {
     /**
      * 填充 PDF模板并加盖章。
      *
      * @param pdf 要填充内容的 Pdf 对象
      */
     void fillContent(Pdf pdf) ;

}
