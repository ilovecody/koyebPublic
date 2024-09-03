package ray.lee.myRestApi.common.others;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import ray.lee.myRestApi.common.utilities.MyRestConstants;

public class MyHttpServletRequestWrapper extends HttpServletRequestWrapper {
	private String myRequestBody;

	public MyHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
		myRequestBody = this.getRequestBodyString();
		super.setAttribute(MyRestConstants.REQUEST_ATTR_RequestBody_Cache, myRequestBody);
	}
	
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(myRequestBody.getBytes(StandardCharsets.UTF_8));

        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return bais.read();
            }
        };
    }	

	private String getRequestBodyString() {
		StringBuilder sb = new StringBuilder();
		InputStream inputStream = null;
		BufferedReader reader = null;
		try {
			inputStream = super.getInputStream();
			reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
			String line;
			while((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if(inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
		return sb.toString();
	}
	
	public Map<String, String> getAllParameterMap() {
		Enumeration<String> parameters = this.getParameterNames();
		
		Map<String, String> params = new HashMap();
		String parameterName;
		while(parameters.hasMoreElements()) {
			parameterName = parameters.nextElement();
			params.put(parameterName, this.getParameter(parameterName));
		}
		return params;
	}
	
	public String getRequestBodyCache() {
		if(StringUtils.hasText(myRequestBody)) {
			return myRequestBody;
		} else {
			return "";
		}
	}
}
