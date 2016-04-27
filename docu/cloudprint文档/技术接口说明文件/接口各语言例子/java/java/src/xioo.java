import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

/*
����:		web.cr6868.com HTTP�ӿ� ���Ͷ���

˵��:		http://web.cr6868.com/asmx/smsservice.aspx?name=��¼��&pwd=�ӿ�����&mobile=�ֻ�����&content=����&sign=ǩ��&stime=����ʱ��&type=pt&extno=�Զ�����չ��
*/
public class xioo {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		//��������
		String content = " JAVAʾ������"; 
		String sign="ǩ��";
		
		// ����StringBuffer�������������ַ���
		StringBuffer sb = new StringBuffer("http://web.cr6868.com/asmx/smsservice.aspx?");

		// ��StringBuffer׷���û���
		sb.append("name=test");

		// ��StringBuffer׷�����루��½��ҳ�棬�ڹ�������--��������--�ӿ����룬��28λ�ģ�
		sb.append("&pwd=CEE4D6CC34577FB24D1726F8AFEB");

		// ��StringBuffer׷���ֻ�����
		sb.append("&mobile=18916409691");

		// ��StringBuffer׷����Ϣ����תURL��׼��
		sb.append("&content="+URLEncoder.encode(content,"UTF-8"));
		
		//׷�ӷ���ʱ�䣬��Ϊ�գ�Ϊ��Ϊ��ʱ����
		sb.append("&stime=");
		
		//��ǩ��
		sb.append("&sign="+URLEncoder.encode(sign,"UTF-8"));
		
		//typeΪ�̶�ֵpt  extnoΪ��չ�룬����Ϊ���� ��Ϊ��
		sb.append("&type=pt&extno=");
		// ����url����
		//String temp = new String(sb.toString().getBytes("GBK"),"UTF-8");
		System.out.println("sb:"+sb.toString());
		URL url = new URL(sb.toString());

		// ��url����
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		// ����url����ʽ ��get�� ���� ��post��
		connection.setRequestMethod("POST");

		// ����
		InputStream is =url.openStream();

		//ת������ֵ
		String returnStr = xioo.convertStreamToString(is);
		
		// ���ؽ��Ϊ��0��20140009090990,1���ύ�ɹ��� ���ͳɹ�   �����˵���ĵ�
		System.out.println(returnStr);
		// ���ط��ͽ��

		

	}
	/**
	 * ת������ֵ����ΪUTF-8��ʽ.
	 * @param is
	 * @return
	 */
	public static String convertStreamToString(InputStream is) {    
        StringBuilder sb1 = new StringBuilder();    
        byte[] bytes = new byte[4096];  
        int size = 0;  
        
        try {    
        	while ((size = is.read(bytes)) > 0) {  
                String str = new String(bytes, 0, size, "UTF-8");  
                sb1.append(str);  
            }  
        } catch (IOException e) {    
            e.printStackTrace();    
        } finally {    
            try {    
                is.close();    
            } catch (IOException e) {    
               e.printStackTrace();    
            }    
        }    
        return sb1.toString();    
    }

}
