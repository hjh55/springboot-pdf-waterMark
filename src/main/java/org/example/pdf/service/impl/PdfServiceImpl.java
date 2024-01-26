package org.example.pdf.service.impl;


import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.*;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.example.pdf.model.dto.Pdf;
import org.example.pdf.service.PdfService;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;

@Service
public class PdfServiceImpl implements PdfService {
    @Value("#{'${files.pdfMuban}'}")
    private String pdfMuban;

    @Value("#{'${paths.tmpPath}'}")
    private String tmpPath;

    @Value("#{'${files.form}'}")
    private String form;

    @Value("#{'${files.waterPicture}'}")
    private String waterPicture;

    @Value("#{'${paths.resultPath}'}")
    private String resultPath;
    public void fillContent(Pdf pdf) {
        //读取现有PDF文件和创建新的输出文件
        PdfReader reader;
        try {
            reader = new PdfReader(pdfMuban);
        } catch (IOException e) {
            throw new RuntimeException("读取模板发生异常", e);
        }
        FileOutputStream fos;
        String tmpFile;
        try {
            tmpFile = tmpPath + UUID.randomUUID() + ".pdf";
            fos = new FileOutputStream(tmpFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("暂存文件路径不存在", e);
        }
        //创建PdfStamper以便在现有PDF文件上添加内容
        PdfStamper stamper;
        try {
            stamper = new PdfStamper(reader, fos);
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("创建填充模板的数据流异常", e);
        }
        AcroFields content = stamper.getAcroFields();

        //添加中文字体支持
        BaseFont zt;
        try {
            zt = BaseFont.createFont(form, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("在字体方面出现异常", e);
        }
        content.addSubstitutionFont(zt);

        //准备填充的数据
        Map<String, Object> data = new HashMap<>();
        data.put("Name", pdf.getName());
        data.put("IDCard", pdf.getIdCardNum());
        data.put("order_num", pdf.getOrderNum());
        // 使用自定义格式化器将日期转换为字符串且要符合格式
        LocalDate localDate = LocalDate.parse(pdf.getTime());
        String formattedDate = localDate.format(DateTimeFormatter.ofPattern("yyyy年M月d日"));
        data.put("Time", formattedDate);

        //遍历数据并填充字段
        for (String key : data.keySet()) {
            try {
                content.setField(key, data.get(key).toString());
            } catch (IOException | DocumentException e) {
                throw new RuntimeException("在在填充字段时有异常", e);
            }
        }

        //设置表单展平以避免后续编辑
        stamper.setFormFlattening(true);

        //关闭PdfStamper和输出流
        try {
            stamper.close();
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("在关闭写入流时有异常", e);
        }

        try {
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException("关闭暂存文件流时异常", e);
        }

        //添加水印到生成的PDF文件
        waterMark(tmpFile, waterPicture ,pdf.getOrderNum());
    }

    /**
     * 添加水印到PDF文件的方法的实现
     *@param  pdfPath 需要加水印的pdf文件路径
     *@param  waterMarkPath 水印图片的文件路径
     *@param orderNum 订单号用来做文件名用
     */
    private void waterMark(String pdfPath, String waterMarkPath ,String orderNum)  {
        //读取水印图像文件
        InputStream waterMarkStream ;
        try {
            waterMarkStream = new FileInputStream(waterMarkPath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("水印图片路径不存在",e);
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            IOUtils.copy(waterMarkStream, output);
        } catch (IOException e) {
            throw new RuntimeException("将水印图片复制到输出流中时异常",e);
        }
        //读取PDF文件
        InputStream inputStream ;
        try {
            inputStream = new FileInputStream(pdfPath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("输出结果的路径不存在",e);
        }
        PdfReader reader ;
        try {
            reader = new PdfReader(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("创建一个输入流时异常",e);
        }
        //创建PdfStamper以便在现有PDF文件上添加水印，并指定输出路径
        PdfStamper stamper ;
        String result = resultPath + orderNum + ".pdf";
        try {
            stamper = new PdfStamper(reader, Files.newOutputStream(Path.of(result)));
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("创建到结果的输出流异常",e);
        }
        //创建Image对象，加载水印图像
        Image image ;
        try {
            image = Image.getInstance(output.toByteArray());
        } catch (IOException | BadElementException e) {
            throw new RuntimeException("无法解析图像",e);
        }
        //获取PDF页面并在其上添加水印
        PdfContentByte under = stamper.getOverContent(1);
        image.scaleToFit(100, 100);
        image.setAbsolutePosition(450, 330);
        try {
            under.addImage(image);
        } catch (DocumentException e) {
            throw new RuntimeException("在PDF添加水印时异常",e);
        }
        //关闭PdfStamper和PdfReader
        try {
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("在关闭写入流时有异常",e);
        }
        try {
            stamper.close();
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("在关闭写入流时有异常",e);
        }
        reader.close();
    }
}



