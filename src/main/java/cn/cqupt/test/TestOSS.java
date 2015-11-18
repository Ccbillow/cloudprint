package cn.cqupt.test;

import cn.cqupt.util.CPConstant;
import cn.cqupt.util.OSSUtils;
import com.aliyun.openservices.ClientException;
import com.aliyun.openservices.ServiceException;
import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.OSSErrorCode;
import com.aliyun.openservices.oss.OSSException;
import com.aliyun.openservices.oss.model.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.util.List;

public class TestOSS {
    /**
     * 阿里云ACCESS_ID
     */
    private static String ACCESS_ID = "oGvTjqOt1zPbVlgr";
    /**
     * 阿里云ACCESS_KEY
     */
    private	 static String ACCESS_KEY = "SI6vc6P15DSBTX6owwzCNyIekWRcW2";

    /**
     * 阿里云BUCKET_NAME  OSS
     */
    private static String BUCKET_NAME = "cquptcloudprint";

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
    private static void deleteBucket(OSSClient client, String bucketName)throws OSSException, ClientException{
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
     * 把Bucket设置成所有人可读
     *
     * @param client  OSSClient对象
     * @param bucketName  Bucket名
     * @throws OSSException
     * @throws ClientException
     */
    private static void setBucketPublicReadable(OSSClient client, String bucketName)throws OSSException, ClientException{
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
    private static void downloadFile(OSSClient client, String bucketName, String Objectkey, String filename)
            throws OSSException, ClientException {
        client.getObject(new GetObjectRequest(bucketName, Objectkey),
                new File(filename));
    }


    public static void main(String[] args) {
        String Objectkey = "18580741650/" + "程涛-重庆邮电大学.pdf";

        String uploadFilePath = "/Users/Cbillow/Documents/docu/study/简历/程涛-重庆邮电大学.pdf";
        String downloadFilePath = "/Users/Cbillow/Documents/docu/study/简历/程涛-重庆邮电大学1.pdf";

        // 使用默认的OSS服务器地址创建OSSClient对象,不叫OSS_ENDPOINT代表使用杭州节点，青岛节点要加上不然包异常
        OSSClient client = new OSSClient(CPConstant.END_POINT, ACCESS_ID, ACCESS_KEY);

        //如果你想配置OSSClient的一些细节的参数，可以在构造OSSClient的时候传入ClientConfiguration对象。
        //ClientConfiguration是OSS服务的配置类，可以为客户端配置代理，最大连接数等参数。
        //具体配置看http://aliyun_portal_storage.oss.aliyuncs.com/oss_api/oss_javahtml/OSSClient.html#id2
        //ClientConfiguration conf = new ClientConfiguration();
        //OSSClient client = new OSSClient(OSS_ENDPOINT, ACCESS_ID, ACCESS_KEY, conf);


        try {
//            createBucket(client, BUCKET_NAME);
            setBucketPublicReadable(client, BUCKET_NAME);

            System.out.println("正在上传...");
            uploadFile(client, BUCKET_NAME, Objectkey, uploadFilePath);

//            System.out.println("正在下载...");
//            downloadFile(client, BUCKET_NAME, Objectkey, downloadFilePath);
            OSSUtils.deleteObject(client, Objectkey);
        }catch(Exception e){
            e.printStackTrace();
        } finally {
//            deleteBucket(client, BUCKET_NAME);
        }
    }
}
