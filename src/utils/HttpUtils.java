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
 * http工具类 http可以使用HttpURLConnection或HttpClient
 * 
 * @author Administrator
 * @date 2014.05.10
 * @version V1.0
 */
public class HttpUtils {

	/**
	 * 通过Get获取网页内容
	 * 
	 * @param url
	 *            如：http://preview.quanjing.com/is002/ev601-025.jpg
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @date 2014.05.10
	 */
	public static Bitmap getHttpGetBitmap(String url)
			throws ClientProtocolException, IOException {
		Bitmap bitmap = null;
		// 新建一个默认的连接
		HttpClient client = new DefaultHttpClient();
		// 新建一个Get方法
		HttpGet get = new HttpGet(url);
		// 得到网络的回应
		HttpResponse response = client.execute(get);

		// 如果服务器响应的是OK的话！
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			// 以下是把网络数据分段读取下来的过程
			InputStream is = response.getEntity().getContent();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		}
		return bitmap;
	}

	/**
	 * 获取网络图片
	 * 
	 * @param urlString
	 *            如：http://f.hiphotos.baidu.com/image/w%3D2048/sign=3
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
		// 使用HttpURLConnection打开连接
		HttpURLConnection urlConn = (HttpURLConnection) imgUrl.openConnection();
		urlConn.setDoInput(true);
		urlConn.connect();
		// 将得到的数据转化成InputStream
		InputStream is = urlConn.getInputStream();
		// 将InputStream转换成Bitmap
		bitmap = BitmapFactory.decodeStream(is);
		is.close();
		return bitmap;
	}
}
