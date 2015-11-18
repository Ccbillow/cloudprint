package cn.cqupt.util;

import com.aliyun.openservices.ClientConfiguration;
import com.aliyun.openservices.ClientException;
import com.aliyun.openservices.ServiceException;
import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.OSSErrorCode;
import com.aliyun.openservices.oss.OSSException;
import com.aliyun.openservices.oss.model.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.util.List;

/**
 * Created by Cbillow on 15/11/12.
 */
public class OSSUtils {

    /**
     * 得到一个oss连接
     * @return
     */
    public static OSSClient getOSSClient() {
        ClientConfiguration conf = new ClientConfiguration();

        // 设置HTTP最大连接数为10
        conf.setMaxConnections(10);

        // 设置TCP连接超时为5000毫秒
        conf.setConnectionTimeout(5000);

        // 设置最大的重试次数为3
        conf.setMaxErrorRetry(3);

        // 设置Socket传输数据超时的时间为2000毫秒
        conf.setSocketTimeout(2000);

        OSSClient client = new OSSClient(CPConstant.END_POINT, CPConstant.ACCESS_ID, CPConstant.ACCESS_KEY, conf);

        return client;
    }

    /**
     * 创建Bucket
     *
     * @param client  OSSClient对象
     * @param bucketName  BUCKET名
     * @throws OSSException
     * @throws ClientException
     */
    public static void createBucket(OSSClient client, String bucketName)throws OSSException, ClientException {
        try{
            client.createBucket(bucketName);
        }catch(ServiceException e){
            if(!OSSErrorCode.BUCKES_ALREADY_EXISTS.equals(e.getErrorCode())){
                throw e;
            }
        }
    }

    /**
     * 删除一个Bucket和其中的Objects
     *
     * @param client  OSSClient对象
     * @param bucketName  Bucket名
     * @throws OSSException
     * @throws ClientException
     */
    public static void deleteBucket(OSSClient client, String bucketName)throws OSSException, ClientException{
        ObjectListing ObjectListing = client.listObjects(bucketName);
        List<OSSObjectSummary> listDeletes = ObjectListing.getObjectSummaries();
        for(int i = 0; i < listDeletes.size(); i++){
            String objectName = listDeletes.get(i).getKey();
            System.out.println("objectName = " + objectName);
            //如果不为空，先删除bucket下的文件
            client.deleteObject(bucketName, objectName);
        }
        client.deleteBucket(bucketName);
    }

    /**
     * 删除一个Bucket下指定的Object
     *
     * @param client    OSSClient对象
     * @param objectKey 指定的object名称
     */
    public static void deleteObject(OSSClient client, String objectKey) {
        client.deleteObject(CPConstant.BUCKET_NAME, objectKey);

        // 构造ListObjectsRequest请求
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(CPConstant.BUCKET_NAME);

        // 递归列出指定用户（目录根据phone区别）目录下的所有文件
        listObjectsRequest.setPrefix(objectKey.substring(0, 11));

        ObjectListing listing = client.listObjects(listObjectsRequest);

        //如果该用户没有文件，则将这个文件夹删除
        if (listing.getObjectSummaries().size() <= 1) {
            client.deleteObject(CPConstant.BUCKET_NAME, objectKey.substring(0, 12));
        }
    }

    /**
     * 把Bucket设置成所有人可读
     *
     * @param client  OSSClient对象
     * @param bucketName  Bucket名
     * @throws OSSException
     * @throws ClientException
     */
    public static void setBucketPublicReadable(OSSClient client, String bucketName)throws OSSException, ClientException{
        //创建bucket
        client.createBucket(bucketName);

        //设置bucket的访问权限， public-read-write权限
        client.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
    }

    /**
     * 上传文件
     *
     * @param client  OSSClient对象
     * @param bucketName  Bucket名
     * @param Objectkey  上传到OSS起的名
     * @param filename  本地文件名
     * @throws OSSException
     * @throws ClientException
     * @throws FileNotFoundException
     */
    public static void uploadFile(OSSClient client, String bucketName, String Objectkey, String filename)
            throws OSSException, ClientException, FileNotFoundException {
        File file = new File(filename);
        ObjectMetadata objectMeta = new ObjectMetadata();
        objectMeta.setContentLength(file.length());
        //判断上传类型，多的可根据自己需求来判定
        if (filename.endsWith("xml")) {
            objectMeta.setContentType("text/xml");
        }
        else if (filename.endsWith("jpg")) {
            objectMeta.setContentType("image/jpeg");
        }
        else if (filename.endsWith("png")) {
            objectMeta.setContentType("image/png");
        }

        InputStream input = new FileInputStream(file);
        client.putObject(bucketName, Objectkey, input, objectMeta);
    }
    public static void uploadFile(OSSClient client, String bucketName, String Objectkey, CommonsMultipartFile file)
            throws OSSException, ClientException, IOException {
        ObjectMetadata objectMeta = new ObjectMetadata();
        objectMeta.setContentLength(file.getSize());
        //判断上传类型，多的可根据自己需求来判定
//        if (filename.endsWith("xml")) {
//            objectMeta.setContentType("text/xml");
//        }
//        else if (filename.endsWith("jpg")) {
//            objectMeta.setContentType("image/jpeg");
//        }
//        else if (filename.endsWith("png")) {
//            objectMeta.setContentType("image/png");
//        }

        InputStream input = file.getInputStream();
        client.putObject(bucketName, Objectkey, input, objectMeta);
    }

    /**
     *  下载文件
     *
     * @param client  OSSClient对象
     * @param bucketName  Bucket名
     * @param Objectkey  上传到OSS起的名
     * @param filename 文件下载到本地保存的路径
     * @throws OSSException
     * @throws ClientException
     */
    public static void downloadFile(OSSClient client, String bucketName, String Objectkey, String filename)
            throws OSSException, ClientException {
        client.getObject(new GetObjectRequest(bucketName, Objectkey),
                new File(filename));
    }
}
