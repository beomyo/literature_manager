package com.example.utils;

import com.example.entity.ArticleInfo;
import com.example.service.ArticleService;
import com.example.utils.bigmodel.BigModelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.example.utils.Caj2pdf.Caj2pdf.runCajToPdf;
import static com.example.utils.bigmodel.QwenChat.QwenChat;
import static com.example.utils.neo4jloader.Neo4jLoader.runNeo4jLoader;
import static com.example.utils.pdf2docx.Pdf2docx.runPdfToDocx;
import static com.example.utils.pdf2txt.Pdf2txt.runpdf2txt;
import static com.example.utils.result2mysql.PaperSummarySaver.saveSummary;

@Component
public class AfterUpload {
    @Autowired
    private ArticleService articleService;
    public void file_task(ArticleInfo articleInfo) {
        System.out.println("成功接收上传的文件，提交异步任务,正在处理论文："+articleInfo.getTitle());LogUtil_AfterUpload.log("成功接收上传的文件，提交异步任务,正在处理论文："+articleInfo.getTitle());
        //获取论文前缀
        String oripath = articleInfo.getPatha().split("\\.")[0];//获取前缀
        String cajpath = oripath + ".caj";
        String pdfpath = oripath + ".pdf";
        String docxpath = oripath + ".docx";
        String txtpath = oripath + ".txt";
        String docpath = oripath + ".doc";
        try {runCajToPdf();} catch (IOException | InterruptedException e) {throw new RuntimeException(e);}
        if (new File(pdfpath).exists()) {
            articleInfo.setPathpdf(pdfpath);System.out.println("pdf已存在或已转换为pdf");LogUtil_AfterUpload.log("pdf已存在或已转换为pdf");} else {System.out.println("pdf转换失败");LogUtil_AfterUpload.log("pdf转换失败");}

        runPdfToDocx();
        if (new File(docxpath).exists()) {
            articleInfo.setPathdocx(docxpath);System.out.println("docx已存在或已转换为docx");LogUtil_AfterUpload.log("docx已存在或已转换为docx");} else {System.out.println("docx转换失败");LogUtil_AfterUpload.log("docx转换失败");}

        runpdf2txt();
        if (new File(txtpath).exists()) {
            articleInfo.setPathtxt(txtpath);System.out.println("已转换为txt");LogUtil_AfterUpload.log("已转换为txt");} else {System.out.println("txt转换失败");LogUtil_AfterUpload.log("txt转换失败");}


        boolean ifsuccess=true;

        if(!new File(docxpath).exists()  &&!new File(pdfpath).exists()&&!new File(txtpath).exists()){
            if (new File(cajpath).exists())new File(cajpath).delete();
            ifsuccess=false;
            System.out.println("没有可用文件，回滚删除已有文件和数据");LogUtil_AfterUpload.log("没有可用文件，回滚删除已有文件和数据");
        }

        if(ifsuccess){
            String s = "你好，按照上面的json串格式返回，要求已经包含在字符串内，必须返回json形式";
            if (!new File(cajpath).exists()  &&!new File(pdfpath).exists()){
                //文档文件上传
                try {
                    System.out.println("将文档上传给大模型,会花几十秒");LogUtil_AfterUpload.log("将文档上传给大模型,会花几十秒");
                    String exist= new File(docpath).exists()?docpath:docxpath;
                    String qwenres = BigModelUtil.qwenDocumentUnderstanding(Config.JSON + s, exist);
                    saveSummary(Config.QWEN_DOC_MODEL, articleInfo.getTitle(), qwenres,"0");
                    System.out.println("大模型处理完毕,已经结果存入数据库"+"本次使用的模型是"+Config.QWEN_DOC_MODEL);LogUtil_AfterUpload.log("大模型处理完毕,已经结果存入数据库"+"本次使用的模型是"+Config.QWEN_DOC_MODEL);
                } catch (Exception e) {System.out.println("调用失败" + e.getMessage());LogUtil_AfterUpload.log("调用失败" + e.getMessage());}
            }else {
                //文档文本上传
                try {
                    String content = new String(Files.readAllBytes(Paths.get(txtpath)));
                    if(content.length()>=35000){

                        System.out.println("文本较长,关闭模型比较,将文本上传给大模型,会花几十秒，当前论文字符长度: " + content.length());LogUtil_AfterUpload.log("将文本上传给大模型,会花几十秒，当前论文字符长度: " + content.length());
                        String qwenres = QwenChat(Config.QWEN_MODEL1,content+Config.JSON+s);
                        saveSummary(Config.QWEN_MODEL1, articleInfo.getTitle(), qwenres,"0");
                        System.out.println("大模型处理完毕,已将结果存入数据库"+"本次使用的模型是"+Config.QWEN_MODEL1);LogUtil_AfterUpload.log("大模型处理完毕,已将结果存入数据库"+"本次使用的模型是"+Config.QWEN_MODEL1);

                    }if(content.length()<35000){

                        System.out.println("文本较短,开启模型比较,将文本上传给第一个大模型,会花几十秒，当前论文字符长度: " + content.length());LogUtil_AfterUpload.log("将文本上传给第一个大模型,会花几十秒，当前论文字符长度: " + content.length());
                        String qwenres = QwenChat(Config.QWEN_MODEL1,content+Config.JSON+s);
                        saveSummary(Config.QWEN_MODEL1, articleInfo.getTitle(), qwenres,"0");

                        System.out.println("成功获取结果！将文本上传给第二个大模型,会花几十秒" );LogUtil_AfterUpload.log("将文本上传给第二个大模型,会花几十秒");
                        String qwenres2 = QwenChat(Config.QWEN_MODEL3,content+Config.JSON+s);
                        saveSummary(Config.QWEN_MODEL3, articleInfo.getTitle(), qwenres2,"0");

                        System.out.println("成功获取结果！将文本上传给评判模型,会花几十秒" );LogUtil_AfterUpload.log("将文本上传给评判大模型,会花几十秒");
                        String end=qwenres+qwenres2;
                        String qwenres3 = QwenChat(Config.QWEN_MODEL4,end+Config.JSON2+"按照上面的json串格式返回，要求已经包含在字符串内，从上面的两个结果中选出每个字段最好的，只填1或者2，1代表第一个好，2代表第二个好，不能全部都选一个模型，必须返回json形式");
                        saveSummary(Config.QWEN_MODEL2, articleInfo.getTitle(), qwenres3,"1");

                        System.out.println("成功获取结果！大模型处理完毕,已将结果存入数据库"+"本次使用的模型是:"+Config.QWEN_MODEL1+"和"+Config.QWEN_MODEL3+" 评判模型为:"+Config.QWEN_MODEL2);LogUtil_AfterUpload.log("成功获取结果！大模型处理完毕,已将结果存入数据库"+"本次使用的模型是:"+Config.QWEN_MODEL1+"和"+Config.QWEN_MODEL3+" 评判模型为:"+Config.QWEN_MODEL2);
                    }
                    } catch (IOException e) {System.out.println("调用失败" + e.getMessage());LogUtil_AfterUpload.log("调用失败" + e.getMessage());} catch (
                        Exception e) {
                    throw new RuntimeException(e);
                }
            }
            articleService.saveArticle(articleInfo);
            System.out.println("成功将论文信息存入mysql");LogUtil_AfterUpload.log("成功将论文信息存入mysql");
            runNeo4jLoader(false,articleInfo.getTitle());
            System.out.println("图谱更新完毕");LogUtil_AfterUpload.log("图谱更新完毕");
            System.out.println("----------------------------------------------------");LogUtil_AfterUpload.log("----------------------------------------------------");
        }
    }
}