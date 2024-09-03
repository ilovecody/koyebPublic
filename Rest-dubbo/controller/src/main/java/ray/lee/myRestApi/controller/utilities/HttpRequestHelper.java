package ray.lee.myRestApi.controller.utilities;

import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import ray.lee.common.pojo.vo.UserCredential;

import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Slf4j
@Component
public class HttpRequestHelper {
	private static final String TRUE_VALUE_YES   = "YES";
	private static final String TRUE_VALUE_ON	 = "on";
	private static final String TRUE_VALUE_ONE	 = "1";
	private static final String FALSE_VALUE_NO   = "NO";
	private static final String FALSE_VALUE_OFF	 = "off";
	private static final String FALSE_VALUE_ZERO = "0";
	
	private String uploadFileTempDir = "";

	public HttpRequestHelper() {
	}

	public String getUploadFileTempDir() {
		String tempDir = ((StringUtils.hasText(this.uploadFileTempDir)) ? this.uploadFileTempDir : "");
		return tempDir;
	}

	public void setUploadFileTempDir(String uploadFileTempDir) {
		this.uploadFileTempDir = uploadFileTempDir;
	}

	public boolean isMultipartRequest(HttpServletRequest request) {
		return (request instanceof MultipartHttpServletRequest);
	}

	public String getString(HttpServletRequest request, String parameterName) {
			String value = null;
			if(isMultipartRequest(request)) {
				MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
				Object obj = multipartRequest.getParameter(parameterName);
				if(obj != null && obj instanceof String) {
					value = (String)obj;	
				}
			} else {
				value = request.getParameter(parameterName);	
			}
			//String trimStr = StringUtils.trimWhitespace(value);
			String trimStr = StringUtils.trimAllWhitespace(value);
			if(!StringUtils.hasText(trimStr)) {
				return null;	
			}
			return value;
	}

	public int getInt(HttpServletRequest request, String parameterName) {
		int value = 0;
		try {
			value = getIntegerObject(request, parameterName).intValue();
		} catch(Exception e) {
			
		}
		return value;
	}

	public Integer getIntegerObject(HttpServletRequest request, String parameterName) {
		Number number = getNumber(request, parameterName, Integer.class);
		if(number == null) {
			return null;	
		}
		return Integer.valueOf(number.intValue());
	}	

	public boolean getBoolean(HttpServletRequest request, String parameterName) {
		boolean value = false;
		try {
			value = getBooleanObject(request, parameterName).booleanValue();
		} catch(Exception e) {
			
		}
		return value;
	}

	public Boolean getBooleanObject(HttpServletRequest request, String parameterName) {
		Boolean value = null;
		String str = getString(request, parameterName);
		try {
			if(str.equalsIgnoreCase(MyRestConstants.YES) || str.equalsIgnoreCase(TRUE_VALUE_YES) || 
			   str.equals(TRUE_VALUE_ONE) || str.equalsIgnoreCase(TRUE_VALUE_ON)) {
				return true;	
			} else if (str.equalsIgnoreCase(MyRestConstants.NO) || str.equalsIgnoreCase(FALSE_VALUE_NO) ||
					   str.equals(FALSE_VALUE_ZERO) || str.equalsIgnoreCase(FALSE_VALUE_OFF)) {
				return false;	
			} else {
				value = Boolean.valueOf(str);	
			}
		}catch(Exception e) {
			
		}
		return value;
	}

	public double getDouble(HttpServletRequest request, String parameterName) {
		double value = 0.0;
		try {
			value = getDoubleObject(request, parameterName).doubleValue();
		} catch(Exception e) {
			
		}
		return value;				
	}	

	public Double getDoubleObject(HttpServletRequest request, String parameterName) {
		Number number = getNumber(request, parameterName, Double.class);
		if(number == null) {
			return null;	
		}
		return Double.valueOf(number.doubleValue());
	}

	public float getFloat(HttpServletRequest request, String parameterName) {
		float value = 0;
		try {
			value = getFloatObject(request, parameterName).floatValue();
		} catch(Exception e) {
			
		}
		return value;				
	}

	public Float getFloatObject(HttpServletRequest request, String parameterName) {
		Number number = getNumber(request, parameterName, Float.class);
		if(number == null) {
			return null;	
		}
		return Float.valueOf(number.floatValue());
	}	

	public long getLong(HttpServletRequest request, String parameterName) {
		long value = 0;
		try {
			value = getLongObject(request, parameterName).longValue();
		} catch(Exception e) {
			
		}
		return value;				
	}

	public Long getLongObject(HttpServletRequest request, String parameterName) {
		Number number = getNumber(request, parameterName, Long.class);
		if(number == null) {
			return null;	
		}
		return Long.valueOf(number.longValue());
	}

	@SuppressWarnings("unchecked")
	public Number getNumber(HttpServletRequest request,String parameterName, Class numberClass) {
		Number value = null;
		try {
			String str = getString(request, parameterName);
			value = NumberUtils.parseNumber(str, numberClass, NumberFormat.getInstance());
		} catch(Exception e) {
			
		}
		return value;		
	}	
	
	public short getShort(HttpServletRequest request, String parameterName) {
		short value = 0;
		try {
			value = getShortObject(request, parameterName).shortValue();
		} catch(Exception e) {
			
		}
		return value;				
	}

	public Short getShortObject(HttpServletRequest request, String parameterName) {
		Number number = getNumber(request, parameterName, Short.class);
		if(number == null) {
			return null;	
		}
		return Short.valueOf(number.shortValue());
	}	

	public Date getDate(HttpServletRequest request, String parameterName, boolean isChineseDate) {
		Date value = null;
		try {
			String strDate = getString(request, parameterName);
			value = Date.from(LocalDateTime.parse(strDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
							  .atZone(ZoneId.systemDefault()).toInstant());
		} catch(Exception e) {
			
		}
		return value;
	}

	public Date getDate(HttpServletRequest request, String parameterName) {
		return getDate(request, parameterName, false);
	}

	public File getFile(HttpServletRequest request, String parameterName) throws Exception {
		File uploadFile = null; 
		if(isMultipartRequest(request)) {
	        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
			MultipartFile file = multipartRequest.getFile(parameterName);
			String tempDir = getUploadFileTempDir();
			uploadFile = new File(tempDir + File.separator + file.getOriginalFilename());
        }
        return uploadFile;
	}
	
	public InputStream getInputStream(HttpServletRequest request, String parameterName) throws Exception {
		InputStream in = null; 
		if(isMultipartRequest(request)) {
	        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
			MultipartFile file = multipartRequest.getFile(parameterName);
			in = file.getInputStream();
        }
        return in;
	}		
	public String getFileName(HttpServletRequest request, String parameterName) throws Exception {
	   String fileName= null;
	   if(isMultipartRequest(request)) {
		   MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
		   MultipartFile file = multipartRequest.getFile(parameterName);
		   fileName = file.getOriginalFilename();
	   }
	   return fileName;
	}

	public static void disableCaching(HttpServletResponse response) {
		if(response != null) {
			response.addHeader("Cache-Control", "no-store");
			response.addHeader("Pragma", "no-cache");
			response.setDateHeader("Expires", 0);
		}
	}
	
	public static Locale getRequestLocale(HttpServletRequest request) {
		try {
			return request.getLocale();	
		} catch(Exception e) {
			return Locale.TAIWAN;	
		}
	}	

	public String getRequestIp(HttpServletRequest request) {
		try {
			String ip = request.getHeader("x-forwarded-for");   
	        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        	ip = request.getHeader("Proxy-Client-IP");   
	        }   
	        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        	ip = request.getHeader("WL-Proxy-Client-IP");   
	        }   
	        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        	ip = request.getRemoteAddr();
	        	if(ip.equals("127.0.0.1")) {
	        		InetAddress inet=null;     
	        		try {
	        			inet = InetAddress.getLocalHost();     
	        		} catch (Exception e) {     
	        			e.printStackTrace();     
	        		}     
	        		ip= inet.getHostAddress();     
	            }  
	        }
	        if(ip != null && ip.length() > 15) {
	        	if(ip.indexOf(",") > 0){     
	        		ip = ip.substring(0, ip.indexOf(","));     
	            }     
	        }     
	        return ip;
		} catch(Exception e) {
			return "";	
		}
	}
	
	public static UserCredential getUserCredential(HttpServletRequest request) {
		return (UserCredential)request.getAttribute(MyRestConstants.REQUEST_ATTR_USER_Credential);
	}	
	
}