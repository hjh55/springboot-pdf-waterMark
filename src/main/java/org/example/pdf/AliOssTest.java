package org.example.pdf;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;

public class AliOssTest {

    @Test
    public void testOss() {
        String endpoint = "";
        String accessKeyId = "";
        String accessKeySecret = "";
        String bucketName = "authdoc";
        String objectName = "stampdir/picture.png";
        String filePath = "F:\\data\\picture.png";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 读取文件流
            InputStream inputStream = new FileInputStream(filePath);

            // 创建PutObject请求，将文件流上传到OSS。
            ossClient.putObject(bucketName, objectName, inputStream);
            System.out.println("添加成功");
        } catch (OSSException oe) {
            System.out.println("捕获到OSSException，这意味着您的请求已经到达OSS，"
                    + "但由于某种原因被拒绝，并收到了一个错误响应。");
            System.out.println("错误消息：" + oe.getErrorMessage());
            System.out.println("错误码：" + oe.getErrorCode());
            System.out.println("请求ID：" + oe.getRequestId());
            System.out.println("主机ID：" + oe.getHostId());
        } catch (Exception ce) {
            System.out.println("捕获到ClientException，这意味着客户端在尝试与OSS通信时遇到了"
                    + "严重的内部问题，例如无法访问网络。");
            System.out.println("错误消息：" + ce.getMessage());
        } finally {
            // 关闭OSS客户端
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}

