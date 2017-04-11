package utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * http������ http����ʹ��HttpURLConnection��HttpClient
 * 
 * @author Administrator
 * @date 2014.05.10
 * @version V1.0
 */
public class HttpUtils {

	/**
	 * ͨ��Get��ȡ��ҳ����
	 * 
	 * @param url
	 *            �磺http://preview.quanjing.com/is002/ev601-025.jpg
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @date 2014.05.10
	 */
	public static Bitmap getHttpGetBitmap(String url)
			throws ClientProtocolException, IOException {
		Bitmap bitmap = null;
		// �½�һ��Ĭ�ϵ�����
		HttpClient client = new DefaultHttpClient();
		// �½�һ��Get����
		HttpGet get = new HttpGet(url);
		// �õ�����Ļ�Ӧ
		HttpResponse response = client.execute(get);

		// �����������Ӧ����OK�Ļ���
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			// �����ǰ��������ݷֶζ�ȡ�����Ĺ���
			InputStream is = response.getEntity().getContent();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		}
		return bitmap;
	}

	/**
	 * ��ȡ����ͼƬ
	 * 
	 * @param urlString
	 *            �磺http://f.hiphotos.baidu.com/image/w%3D2048/sign=3
	 *            b06d28fc91349547e1eef6462769358
	 *            /d000baa1cd11728b22c9e62ccafcc3cec2fd2cd3.jpg
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @date 2014.05.10
	 */
	public static Bitmap getNetWorkBitmap(String urlString)
			throws MalformedURLException, IOException {
		URL imgUrl = null;
		Bitmap bitmap = null;
		imgUrl = new URL(urlString);
		// ʹ��HttpURLConnection������
		HttpURLConnection urlConn = (HttpURLConnection) imgUrl.openConnection();
		urlConn.setDoInput(true);
		urlConn.connect();
		// ���õ�������ת����InputStream
		InputStream is = urlConn.getInputStream();
		// ��InputStreamת����Bitmap
		bitmap = BitmapFactory.decodeStream(is);
		is.close();
		return bitmap;
	}
}
