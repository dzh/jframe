/**
 * 
 */
package jframe.aliyun.service.oss;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectResult;

/**
 * @author dzh
 * @date Feb 26, 2016 12:04:30 PM
 * @since 1.0
 */
public class TestOSSService {

    @Test
    public void test() throws OSSException, ClientException, IOException {
        OSSClient client = new OSSClient("oss-cn-hangzhou.aliyuncs.com", "", "", "");
        // BucketInfo info = client.getBucketInfo("edrmry");
        boolean exists = client.doesBucketExist("edrmry");
        System.out.println(exists);
        // System.out.println(client.listBuckets().size());
        // client.createBucket("dzh1");
        PutObjectResult r = client.putObject("edrmry", "dzh1.jpg", new FileInputStream("/Users/dzh/Pictures/8.pic.jpg"));
        System.out.println(r.getETag());
        OSSObject o = client.getObject("edrmry", "dzh1");
        InputStream is = o.getObjectContent();

        FileOutputStream fos = new FileOutputStream("/Users/dzh/Pictures/8.pic.2.jpg");
        int len = 0;
        byte[] buf = new byte[32];
        while ((len = is.read(buf)) != -1) {
            fos.write(buf, 0, len);
        }
        fos.flush();
        fos.close();
    }

}
